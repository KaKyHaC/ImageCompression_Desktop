package ImageCompression.Objects;

import java.math.BigInteger;
import java.util.Vector;

/**
 * Created by Димка on 27.09.2016.
 */
public class DataOPC {

    final int SIZEOFBLOCK=8;

    public short [] base;
    public boolean [][] sign;
    public short DC;
    public BigInteger N;
    public Vector<Long> Code;

    public DataOPC() {

        base=new short[SIZEOFBLOCK];
        sign=new boolean[SIZEOFBLOCK][SIZEOFBLOCK];
        N=new BigInteger("0");
        Code=new Vector<Long>();
    }





    public byte[] BinaryStringGet() {
        return N.toByteArray();

    }
    public void BinaryStringSet(byte[] code) {

        N=new BigInteger(code);

    }

    public byte[] SignToString() {

        byte [] res=new byte[SIZEOFBLOCK];
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                res[i]<<=1;
                if(sign[i][j]==true)
                {
                    res[i]|=1;
                }

            }
        }
        return res;
          }
    public void SingFromString(byte[] s) {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                if((s[i]&1)==1)
                {
                   sign[i][j]=true;
                }
                else
                {
                    sign[i][j]=false;
                }
                s[i]>>=1;
            }
        }


/*      char[] zero = new char[]{'0','0','0','0','0','0','0','0'};
        for(int i=0;i<st.length();i++)
        {
            char ch=st.charAt(i);
            int Int=(int)ch;

            StringBuilder buf=new StringBuilder(Integer.toString(Int,2));
            int len=SIZEOFBLOCK-buf.length();
            buf.insert(0,zero,0,len);


            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                sign[i][j]=(buf.charAt(j)=='1')?true:false;
            }

        }*/

    }

    public short[] FromBaseToString() {
       return base;
    }
    public void FromStringToBase(short[] b) {
        base=b;
    }

    public short getDC() {
        return DC;
    }
    public void setDC(short DC) {
        this.DC = DC;
    }

    public Vector<Long> getVectorCode(){
        return Code;
    }
    public void setVectorCode(Vector<Long> v)
    {
        Code=v;
    }
}
