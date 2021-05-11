package part4;

import java.awt.*;
import java.awt.image.BufferedImage;

abstract class Obj {
    int w, h;       //宽、高
    int cx, cy;     //几何中心坐标
    boolean appear = true;  //是否渲染

    BufferedImage image;

    public void printThis(Graphics2D g) {
        if (appear) {
            int leftX = cx - w/2;
            int upperY = cy - h/2;
            g.drawImage(image, leftX, upperY, w, h, null);
        }
    }

    public void update() {

    }
}


