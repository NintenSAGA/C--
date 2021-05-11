package part3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

class BuildUp {
    boolean exitSig = false;
    String level;

    private final static int width = 1280, height = 720;       //设定画面宽高
    final static int textAreaHeight = 483;
    final static int textWidth = 42;
    final static float fontSize = 19.0F;
    final static int textLeftSide = width/2 - (int) fontSize*(textWidth/2);
    final static int textUpperSide = 520;

    JFrame frame;
    Display display = new Display();

    int fps = 45;                               //设定每秒帧数
    ToolBox tb;                 //创建工具箱对象

    ArrayList<Scene> sceneList;
    Scene sceneNow;
    String textNow;
    Stage stageNow;

    float opacity;
    float maxOpacity = 1.0F;
    float oV;
    float oA = 0.001F;
    boolean fadeIn;
    boolean fadeOut;

    Font font;

    Timer timer = new Timer(1000/fps,     //建立计时器
            (ActionEvent e) -> {
                this.loop();            //参数更新
                display.repaint();      //渲染更新
            });

    public BuildUp(JFrame frame) {
        this.frame = frame;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/STHeiti Light.ttc"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setUp(String level) {
        this.level = level;
        this.tb = new ToolBox(this);
        this.sceneList = new ArrayList<>();

        tb.levelLoadIn();
        sceneNow = sceneList.get(0);
        stageNow = sceneNow.stageList.get(0);
        textNow = sceneNow.backGround;
        opacity = maxOpacity;
        fadeIn = true;
        oV = 0;
        fadeOut = false;

        tb.keyBindingSetUp();

        frame.add(display);
        frame.setVisible(true);

        timer.start();
    }

    public void loop() {
        if (!fadeIn) sceneNow.aniUpdate();
        fadeUpdate();
    }

    class Display extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setFont(font.deriveFont(fontSize));
            sceneNow.printBG(g2d);
            if (!fadeIn) sceneNow.printText(g2d);
            if (fadeIn || fadeOut) blackScreen((Graphics2D) g2d.create());
        }
    }

    public void gameSet() {
        fadeOut = true;
    }

    public void gameExit() {
        display.repaint();
        timer.stop();
        frame.remove(display);
        this.exitSig = true;
    }

    public void gameRestart() {
        this.setUp(level);
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
            if (opacity >= maxOpacity) gameExit();
        }
    }
}