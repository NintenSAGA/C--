package part4;

import javax.imageio.ImageIO;
import java.awt.*;

class Bullet extends Obj {
    double radiant;     //偏转角
    int vMax = 50;      //最大速度
    int v;              //当前速度
    int radius = 20;    //显示半径

    int type;           //垃圾类型
    String name;        //垃圾名字

    public Bullet(int type, String name) {
        try {
            image = ImageIO.read(ToolBox.res("garbage.png"));

            this.type = type;
            this.name = name;

            w = image.getWidth();
            h = image.getHeight();

            double ratio = (double)radius*2/w;      //等比缩放
            w *= ratio;
            h *= ratio;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shoot(int bornX, int bornY, double angle) {
        cx = bornX;
        cy = bornY;
        this.v = vMax;
        this.radiant = Math.toRadians(angle);
    }

    public void update() {
        cx += v*Math.cos(radiant);
        cy += v*Math.sin(radiant);
    }

    public void printThis(Graphics2D g) {
        super.printThis(g);
        //g.drawString(name, cx-w/2, cy); //显示垃圾名称（临时）
    }
}
