//package ImageCompressionLib.Data;
//
//import ImageCompressionLib.Constants.State;
//import ImageCompressionLib.Data.Type.Flag;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.Date;
//
//import static org.junit.Assert.*;
//
//public class ModuleOpcOldTest {
//    int size=200;
//    Flag flag=new Flag("0");
//    TripleShortMatrixOld tripleShortMatrixOld =new TripleShortMatrixOld(size,size, State.RGB);
//    @Before
//    public void setUp() throws Exception {
//        for(int i=0;i<size;i++){
//            for(int j=0;j<size;j++){
//                tripleShortMatrixOld.getA()[i][j]=(short)(i+j);
//                tripleShortMatrixOld.getB()[i][j]=(short)(i+j);
//                tripleShortMatrixOld.getC()[i][j]=(short)(i+j);
//            }
//        }
//    }
//
//    @Test
//    public void TestDefault(){
//        TripleDataOpcMatrixOld tripleDataOpcMatrixOld =new ModuleOpcOld(tripleShortMatrixOld,flag,true).getBoxOfOpc(true);
//        DataOpcOld[][] a,b,c;
//        ModuleOpcOld moduleOpcOld1 =new ModuleOpcOld(tripleDataOpcMatrixOld, flag,true);
//        TripleShortMatrixOld res= moduleOpcOld1.getMatrix(true);
//
//        AssertMatrixInRange(tripleShortMatrixOld,res,1,true);
//    }
//    @Test
//    public void TestTime(){
//        ModuleOpcOld moduleOpcOld =new ModuleOpcOld(tripleShortMatrixOld,flag,true);
//        Date t1=new Date();
//        moduleOpcOld.directOPCMultiThreads();
//        moduleOpcOld.reverseOPCMultiThreads();
//        Date t2=new Date();
//        System.out.println("in multi threads t="+(t2.getTime()-t1.getTime()));
//
//        Date t3=new Date();
//        moduleOpcOld.directOPC();
//        moduleOpcOld.reverseOPC();
//        Date t4=new Date();
//        System.out.println("in one thread t="+(t4.getTime()-t3.getTime()));
//
//        Date t5=new Date();
//        moduleOpcOld.directOpcGlobalBase(5,5);
//        moduleOpcOld.reverseOPC();
//        Date t6=new Date();
//        System.out.println("Global Base t="+(t6.getTime()-t5.getTime()));
//
//        assertTrue((t4.getTime()-t3.getTime())>(t2.getTime()-t1.getTime()));
//    }
//
//    @Test
//    public void TestAssertFun(){
//        TripleShortMatrixOld a=new TripleShortMatrixOld(size,size,State.RGB);
//        TripleShortMatrixOld b=new TripleShortMatrixOld(size,size,State.RGB);
//        AssertMatrixInRange(a,b,0,true);
//
//        a.getA()[0][0]=1;
//        AssertMatrixInRange(a,b,2,true);
//
//        a.getA()[0][0]=200;
//        a.getC()[0][0]=200;
//        a.getB()[0][0]=200;
//        AssertMatrixInRange(a,b,1,false);
//    }
//
//    private void AssertMatrixInRange(TripleShortMatrixOld m1, TripleShortMatrixOld m2, int delta, Boolean inRange){
//        AssertArrayArrayInRange(m1.getA(), m2.getA(),delta,inRange);
//        AssertArrayArrayInRange(m1.getB(), m2.getB(),delta,inRange);
//        AssertArrayArrayInRange(m1.getC(), m2.getC(),delta,inRange);
//    }
//    private void AssertArrayArrayInRange(short[][] a,short[][] b,int delta,boolean inRange){
//        boolean total=true;
//        for(int i=0;i<a.length;i++){
//            for(int j=0;j<a[0].length;j++){
//                boolean isEqual=false;
//                for(int d=-delta;d<=delta;d++){
//                    if(a[i][j]==b[i][j]+d)
//                        isEqual=true;
//                }
//                if(inRange)
//                    assertTrue(isEqual);
//                total=total&&isEqual;
//            }
//        }
//        if(inRange)
//            assertTrue(total);
//        else
//            assertFalse(total);
//    }
//
//
//}
