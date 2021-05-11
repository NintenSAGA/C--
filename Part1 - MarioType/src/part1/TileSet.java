package part1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class Tile extends Obj {
    static BufferedImage image = null;
    static int tileGap = 1;
    static int BlockLength = 40;
    static int row = 8, col = 16;

    int flashCD = 10;
    int flashCount;
    int frNum = 0;

    BufferedImage cropImage;
    int type;
    int deltaX, deltaY;

    boolean bottomSpecial;
    boolean itemSpecial;
    boolean groundSpecial;

    public Tile(int type, int leftX, int upperY) {
        try {
            if (image == null) {
                image = ImageIO.read(ToolBox.res("Tileset.png"));
            }

            cropImage = cropImageLoader(type);

            this.w = 40;
            this.h = 40;
            this.cx = leftX+w/2;
            this.cy = BuildUp.offSetY + upperY + h/2;
            this.type = type;

            this.shape = new Rectangle(cx - w/2, cy - h/2, w, h);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static BufferedImage cropImageLoader(int type) {
        int sinW = 16;
        int sinH = 16;

        int typeX = tileGap+(sinW+tileGap)*(type%col - 1);
        int typeY = tileGap+(sinH+tileGap)*(type/col);

        return image.getSubimage(typeX, typeY, sinW, sinH);
    }

    public void printTile(Graphics g) {
        int lx = cx - w / 2;
        int hy = cy - h / 2;

        g.drawImage(cropImage, lx + deltaX, hy + deltaY, w, h, null);

        //g.setColor(Color.MAGENTA);
        //g.drawRect(lx, hy, w, h);
    }

    public void refresh(Human hm) {
        if (BuildUp.levelScroll){
            deltaX = hm.printX - hm.cx;
        }
        if (BuildUp.verticalScroll) {
            deltaY = hm.printY - hm.cy;
        }
    }

    public void specialAction() {

    }

    public void putOnItem(Item item) {

    }
}

class BottomSpecialTile extends Tile {
    static int vyInitial = 6;
    static int ay = 2;
    int vy = 0;
    boolean activated = false;
    boolean movingKey = false;

    public BottomSpecialTile(int type, int leftX, int upperY) {
        super(type, leftX, upperY);
        bottomSpecial = true;
    }

    public void refresh(Human hm) {
        super.refresh(hm);
        if (movingKey && vy > -vyInitial) {
            this.cy -= vy;
            this.shape.translate(0, -vy);
            vy -= ay;
        } else if (vy <= -vyInitial) {
            this.cy -= vy;
            this.shape.translate(0, -vy);
            vy = 0;
            movingKey = false;
        }
    }

    public void hitFromTheBottom() {
        vy = vyInitial;
        movingKey = true;
    }
}

class QuestionBox extends BottomSpecialTile {
    ArrayList<BufferedImage> frames = new ArrayList<>();

    static int code = 1;
    static int codeMin = 1;
    static int codeMax = 4;

    BuildUp src;

    public QuestionBox(int type, int leftX, int upperY, BuildUp src) {
        super(code, leftX, upperY);
        frames.add(cropImageLoader(code));
        frames.add(cropImageLoader(code+1));
        frames.add(cropImageLoader(code+2));
        frames.add(cropImageLoader(code+3));
        this.type = type;
        this.src = src;
    }

    public void coolDown() {
        flashCount++;
        if (flashCount >= flashCD) {
            this.imageRefresh();   //加载下一个动作
            flashCount = 0;      //计时器归零
        }
    }

    public void imageRefresh() {
        if (frNum < frames.size()-1) frNum++;
        else frNum = 0;

        cropImage = frames.get(frNum);
    }

    @Override
    public void refresh(Human hm) {
        super.refresh(hm);
        if (!activated) this.coolDown();
    }

    @Override
    public void specialAction() {
        super.specialAction();
        if (type == 2) {
            Item item = new Item(Item.code, cx-w/2, cy-h/2-h*3);
            src.itemList.add(item);
        } else if (type == 3) {
            Enemy enemy = new Enemy(cx-w/2, cy-h/2-h*2);
            //Human enemy = new Human(cx-w/2, cy-h/2-h*2);
            src.creatureList.add(enemy);
        }
        cropImage = cropImageLoader(code+4);
        activated = true;
        this.hitFromTheBottom();
        bottomSpecial = false;

    }
}

class Block extends BottomSpecialTile {
    static int code = 6;
    ArrayList<BufferedImage> frames = new ArrayList<>();

    public Block(int type, int leftX, int upperY) {
        super(type, leftX, upperY);
        bottomSpecial = true;
        frames.add(cropImageLoader(this.type));
        frames.add(cropImageLoader(this.type+1));
        frames.add(cropImageLoader(this.type+2));
        this.flashCD = 5;
    }

    public void coolDown() {
        flashCount++;
        if (flashCount >= flashCD) {
            this.imageRefresh();   //加载下一个动作
            flashCount = 0;      //计时器归零
        }
    }

    public void imageRefresh() {
        if (frNum < frames.size()-1) frNum++;
        else appear = false;

        cropImage = frames.get(frNum);
    }

    @Override
    public void refresh(Human hm) {
        super.refresh(hm);
        if (activated && appear) this.coolDown();
    }

    @Override
    public void specialAction() {
        super.specialAction();
        this.activated = true;
        this.hitFromTheBottom();
    }
}

class Item extends Tile {
    static int code = 34;

    public Item(int type, int leftX, int upperY) {
        super(code, leftX, upperY);
        itemSpecial = true;
    }

    public void specialAction(Human hm) {
        hm.getItem(this);
        this.appear = false;
    }
}

class specialGround extends Tile {
    static int code = 66;
    boolean activated = false;
    Item itemOn;
    BuildUp src;

    public specialGround(int type, int leftX, int upperY, BuildUp src) {
        super(type, leftX, upperY);
        groundSpecial = true;
        this.src = src;
    }

    @Override
    public void printTile(Graphics g) {
        super.printTile(g);

        if (activated) {
            int lx = cx - w / 2;
            int hy = cy - h / 2;

            g.drawImage(itemOn.cropImage, lx + deltaX, hy + deltaY-h, w, h, null);
        }
    }

    @Override
    public void putOnItem(Item item) {
        groundSpecial = false;
        activated = true;
        itemOn = item;
        src.groundSum--;
    }


}