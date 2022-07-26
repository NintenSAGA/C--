package part1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

class ToolBox {

    int leftA, rightA, leftB, rightB;
    int upperA, lowerA, upperB, lowerB;

    //水平碰撞检测
    public boolean levelBondDetect(MovingObjects a, Obj b) {
        boolean detect = false;

        if (!b.appear) return false;

        levelLoadIn(a, b);

        if (levelCollisionJudgement(a, b)){ //当前位置加上速度offset，检测碰撞
            levelRigidLaw(a);
            detect = true;
        }
        return detect;
    }

    public boolean verticalBondDetect(MovingObjects a, Tile b) {

        if (!b.appear) return false;

        boolean supported = false;

        verticalLoadIn(a, b);

        if (verticalUpperCollisionJudgement(a, b)) {
            supported = true;
            if (b.groundSpecial) {
                a.specialSupportTile = b;
            }
            if (a.vy != 0 && (lowerA-upperB) <= b.shape.getHeight()) {
                a.vy =  lowerA - upperB;
            }
        }

        if (verticalLowerCollisionJudgement(a, b)) {
            supported = true;
            if (b.bottomSpecial) {
                b.specialAction();
                if (a.vy != 0) {
                    a.vy = BottomSpecialTile.vyInitial;
                }
            }
            else if (a.vy != 0) {
                a.vy =  upperA - lowerB;
            }

        }

        return supported;
    }

    public boolean enemyCollisionDetect(Human a, MovingObjects b) {
        if (b.defeated) return false;

        verticalLoadIn(a, b);
        levelLoadIn(a, b);

        if (verticalUpperCollisionJudgement(a, b)) {

            b.getDefeated();
            a.vy = lowerA-(upperB+b.w/2);
            a.bounceKey = true;
            return true;
        }

        if (levelCollisionJudgement(a, b)){ //当前位置加上速度offset，检测碰撞
            levelRigidLaw(a);
            //a.speedUp(b.direction);
            //a.defeated();
        }

        return false;
    }

    public boolean itemGetDetect(Human a, Item b) {
        if (!b.appear || a.itemHoldKey) return false;

        verticalLoadIn(a, b);
        levelLoadIn(a, b);

        if (verticalUpperCollisionJudgement(a, b) || levelCollisionJudgement(a, b)) {
            b.specialAction(a);
            return true;
        }

        return false;
    }

    public void levelRigidLaw(MovingObjects a) {
        if (a.vx > 0 && rightA <= leftB)        a.vx = leftB-rightA;     //在人物右侧

        if (a.vx < 0 && leftA >= rightB)        a.vx = rightB-leftA;     //在人物左侧

        if (leftA < leftB && rightA > leftB)    a.vx = leftB-rightA;

        if (rightA > rightB && leftA < rightB)  a.vx = rightB-leftA;
    }

    public void levelLoadIn(MovingObjects a, Obj b) {
        leftA = (int)a.shape.getX();
        rightA = (int)(a.shape.getX() + a.shape.getWidth());

        leftB = (int)b.shape.getX();
        rightB = (int)(b.shape.getX()+b.shape.width);
    }

    public boolean levelCollisionJudgement(MovingObjects a, Obj b) {
        return b.shape.intersects(new Rectangle(
                (int)a.shape.getX()+a.vx, (int)a.shape.getY(),
                (int)a.shape.getWidth(), (int)a.shape.getHeight()));
    }

    public void verticalLoadIn(MovingObjects a, Obj b) {
        lowerA = (int) (a.shape.getY() + a.shape.getHeight());  //人物下端
        upperA = (int) a.shape.getY();
        upperB = (int) (b.shape.getY());
        lowerB = (int) (b.shape.getY()+b.shape.getHeight());
    }

    public boolean verticalUpperCollisionJudgement(MovingObjects a, Obj b) {
        return b.shape.intersects(new Rectangle(
                (int)a.shape.getX(), (int)a.shape.getY()+1-a.vy,    //+1防止下沉
                (int)a.shape.getWidth(), (int)a.shape.getHeight()))
                && lowerA-a.vy >= upperB && lowerA <= upperB;
    }

    public boolean verticalLowerCollisionJudgement(MovingObjects a, Obj b) {
        return b.shape.intersects(new Rectangle(           //检测下面的碰撞
                (int)a.shape.getX(), (int)a.shape.getY()-a.vy,    //+1防止下沉
                (int)a.shape.getWidth(), (int)a.shape.getHeight()))
                && upperA-a.vy <= lowerB && upperA >= lowerB;
    }

    public void keyBindingSetUp(BuildUp bu, BuildUp.Display display, Human hm) {
        //KeyBinding
        KeyStroke A_Pressed = KeyStroke.getKeyStroke('A', 0, false);
        KeyStroke A_Released = KeyStroke.getKeyStroke('A', 0, true);
        KeyStroke D_Pressed = KeyStroke.getKeyStroke('D', 0, false);
        KeyStroke D_Released = KeyStroke.getKeyStroke('D', 0, true);
        KeyStroke W = KeyStroke.getKeyStroke('W', 0, false);
        KeyStroke S = KeyStroke.getKeyStroke('S', 0, false);
        KeyStroke P = KeyStroke.getKeyStroke('P', 0, false);
        KeyStroke O = KeyStroke.getKeyStroke('O', 0, false);

        AbstractAction left = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hm.speedUp(-1);
            }
        };
        AbstractAction right = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hm.speedUp(1);
            }
        };
        AbstractAction lStop = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hm.slowDown(-1);
            }
        };
        AbstractAction rStop = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hm.slowDown(1);
            }
        };
        AbstractAction jump = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hm.jump();
            }
        };
        AbstractAction useItem = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hm.useItem();
            }
        };
        AbstractAction finish = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bu.groundSum = 0;
            }
        };
        AbstractAction restart = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bu.gameRestart();
            }
        };

        InputMap inputMap = display.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = display.getActionMap();

        inputMap.put(A_Pressed, "left");
        inputMap.put(A_Released, "lStop");
        inputMap.put(D_Pressed, "right");
        inputMap.put(D_Released, "rStop");
        inputMap.put(W, "jump");
        inputMap.put(S, "useItem");
        inputMap.put(P, "finish");
        inputMap.put(O, "restart");

        actionMap.put("left", left);
        actionMap.put("lStop", lStop);
        actionMap.put("right", right);
        actionMap.put("rStop", rStop);
        actionMap.put("jump", jump);
        actionMap.put("useItem", useItem);
        //actionMap.put("finish", finish);
        actionMap.put("restart", restart);
    }

    public void mapScroller(ArrayList<BackGround> bgs) {
        if (bgs.get(1).centralBgDetect() != 0) {    //加载下一张背景
            if (bgs.get(1).centralBgDetect() == -1) {
                bgs.remove(0);
                bgs.add(new BackGround((bgs.get(1).leftX + BackGround.w), 0, bgs.get(1).ratio));
                System.out.print("yeah");
            }
            else {
                bgs.remove(2);
                bgs.add(0, new BackGround((bgs.get(0).leftX - BackGround.w), 0, bgs.get(1).ratio));
            }
        }
    }

    public static URL res(String file) {
        return Objects.requireNonNull(ToolBox.class.getResource("/part1/" +file));
    }
}