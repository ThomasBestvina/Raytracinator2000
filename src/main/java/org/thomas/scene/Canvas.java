package org.thomas.scene;

import org.thomas.math.Color;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Canvas {
    public Color[][] canvas;
    public Canvas(int width, int height)
    {
        Color black = new Color(0, 0, 0);
        canvas = new Color[width][height];
        for(Color[] row : canvas)
        {
            Arrays.fill(row, black);
        }
    }

    public int getHeight()
    {
        return canvas[0].length;
    }

    public int getWidth()
    {
        return canvas.length;
    }

    public void setColor(int x, int y, Color color)
    {
        canvas[x][y] = color;
    }

    public Color getColor(int x, int y)
    {
        return canvas[x][y];
    }

    public String getPPMHeader()
    {
        return "P3\n" + getWidth() + " " + getHeight()+"\n255";
    }


    public String getPPMFooter()
    {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                sb.append(clampColorRange((int) Math.ceil(canvas[x][y].r() * 255)));
                sb.append(' ');
                sb.append(clampColorRange((int) Math.ceil(canvas[x][y].g() * 255)));
                sb.append(' ');
                sb.append(clampColorRange((int) Math.ceil(canvas[x][y].b() * 255)));
                if (x < getWidth() - 1) sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void writeToFile(String path) throws IOException {
        String ppm = getPPMHeader() + "\n" + getPPMFooter();
        Path filePath = Path.of(path);
        Files.createDirectories(filePath.getParent());
        try {
            Files.writeString(filePath, ppm);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private int clampColorRange(int i)
    {
        return Math.clamp(i, 0, 255);
    }
}