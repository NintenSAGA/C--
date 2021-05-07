package part1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

class BackGround {
    BufferedImage bg;
    int leftX, upperY;
    static int w, h;
    int delta = 0;
    double ratio;

    public BackGround(int lx, int hy, double ratio) {
        try {
            bg = ImageIO.read(new File("material/Part1/Background.png"));
            this.leftX = lx;
            this.upperY = hy;
            w = bg.getWidth();
            h = bg.getHeight();
            double zoom = (double) BuildUp.height/h;
            //h *= zoom;
            //w *= zoom;
            h = BuildUp.height;
            w = BuildUp.width;
            this.ratio = ratio;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refresh(int bornX, Obj o) {
        delta = bornX - o.cx;
    }

    public void printBg(Graphics g) {
        g.drawImage(bg, (int) (leftX + delta*ratio), BuildUp.height-h, w, h, null);
    }

    public int centralBgDetect() {
        if (leftX + delta*ratio <= -(double)w*ratio) {
            return -1;
        }
        if (leftX + delta*ratio >= (double)w*ratio) {
            return 1;
        }
        return 0;
    }
}

class SceneBuilder {
    File levelData = new File("material/Part1/Level");
    static int row = 15, col;
    String[][] levelMap = new String[row][];

    public void levelLoader(String level) {

        levelData = new File(level);

        try {
            Scanner scan = new Scanner(levelData);

            for (int i = 0;i < row;i++) {
                String rowS = scan.nextLine();
                levelMap[i] = rowS.split("\t");
                col = levelMap[i].length;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ObjLoader(BuildUp src, String level) {
        this.levelLoader(level);

        ArrayList<Tile> tileList = src.tileList;
        ArrayList<MovingObjects> creatureList = src.creatureList;
        ArrayList<Item> itemList = src.itemList;

        for (int i = 0;i < row;i++) {
            for (int j = 0;j < col;j++) {
                int type = Integer.parseInt(levelMap[i][j]);
                if (type != 0) {
                    if (type >= QuestionBox.codeMin && type <= QuestionBox.codeMax) {
                        QuestionBox tile = new QuestionBox(type, j * Tile.BlockLength, i * Tile.BlockLength, src);
                        tileList.add(tile);
                    }

                    else if (type == Block.code) {
                        Block tile = new Block(type, j * Tile.BlockLength, i * Tile.BlockLength);
                        tileList.add(tile);
                    }

                    else if (type == Enemy.code) {
                        Enemy enemy = new Enemy(j * Tile.BlockLength, i * Tile.BlockLength);
                        creatureList.add(enemy);
                    }

                    else if (type == Item.code) {
                        Item item = new Item(type, j * Tile.BlockLength, i * Tile.BlockLength);
                        itemList.add(item);
                    }

                    else if (type == specialGround.code) {
                        specialGround sG = new specialGround(type, j * Tile.BlockLength, i * Tile.BlockLength, src);
                        tileList.add(sG);
                        src.groundSum++;
                    }

                    else {
                        Tile tile = new Tile(type, j * Tile.BlockLength, i * Tile.BlockLength);
                        tileList.add(tile);
                    }
                }

            }
        }
    }
}



