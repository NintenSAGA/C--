package part4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


class BuildUp {
    boolean exitSig = false;
    String level;

    static int width = 1280, height = 720;       //设定画面宽高
    int bornX = width/2, bornY = height*4/5;

    JFrame frame = new JFrame();
    Display display = new Display();

    int fps = 60;                               //设定每秒帧数
    ToolBox tb;                 //创建工具箱对象

    Shooter st;                     //射手
    ArrayList<Bullet> btList;       //已发射子弹
    ArrayList<Ball> ballList;       //已生成球
    ArrayList<Bullet> unShotList;   //未发射子弹
    ArrayList<Ball> unGenBallList;  //未生成球

    Timer timer = new Timer(1000/fps,     //建立计时器
            (ActionEvent e) -> {
                this.loop();            //参数更新
                display.repaint();      //渲染更新
            });

//    public BuildUp(String level) {
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(width, height);
//        frame.setResizable(false);
//        this.level = level;
//    }

    public BuildUp(JFrame frame) {
        this.frame = frame;
    }   //构造函数

    public void setUp(String level) {                   //新游戏构建
        this.level = level;                             //关卡文件加载
        st = new Shooter(bornX, bornY, this);
        btList = new ArrayList<>();
        ballList = new ArrayList<>();
        unShotList = new ArrayList<>();
        unGenBallList = new ArrayList<>();
        tb = new ToolBox(this);

        tb.keyBindingSetUp(this.display, st);           //键位绑定
        tb.garbageLoadIn(level);                        //加载垃圾
        Collections.shuffle(unShotList);                //乱序排列
        Collections.shuffle(unGenBallList);             //乱序排列

        frame.add(display);
        frame.setVisible(true);

        timer.start();
    }

    public void loop() {

        tb.ballGenerate();      //定时生成新球

        ballList.parallelStream().forEach(Ball::update);    //更新每个球
        btList.parallelStream().forEach(Bullet::update);    //更新每个子弹
        
        Iterator<Bullet> ibt = btList.iterator();
        while (ibt.hasNext()) {
            Bullet bt = ibt.next();
            Iterator<Ball> ibl = ballList.iterator();
            while (ibl.hasNext()) {
                Ball bl = ibl.next();
                if (tb.collisionDetect(bt, bl) == 1) {
                    //打中了球且类型相符
                    ibt.remove();
                    ibl.remove();
                    break;
                } else if (tb.outOfRangeDetect(bt) || tb.collisionDetect(bt, bl) == -1) {
                    //打中了球但类型不符
                    unShotList.add(bt);
                    ibt.remove();
                    break;
                }
            }
        }

        st.update();        //更新射手

        if (unShotList.isEmpty()) gameSet();
    }

    public void gameSet() {
        //游戏通关
        display.repaint();
        timer.stop();
        frame.remove(display);
        exitSig = true;
    }

    public void gameRestart() {
        //再来一次
        timer.stop();
        this.setUp(level);
    }

    class Display extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //Print out the background

            Graphics2D g2d = (Graphics2D) g;

            ballList.forEach(bl -> bl.printThis(g2d));

            btList.forEach(bt -> bt.printThis(g2d));

            st.printThis(g2d);

        }
    }
}