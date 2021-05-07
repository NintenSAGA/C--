package part2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

class Arts {
    private final String name;
    private int amount;
    private final double damage;
    private String detail;
    private BufferedImage isSelected, notSelected;

    public Arts(String name, int amount, double damage) {
        this.name = name;
        this.amount = amount;
        this.damage = damage;

        try {
            isSelected = ImageIO.read(new File("material/Part2/Arts is selected.png"));
            notSelected = ImageIO.read(new File("material/Part2/Arts not selected.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Arts(String name, int amount, double damage, String detail) {
        this(name, amount, damage);
        this.detail = detail;
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

    public String getDetail() {
        return detail;
    }
}
