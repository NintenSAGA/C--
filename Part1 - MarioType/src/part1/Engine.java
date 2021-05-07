package part1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


class BuildUp {
    boolean exitSig = false;
    String level;

    int groundSum;                              //可种植土地数量

    static int width = 1280, height = 720;       //设定画面宽高
    static int offSetY = 80;                    //竖直偏移量
    static boolean levelScroll = true;          //水平卷轴开关
    static boolean verticalScroll = false;      //竖直卷轴开关

    JFrame frame = new JFrame();
    Display display = new Display();

    int fps = 60;                               //设定每秒帧数
    ToolBox tb = new ToolBox();                 //创建工具箱对象

    int levelGround = 40*(14)+offSetY;            //设定地面高度
    ArrayList<BackGround> bgs; //存储背景图片

    int bornX = width/2, bornY = levelGround;   //设定出生位置

    Human hm;                               //建立人物
    ArrayList<Tile> tileList;               //瓷砖列表
    ArrayList<MovingObjects> creatureList;  //生物列表
    ArrayList<Item> itemList;               //道具列表

    Timer timer = new Timer(1000/fps,     //建立计时器
            (ActionEvent e) -> {
                this.loop();
                display.repaint();
            });

    public BuildUp(JFrame frame) {
        this.frame = frame;
    }

    public void setUp(String level) {
        this.level = level;
        this.exitSig = false;
        this.groundSum = 0;         //一定要初始化

        hm = new Human(bornX, bornY);
        tileList = new ArrayList<>();   //瓷砖列表
        creatureList = new ArrayList<>();
        itemList = new ArrayList<>();
        bgs = new ArrayList<>();

        tb.keyBindingSetUp(this, display, hm);

        double ratio = 0.5;             //运动跟随速度
        BackGround middle = new BackGround(0, 0, ratio);

        bgs.add(new BackGround(-BackGround.w, 0, ratio));  //0
        bgs.add(middle);                                        //1
        bgs.add(new BackGround(BackGround.w, 0, ratio));   //2

        SceneBuilder sB = new SceneBuilder();
        sB.ObjLoader(this, level);

        frame.add(display);
        frame.setVisible(true);

        timer.start();
    }

    public boolean getKey() {
        return exitSig;
    }

    public void loop() {

        hm.update(tileList, creatureList, itemList);

        creatureList.forEach(e -> e.update(hm, tileList));

        tb.mapScroller(bgs);

        bgs.forEach(b -> b.refresh(bornX, hm)); //刷新每一张背景的渲染坐标

        tileList.forEach(t -> t.refresh(hm));   //刷新每一张瓷砖的渲染坐标

        itemList.forEach(i -> i.refresh(hm));

        if (groundSum == 0) {
            gameSet();          //全部栽种完毕后通关
        }

    }

    public void gameRestart() {
        timer.stop();
        this.setUp(level);
    }

    public void gameSet() {
        display.repaint();
        timer.stop();
        frame.remove(display);
        this.exitSig = true;

    }

    class Display extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //Print out the background
            Graphics2D g2d = (Graphics2D) g;

            bgs.forEach(b -> b.printBg(g2d));
            itemList.stream().filter(i -> i.appear).forEach(i -> i.printTile(g2d));
            tileList.stream().filter(t -> t.appear).forEach(t -> t.printTile(g2d));
            creatureList.stream().filter(e -> e.appear).forEach(e -> e.printCh(g2d));
            //只渲染仍在显示的

            hm.printCh(g2d);
        }
    }
}