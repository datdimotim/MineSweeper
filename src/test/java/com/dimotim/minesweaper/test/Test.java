package com.dimotim.minesweaper.test;

import com.dimotim.minesweaper.Field;
import com.dimotim.minesweaper.Model;
import com.dimotim.minesweaper.engine.AnalysisResult;
import com.dimotim.minesweaper.engine.Engine;

import javax.swing.*;

import static com.dimotim.minesweaper.ModelToEngineConverter.toEngineField;
import static com.dimotim.minesweaper.test.Utils.*;

public class Test {
    public static void checkEngine(){
        Model model=createProfessional();
        while (true) {
            AnalysisResult r = Engine.analysis(toEngineField(model));
            check_engine.AnalysisResult check=check_engine.Engine.analysis(toEngineField(model));
            compareResults(r,check);
            if(!useAnalysis(model,r))openNewPoint(model);
            if(model.isWin())return;
        }
    }

    private static void show(Model model){
        JOptionPane.showMessageDialog(null,new Field(model));
    }

    public static long simpleTest(){
        final long st=System.currentTimeMillis();
        Test test=new Test();
        test.solve();
        final long time=System.currentTimeMillis()-st;
        System.out.println("openedPts="+test.openedPts+" countAnalysis="+test.countAnalysis);
        System.out.println("Time="+time);
        System.out.println("count="+Engine.count);
        Engine.count=0;
        return time;
    }

    private int openedPts=0;
    private int countAnalysis=0;

    public void solve(){
        Model model=createProfessional();
        while (true) {
            AnalysisResult r = Engine.analysis(toEngineField(model));
            countAnalysis++;
            if(!useAnalysis(model,r)){
                openedPts++;
                openNewPoint(model);
            }
            if(model.isWin())return;
        }
    }
}
