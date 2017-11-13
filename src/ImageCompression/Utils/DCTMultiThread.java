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



    public static short[][] directDCT(short[][] date){
        short[][] dateOriginal=date;
        short[][] dateProcessed=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        return directDCT(dateOriginal,dateProcessed);
    }
    public static short[][] reverseDCT(short[][] date){
        short[][] dateOriginal=date;
        short[][] dateProcessed=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        return reverseDCT(dateOriginal,dateProcessed);
    }

    public static short[][] directQuantization(TypeQuantization _tq,short[][]date) {

        if (_tq == TypeQuantization.luminosity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    //dateProcessed[i][j]/=QuantizationTable.getSmart(1,i,j);
                    date[i][j] /= QuantizationTable.getLuminosity(i, j);
                }
        else if (_tq == TypeQuantization.Chromaticity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dateProcessed[i][j]/=QuantizationTable.getSmart(3,i,j);
                    date[i][j] /= QuantizationTable.getChromaticity(i, j);
                }
        return date;
    }

    public static short[][] reverseQuantization(TypeQuantization _tq,short[][]date) {

        if (_tq == TypeQuantization.luminosity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dateProcessed[i][j]*=QuantizationTable.getSmart(1,i,j);
                    date[i][j] *= QuantizationTable.getLuminosity(i, j);
                }
        else if (_tq == TypeQuantization.Chromaticity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dateProcessed[i][j]*=QuantizationTable.getSmart(3,i,j);
                    date[i][j] *= QuantizationTable.getChromaticity(i, j);
                }
        return date;

    }

    /*-------main metode---------*/
    private static short[][] directDCT(short[][] dateOriginal,short[][]dateProcessed) {
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
                        double buf=dateOriginal[x][y];
                        double mul=buf*cos;
                        sum+=mul;*/
                        sum += dateOriginal[x][y] * Cosine.getCos(x, y, i, j);
                        // sum+=Cosine.getDirectDCTres(i,j,x,y,dateOriginal[x][y]);
                    }
                }
                res*=sum;
                dateProcessed[i][j]=(short)res;
                /*if(i==0&&j==0)
                    AC=(long)res;
                else if (res<256&&res>-256)
                    dateDCT[i][j]=(byte)res;
                else getAC();*/


            }
        }
        return dateProcessed;
    }
    private static short[][] reverseDCT(short[][]dateOriginal,short[][]dateProcessed) {
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
                        double buf=Ci*Cj*dateOriginal[i][j]*Cosine.getCos(x,y,i,j);
                        sum+=buf;
                    }
                }
                dateProcessed[x][y]=(short)(0.25*sum);
            }
        }
       // plus128();//
        return dateProcessed;
    }

    private static short[][] minus128 (short[][]dateOriginal){
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                dateOriginal[i][j]-=(short)128;
            }
        return dateOriginal;
    }
    private static short[][] plus128 (short[][]dateProcessed){
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                dateProcessed[i][j]+=(short)128;
            }
        return dateProcessed;
    }

    // обопщенно позоционное кодирование







}
