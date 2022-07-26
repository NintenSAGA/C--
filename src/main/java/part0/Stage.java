package part0;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Stage {
    private BufferedImage selected;
    private BufferedImage cleared;
    private BufferedImage unSelected;
    private boolean isCleared;
    boolean isAvailable;
    private final int serialNum;
    private int sLX = 0;
    private int sUY = 0;
    private int cLX = 0;
    private int cUY = 0;
    private String intro;
    private String req;
    private String name;

    public Stage(int serialNum) {
        this.serialNum = serialNum;
        if (this.serialNum != 5) isAvailable = true;

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
            unSelected = ImageIO.read(ToolBox.res("UnSelected.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void infoLoadIn(String name, String intro, String req) {
        this.intro = "介绍：" + intro;
        this.req = "要求：" + req;
        this.name = name;
    }

    public void showInfo(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.white);
        ToolBox.drawString(g2d, intro+"\n\n"+req, 111, 225, 35);
        g2d.dispose();
    }

    public void setCleared() {
        isCleared = true;
        isAvailable = false;
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void setAvailable() {
        isAvailable = true;
    }

    public void drawYourself(int selectedNum, Graphics2D g2d) {
        if (selectedNum == serialNum) drawSelected(g2d);
        //else drawUnSelected(g2d);
        if (isCleared) drawCleared(g2d);
    }

    public void drawSelected(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();
        g2d.drawImage(selected, sLX, sUY, null);
        g2d.dispose();
    }

    public void drawUnSelected(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();
        g2d.drawImage(unSelected, sLX, sUY, null);
        g2d.dispose();
    }

    public void drawCleared(Graphics2D g2d) {
        g2d = (Graphics2D) g2d.create();

        g2d.drawImage(cleared, cLX, cUY, null);

        g2d.dispose();
    }
}

class finalStage extends Stage {
    BufferedImage unavailable, available;

    public finalStage() {
        super(5);
        isAvailable = false;
        try {
            available = ImageIO.read(ToolBox.res("center1.png"));
            unavailable = ImageIO.read(ToolBox.res("center0.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void drawYourself(int selectedNum, Graphics2D g2d) {
        g2d.drawImage(isAvailable ? available:unavailable, 0, 0, null);
        super.drawYourself(selectedNum, g2d);
    }
}
