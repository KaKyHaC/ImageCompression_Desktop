package ImageCompression.Utils.Functions;


import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Objects.Flag;
import ImageCompression.Utils.Objects.Parameters;

import java.math.BigInteger;

/**
 * Created by Димка on 27.09.2016.
 */
public class OPCMultiThread { //singelton
    public final static int SIZEOFBLOCK = 8;
    private OPCMultiThread(){}

    private static  void directOPC(Flag flag, short[][]dataOrigin, DataOPC dataOPC){
        MakeUnSigned(dataOrigin,dataOPC);
        if(flag.isDC())
            DCminus(dataOrigin,dataOPC);
        FindeBase(dataOrigin,dataOPC);

        if(flag.isLongCode())
            OPCdirectUseOnlyLong(dataOrigin,dataOPC);
        else
            OPCdirect(dataOrigin,dataOPC);
    }
    private static  void reverseOPC(Flag flag,short[][]dataOrigin,DataOPC dataOPC){
        if(flag.isLongCode())
            OPCreverseUseOnlyLong(dataOrigin,dataOPC);
        else
            OPCreverse(dataOrigin,dataOPC);

        if(flag.isDC())
            DCplus(dataOrigin,dataOPC);
        
        MakeSigned(dataOrigin,dataOPC);

    }


    private static  DataOPC FindeBase(final short[][] dataOrigin,DataOPC dataOPC) {
//        short[] base=new short[SIZEOFBLOCK];
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            assert (dataOrigin[i][0]<(short)(Byte.MAX_VALUE&0xFF));
            dataOPC.base[i]=(byte)(dataOrigin[i][0]);
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                if(dataOPC.base[i]<(dataOrigin[i][j]))
                {
                    dataOPC.base[i]=(byte)(dataOrigin[i][j]);
                }
            }
            dataOPC.base[i]++;
        }
        return dataOPC;
    }

    private static  DataOPC MakeUnSigned (final short[][] dataOrigin,DataOPC dataOPC)    {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                if(dataOrigin[i][j]<(short)0)
                {
                    dataOrigin[i][j]=(short)(dataOrigin[i][j]*(-1));
                    dataOPC.sign[i][j]=false;
                }
                else
                {
                    dataOPC.sign[i][j]=true;
                }
            }
        }
        return dataOPC;
    }
    private static  DataOPC MakeSigned(final short[][] dataOrigin,DataOPC dataOPC)    {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                if(dataOPC.sign[i][j]==false)
                {
                    dataOrigin[i][j]=(short)(dataOrigin[i][j]*(-1));
                }

            }
        }
        return dataOPC;
    }

    private static  void DCminus(short[][]dataOrigin,DataOPC dataOPC){
        dataOPC.DC=(dataOrigin[0][0]);
        dataOrigin[0][0]=0;
    }
    private static  void DCplus(short[][]dataOrigin,DataOPC dataOPC){
        dataOrigin[0][0]=dataOPC.DC;
    }


    private static  void OPCdirect(short[][]dataOrigin,DataOPC dataOPC){//TODO diagonal for optimization
        dataOPC.N=OPCdirectLong(dataOrigin,dataOPC);
/*        BigInteger base= new BigInteger("1");
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                if(dataOrigin[i][j]!=0)
                {
                    dataOPC.N=dataOPC.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(dataOPC.base[i]));

            }
        }*/
    }
    private static BigInteger OPCdirectLong(short[][]dataOrigin, DataOPC dataOPC){

        long base= 1;
        long res=0;
        long bufbase;
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                bufbase=base*dataOPC.base[i];

               /* if(bufbase<res)
                    System.out.println("res");*/
/*                if(bufbase<base)
                    System.out.println("base");*/
                if(bufbase> Parameters.getMAXLONG())//is true ?
                {
//                    System.out.println("go");
                    return OPCdirectBI(res,base,i,j,dataOrigin,dataOPC);
                }
                if(dataOrigin[i][j]!=0)
                {
                    res+=base*(dataOrigin[i][j]);
                }
                base=bufbase;

            }
        }
        return BigInteger.valueOf(res);
    }
    private static  BigInteger OPCdirectBI(long res,long baseval,int i1,int j1,short[][]dataOrigin,DataOPC dataOPC){

        BigInteger val=BigInteger.valueOf(res);
        BigInteger base=BigInteger.valueOf(baseval);

        int i=i1;
        int j=j1;
        for(;i>=0;i--)
        {
            for(;j>=0;j--)
            {
                if(dataOrigin[i][j]!=0)
                {
                    val=val.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(dataOPC.base[i]));

            }
            j=SIZEOFBLOCK-1;
        }
        return val;
    }

    private static  void OPCreverse(short[][]dataOrigin,DataOPC dataOPC) // method copy from C++ Project MAH
    {
        BigInteger copy=new BigInteger("1");
        for (int i = SIZEOFBLOCK - 1; i >= 0; i--)
        {
            for (int j = SIZEOFBLOCK - 1; j >= 0; j--)
            {
                BigInteger a, b;

                a = dataOPC.N.divide( copy);
                copy =copy.multiply(BigInteger.valueOf(dataOPC.base[i]));

                b =dataOPC.N.divide( copy);
                b =b.multiply(BigInteger.valueOf( dataOPC.base[i]));
                dataOrigin[i][j] = (a.subtract(b)).shortValue() ;
            }
        }
    }

    private static  void OPCdirectUseOnlyLong(short[][]dataOrigin,DataOPC dataOPC){
        long base= 1;
        long res=0;
        long bufbase;
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                bufbase=base*dataOPC.base[i];
                if(bufbase> Parameters.getMAXLONG())//is true ?
                {
                    dataOPC.Code.add(res);
                    base= 1;
                    res=0;
                    bufbase=base*dataOPC.base[i];

                }
                if(dataOrigin[i][j]!=0)
                {
                    res+=base*(dataOrigin[i][j]);
                }
                base=bufbase;
            }
        }
        dataOPC.Code.add(res);
    }
    private static  void OPCreverseUseOnlyLong(short[][]dataOrigin,DataOPC dataOPC){
        long copy=1;
        int index=0;
        long curN=dataOPC.Code.elementAt(index);
        long nextcopy;
        for (int i = SIZEOFBLOCK - 1; i >= 0; i--)
        {
            if(dataOPC.base[i]==0)//for wrong password;
                dataOPC.base[i]=1;
            for (int j = SIZEOFBLOCK - 1; j >= 0; j--)
            {
                nextcopy=copy*dataOPC.base[i];
                if(nextcopy>Parameters.getMAXLONG()||nextcopy<0)
                {
                    copy=1;
                    index++;
                    nextcopy=copy*dataOPC.base[i];
                    if(index<dataOPC.Code.size())
                        curN=dataOPC.Code.elementAt(index);
                }
                long a, b;


                a = curN/( copy);
                copy =nextcopy;

                b =curN/( copy);
                b =b*(( dataOPC.base[i]));
                dataOrigin[i][j] =(short) (a-(b));
            }
        }
    }




    public static short[][]getDataOrigin(DataOPC dopc,Flag flag){
        DataOPC dataOPC=dopc;
        short[][] dataOrigin=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        reverseOPC(flag,dataOrigin,dataOPC);
        return dataOrigin;
    }
    public static DataOPC getDataOPC(short[][] dataOrigin,Flag flag){
        DataOPC dataOPC=new DataOPC();
        directOPC(flag,dataOrigin,dataOPC);
        return  dataOPC;
    }

    public static DataOPC findBase(short[][] dataOrigin,Flag flag){//TODO What it that doing ?
        DataOPC dataOPC=new DataOPC();

        MakeUnSigned(dataOrigin,dataOPC);
        if(flag.isDC()) {
            DCminus(dataOrigin,dataOPC);
        }
        FindeBase(dataOrigin,dataOPC);

        return  dataOPC;}
    public static DataOPC directOPCwithFindedBase(short[][] dataOrigin,DataOPC d,Flag flag){
        DataOPC dataOPC=d;

        MakeUnSigned(dataOrigin,dataOPC);
        if(flag.isDC()) {
            DCminus(dataOrigin,dataOPC);
        }

        if(flag.isLongCode())
            OPCdirectUseOnlyLong(dataOrigin,dataOPC);
        else
            OPCdirect(dataOrigin,dataOPC);
        return dataOPC;
    }

}
