package part3;

import part0.Fade;

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

    Fade fade;

    Font font;

    Timer timer = new Timer(1000/fps,     //建立计时器
            (ActionEvent e) -> {
                this.loop();            //参数更新
                display.repaint();      //渲染更新
            });

    public BuildUp(JFrame frame) {
        this.frame = frame;
        fade = new Fade();

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/font.TTF"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setUp(String level) {
        this.level = level;
        this.tb = new ToolBox(this);
        this.sceneList = new ArrayList<>();
        exitSig = false;

        tb.levelLoadIn();
        sceneNow = sceneList.get(0);
        stageNow = sceneNow.stageList.get(0);
        textNow = sceneNow.backGround;

        tb.keyBindingSetUp();

        frame.add(display);
        frame.setVisible(true);

        timer.start();

        fade.fadeInSetUp(Color.black);
    }

    public void loop() {
        if (fade.isFinished()) sceneNow.aniUpdate();
        fade.fadeUpdate();
    }

    class Display extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setFont(font.deriveFont(Font.BOLD, fontSize));
            sceneNow.printBG(g2d);
            if (fade.isFinished()) sceneNow.printText(g2d);
            fade.drawYourSelf(g2d);
        }
    }

    public void gameSet() {
        fade.fadeOutSetUp(Color.black, this::gameExit);
    }

    public void gameExit() {
        display.repaint();
        timer.stop();
        frame.remove(display);
        this.exitSig = true;
    }

    public void gameRestart() {
        fade.fadeOutSetUp(Color.black, () -> this.setUp(level));
    }
}