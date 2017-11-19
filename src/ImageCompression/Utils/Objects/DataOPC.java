package ImageCompression.Utils.Objects;

import java.math.BigInteger;
import java.util.Vector;

/**
 * Created by Димка on 27.09.2016.
 */
public class DataOPC {
    final static int SIZEOFBLOCK=8;

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
                if(sign[i][j]){
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
                sign[i][j]=((s[i]&1)==1);
                s[i]>>=1;
            }
        }
    }

    public short[] FromBaseToArray() {
       return base;
    }
    public void FromArrayToBase(short[] b) {
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

    public String toString(Flag flag) {
        StringBuilder sb=new StringBuilder();
        for(short b : FromBaseToArray())
            sb.append(b);

        return sb.toString();
    }
}
