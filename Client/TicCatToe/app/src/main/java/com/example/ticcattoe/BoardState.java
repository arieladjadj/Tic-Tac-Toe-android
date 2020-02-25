package com.example.ticcattoe;

public class BoardState {

    //final strings states
    public static final String PLAYER_WIN = "player_win";
    public static final String AI_PLAYER_WIN = "ai_player_win";
    public static final String DRAW = "draw";
    public static final String NOTHING = "nothing";

    public static boolean isWin(char[][] board, char player) {
        for(int i=0; i<3; i++) { //checks for column and rows
            if(board[i][0] == player && board[i][0] == board[i][1]
                    && board[i][1] == board[i][2]) return true;
            if(board[0][i] == player && board[0][i] == board[1][i]
                    && board[1][i] == board[2][i]) return true;
        }

        //checks diagonal
        if(board[0][0] == player && board[1][1] == board[0][0]
                && board[1][1] == board[2][2]) return true;
        if(board[0][2] == player && board[0][2] == board[1][1]
                && board[2][0] == board[1][1]) return true;

        return false;
    }

    public static boolean isDraw(char[][] board) {
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if(board[i][j] == '-') return false;
            }
        }
        return true;
    }


}
