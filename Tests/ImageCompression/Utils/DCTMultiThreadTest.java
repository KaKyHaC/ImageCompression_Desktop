package ImageCompression.Utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DCTMultiThreadTest {
    short[][] data,res;
    int size=DCTMultiThread.SIZEOFBLOCK;
    @Before
    public void setUp() throws Exception {
        data=new short[size][size];
        for(int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                data[i][j]=(short)(i*j);
            }
        }
    }

    @Test
    public void directDCT() throws Exception {
        res=DCTMultiThread.directDCT(data);
        res=DCTMultiThread.reverseDCT(res);
        assertArrayEquals(res,data);
    }

}