package ImageCompressionLib.Constants;

/**
 * Created by Димка on 07.08.2016.
 */
public class Cosine {//singelton
    final private static int SIZEOFBLOCK = 8;
    final private static int MAXOFPIXEL = 256;

    private static Cosine instance = new Cosine();
    private static double[][] cosine;
    private static double[][][][] value;
    private static double[][] DCTres;

    //private static double[][][][][][]  reverseDCT;
    private static double[][][][][] directDCT;


    private Cosine() {
        cosine = new double[SIZEOFBLOCK][SIZEOFBLOCK];
        value = new double[SIZEOFBLOCK][SIZEOFBLOCK][SIZEOFBLOCK][SIZEOFBLOCK];
        DCTres = new double[2][2];

        for (int i = 0; i < SIZEOFBLOCK; i++) {
            for (int j = 0; j < SIZEOFBLOCK; j++) {
                double data = ((2.0 * i + 1.0) * j * Math.PI) / (2 * SIZEOFBLOCK);
                double cos = Math.cos((data));//toRadianse
                cosine[i][j] = cos;

            }
        }

        for (int i = 0; i < SIZEOFBLOCK; i++) {
            for (int j = 0; j < SIZEOFBLOCK; j++) {
                for (int x = 0; x < SIZEOFBLOCK; x++) {
                    for (int y = 0; y < SIZEOFBLOCK; y++) {
                        value[i][j][x][y] = cosine[y][j] * cosine[x][i];
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                double Ci = (i == 0) ? 1.0 / Math.sqrt(2.0) : 1.0;
                double Cj = (j == 0) ? 1.0 / Math.sqrt(2.0) : 1.0;
                DCTres[i][j] = (1.0 / Math.sqrt(2.0 * SIZEOFBLOCK)) * Ci * Cj;
            }

        // directDCT();
    }

    public static double getCos(int x, int i) {
        return cosine[x][i];
    }

    public static double getCos(int x, int y, int i, int j) {
        return value[i][j][x][y];
    }// it's true ?

    public static double getDCTres(int i, int j) {
        if (i != 0)
            i = 1;
        if (j != 0)
            j = 1;
        return DCTres[i][j];
    }

    public static double getDirectDCTres(int i, int j, int x, int y, int val) {
        return directDCT[i][j][x][y][val];
    }

    private void directDCT() {
        directDCT = new double[SIZEOFBLOCK][SIZEOFBLOCK][SIZEOFBLOCK][SIZEOFBLOCK][MAXOFPIXEL];
        for (int i = 0; i < SIZEOFBLOCK; i++) {
            for (int j = 0; j < SIZEOFBLOCK; j++) {
                for (int x = 0; x < SIZEOFBLOCK; x++) {
                    for (int y = 0; y < SIZEOFBLOCK; y++) {
                        for (int q = 0; q < MAXOFPIXEL; q++)
                            directDCT[i][j][x][y][q] = value[i][j][x][y] * q;
                    }
                }
            }
        }
    }

}
