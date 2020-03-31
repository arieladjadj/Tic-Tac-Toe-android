package com.example.ticcattoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static android.widget.Toast.LENGTH_SHORT;

public class Online extends AppCompatActivity implements View.OnClickListener {

    Dialog waitingDialog, exitingDialog;
    Button stayInActivity, returnToHomePageBtn;
    Toolbar mainToolbar, waitingDialogToolbar, exitingDialogToolbar;
    char[][] board;
    Button[][] btnsBoard;
    TextView[] scores;
    char playerCh, opponentCh;
    public static final String SERVER_IP = "192.168.1.12";
    public static final int SERVER_PORT = 5000;
    Socket serverSock;
    Scanner dataInputStream;
    PrintWriter dataOutputStream;
    boolean isPlayerTurn;
    Thread connect2ServerT;
    String msg2Server;

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
        this.scores = new TextView[3];
        this.scores[0]  = (TextView) findViewById(R.id.x_results);
        this.scores[1] = (TextView)findViewById(R.id.o_results);
        this.scores[2] = (TextView)findViewById(R.id.d_results);
        createWaitingDialog();
    }

    @Override
    protected void onDestroy() {
        if(this.connect2ServerT.isAlive()) this.connect2ServerT.interrupt();
        if(this.serverSock != null && !this.serverSock.isClosed()){
            try {
                this.serverSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(exitingDialog != null) exitingDialog.dismiss();
        if(waitingDialog != null) waitingDialog.dismiss();
        super.onDestroy();
    }

    private void createExitDialog() {
        if(this.exitingDialog != null && this.exitingDialog.isShowing()) this.exitingDialog.cancel();
        if(this.waitingDialog != null && this.waitingDialog.isShowing()) {
            this.waitingDialog.cancel();
        }
        if(this.connect2ServerT.isAlive()) this.connect2ServerT.interrupt();
        // if(this.connect2ServerT.isAlive()) this.connect2ServerT.interrupt();
        if(this.serverSock != null && !this.serverSock.isClosed()){
            try {
                this.serverSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        try {
            exitingDialog.show();
        }catch(Exception e) {}

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

        this.connect2ServerT = new Thread(new connect2Server());
        this.connect2ServerT.start();
    }

    class sendMsg2Server implements Runnable {

        @Override
        public void run() {
            dataOutputStream.println(msg2Server);
            dataOutputStream.flush();
        }
    }
    class connect2Server implements Runnable {

        @Override
        public void run() {
            try {
                serverSock = new Socket(SERVER_IP, SERVER_PORT);
                dataOutputStream = new PrintWriter(serverSock.getOutputStream(), true);
                dataInputStream = new Scanner(serverSock.getInputStream());
                String msgFromServer = dataInputStream.nextLine();
                showToast(msgFromServer);
                Log.d("ariel", "Before: msg from sever: " + msgFromServer);
                showToast(msgFromServer);
                if(msgFromServer.equals("wait")){
                    msgFromServer = dataInputStream.nextLine();
                    showToast(msgFromServer);
                    if(msgFromServer.contains("connect")){
                        playerCh = 'X';
                        opponentCh = 'O';
                        isPlayerTurn = true;
                    }
                }else{
                    isPlayerTurn = false;
                    playerCh = 'O';
                    opponentCh = 'X';
                }
            } catch (Exception e) {
                showToast("Socket Error \nQuiting ");
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }

            waitingDialog.cancel();
            createBoard();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    generateBtnsBoard();
                }
            });

                //while loop to get msgs from server
                while(true) {
                    try{
                        String msgFromServer = dataInputStream.nextLine();
                        Log.d("ariel", "After: msg from sever: " + msgFromServer);
                        if(!msgFromServer.contains("disconnect") && msgFromServer.length() >3) makeOpponentMove(msgFromServer);
                        else {
                            showToast("Opponent disconnected");
                            serverSock.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createExitDialog();
                                }
                            });
                            break;
                        }
                    }catch (Exception e){
                        //
                        showToast("Socket Error \nQuiting ");
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                        break;
                    }
                }
        }
    }

    private void makeOpponentMove(String msgFromServer) {
        final int pos[] = parseMsg(msgFromServer);
        runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  Log.d("ariel", "From makeOpponentMove: " + pos[0]+""+pos[1]);
                  if(opponentCh == 'X') btnsBoard[pos[0]][pos[1]].setTextColor(getResources().getColor(R.color.color2));
                  else btnsBoard[pos[0]][pos[1]].setTextColor(getResources().getColor(R.color.color1));
                  btnsBoard[pos[0]][pos[1]].setText(String.valueOf(opponentCh));
                  board[pos[0]][pos[1]] = opponentCh;
                  String boardState = getBoardState();
                  isPlayerTurn = true;
                  if(boardState != BoardState.NOTHING) {
                      finishGame(boardState);
                  }
              }
          }
        );
    }

    private int[] parseMsg(String msgFromServer) {
        Log.d("ariel", "From parseMSG: " + msgFromServer.charAt(1));
        return new int[] {Character.getNumericValue(msgFromServer.charAt(1)),Character.getNumericValue(msgFromServer.charAt(3))};
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
        Button btn = (Button) view;
        int[] pos = findPos(btn);
        if(pos != null) {
            if(this.isPlayerTurn != true) Toast.makeText(this, "It is not your turn! ",LENGTH_SHORT).show();
            else {
                //makeMove(pos[0], pos[1]);
                if(this.playerCh == 'X') btn.setTextColor(getResources().getColor(R.color.color2));
                else btn.setTextColor(getResources().getColor(R.color.color1));
                makeMove(pos[0], pos[1]);
                synchronized (this.serverSock) {
                    this.msg2Server =  pos[0] + "," + pos[1];
                    new Thread(new sendMsg2Server()).start();
                }
            }
        }

        if(view == stayInActivity ) {
            if(this.connect2ServerT != null && this.connect2ServerT.isAlive()) this.connect2ServerT.interrupt();
            if(!this.connect2ServerT.isAlive()) createWaitingDialog();

        }else if(view == returnToHomePageBtn){
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    private void makeMove(int row, int col) {
        if(this.board[row][col] == '-') {
            if(playerCh == 'X') btnsBoard[row][col].setTextColor(getResources().getColor(R.color.color2));
            else btnsBoard[row][col].setTextColor(getResources().getColor(R.color.color1));
            this.btnsBoard[row][col].setText(String.valueOf(this.playerCh));
            this.board[row][col] = this.playerCh;
            String boardState = getBoardState();

            this.isPlayerTurn = false;
            if(boardState != BoardState.NOTHING){
                finishGame(boardState);
            }

        }
    }


    private void generateBtnsBoard(){
        this.btnsBoard = new Button[3][3];
        this.btnsBoard[0][0] = (Button) findViewById(R.id.btn00);
        this.btnsBoard[0][1] = findViewById(R.id.btn01);
        this.btnsBoard[0][2] = findViewById(R.id.btn02);
        this.btnsBoard[1][0] = findViewById(R.id.btn10);
        this.btnsBoard[1][1] = findViewById(R.id.btn11);
        this.btnsBoard[1][2] = findViewById(R.id.btn12);
        this.btnsBoard[2][0] = findViewById(R.id.btn20);
        this.btnsBoard[2][1] = findViewById(R.id.btn21);
        this.btnsBoard[2][2] = findViewById(R.id.btn22);

        for(int i=0; i<3;i++){for(int j=0;j<3;j++){this.btnsBoard[i][j].setOnClickListener(this);}}
    }
    private String getBoardState(){
        Log.d("ariel","opponent ch = "+ this.opponentCh + " player ch = " + this.playerCh);
        if(BoardState.isWin(this.board, this.opponentCh)) return BoardState.AI_PLAYER_WIN;
        else if(BoardState.isWin(this.board, this.playerCh)) return BoardState.PLAYER_WIN;
        else if(BoardState.isDraw(this.board)) return BoardState.DRAW;
        return BoardState.NOTHING;
    }

    private int[] findPos(Button btn){
        int[] pos = null;
        for(int i=0; i<3 && pos==null && this.btnsBoard != null ; i++){ for(int j=0;j<3;j++){ if(this.btnsBoard[i][j] == btn) pos =  new int[] {i,j}; } }
        return pos;
    }

    private void finishGame(String boardState) {
        int scorePos = -1;
        showToast(boardState);
        if (boardState.equals(BoardState.AI_PLAYER_WIN)) {
            Toast.makeText(this, "Opponent won the game", LENGTH_SHORT).show();
            if (this.opponentCh == 'X') scorePos = 0;
            else scorePos = 1;
        } else if (boardState.equals(BoardState.PLAYER_WIN)) {
            Toast.makeText(this, "Yow won the game", LENGTH_SHORT).show();
            if (this.playerCh == 'X') scorePos = 0;
            else scorePos = 1;
        } else {
            Toast.makeText(this, "Draw", LENGTH_SHORT).show();
            scorePos = 2;
        }
        Log.d("ariel", "From finish game: scorePos = " + scorePos + " player char = " + this.playerCh);
        this.scores[scorePos].setText(Integer.toString(Integer.parseInt(
                this.scores[scorePos].getText().toString()) + 1));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetGame();
            }
        }, 800);

    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.board[i][j] = '-';
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.btnsBoard[i][j].setText("");
            }
        }
    }


    @Override
    public void onBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createExitDialog();
            }
        });
    }
}
