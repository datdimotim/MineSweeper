package com.dimotim.minesweaper.test;

import static com.dimotim.minesweaper.test.Test.checkEngine;
import static com.dimotim.minesweaper.test.Test.simpleTest;

public class TestRunner {
    public static void main(String[] args) {
        TestRunner tr=new TestRunner();
        tr.checkEngineLoop();
        tr.avgTimeTest();
        tr.timeRecords();
    }

    @org.junit.Test
    public void checkEngineLoop(){
        for(int i=0;i<100;i++) {
            checkEngine();
            System.out.println(i);
        }
    }

    public void timeRecords(){
        long timeRecord=-1;
        while (true){
            long time=simpleTest();
            if(time>timeRecord)timeRecord=time;
            System.out.println("Record Time="+timeRecord);
            System.out.println();
        }
    }

    private void avgTimeTest(){
        Test test=new Test();
        long st=System.currentTimeMillis();
        for(int i=0;i<100;i++)test.solve();
        System.out.println("Avg time="+((System.currentTimeMillis()-st)/100));
    }

}
