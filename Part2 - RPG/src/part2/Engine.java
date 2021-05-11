package part2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Scanner;

class BuildUp {
    boolean exitSig = false;
    String level;

    private static final int width = 1280;
    private static final int height = 720;       //设定画面宽高
    static final int textAreaHeight = 580;
    final static int textWidth = 35;
    final static float fontSize = 30.0F;
    final static int textLeftSide = 130;
    final static int textUpperSide = 600;

    private final JFrame frame;
    final Display display = new Display();

    private final static int fps = 45;                               //设定每秒帧数
    ToolBox tb;                 //创建工具箱对象

    Player player;
    Enemy enemy;
    Scene scene;

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
        opacity = maxOpacity;
        fadeIn = true;
        oV = 0;
        fadeOut = false;

        tb.levelLoadIn();
        scene = new Scene(tb, enemy, player);

        tb.keyBindingSetUp();

        frame.add(display);
        frame.setVisible(true);

        timer.start();
    }

    public void loop() {
        if (!fadeIn) {
            player.update();
            enemy.update();
            scene.update();
        }

        fadeUpdate();
    }

    class Display extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(font.deriveFont(fontSize));
            scene.drawTheScene(g2d);
            if (fadeIn || fadeOut) blackScreen((Graphics2D) g2d.create());
        }
    }

    public void gameFailed() {
        gameRestart();
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
                scene.nextText();
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