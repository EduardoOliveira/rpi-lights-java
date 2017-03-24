package eu.knoker.rpi.lights;

import io.vertx.ext.web.RoutingContext;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by eduardo on 23/03/2017.
 */
public class ColorHandler {

    private Color current = Color.black;
    private Color target = Color.black;
    private long timerId = 0;
    private List<Color> cycleColors;
    private int index;
    private long sleep = 10;

    public void set(RoutingContext ctx) {
        ctx.response().end();

        String color = ctx.request().getParam("color");

        this.sleep = 10;
        this.cycleColors = new LinkedList<>();
        this.index = 0;
        lightColor(parseColor(color));
    }

    private Color parseColor(String color) {
        switch (color) {
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            case "green":
                return Color.GREEN;
            case "black":
                return Color.BLACK;
            case "white":
                return Color.WHITE;
            case "cyan":
                return Color.CYAN;
            case "pink":
                return Color.PINK;
            case "yellow":
                return Color.YELLOW;
            case "orange":
                return Color.ORANGE;
            case "magenta":
                return Color.MAGENTA;
            case "purple":
                return new Color(255, 0, 255);
            case "random":
                return new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
        }
        return Color.black;
    }

    private void lightColor(Color c) {
        target = c;
        if (timerId != 0) {

            Verticle.vertxInstance.cancelTimer(timerId);
            timerId = 0;
        }
        timerId = Verticle.vertxInstance.setPeriodic(this.sleep, id -> {
            if (current.getRed() < target.getRed()) {
                current = new Color(current.getRed() + 1, current.getGreen(), current.getBlue());
            } else if (current.getRed() > target.getRed()) {
                current = new Color(current.getRed() - 1, current.getGreen(), current.getBlue());
            }
            if (current.getGreen() < target.getGreen()) {
                current = new Color(current.getRed(), current.getGreen() + 1, current.getBlue());
            } else if (current.getGreen() > target.getGreen()) {
                current = new Color(current.getRed(), current.getGreen() - 1, current.getBlue());
            }
            if (current.getBlue() < target.getBlue()) {
                current = new Color(current.getRed(), current.getGreen(), current.getBlue() + 1);
            } else if (current.getBlue() > target.getBlue()) {
                current = new Color(current.getRed(), current.getGreen(), current.getBlue() - 1);
            }

            Verticle.setRed(current.getRed());
            Verticle.setGreen(current.getGreen());
            Verticle.setBlue(current.getBlue());

            if (current.equals(target)) {
                if (!cycleColors.isEmpty()) {
                    index++;
                    if (index >= cycleColors.size()) index = 0;
                    target = cycleColors.get(index);
                } else {
                    Verticle.vertxInstance.cancelTimer(timerId);
                }
            }
        });
    }

    public void cycle(RoutingContext ctx) {
        ctx.response().end();

        String[] colors = ctx.request().getParam("colors").split(",");
        this.cycleColors = new LinkedList<>();
        this.index = 0;
        if (ctx.request().getParam("wait") != null) {
            this.sleep = Long.parseLong(ctx.request().getParam("wait"));
        }

        for (String c : colors) {
            Color colorInstance = parseColor(c);
            if (colorInstance != Color.black) {
                cycleColors.add(colorInstance);
            }
        }
        lightColor(cycleColors.get(0));

    }
}
