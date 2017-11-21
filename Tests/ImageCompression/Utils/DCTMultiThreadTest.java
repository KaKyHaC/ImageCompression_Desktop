package ImageCompression.Utils;

import ImageCompression.Utils.Functions.DCTMultiThread;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class DCTMultiThreadTest {
    short[][] data,res;
    int size= DCTMultiThread.SIZEOFBLOCK;
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
    public void TestDctEquals() throws Exception {
        res=DCTMultiThread.directDCT(data);
        res=DCTMultiThread.reverseDCT(res);

//        assertArrayEquals(res,data);
        assertArrayInRange(data,res,0);
    }
    @Test
    public void TestDCTinRange1() throws Exception {
        res=DCTMultiThread.directDCT(data);
        res=DCTMultiThread.reverseDCT(res);

//        assertArrayEquals(res,data);
        assertArrayInRange(data,res,1);
    }
    @Test
    public void MultiDCT() throws Exception{
        Random random=new Random();
        for(int i=0;i<10;i++){
            for(int x=0;x<size;x++){
                for(int y=0;y<size;y++){
                    data[x][y]=(short)random.nextInt(255);
                }
            }
            res=DCTMultiThread.directDCT(data);
            res=DCTMultiThread.reverseDCT(res);

            assertArrayInRange(data,res,2);
        }
    }

    private void assertArrayInRange(short[][] a,short[][] b,int delta){
        assertEquals(a.length,b.length);
        assertTrue(a.length>0);
        assertEquals(a[0].length,b[0].length);

        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[0].length;j++){
                boolean isEqual=false;
                for(int d=-delta;d<=delta;d++){
                    if(a[i][j]==(b[i][j]+d))
                        isEqual=true;
                }
                assertTrue("in ["+i+"]["+j+"] value "+a[i][j]+"!="+b[i][j],isEqual);
            }
        }
    }

}