package org.thomas.material;

import org.thomas.math.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    private final BufferedImage image;

    public Texture(String path) throws IOException {
        image = ImageIO.read(new File(path));
    }

    private int getWidth() {
        return image.getWidth();
    }
    private int getHeight() {
        return image.getHeight();
    }

    public Color sample(double u, double v) {
        u = u - Math.floor(u);
        v = v - Math.floor(v);

        double px = u * (getWidth() - 1);
        double py = (1.0 - v) * (getHeight() - 1); // note to future self, java.io stores images with (0,0) as top left.

        int x0 = (int)Math.floor(px);
        int y0 = (int)Math.floor(py);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        double fx = px - x0;
        double fy = py - y0;

        x0 = clamp(x0, 0, getWidth() - 1);
        x1 = clamp(x1, 0, getWidth() - 1);
        y0 = clamp(y0, 0, getHeight() - 1);
        y1 = clamp(y1, 0, getHeight() - 1);

        Color c00 = getPixelColor(x0, y0);
        Color c10 = getPixelColor(x1, y0);
        Color c01 = getPixelColor(x0, y1);
        Color c11 = getPixelColor(x1, y1);

        double r = bilinearInterpolate(c00.r(), c10.r(), c01.r(), c11.r(), fx, fy);
        double g = bilinearInterpolate(c00.g(), c10.g(), c01.g(), c11.g(), fx, fy);
        double b = bilinearInterpolate(c00.b(), c10.b(), c01.b(), c11.b(), fx, fy);

        return new Color(r, g, b);
    }

    private Color getPixelColor(int x, int y) {
        int rgb = image.getRGB(x, y);
        double r = ((rgb >> 16) & 0xFF) / 255.0;
        double g = ((rgb >> 8) & 0xFF) / 255.0;
        double b = (rgb & 0xFF) / 255.0;
        return new Color(r, g, b);
    }

    private double bilinearInterpolate(double c00, double c10, double c01, double c11, double fx, double fy) {
        double top = lerp(c00, c10, fx);
        double bottom = lerp(c01, c11, fx);
        return lerp(top, bottom, fy);
    }

    private double lerp(double a, double b, double t) {
        return a * (1.0 - t) + b * t;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
