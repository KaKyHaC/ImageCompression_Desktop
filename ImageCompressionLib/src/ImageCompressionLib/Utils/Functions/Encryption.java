package ImageCompressionLib.Utils.Functions;


import ImageCompressionLib.Containers.Matrix.Matrix;
import ImageCompressionLib.Containers.TripleDataOpcMatrix;
import ImageCompressionLib.Containers.Type.DataOpc;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Димка on 30.10.2016.
 */
public class Encryption { //singleton
    private static Encryption ourInstance = new Encryption();
    private Encryption() {
    }

    static short[] key;

    public static TripleDataOpcMatrix encode(TripleDataOpcMatrix bopc, @NotNull String key) throws Exception {
        ourInstance.key=KeyGen(key);
        Encryption.encode(bopc.getA());
        Encryption.encode(bopc.getB());
        Encryption.encode(bopc.getC());
        return bopc;
    }

    private static void encode(Matrix<DataOpc> dopcs){
        for(int i=0;i<dopcs.getWidth();i++){
            for(int j =0;j<dopcs.getHeight();j++){
                encode(dopcs.get(i,j));
            }
        }
    }

    private static void encode(DataOpc DataOpcOld){
        DataOpcOld.setBase(encode(DataOpcOld.getBase()));
    }

    // метод для шифровки текста с помощью XOR
    private static short[] encode(short[] secret) {
        short sS,sK,sR;
        short[] result=new short[secret.length];
        for(int i=0;i<secret.length;i++){
            sS=secret[i];
            sK=key[i%key.length];
            sR=(short)(sS^sK);
            result[i]=sR;
        }
        return result;
    }

    private static short[] KeyGen(String key) throws Exception {
        if(key.length()==0)
            throw new Exception("Password length == 0");
        short[] res=new short[key.length()];
        short sK;
        for(int i=0;i<key.length();i++){
            sK=(short)key.charAt(i%key.length());
            sK<<=(8-sK%8);
            res[i]=sK;
        }
        return res;
    }
}
