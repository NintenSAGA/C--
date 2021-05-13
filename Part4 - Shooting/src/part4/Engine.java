package part4;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


class BuildUp {
    boolean exitSig = false;
    String level;

    static int width = 1280, height = 720;       //设定画面宽高
    int bornX = width/2, bornY = height*4/5;

    JFrame frame;
    Display display = new Display();

    int fps = 60;                               //设定每秒帧数
    ToolBox tb;                 //创建工具箱对象

    Shooter st;                     //射手
    ArrayList<Bullet> btList;       //已发射子弹
    ArrayList<Ball> ballList;       //已生成球
    ArrayList<Bullet> unShotList;   //未发射子弹
    ArrayList<Ball> unGenBallList;  //未生成球

    float opacity;
    float maxOpacity = 1.0F;
    float oV;
    float oA = 0.001F;
    boolean fadeIn;
    boolean fadeOut;

    BufferedImage instruction1, instruction2;
    BufferedImage bg, tube;

    Font font;
    static final float fontSize = 40.0F;

    Timer timer = new Timer(1000/fps,     //建立计时器
            (ActionEvent e) -> {
                this.loop();            //参数更新
                display.repaint();      //渲染更新
            });

    public BuildUp(JFrame frame) {
        this.frame = frame;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT,
                    this.getClass().getResourceAsStream("/font.TTF"));
            instruction1 = ImageIO.read(ToolBox.res("Instruction41.png"));
            instruction2 = ImageIO.read(ToolBox.res("Instruction42.png"));
            bg = ImageIO.read(ToolBox.res("bg.png"));
            tube = ImageIO.read(ToolBox.res("tube.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        opacity = maxOpacity;
        fadeIn = true;
        oV = 0;
        fadeOut = false;

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

        if (unShotList.isEmpty()) fadeOut = true;

        fadeUpdate();
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
            g2d.setFont(font.deriveFont(Font.BOLD,fontSize));

            g2d.drawImage(bg, 0, 0, null);

            st.printThis(g2d);

            ballList.forEach(bl -> bl.printThis(g2d));

            btList.forEach(bt -> bt.printThis(g2d));

            g2d.drawImage(btList.isEmpty() ? instruction2 : instruction1,
                    0, 0, null);

            g2d.drawImage(tube, 0, 0, null);

            if (fadeIn || fadeOut) blackScreen((Graphics2D) g2d.create());

        }
    }

    public void blackScreen(Graphics2D g2d) {
        AlphaComposite composite = (AlphaComposite) g2d.getComposite();
        g2d.setComposite(composite.derive(Math.max(0F, Math.min(1.0F, opacity))));
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
    }

    public void fadeUpdate() {
        if (fadeIn) {
            oV += oA;
            opacity -= oV;
            if (opacity <= 0) {
                oV = 0;
                fadeIn = false;
            }
        }
        if (fadeOut) {
            oV += oA;
            opacity += oV;
            if (opacity >= maxOpacity) gameSet();
        }
    }
}