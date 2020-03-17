package com.example.ticcattoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static android.widget.Toast.LENGTH_SHORT;

public class Online extends AppCompatActivity implements View.OnClickListener {

    Dialog waitingDialog, exitingDialog;
    Button stayInActivity, returnToHomePageBtn;
    Toolbar mainToolbar, waitingDialogToolbar, exitingDialogToolbar;
    char[][] board;
    ImageView[][] imgsBoard;
    char playerCh, opponentCh;
    public static final String SERVER_IP = "192.168.1.12";
    public static final int SERVER_PORT = 5000;
    Socket serverSock;
    Scanner dataInputStream;
    DataOutputStream dataOutputStream;
    boolean isPlayerTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        mainToolbar = (Toolbar)findViewById(R.id.toolbar);
        mainToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_50dp);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExitDialog();
            }
        });
        createWaitingDialog();
    }

    private void createExitDialog() {
        if(this.waitingDialog != null && this.waitingDialog.isShowing()) {
            this.waitingDialog.cancel();
        }
        this.exitingDialog = new Dialog(this);
        this.exitingDialog.setContentView(R.layout.exiting_dialog);
        exitingDialogToolbar = (Toolbar)this.exitingDialog.findViewById(R.id.exitDialogToolbar);
        exitingDialogToolbar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        this.stayInActivity = (Button)this.exitingDialog.findViewById(R.id.positiveBtn);
        this.returnToHomePageBtn = (Button)this.exitingDialog.findViewById(R.id.negativeBtn);
        this.returnToHomePageBtn.setOnClickListener(this);
        this.stayInActivity.setOnClickListener(this);
        exitingDialog.setCancelable(false);
        exitingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitingDialog.show();

    }
    private void createWaitingDialog() {
        this.waitingDialog = new Dialog(this);
        waitingDialog.setContentView(R.layout.waiting_dialog);
        waitingDialogToolbar = (Toolbar)this.waitingDialog.findViewById(R.id.waitingDialogToolbar);
        waitingDialogToolbar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        waitingDialogToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_50dp);
        waitingDialog.setCancelable(false);
        waitingDialogToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExitDialog();
            }
        });
        waitingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        waitingDialog.show();

        new Thread(new connect2Server()).start();
    }

    class connect2Server implements Runnable {

        @Override
        public void run() {
            try {
                showToast("wtf");
                serverSock = new Socket(SERVER_IP, SERVER_PORT);
                showToast("Socket created");
                dataOutputStream = new DataOutputStream(serverSock.getOutputStream());
                dataInputStream = new Scanner(serverSock.getInputStream());
                showToast("Waiting for msg from server");
                String msgFromServer = dataInputStream.nextLine();
                showToast(msgFromServer);
                if(msgFromServer.equals("wait")){
                    showToast(msgFromServer);
                    if(dataInputStream.nextLine().equals("connect")) isPlayerTurn = false;
                    else createExitDialog();
                }else  isPlayerTurn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {

            }

            try {
                dataOutputStream.writeUTF("well");
            } catch (IOException e) {
                e.printStackTrace();
            }
            createBoard();

                //while loop to get msgs from server
                while(true) {
                    try{
                        String msgFromServer = dataInputStream.nextLine();
                        showToast(msgFromServer);
                        if(!msgFromServer.equals("disconnected")) makeOpponentMove(msgFromServer);
                    }catch (Exception e){
                        //
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createExitDialog();
                            }
                        });
                    }
                }
        }
    }

    private void makeOpponentMove(String msgFromServer) {
        int pos[] = parseMsg(msgFromServer);
    }

    private int[] parseMsg(String msgFromServer) {
        return new int[] {0,0};
    }

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Online.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createBoard() {
        this.board = new char[3][3];
        for(int i=0; i<3;i++){ for(int j=0; j<3;j++){ this.board[i][j]='-'; } }
    }
    @Override
    public void onClick(View view) {

        if(view instanceof ImageView) {
            if(this.isPlayerTurn != true) Toast.makeText(this, "It is not your turn! ",LENGTH_SHORT).show();
            else {
                ImageView img = (ImageView) view;
                int[] pos = findPos(img);
                makeMove(pos[0], pos[1]);
            }

        }
        if(view == stayInActivity ) {
            createWaitingDialog();
        }else if(view == returnToHomePageBtn){
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    private void makeMove(int row, int col) {
        if(this.board[row][col] == '-') {
            if(this.playerCh == 'O') this.imgsBoard[row][col].setImageResource(R.drawable.tct_o);
            else {this.imgsBoard[row][col].setImageResource(R.drawable.tct_x); }
            this.board[row][col] = this.playerCh;
            String boardState = getBoardState();
            if(boardState == BoardState.NOTHING){
                //this.opponentCh.makeMove(this.board);
                //boardState = getBoardState();
            }

            if(boardState != BoardState.NOTHING){
                finishGame(boardState);
            }

        }
    }


    private String getBoardState(){
        if(BoardState.isWin(this.board, this.opponentCh)) return BoardState.AI_PLAYER_WIN;
        else if(BoardState.isWin(this.board, this.playerCh)) return BoardState.PLAYER_WIN;
        else if(BoardState.isDraw(this.board)) return BoardState.DRAW;
        return BoardState.NOTHING;
    }

    private int[] findPos(ImageView img){
        int[] pos = null;
        for(int i=0; i<3 && pos==null; i++){ for(int j=0;j<3;j++){ if(this.imgsBoard[i][j] == img) pos =  new int[] {i,j}; } }
        return pos;
    }

    private void finishGame(String boardState) {

    }


    @Override
    public void onBackPressed() {
        createExitDialog();
    }
}
