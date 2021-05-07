package part2;

import java.util.ArrayList;

public class Actionable {
    String name;
    double hp;
    ArrayList<Arts> artsList;

    public Actionable(String name, double hp, ArrayList<Arts> artsList) {
        this.name = name;
        this.hp = hp;
        this.artsList = artsList;
    }

    public void useArt(Actionable rec, int code) {
        Arts art = getArt(code);

        if (art.isAvailable()) {
            art.getUsed();
            rec.getHurt(art.getDamage());
        } else {

        }
    }

    public Arts getArt(int code) {
        return code < artsList.size() ? artsList.get(code) : null;
    }

    public void getHurt(double damage) {
        hp -= Math.min(damage, hp);
        if (hp == 0) getDefeated();
    }


    public void getDefeated() {

    }
}

class Player extends Actionable{
    public Player(String name, double hp, ArrayList<Arts> artsList) {
        super(name, hp, artsList);
    }
}

class Enemy extends Actionable{
    public Enemy(String name, double hp, ArrayList<Arts> artsList) {
        super(name, hp, artsList);
    }
}

class Arts {
    String name;
    int amount;
    double damage;

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

