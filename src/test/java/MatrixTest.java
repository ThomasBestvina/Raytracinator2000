import org.junit.jupiter.api.Test;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixTest {
    @Test
    void ConstructingMatrixFourSquare() {
        double[][] mat = {
                {1, 2, 3, 4},
                {5.5, 6.5, 7.5, 8.5},
                {9, 10, 11, 12},
                {13.5, 14.5, 11.5, 16.5}
        };
        Matrix matrix = new Matrix(mat);
        assertEquals(1, matrix.get(0, 0));
        assertEquals(4, matrix.get(0, 3));
    }

    @Test
    void ConstructingMatrixTwoSquare() {
        double[][] mat = {
                {-3, 5},
                {1, -2}
        };
        Matrix matrix = new Matrix(mat);
        assertEquals(5, matrix.get(0, 1));
    }

    @Test
    void MatricesEqual() {
        Matrix m1 = new Matrix(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 8, 7, 6},
                {5, 4, 3, 2}
        });
        Matrix m2 = new Matrix(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 8, 7, 6},
                {5, 4, 3, 2}
        });
        Matrix m3 = new Matrix(new double[][]{
                {2, 3, 4, 5},
                {6, 7, 8, 9},
                {8, 7, 6, 5},
                {4, 3, 2, 1}
        });

        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
    }

    @Test
    void multiplyMatricies() {
        Matrix m1 = new Matrix(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 8, 7, 6},
                {5, 4, 3, 2}
        });
        Matrix m2 = new Matrix(new double[][]{
                {-2, 1, 2, 3},
                {3, 2, 1, -1},
                {4, 3, 6, 5},
                {1, 2, 7, 8}
        });
        Matrix expectedResult = new Matrix(new double[][]{
                {20, 22, 50, 48},
                {44, 54, 114, 108},
                {40, 58, 110, 102},
                {16, 26, 46, 42}
        });
        assertEquals(expectedResult, m1.multiply(m2));
    }

    @Test
    void multiplyInvalidMultiplication() {
        Matrix m1 = new Matrix(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 8, 7, 6},
                {5, 4, 3, 2}
        });
        Matrix m2 = new Matrix(new double[][]{{2, 3}});
        assertNull(m1.multiply(m2));
    }

    @Test
    void multiplyByVector() {
        Matrix m1 = new Matrix(new double[][]{
                {1, 2, 3, 4},
                {2, 4, 4, 2},
                {8, 6, 4, 1},
                {0, 0, 0, 1}
        });
        Vector v = new Vector(1, 2, 3, 1);
        assertEquals(new Vector(18, 24, 33, 1), m1.multiply(v));
    }

    @Test
    void multiplyByIdentityMatrix() {
        Matrix m1 = new Matrix(new double[][]{
                {0, 1, 2, 4},
                {1, 2, 4, 8},
                {2, 4, 8, 16},
                {4, 8, 16, 32}
        });
        assertEquals(m1, m1.multiply(Matrix.identityMatrix()));
    }

    @Test
    void TransposeMatrix() {
        Matrix m1 = new Matrix(new double[][]{
                {0, 9, 3, 0},
                {9, 8, 0, 8},
                {1, 8, 5, 3},
                {0, 0, 5, 8}
        });

        Matrix transposeM1 =
                new Matrix(new double[][]{
                        {0, 9, 1, 0},
                        {9, 8, 8, 0},
                        {3, 0, 5, 5},
                        {0, 8, 3, 8}
                });
        assertEquals(transposeM1, m1.transpose());
    }

    @Test
    void TransposeIdentity() {
        Matrix i = Matrix.identityMatrix();
        assertEquals(i, i.transpose());
    }

    @Test
    void TwoByTwoDeterminant() {
        Matrix m1 = new Matrix(new double[][]{
                {1, 5},
                {-3, 2}
        });

        assertEquals(17, m1.determinant());
    }

    @Test
    void SubMatrixThreeByThree() {
        Matrix m1 = new Matrix(new double[][]{
                {1, 5, 0},
                {-3, 2, 7},
                {0, 6, -3}
        });
        Matrix m2 = new Matrix(new double[][]{
                {-3, 2},
                {0, 6}
        });
        assertEquals(m2, m1.submatrix(0, 2));
    }

    @Test
    void SubMatrixFourByFour() {
        Matrix m1 = new Matrix(new double[][]{
                {-6, 1, 1, 6},
                {-8, 5, 8, 6},
                {-1, 0, 8, 2},
                {-7, 1, -1, 1}
        });
        Matrix m2 = new Matrix(new double[][]{
                {-6, 1, 6},
                {-8, 8, 6},
                {-7, -1, 1}
        });
        assertEquals(m2, m1.submatrix(2, 1));
    }

    @Test
    void minorOfThreeByThreeMatrix() {
        Matrix m1 = new Matrix(new double[][]{
                {3, 5, 0},
                {2, -1, -7},
                {6, -1, 5}
        });
        assertEquals(25, m1.minor(1, 0));
    }

    @Test
    void ThreeByThreeCofactor() {
        Matrix m1 = new Matrix(new double[][]{
                {3, 5, 0},
                {2, -1, -7},
                {6, -1, 5}
        });
        assertEquals(-12, m1.cofactor(0, 0));
        assertEquals(-25, m1.cofactor(1, 0));
    }

    @Test
    void determinantThreeByThreeMatrix() {
        Matrix m1 = new Matrix(new double[][]{
                {1, 2, 6},
                {-5, 8, -4},
                {2, 6, 4}
        });
        assertEquals(56, m1.cofactor(0, 0));
        assertEquals(12, m1.cofactor(0, 1));
        assertEquals(-46, m1.cofactor(0, 2));
        assertEquals(-196, m1.determinant());
    }

    @Test
    void determinantFourByFourMatrix() {
        Matrix m1 = new Matrix(new double[][]{
                {-2, -8, 3, 5},
                {-3, 1, 7, 3},
                {1, 2, -9, 6},
                {-6, 7, 7, -9}
        });
        assertEquals(690, m1.cofactor(0, 0));
        assertEquals(447, m1.cofactor(0, 1));
        assertEquals(210, m1.cofactor(0, 2));
        assertEquals(51, m1.cofactor(0, 3));
        assertEquals(-4071, m1.determinant());
    }

    @Test
    void isInvertibleMatrix() {
        Matrix is = new Matrix(new double[][]{
                {6, 4, 4, 4},
                {5, 5, 7, 6},
                {4, -9, 3, -7},
                {9, 1, 7, -6}
        });
        Matrix isNot = new Matrix(new double[][]{
                {-4, 2, -2, -3},
                {9, 6, 2, 6},
                {0, -5, 1, -5},
                {0, 0, 0, 0}
        });
        assertTrue(is.isInvertible());
        assertFalse(isNot.isInvertible());
    }

    @Test
    void InvertMatrix() {
        Matrix a = new Matrix(new double[][]{
                {-5,2,6,-8},
                {1,-5,1,8},
                {7,7,-6,-7},
                {1,-3,7,4}
        });
        Matrix b = a.inverse();



        assertEquals(532, a.determinant());
        assertEquals(-160, a.cofactor(2,3));
        assertEquals((double) -160 /532, b.get(3,2));
        assertEquals(105, a.cofactor(3,2));
        assertEquals((double) 105/532, b.get(2,3));

        Matrix expectedB = new Matrix(new double[][]{
                {0.21805, 0.45113, 0.24060, -0.04511},
                {-0.80827, -1.45677, -0.44361, 0.52068},
                {-0.07895, -0.22368, -0.05263, 0.19737},
                {-0.52256, -0.81391, -0.30075, 0.30639}
        });

        assertEquals(expectedB, b);
    }

    @Test
    void InvertAnotherMatrix() {
        Matrix a = new Matrix(new double[][]{
                {8, -5, 9, 2},
                {7, 5, 6, 1},
                {-6, 0, 9, 6},
                {-3, 0, -9, -4}
        });

        Matrix expectedInverse = new Matrix(new double[][]{
                {-0.15385, -0.15385, -0.28205, -0.53846},
                {-0.07692, 0.12308, 0.02564, 0.03077},
                {0.35897, 0.35897, 0.43590, 0.92308},
                {-0.69231, -0.69231, -0.76923, -1.92308}
        });

        assertEquals(expectedInverse, a.inverse());
    }

    @Test
    void InvertThirdMatrix() {
        Matrix a = new Matrix(new double[][]{
                {9, 3, 0, 9},
                {-5, -2, -6, -3},
                {-4, 9, 6, 4},
                {-7, 6, 6, 2}
        });

        Matrix expectedInverse = new Matrix(new double[][]{
                {-0.04074, -0.07778, 0.14444, -0.22222},
                {-0.07778, 0.03333, 0.36667, -0.33333},
                {-0.02901, -0.14630, -0.10926, 0.12963},
                {0.17778, 0.06667, -0.26667, 0.33333}
        });

        assertEquals(expectedInverse, a.inverse());
    }

    @Test
    void multiplyProductByInverse() {
        Matrix a = new Matrix(new double[][]{
                {3, -9, 7, 3},
                {3, -8, 2, -9},
                {-4, 4, 4, 1},
                {-6, 5, -1, 1}
        });
        Matrix b = new Matrix(new double[][]{
                {8, 2, 2, 2},
                {3, -1, 7, 0},
                {7, 0, 5, 4},
                {6, -2, 0, 5}
        });

        Matrix c = a.multiply(b);
        assertEquals(a, c.multiply(b.inverse()));
    }

    @Test
    void ScalingMatrixAppliedToPoint()
    {
        Matrix s = Matrix.scalar(2,3,4);
        Vector p = Vector.point(-4,6,8);
        assertEquals(Vector.point(-8,18,32), s.multiply(p));
    }

    @Test
    void ScalingMatrixAppliedToVector()
    {
        Matrix s = Matrix.scalar(2,3,4);
        Vector v = Vector.vector3(-4,6,8);
        assertEquals(Vector.vector3(-8,18,32), s.multiply(v));
    }

    @Test
    void MultiplyingByInverseOfScalingMatrix()
    {
        Matrix s = Matrix.scalar(2,3,4);
        Matrix inv = s.inverse();
        Vector v = Vector.vector3(-4,6,8);
        assertEquals(Vector.vector3(-2,2,2), inv.multiply(v));
    }

    @Test
    void ReflectPoint()
    {
        Matrix t = Matrix.scalar(-1,1,1);
        Vector p = Vector.point(2,3,4);
        assertEquals(Vector.point(-2,3,4), t.multiply(p));
    }

    @Test
    void RotatePointAroundXAxis()
    {
        Vector p = Vector.point(0,1,0);
        Matrix half_quarter = Matrix.rotationX(Math.PI/4);
        Matrix full_quarter = Matrix.rotationX(Math.PI/2);
        assertEquals(Vector.point(0, Math.sqrt(2)/2, Math.sqrt(2)/2), half_quarter.multiply(p));
        assertEquals(Vector.point(0, 0, 1), full_quarter.multiply(p));
    }

    @Test
    void inverseXRotationRotatesInOppositeDirection()
    {
        Vector p = Vector.point(0,1,0);
        Matrix half_quarter = Matrix.rotationX(Math.PI/4);
        Matrix inv = half_quarter.inverse();
        assertEquals(Vector.point(0, Math.sqrt(2)/2, -Math.sqrt(2)/2), inv.multiply(p));
    }

    @Test
    void RotatePointAroundYAxis()
    {
        Vector p = Vector.point(0,0,1);
        Matrix half_quarter = Matrix.rotationY(Math.PI/4);
        Matrix full_quarter = Matrix.rotationY(Math.PI/2);
        assertEquals(Vector.point(Math.sqrt(2)/2, 0, Math.sqrt(2)/2), half_quarter.multiply(p));
        assertEquals(Vector.point(1, 0, 0), full_quarter.multiply(p));
    }

    @Test
    void RotatePointAroundZAxis()
    {
        Vector p = Vector.point(0,1,0);
        Matrix half_quarter = Matrix.rotationZ(Math.PI/4);
        Matrix full_quarter = Matrix.rotationZ(Math.PI/2);
        assertEquals(Vector.point(-Math.sqrt(2)/2, Math.sqrt(2)/2, 0), half_quarter.multiply(p));
        assertEquals(Vector.point(-1, 0, 0), full_quarter.multiply(p));
    }

    @Test
    void ShearingXInProportionToY() {
        Matrix transform = Matrix.shear(1, 0, 0, 0, 0, 0);
        Vector p = Vector.point(2, 3, 4);
        assertEquals(Vector.point(5, 3, 4), transform.multiply(p));
    }

    @Test
    void ShearingXInProportionToZ() {
        Matrix transform = Matrix.shear(0, 1, 0, 0, 0, 0);
        Vector p = Vector.point(2, 3, 4);
        assertEquals(Vector.point(6, 3, 4), transform.multiply(p));
    }

    @Test
    void ShearingYInProportionToX() {
        Matrix transform = Matrix.shear(0, 0, 1, 0, 0, 0);
        Vector p = Vector.point(2, 3, 4);
        assertEquals(Vector.point(2, 5, 4), transform.multiply(p));
    }

    @Test
    void ShearingYInProportionToZ() {
        Matrix transform = Matrix.shear(0, 0, 0, 1, 0, 0);
        Vector p = Vector.point(2, 3, 4);
        assertEquals(Vector.point(2, 7, 4), transform.multiply(p));
    }

    @Test
    void ShearingZInProportionToX() {
        Matrix transform = Matrix.shear(0, 0, 0, 0, 1, 0);
        Vector p = Vector.point(2, 3, 4);
        assertEquals(Vector.point(2, 3, 6), transform.multiply(p));
    }

    @Test
    void ShearingZInProportionToY() {
        Matrix transform = Matrix.shear(0, 0, 0, 0, 0, 1);
        Vector p = Vector.point(2, 3, 4);
        assertEquals(Vector.point(2, 3, 7), transform.multiply(p));
    }

    @Test
    void IndividualTransformationsAppliedInSequence() {
        Vector p = Vector.point(1, 0, 1);
        Matrix A = Matrix.rotationX(Math.PI / 2);
        Matrix B = Matrix.scalar(5, 5, 5);
        Matrix C = Matrix.translation(10, 5, 7);

        Vector p2 = A.multiply(p);
        assertEquals(Vector.point(1, -1, 0), p2);

        Vector p3 = B.multiply(p2);
        assertEquals(Vector.point(5, -5, 0), p3);

        Vector p4 = C.multiply(p3);
        assertEquals(Vector.point(15, 0, 7), p4);
    }

    @Test
    void ChainedTransformationsAppliedInReverseOrder() {
        Vector p = Vector.point(1, 0, 1);
        Matrix A = Matrix.rotationX(Math.PI / 2);
        Matrix B = Matrix.scalar(5, 5, 5);
        Matrix C = Matrix.translation(10, 5, 7);

        Matrix T = C.multiply(B).multiply(A);
        assertEquals(Vector.point(15, 0, 7), T.multiply(p));
    }
}