package part1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

import part0.Fade;


class BuildUp {
    boolean exitSig = false;
    String level;

    int groundSum;                              //可种植土地数量

    static int width = 1280, height = 720;       //设定画面宽高
    static int offSetY = 80;                    //竖直偏移量
    static boolean levelScroll = true;          //水平卷轴开关
    static boolean verticalScroll = false;      //竖直卷轴开关

    JFrame frame;
    Display display = new Display();

    int fps = 60;                               //设定每秒帧数
    ToolBox tb = new ToolBox();                 //创建工具箱对象

    int levelGround = 40*(14)+offSetY;            //设定地面高度
    ArrayList<BackGround> bgs; //存储背景图片

    int bornX = width/2, bornY = levelGround-75;   //设定出生位置

    Human hm;                               //建立人物
    ArrayList<Tile> tileList;               //瓷砖列表
    ArrayList<MovingObjects> creatureList;  //生物列表
    ArrayList<Item> itemList;               //道具列表

    BufferedImage instruction1, instruction2;
    Font font;

    Fade fade;

    Timer timer = new Timer(1000/fps,     //建立计时器
            (ActionEvent e) -> {
                this.loop();
                display.repaint();
            });

    public BuildUp(JFrame frame) {
        this.frame = frame;
        fade = new Fade();

        try {
            instruction1 = ImageIO.read(ToolBox.res("Instruction11.png"));
            instruction2 = ImageIO.read(ToolBox.res("Instruction12.png"));
            font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(this.getClass().getResourceAsStream("/font.TTF")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

        fade.fadeInSetUp(Color.black);
    }

    public void loop() {
        if (groundSum == 0) {
            gameSet();
        }

        fade.fadeUpdate(0.01F);

        hm.update(tileList, creatureList, itemList);

        creatureList.forEach(e -> e.update(hm, tileList));

        tb.mapScroller(bgs);

        bgs.forEach(b -> b.refresh(bornX, hm)); //刷新每一张背景的渲染坐标

        tileList.forEach(t -> t.refresh(hm));   //刷新每一张瓷砖的渲染坐标

        itemList.forEach(i -> i.refresh(hm));

    }

    public void gameRestart() {
        fade.fadeOutSetUp(Color.black, () -> this.setUp(level));
    }

    public void gameSet() {
        fade.fadeOutSetUp(Color.black, this::exit);
        //exit();
    }

    public void exit() {
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

            g2d.drawImage(hm.specialSupportTile!=null ? instruction2 : instruction1, 0, 0, null);

            g2d.setFont(font.deriveFont(Font.BOLD, 25F));
            g2d.setColor(Color.white);
            g2d.drawString("剩余空地：%d处".formatted(groundSum), 1000, 42+25);

            fade.drawYourSelf(g2d);
        }
    }
}