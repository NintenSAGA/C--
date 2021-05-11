package part0;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Stage {
    private BufferedImage selected;
    private BufferedImage cleared;
    private boolean isCleared;
    private int serialNum;
    private int sLX = 0;
    private int sUY = 0;
    private int cLX = 0;
    private int cUY = 0;

    public Stage(int serialNum) {
        this.serialNum = serialNum;

        int XOffset = 254;
        int YOffset = 209;


        switch (serialNum) {
            case 1 -> {
                sLX -= XOffset;
                cLX -= XOffset;
            }
            case 2 -> {
                sUY -= YOffset;
                cUY -= YOffset;
            }
            case 3 -> {
                sLX += XOffset;
                cLX += XOffset;
            }
            case 4 -> {
                sUY += YOffset;
                cUY += YOffset;
            }

        }

        try {
            selected = ImageIO.read(ToolBox.res("Selected.png"));
            cleared = ImageIO.read(ToolBox.res("Clear.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setCleared() {
        isCleared = true;
    }

    public void drawYourself(int selectedNum, Graphics2D g2d) {
        if (selectedNum == serialNum) drawSelected(g2d);
        if (isCleared) drawCleared(g2d);
    }

    public void drawSelected(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.drawImage(selected, sLX, sUY, null);

        g2d.dispose();
    }

    public void drawCleared(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.drawImage(cleared, cLX, cUY, null);

        g2d.dispose();
    }


}
