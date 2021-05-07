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
    final static int textWidth = 30;
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

    Scanner scanner;

    Timer timer = new Timer(1000/fps,     //建立计时器
            (ActionEvent e) -> {
                this.loop();            //参数更新
                display.repaint();      //渲染更新
            });

    public BuildUp(JFrame frame) {
        this.frame = frame;
    }

    public void setUp(String level) {
        this.level = level;
        this.tb = new ToolBox(this);

        tb.levelLoadIn();
        scene = new Scene(tb, enemy, player);

        tb.keyBindingSetUp();

        frame.add(display);
        frame.setVisible(true);

        timer.start();

        scanner = new Scanner(System.in);
    }

    public void loop() {
        player.update();
        enemy.update();

        scene.update();

//        System.out.println(scanner.nextLine());
    }

    class Display extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            scene.drawTheScene(g2d);
        }
    }

    public void gameSet() {
        display.repaint();
        timer.stop();
        frame.remove(display);
        this.exitSig = true;
    }

    public void gameFailed() {
        gameRestart();
    }

    public void gameRestart() {
        this.setUp(level);
    }
}