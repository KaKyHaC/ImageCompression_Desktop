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
    public final static int SIZEOFBLOCK=8;

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
    public void FromBigIntToVector(ByteVector vector,short[] base){

        byte[] code=N.toByteArray();
//        assert (code.length<Short.MAX_VALUE);
//        vector.append((short)code.length);
        int length=getLengthOfCode(base);
        while (length-->code.length)
            vector.append((byte)0);

        for(byte b : code){
            vector.append(b);
        }


//        System.out.print(",dL="+(code.length-length));
//        assert code.length<=length:"cL:"+code.length+">l:"+length;
    }
    public void FromArrayToBigInt(byte[] code) {
        N=new BigInteger(code);
    }
    public void FromVectorToBigInt(ByteVector vector,short[]base){
//        int len=vector.getNextShort();
        int len =getLengthOfCode(base);
        byte[] code=new byte[len];
        for(int i=0;i<len;i++){
            code[i]=vector.getNext();
        }
        N=new BigInteger(code);
    }

    public byte[] FromSignToArray() {
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
    public void FromSignToVector(ByteVector vector){
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            byte res=0;
            for(int j=0;j<SIZEOFBLOCK;j++)
            {
                res<<=1;
                if(sign[i][j]){
                    res|=1;
                }
            }
            vector.append(res);
        }
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
    public void FromVectorToSign(ByteVector vector){
        for(int i=0;i<SIZEOFBLOCK;i++)
        {
            byte s=vector.getNext();
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                sign[i][j]=((s&1)==1);
                s>>=1;
            }
        }
    }

    public short[] FromBaseToArray() {
        short[] res=new short[SIZEOFBLOCK];
        System.arraycopy(base, 0, res, 0, res.length);
        return res;
    }
    public void FromArrayToBase(short[] b) {
        System.arraycopy(b, 0, base, 0, b.length);
    }
    public void FromBaseToVector(ByteVector vector){
//        if(!flag.isDC()){
//            vector.append(base[i++]);
//        }
//        while (i<SIZEOFBLOCK){
//            assert (base[i]<0xff):"base["+i+"]="+base[i];
//            vector.append((byte)base[i++]);
//        }
        vector.append(base[0]);
        vector.append(base[1]);
        vector.append(base[2]);
        vector.append(base[3]);

        vector.append(base[4]);
        vector.append(base[5]);
        vector.append(base[6]);
        vector.append(base[7]);
    }
    public void FromVectorToBase(ByteVector vector){
//        if(!flag.isDC()){
//            base[i++]=vector.getNextShort();
//        }
//        while (i<SIZEOFBLOCK) {
//            base[i++]=(short)((vector.getNext())&0xff);
//        }
        base[0]=vector.getNextShort();
        base[1]=vector.getNextShort();
        base[2]=vector.getNextShort();
        base[3]=vector.getNextShort();

        base[4]=vector.getNextShort();
        base[5]=vector.getNextShort();
        base[6]=vector.getNextShort();
        base[7]=vector.getNextShort();
    }

    public byte[] getDC() {
        byte[] res=new byte[2];
        res[0]=(byte)(DC>>SIZEOFBLOCK);
        res[1]=(byte)DC;
        return res;
    }
    public void FromDcToVector(ByteVector vector){
        vector.append(DC);
    }
    public void setDC(byte[] bytes) {
        this.DC = bytes[0];
        DC<<=SIZEOFBLOCK;
        DC|=bytes[1];
    }
    public void FromVectorToDc(ByteVector vector){
        DC=vector.getNextShort();
    }

    public Vector<Long> getVectorCode(){
        return Code;
    }
    public void FromCodeToVector(ByteVector vector){
        int len=Code.size();
        assert (len<0xf);
        vector.append((byte)len);
        for (long l : Code){
            vector.append(l);
        }
    }
    public void setVectorCode(Vector<Long> v) {
        Code=v;
    }
    public void FromVectorToCode(ByteVector vector){
        int len = vector.getNext()&0xFF;
        for(int i=0;i<len;i++){
            Code.add(vector.getNextLong());
        }
    }

    //support utils
    private static int getLengthOfCode(short []base){//TODO optimize this fun
        BigInteger bi=new BigInteger("1");
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++)
                bi=bi.multiply(BigInteger.valueOf((base[i])));
        }
//        if(bi.compareTo(N)<0)
//            System.out.println("Alarm");
        return bi.toByteArray().length;
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
    public DataOPC valueOf(String s,Flag flag){
        int offset=SIZEOFBLOCK;
        int index=0;

        if(flag.isOneFile()&&!flag.isGlobalBase()) {
            short[] a=new short[offset];
            for (int i = 0; i < offset; i++) {
                a[i] = (short) s.charAt(index++);
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
        return this;
    }

    public ByteVector toByteVector(ByteVector vector,Flag f){
        if(f.isDC())
            FromDcToVector(vector);

        if(!f.isGlobalBase()&&f.isOneFile())
            FromBaseToVector(vector);

        if(f.isLongCode())
            FromCodeToVector(vector);
        else
            FromBigIntToVector(vector,base);

        FromSignToVector(vector);

        return vector;
    }
    public DataOPC valueOf(ByteVector vector,Flag f){
        if(f.isDC())
            FromVectorToDc(vector);

        if(!f.isGlobalBase()&&f.isOneFile())
            FromVectorToBase(vector);

        if(f.isLongCode())
            FromVectorToCode(vector);
        else
            FromVectorToBigInt(vector,base);

        FromVectorToSign(vector);

        return this;
    }


    @Override
    public boolean equals(Object obj) {
        if(this==obj)
            return true;
        if(obj.getClass()!=DataOPC.class)
            return false;

        DataOPC d=(DataOPC)obj;
        if(d.DC!=DC)
            return false;
        for(int i=0;i<SIZEOFBLOCK;i++){
            if(d.base[i]!=base[i])
                return false;
            for (int j=0;j<SIZEOFBLOCK;j++){
                if(d.sign[i][j]!=sign[i][j])
                    return false;
            }
        }
        if(d.Code.size()!=Code.size())
            return false;

        for (int i=0;i<Code.size();i++){
            if(!(d.Code.get(i).equals(Code.get(i))))
                return false;
        }
        return true;
    }

    public long getByteSize(Flag flag){
        ByteVector vector=new ByteVector(10);
        toByteVector(vector,flag);
        return vector.getSize();
    }
}
