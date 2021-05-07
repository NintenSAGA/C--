package part4;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Ball extends Obj {
    static int radius = 40;         //显示半径
    static int vx = 1;              //水平运动速度
    int vr = 180*vx/(int)(Math.PI*radius);      //旋转角速度
    double angle;
    double deltaAngle = 0;
    double radiant;

    int rotateX, rotateY;

    int initY = 50, initX = 200;                //初始生成点
    int lineWidth = BuildUp.width - 2*initX;    //运动终点
    int distance = radius*2;                    //行距
    int rho = distance/2;                       //旋转半径

    int direction = 1;                          //运动方向 1右 -1左

    boolean rotate = false;                     //是否在旋转

    int type;                                   //垃圾桶类型

    static String[] typeS = {"可回收","其他","厨余","有害"}; //垃圾桶分类

    File imageFIle = new File("material/Part4/lyf.png");

    public Ball(int type) {
        try {
            image = ImageIO.read(imageFIle);
            cx = initX;
            cy = initY;

            w = image.getWidth();
            h = image.getHeight();

            double ratio = (double)radius*2/w;  //等比缩放
            w *= ratio;
            h *= ratio;

            this.type = type;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update() {
        if (!rotate) {  //非旋转状态时水平移动
            if ((direction==1&&cx<initX+lineWidth) || (direction==-1&&cx>initX)){
                cx += vx*direction;     //按方向水平移动
            } else {
                rotate = true;          //进入旋转状态
                direction *= -1;        //反向行进
                angle = 90;             //初始角度为90度
                rotateX = cx;           //旋转中心横坐标
                rotateY = cy+radius;    //旋转中心纵坐标
            }
        }

        if (rotate) {
            if (deltaAngle < 180) {
                deltaAngle += vr;       //旋转角度变化量
                radiant = Math.toRadians(angle+deltaAngle*direction);   //转化为弧度制
                cx = rotateX + (int)(Math.cos(radiant)*rho);            //计算渲染坐标
                cy = rotateY - (int)(Math.sin(radiant)*rho);
            } else {
                deltaAngle = 0;         //180度时完成旋转 回到初始状态
                rotate = false;
            }
        }
    }



    public void printThis(Graphics2D g) {
        //g.rotate((part4.Shooter.angle/180)*Math.PI, cx, cy);
        super.printThis(g);
        g.setColor(Color.white);
        g.drawString(typeS[type-1], cx-w/2, cy);    //显示垃圾桶类型（临时）
        //g.rotate(-(part4.Shooter.angle/180)*Math.PI, cx, cy);
    }

}
