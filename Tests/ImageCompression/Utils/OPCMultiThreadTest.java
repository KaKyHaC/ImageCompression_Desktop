package ImageCompression.Utils;

import ImageCompression.Objects.DataOPC;
import ImageCompression.Objects.Flag;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OPCMultiThreadTest {
    short[][] data,res;
    int size=OPCMultiThread.SIZEOFBLOCK;
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

}