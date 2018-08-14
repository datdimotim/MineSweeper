package com.dimotim.minesweaper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class Model{
    private final int cols;
    private final int rows;
    private final Pt[][] gameField;
    private final boolean[][] mineField;
    private final boolean[][] autoMarked;
    private boolean isGameOver=false;
    private boolean isWin=false;
    private int cellOpened=0;
    private int mineMarked=0;
    private final int mines;
    private final ArrayList<Consumer<Void>> modelChangeListeners =new ArrayList<>();

    public Model(int cols, int rows, int mines){
        this.cols=cols;
        this.rows=rows;
        this.mines=mines;
        gameField=new Pt[cols][rows];
        autoMarked=new boolean[cols][rows];
        if(mines<0)throw new IllegalArgumentException("mines="+mines+" < 0");
        if(mines>cols*rows)throw new IllegalArgumentException("mines="+mines+" > "+"(cols="+cols+")*(rows="+rows+")");
        for(Pt[] s:gameField)Arrays.fill(s,Pt.Unknown);
        mineField=MineShuffler.shuffle(cols,rows,mines);
    }

    public void addModelChangeListener(Consumer<Void> listener){
        modelChangeListeners.add(listener);
    }

    public void notifyListeners(){
        for(Consumer<Void> l: modelChangeListeners)l.accept(null);
    }

    public void selectPointSafe(int col,int row){
        if(!mineField[col][row]){
            selectPoint(col, row);
            return;
        }

        autoMarked[col][row]=true;
        markPoint(col, row);
        notifyListeners();
    }

    public void selectPoint(int col,int row){
        if(isGameOver)return;
        if(!inBounds(col,row))return;
        if(gameField[col][row]==Pt.Flag)return;
        if(mineField[col][row])onLoose();
        if(gameField[col][row].ordinal()>0&&gameField[col][row].ordinal()<Pt.Eight.ordinal()){
            if(evalNumber(col, row)==evalFlags(col, row)){
                if(isUnknown(col-1, row-1))selectPoint(col-1, row-1);
                if(isUnknown(col-1, row))selectPoint(col-1, row);
                if(isUnknown(col-1, row+1))selectPoint(col-1, row+1);
                if(isUnknown(col, row-1))selectPoint(col, row-1);
                if(isUnknown(col, row+1))selectPoint(col, row+1);
                if(isUnknown(col+1, row-1))selectPoint(col+1, row-1);
                if(isUnknown(col+1, row))selectPoint(col+1, row);
                if(isUnknown(col+1, row+1))selectPoint(col+1, row+1);
            }
        }
        if(gameField[col][row]!=Pt.Unknown)return;
        gameField[col][row]=initNumber(col,row);
        cellOpened++;
        if(cellOpened==cols*rows-mines){
            isGameOver=true;
            isWin=true;
        }
        if(gameField[col][row]==Pt.Zero){
            selectPoint(col-1,row-1);
            selectPoint(col-1,row);
            selectPoint(col-1,row+1);
            selectPoint(col,row-1);
            selectPoint(col,row+1);
            selectPoint(col+1,row-1);
            selectPoint(col+1,row);
            selectPoint(col+1,row+1);
        }
    }

    public Pt[][] getGameField(){
        Pt[][] copy=new Pt[cols][];
        for(int i=0;i<cols;i++){
            copy[i]=gameField[i].clone();
        }
        return copy;
    }

    public boolean[][] getAutoMarked(){
        boolean[][] copy=new boolean[cols][];
        for(int i=0;i<cols;i++)copy[i]=autoMarked[i].clone();
        return copy;
    }

    public boolean isWin() {
        return isWin;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private void onLoose(){
        isGameOver=true;
        for(int i=0;i<cols;i++){
            for(int j=0;j<rows;j++)
                if(gameField[i][j]!=Pt.Flag&&mineField[i][j])gameField[i][j]=Pt.Mine;
        }
    }

    public void markPoint(int col, int row){
        if(isGameOver)return;
        if(gameField[col][row]==Pt.Unknown) {
            gameField[col][row] = Pt.Flag;
            mineMarked++;
            return;
        }
        if(gameField[col][row]==Pt.Flag){
            gameField[col][row] = Pt.Unknown;
            mineMarked--;
            return;
        }

    }

    public Pt getCell(int col, int row){
        return gameField[col][row];
    }

    public int getCols(){
        return cols;
    }

    public int getRows(){
        return rows;
    }

    public int getMineMarked() {
        return mineMarked;
    }

    private Pt initNumber(int col,int row){
        return Pt.values()[evalNumber(col,row)];
    }

    private int evalNumber(int col, int row){
        int count=0;
        if(isMine(col-1,row-1))count++;
        if(isMine(col-1,row))count++;
        if(isMine(col-1,row+1))count++;
        if(isMine(col+1,row-1))count++;
        if(isMine(col+1,row))count++;
        if(isMine(col+1,row+1))count++;
        if(isMine(col,row-1))count++;
        if(isMine(col,row+1))count++;
        return count;
    }

    private int evalFlags(int col, int row){
        int count=0;
        if(isFlag(col-1,row-1))count++;
        if(isFlag(col-1,row))count++;
        if(isFlag(col-1,row+1))count++;
        if(isFlag(col+1,row-1))count++;
        if(isFlag(col+1,row))count++;
        if(isFlag(col+1,row+1))count++;
        if(isFlag(col,row-1))count++;
        if(isFlag(col,row+1))count++;
        return count;
    }

    private boolean inBounds(int col,int row){
        return col>=0&&col<cols&&row>=0&&row<rows;
    }

    private boolean isMine(int col,int row){
        return inBounds(col,row)&&mineField[col][row];
    }

    private boolean isUnknown(int col, int row){
        return inBounds(col, row)&&gameField[col][row]==Pt.Unknown;
    }

    private boolean isFlag(int col, int row){
        return inBounds(col, row)&&gameField[col][row]==Pt.Flag;
    }
}
