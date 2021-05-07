package part3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
        Scene scene;
        boolean stageWriting = false;
        Stage stage = new Stage();
        try {
            Scanner scanner = new Scanner(new File(bu.level));
            String bg = scanner.nextLine().split("\\|")[1]; //加载背景
            String[] nameList = Arrays.copyOfRange(scanner.next().split("\\|"), 1, 4); //加载人名
            String[] materialList = scanner.next().split("\\|");
            ArrayList<Stage> stageList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.startsWith("#")) continue;
                if (line.equals("\n")) continue;
                String[] words = line.split("\\|");
                if (words[0].equals("Stage")) {
                    stageWriting = true;
                    stage = new Stage(Integer.parseInt(words[1]));
                    stageList.add(stage);
                } else if (words[0].equals("END_OF_THE_STAGE")) {
                    stageWriting = false;
                } else if (stageWriting) {
                    stage.dialog.get(words[0]).add(words[1]);
                }
            }
            scene = new Scene(bg, this, stageList, nameList, materialList);
            bu.sceneList.add(scene);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<String> wordsWrapping(String sentence, int width) {
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

    void drawString(Graphics g, String textNow, int x, int y, int w) {
        for (String line : wordsWrapping(textNow, w))
            g.drawString(line, x, y += g.getFontMetrics().getHeight()*1.5);
    }

    public void keyBindingSetUp() {
        //KeyBinding

        keyMap.put('A', bu.sceneNow::prevText);
        keyMap.put('D', bu.sceneNow::nextText);
        keyMap.put('W', bu.sceneNow::objText);
        keyMap.put('P', bu.sceneNow::stageInit);
        keyMap.put('O', bu::gameRestart);

        KeyStroke A_Pressed = KeyStroke.getKeyStroke('A', 0, false);
        KeyStroke D_Pressed = KeyStroke.getKeyStroke('D', 0, false);
        KeyStroke W = KeyStroke.getKeyStroke('W', 0, false);
        KeyStroke P = KeyStroke.getKeyStroke('P', 0, false);
        KeyStroke O = KeyStroke.getKeyStroke('O', 0, false);

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
        AbstractAction objection = new AbstractAction() {
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

        InputMap inputMap = bu.display.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = bu.display.getActionMap();

        inputMap.put(A_Pressed, "left");
        inputMap.put(D_Pressed, "right");
        inputMap.put(W, "objection");
        inputMap.put(P, "finish");
        inputMap.put(O, "restart");

        actionMap.put("left", left);
        actionMap.put("right", right);
        actionMap.put("objection", objection);
        actionMap.put("finish", finish);
        actionMap.put("restart", restart);
    }

    public void keyTransit(char key) {
        Runnable runnable = keyMap.getOrDefault(key, null);
        if (runnable != null) runnable.run();
    }


}
