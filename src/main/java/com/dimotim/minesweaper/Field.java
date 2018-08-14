package com.dimotim.minesweaper;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Field extends JPanel {
    private final JButton[][] btns;
    private @NotNull Model model;
    private final ArrayList<FieldListener> listeners=new ArrayList<>();
    private int mineMarked=-1;
    public Field(Model model){
        this.model=model;
        final int rows=model.getRows();
        final int cols=model.getCols();
        setLayout(new GridLayout(rows,cols));
        btns=new JButton[cols][rows];
        initButtons(cols,rows);
        initListeners();
        model.addModelChangeListener(v->update());
        update();
    }

    public void addFieldListener(FieldListener listener){
        listeners.add(listener);
    }

    public Model getModel() {
        return model;
    }

    public void update(){
        for(int i=0;i<btns[0].length;i++){
            for(int j=0;j<btns.length;j++){
                btns[j][i].setBackground(Color.BLACK);
                btns[j][i].setText(model.getCell(j,i).toString());
                if(model.getCell(j,i)!=Pt.Unknown)btns[j][i].setBackground(Color.DARK_GRAY);
                if(model.getCell(j,i)==Pt.Mine)btns[j][i].setBackground(Color.RED);
                if(model.getCell(j,i)==Pt.Flag)btns[j][i].setBackground(Color.BLUE);
            }
        }

        if(model.isGameOver())notifyListenersGameOver();
        if(mineMarked!=model.getMineMarked()){
            mineMarked=model.getMineMarked();
            notifyListenersMineMarked();
        }
    }

    private void notifyListenersGameOver(){
        for(FieldListener l:listeners)l.onGameOver(model.isWin());
    }

    private void notifyListenersMineMarked(){
        for(FieldListener l:listeners)l.onMineMarked(mineMarked);
    }

    private void initListeners(){
        for(int i=0;i<btns.length;i++){
            for(int j=0;j<btns[0].length;j++){
                final int ii=i;
                final int jj=j;
                btns[i][j].addActionListener(e->{
                    if(Controller.isOptimal(model,ii,jj)){
                        model.selectPointSafe(ii,jj);
                        update();
                    }
                    else JOptionPane.showMessageDialog(null,"Просрал");
                });
                btns[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(SwingUtilities.isRightMouseButton(e)) {
                            model.markPoint(ii, jj);
                            update();
                        }
                    }
                });
            }
        }
    }

    private void initButtons(int cols,int rows){
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                btns[j][i]=createButton(model.getCell(j,i).toString());
                add(btns[j][i]);
            }
        }
    }

    private JButton createButton(String name){
        JButton button=new JButton(name);
        button.setPreferredSize(new Dimension(50,50));
        button.setFocusable(false);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.GREEN);
        button.setFont(new Font("",Font.BOLD,30));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE,1));
        return button;
    }

    public interface FieldListener{
        void onGameOver(boolean isWin);
        void onMineMarked(int markedMines);
    }
}
