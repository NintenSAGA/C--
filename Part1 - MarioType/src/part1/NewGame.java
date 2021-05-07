package part1;

import javax.swing.*;

public class NewGame {
    public static void main (String[] args) {

//        try {
//            BuildUp pro1 = new BuildUp();
//            pro1.setUp("material/Part1/Level");
//            boolean key;
//            while (true){
//                //key = pro1.getKey();
//                System.out.println("");
//                if (pro1.exitSig) {
//                    return;
//                }
//                //key = pro1.getKey();
//            }
//            //new BuildUp().run();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    public static boolean runThis (JFrame frame) {
        BuildUp pro1 = new BuildUp(frame);
        pro1.setUp("material/Part1/Level");     //加载关卡文件
        while (true){
            System.out.println("");
            if (pro1.exitSig) {
                return true;
            }
        }
    }
}

