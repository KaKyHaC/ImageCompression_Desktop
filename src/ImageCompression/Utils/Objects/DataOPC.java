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

    public short[] FromSignToArray() {
        short [] res=new short[SIZEOFBLOCK/2];
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                res[i/2]<<=1;
                if(sign[i][j]){
                    res[i/2]|=1;
                }
            }
        }

        return res;
    }
    public void FromArrayToSing(short[] s) {
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                sign[i][j]=((s[i/2]&1)==1);
                s[i/2]>>=1;
            }
        }
    }

    public short[] FromBaseToArray() {
        int ofset=SIZEOFBLOCK/2;

        short[] res=new short[SIZEOFBLOCK/2];
        for(int i=0;i<res.length;i++){
            assert (base[i]<0xFF);
            assert (base[i+ofset]<0xFF);
            res[i]=base[i];
            res[i]<<=SIZEOFBLOCK;
            res[i]|=base[i+ofset];
        }
        return res;
    }
    public void FromArrayToBase(short[] b) {
        int ofset=SIZEOFBLOCK/2;

        short[] res=new short[SIZEOFBLOCK/2];
        for(int i=0;i<res.length;i++) {
            base[i+ofset]=(short)(b[i]&0xff);
            b[i]>>=SIZEOFBLOCK;
            base[i]=(short)(b[i]&0xff);
        }
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
        int offset=SIZEOFBLOCK/2;
        StringBuilder sb=new StringBuilder();

        if(flag.isOneFile()&&!flag.isGlobalBase())
            for(short b : FromBaseToArray())
                sb.append((char)b);

        short[] sign=FromSignToArray();
        for(int i=0;i<sign.length;i++){
            sb.append((char)sign[i]);
        }

        if(flag.isDC())
            sb.append((char)DC);

        if(!flag.isLongCode()){
            byte[] code=N.toByteArray();
            sb.append((char)code.length);
            for(int i=0;i<code.length;i++){
                char c=(char)code[i];
                c<<=SIZEOFBLOCK;
                c|=(char)code[++i];
                sb.append(c);
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
        int offset=SIZEOFBLOCK/2;
        int index=0;

        if(flag.isOneFile()&&!flag.isGlobalBase()) {
            short[] a=new short[offset];
            for (int i = 0; i < offset; i++) {
                a[i] = (short) s.charAt(index++);
            }
            FromArrayToBase(a);
        }

        byte[] sign=new byte[SIZEOFBLOCK];
        for(int i=0;i<offset;i++){
            sign[i+offset]=(byte)s.charAt(index);
            sign[i]=(byte)(s.charAt(index++)>>SIZEOFBLOCK);
        }

        if(flag.isDC())
            DC=(short)s.charAt(index++);

        if(!flag.isLongCode()){
            int length=s.charAt(index++);
            byte[] code=new byte[length];
            for(int i=0;i<length;i++){
                code[i]=(byte)s.charAt(index);
                code[++i]=(byte)(s.charAt(index++)>>SIZEOFBLOCK);
            }
        }else{
            int length=s.charAt(index++);
            for(int i=0;i<length;i++){
                long v=(long)s.charAt(index++);
                v|=(long)(s.charAt(index++)<<16);
                v|=(long)(s.charAt(index++)<<32);
                v|=(long)(s.charAt(index++)<<48);
                Code.add(v);
            }
        }

    }
}
