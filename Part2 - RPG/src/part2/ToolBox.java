package part2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ToolBox {
    BuildUp bu;
    HashMap<Character, Runnable> keyMap = new HashMap<>();

    public ToolBox(BuildUp bu) {
        this.bu = bu;
    }

    public void levelLoadIn() {
        String line;
        String name = null;
        Player player = null;
        Enemy enemy = null;
        double hp = 0;
        ArrayList<Arts> artsList;
        try {
            Scanner scanner = new Scanner(new File(bu.level));
            String[] info;
            //Load in the player
            do {
                line = scanner.nextLine();
            } while (!line.startsWith("Player|"));
            info = line.split("\\|");
            artsList = new ArrayList<>();
            line = scanner.nextLine();
            do {
                if (line.startsWith("#") || line.startsWith("Arts")) {
                    line = scanner.nextLine();
                    continue;
                }
                String[] data = line.split("\\|");
                artsList.add(new Arts(data[0], Integer.parseInt(data[1]), Double.parseDouble(data[2]), data[3]));
                line = scanner.nextLine();
            } while (!line.equals("END_OF_PLAYER"));
            player = new Player(info[1], Double.parseDouble(info[2]), artsList);

            //Load in the enemy
            do {
                line = scanner.nextLine();
            } while (!line.startsWith("Enemy|"));
            info = line.split("\\|");
            artsList = new ArrayList<>();
            line = scanner.nextLine();
            do {
                if (line.startsWith("#") || line.startsWith("Arts")) {
                    line = scanner.nextLine();
                    continue;
                }
                String[] data = line.split("\\|");
                artsList.add(new Arts(data[0], Integer.parseInt(data[1])));
                line = scanner.nextLine();
            } while (!line.equals("END_OF_ENEMY"));
            enemy = new Enemy(info[1], Double.parseDouble(info[2]), artsList);

            bu.player = player;
            bu.enemy = enemy;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList<String> wordsWrapping(String sentence, int width) {
        StringBuilder line = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();
        int count = 0;
        for (char ch:sentence.toCharArray()) {
            if (count == 0 && "，。！？".indexOf(ch) != -1) {
                result.set(result.size()-1, result.get(result.size()-1)+ch);
                continue;
            } else {
                count += 1;
            }
            line.append(ch);
            if (count == width) {
                count = 0;
                result.add(line.toString());
                line = new StringBuilder();
            }
        }
        result.add(line.toString());

        return result;
    }

    public static void drawString(Graphics g, String textNow, int x, int y, int w) {
        if(textNow.length() == 0) return;
        int dy = (int)(g.getFontMetrics().getHeight()*1.2);
        for (String line : wordsWrapping(textNow, w)) {
            g.drawString(line, x, y);
            y += dy;
        }
    }

    public void keyBindingSetUp() {
        //KeyBinding

        keyMap.put('A', bu.scene::prevText);
        keyMap.put('D', bu.scene::nextText);
        keyMap.put('W', bu.scene::up);
        keyMap.put('S', bu.scene::down);
        keyMap.put('P', bu.scene::stageInit);
        keyMap.put('O', bu::gameRestart);

        KeyStroke A_Pressed = KeyStroke.getKeyStroke('A', 0, false);
        KeyStroke D_Pressed = KeyStroke.getKeyStroke('D', 0, false);
        KeyStroke W = KeyStroke.getKeyStroke('W', 0, false);
        KeyStroke P = KeyStroke.getKeyStroke('P', 0, false);
        KeyStroke O = KeyStroke.getKeyStroke('O', 0, false);
        KeyStroke S = KeyStroke.getKeyStroke('S', 0, false);

        AbstractAction left = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('A');
            }
        };
        AbstractAction right = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('D');
            }
        };
        AbstractAction up = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('W');
            }
        };
        AbstractAction finish = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('P');
            }
        };
        AbstractAction restart = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('O');
            }
        };
        AbstractAction down = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('S');
            }
        };

        InputMap inputMap = bu.display.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = bu.display.getActionMap();

        inputMap.put(A_Pressed, "left");
        inputMap.put(D_Pressed, "right");
        inputMap.put(W, "up");
        inputMap.put(P, "finish");
        inputMap.put(O, "restart");
        inputMap.put(S, "down");

        actionMap.put("left", left);
        actionMap.put("right", right);
        actionMap.put("up", up);
        actionMap.put("finish", finish);
        actionMap.put("restart", restart);
        actionMap.put("down", down);
    }

    public void keyTransit(char key) {
        keyMap.get(key).run();
    }


}
