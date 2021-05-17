package part4;

import javax.imageio.ImageIO;
import java.awt.*;

class Shooter extends Obj {
    double angle = -90;
    double vrMax = 4;
    double ar = 1;
    double vr;
    double rX, rY;
    int direction = 0;
    BuildUp bu;

    public Shooter(int bornX, int bornY, BuildUp bu) {
        try {
            image = ImageIO.read(ToolBox.res("shooter.png"));
            w = image.getWidth();
            h = image.getHeight();
            cx = 590+50;
            cy = 470+50;
            rX = (double) cx;
            rY = (double) cy;

            double ratio = (double)100/w;
            w *= ratio;
            h *= ratio;

            this.bu = bu;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printThis(Graphics2D g2d) {

        Graphics2D g = (Graphics2D) g2d.create();
        g.rotate((angle/180)*Math.PI, rX, rY);
        int leftX = 590;
        int upperY = 470;
        g.setColor(Color.decode("#88AEFF"));
        g.drawLine(cx, cy, cx+1000, cy);
        g.drawImage(image, leftX, upperY, w, h, null);
        g.dispose();

        if (bu.unShotList.size() != 0) {
            g = (Graphics2D) g2d.create();
            g.setColor(Color.white);
            g.drawString(bu.unShotList.get(0).name,
                    640-((float)bu.unShotList.get(0).name.length()/2)*BuildUp.fontSize,
                    591+BuildUp.fontSize);
            g.dispose();
        }
    }

    public void speedUp(int direction) {
        this.direction = direction;
    }

    public void slowDown(int direction) {
        if (direction == this.direction) {
            this.direction = 0;
        }
    }

    public void update() {
        if (direction == 0) {
            vr = 0;
        } else {
            if (vr < vrMax) vr += ar;
            else vr = vrMax;
        }

        angle += direction*vr;
        //System.out.println(direction+" "+vr);
    }

    public void shoot() {
        if (bu.unShotList.size()!=0 && bu.btList.isEmpty()){
            Bullet bt = bu.unShotList.get(0);
            bu.unShotList.remove(0);
            bt.shoot(cx, cy, angle);
            bu.btList.add(bt);
        }
    }
}
