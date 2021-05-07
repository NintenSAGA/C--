package part2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class Scene {
    String textNow;
    ArrayList<String> textBufferedList;
    Iterator<String> iText;

    ToolBox tb;

    String mode;
    int index;
    int phase;
    float nameHeight;

    Player player;
    Enemy enemy;

    Scanner scanner;

    //动画
    double orgRatio = 0.5;
    double ratio = orgRatio;
    double a = 0.1;
    double v = 0;
    int cursor = 0;
    String TextBuffered;
    boolean successKey;
    boolean exitKey;

    static HashMap<String, Color> colorMap = new HashMap<>();
    static {

    }
    static HashMap<String, Runnable> shiftMap = new HashMap<>();
    {

    }

    public Scene(Enemy enemy, Player player) {
        this.enemy = enemy;
        this.player = player;
        enemy.sceneTie(this);
        player.sceneTie(this);
        textBufferedList = new ArrayList<>();
        iText = textBufferedList.iterator();
        scanner = new Scanner(System.in);

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawTheScene(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.dispose();
    }

    public void textAppend(String newText) {
        textBufferedList.add(newText);
    }

    public boolean isTextLoadFinished() {
        return textBufferedList.isEmpty();
    }

    public void update() {

        if (isTextLoadFinished()) {
            System.out.println(player.getName()+"\t"+player.getHp());
            System.out.println(enemy.getName()+"\t"+enemy.getHp());
            for (Arts art:player.artsList) {
                System.out.printf("%d|%s|%d\n", player.artsList.indexOf(art), art.getName(), art.getAmount());
            }
            player.useArt(scanner.nextInt());
        } else {
            System.out.println(textBufferedList.get(0));
            textBufferedList.remove(0);
        }
    }

    public void printBG(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.dispose();
    }

    public void printText(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.dispose();
    }

    public void nextText() {

    }

    public void prevText() {

    }

    public void objText() {

    }

    public void stageInit() {

    }

    public void modeShift(String mode, boolean reset) {

    }

    public void phaseShift(int phase) {

    }

    public void textReload() {

        cursor += cursor< TextBuffered.length() ? 1 : 0;
        textNow = TextBuffered.substring(0, cursor);
    }

    public void aniObj() {

    }

    public void aniUpdate() {

    }

    public void aniSuccess() {

    }

}