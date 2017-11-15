package ImageCompression.Utils;

import ImageCompression.Containers.BoxOfOPC;
import ImageCompression.Objects.DataOPC;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EncryptionTest {
    BoxOfOPC boxOfOPC,_boxOfOPC;
    String key="dimas";
    @Before
    public void setUp() throws Exception {
        boxOfOPC=new BoxOfOPC(16,16,false);
        _boxOfOPC=new BoxOfOPC(16,16,false);
    }
    private BoxOfOPC createInstance(){
        int size=16;
        BoxOfOPC res=new BoxOfOPC(size,size,false);
        return res;
    }

    @Test
    public void encode() throws Exception {
        Encryption.encode(boxOfOPC,key);
        Encryption.encode(boxOfOPC,key);
        assertEquals(boxOfOPC,_boxOfOPC);
    }
    private void AssertBoxEquals(BoxOfOPC a,BoxOfOPC b){
        AssertDataEqual(a.a,b.a);
        AssertDataEqual(a.b,b.b);
        AssertDataEqual(a.c,b.c);
    }
    private void AssertDataEqual(DataOPC[][] a,DataOPC[][] b){
        assertEquals(a.length,b.length);
        assertEquals(a[0].length,b[0].length);
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[0].length;j++){
                AssertDataEqual(a[i][j],b[i][j]);
            }
        }
    }
    private void AssertDataEqual(DataOPC a,DataOPC b){
        assertEquals(a.DC,b.DC);
        assertEquals(a.Code,b.Code);
        assertEquals(a.base,b.base);
        assertEquals(a.sign,b.sign);
        assertEquals(a.N,b.N);

    }

}