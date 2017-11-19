package ImageCompression.Utils.Objects;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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


    public short[] FromBigIntToArray() {
        byte []b=N.toByteArray();
        int size=b.length/2+b.length%2;
        short[] res=new short[size];
        for(int i=base.length-1;i>=0;i--){
            res[i/2]<<=8;
            res[i/2]=b[i];
        }
        return res;
    }
    public void FromArrayToBigInt(short[] code)
    {// to turn shorts back to bytes.
        byte[] bytes2 = new byte[code.length * 2];
        ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(code);
        N=new BigInteger(bytes2);
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
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
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
            short[] n =FromBigIntToArray();
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
        int offset=SIZEOFBLOCK/2;
        int index=0;

        if(flag.isOneFile()&&!flag.isGlobalBase()) {
            short[] a=new short[offset];
            for (int i = 0; i < offset; i++) {
                a[i] = (short) s.charAt(index++);
            }
            FromArrayToBase(a);
        }

        short[] sign=new short[offset];
        for(int i=0;i<offset;i++){
            sign[i]=(short)s.charAt(index++);
        }
        FromArrayToSing(sign);

        if(flag.isDC())
            DC=(short)s.charAt(index++);

        if(!flag.isLongCode()){
            int length=s.charAt(index++);
            short[] code=new short[length];
            for(int i=0;i<length;i++){
                code[i]=(short) s.charAt(index++);
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