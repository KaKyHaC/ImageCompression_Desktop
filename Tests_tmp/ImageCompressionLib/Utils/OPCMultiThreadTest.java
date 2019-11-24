//package ImageCompressionLib.Utils;
//
//import ImageCompressionLib.Data.Type.Flag;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class OPCMultiThreadTest {
//    short[][] data,res;
//    int size= OPCMultiThread.SIZEOFBLOCK;
//    Flag flag;
//    @Before
//    public void setUp() throws Exception {
//        data=new short[size][size];
//        for(int i=0;i<size;i++){
//            for (int j=0;j<size;j++){
//                data[i][j]=(short)(i*j);
//            }
//        }
//
//        flag=new Flag("0");
//    }
//
//    @Test
//    public void TestOPC() throws Exception {
//        DataOpcOld DataOpcOld =OPCMultiThread.getDataOpc(data,flag);
//        res=OPCMultiThread.getDataOrigin(DataOpcOld,flag);
//
//        assertArrayEquals(data,res);
//    }
//
//    @Test
//    public void TestOPCwithFindedBase() throws Exception {
//        DataOpcOld base=OPCMultiThread.findBase(data,flag);
//        DataOpcOld DataOpcOld =OPCMultiThread.directOPCwithFindedBase(data,base,flag);
//        res=OPCMultiThread.getDataOrigin(DataOpcOld,flag);
//
//        assertArrayEquals(data,res);
//    }
//
//    @Test
//    public void TestJpegMatrix(){
////        param.setLongCode(true);
//        flag.setChecked(Flag.Parameter.DC,true);
//        flag.setChecked(Flag.Parameter.OneFile,true);
//
//        short[][] jpg={{-26,-3,-6,2,2,0,0,0},
//                        {1,-2,-4,0,0,0,0,0},
//                        {-3,1,5,-1,-1,0,0,0},
//                        {-4,1,2,-1,0,0,0,0},
//                        {1,0,0,0,0,0,0,0},
//                        {0,0,0,0,0,0,0,0},
//                        {0,0,0,0,0,0,0,0},
//                        {0,0,0,0,0,0,0,0}};
//        short[][] cpy={{-26,-3,-6,2,2,0,0,0},
//                        {1,-2,-4,0,0,0,0,0},
//                        {-3,1,5,-1,-1,0,0,0},
//                        {-4,1,2,-1,0,0,0,0},
//                        {1,0,0,0,0,0,0,0},
//                        {0,0,0,0,0,0,0,0},
//                        {0,0,0,0,0,0,0,0},
//                        {0,0,0,0,0,0,0,0}};
//
//        DataOpcOld dopc=OPCMultiThread.getDataOpc(jpg,flag);
//        System.out.println("dopc after jpg size ="+dopc.getByteSize(flag));
//
//        short[][]res=OPCMultiThread.getDataOrigin(dopc,flag);
//        assertArrayEquals(res,cpy);
//    }
//
//}