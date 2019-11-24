//package ImageCompressionLib.Utils;
//
//import ImageCompressionLib.Data.Enumerations.State;
//import ImageCompressionLib.Utils.Objects.EncryptionUtils;
//import ImageCompressionLib.Data.Type.Flag;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class EncryptionTest {
//    ModuleOpcOld moduleOpcOld, _moduleOpcOld;
//    String key="dimas";
//    TripleShortMatrixOld tripleShortMatrixOld =new TripleShortMatrixOld(200,200, State.RGB);
//    Flag flag;
//    @Before
//    public void setUp() throws Exception {
//        flag=new Flag();
//        moduleOpcOld =new ModuleOpcOld(tripleShortMatrixOld,flag,true);
//        _moduleOpcOld =new ModuleOpcOld(tripleShortMatrixOld,flag,true);
//    }
//
//    @Test
//    public void TestUtils(){
//        AssertBoxEquals(moduleOpcOld.getBoxOfOpc(true), _moduleOpcOld.getBoxOfOpc(true));
//    }
//
//    @Test
//    public void encode() throws Exception {
//        EncryptionUtils.encode(moduleOpcOld.getBoxOfOpc(true),key);
//        EncryptionUtils.encode(moduleOpcOld.getBoxOfOpc(true),key);
//        AssertBoxEquals(moduleOpcOld.getBoxOfOpc(true), _moduleOpcOld.getBoxOfOpc(true));
//    }
//    private void AssertBoxEquals(TripleDataOpcMatrixOld a, TripleDataOpcMatrixOld b){
//        AssertDataEqual(a.getA(),b.getA());
//        AssertDataEqual(a.getB(),b.getB());
//        AssertDataEqual(a.getC(),b.getC());
//    }
//    private void AssertDataEqual(DataOpcOld[][] a, DataOpcOld[][] b){
//        assertEquals(a.length,b.length);
//        assertEquals(a[0].length,b[0].length);
//        for(int i=0;i<a.length;i++){
//            for(int j=0;j<a[0].length;j++){
//                AssertDataEqual(a[i][j],b[i][j]);
//            }
//        }
//    }
//    private void AssertDataEqual(DataOpcOld a, DataOpcOld b){
//        assertEquals(a.getDC(),b.getDC());
//        assertEquals(a.getVectorCode(),b.getVectorCode());
//        assertEquals(a.getSign(),b.getSign());
//        assertEquals(a.getN(),b.getN());
////        assertEquals(a.base,b.base);
//        for(int i=0;i<a.getBase().length;i++){
//            assertEquals(a.getBase()[i],b.getBase()[i]);
//        }
//    }
//
//}