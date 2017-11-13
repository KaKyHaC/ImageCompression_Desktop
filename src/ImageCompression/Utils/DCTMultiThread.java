package ImageCompression.Utils;


import ImageCompression.Constants.Cosine;
import ImageCompression.Constants.QuantizationTable;
import ImageCompression.Containers.TypeQuantization;

public class DCTMultiThread {//singelton

    public final static int SIZEOFBLOCK = 8;
    final static double OneDivadMathsqrt2=1.0/Math.sqrt(2.0);


    private static TypeQuantization tq;
    // private long DC;

    private DCTMultiThread() {}



    public static short[][] directDCT(short[][] data){
        short[][] dataOriginal=data;
        short[][] dataProcessed=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        return directDCT(dataOriginal,dataProcessed);
    }
    public static short[][] reverseDCT(short[][] data){
        short[][] dataOriginal=data;
        short[][] dataProcessed=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        return reverseDCT(dataOriginal,dataProcessed);
    }

    public static short[][] directQuantization(TypeQuantization _tq,short[][]data) {

        if (_tq == TypeQuantization.luminosity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    //dataProcessed[i][j]/=QuantizationTable.getSmart(1,i,j);
                    data[i][j] /= QuantizationTable.getLuminosity(i, j);
                }
        else if (_tq == TypeQuantization.Chromaticity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dataProcessed[i][j]/=QuantizationTable.getSmart(3,i,j);
                    data[i][j] /= QuantizationTable.getChromaticity(i, j);
                }
        return data;
    }

    public static short[][] reverseQuantization(TypeQuantization _tq,short[][]data) {

        if (_tq == TypeQuantization.luminosity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dataProcessed[i][j]*=QuantizationTable.getSmart(1,i,j);
                    data[i][j] *= QuantizationTable.getLuminosity(i, j);
                }
        else if (_tq == TypeQuantization.Chromaticity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dataProcessed[i][j]*=QuantizationTable.getSmart(3,i,j);
                    data[i][j] *= QuantizationTable.getChromaticity(i, j);
                }
        return data;

    }

    /*-------main metode---------*/
    private static short[][] directDCT(short[][] dataOriginal,short[][]dataProcessed) {
       // minus128();//test
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                double res= Cosine.getDCTres(i,j);
                double sum=0.0;
                for(int x=0;x<SIZEOFBLOCK;x++)
                {
                    for(int y=0;y<SIZEOFBLOCK;y++)
                    {
                        /*double cos=Cosine.getCos(x,y,i,j);
                        double buf=dataOriginal[x][y];
                        double mul=buf*cos;
                        sum+=mul;*/
                        sum += dataOriginal[x][y] * Cosine.getCos(x, y, i, j);
                        // sum+=Cosine.getDirectDCTres(i,j,x,y,dataOriginal[x][y]);
                    }
                }
                res*=sum;
                dataProcessed[i][j]=(short)res;
                /*if(i==0&&j==0)
                    AC=(long)res;
                else if (res<256&&res>-256)
                    dataDCT[i][j]=(byte)res;
                else getAC();*/


            }
        }
        return dataProcessed;
    }
    private static short[][] reverseDCT(short[][]dataOriginal,short[][]dataProcessed) {
        for(int x=0;x<SIZEOFBLOCK;x++)
        {
            for(int y=0;y<SIZEOFBLOCK;y++)
            {
               // double res=1.0/4.0;
                double sum=0.0;
                for(int i=0;i<SIZEOFBLOCK;i++)
                {
                    for(int j=0;j<SIZEOFBLOCK;j++)
                    {
                        double Ci=(i==0)?OneDivadMathsqrt2:1.0;
                        double Cj=(j==0)?OneDivadMathsqrt2:1.0;
                        double buf=Ci*Cj*dataOriginal[i][j]*Cosine.getCos(x,y,i,j);
                        sum+=buf;
                    }
                }
                dataProcessed[x][y]=(short)(0.25*sum);
            }
        }
       // plus128();//
        return dataProcessed;
    }

    private static short[][] minus128 (short[][]dataOriginal){
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                dataOriginal[i][j]-=(short)128;
            }
        return dataOriginal;
    }
    private static short[][] plus128 (short[][]dataProcessed){
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                dataProcessed[i][j]+=(short)128;
            }
        return dataProcessed;
    }

    // обопщенно позоционное кодирование







}
