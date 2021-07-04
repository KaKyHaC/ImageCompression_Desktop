package ImageCompressionLib.constants;

/**
 * Created by Димка on 08.08.2016.
 */
public class QuantizationTable {//singelton

    private static final int SIZEOFTABLE = 8;
    private static QuantizationTable instance=new QuantizationTable();
    private static short[][] luminosity ,chromaticity,smart;
    private static int smartval;

    private QuantizationTable()
    {
        luminosity = new short[][]{
                {1, 2, 3, 4, 5, 6, 7, 8},
                {2, 2, 3, 4, 5, 8, 10, 15},
                {3, 4, 5, 6, 7, 9, 16, 20},
                {4, 6, 8, 19, 21, 27, 28, 36},
                {5, 12, 27, 36, 48, 59, 69, 77},
                {7, 35, 35, 44, 51, 69, 79, 82},
                {9, 44, 47, 48, 49, 59, 69, 79},
                {12, 49, 55, 68, 79, 80, 99, 99}};

/*        luminosity = new short[][]{
                {1,1,1,2,3,40,51,61},
                {1,1,2,19,26,58,60,55},
                {1,2,16,24,40,57,69,56},
                {2,17,22,29,51,87,80,62},
                {18,22,37,56,68,99,99,77},
                {24,35,55,64,81,99,99,92},
                {49,64,78,87,99,99,99,99},
                {72,92,95,98,99,90,99,99}};*/

        chromaticity=new short[][]{
                {1, 1, 2, 4, 9, 99, 99, 99},
                {1, 2, 2, 6, 99, 99, 99, 99},
                {2, 2, 56, 99, 99, 99, 99, 99},
                {4, 66, 99, 99, 99, 99, 99, 99},
                {99,99,99,99,99,99,99,99},
                {99,99,99,99,99,99,99,99},
                {99,99,99,99,99,99,99,99},
                {99,99,99,99,99,99,99,99}
        };


    }
    private static void CreateSmart(int val){
        smartval=val;
        smart=new short[SIZEOFTABLE][SIZEOFTABLE];
        for(int i=0;i<SIZEOFTABLE;i++)
        {
            for(int j=0;j<SIZEOFTABLE;j++)
            {
                smart[i][j]=(short)(1+(val)*(i+j));
            }
        }
    }


    public static short getLuminosity(int x,int y){return luminosity[x][y];}
    public static short getChromaticity(int x,int y){return chromaticity[x][y];}
    public static short getSmart(int val,int i,int j){
        if(smart==null||val!=smartval)
            CreateSmart(val);
        return smart[i][j];
    }
}
