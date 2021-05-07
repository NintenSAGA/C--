package part2;

import javax.swing.*;

public class NewGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        int width = 1280, height = 740;      //初始宽高比
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setResizable(false);
        NewGame.runThis(frame);
    }

    public static boolean runThis (JFrame frame) {
        BuildUp pro1 = new BuildUp(frame);
        pro1.setUp("material/Part2/level_1");     //加载关卡文件
        do {
            System.out.print("");
        } while (!pro1.exitSig);
        return true;
    }
}
