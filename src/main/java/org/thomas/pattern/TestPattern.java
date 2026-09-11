package org.thomas.pattern;

import org.thomas.math.Color;
import org.thomas.math.Vector;

public class TestPattern extends Pattern {

    public TestPattern() {
        super(null, null);
    }

    @Override
    public Color colorAt(Vector point) {
        return new Color(point.x, point.y, point.z);
    }
}