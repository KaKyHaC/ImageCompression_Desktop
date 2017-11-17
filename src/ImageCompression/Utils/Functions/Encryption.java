package ImageCompression.Utils.Functions;


import ImageCompression.Containers.BoxOfOPC;
import ImageCompression.Utils.Objects.DataOPC;

/**
 * Created by Димка on 30.10.2016.
 */
public class Encryption { //singleton
    private static Encryption ourInstance = new Encryption();

    public static Encryption getInstance() {
        return new Encryption();
    }

    private Encryption() {
    }

    static short[] key;
    DataOPC dopc;

    public static void encode(BoxOfOPC bopc, String key){
        ourInstance.key=KeyGen(key);
        Encryption.encode(bopc.a);
        Encryption.encode(bopc.b);
        Encryption.encode(bopc.c);
    }

    private static void encode(DataOPC[][] dopcs){
        for(int i=0;i<dopcs.length;i++){
            for(int j =0;j<dopcs[0].length;j++){
                encode(dopcs[i][j]);
            }
        }
    }

    private static void encode(DataOPC dataOPC){
        dataOPC.base=encode(dataOPC.base);
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

    private static short[] KeyGen(String key){
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
