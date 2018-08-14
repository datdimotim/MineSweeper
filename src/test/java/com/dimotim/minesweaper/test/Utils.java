package com.dimotim.minesweaper.test;

import com.dimotim.minesweaper.Model;
import com.dimotim.minesweaper.Pt;
import com.dimotim.minesweaper.engine.AnalysisResult;

public class Utils {
    public static void openNewPoint(Model model){
        Pt[][] field=model.getGameField();
        for(int i=0;i<field.length;i++){
            for(int j=0;j<field[0].length;j++){
                if(field[i][j]!=Pt.Unknown)continue;
                model.selectPointSafe(i,j);
                return;
            }
        }
    }

    public static Model createProfessional(){
        return new Model(30,16,99);
    }

    public static void compareResults(AnalysisResult r, check_engine.AnalysisResult c){
        if(r==null&&c==null)return;
        if(r==null)throw new RuntimeException();
        if(c==null)throw new RuntimeException();
        if(r.minedPts.size()!=c.minedPts.size())throw new RuntimeException();
        if(r.emptyPts.size()!=c.emptyPts.size())throw new RuntimeException();
        for(int i=0;i<r.emptyPts.size();i++)if(!cmp(r.emptyPts.get(i),c.emptyPts.get(i)))throw new RuntimeException();
        for(int i=0;i<r.minedPts.size();i++)if(!cmp(r.minedPts.get(i),c.minedPts.get(i)))throw new RuntimeException();
    }

    public static boolean useAnalysis(Model model, AnalysisResult r){
        for (int[] p:r.emptyPts) model.selectPoint(p[0], p[1]);
        for (int[] p:r.minedPts) {
            if(model.getCell(p[0],p[1])==Pt.Flag)continue;
            model.markPoint(p[0],p[1]);
        }
        if (model.isGameOver()&&!model.isWin()) throw new RuntimeException();
        return r.emptyPts.size()!=0;
    }

    private static boolean cmp(int[] l,int[] r){
        if(l[0]!=r[0])return false;
        if(l[1]!=r[1])return false;
        return true;
    }
}
