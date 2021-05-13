package part0;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameSelector {
    JFrame frame = new JFrame();
    static int width = 1280, height = 747;      //初始宽高比
    boolean [] process = new boolean[5];     //检测完成进度

    boolean loadKey = false;        //加载开关
    String choice;                  //当前选择关卡
    Font font;
    ToolBox tb;
    int fps = 60;

    BufferedImage bg0, Selections, Info, StageSelectLogo;
    BufferedImage instruction1, instruction2;

    ArrayList<Stage> stageList;
    int[][] selectPanel = {{0, 2, 0}, {1, 5, 3}, {0, 4, 0}};

    int phase;
    int[] f;

    SelectPanel sp;

    float opacity;
    float maxOpacity = 1.0F;
    float oV;
    float oA = 0.001F;
    boolean fadeIn;
    boolean fadeOut;

    Timer timer = new Timer(1000/fps,     //建立计时器
            (ActionEvent e) -> {         //参数更新
                this.loop();
                sp.repaint();      //渲染更新
            });

    public GameSelector() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setResizable(false);
        stageList = new ArrayList<>();
        sp = new SelectPanel();
        tb = new ToolBox(this);
        tb.keyBindingSetUp();

        for (int i = 1; i <= 5; i++) {
            stageList.add(new Stage(i));
        }

        try {
            font = Font.createFont(Font.TRUETYPE_FONT,
                    this.getClass().getResourceAsStream("/font.TTF"));
            bg0 = ImageIO.read(ToolBox.res("bg0.png"));
            Selections = ImageIO.read(ToolBox.res("Selections.png"));
            Info = ImageIO.read(ToolBox.res("Info.png"));
            StageSelectLogo = ImageIO.read(ToolBox.res("StageSelectLogo.png"));
            instruction1 = ImageIO.read(ToolBox.res("Instruction01.png"));
            instruction2 = ImageIO.read(ToolBox.res("Instruction02.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void selectPanel() {
        phase = 0;
        f = new int[]{1, 1};

        opacity = maxOpacity;
        fadeIn = true;
        oV = 0;
        fadeOut = false;

        for (int i = 1; i <= 5; i++) {
            if (process[i-1]) stageList.get(i-1).setCleared();
        }

        frame.add(sp);
        frame.setVisible(true);

        timer.start();

    }

    public void loop() {
        fadeUpdate();
    }

    class SelectPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(font.deriveFont(30.0F));

            g2d.drawImage(bg0, 0, 0, null);

            switch (phase) {
                case 0 -> {
                    g2d.drawImage(StageSelectLogo, 71, 40, null);
                    g2d.drawImage(Selections, 0,  0, null);
                    for (Stage stage:stageList) stage.drawYourself(selectPanel[f[0]][f[1]], g2d);
                    g2d.drawImage(stageList.get(selectPanel[f[0]][f[1]]-1).isAvailable ? instruction2 : instruction1,
                            0 ,0 ,null);
                }
                case 1 -> {
                    g2d.drawImage(Info, 0, 0, null);
                }
            }

            if (fadeIn || fadeOut) blackScreen((Graphics2D) g2d.create());
        }
    }

    public void enterTheGame() {
        timer.stop();
        sp.repaint();
        frame.remove(sp);
        loadKey = true;
        choice = String.valueOf(selectPanel[f[0]][f[1]]);
    }

    public void left() {
        if (phase == 0 && f[1] != 0 && selectPanel[f[0]][f[1]-1] != 0) f[1] -= 1;
        else if (phase == 1) phase = 0;
    }

    public void right() {
        if (phase == 0 && f[1] != 2 && selectPanel[f[0]][f[1]+1] != 0) f[1] += 1;
    }

    public void up() {
        if (phase == 0 && f[0] != 0 && selectPanel[f[0]-1][f[1]] != 0) f[0] -= 1;
    }

    public void down() {
        if (phase == 0 && f[0] != 2 && selectPanel[f[0]+1][f[1]] != 0) f[0] += 1;
    }

    public void confirm() {
        if (phase == 0 && stageList.get(selectPanel[f[0]][f[1]]-1).isAvailable) phase = 1;
        else if (phase == 1) fadeOut = true;
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
            if (opacity >= maxOpacity) enterTheGame();
        }
    }
}
