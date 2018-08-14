package com.dimotim.minesweaper;

import java.util.Random;

public class MineShuffler {
    private final int cols;
    private final int rows;
    private final boolean[][] mineField;

    private MineShuffler(boolean[][] mineField) {
        this.mineField = mineField;
        cols=mineField.length;
        rows=mineField[0].length;
    }

    public static boolean[][] shuffle(int cols, int rows, int mines){
        MineShuffler m=new MineShuffler(new boolean[cols][rows]);
        m.initMines(mines);
        m.shuffle(mines);
        return m.mineField;
    }


    private int numberToCol(int number){
        return number%cols;
    }

    private int numberToRow(int number){
        return number/cols;
    }

    private void initMines(int minesCount){
        for(int i=0;i<minesCount;i++)mineField[numberToCol(i)][numberToRow(i)]=true;
    }

    private void shuffle(int minesCount){
        Random rnd=new Random();//seed = 1
        for(int i=0;i<minesCount;i++)swap(i,rnd.nextInt(cols*rows));
    }

    private void swap(int i, int j){
        boolean a=mineField[numberToCol(i)][numberToRow(i)];
        mineField[numberToCol(i)][numberToRow(i)]=mineField[numberToCol(j)][numberToRow(j)];
        mineField[numberToCol(j)][numberToRow(j)]=a;
    }
}
