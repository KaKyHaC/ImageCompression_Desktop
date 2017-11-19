package ImageCompression.Utils.Objects;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Queue;
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


    public byte[] FromBigIntToArray() {
        return N.toByteArray();
    }
    public void FromArrayToBigInt(byte[] code) {
        N=new BigInteger(code);
    }

    public byte[] FromSignToArray() {
        byte [] res=new byte[SIZEOFBLOCK/2];
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
    public void FromArrayToSing(byte[] s) {
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                sign[i][j]=((s[i]&1)==1);
                s[i]>>=1;
            }
        }
    }

    public byte[] FromBaseToArray() {
        int ofset=SIZEOFBLOCK/2;

        byte[] res=new byte[SIZEOFBLOCK/2];
        for(int i=0;i<res.length;i++){
            res[i]=(byte)base[i];
        }
        return res;
    }
    public void FromArrayToBase(byte[] b) {
        for(int i=0;i<b.length;i++) {
            base[i]=(short)((b[i])&0xff);
        }
    }

    public byte[] getDC() {
        byte[] res=new byte[2];
        res[0]=(byte)(DC>>SIZEOFBLOCK);
        res[1]=(byte)DC;
        return res;
    }
    public void setDC(byte[] bytes) {
        this.DC = bytes[0];
        DC<<=SIZEOFBLOCK;
        DC|=bytes[1];
    }

    public Vector<Long> getVectorCode(){
        return Code;
    }
    public void setVectorCode(Vector<Long> v)
    {
        Code=v;
    }

    public String toString(Flag flag) {
        int offset=SIZEOFBLOCK/2;
        StringBuilder sb=new StringBuilder();

        if(flag.isOneFile()&&!flag.isGlobalBase())
            for(short b : FromBaseToArray())
                sb.append((char)b);

        byte[] sign=FromSignToArray();
        for(int i=0;i<sign.length;i++){
            sb.append((char)sign[i]);
        }

        if(flag.isDC())
            sb.append((char)DC);

        if(!flag.isLongCode()){
            byte[] n =FromBigIntToArray();
            sb.append((char)n.length);
            for(short c : n){
                sb.append((short)c);
            }
        }else{
            sb.append((char)Code.size());
            for(int i=0;i<Code.size();i++){
                long v=Code.elementAt(i);
                sb.append((char)v);
                v>>=16;
                sb.append((char)v);
                v>>=16;
                sb.append((char)v);
                v>>=16;
                sb.append((char)v);
            }
        }

        return sb.toString();
    }
    public void valueOf(String s,Flag flag){
        int offset=SIZEOFBLOCK;
        int index=0;

        if(flag.isOneFile()&&!flag.isGlobalBase()) {
            byte[] a=new byte[offset];
            for (int i = 0; i < offset; i++) {
                a[i] = (byte) s.charAt(index++);
            }
            FromArrayToBase(a);
        }

        byte[] sign=new byte[offset];
        for(int i=0;i<offset;i++){
            sign[i]=(byte) s.charAt(index++);
        }
        FromArrayToSing(sign);

        if(flag.isDC())
            DC=(short)s.charAt(index++);

        if(!flag.isLongCode()){
            int length=s.charAt(index++);
            byte[] code=new byte[length];
            for(int i=0;i<length;i++){
                code[i]=(byte) s.charAt(index++);
            }
            FromArrayToBigInt(code);
        }else{
            int length=s.charAt(index++);
            for(int i=0;i<length;i++){
                long v=(long)s.charAt(index++);
                v|=(long)(s.charAt(index++))<<16;
                v|=(long)(s.charAt(index++))<<32;
                v|=(long)(s.charAt(index++))<<48;
                Code.add(v);
            }
        }

    }
}
