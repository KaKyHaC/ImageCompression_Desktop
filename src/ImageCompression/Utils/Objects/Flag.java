package ImageCompression.Utils.Objects;

/**
 * Created by Димка on 13.10.2016.
 */

public class Flag {
    final static int OneFile=16,Enlargement=32,DC=64,LongCode=128,GlobalBase=256,Password=512,Steganography=1024,Alignment=2048;
    private short f;

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

    public boolean isOneFile(){
        return isChecked(OneFile);
    }

    public void setOneFile(boolean o){
       setChecked(o,OneFile);
    }

    public boolean isEnlargement(){
        return isChecked(Enlargement);
    }

    public void setEnlargement(boolean o){
       setChecked(o,Enlargement);
    }

    public boolean isDC(){
        return isChecked(DC);
    }

    public void setDC(boolean o){
       setChecked(o,DC);
    }

    public boolean isLongCode(){return isChecked(LongCode);}

    public void setLongCode(boolean b){setChecked(b,LongCode);}

    public boolean isGlobalBase(){return isChecked(GlobalBase);}

    public void setGlobalBase(boolean b){setChecked(b,GlobalBase);}

    public boolean isPassword(){return isChecked(Password);}

    public void setPassword(boolean b){setChecked(b,Password);}

    public boolean isSteganography(){return isChecked(Steganography);}

    public void setSteganography(boolean b){setChecked(b,Steganography);}

    public boolean isAlignment(){return isChecked(Alignment);}

    public void setAlignment(boolean b){setChecked(b,Alignment);}

    private boolean isChecked(int param){
        return (f&param)!=0;
    }

    private void setChecked(boolean state,int param){
        if(state==true){
            f|=param;
        }
        else {
            f&=(~param);
        }
    }

    @Override
    public String toString() {
        return Short.toString(f);
    }

    public enum QuantizationState {Without, First}

    public enum Encryption {Without, First}
}

