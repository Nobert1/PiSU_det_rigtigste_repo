package monopoly.mini.model.properties;

import java.awt.*;

/**
 * @author Gustav Emil Nobert s185031
 */
public enum Colors {
    GREY(142, 142, 147),
    RED(255, 59, 48),
    GREEN(51, 255, 51),
    PURPLE(88, 86, 214),
    LIGHTBLUE(52, 170, 220),
    MAGENTA(255,51,255),
    LIGHTRED(255,102,102),
    WHITE(255,255,255),
    YELLOW(255,255,0),
    DARKGREEN(0, 102, 0),
    SHIPPINGWHITE(224,224,224);

    private final int r;
    private final int g;
    private final int b;


    Colors(int i, int i1, int i2) {
        this.r = i;
        this.g = i1;
        this.b = i2;
    }



    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }


    public int getB() {
        return b;
    }

    public static Color getcolor(Colors colors) {
        Color color = new Color(colors.getR(), colors.getG(), colors.getB());
        return color;
    }

    public static String getcolorName (Color color) {
        int RGB = color.getRGB();

        if (RGB == Colors.getcolor(RED).getRGB())
            return "RED";

        if (RGB == Colors.getcolor(LIGHTBLUE).getRGB())
            return "LIGHTBLUE";

        if (RGB == Colors.getcolor(GREEN).getRGB())
            return "GREEN";

        if (RGB == Colors.getcolor(WHITE).getRGB())
            return "WHITE";

        if (RGB == Colors.getcolor(MAGENTA).getRGB())
            return "MAGENTA";

        if (RGB == Colors.getcolor(YELLOW).getRGB())
            return "YELLOW";

        return "RED";
    }



    public static Color getcolorName (String color) {

        if (color.equals("RED"))
            return Colors.getcolor(RED);

        if (color.equals("LIGHTBLUE"))
            return Colors.getcolor(LIGHTBLUE);

        if (color.equals("GREEN"))
            return Colors.getcolor(GREEN);

        if (color.equals("WHITE"))
            return Colors.getcolor(WHITE);

        if (color.equals("MAGENTA"))
            return Colors.getcolor(MAGENTA);

        if (color.equals("YELLOW"))
            return Colors.getcolor(YELLOW);

        return Colors.getcolor(YELLOW);

    }
    }