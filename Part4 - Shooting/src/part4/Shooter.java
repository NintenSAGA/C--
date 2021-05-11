package part4;

import javax.imageio.ImageIO;
import java.awt.*;

class Shooter extends Obj {
    double angle = 0;
    double vrMax = 4;
    double ar = 1;
    double vr;
    double rX, rY;
    int direction = 0;
    BuildUp bu;

    public Shooter(int bornX, int bornY, BuildUp bu) {
        try {
            image = ImageIO.read(ToolBox.res("lyf.png"));
            cx = bornX;
            cy = bornY;
            rX = (double) cx;
            rY = (double) cy;
            w = image.getWidth();
            h = image.getHeight();

            double ratio = (double)100/w;
            w *= ratio;
            h *= ratio;

            this.bu = bu;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printThis(Graphics2D g) {
        g.rotate((angle/180)*Math.PI, rX, rY);
        int leftX = cx - w/2;
        int upperY = cy - h/2;
        g.drawImage(image, leftX, upperY, w, h, null);
        g.setColor(Color.MAGENTA);
        g.drawLine(cx, cy, leftX+1000, upperY);
        g.rotate(-(angle/180)*Math.PI, rX, rY);

        if (bu.unShotList.size() != 0) {
            g.setColor(Color.white);
            g.drawString(bu.unShotList.get(0).name, cx-w/2, cy);
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
