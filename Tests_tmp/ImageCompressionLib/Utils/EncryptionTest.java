package ImageCompressionLib.Utils;

import ImageCompressionLib.Containers.DataOpcOld;
import ImageCompressionLib.Containers.TripleDataOpcMatrix;
import ImageCompressionLib.Constants.State;
import ImageCompressionLib.ProcessingModules.ModuleOPC.ModuleOPC;
import ImageCompressionLib.Containers.TripleShortMatrix;
import ImageCompressionLib.Utils.Functions.Encryption;
import ImageCompressionLib.Containers.Type.Flag;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EncryptionTest {
    ModuleOPC moduleOPC, _moduleOPC;
    String key="dimas";
    TripleShortMatrix tripleShortMatrix =new TripleShortMatrix(200,200, State.RGB);
    Flag flag;
    @Before
    public void setUp() throws Exception {
        flag=new Flag();
        moduleOPC =new ModuleOPC(tripleShortMatrix,flag,true);
        _moduleOPC =new ModuleOPC(tripleShortMatrix,flag,true);
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
    private void AssertDataEqual(DataOpcOld[][] a, DataOpcOld[][] b){
        assertEquals(a.length,b.length);
        assertEquals(a[0].length,b[0].length);
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[0].length;j++){
                AssertDataEqual(a[i][j],b[i][j]);
            }
        }
    }
    private void AssertDataEqual(DataOpcOld a, DataOpcOld b){
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