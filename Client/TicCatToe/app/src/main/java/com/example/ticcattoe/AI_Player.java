package com.example.ticcattoe;

public class AI_Player {

    private char aiPlayerCH, playerCh;

    private class Move{
        int row, col;
        public Move(int row, int col){this.row=row;this.col=col;}
        public Move(){}
    }

    public AI_Player(char aiPlayerCH, char playerCh) {
        this.aiPlayerCH = aiPlayerCH;
        this.playerCh = playerCh;
    }

    public void makeMove(char[][] board) {
        Move move = getBestMove(board, this.aiPlayerCH);
        board[move.row][move.col] = this.aiPlayerCH;
    }

    private Move getBestMove(char[][] board, char AIPlayerCh) {
        Move bestMove = new Move();
        int bestMoveVal = -100000;
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if(board[i][j] == '-') {
                    board[i][j] = AIPlayerCh;
                    int moveValue = minimax(board,false, 0);
                    if(moveValue > bestMoveVal ) {
                        bestMove.row=i;
                        bestMove.col = j;
                        bestMoveVal = moveValue;
                    }
                    board[i][j] = '-';
                }
            }
        }
        return bestMove;
    }

    private int minimax(char[][] board, boolean isAITurn, int levels) {
        BoardState boardState = new BoardState();

        if(boardState.isWin(board, this.aiPlayerCH)) return 1;        //if maximizer won the game
        else if(boardState.isWin(board, this.playerCh)) return -1;  //if minimizer won the game
        else if(boardState.isDraw(board)) return 0;                 //if draw
        int minimaxVal=0, val; //=-1;
        if(isAITurn) {
            minimaxVal =-1000;
            for(int i=0; i<3; i++) {
                for(int j=0;j<3; j++) {
                    if(board[i][j] == '-') { //if slot is free
                        board[i][j] = this.aiPlayerCH;
                        val = minimax(board,false, levels+1);
                        minimaxVal = Math.max(minimaxVal, val);
                        board[i][j]= '-';
                    }
                }
            }
        }else {
            minimaxVal = 1000;
            for(int i=0;i<3;i++) {
                for(int j=0;j<3; j++) {
                    if(board[i][j] == '-') {
                        board[i][j] = this.playerCh;
                        val = minimax(board,true, levels+1);
                        minimaxVal = Math.min(val, minimaxVal);
                        board[i][j] = '-';
                    }
                }
            }
        }
        return minimaxVal;
    }
}
