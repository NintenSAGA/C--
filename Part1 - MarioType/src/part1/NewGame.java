package part1;

import javax.swing.*;

public class NewGame {
    public static boolean runThis (JFrame frame) {
        BuildUp pro1 = new BuildUp(frame);
        pro1.setUp("Level-01");     //加载关卡文件
        do {
            System.out.print("");
        } while (!pro1.exitSig);
        pro1.setUp("Level-02");     //加载关卡文件
        do {
            System.out.print("");
        } while (!pro1.exitSig);
        pro1.setUp("Level-03");     //加载关卡文件
        do {
            System.out.print("");
        } while (!pro1.exitSig);
        return true;
    }
}

