package ImageCompression.Utils;

import ImageCompression.Objects.BoxOfOPC;
import ImageCompression.Containers.Matrix;
import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Functions.Encryption;
import ImageCompression.Utils.Objects.Flag;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EncryptionTest {
    BoxOfOPC boxOfOPC,_boxOfOPC;
    String key="dimas";
    Matrix matrix=new Matrix(200,200,new Flag("0"));
    @Before
    public void setUp() throws Exception {
        boxOfOPC=new BoxOfOPC(matrix);
        _boxOfOPC=new BoxOfOPC(matrix);
    }

    @Test
    public void TestUtils(){
        AssertBoxEquals(boxOfOPC,_boxOfOPC);
    }

    @Test
    public void encode() throws Exception {
        Encryption.encode(boxOfOPC,key);
        Encryption.encode(boxOfOPC,key);
        AssertBoxEquals(boxOfOPC,_boxOfOPC);
    }
    private void AssertBoxEquals(BoxOfOPC a,BoxOfOPC b){
        AssertDataEqual(a.getDopcA(),b.getDopcA());
        AssertDataEqual(a.getDopcB(),b.getDopcB());
        AssertDataEqual(a.getDopcC(),b.getDopcC());
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
        assertEquals(a.sign,b.sign);
        assertEquals(a.N,b.N);
//        assertEquals(a.base,b.base);
        for(int i=0;i<a.base.length;i++){
            assertEquals(a.base[i],b.base[i]);
        }
    }

}