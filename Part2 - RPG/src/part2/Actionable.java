package part2;

import javax.imageio.ImageIO;
import javax.tools.Tool;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Actionable {
    private final String name;
    private final double hpMax;
    private double hp;
    ArrayList<Arts> artsList;
    ArrayList<Arts> recoveryList;
    private double damageGet;
    private Scene scene;
    String attackSuccess = "%s对%s使用了%s，造成了%.1f点伤害！";
    String recovery = "%s使用了%s，回复了%.1f点生命值！";
    String attackFailed = "嘿嘿，%s没打中！";
    String defeatedText;
    Actionable target;
    private boolean defeated;

    BufferedImage block;
    BufferedImage hpBar;
    Image front;
    Image back;
    int blockLeftX, blockUpperY;
    int hpLeftX, hpUpperY;
    int nameLeftX, nameUpperY;
    float fontSize = 20.0F;
    Toolkit tk;

    public Actionable(String name, double hp, ArrayList<Arts> artsList) {
        this.name = name;
        this.hpMax = hp;
        this.hp = hpMax;
        this.artsList = artsList;
        this.defeated = false;
        tk = Toolkit.getDefaultToolkit();
    }

    public void useArt(int code, Actionable target, int mode) {
        Arts art = getArt(code, mode);

        if (art!=null && art.isAvailable()) {
            art.getUsed();
            target.getHurt(art.getDamage());
            scene.textAppend(
                    mode == 0 ?
                    attackSuccess.formatted(name, target.getName(), art.getName(), target.damageGet):
                            recovery.formatted(name, art.getName(), -target.damageGet));
        } else {
            scene.textAppend(attackFailed.formatted(name));
        }
    }

    public void useArt(int code) {
        this.useArt(code, this.target, 0);
    }

    public void useArt(int code, int mode) {
        switch (mode) {
            case 0 -> this.useArt(code, this.target, 0);
            case 1 -> this.useArt(code, this, 1);
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
        ToolBox.drawString(g2d, name, nameLeftX, nameUpperY, BuildUp.textWidth);
    }

    public void drawFront(Graphics2D g2d, int type) {

    }

    public void drawBack(Graphics2D g2d, int type) {

    }

    public double getHp() {
        return hp;
    }

    public double getHpMax() {
        return hpMax;
    }

    public Arts getArt(int code, int mode) {
        return switch(mode) {
            case 0 -> code < artsList.size() ? artsList.get(code) : null;
            case 1 -> code < recoveryList.size() ? recoveryList.get(code) : null;
            default -> null;
        };
    }

    public void getHurt(double damage) {
        this.damageGet = damage > 0 ? Math.min(hp, damage) : -Math.min(hpMax - hp, -damage);
    }

    public void getDefeated() {
        scene.textAppend(defeatedText.formatted(this.getName(), target.getName()));
        defeated = true;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void update() {
        hp -= damageGet;
        damageGet = 0;

        if (hp == 0 && !defeated) {
            getDefeated();
        }
    }

    public Arts getSelectedArt(int index, int mode) {
        return mode == 0 ? artsList.get(index) : recoveryList.get(index);
    }

    public Scene getScene() {
        return scene;
    }
}

class Player extends Actionable{
    int hpValueLeftX, hpValueUpperY;

    public Player(String name, double hp, ArrayList<Arts> artsList) {
        super(name, hp, artsList);
        defeatedText = "%s竟被%s击败了！太菜了吧！！！";
        Collections.shuffle(this.artsList);
        this.recoveryList = (ArrayList<Arts>) this.artsList
                .stream()
                .filter(x -> x.getDamage() < 0)
                .collect(Collectors.toList());
        this.artsList = (ArrayList<Arts>) this.artsList
                .stream()
                .filter(x -> x.getDamage() >= 0)
                .collect(Collectors.toList());

        try {
            block = ImageIO.read(ToolBox.res("Player block.png"));
            hpBar = ImageIO.read(ToolBox.res("Player hp.png"));
            //front = tk.getImage(ToolBox.res("player_front.gif"));
            //back = tk.getImage(ToolBox.res("player_back.gif"));
            back = ImageIO.read(ToolBox.res("player_back.png"));
            front = ImageIO.read(ToolBox.res("player_front.png"));
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

    public void showYourArts(Graphics2D g2d, int index, int mode) {
        ArrayList<Arts> showList = mode == 0 ? artsList : recoveryList;

        for (int i = 0;i < showList.size();i++) {
            showList.get(i).drawYourself(g2d, i == index, i);
        }
    }

    @Override
    public void drawYourSelf(Graphics2D g2d) {
        super.drawYourSelf(g2d);
        g2d.setFont(g2d.getFont().deriveFont(fontSize));
        ToolBox.drawString(g2d, "%.0f/%.0f".formatted(getHp(), getHpMax()), hpValueLeftX, hpValueUpperY, BuildUp.textWidth);
        //g2d.dispose();
    }

    public void drawFront(Graphics2D g2d, int type) {
        g2d.drawImage(front, 289, 182, 210, 276, null);
    }

    public void drawBack(Graphics2D g2d, int type) {
        g2d.drawImage(back, 243, 321, 266, 359 ,null);
    }
}

class Enemy extends Actionable{
    public int code;

    public Enemy(String name, double hp, ArrayList<Arts> artsList, int code) {
        super(name, hp, artsList);
        defeatedText = "%s被%s成功击败了！太牛逼了吧！！！";
        this.code = code;

        try {
            block = ImageIO.read(ToolBox.res("Enemy block.png"));
            hpBar = ImageIO.read(ToolBox.res("Enemy hp.png"));
            front = ImageIO.read(ToolBox.res("enemy"+code+"_front.png"));
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

    @Override
    public void useArt(int code) {
        Arts art = this.getArt(code, 0);
        if ((art.getDamage() >= this.getScene().player.getHp())
        && Math.random() < 0.8) code = 100;
        super.useArt(code);
    }

    public void sceneTie(Scene scene) {
        super.sceneTie(scene);
        this.target = scene.player;
    }

    @Override
    public void drawYourSelf(Graphics2D g2d) {
        super.drawYourSelf(g2d);
    }

    public void drawFront(Graphics2D g2d, int type) {
        switch (type) {
            case 0 -> {
                if(code == 3) g2d.drawImage(front, 131, -200, 1070, 822, null);
                else g2d.drawImage(front, 131, 50, 1070, 822, null);
            }
            case 1 -> g2d.drawImage(front, 756, 119, 304, 234, null);
        }
    }

    public void drawBack(Graphics2D g2d, int type) {

    }


}

