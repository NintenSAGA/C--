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
    int sIndex;
    int aIndex;
    int indexMax;

    Player player;
    Enemy enemy;
    Selection selection;

    BufferedImage textBlock;
    BufferedImage bg1;

    Scanner scanner;

    ArrayList<String> intro = new ArrayList<>();

    ArrayList<String> selections = new ArrayList<>();
    {
        selections.add("攻击");
        selections.add("回复");
    }

    //动画
    double orgRatio = 0.5;
    int cursor = 0;
    String textBuffered;

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
        this.sIndex = 0;
        this.aIndex = 0;
        this.mode = "O";
        this.selection = new Selection();
        intro.add("一只野生的%s出现了！".formatted(enemy.getName()));
        intro.add("%s该怎么办？".formatted(player.getName()));

        try {
            textBlock = ImageIO.read(new File("material/Part2/Text field.png"));
            bg1 = ImageIO.read(new File("material/Part2/bg1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawTheScene(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.setBackground(Color.lightGray);

        switch (mode) {
            case "I" -> enemy.drawFront(g2d, 0);
            case "SA1", "SA1I" -> player.drawFront(g2d, 0);
            default -> {
                g2d.drawImage(bg1, 0, 0, null);
                player.drawBack(g2d, 1);
                enemy.drawFront(g2d, 1);
            }
        }

        player.drawYourSelf(g2d);
        enemy.drawYourSelf(g2d);

        if (!mode.startsWith("S") || mode.equals("SA2")) {
            printText(g2d);
        }
        if (mode.equals("S")) {
            selection.drawYourself(g2d, sIndex);
        }
        if (mode.startsWith("SA1")){
            player.showYourArts(g2d, aIndex, sIndex);
            g2d.setColor(Color.orange);
            //ToolBox.drawString(g2d, player.artsList.get(index).getDetail(), 200, 150, BuildUp.textWidth);
            if (mode.endsWith("I")) printText(g2d);
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

    public void printText(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.setFont(g2d.getFont().deriveFont(BuildUp.fontSize));
        g2d.drawImage(textBlock, 0, BuildUp.textAreaHeight, null);
        g2d.setColor(Color.white);
        ToolBox.drawString(g2d, textNow, BuildUp.textLeftSide, BuildUp.textUpperSide, BuildUp.textWidth);

        g2d.dispose();
    }

    public void nextText() {
        if (mode.equals("O")) {
            for (String line:intro) textAppend(line);
            modeShift("I");
        }
        if (isTextLoadFinished()) {
            switch (mode) {
                case "I", "M" -> {
                    if (!player.isDefeated() && !enemy.isDefeated()) {
                        modeShift("S");
                        indexMax = selections.size();
                    } else if (player.isDefeated()) {
                        tb.bu.gameFailed();
                    }
                }

                case "S" -> {
                    modeShift("SA1");
                    if (sIndex == 0) {
                        indexMax = player.artsList.size();
                    } else {
                        indexMax = player.recoveryList.size();
                    }
                    aIndex = indexMax - 1;
                }

                case "SA1" -> {
                    if (!player.isDefeated()) player.useArt(aIndex, sIndex);
                    modeShift("SA2");
                }

                case "SA2" -> {
                    if (enemy.isDefeated()) tb.bu.gameSet();
                    else enemy.useArt((int) (Math.random() * enemy.artsList.size()));
                    modeShift("M");
                }

                case "SA1I" -> {
                    modeShift("SA1", false);
                    nextText();
                }
            }
        }
        readNext();
    }

    public void prevText() {
        if (mode.startsWith("SA")) modeShift("S");
    }

    public void up() {
        if (mode.startsWith("SA1")) {
            if (aIndex+1 <= indexMax-1) aIndex += 1;
            else aIndex = 0;
            if (mode.equals("SA1I")) {
                showInfo();
                showInfo();
            }
        }else if (mode.equals("S")){
            sIndex -= Math.min(sIndex, 1);
        }
    }

    public void down() {
        if (mode.startsWith("SA1")) {
            if (aIndex-1 >= 0) aIndex -= 1;
            else aIndex = indexMax-1;
            if (mode.equals("SA1I")) {
                showInfo();
                showInfo();
            }
        }else if (mode.equals("S")){
            sIndex += Math.min(indexMax-sIndex-1, 1);
        }
    }

    public void showInfo() {
        if (mode.equals("SA1")) {
            modeShift("SA1I", false);
            textAppend(player.getSelectedArt(aIndex, sIndex).getDetail());
            readNext();
        } else if (mode.equals("SA1I")) {
            modeShift("SA1");
        }
    }

    public void modeShift(String mode) {
        this.mode = mode;
    }

    public void modeShift(String mode, boolean reset) {
        this.mode = mode;
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