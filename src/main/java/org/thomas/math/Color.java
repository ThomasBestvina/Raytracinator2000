package org.thomas.math;

public class Color extends Vector {
    public Color(double r, double g, double b) {
        super(r,g,b, 0);
    }

    public double r() { return this.x;}
    public double g() { return this.y;}
    public double b() { return this.z;}

    public static Color black = new Color(0,0,0);
    public static Color white = new Color(1,1,1);

    /*
    This is the hadamard product, not cross product.
     */
    public Color multiply(Color c) {
        return new Color(this.r() * c.r(), this.g() * c.g(), this.b() * c.b());
    }

    public Color multiply(double s)
    {
        return new Color(this.r()*s, this.g()*s, this.b()*s);
    }

    public Color subtract(Color c) {
        return new Color(this.x - c.x, this.y - c.y, this.z - c.z);
    }

    public Color add(Color c)
    {
        return new Color(c.x + this.x, c.y + this.y, c.z + this.z);
    }
}
