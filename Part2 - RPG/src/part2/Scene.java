package part2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class Scene {
    String textNow;
    ArrayList<String> textBufferedList;

    ToolBox tb;

    String mode;
    int index;
    int indexMax;
    int phase;
    float nameHeight;

    Player player;
    Enemy enemy;

    BufferedImage textBlock;

    Scanner scanner;

    ArrayList<String> intro = new ArrayList<>();
    {
        intro.add("亲爱的宝贝，我是您爹。");
        intro.add("您是真的牛逼，我是真的佩服您。");
        intro.add("wdnmd，能说点人听的话吗？");
    }
    String saPrompt = "你他妈要干嘛？";

    ArrayList<String> selections = new ArrayList<>();
    {
        selections.add("攻击");
        selections.add("回复");
    }

    //动画
    double orgRatio = 0.5;
    double ratio = orgRatio;
    double a = 0.1;
    double v = 0;
    int cursor = 0;
    String textBuffered;
    boolean successKey;
    boolean exitKey;

    static HashMap<String, Color> colorMap = new HashMap<>();
    static {

    }
    static HashMap<String, Runnable> shiftMap = new HashMap<>();
    {
        shiftMap.put("O", () -> modeShift("I"));
        shiftMap.put("I", () -> modeShift("S"));
        shiftMap.put("S", () -> select());
        shiftMap.put("SI", () -> System.out.println("您写错代码了"));
        shiftMap.put("SA", () -> modeShift("M"));
        shiftMap.put("M", () -> triumphJudgement());
        shiftMap.put("T", () -> tb.bu.gameSet());
        shiftMap.put("F", () -> tb.bu.gameFailed());
    }

    public Scene(ToolBox tb, Enemy enemy, Player player) {
        this.enemy = enemy;
        this.player = player;
        enemy.sceneTie(this);
        player.sceneTie(this);
        textBufferedList = new ArrayList<>();
        scanner = new Scanner(System.in);
        textBuffered = "";
        textNow = textBuffered;
        this.tb = tb;
        this.index = 0;
        this.mode = "O";

        try {
            textBlock = ImageIO.read(new File("material/Part2/Text field.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawTheScene(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        player.drawYourSelf(g2d);
        enemy.drawYourSelf(g2d);

        if (!mode.startsWith("S") || mode.equals("SA2")) {
            printText(g2d);
        }
        if (mode.equals("SA1")){
            int dy = 0;
            for (Arts art : player.artsList) {
                if (player.artsList.indexOf(art) == index) g2d.setColor(Color.magenta);
                else g2d.setColor(Color.black);
                g2d.drawString("%d|%s|%d\n".formatted(player.artsList.indexOf(art), art.getName(), art.getAmount()), 50, 150 + dy);
                dy += g2d.getFontMetrics().getHeight() * 1.5;
            }
            g2d.setColor(Color.orange);
            ToolBox.drawString(g2d, player.artsList.get(index).getDetail(), 200, 150, BuildUp.textWidth);
        }

        g2d.dispose();
    }

    public void textAppend(String newText) {
        textBufferedList.add(newText);
    }

    public boolean isTextLoadFinished() {
        return textBufferedList.isEmpty();
    }

    public void update() {
        textReload();

    }

    public void printBG(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.dispose();
    }

    public void printText(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.setFont(g2d.getFont().deriveFont(BuildUp.fontSize));
        g2d.drawImage(textBlock, 0, BuildUp.textAreaHeight, null);
        g2d.setColor(Color.white);
        ToolBox.drawString(g2d, textNow, BuildUp.textLeftSide, (int) (BuildUp.textUpperSide+BuildUp.fontSize), BuildUp.textWidth);

        g2d.dispose();
    }

    public void nextText() {
        if (mode.equals("O")) {
            for (String line:intro) textAppend(line);
            modeShift("I");
        }
        if (isTextLoadFinished()) {
            if (mode.equals("I") || mode.equals("M")) {
                modeShift("S");
                indexMax = selections.size();
                textAppend(saPrompt);
            } else if (mode.equals("S")){
                if (index == 0) {
                    modeShift("SA1");
                    indexMax = player.artsList.size();
                    textAppend(saPrompt);
                } else {
                    textAppend("我还没写这个代码，真是对不住您了");
                }
            } else if (mode.equals("SA1")) {
                player.useArt(index);
                modeShift("SA2");
            } else if (mode.equals("SA2")) {
                enemy.useArt((int)(Math.random()*enemy.artsList.size()));
                modeShift("M");
            }
        }
        readNext();
    }

    public void prevText() {

    }

    public void up() {
        if (mode.startsWith("S") && !mode.equals("SA2")){
            index -= Math.min(index, 1);
        }
    }

    public void down() {
        if (mode.startsWith("S") && !mode.equals("SA2")){
            index += Math.min(indexMax-index-1, 1);
        }
    }

    public void stageInit() {

    }

    public void modeShift(String mode) {
        this.mode = mode;
        index = 0;
    }

    public void select() {

    }

    public void triumphJudgement() {
        if (enemy.isDefeated()) {
            modeShift("T");
        } else if (player.isDefeated()) {
            modeShift("F");
        } else {
            modeShift("S");
        }
    }

    public void phaseShift(int phase) {

    }

    public void textReload() {

        cursor += cursor< textBuffered.length() ? 1 : 0;
        textNow = textBuffered.substring(0, cursor);
    }

    public void readNext() {
        if (textBufferedList.size() == 0) return;
        textBuffered = textBufferedList.get(0);
        textBufferedList.remove(0);
        cursor = 0;
    }

}