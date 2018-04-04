package ImageCompression.Utils.Functions;


import ImageCompression.Containers.DataOpc;
import ImageCompression.Containers.Flag;
import ImageCompression.Parameters;

import java.math.BigInteger;

/**
 * Created by Димка on 27.09.2016.
 */
public class OPCMultiThread { //singelton
    public final static int SIZEOFBLOCK = 8;
    private OPCMultiThread(){}

    private static  void directOPC(Flag flag, short[][]dataOrigin, DataOpc DataOpc){
        MakeUnSigned(dataOrigin,DataOpc);
        if(flag.isChecked(Flag.Parameter.DC))
            DCminus(dataOrigin,DataOpc);
        FindeBase(dataOrigin,DataOpc);

        if(flag.isChecked(Flag.Parameter.LongCode))
            OPCdirectUseOnlyLong(dataOrigin,DataOpc);
        else
            OPCdirect(dataOrigin,DataOpc);
    }
    private static  void reverseOPC(Flag flag,short[][]dataOrigin,DataOpc DataOpc){
        if(flag.isChecked(Flag.Parameter.LongCode))
            OPCreverseUseOnlyLong(dataOrigin,DataOpc);
        else
            OPCreverse(dataOrigin,DataOpc);

        if(flag.isChecked(Flag.Parameter.DC))
            DCplus(dataOrigin,DataOpc);
        
        MakeSigned(dataOrigin,DataOpc);
    }


    private static  DataOpc FindeBase(final short[][] dataOrigin,DataOpc DataOpc) {
//        short[] base=new short[SIZEOFBLOCK];
        for(int i=0;i<SIZEOFBLOCK;i++) {
//            assert (dataOrigin[i][0]<(short)(Byte.MAX_VALUE&0xFF));
            DataOpc.base[i]=(dataOrigin[i][0]);
            for(int j=0;j<SIZEOFBLOCK;j++) {
                if(DataOpc.base[i]<(dataOrigin[i][j])) {
                    DataOpc.base[i]=(dataOrigin[i][j]);
                }
            }
            DataOpc.base[i]++;
//            assert DataOpc.base[i]<0xff:"base ["+i+"]="+DataOpc.base[i];
        }
        return DataOpc;
    }

    private static  DataOpc MakeUnSigned (final short[][] dataOrigin,DataOpc DataOpc)    {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                if(dataOrigin[i][j]<(short)0) {
                    dataOrigin[i][j]=(short)(dataOrigin[i][j]*(-1));
                    DataOpc.sign[i][j]=false;
                }
                else {
                    DataOpc.sign[i][j]=true;
                }
            }
        }
        return DataOpc;
    }
    private static  DataOpc MakeSigned(final short[][] dataOrigin,DataOpc DataOpc)    {
        for(int i=0;i<SIZEOFBLOCK;i++) {
            for(int j=0;j<SIZEOFBLOCK;j++) {
                if(!DataOpc.sign[i][j]) {
                    dataOrigin[i][j]=(short)(dataOrigin[i][j]*(-1));
                }
            }
        }
        return DataOpc;
    }

    private static  void DCminus(short[][]dataOrigin,DataOpc DataOpc){
        DataOpc.DC=(dataOrigin[0][0]);
        dataOrigin[0][0]=0;
    }
    private static  void DCplus(short[][]dataOrigin,DataOpc DataOpc){
        dataOrigin[0][0]=DataOpc.DC;
    }


    private static  void OPCdirect(short[][]dataOrigin,DataOpc DataOpc){//TODO diagonal for optimization
        DataOpc.N=OPCdirectLong(dataOrigin,DataOpc);
/*        BigInteger base= new BigInteger("1");
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                if(dataOrigin[i][j]!=0)
                {
                    DataOpc.N=DataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(DataOpc.base[i]));

            }
        }*/
    }
    private static BigInteger OPCdirectLong(short[][]dataOrigin, DataOpc DataOpc){

        long base= 1;
        long res=0;
        long bufbase=1;
        for(int i=SIZEOFBLOCK-1;i>=0;i--) {
            for(int j=SIZEOFBLOCK-1;j>=0;j--) {
                bufbase=base*DataOpc.base[i];
               /* if(bufbase<res)
                    System.out.println("res");*/
/*                if(bufbase<base)
                    System.out.println("base");*/
                if(bufbase> Parameters.getMAXLONG()){//is true ?
//                    System.out.println("go");
                    return OPCdirectBI(res,base,i,j,dataOrigin,DataOpc);
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
    private static  BigInteger OPCdirectBI(long res,long baseval,int i1,int j1,short[][]dataOrigin,DataOpc DataOpc){

        BigInteger val=BigInteger.valueOf(res);
        BigInteger base=BigInteger.valueOf(baseval);

        int i=i1;
        int j=j1;
        for(;i>=0;i--) {
            for(;j>=0;j--) {
                if(dataOrigin[i][j]!=0) {
                    val=val.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(DataOpc.base[i]));
            }
            j=SIZEOFBLOCK-1;
        }
        return val;
    }

    private static  void OPCreverse(short[][]dataOrigin,DataOpc DataOpc) {// method copy from C++ Project MAH
        BigInteger copy=new BigInteger("1");
        for (int i = SIZEOFBLOCK - 1; i >= 0; i--) {
            for (int j = SIZEOFBLOCK - 1; j >= 0; j--) {
                BigInteger a, b;

                a = DataOpc.N.divide( copy);
                copy =copy.multiply(BigInteger.valueOf(DataOpc.base[i]));

                b =DataOpc.N.divide( copy);
                b =b.multiply(BigInteger.valueOf( DataOpc.base[i]));
                dataOrigin[i][j] = (a.subtract(b)).shortValue() ;
            }
        }
    }

    private static  void OPCdirectUseOnlyLong(short[][]dataOrigin,DataOpc DataOpc){
        long base= 1;
        long res=0;
        long bufbase;
        for(int i=SIZEOFBLOCK-1;i>=0;i--) {
            for(int j=SIZEOFBLOCK-1;j>=0;j--) {
                bufbase=base*DataOpc.base[i];
                if(bufbase> Parameters.getMAXLONG())//is true ?
                {
                    DataOpc.Code.add(res);
                    base= 1;
                    res=0;
                    bufbase=base*DataOpc.base[i];
                }
                if(dataOrigin[i][j]!=0) {
                    res+=base*(dataOrigin[i][j]);
                }
                base=bufbase;
            }
        }
        DataOpc.Code.add(res);
    }
    private static  void OPCreverseUseOnlyLong(short[][]dataOrigin,DataOpc DataOpc){
        long copy=1;
        int index=0;
        long curN=DataOpc.Code.elementAt(index);
        long nextcopy;
        for (int i = SIZEOFBLOCK - 1; i >= 0; i--) {
            if(DataOpc.base[i]==0)//for wrong password;
                DataOpc.base[i]=1;
            for (int j = SIZEOFBLOCK - 1; j >= 0; j--) {
                nextcopy=copy*DataOpc.base[i];
                if(nextcopy>Parameters.getMAXLONG()||nextcopy<0) {
                    copy=1;
                    index++;
                    nextcopy=copy*DataOpc.base[i];
                    if(index<DataOpc.Code.size())
                        curN=DataOpc.Code.elementAt(index);
                }
                long a, b;


                a = curN/( copy);
                copy =nextcopy;

                b =curN/( copy);
                b =b*(( DataOpc.base[i]));
                dataOrigin[i][j] =(short) (a-(b));
            }
        }
    }




    public static short[][]getDataOrigin(DataOpc dopc,Flag flag){
        DataOpc DataOpc=dopc;
        short[][] dataOrigin=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        reverseOPC(flag,dataOrigin,DataOpc);
        return dataOrigin;
    }
    public static DataOpc getDataOpc(short[][] dataOrigin,Flag flag){
        DataOpc DataOpc=new DataOpc();
        directOPC(flag,dataOrigin,DataOpc);
        return  DataOpc;
    }

    public static DataOpc findBase(short[][] dataOrigin,Flag flag){//TODO What it that doing ?
        DataOpc DataOpc=new DataOpc();

        MakeUnSigned(dataOrigin,DataOpc);
        if(flag.isDC()) {
            DCminus(dataOrigin,DataOpc);
        }
        FindeBase(dataOrigin,DataOpc);

        return  DataOpc;}
    public static DataOpc directOPCwithFindedBase(short[][] dataOrigin,DataOpc d,Flag flag){
        DataOpc DataOpc=d;

        MakeUnSigned(dataOrigin,DataOpc);
        if(flag.isDC()) {
            DCminus(dataOrigin,DataOpc);
        }

        if(flag.isLongCode())
            OPCdirectUseOnlyLong(dataOrigin,DataOpc);
        else
            OPCdirect(dataOrigin,DataOpc);
        return DataOpc;
    }

}
