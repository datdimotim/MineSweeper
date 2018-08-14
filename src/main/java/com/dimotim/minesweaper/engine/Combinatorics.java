package com.dimotim.minesweaper.engine;

public class Combinatorics {
    public static int C(int n,int k){
        return cTable[n-1][k];
    }

    private static int fac(int n){
        int res=1;
        while (n>0)res*=n--;
        return res;
    }

    private static int Cslow(int n, int k){
        return fac(n)/fac(k)/fac(n-k);
    }

    private static final int[][] cTable=initCTable();
    private static int[][] initCTable(){
        int[][] cTable=new int[8][];
        for(int n=1;n<=8;n++){
            cTable[n-1]=new int[n+1];
            for(int k=0;k<=n;k++)cTable[n-1][k]=Cslow(n,k);
        }
        return cTable;
    }
}
