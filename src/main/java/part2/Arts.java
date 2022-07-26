package part2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

class Arts {
    private final String name;
    private final int amountMax;
    private int amount;
    private final double damage;
    private String detail;
    private BufferedImage isSelected, notSelected;
    private int blockLeftX, blockUpperY;
    private int nameLeftX, nameUpperY;
    private int amountLeftX, amountUpperY;

    public Arts(String name, int amount, double damage) {
        this.name = name;
        this.amountMax = amount;
        this.amount = amountMax;
        this.damage = damage;

        try {
            isSelected = ImageIO.read(ToolBox.res("Arts is selected.png"));
            notSelected = ImageIO.read(ToolBox.res("Arts not selected.png"));
            blockLeftX = 830;
            blockUpperY = 631;
            nameLeftX = 910;
            nameUpperY = 655;
            amountLeftX = 1172;
            amountUpperY = 651;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Arts(String name, int amount, double damage, String detail) {
        this(name, amount, damage);
        this.detail = detail;

        String attackInfo = "\n可对敌人造成%.0f点伤害，共可使用%d次。";
        String healInfo = "\n可自我回复%.0f点生命值，共可使用%d次。";
        if (damage >= 0) this.detail += attackInfo.formatted(damage, amountMax);
        else this.detail += healInfo.formatted(-damage, amountMax);
    }

    public Arts(String name, double damage) {
        this(name, -1, damage);
    }

    public void drawYourself(Graphics2D g2d, boolean selected, int offset) {
        g2d = (Graphics2D) g2d.create();

        float fontSize = 22.0F;
        g2d.setFont(g2d.getFont().deriveFont(fontSize));
        int gap = 68;
        g2d.drawImage(selected ? isSelected : notSelected, blockLeftX, blockUpperY - gap *offset, null);
        g2d.setColor(amount > 0 ? Color.black : Color.red);
        ToolBox.drawString(g2d, name, nameLeftX+(selected?39:0), nameUpperY - gap *offset - (!selected?6:0), 100);
        g2d.setColor(Color.white);
        ToolBox.drawString(g2d, "%d/%d".formatted(amount, amountMax), amountLeftX+(selected?39:0), amountUpperY - gap *offset - (!selected?6:0), 100);

        g2d.dispose();
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
        if (amount != -1) amount -= Math.min(1, amount);
    }

    public String getDetail() {
        return detail;
    }
}
