package com.dimotim.minesweaper;

import com.dimotim.minesweaper.engine.AnalysisResult;
import com.dimotim.minesweaper.engine.Engine;

import javax.swing.*;
import java.util.ArrayList;

import static com.dimotim.minesweaper.ModelToEngineConverter.toEngineField;

public class Controller{
    public static boolean play(Model model,Field field){
        AnalysisResult r = Engine.analysis(toEngineField(model));
        ArrayList<int[]> e=r.emptyPts;
        ArrayList<int[]> m=r.minedPts;
        for(int[] p:e)model.selectPoint(p[0],p[1]);
        for(int[] p:m)if(model.getCell(p[0],p[1])!=Pt.Flag)model.markPoint(p[0],p[1]);
        field.update();
        return !model.isGameOver()&&e.size()!=0;
    }

    public static void run(Model model,Field field){
        long st=System.currentTimeMillis();
        while (play(model,field));
        System.out.println("time="+(System.currentTimeMillis()-st));
    }

    public static boolean isOptimal(Model model, int col, int row){
        final int cols=model.getCols();
        final int rows=model.getRows();
        Pt[][] gameField=model.getGameField();
        boolean[][] autoMarked=model.getAutoMarked();
        AnalysisResult r=Engine.analysis(toEngineField(model));

        if(contains(r.emptyPts,new int[]{col,row}))return true;

        if(r.emptyPts.size()!=0){
            System.out.println("Здесь может быть мина");
            return false;
        }

        for(int[] mp:r.minedPts) {
            if (gameField[mp[0]][mp[1]] != Pt.Flag) {
                System.out.println("Не хватает флагов");
                return false;
            }
        }

        for(int i=0;i<cols;i++) {
            for(int j=0;j<rows;j++) {
                if(gameField[i][j]!=Pt.Flag)continue;
                if(autoMarked[i][j])continue;
                if (!contains(r.minedPts,new int[]{i,j})) {
                    System.out.println("Есть флаги в недопустимых местах");
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean contains(ArrayList<int[]> l, int[] pt){
        for(int[] p:l)if(p[0]==pt[0]&&p[1]==pt[1])return true;
        return false;
    }
}
