package part2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class Actionable {
    private final String name;
    private double hpMax;
    private double hp;
    ArrayList<Arts> artsList;
    private double damageGet;
    private Scene scene;
    String attackSuccess = "%s对%s使用了%s，造成了%.2f点伤害！";
    String attackFailed = "嘿嘿，%s没打中！";
    String defeatedText = "%s被%s成功击败了！太牛逼了吧！！！";
    Actionable target;
    private boolean defeated;

    BufferedImage block;
    BufferedImage hpBar;
    int blockLeftX, blockUpperY;
    int hpLeftX, hpUpperY;
    int nameLeftX, nameUpperY;
    float fontSize = 20.0F;

    public Actionable(String name, double hp, ArrayList<Arts> artsList) {
        this.name = name;
        this.hpMax = hp;
        this.hp = hpMax;
        this.artsList = artsList;
        this.defeated = false;
    }

    public void useArt(int code) {
        Arts art = getArt(code);

        if (art.isAvailable()) {
            art.getUsed();
            target.getHurt(art.getDamage());
            scene.textAppend(attackSuccess.formatted(name, target.getName(), art.getName(), art.getDamage()));
        } else {
            scene.textAppend(attackFailed.formatted(name));
        }
    }

    public void sceneTie(Scene scene) {
        this.scene = scene;
    }

    public String getName() {
        return name;
    }

    public void drawYourSelf(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.drawImage(block, blockLeftX, blockUpperY, null);
        g2d.drawImage(hpBar, hpLeftX, hpUpperY, (int)(hpBar.getWidth()*(hp/hpMax)), hpBar.getHeight(), null);

        g2d.setFont(g2d.getFont().deriveFont(fontSize));
        g2d.drawString(name, nameLeftX, nameUpperY+fontSize);
    }

    public double getHp() {
        return hp;
    }

    public double getHpMax() {
        return hpMax;
    }

    public Arts getArt(int code) {
        return code < artsList.size() ? artsList.get(code) : null;
    }

    public void getHurt(double damage) {
        this.damageGet = damage;
    }

    public void getDefeated() {
        scene.textAppend(defeatedText.formatted(this.getName(), target.getName()));
        defeated = true;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void update() {
        hp -= Math.min(hp, damageGet);
        damageGet = 0;

        if (hp == 0) {
            getDefeated();
        }
    }
}

class Player extends Actionable{
    int hpValueLeftX, hpValueUpperY;

    public Player(String name, double hp, ArrayList<Arts> artsList) {
        super(name, hp, artsList);

        try {
            block = ImageIO.read(new File("material/Part2/Player block.png"));
            hpBar = ImageIO.read(new File("material/Part2/Player hp.png"));
            blockLeftX = 0;
            blockUpperY = 500;
            hpLeftX = 14;
            hpUpperY = 537;
            nameLeftX = 13;
            nameUpperY = 510;
            hpValueLeftX = 13;
            hpValueUpperY = 551;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sceneTie(Scene scene) {
        super.sceneTie(scene);
        this.target = scene.enemy;
    }

    @Override
    public void drawYourSelf(Graphics2D g2d) {
        super.drawYourSelf(g2d);
        g2d.drawString("%.1f/%.1f".formatted(getHp(), getHpMax()), hpValueLeftX, hpValueUpperY+fontSize);
        //g2d.dispose();
    }
}

class Enemy extends Actionable{
    public Enemy(String name, double hp, ArrayList<Arts> artsList) {
        super(name, hp, artsList);

        try {
            block = ImageIO.read(new File("material/Part2/Enemy block.png"));
            hpBar = ImageIO.read(new File("material/Part2/Enemy hp.png"));
            blockLeftX = 939;
            blockUpperY = 25;
            hpLeftX = 980;
            hpUpperY = 66;
            nameLeftX = 979;
            nameUpperY = 35;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sceneTie(Scene scene) {
        super.sceneTie(scene);
        this.target = scene.player;
    }

    @Override
    public void drawYourSelf(Graphics2D g2d) {
        super.drawYourSelf(g2d);
        //g2d.dispose();
    }
}

