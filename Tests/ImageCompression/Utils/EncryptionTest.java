package ImageCompression.Utils;

import ImageCompression.Containers.DataOpc;
import ImageCompression.Containers.TripleDataOpcMatrix;
import ImageCompression.Constants.State;
import ImageCompression.ProcessingModules.ModuleOPC.ModuleOPC;
import ImageCompression.Containers.TripleShortMatrix;
import ImageCompression.Utils.Functions.Encryption;
import ImageCompression.Containers.Flag;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EncryptionTest {
    ModuleOPC moduleOPC, _moduleOPC;
    String key="dimas";
    TripleShortMatrix tripleShortMatrix =new TripleShortMatrix(200,200,new Flag("0"), State.RGB);
    @Before
    public void setUp() throws Exception {
        moduleOPC =new ModuleOPC(tripleShortMatrix);
        _moduleOPC =new ModuleOPC(tripleShortMatrix);
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
    private void AssertBoxEquals(TripleDataOpcMatrix a, TripleDataOpcMatrix b){
        AssertDataEqual(a.getA(),b.getA());
        AssertDataEqual(a.getB(),b.getB());
        AssertDataEqual(a.getC(),b.getC());
    }
    private void AssertDataEqual(DataOpc[][] a, DataOpc[][] b){
        assertEquals(a.length,b.length);
        assertEquals(a[0].length,b[0].length);
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[0].length;j++){
                AssertDataEqual(a[i][j],b[i][j]);
            }
        }
    }
    private void AssertDataEqual(DataOpc a,DataOpc b){
        assertEquals(a.getDC(),b.getDC());
        assertEquals(a.getVectorCode(),b.getVectorCode());
        assertEquals(a.getSign(),b.getSign());
        assertEquals(a.getN(),b.getN());
//        assertEquals(a.base,b.base);
        for(int i=0;i<a.getBase().length;i++){
            assertEquals(a.getBase()[i],b.getBase()[i]);
        }
    }

}