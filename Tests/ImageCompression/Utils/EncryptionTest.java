package ImageCompression.Utils;

import ImageCompression.Containers.BoxOfOpc;
import ImageCompression.Constants.State;
import ImageCompression.Objects.ModuleOPC;
import ImageCompression.Containers.Matrix;
import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Functions.Encryption;
import ImageCompression.Utils.Objects.Flag;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EncryptionTest {
    ModuleOPC moduleOPC, _moduleOPC;
    String key="dimas";
    Matrix matrix=new Matrix(200,200,new Flag("0"), State.RGB);
    @Before
    public void setUp() throws Exception {
        moduleOPC =new ModuleOPC(matrix);
        _moduleOPC =new ModuleOPC(matrix);
    }

    @Test
    public void TestUtils(){
        AssertBoxEquals(moduleOPC.getBoxOfOpc(true), _moduleOPC.getBoxOfOpc(true));
    }

    @Test
    public void encode() throws Exception {
        Encryption.encode(moduleOPC.getBoxOfOpc(true),key);
        Encryption.encode(moduleOPC.getBoxOfOpc(true),key);
        AssertBoxEquals(moduleOPC.getBoxOfOpc(true), _moduleOPC.getBoxOfOpc(true));
    }
    private void AssertBoxEquals(BoxOfOpc a, BoxOfOpc b){
        AssertDataEqual(a.getA(),b.getA());
        AssertDataEqual(a.getB(),b.getB());
        AssertDataEqual(a.getC(),b.getC());
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