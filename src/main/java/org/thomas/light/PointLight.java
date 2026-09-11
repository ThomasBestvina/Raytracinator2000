package org.thomas.light;

import org.thomas.math.Color;
import org.thomas.math.Vector;

import java.util.List;

public class PointLight implements Light {
    private final Vector position;
    private final Color color;

    public PointLight(Vector position, Color Intensity) {
        this.position = position;
        this.color = Intensity;
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public List<Vector> samplePoints() {
        return List.of(position);
    }

    @Override
    public Color getIntensity() {
        return color;
    }
}