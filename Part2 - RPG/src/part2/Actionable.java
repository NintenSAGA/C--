package part2;

import java.util.ArrayList;

public class Actionable {
    private final String name;
    private double hp;
    ArrayList<Arts> artsList;
    private double damageGet;
    private Scene scene;
    String attackSuccess = "%s对%s使用了%s，造成了%.2f点伤害！";
    String attackFailed = "嘿嘿，没打中！";
    String defeated = "%s被%s成功击败了！太牛逼了吧！！！";
    Actionable target;

    public Actionable(String name, double hp, ArrayList<Arts> artsList) {
        this.name = name;
        this.hp = hp;
        this.artsList = artsList;
    }

    public void useArt(int code) {
        Arts art = getArt(code);

        if (art.isAvailable()) {
            art.getUsed();
            target.getHurt(art.getDamage());
            scene.textAppend(attackSuccess.formatted(name, target.getName(), art.getName(), art.getDamage()));
        } else {
            scene.textAppend(attackFailed);
        }
    }

    public void sceneTie(Scene scene) {
        this.scene = scene;
    }

    public String getName() {
        return name;
    }

    public double getHp() {
        return hp;
    }

    public Arts getArt(int code) {
        return code < artsList.size() ? artsList.get(code) : null;
    }

    public void getHurt(double damage) {
        this.damageGet = damage;
    }


    public void getDefeated() {
        scene.textAppend(defeated.formatted(this.getName(), target.getName()));
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
    public Player(String name, double hp, ArrayList<Arts> artsList) {
        super(name, hp, artsList);
    }

    public void sceneTie(Scene scene) {
        super.sceneTie(scene);
        this.target = scene.enemy;
    }
}

class Enemy extends Actionable{
    public Enemy(String name, double hp, ArrayList<Arts> artsList) {
        super(name, hp, artsList);
    }

    public void sceneTie(Scene scene) {
        super.sceneTie(scene);
        this.target = scene.player;
    }
}

class Arts {
    private final String name;
    private int amount;
    private final double damage;

    public Arts(String name, int amount, double damage) {
        this.name = name;
        this.amount = amount;
        this.damage = damage;
    }

    public Arts(String name, double damage) {
        this(name, -1, damage);
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public double getDamage() {
        return damage;
    }

    public boolean isAvailable() {
        return amount == -1 || amount > 0;
    }

    public void getUsed() {
        amount -= Math.min(1, amount);
    }
}

