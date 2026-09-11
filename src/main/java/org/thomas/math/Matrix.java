package org.thomas.math;

public class Matrix {
    protected double[][] data;

    public Matrix(int rows, int cols) {
        data = new double[rows][cols];
    }
    public Matrix(double[][] data) {
        this.data = data;
    }
    public double get(int row, int col) {
        return data[row][col];
    }
    public void get(int row, int col, double value) {
        data[row][col] = value;
    }
    public int width() {
        return data[0].length;
    }
    public int height() {
        return data.length;
    }


    public static Matrix identityMatrix()
    {
        return new Matrix(new double[][]{
                {1,0,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {0,0,0,1}
        });
    }

    public static Matrix translation(double x, double y, double z)
    {
        return new Matrix(new double[][]{
                {1,0,0,x},
                {0,1,0,y},
                {0,0,1,z},
                {0,0,0,1}
        });
    }

    public static Matrix scalar(double x, double y, double z)
    {
        return new Matrix(new double[][]{
                {x, 0, 0, 0},
                {0, y, 0, 0},
                {0, 0, z, 0},
                {0, 0, 0, 1}
        });
    }

    public static Matrix rotationX(double R)
    {
        return new Matrix(new double[][]{
                {1, 0,           0,            0},
                {0, Math.cos(R), -Math.sin(R), 0},
                {0, Math.sin(R), Math.cos(R),  0},
                {0, 0,           0,            1}
        });
    }

    public static Matrix rotationY(double R)
    {
        return new Matrix(new double[][]{
                {Math.cos(R),  0, Math.sin(R), 0},
                {0,            1, 0,           0},
                {-Math.sin(R), 0, Math.cos(R), 0},
                {0,            0, 0,           1}
        });
    }

    public static Matrix rotationZ(double R)
    {
        return new Matrix(new double[][]{
                {Math.cos(R), -Math.sin(R), 0, 0},
                {Math.sin(R), Math.cos(R),  0, 0},
                {0,           0,            1, 0},
                {0,           0,            0, 1}
        });
    }

    public static Matrix shear(double x_y, double x_z, double y_x, double y_z, double z_x, double z_y)
    {
        return new Matrix(new double[][]{
                {1,   x_y, x_z, 0},
                {y_x, 1,   y_z, 0},
                {z_x, z_y, 1,   0},
                {0,   0,   0,   1}
        });
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true; // if we already know they are the same object, no point in iterating over the entire matrix.
        if(!(obj instanceof Matrix))
            return false;
        Matrix m = (Matrix)obj;
        if(width() != m.width() || height() != m.height())
            return false;
        for(int i = 0; i < height(); i++) {
            for(int j = 0; j < width(); j++) {
                if(!DoubleMath.equal(get(i,j), m.get(i,j)))
                    return false;
            }
        }
        return true;
    }

    /**
     * Multiplies two matricies
     * @param m
     * @return multiplied m, null if cannot be multiplied
     */
    public Matrix multiply(Matrix m)
    {
        if(this.width() != m.height())
            return null;
        Matrix result = new Matrix(this.height(), m.width());
        for(int i = 0; i < this.height(); i++) {
            for(int j = 0; j < m.width(); j++) {
                double sum = 0;
                for(int k = 0; k < this.width(); k++) {
                    sum += get(i, k) * m.get(k, j);
                }
                result.get(i, j, sum);
            }
        }
        return result;
    }

    /*
    Assumes v has equal length to matrix width. Treats v as vertical matrix.
     */
    public Vector multiply(Vector v)
    {
        double[][] col;
        switch(width()) {
            case 2 -> col = new double[][]{{v.x}, {v.y}};
            case 3 -> col = new double[][]{{v.x}, {v.y}, {v.z}};
            case 4 -> col = new double[][]{{v.x}, {v.y}, {v.z}, {v.w}};
            default -> { return null; }
        }
        Matrix result = multiply(new Matrix(col));
        if(result == null) return null;
        return new Vector(
                result.get(0, 0),
                result.get(1, 0),
                result.get(2, 0),
                width() == 4 ? result.get(3, 0) : 0
        );
    }

    public Matrix transpose(){
        Matrix result = new Matrix(width(), height());
        for(int row = 0; row < height(); row++) {
            for(int col = 0; col < width(); col++) {
                result.get(col,row,data[row][col]);
            }
        }
        return result;
    }

    public double determinant()
    {
        return determinant(this);
    }

    private static double determinant(Matrix m)
    {
        if(m.width() == 2 && m.height() == 2) return m.data[0][0]*m.data[1][1]-m.data[0][1]*m.data[1][0];
        double sum = 0;
        for(int row = 0; row < m.height(); row++) {
            sum += m.data[row][0] * m.cofactor(row, 0);
        }
        return sum;
    }

    /*
    Assumes the matrix is a square.
     */
    public Matrix submatrix(int rowDeleted, int columnDeleted)
    {
        Matrix result = new Matrix(height()-1, width()-1);
        int destRow = 0;
        for (int row = 0; row < height(); row++) {
            if (row == rowDeleted) continue;
            int destCol = 0;
            for (int col = 0; col < width(); col++) {
                if (col == columnDeleted) continue;
                result.get(destRow, destCol, data[row][col]);
                destCol++;
            }
            destRow++;
        }
        return result;
    }

    public double minor(int mRow, int mCol)
    {
        return submatrix(mRow, mCol).determinant();
    }

    public double cofactor(int coRow, int coCol)
    {
        return ((coRow + coCol) % 2 == 0 ? 1 : -1) * minor(coRow, coCol);
    }

    public boolean isInvertible()
    {
        return this.determinant() != 0;
    }

    public Matrix inverse()
    {
        double Determinant = determinant(this);
        Matrix result = new Matrix(width(), height());

        for(int row = 0; row < height(); row++) {
            for(int col = 0; col < width(); col++) {
                result.get(col, row, cofactor(row, col)/Determinant);
            }
        }

        return result;
    }
}
