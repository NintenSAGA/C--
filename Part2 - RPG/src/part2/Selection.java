package part2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Selection {
    private BufferedImage fightSelected, fightUnselected;
    private BufferedImage healSelected, healUnselected;
    private int fightLeftX, fightUpperY;
    private int healLeftX, healUpperY;

    public Selection() {
        try {
            fightSelected = ImageIO.read(new File("material/Part2/Fight Selected.png"));
            fightUnselected = ImageIO.read(new File("material/Part2/Fight Unselected.png"));
            healSelected = ImageIO.read(new File("material/Part2/Heal Selected.png"));
            healUnselected = ImageIO.read(new File("material/Part2/Heal Unselected.png"));
            fightLeftX = 991;
            fightUpperY = 512;
            healLeftX = 991;
            healUpperY = 583;
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void drawYourself(Graphics2D g2d, int index) {
        g2d.drawImage(index == 0 ? fightSelected:fightUnselected, fightLeftX, fightUpperY, null);
        g2d.drawImage(index == 1 ? healSelected:healUnselected, healLeftX, healUpperY, null);
    }
}
