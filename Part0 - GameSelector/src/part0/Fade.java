package part0;

import java.awt.*;

public class Fade {
    private float opacity;
    private final float maxOpacity = 1.0F;
    private float oV;
    private boolean fadeIn;
    private boolean fadeOut;
    private Runnable outNext, inNext;
    private Color fadeColor;

    public Fade() {
        fadeReset();
    }

    public void fadeReset() {
        fadeIn = false;
        fadeOut = false;
        oV = 0;
    }

    public void fadeInSetUp(Color color) {
        opacity = maxOpacity;
        fadeIn = true;
        this.fadeColor = color;
    }

    public void fadeInSetUp(Color color, Runnable inNext) {
        this.fadeInSetUp(color);
        this.inNext = inNext;
    }

    public void fadeOutSetUp(Color color, Runnable outNext) {
        opacity = 0;
        fadeOut = true;
        this.fadeColor = color;
        this.outNext = outNext;
    }

    public void drawYourSelf(Graphics2D g2d) {
        if (fadeIn || fadeOut) {
            g2d = (Graphics2D) g2d.create();
            AlphaComposite composite = (AlphaComposite) g2d.getComposite();
            g2d.setComposite(composite.derive(Math.max(0F, Math.min(1.0F, opacity))));
            g2d.setColor(fadeColor);
            g2d.fillRect(0, 0, 1280, 720);
            g2d.dispose();
        }
    }

    public void fadeUpdate(float oA) {
        if (fadeIn) {
            oV += oA;
            opacity -= oV;
            if (opacity <= 0) {
                if (inNext != null) {
                    inNext.run();
                    inNext = null;
                }
                fadeReset();
            }
        } else if (fadeOut) {
            oV += oA;
            opacity += oV;
            if (opacity >= maxOpacity) {
                fadeReset();
                outNext.run();
            }
        }
    }

    public void fadeUpdate() {
        fadeUpdate(0.001F);
    }


    public boolean isFinished() {
        return !(fadeIn|fadeOut);
    }
}
