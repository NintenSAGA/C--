package part1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

class MovingObjects extends Obj {
    BufferedImage ch;
    ArrayList<BufferedImage> frames = new ArrayList<>();
    int frNum = 0;          //动作帧
    int actionFrNum;        //动作帧总数
    int pw;                 //用来设置人物水平朝向
    int vx = 0;              //水平运动初始速度
    int direction = 0;      //水平运动方向

    int ay = 1;             //竖直加速度
    int vyInitial = 20;
    int vy = 0;             //竖直速度
    boolean jumpKey = false;    //跳跃状态
    boolean supportKey = false; //检测支撑体
    boolean levelKey = false;   //用于控制敌人碰壁掉头
    boolean defeated = false;   //是否被击败
    Tile specialSupportTile = null; //是否在特别方块上方

    int movementCD = 5;         //运动帧渲染间隔
    int movementCount = movementCD;

    int deltaX, deltaY;         //渲染坐标相对变化量

    ToolBox tb = new ToolBox(); //工具箱

    String imagePlace;

    public void update(ArrayList<Tile> objList) {
        supportKey = false;
        levelKey = false;
        for (Tile o:objList) {
            supportKey = (supportKey || tb.verticalBondDetect(this, o));  //有支撑时为True
            levelKey = (levelKey || tb.levelBondDetect(this, o));       //碰壁时为True
        }

        cx += vx;
        cy -= vy;
        shape.setLocation(cx-w/2, cy-h/2);

        if (!supportKey) {
            jumpKey = true;     //悬空时进入跳跃模式
            if (vy > -vyInitial) {
                vy -= ay;
            }
        }
        else {
            vy = 0;         //触地静止
            jumpKey = false;
        }

        this.bridge();
    }

    public void update(Human hm, ArrayList<Tile> objList) {
        if (BuildUp.levelScroll){
            deltaX = hm.printX - hm.cx;
        }
        if (BuildUp.verticalScroll) {
            deltaY = hm.printY - hm.cy;
        }

        if (!defeated) {
            this.update(objList);
        }
    }

    public void bridge() {
        movementCount++;
        if (movementCount >= movementCD) {
            this.refresh();   //加载下一个动作
            movementCount = 0;      //计时器归零
        }
    }

    public void refresh() {
        if (frNum < actionFrNum-1) frNum++;
        else frNum = 0;

        ch = frames.get(frNum);
    }

    public void printCh(Graphics g) {
        int lx = cx - w / 2;
        int hy = cy - h / 2;

        if (pw > 0) g.drawImage(ch, lx + deltaX, hy + deltaY, pw, h, null);   //朝向右侧
        else g.drawImage(ch, lx + deltaX+w, hy + deltaY, pw, h, null);    //朝向左侧
    }

    public void getDefeated(){}
}

class Human extends MovingObjects{
    int ax = 5;              //水平运动加速度
    int axBrake = -2;
    int printX, printY;             //人物固定在画面的位置 background_x
    int vxMax = 10;          //水平运动最大速度
    int BornX;
    int bottomY;

    boolean brake = false;
    boolean bounceKey = false;
    boolean itemHoldKey = false;
    Item itemHold;

    int movementCD = 5;
    int movementCount = movementCD;

    ToolBox tb = new ToolBox(); //工具箱

    String imagePlace = "material/Part1/Mario-0";
    String imagePlaceHold = "material/Part1/MarioHold-0";

    ArrayList<BufferedImage> framesHold = new ArrayList<>();
    ArrayList<BufferedImage> framesIdle = new ArrayList<>();

    BufferedImage up, down;

    public Human(int BornX, int bottomY) {

        ay = 1;             //竖直加速度
        vyInitial = 20;
        vy = 0;             //竖直速度
        actionFrNum = 3;

        try {

            for (int i = 1;i <= 5;i++) {
                File imageFile = new File(imagePlace+i+".png");
                BufferedImage imageNow = ImageIO.read(imageFile);
                framesIdle.add(imageNow);
                imageFile = new File(imagePlaceHold+i+".png");
                imageNow = ImageIO.read(imageFile);
                framesHold.add(imageNow);
            }
            frames = framesIdle;

            ch = frames.get(0);
            w = ch.getWidth();
            h = ch.getHeight();
            //加载图片，获取设置宽高

            double zoom = (double) 60/h; //图像缩放
            w *= zoom;
            h *= zoom;

            this.BornX = BornX;
            this.bottomY = bottomY;
            cx = BornX;  //几何中心 x
            cy = bottomY - h/2;  //几何中心 y
            printX = cx;        //取初始几何中心为画面固定位置中心
            printY = 400;
            pw = -w;         //初始朝向为正

            int wTemp = w;
            this.shape = new Rectangle(cx-wTemp/2, bottomY-h, wTemp, h);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void printCh (Graphics g) {
        int leftX;      //取最左x
        if (BuildUp.levelScroll)  leftX = printX - w/2;
        else  leftX = cx - w/2;

        int upperY;
        if (BuildUp.verticalScroll) upperY = printY - h/2; //取最上y
        else upperY = cy - h/2;

        if (direction != 0) pw = w*(-direction);   //无方向输入时不改变朝向

        if (itemHoldKey) {
            if (pw > 0) g.drawImage(itemHold.cropImage, leftX-w, upperY + h/5, itemHold.w, itemHold.h, null);
            else g.drawImage(itemHold.cropImage, leftX+(2*w-itemHold.w), upperY + h/5, itemHold.w, itemHold.h, null);
        }

        if (pw > 0) g.drawImage(ch, leftX, upperY, pw, h, null);   //朝向右侧
        else g.drawImage(ch, leftX+w, upperY, pw, h, null);    //朝向左侧

        if (defeated) {

        }
    }

    public void speedUp(int direction) {
        this.direction = direction;
        vx = 0;
        brake = false;

    }

    public void slowDown(int direction) {
        if (direction == this.direction) {
            //this.direction = 0;
            brake = true;
        }
    }

    public void jump() {
        if (!jumpKey) {
            jumpKey = true;
            vy = vyInitial;    //初始跳跃速度
        }
    }


    public void update(ArrayList<Tile> objList, ArrayList<MovingObjects> creatureList, ArrayList<Item> itemList) {

        if (cy-w/2 > Tile.BlockLength*(SceneBuilder.row+3)) {
            this.defeated();
        }

        if (brake) {
            if (vx*direction <= 0) {
                brake = false;
                vx = 0;
                direction = 0;
            } else {
                vx += direction * axBrake;
            }
        } else if (direction != 0) {
            if (Math.abs(vx) < vxMax) {
                vx += direction * ax;
            }
            else if (vx *direction < 0) {
                vx += direction * ax;
            }
        }

        if (vx*direction <= 0) { vx = 0; direction = 0;}

        if (bounceKey) {
            vy = vyInitial;
            bounceKey = false;
        }

        boolean hit = false;
        for (MovingObjects c:creatureList) {
            hit = (hit|tb.enemyCollisionDetect(this, c));
            if (hit) break;
        }

        boolean get = false;
        for (Item i:itemList) {
            get = (get|tb.itemGetDetect(this, i));
            if (get) break;
        }

        specialSupportTile = null;

        super.update(objList);
    }


    public void refresh() {
        if (jumpKey) {
            if (vy >= 0) {
                ch = frames.get(3);
            } else {
                ch = frames.get(4);
            }
        } else {
            if (vx != 0) {   //只有运动时才切换
                super.refresh();
            }
            else {
                ch = frames.get(0);
                frNum = 0;
            } //静止回归默认帧
        }
    }

    public void defeated() {
        vy = 30;
        defeated = true;
    }

    public void getItem(Item itemHold) {
        if (!itemHoldKey) {
            itemHoldKey = true;
            this.itemHold = itemHold;
            frames = framesHold;
        }
    }

    public void useItem() {
        if (itemHoldKey && (specialSupportTile != null)) {
            specialSupportTile.putOnItem(itemHold);
            itemHoldKey = false;
            frames = framesIdle;
            itemHold = null;
        }
    }

}


//敌人
class Enemy extends MovingObjects{
    String imagePlace = "material/Part1/Enemy.png";
    int tileGap = 1;
    static int code =  130;
    int vxInitial = 2;

    public Enemy (int leftX, int upperY) {
        try {
            actionFrNum = 2;

            BufferedImage image = ImageIO.read(new File(imagePlace));
            h = image.getHeight();
            w = h;
            direction = 1;
            vx = vxInitial*direction;

            for (int i = 0;i < 2;i++) {
                BufferedImage cropImage = image.getSubimage((w+tileGap)*i, 0, w, h);
                frames.add(cropImage);
            }

            ch = frames.get(0);

            double zoom = (double) 40/h; //图像缩放
            w *= zoom;
            h *= zoom;

            cx = leftX + w/2;  //几何中心 x
            cy = upperY + h/2;  //几何中心 y
            pw = -w;         //初始朝向为正

            int wTemp = w;
            this.shape = new Rectangle(cx-wTemp/2, upperY, wTemp, h);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void update(Human hm, ArrayList<Tile> objList) {
        if (appear) {
            super.update(hm, objList);
        }
    }

    @Override
    public void bridge() {
        if (levelKey) {
            direction = -direction;
            vx = vxInitial*direction;
            pw *= -1;
        }
        super.bridge();
    }

    @Override
    public void printCh(Graphics g) {
        if (appear) {
            super.printCh(g);
        }
        if (defeated) {
            if (movementCount == 0) appear = false;
            else movementCount--;
        }
    }

    public void getDefeated() {
        defeated = true;
        movementCount = movementCD;
        cy = cy+h/4;
        h /= 2;
    }
}