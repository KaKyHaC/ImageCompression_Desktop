package ImageCompressionLib.Utils.Functions;


import ImageCompressionLib.Constants.Cosine;
import ImageCompressionLib.Constants.QuantizationTable;
import ImageCompressionLib.Constants.TypeQuantization;
import ImageCompressionLib.Containers.Matrix.Matrix;
import ImageCompressionLib.Containers.Matrix.ShortMatrix;

public class DCTMultiThread {//singelton

    public final static int SIZEOFBLOCK = 8;
    private final static double OneDivideMathsqrt2 =1.0/Math.sqrt(2.0);


    private static TypeQuantization tq;
    // private long DC;

    private DCTMultiThread() {}



    public static Matrix<Short> directDCT(Matrix<Short> data){
        Matrix<Short> dataProcessed=new ShortMatrix(data.getWidth(),data.getHeight());
        return directDCT(data,dataProcessed);
    }
    public static Matrix<Short> reverseDCT(Matrix<Short> data){
        Matrix<Short> dataProcessed=new ShortMatrix(data.getWidth(),data.getHeight());
        return reverseDCT(data,dataProcessed);
    }

    public static Matrix<Short> directQuantization(TypeQuantization _tq,Matrix<Short>data) {
        if (_tq == TypeQuantization.luminosity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    //dataProcessed[i][j]/=QuantizationTable.getSmart(1,i,j);
                    data.set(i,j,(short)(data.get(i,j)/ QuantizationTable.getLuminosity(i, j)));
                }
        else if (_tq == TypeQuantization.Chromaticity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dataProcessed[i][j]/=QuantizationTable.getSmart(3,i,j);
                    data.set(i,j,(short)(data.get(i,j)/ QuantizationTable.getChromaticity(i, j)));
//                    data[i][j] /= QuantizationTable.getChromaticity(i, j);
                }
        return data;
    }

    public static Matrix<Short> reverseQuantization(TypeQuantization _tq,Matrix<Short>data) {
        if (_tq == TypeQuantization.luminosity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dataProcessed[i][j]*=QuantizationTable.getSmart(1,i,j);
                    data.set(i,j,(short)(data.get(i,j)* QuantizationTable.getLuminosity(i, j)));
//                    data[i][j] *= QuantizationTable.getLuminosity(i, j);
                }
        else if (_tq == TypeQuantization.Chromaticity)
            for (int i = 0; i < SIZEOFBLOCK; i++)
                for (int j = 0; j < SIZEOFBLOCK; j++) {
                    // dataProcessed[i][j]*=QuantizationTable.getSmart(3,i,j);
                    data.set(i,j,(short)(data.get(i,j)* QuantizationTable.getChromaticity(i, j)));
//                    data[i][j] *= QuantizationTable.getChromaticity(i, j);
                }
        return data;

    }

    /*-------main metode---------*/
    private static Matrix<Short> directDCT(Matrix<Short> dataOriginal,Matrix<Short>dataProcessed) {
       // minus128();//test
        int w=dataOriginal.getWidth();
        int h=dataOriginal.getHeight();
        for(int i=0;i<w;i++) {
            for(int j=0;j<h;j++) {
                double res= Cosine.getDCTres(i,j);
                double sum=0.0;
                for(int x=0;x<w;x++) {
                    for(int y=0;y<h;y++) {
//                        assert dataOriginal[x][y]<=0xff:"dataOriginal["+x+"]["+y+"]="+dataOriginal[x][y];
                        /*double cos=Cosine.getCos(x,y,i,j);
                        double buf=dataOriginal[x][y];
                        double mul=buf*cos;
                        sum+=mul;*/
                        sum += dataOriginal.get(x,y) * Cosine.getCos(x, y, i, j);
                        // sum+=Cosine.getDirectDCTres(i,j,x,y,dataOriginal[x][y]);
                    }
                }
                res*=sum;
                dataProcessed.set(i,j,(short)res);
                /*if(i==0&&j==0)
                    AC=(long)res;
                else if (res<256&&res>-256)
                    dataDCT[i][j]=(byte)res;
                else getAC();*/
            }
        }
        return dataProcessed;
    }
    private static Matrix<Short> reverseDCT(Matrix<Short>dataOriginal,Matrix<Short>dataProcessed) {
        int w=dataOriginal.getWidth();
        int h=dataOriginal.getHeight();
        for(int x=0;x<w;x++) {
            for(int y=0;y<h;y++) {
               // double res=1.0/4.0;
                double sum=0.0;
                for(int i=0;i<w;i++) {
                    for(int j=0;j<h;j++) {
                        double Ci=(i==0)? OneDivideMathsqrt2 :1.0;
                        double Cj=(j==0)? OneDivideMathsqrt2 :1.0;
                        double buf=Ci*Cj*dataOriginal.get(i,j)*Cosine.getCos(x,y,i,j);
                        sum+=buf;
                    }
                }
//                assert FromDoubleToShort(0.25*sum)<=0xff:"dataProcessed["+x+"]["+y+"]="+0.25*sum;
                dataProcessed.set(x,y,FromDoubleToShort(0.25*sum)); // old (short)
            }
        }
       // plus128();//
        return dataProcessed;
    }

    private static Matrix<Short> minus128 (Matrix<Short>dataOriginal){
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++) {
                dataOriginal.set(i,j,(short)(dataOriginal.get(i,j)-128));
            }
        return dataOriginal;
    }
    private static Matrix<Short> plus128 (Matrix<Short>dataProcessed){
        for(int i=0;i< SIZEOFBLOCK;i++)
            for(int j=0;j<SIZEOFBLOCK;j++) {
                dataProcessed.set(i,j,(short)(dataProcessed.get(i,j)+128));
            }
        return dataProcessed;
    }

    // обопщенно позоционное кодирование


    private static short FromDoubleToShort(double d){
        if(d>=255)
            return 255;
        short res=(short)d;
        if(d%1>0.5)
            res++;
        return res;
    }
}
