package com.example.ticcattoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

public class SingleMode extends AppCompatActivity implements View.OnClickListener {

    Dialog waitingDialog, exitingDialog;
    Button stayInActivity, returnToHomePageBtn;
    Toolbar mainToolbar, exitingDialogToolbar;
    char[][] board;
    Button[][] btnsBoard;
    AI_Player ai_player;
    char aiPlayerCh, playerCh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mainToolbar = (Toolbar)findViewById(R.id.toolbar);
        mainToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_50dp);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExitDialog();
            }
        });
        generateBoard();
        generateBtnsBoard();
        this.aiPlayerCh='O';
        this.playerCh='X';
        this.ai_player = new AI_Player(this.aiPlayerCh, this.playerCh, this);
    }

    private void generateBoard() {
        this.board = new char[3][3];
        for(int i=0; i<3;i++){ for(int j=0; j<3;j++){ this.board[i][j]='-'; } }
    }

    private void generateBtnsBoard(){
        this.btnsBoard = new Button[3][3];
        this.btnsBoard[0][0] = findViewById(R.id.btn00);
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


    @Override
    public void onClick(View view) {
        if(view == stayInActivity ) {
            this.exitingDialog.cancel();
        }else if(view == returnToHomePageBtn){
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        } else if(view instanceof Button) { //btn click
            Button btn = (Button)view;
            int[] pos = findPos(btn);
            makeMove(pos[0], pos[1]);
        }
    }

    @Override
    public void onBackPressed() {
        createExitDialog();
    }

    private void makeMove(int row, int col) {
        if(this.board[row][col] == '-') {
            //start anim
            this.btnsBoard[row][col].setText(String.valueOf(this.playerCh));
            this.board[row][col] = this.playerCh;
            String boardState = getBoardState();
            if(boardState == BoardState.NOTHING){
                this.ai_player.makeMove(this.board);
                boardState = getBoardState();
            }

            if(boardState != BoardState.NOTHING){
                finishGame(boardState);
            }

        }
    }

    private int[] findPos(Button btn){
        int[] pos = null;
        for(int i=0; i<3 && pos==null; i++){ for(int j=0;j<3;j++){ if(this.btnsBoard[i][j] == btn) pos =  new int[] {i,j}; } }
        return pos;
    }

    private String getBoardState(){
        if(BoardState.isWin(this.board, this.aiPlayerCh)) return BoardState.AI_PLAYER_WIN;
        else if(BoardState.isWin(this.board, this.playerCh)) return BoardState.PLAYER_WIN;
        else if(BoardState.isDraw(this.board)) return BoardState.DRAW;
        return BoardState.NOTHING;
    }

    private void finishGame(String boardState) {
        if(boardState.equals(BoardState.AI_PLAYER_WIN)){
            Toast.makeText(this, "AI Won", Toast.LENGTH_LONG).show();
        }else if(boardState.equals(BoardState.DRAW)){
            Toast.makeText(this, "Draw", Toast.LENGTH_LONG).show();
        }else{ ///never going to happen

        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resetBoards();
            }
        }, 1200);

    }

    private void resetBoards() {
        for(int i=0; i<3;i++){for(int j=0; j<3; j++){this.board[i][j]='-';}}
        for(int i=0; i<3;i++){for(int j=0; j<3; j++){this.btnsBoard[i][j].setText("");}}
    }
}
