package part4;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class ToolBox {
    int genCoolDown = (Ball.radius*2)/Ball.vx; //新球生成冷却时间
    int genCount = genCoolDown;
    BuildUp bu;

    public ToolBox(BuildUp bu) {
        this.bu = bu;
    }

    public void garbageLoadIn(String filePlace) {
        try {
            Stream<String> line =
                    new BufferedReader(
                            new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/part4/" + filePlace)),
                                    StandardCharsets.UTF_8)
                    ).lines();
            bu.unShotList = (ArrayList<Bullet>) line
                    .map(t -> t.split("\t"))
                    .map(t -> new Bullet(Integer.parseInt(t[0]), t[1]))
                    .collect(toList());
            bu.unGenBallList = (ArrayList<Ball>) bu.unShotList.parallelStream()
                    .map(t -> t.type)
                    .map(Ball::new)
                    .collect(toList());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void keyBindingSetUp(BuildUp.Display display, Shooter st) {
        //KeyBinding
        KeyStroke A_Pressed = KeyStroke.getKeyStroke('A', 0, false);
        KeyStroke A_Released = KeyStroke.getKeyStroke('A', 0, true);
        KeyStroke D_Pressed = KeyStroke.getKeyStroke('D', 0, false);
        KeyStroke D_Released = KeyStroke.getKeyStroke('D', 0, true);
        KeyStroke W = KeyStroke.getKeyStroke('W', 0, false);
        KeyStroke P = KeyStroke.getKeyStroke('P', 0, false);
        KeyStroke O = KeyStroke.getKeyStroke('O', 0, false);

        AbstractAction left = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                st.speedUp(-1);
            }
        };
        AbstractAction right = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                st.speedUp(1);
            }
        };
        AbstractAction lStop = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                st.slowDown(-1);
            }
        };
        AbstractAction rStop = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                st.slowDown(1);
            }
        };
        AbstractAction shoot = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                st.shoot();
            }
        };
        AbstractAction finish = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bu.unShotList.clear();
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
        inputMap.put(W, "shoot");
        inputMap.put(P, "finish");
        inputMap.put(O, "restart");

        actionMap.put("left", left);
        actionMap.put("lStop", lStop);
        actionMap.put("right", right);
        actionMap.put("rStop", rStop);
        actionMap.put("shoot", shoot);
        //actionMap.put("finish", finish);
        actionMap.put("restart", restart);
    }

    public void ballGenerate() {
        if (genCount <= 0) {
            if (bu.unGenBallList.size() != 0) {
                Ball bl = bu.unGenBallList.get(0);
                bu.unGenBallList.remove(0);
                bu.ballList.add(bl);
            }
            genCount = genCoolDown;
        } else {
            genCount -= 1;
        }
    }

    public int collisionDetect(Bullet a, Ball b) {
        int ra = a.radius;
        int rb = Ball.radius;

        double dSq = Math.pow(a.cx - b.cx, 2) + Math.pow(a.cy - b.cy, 2);
        double d = Math.sqrt(dSq);

        boolean shot = d <= ra + rb;

        return !shot ? 0 : (a.type == b.type ? 1 : -1);
    }

    public boolean outOfRangeDetect(Bullet a) {
        return a.cx < -a.w / 2 || a.cx > BuildUp.width + a.w / 2 ||
                a.cy < -a.h / 2 || a.cy > BuildUp.height + a.h / 2;
    }

    public static URL res(String file) {
        return Objects.requireNonNull(ToolBox.class.getResource("/part4/" +file));
    }
}