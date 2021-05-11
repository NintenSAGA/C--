package part0;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NewGame {
    public static void main(String[] args) {
        GameSelector gs = new GameSelector();

        gs.selectPanel();   //加载图形界面

        Map<String, Function<JFrame, Boolean>> map = new HashMap<>();    //lambda加载 Function类型
        map.put("1", part1.NewGame::runThis);       //游戏一
        map.put("2", part2.NewGame::runThis);
        map.put("3", part3.NewGame::runThis);
        map.put("4", part4.NewGame::runThis);       //游戏二

        //在收到下一次关卡加载信号前不断轮询
        while(true) {
            System.out.print("");  //无意义代码，为了让循环继续
            if (gs.loadKey) {
                gs.frame.getContentPane().removeAll();  //清楚上一轮留下的构件
                gs.process[Integer.parseInt(gs.choice)-1] = map.get(gs.choice).apply(gs.frame);    //进入相应关卡
                gs.selectPanel();   //重新加载图形界面
                gs.loadKey = false; //停止加载
            }
        }
    }

}
