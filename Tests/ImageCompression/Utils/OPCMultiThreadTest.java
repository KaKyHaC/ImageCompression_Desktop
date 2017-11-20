package ImageCompression.Utils;

import ImageCompression.Containers.Matrix;
import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Objects.Flag;
import ImageCompression.Utils.Functions.OPCMultiThread;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OPCMultiThreadTest {
    short[][] data,res;
    int size= OPCMultiThread.SIZEOFBLOCK;
    Flag flag;
    @Before
    public void setUp() throws Exception {
        data=new short[size][size];
        for(int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                data[i][j]=(short)(i*j);
            }
        }

        flag=new Flag("0");
    }

    @Test
    public void TestOPC() throws Exception {
        DataOPC dataOPC=OPCMultiThread.getDataOPC(data,flag);
        res=OPCMultiThread.getDataOrigin(dataOPC,flag);

        assertArrayEquals(data,res);
    }

    @Test
    public void TestOPCwithFindedBase() throws Exception {
        DataOPC base=OPCMultiThread.findBase(data,flag);
        DataOPC dataOPC=OPCMultiThread.directOPCwithFindedBase(data,base,flag);
        res=OPCMultiThread.getDataOrigin(dataOPC,flag);

        assertArrayEquals(data,res);
    }

    @Test
    public void TestJpegMatrix(){
//        flag.setLongCode(true);
        flag.setDC(true);
        flag.setOneFile(true);

        short[][] jpg={{-26,-3,-6,2,2,0,0,0},
                        {1,-2,-4,0,0,0,0,0},
                        {-3,1,5,-1,-1,0,0,0},
                        {-4,1,2,-1,0,0,0,0},
                        {1,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0}};
        short[][] cpy={{-26,-3,-6,2,2,0,0,0},
                        {1,-2,-4,0,0,0,0,0},
                        {-3,1,5,-1,-1,0,0,0},
                        {-4,1,2,-1,0,0,0,0},
                        {1,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0}};

        DataOPC dopc=OPCMultiThread.getDataOPC(jpg,flag);
        System.out.println("dopc after jpg size ="+dopc.getByteSize(flag));

        short[][]res=OPCMultiThread.getDataOrigin(dopc,flag);
        assertArrayEquals(res,cpy);
    }

}