package part3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class Scene {
    String backGround;

    ArrayList<Stage> stageList;
    Iterator<Stage> stageI;

    String textNow;
    Stage stageNow;

    ToolBox tb;

    String mode;
    int index;
    int phase;
    float nameHeight;

    //动画
    double orgRatio = 0.5;
    double ratio = orgRatio;
    double a = 0.1;
    double v = 0;
    int cursor = 0;
    GameCharacter gc;
    String backUpText;
    boolean successKey;
    boolean exitKey;

    //贴图加载
    String self;
    String oppo;
    String bg;
    String filePlace;

    String name;
    String[] nameList;

    BufferedImage bg_center;
    BufferedImage objection;
    BufferedImage textArea;
    BufferedImage success;
    BufferedImage A_backward, D_forward, W_objection;
    BufferedImage left_arrow, right_arrow;

    static HashMap<String, Color> colorMap = new HashMap<>();
    static {
        colorMap.put("B0", Color.PINK);
        colorMap.put("A1", Color.green);
        colorMap.put("S1", Color.green);
        colorMap.put("R2", Color.red.brighter());
        colorMap.put("H1", Color.orange);
        colorMap.put("I1", Color.magenta);
        colorMap.put("W1", Color.orange);
    }
    static HashMap<String, Runnable> shiftMap = new HashMap<>();
    {
        shiftMap.put("Q0", () -> modeShift("A", true));
        shiftMap.put("A0", () -> phaseShift(1));
        shiftMap.put("A1", () -> modeShift("H", true));
        shiftMap.put("H1", () -> modeShift("A", true));
        shiftMap.put("R2", () -> phaseShift(3));
        shiftMap.put("S1", () -> phaseShift(2));
        shiftMap.put("I1", () -> modeShift("A", true));
        shiftMap.put("W1", () -> modeShift("A", false));
    }

    public Scene(String backGround, ToolBox tb, ArrayList<Stage> stageList, String[] nameList, String[] materialList) {
        this.backGround = backGround;
        this.textNow = "";
        this.backUpText = backGround;
        this.cursor = 0;
        this.tb = tb;
        this.mode = "B";
        this.stageList = stageList;
        this.stageI = stageList.iterator();
        this.nameList = nameList;
        this.filePlace = "material/Part3/";
        this.self = materialList[1];
        this.oppo = materialList[2];
        this.bg = materialList[3];
        this.gc = new GameCharacter(this.self, this.oppo, this.bg, this.filePlace);

        try {
            textArea = ImageIO.read(new File("material/Part3/TextArea.png"));
            objection = ImageIO.read(new File("material/Part3/objection.png"));
            bg_center = ImageIO.read(new File(filePlace+bg+"_center.png"));
            success = ImageIO.read(new File("material/Part3/success.png"));
            A_backward = ImageIO.read(new File("material/Part3/A_backward.png"));
            D_forward = ImageIO.read(new File("material/Part3/D_forward.png"));
            W_objection = ImageIO.read(new File("material/Part3/W_objection.png"));
            left_arrow = ImageIO.read(new File("material/Part3/left_arrow.png"));
            right_arrow = ImageIO.read(new File("material/Part3/right_arrow.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printBG(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();
        if (stageNow != null) gc.printCh(g2d, mode, phase, stageNow.isAnswer(mode, index));
        else g2d.drawImage(bg_center, 0, 0, null);

        g2d.drawImage(textArea, 0, BuildUp.textAreaHeight, null);
        g2d.drawImage(D_forward, 0, 0, null);
        g2d.drawImage(right_arrow, 0, 0, null);
        if ((mode+phase).equals("A1")) {
            if (index > 0){
                g2d.drawImage(A_backward, 0, 0, null);
                g2d.drawImage(left_arrow, 0, 0, null);
            }
            g2d.drawImage(W_objection, 0, 0, null);
        }
        g2d.dispose();
    }

    public void printText(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();
        g2d.setFont(g2d.getFont().deriveFont(BuildUp.fontSize));
        g2d.setColor(colorMap.getOrDefault(mode+phase, Color.white));
        tb.drawString(g2d, textNow, BuildUp.textLeftSide, BuildUp.textUpperSide, BuildUp.textWidth);
        g2d.setColor(Color.white);

        //显示说话人
        if (nameHeight == 0) {nameHeight = BuildUp.textAreaHeight+g2d.getFontMetrics().getHeight()+3.5F; }
        if (mode.equals("B")) {
            name = nameList[0];
        } else if ("QH".contains(mode)) {
            name = nameList[1];
        } else {
            name = nameList[2];
        }
        float nameLeft = 126 + (175-BuildUp.fontSize*name.length())*0.5F;
        g2d.drawString(name, nameLeft, nameHeight);

        BufferedImage vfx = (mode+phase).equals("S1") ? objection : (successKey ? success : null);
        if (vfx != null) {
            int w = vfx.getWidth(null);
            int h = vfx.getHeight(null);
            g2d.drawImage(vfx, (int) (0 - (ratio-1)*w/2), (int) (0 - (ratio-1)*h/2),
                                (int) (ratio*w), (int)(ratio*h),null);
        }

        g2d.dispose();
    }

    public void nextText() {
        if (cursor<backUpText.length()) {
            cursor = backUpText.length();
            textReload();
            return;
        }
        if (mode.equals("B")) {
            this.stageInit();
        }
        else if (!"RIS".contains(mode) && stageNow.hasNext(mode, index)) {
            index += 1;
            cursor = 0;
        } else if (ratio == orgRatio){
            System.out.println(mode+phase);
            shiftMap.get(mode+phase).run();
            if (!successKey) cursor = 0;
        }

    }

    public void prevText() {
        if ((mode+phase).equals("A1") && stageNow.hasPrev(index)) {
            index -= 1;
            cursor = 0;
        }

    }

    public void objText() {
        if ((mode+phase).equals("A1")){
            this.mode = "S";
            aniObj();
        }
    }

    public void stageInit() {
        if (stageI.hasNext()) {
            modeShift("Q", true);
            phase = 0;
            stageNow = stageI.next();
        } else {
            aniSuccess();
            phase = 2;
        }
    }

    public void modeShift(String mode, boolean reset) {
        this.mode = mode;
        if (reset) this.index = 0;
        cursor = 0;
    }

    public void phaseShift(int phase) {
        this.phase = phase;
        if (phase == 1) {
            modeShift("I", true);
        } else if (phase == 2) {
            modeShift("R", false);
        } else if (phase == 3) {
            if (successKey) {
                this.phase = 2;
                tb.bu.gameSet();
            } else if (stageNow.isAnswer(mode, index)) {
                stageInit();
            } else {
                modeShift("W", false);
                this.phase = 1;
            }
        }
    }

    public void textReload() {
        if (mode.equals("B")) {
            backUpText = backGround;
        } else if ("QAHR".contains(mode)){
            backUpText = stageNow.getText(mode, index);
        } else if (mode.equals("I")) {
            backUpText = "——————————————————质询阶段——————————————————";
        } else if (mode.equals("W")) {
            backUpText = stageNow.getText(mode, 0);
        }

        cursor += cursor<backUpText.length() ? 1 : 0;
        textNow = backUpText.substring(0, cursor);
    }

    public void aniObj() {
        ratio = 1.5;
        v = 0;
    }

    public void aniUpdate() {
        textReload();

        if (ratio > orgRatio) {
            v += a;
            ratio -= v;
        } else {
            ratio = orgRatio;
        }
    }

    public void aniSuccess() {
        ratio = 1.5;
        v = 0;
        successKey = true;
    }

}

class Stage {
    Map<String, ArrayList<String>> dialog = new HashMap<>();
    String mode;
    int index;
    int answer;
    int phase;

    public Stage() {}

    public Stage(int answer){
        dialog.put("Q", new ArrayList<>());
        dialog.put("A", new ArrayList<>());
        dialog.put("H", new ArrayList<>());
        dialog.put("R", new ArrayList<>());
        dialog.put("S", new ArrayList<>());
        dialog.put("W", new ArrayList<>());
        mode = "B";
        index = 0;
        this.answer = answer;
        this.phase = 0;
    }

    public String getText(String mode, int index) {
        return dialog.get(mode).get(index);
    }

    public boolean hasNext(String mode, int index) {
        return index < dialog.get(mode).size()-1;
    }

    public boolean hasPrev(int index) {
        return index > 0;
    }

    public boolean isAnswer(String mode, int index) {
        return mode.equals("R") && index == answer;
    }
}

class GameCharacter {
    BufferedImage self_speak, self_think, self_awkward;
    BufferedImage oppo_speak, oppo_surprise, oppo_questioned, oppo_counter;
    BufferedImage bg_left, bg_right;
    HashMap<String, BufferedImage> imageMap = new HashMap<>();

    public GameCharacter(String self, String oppo, String bg, String filePlace) {
        try {
            self_speak = ImageIO.read(new File(filePlace+self+"_speak.png"));
            self_think = ImageIO.read(new File(filePlace+self+"_think.png"));
            self_awkward = ImageIO.read(new File(filePlace+self+"_awkward.png"));

            oppo_speak = ImageIO.read(new File(filePlace+oppo+"_speak.png"));
            oppo_surprise = ImageIO.read(new File(filePlace+oppo+"_surprise.png"));
            oppo_questioned = ImageIO.read(new File(filePlace+oppo+"_questioned.png"));
            oppo_counter = ImageIO.read(new File(filePlace+oppo+"_counter.png"));

            bg_left = ImageIO.read(new File(filePlace+bg+"_left.png"));
            bg_right = ImageIO.read(new File(filePlace+bg+"_right.png"));

            imageMap.put("Q0", self_speak);
            imageMap.put("A0", oppo_speak);
            imageMap.put("A1", oppo_questioned);
            imageMap.put("I1", oppo_questioned);
            imageMap.put("H1", self_think);
            imageMap.put("R2", oppo_counter);
            imageMap.put("R21", oppo_surprise);
            imageMap.put("S1", oppo_surprise);
            imageMap.put("W1", self_awkward);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printCh(Graphics2D g2d, String mode, int phase, boolean ans) {
        if ("BQHW".contains(mode)) g2d.drawImage(bg_left, 0, 0, null);
        else g2d.drawImage(bg_right, 0, 0, null);

        BufferedImage image;

        if (!ans) image = imageMap.getOrDefault(mode+phase, null);
        else image = imageMap.get("R21");

        if (image != null) g2d.drawImage(image, 0, 0, null);
    }


}