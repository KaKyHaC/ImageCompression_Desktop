package ImageCompressionLib.Utils.Functions;


import ImageCompressionLib.Constants.ConstantsKt;
import ImageCompressionLib.Containers.DataOpcOld;
import ImageCompressionLib.Containers.Flag;
//import ImageCompressionLib.Parameters;

import java.math.BigInteger;

import static ImageCompressionLib.Containers.Flag.Parameter.DCT;

//import static ImageCompressionLib.Constants.ConstantsKt.MAX_LONG;

/**
 * Created by Димка on 27.09.2016.
 */
public class OPCMultiThread { //singelton
    public final static int SIZEOFBLOCK = 8;
    private OPCMultiThread(){}

    private static  void directOPC(Flag flag, short[][]dataOrigin, DataOpcOld DataOpcOld){
        if(flag.isChecked(DCT))
            MakeUnSigned(dataOrigin, DataOpcOld);
        if(flag.isChecked(Flag.Parameter.DC))
            DCminus(dataOrigin, DataOpcOld);
        FindeBase(dataOrigin, DataOpcOld);

        if(flag.isChecked(Flag.Parameter.LongCode))
            OPCdirectUseOnlyLong(dataOrigin, DataOpcOld);
        else
            OPCdirect(dataOrigin, DataOpcOld);
    }
    private static  void reverseOPC(Flag flag, short[][]dataOrigin, DataOpcOld DataOpcOld){
        if(flag.isChecked(Flag.Parameter.LongCode))
            OPCreverseUseOnlyLong(dataOrigin, DataOpcOld);
        else
            OPCreverse(dataOrigin, DataOpcOld);

        if(flag.isChecked(Flag.Parameter.DC))
            DCplus(dataOrigin, DataOpcOld);

        if(flag.isChecked(DCT))
            MakeSigned(dataOrigin, DataOpcOld);
    }


    private static DataOpcOld FindeBase(final short[][] dataOrigin, DataOpcOld DataOpcOld) {
//        short[] base=new short[SIZEOFBLOCK];
        for(int i=0;i<SIZEOFBLOCK;i++) {
//            assert (dataOrigin[i][0]<(short)(Byte.MAX_VALUE&0xFF));
            DataOpcOld.getBase()[i]=(dataOrigin[i][0]);
            for(int j=0;j<SIZEOFBLOCK;j++) {
                if(DataOpcOld.getBase()[i]<(dataOrigin[i][j])) {
                    DataOpcOld.getBase()[i]=(dataOrigin[i][j]);
                }
            }
            DataOpcOld.getBase()[i]++;
//            assert DataOpcOld.getBase()[i]<0xff:"base ["+i+"]="+DataOpcOld.getBase()[i];
        }
        return DataOpcOld;
    }

    private static DataOpcOld MakeUnSigned (final short[][] dataOrigin, DataOpcOld DataOpcOld)    {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                if(dataOrigin[i][j]<(short)0) {
                    dataOrigin[i][j]=(short)(dataOrigin[i][j]*(-1));
                    DataOpcOld.getSign()[i][j]=false;
                }
                else {
                    DataOpcOld.getSign()[i][j]=true;
                }
            }
        }
        return DataOpcOld;
    }
    private static DataOpcOld MakeSigned(final short[][] dataOrigin, DataOpcOld DataOpcOld)    {
        for(int i=0;i<SIZEOFBLOCK;i++) {
            for(int j=0;j<SIZEOFBLOCK;j++) {
                if(!DataOpcOld.getSign()[i][j]) {
                    dataOrigin[i][j]=(short)(dataOrigin[i][j]*(-1));
                }
            }
        }
        return DataOpcOld;
    }

    private static  void DCminus(short[][]dataOrigin, DataOpcOld DataOpcOld){
        DataOpcOld.setDC((dataOrigin[0][0]));
        dataOrigin[0][0]=0;
    }
    private static  void DCplus(short[][]dataOrigin, DataOpcOld DataOpcOld){
        dataOrigin[0][0]= DataOpcOld.getDC();
    }


    private static  void OPCdirect(short[][]dataOrigin, DataOpcOld DataOpcOld){//TODO diagonal for optimization
        DataOpcOld.setN(OPCdirectLong(dataOrigin, DataOpcOld));
/*        BigInteger base= new BigInteger("1");
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                if(dataOrigin[i][j]!=0)
                {
                    DataOpcOld.N=DataOpcOld.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(DataOpcOld.getBase()[i]));

            }
        }*/
    }
    private static BigInteger OPCdirectLong(short[][]dataOrigin, DataOpcOld DataOpcOld){

        long base= 1;
        long res=0;
        long bufbase=1;
        for(int i=SIZEOFBLOCK-1;i>=0;i--) {
            for(int j=SIZEOFBLOCK-1;j>=0;j--) {
                bufbase=base* DataOpcOld.getBase()[i];
               /* if(bufbase<res)
                    System.out.println("res");*/
/*                if(bufbase<base)
                    System.out.println("base");*/
                if(bufbase> ConstantsKt.getMAX_LONG()){//is true ?
//                    System.out.println("go");
                    return OPCdirectBI(res,base,i,j,dataOrigin, DataOpcOld);
                }
                if(dataOrigin[i][j]!=0) {
                    res+=base*(dataOrigin[i][j]);
                }
                base=bufbase;

            }
        }
//        if(bufbase<res)System.out.println("AAAA");
        return BigInteger.valueOf(res);
    }
    private static  BigInteger OPCdirectBI(long res, long baseval, int i1, int j1, short[][]dataOrigin, DataOpcOld DataOpcOld){

        BigInteger val=BigInteger.valueOf(res);
        BigInteger base=BigInteger.valueOf(baseval);

        int i=i1;
        int j=j1;
        for(;i>=0;i--) {
            for(;j>=0;j--) {
                if(dataOrigin[i][j]!=0) {
                    val=val.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(DataOpcOld.getBase()[i]));
            }
            j=SIZEOFBLOCK-1;
        }
        return val;
    }

    private static  void OPCreverse(short[][]dataOrigin, DataOpcOld DataOpcOld) {// method copy from C++ Project MAH
        BigInteger copy=new BigInteger("1");
        for (int i = SIZEOFBLOCK - 1; i >= 0; i--) {
            for (int j = SIZEOFBLOCK - 1; j >= 0; j--) {
                BigInteger a, b;

                a = DataOpcOld.getN().divide( copy);
                copy =copy.multiply(BigInteger.valueOf(DataOpcOld.getBase()[i]));

                b = DataOpcOld.getN().divide( copy);
                b =b.multiply(BigInteger.valueOf( DataOpcOld.getBase()[i]));
                dataOrigin[i][j] = (a.subtract(b)).shortValue() ;
            }
        }
    }

    private static  void OPCdirectUseOnlyLong(short[][]dataOrigin, DataOpcOld DataOpcOld){
        long base= 1;
        long res=0;
        long bufbase;
        for(int i=SIZEOFBLOCK-1;i>=0;i--) {
            for(int j=SIZEOFBLOCK-1;j>=0;j--) {
                bufbase=base* DataOpcOld.getBase()[i];
                if(bufbase> ConstantsKt.getMAX_LONG())//is true ?
                {
                    DataOpcOld.getVectorCode().add(res);
                    base= 1;
                    res=0;
                    bufbase=base* DataOpcOld.getBase()[i];
                }
                if(dataOrigin[i][j]!=0) {
                    res+=base*(dataOrigin[i][j]);
                }
                base=bufbase;
            }
        }
        DataOpcOld.getVectorCode().add(res);
    }
    private static  void OPCreverseUseOnlyLong(short[][]dataOrigin, DataOpcOld DataOpcOld){
        long copy=1;
        int index=0;
        long curN= DataOpcOld.getVectorCode().elementAt(index);
        long nextcopy;
        for (int i = SIZEOFBLOCK - 1; i >= 0; i--) {
            if(DataOpcOld.getBase()[i]==0)//for wrong password;
                DataOpcOld.getBase()[i]=1;
            for (int j = SIZEOFBLOCK - 1; j >= 0; j--) {
                nextcopy=copy* DataOpcOld.getBase()[i];
                if(nextcopy>ConstantsKt.getMAX_LONG()||nextcopy<0) {
                    copy=1;
                    index++;
                    nextcopy=copy* DataOpcOld.getBase()[i];
                    if(index< DataOpcOld.getVectorCode().size())
                        curN= DataOpcOld.getVectorCode().elementAt(index);
                }
                long a, b;


                a = curN/( copy);
                copy =nextcopy;

                b =curN/( copy);
                b =b*(( DataOpcOld.getBase()[i]));
                dataOrigin[i][j] =(short) (a-(b));
            }
        }
    }




    public static short[][]getDataOrigin(DataOpcOld dopc, Flag flag){
        DataOpcOld DataOpcOld =dopc;
        short[][] dataOrigin=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        reverseOPC(flag,dataOrigin, DataOpcOld);
        return dataOrigin;
    }
    public static DataOpcOld getDataOpc(short[][] dataOrigin, Flag flag){
        DataOpcOld DataOpcOld =new DataOpcOld();
        directOPC(flag,dataOrigin, DataOpcOld);
        return DataOpcOld;
    }

    public static DataOpcOld findBase(short[][] dataOrigin, Flag flag){//TODO What it that doing ?
        DataOpcOld DataOpcOld =new DataOpcOld();

        MakeUnSigned(dataOrigin, DataOpcOld);
        if(flag.isChecked(Flag.Parameter.DC)) {
            DCminus(dataOrigin, DataOpcOld);
        }
        FindeBase(dataOrigin, DataOpcOld);

        return DataOpcOld;}
    public static DataOpcOld directOPCwithFindedBase(short[][] dataOrigin, DataOpcOld d, Flag flag){
        DataOpcOld DataOpcOld =d;

        MakeUnSigned(dataOrigin, DataOpcOld);
        if(flag.isChecked(Flag.Parameter.DC)) {
            DCminus(dataOrigin, DataOpcOld);
        }

        if(flag.isChecked(Flag.Parameter.LongCode))
            OPCdirectUseOnlyLong(dataOrigin, DataOpcOld);
        else
            OPCdirect(dataOrigin, DataOpcOld);
        return DataOpcOld;
    }

}
