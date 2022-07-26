package part0;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ToolBox {
    GameSelector gs;
    HashMap<Character, Runnable> keyMap = new HashMap<>();

    public ToolBox(GameSelector gs) {
        this.gs = gs;
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
            g.drawString(line, x, y);
            y += dy;
        }
    }

    public void keyBindingSetUp() {
        //KeyBinding

        keyMap.put('A', gs::left);
        keyMap.put('D', gs::right);
        keyMap.put('W', gs::up);
        keyMap.put('S', gs::down);
        keyMap.put('E', gs::confirm);

        KeyStroke A = KeyStroke.getKeyStroke('A', 0, false);
        KeyStroke D = KeyStroke.getKeyStroke('D', 0, false);
        KeyStroke W = KeyStroke.getKeyStroke('W', 0, false);
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
        AbstractAction down = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('S');
            }
        };
        AbstractAction confirm = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTransit('E');
            }
        };

        InputMap inputMap = gs.sp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = gs.sp.getActionMap();

        inputMap.put(A, "left");
        inputMap.put(D, "right");
        inputMap.put(W, "up");
        inputMap.put(S, "down");
        inputMap.put(E, "confirm");

        actionMap.put("left", left);
        actionMap.put("right", right);
        actionMap.put("up", up);
        actionMap.put("down", down);
        actionMap.put("confirm", confirm);
    }

    public void keyTransit(char key) {
        keyMap.get(key).run();
    }

    public static URL res(String file) {
        return Objects.requireNonNull(ToolBox.class.getResource("/part0/" +file));
    }

    public void textLoadIn() {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        this.getClass().getResourceAsStream("/part0/text")),
                        StandardCharsets.UTF_8)
        );

        try {
            String line = "";
            ArrayList<String> text = new ArrayList<>();

            while (!line.startsWith("Preface")){
                line = br.readLine();
            }
            while (!(line = br.readLine()).startsWith("END_OF_THE_PREFACE")){
                text.add(line);
            }
            gs.preface = String.join("\n", text);

            text = new ArrayList<>();
            while (!line.startsWith("Ending")){
                line = br.readLine();
            }
            while (!(line = br.readLine()).startsWith("END_OF_THE_ENDING")){
                text.add(line);
            }
            gs.ending = String.join("\n", text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void infoLoadIn() {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        this.getClass().getResourceAsStream("/part0/Info")),
                        StandardCharsets.UTF_8)
        );

        try {
            String line;
            String[] data;
            int stageCursor = 0;
            String intro = null;
            String req = null;
            String name = null;
            while ((line = br.readLine()) != null) {
                if(line.equals("\n")) continue;
                data = line.split("\\|");
                switch (data[0]) {
                    case "S" -> {
                        stageCursor = Integer.parseInt(data[1])-1;
                        name = data[2];
                    }
                    case "Intro" -> intro = data[1];
                    case "Req" -> req = data[1];
                    case "END" -> gs.stageList.get(stageCursor).infoLoadIn(name, intro, req);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
