package ImageCompressionLib.Containers.Type;

/**
 * Created by Димка on 13.10.2016.
 */

public class Flag {
    //TODO remove steganography
    public enum Parameter{
        // empty value 8
        OneFile(16),Enlargement(32),DC(64),LongCode(128),GlobalBase(256),
        Password(512)/*,Steganography(1024)*/,Alignment(2048),CompressionUtils(4096),Quantization(1),Encryption(4),DCT(2);
        final int value;
        Parameter(int i) {
            value=i;
        }
    }
    @Deprecated
    public enum QuantizationState {Without, First}
    @Deprecated
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

    @Deprecated
    public QuantizationState getQuantization(){
        byte q=(byte)(f&3);
        if(q==0)
            return QuantizationState.Without;
        else if(q==1)
            return  QuantizationState.First;
        else
            return null;
    }
    @Deprecated
    public void setQuantization(QuantizationState qs){
        switch (qs) {
            case Without:f&=(~3);
                break;
            case First:f|=(1);f&=(~2);
                break;
        }
    }

    @Deprecated
    public Encryption getEncryption(){
        byte e=(byte)(f&12);
        switch (e){
            case 0:return Encryption.Without;
            case 4:return Encryption.First;
            default: return null;
        }
    }
    @Deprecated
    public void setEncryption(Encryption e){
        switch (e) {
            case Without: f&=(~12);
                break;
            case First:f|=(4);f&=(~8);
                break;
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
    public void setTrue(Parameter parameter){
        setChecked(parameter,true);
    }
    public void setFalse(Parameter parameter){
        setChecked(parameter,false);
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
        StringBuilder sb=new StringBuilder("Flag(");
        for(Parameter p :Parameter.values()){
            if(isChecked(p))
                sb.append(p.name()+"=true,");
        }
        sb.append(")");
        return sb.toString();
    }


    public static Flag createDefaultFlag(){
        Flag f=new Flag();
        f.setTrue(Parameter.OneFile);
        f.setTrue(Parameter.DC);
        f.setTrue(Parameter.CompressionUtils);
        f.setTrue(Parameter.DCT);
        return f;
    }
    public static Flag createCompressionFlag(){
        Flag f=new Flag();
        f.setTrue(Parameter.OneFile);
        f.setTrue(Parameter.Enlargement);
        f.setTrue(Parameter.DC);
        f.setTrue(Parameter.LongCode);
        f.setTrue(Parameter.CompressionUtils);
        f.setTrue(Parameter.Quantization);
        f.setTrue(Parameter.DCT);
        return f;
    }
}

