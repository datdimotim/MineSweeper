package com.dimotim.minesweaper;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public static void main(String[] args) {
        new MainWindow();
    }

    public MainWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initUI();
        pack();
        setVisible(true);
    }

    private void initUI(){
        JPanel root=new JPanel(new BorderLayout());
        JPanel controlPanel=new JPanel();
        JButton startButton=new JButton("New Game");
        JButton stepEngineButton=new JButton("Step Engine");
        JButton runEngineButton=new JButton("Run Engine");
        JLabel mineLabel=new JLabel();
        controlPanel.add(startButton);
        controlPanel.add(stepEngineButton);
        controlPanel.add(runEngineButton);
        controlPanel.add(mineLabel);
        final Field[] field=new Field[1];
        final int cols=30;
        final int rows=16;
        final int mines=99;
        startButton.addActionListener(e->{
            if(field[0]!=null)root.remove(field[0]);
            field[0]=new Field(new Model(cols, rows, mines));
            field[0].addFieldListener(new Field.FieldListener() {
                @Override
                public void onGameOver(boolean isWin) {
                    JOptionPane.showMessageDialog(null,isWin?"You are win!":"You are lose.");
                }

                @Override
                public void onMineMarked(int markedMines) {
                    mineLabel.setText("Mines: "+(mines-markedMines));
                }
            });
            root.add(field[0],BorderLayout.CENTER);
            revalidate();
        });

        stepEngineButton.addActionListener(e->Controller.play(field[0].getModel(),field[0]));
        runEngineButton.addActionListener(e->Controller.run(field[0].getModel(),field[0]));

        root.add(controlPanel,BorderLayout.AFTER_LAST_LINE);
        startButton.doClick();
        setContentPane(root);
    }
}

