package com.dimotim.minesweaper.engine;

import java.util.ArrayList;

public class AnalysisResult {
    public final ArrayList<int[]> emptyPts;
    public final ArrayList<int[]> minedPts;

    public AnalysisResult(ArrayList<int[]> emptyPts, ArrayList<int[]> minedPts) {
        this.emptyPts = emptyPts;
        this.minedPts = minedPts;
    }
}
