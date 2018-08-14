package com.dimotim.minesweaper.engine;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import static java.lang.Math.max;
import static java.lang.Math.min;

import static com.dimotim.minesweaper.engine.Combinatorics.C;

public class Engine {
    private final int[][] field;
    private final int[][] regionMap;
    private final ArrayList<ArrayList<Point>> numbers;
    private final ArrayList<Point> unknowns;
    private final int cols;
    private final int rows;
    private final int countOfRegions;
    public static final int MINE=9;
    public static final int UNKNOWN=10;
    public static final int EMPTY=11;

    private Engine(int[][] field){
        this.field=field;
        this.cols=field.length;
        this.rows=field[0].length;

        regionMap=new int[cols][rows];
        int currentRegion=0;
        for(int i=0;i<cols;i++) {
            for (int j = 0; j < rows; j++) {
                if (isDigit(field[i][j])) {
                    if (regionMap[i][j] == 0) {
                        currentRegion++;
                        fillRegion(i,j,currentRegion);
                    }
                }
            }
        }
        countOfRegions=currentRegion;

        numbers=new ArrayList<>();
        for(int i=0;i<countOfRegions;i++)numbers.add(new ArrayList<>());
        for(int i=0;i<cols;i++){
            for(int j=0;j<rows;j++){
                if(!isDigit(field[i][j]))continue;;
                numbers.get(regionMap[i][j]-1).add(new Point(i,j));
            }
        }

        unknowns=new ArrayList<>();
        for(int i=0;i<cols;i++){
            for(int j=0;j<rows;j++){
                if(regionMap[i][j]==0)continue;
                if(field[i][j]!=UNKNOWN)continue;
                unknowns.add(new Point(i,j));
            }
        }

        points=new int[cols][rows][];
        for(int i=0;i<cols;i++)for(int j=0;j<rows;j++)points[i][j]=new int[]{i,j};
    }

    private final int[][][] points;

    private void fillRegion(final int pti,final int ptj,int region){
        for (int i = max(0, pti - 2); i < min(cols, pti + 3); i++) {
            for (int j = max(0, ptj - 2); j < min(rows, ptj + 3); j++) {
                if(regionMap[i][j]==region)continue;
                if(!isDigit(field[i][j]))continue;
                if(regionMap[i][j]!=0)throw new RuntimeException();
                regionMap[i][j]=region;
                fillRegion(i,j,region);
            }
        }
        for (int i = max(0, pti - 1); i < min(cols, pti + 2); i++) {
            for (int j = max(0, ptj - 1); j < min(rows, ptj + 2); j++) {
                if(regionMap[i][j]==region)continue;
                if(isDigit(field[i][j]))continue;
                if(regionMap[i][j]!=0)throw new RuntimeException();
                regionMap[i][j]=region;
            }
        }
    }

    public static AnalysisResult analysis(int[][] field){
        return new Engine(field).analysis();
    }

    private AnalysisResult analysis(){
        ArrayList<int[]> e=new ArrayList<>();
        ArrayList<int[]> m=new ArrayList<>();

        mayBeMined=new boolean[cols][rows];
        mayBeEmpty=new boolean[cols][rows];

        for(Point unknown:unknowns){
            field[unknown.col][unknown.row] = MINE;
            if(!mayBeMined[unknown.col][unknown.row]&&!validate(regionMap[unknown.col][unknown.row])){
                field[unknown.col][unknown.row]=EMPTY;
                mayBeEmpty[unknown.col][unknown.row]=true;
            }
            else {
                mayBeMined[unknown.col][unknown.row]=true;
                field[unknown.col][unknown.row]=EMPTY;
                if(!mayBeEmpty[unknown.col][unknown.row]&&!validate(regionMap[unknown.col][unknown.row])){
                    field[unknown.col][unknown.row]=MINE;
                    mayBeMined[unknown.col][unknown.row]=true;
                }
                else {
                    mayBeEmpty[unknown.col][unknown.row]=true;
                    field[unknown.col][unknown.row]=UNKNOWN;
                }
            }
        }

        for(int i=0;i<cols;i++){
            for(int j=0;j<rows;j++){
                if(mayBeMined[i][j]&&!mayBeEmpty[i][j])m.add(new int[]{i,j});
                if(mayBeEmpty[i][j]&&!mayBeMined[i][j])e.add(new int[]{i,j});
            }
        }

        return new AnalysisResult(e,m);
    }

    private boolean validate(int reg){
        Point pt=findBestPoint(reg);
        if(pt==null){
            onComplete();
            return true;
        }
        final int col=pt.col;
        final int row=pt.row;
        final int countMine=count(pt,MINE);
        final int countUnknown=count(pt,UNKNOWN);
        if(evalWays(pt,countMine,countUnknown)==0)return false;
        Setter setter=new Setter(col,row);
        firstVar(setter,field[col][row]-countMine);
        if(validate(reg)){
            setter.clearAll();
            return true;
        }
        while (nextVar(setter))if(validate(reg)){
            setter.clearAll();
            return true;
        }
        setter.clearAll();
        return false;
    }

    private boolean[][] mayBeMined;
    private boolean[][] mayBeEmpty;

    private void onComplete(){
        for(int i=0;i<cols;i++){
            for(int j=0;j<rows;j++){
                if(field[i][j]==EMPTY)mayBeEmpty[i][j]=true;
                if(field[i][j]==MINE)mayBeMined[i][j]=true;
            }
        }
    }

    private void firstVar(Setter setter,int mines){
        for(int i=0;i<mines;i++)setter.setPt(i,MINE);
        for(int i=mines;i<setter.getCountOfFreePoints();i++)setter.setPt(i,EMPTY);
    }

    private boolean nextVar(Setter setter){
        for(int i=0;i<setter.getCountOfFreePoints()-1;i++){
            if(setter.getPt(i+1)!=EMPTY)continue;
            if(setter.getPt(i)!=MINE)continue;

            setter.setPt(i,EMPTY);
            setter.setPt(i+1,MINE);

            int free=0;
            for(int j=0;j<i;j++){
                if(setter.getPt(j)==MINE){
                    setter.setPt(j,EMPTY);
                    setter.setPt(free++,MINE);
                }
            }
            return true;
        }
        return false;
    }


    private class Setter{
        private final ArrayList<int[]> pts=new ArrayList<>(8);
        Setter(int col,int row){
            for(int i=Math.max(col-1,0);i<Math.min(col+2,cols);i++){
                for(int j=Math.max(row-1,0);j<Math.min(row+2,rows);j++){
                    if(field[i][j]==UNKNOWN)pts.add(points[i][j]);
                }
            }
        }

        int getCountOfFreePoints(){
            return pts.size();
        }

        void clearAll(){
            for(int[] p:pts)field[p[0]][p[1]]=UNKNOWN;
        }

        void setPt(int ptNumber,int value){
            int[] ptCoords=pts.get(ptNumber);
            field[ptCoords[0]][ptCoords[1]]=value;
        }

        int getPt(int ptNumber){
            int[] ptCoords=pts.get(ptNumber);
            return field[ptCoords[0]][ptCoords[1]];
        }
    }

    /**
     *
     * @return Point(col,row) - цифра с наименьшим числом вариантов, если вариантов 0 - то эта ячейка с ошибкой
     *         null - позиция собрана, ошибок нет
     */
    private @Nullable Point findBestPoint(int reg){
        int best=Integer.MAX_VALUE;
        Point bestPoint=null;

        for(Point pt:numbers.get(reg-1)){
            final int countMine=count(pt,MINE);
            final int countUnknown=count(pt,UNKNOWN);
            if(!isUnfilled(pt,countMine,countUnknown))continue;
            final int ways=evalWays(pt,countMine,countUnknown);
            if(ways<best){
                best=ways;
                bestPoint=pt;
            }
        }
        return bestPoint;
    }

    /**
     *
     * @return число вариантов
     *         0 - ячейка с ошибкой
     */
    private int evalWays(Point pt,int countMine,int countUnknown){
        final int col=pt.col;
        final int row=pt.row;
        if(field[col][row]<countMine)return 0;
        if(countUnknown<field[col][row]-countMine)return 0;

        if(countUnknown==0)return 1;
        final int toPlace=field[col][row]-countMine;
        return C(countUnknown,toPlace);
    }

    public static int count=0;

    private int count(Point pt,int elem){count++;
        final int col=pt.col;
        final int row=pt.row;
        int count=0;
        for(int i=Math.max(col-1,0);i<Math.min(col+2,cols);i++){
            for(int j=Math.max(row-1,0);j<Math.min(row+2,rows);j++){
                if(field[i][j]==elem)count++;
            }
        }
        return count;
    }

    /**
     *
     * @param pt -
     * @return true  - если 1)ячейка с ошибкой 2) есть вокруг Unknown
     *         false - во всех остальных случаях
     */
    private boolean isUnfilled(Point pt,int countMine,int countUnknown){
        if(countUnknown!=0)return true;
        final int col=pt.col;
        final int row=pt.row;
        if(field[col][row]<countMine)return true;
        return countUnknown<field[col][row]-countMine;
    }

    private boolean isDigit(int i){
        return i<=8;
    }

    private static class Point{
        final int col;
        final int row;

        Point(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }
}
