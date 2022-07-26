package part0;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

public class GameSelector {
    JFrame frame = new JFrame();
    static int width = 1280, height = 747;      //初始宽高比
    boolean [] process = new boolean[5];     //检测完成进度

    boolean loadKey = false;        //加载开关
    String choice;                  //当前选择关卡
    Font font;
    ToolBox tb;
    int fps = 45;

    BufferedImage logoPage, prompt;
    BufferedImage bg0, Selections, Info, StageSelectLogo;
    BufferedImage instruction1, instruction2;
    BufferedImage preface1, preface2;
    BufferedImage credit;

    ArrayList<Stage> stageList;
    int[][] selectPanel = {{0, 2, 0}, {1, 5, 3}, {0, 4, 0}};

    int phase;
    int[] f;

    SelectPanel sp;

    Fade fade0, fade1;
    typeWriter tw;
    String preface, ending;

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

        fade0 = new Fade();
        fade1 = new Fade();
        tw = new typeWriter();

        for (int i = 1; i <= 4; i++) {
            stageList.add(new Stage(i));
        }
        stageList.add(new finalStage());
        tb.infoLoadIn();
        tb.textLoadIn();
        tw.textInject(preface);

        try {
            font = Font.createFont(Font.TRUETYPE_FONT,
                    Objects.requireNonNull(this.getClass().getResourceAsStream("/part0/font.TTF")));
            bg0 = ImageIO.read(ToolBox.res("bg0.png"));
            Selections = ImageIO.read(ToolBox.res("Selections.png"));
            Info = ImageIO.read(ToolBox.res("Info.png"));
            StageSelectLogo = ImageIO.read(ToolBox.res("StageSelectLogo.png"));
            instruction1 = ImageIO.read(ToolBox.res("Instruction01.png"));
            instruction2 = ImageIO.read(ToolBox.res("Instruction02.png"));
            logoPage = ImageIO.read(ToolBox.res("logo_page.png"));
            prompt = ImageIO.read(ToolBox.res("prompt.png"));
            preface1 = ImageIO.read(ToolBox.res("preface1.png"));
            preface2 = ImageIO.read(ToolBox.res("preface2.png"));
            credit = ImageIO.read(ToolBox.res("credit.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void selectPanel() {
        phase = -2;
        f = new int[]{1, 1};
        fade0.fadeInSetUp(Color.black);
        frame.add(sp);
        frame.setVisible(true);
        timer.start();
    }

    public void backToPanel() {
        phase = 0;
        f = new int[]{1, 1};

        fade1.fadeInSetUp(Color.black);

        boolean complete = true;
        for (int i = 1; i <= 4; i++) {
            if (process[i-1]) stageList.get(i-1).setCleared();
            complete &= stageList.get(i-1).isCleared();
        }
        if (complete) stageList.get(4).setAvailable();

        frame.add(sp);
        frame.setVisible(true);
        timer.start();
    }

    public void loop() {
        fade0.fadeUpdate();
        fade1.fadeUpdate();
        if (phase == -1 || phase == 2) tw.update();
    }

    class SelectPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(font.deriveFont(30.0F));

            g2d.drawImage(bg0, 0, 0, null);

            switch (phase) {
                case -2 -> {
                    g2d.drawImage(logoPage, 0, 0, null);
                    g2d.drawImage(prompt, 0, 0, null);
                }
                case -1, 2 -> {
                    g2d.drawImage(tw.isFinished() ? preface2 : preface1, 0, 0, null);
                    tw.textShow(g2d, 129, 200, 32);
                }
                case 0 -> {
                    g2d.drawImage(StageSelectLogo, 71, 40, null);
                    g2d.drawImage(Selections, 0,  0, null);
                    for (Stage stage:stageList) stage.drawYourself(selectPanel[f[0]][f[1]], g2d);
                    g2d.drawImage(stageList.get(selectPanel[f[0]][f[1]]-1).isAvailable ? instruction2 : instruction1,
                            0 ,0 ,null);
                }
                case 1 -> {
                    g2d.drawImage(Info, 0, 0, null);
                    stageList.get(selectPanel[f[0]][f[1]]-1).showInfo(g2d);
                }
                case 3 -> g2d.drawImage(credit, 0, 0, null);
            }

            fade0.drawYourSelf(g2d);
            fade1.drawYourSelf(g2d);
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
        switch (phase) {
            case -2 -> fade0.fadeOutSetUp(Color.white, () -> {
                phase = -1;
                tw.textInject(preface);
                fade1.fadeInSetUp(Color.white);
            });
            case -1 -> fade0.fadeOutSetUp(Color.white, () -> {
                    phase = 0;
                    tw.textInject(ending);
                    fade1.fadeInSetUp(Color.white);
            });
            case 0 -> {
                if (stageList.get(selectPanel[f[0]][f[1]] - 1).isAvailable){
                    if (selectPanel[f[0]][f[1]] == 5) {
                        fade1.fadeOutSetUp(Color.white,
                                () -> {
                                    phase = 2;
                                    fade1.fadeInSetUp(Color.white);
                                });
                    } else {
                        phase = 1;
                    }
                }
            }
            case 1 -> fade1.fadeOutSetUp(Color.black, this::enterTheGame);
            case 2 -> fade0.fadeOutSetUp(Color.white, () -> {
                phase = 3;
                fade1.fadeInSetUp(Color.white);
            });
        }
    }

    static class typeWriter{
        private int cursor;
        private String textNow, textBuffered;

        public void textInject(String text) {
            cursor = 0;
            this.textBuffered = text;
        }
        public void textShow(Graphics2D g2d, int x, int y, int w) {
            g2d = (Graphics2D) g2d.create();

            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
            g2d.setColor(Color.white);
            part2.ToolBox.drawString(g2d, textNow, x, (int) (y-30.0F), w);
            g2d.dispose();
        }
        public void update() {
            cursor += !isFinished() ? 1 : 0;
            textNow = textBuffered.substring(0, cursor);
        }
        public boolean isFinished() {
            return cursor >= textBuffered.length();
        }
    }
}
