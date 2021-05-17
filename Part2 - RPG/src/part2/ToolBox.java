package part2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class ToolBox {
    BuildUp bu;
    HashMap<Character, Runnable> keyMap = new HashMap<>();

    public ToolBox(BuildUp bu) {
        this.bu = bu;
    }

    public void levelLoadIn() {
        String line;
        Player player = null;
        Enemy enemy = null;
        ArrayList<Arts> artsList;
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/" + bu.level)),
                            StandardCharsets.UTF_8)
            );
            String[] info;
            //Load in the player
            do {
                line = br.readLine();
            } while (!line.startsWith("Player|"));
            info = line.split("\\|");
            artsList = new ArrayList<>();
            line = br.readLine();
            do {
                if (line.startsWith("#") || line.startsWith("Arts")) {
                    line = br.readLine();
                    continue;
                }
                String[] data = line.split("\\|");
                artsList.add(new Arts(data[0], Integer.parseInt(data[1]), Double.parseDouble(data[2]), data[3]));
                line = br.readLine();
            } while (!line.equals("END_OF_PLAYER"));
            player = new Player(info[1], Double.parseDouble(info[2]), artsList);

            //Load in the enemy
            do {
                line = br.readLine();
            } while (!line.startsWith("Enemy|"));
            info = line.split("\\|");
            artsList = new ArrayList<>();
            line = br.readLine();
            do {
                if (line.startsWith("#") || line.startsWith("Arts")) {
                    line = br.readLine();
                    continue;
                }
                String[] data = line.split("\\|");
                artsList.add(new Arts(data[0], Integer.parseInt(data[1])));
                line = br.readLine();
            } while (!line.equals("END_OF_ENEMY"));
            enemy = new Enemy(info[1], Double.parseDouble(info[2]), artsList, Integer.parseInt(info[3]));

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
            if (count == 0 && "，。！”？.,…".indexOf(ch) != -1) {
                result.set(result.size()-1, result.get(result.size()-1)+ch);
                continue;
            } else {
                count += 1;
            }
            line.append(ch);
            if (count == width || ch == '\n') {
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
        float fontSize = g.getFont().getSize();
        y += fontSize;
        int dy = (int)(fontSize*1.2);
        for (String line : wordsWrapping(textNow, w)) {
            if(line.contains("\t")) line = line.replace("\t", "        ");
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
        keyMap.put('P', () -> bu.enemy.getDefeated());
        keyMap.put('O', bu::gameRestart);
        keyMap.put('E', bu.scene::showInfo);

        KeyStroke A_Pressed = KeyStroke.getKeyStroke('A', 0, false);
        KeyStroke D_Pressed = KeyStroke.getKeyStroke('D', 0, false);
        KeyStroke W = KeyStroke.getKeyStroke('W', 0, false);
        KeyStroke P = KeyStroke.getKeyStroke('P', 0, false);
        KeyStroke O = KeyStroke.getKeyStroke('O', 0, false);
        KeyStroke S = KeyStroke.getKeyStroke('S', 0, false);
        KeyStroke E = KeyStroke.getKeyStroke('E', 0, false);

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
        AbstractAction info = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('E');
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
        inputMap.put(E, "info");

        actionMap.put("left", left);
        actionMap.put("right", right);
        actionMap.put("up", up);
        actionMap.put("finish", finish);
        actionMap.put("restart", restart);
        actionMap.put("down", down);
        actionMap.put("info", info);
    }

    public void keyTransit(char key) {
        keyMap.get(key).run();
    }

    public static URL res(String file) {
        return Objects.requireNonNull(ToolBox.class.getResource("/"+file));
    }


}
