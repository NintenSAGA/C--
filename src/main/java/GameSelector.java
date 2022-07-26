//import javax.swing.*;
//import java.awt.*;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//public class GameSelector {
//    JFrame frame = new JFrame();
//    static int width = 1280, height = 720;      //初始宽高比
//    static Map<String, Function<JFrame, Boolean>> map = new HashMap<>();    //lambda加载 Function类型
//    static boolean [] process = new boolean[4];     //检测完成进度
//
//    boolean loadKey = false;        //加载开关
//    String choice;                  //当前选择关卡
//    JPanel panel;
//    String display;
//
//    static {
//        map.put("1", part1.NewGame::runThis);       //游戏一
//        map.put("2", part2.NewGame::runThis);
//        map.put("3", part3.NewGame::runThis);
//        map.put("4", part4.NewGame::runThis);       //游戏二
//    }
//
//    public static void main(String[] args) {
//        GameSelector gs = new GameSelector();
//
//        //Scanner scanner = new Scanner(System.in);
//        //String choice = scanner.next();
//        //调试用代码
//
//        gs.selectPanel();   //加载图形界面
//
//        //在收到下一次关卡加载信号前不断轮询
//        while(true) {
//            System.out.print("");  //无意义代码，为了让循环继续
//            if (gs.loadKey) {
//                gs.frame.getContentPane().removeAll();  //清楚上一轮留下的构件
//                process[Integer.parseInt(gs.choice)-1] = map.get(gs.choice).apply(gs.frame);    //进入相应关卡
//                gs.selectPanel();   //重新加载图形界面
//                gs.loadKey = false; //停止加载
//            }
//        }
//
//
//    }
//
//    public GameSelector() {
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(width, height);
//        frame.setResizable(false);
//    }
//
//    public void selectPanel() {
//        panel = new JPanel();
//        Container container = frame.getContentPane();
//
//        Button part1 = new Button("Part 1");
//        Button part2 = new Button("Part 2");
//        Button part3 = new Button("Part 3");
//        Button part4 = new Button("Part 4");
//
//        JTextField jTextField = new JTextField();
//        display = String.format("关卡1:%s 关卡2:%s 关卡3:%s 关卡4:%s",
//                process[0], process[1], process[2], process[3]);        //显示当前完成进度
//        jTextField.setText(display);
//        jTextField.setEditable(false);
//
//        part1.addActionListener((e) -> {loadKey = true; choice = "1";});
//        part2.addActionListener((e) -> {loadKey = true; choice = "2";});
//        part3.addActionListener((e) -> {loadKey = true; choice = "3";});
//        part4.addActionListener((e) -> {loadKey = true; choice = "4";});
//
//        panel.setLayout(new BorderLayout());
//        panel.add(BorderLayout.WEST, part1);
//        panel.add(BorderLayout.NORTH, part2);
//        panel.add(BorderLayout.SOUTH, part3);
//        panel.add(BorderLayout.EAST,part4);
//        panel.add(BorderLayout.CENTER, jTextField);
//
//        container.add(panel);
//
//        frame.setVisible(true);
//
//
//    }
//
//    public void test(String choice) {
//        //map.get(choice).accept(this.frame);
//    }
//}
