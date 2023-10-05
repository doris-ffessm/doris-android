package fr.ffessm.doris.prefetch;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColorTools {

    /**
     * get n colors equidistant on the color wheel
     * baseHue = 0; // base color (0 = red) (value goes from 0 to 360)
     * saturation = 100; // saturation (full saturation = 100) (value goes from 0 to 100)
     * brightness = 50; // luminosity (value goes from 0 to 100)
     *
     */
    public static List<Color> getNbWheelColors(int nbColors, int baseHue, int saturation, int brightness) {
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < nbColors; i++) {
            colors.add(Color.getHSBColor(
                    (baseHue + (i*(360/nbColors)) ) / 360f,
                    saturation / 100f,
                    brightness / 100f));
        }
        return colors;
    }

    /**
     * cf. https://developer.android.com/reference/android/graphics/Color#encoding
     * @param color color to be converted in int suitable for android
     * @return corresponding int for the color
     */
    public static int getColorInt(Color color) {
        return  (color.getAlpha() & 0xff) << 24 | (color.getRed() & 0xff) << 16 | (color.getGreen() & 0xff) << 8 | (color.getBlue() & 0xff);
    }
}
