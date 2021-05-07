package part4;

import javax.swing.*;

public class NewGame {
    public static void main(String[] args) {
        //new BuildUp("material/Part4/GarbageListEdited").setUp();
    }

    public static boolean runThis(JFrame frame) {
        BuildUp pro4 = new BuildUp(frame);
        pro4.setUp("material/Part4/GarbageListEdited");     //外部传入文件路径
        while (true){
            System.out.println("");     //无用代码
            if (pro4.exitSig) {
                return true;            //返回通关成功指令
            }
        }
    }
}
