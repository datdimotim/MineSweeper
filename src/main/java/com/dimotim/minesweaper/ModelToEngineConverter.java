package com.dimotim.minesweaper;

import com.dimotim.minesweaper.engine.Engine;

public class ModelToEngineConverter {
    private static int[][] gameFieldToEngineField(Pt[][] gameField){
        int[][] ef=new int[gameField.length][gameField[0].length];
        for(int i=0;i<gameField.length;i++)
            for(int j=0;j<gameField[0].length;j++)
                if(gameField[i][j].ordinal()>8)ef[i][j]=Engine.UNKNOWN;
                else ef[i][j]=gameField[i][j].ordinal();
        return ef;
    }

    public static int[][] toEngineField(Model model){
        int[][] engineField=gameFieldToEngineField(model.getGameField());
        boolean[][] autoMarked=model.getAutoMarked();
        for(int i=0;i<engineField.length;i++){
            for(int j=0;j<engineField[0].length;j++){
                if(autoMarked[i][j])engineField[i][j]=Engine.MINE;
            }
        }
        return engineField;
    }
}
