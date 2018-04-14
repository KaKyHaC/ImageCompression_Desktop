package ImageCompressionLib.Containers;

import ImageCompressionLib.Utils.Functions.Steganography;

/**
 * Created by Димка on 13.10.2016.
 */

public class Flag {
    public enum Parameter{
        OneFile(16),Enlargement(32),DC(64),LongCode(128),GlobalBase(256),
        Password(512),Steganography(1024),Alignment(2048),CompressionUtils(4096);

        final int value;
        Parameter(int i) {
            value=i;
        }
    }
    public enum QuantizationState {Without, First}
    public enum Encryption {Without, First}

    private short f;

    public Flag(){}
    public Flag(String s) {
        f = Short.decode(s);
    }
    public Flag(short val){
        f=val;
    }

    public short getFlag(){return f;}

    public QuantizationState getQuantization(){
        byte q=(byte)(f&3);
        if(q==0)
            return QuantizationState.Without;
        else if(q==1)
            return  QuantizationState.First;
        else
            return null;
    }
    public void setQuantization(QuantizationState qs){
        switch (qs) {
            case Without:f&=(~3);
                break;
            case First:f|=(1);f&=(~2);
                break;
        }
    }

    public Encryption getEncryption(){
        byte e=(byte)(f&12);
        switch (e){
            case 0:return Encryption.Without;
            case 4:return Encryption.First;
            default: return null;
        }
    }
    public void setEncryption(Encryption e){
        switch (e) {
            case Without: f&=(~12);
                break;
            case First:f|=(4);f&=(~8);
                break;
        }
    }

    //Utils
    private boolean isChecked(int param){
        return (f&param)!=0;
    }
    private void setChecked(boolean state,int param){
        if(state){
            f|=param;
        } else {
            f&=(~param);
        }
    }

    public boolean isChecked(Parameter param){
        return (f&param.value)!=0;
    }
    public void setChecked(Parameter parameter,boolean state){
        if(state){
            f|= parameter.value;
        } else {
            f&=(~parameter.value);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flag flag = (Flag) o;

        return f == flag.f;
    }
    @Override
    public int hashCode() {
        return (int) f;
    }
    @Override
    public String toString() {
        return Short.toString(f);
    }
}

