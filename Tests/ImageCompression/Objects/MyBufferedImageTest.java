package ImageCompression.Objects;

import ImageCompression.Containers.Matrix;
import ImageCompression.Utils.Objects.Flag;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.Random;

import static org.junit.Assert.*;

public class MyBufferedImageTest {
    BufferedImage bufferedImage;
    Flag flag=new Flag("0");
    int w,h;

    @Before
    public void BeforeEach(){
        bufferedImage= new BufferedImage(200,200,BufferedImage.TYPE_3BYTE_BGR);
        w=bufferedImage.getWidth();
        h=bufferedImage.getHeight();
    }
    @Test
    public void TestDefault()throws Exception{
        randomInit(bufferedImage);

        MyBufferedImage myBufferedImage=new MyBufferedImage(bufferedImage,flag);
        Matrix matrix=myBufferedImage.getYCbCrMatrix();

        MyBufferedImage myBufferedImage1=new MyBufferedImage(matrix);
        BufferedImage bufferedImage1=myBufferedImage1.getBufferedImage();

        int [] arr1=bufferedImage.getRGB(0,0,w,h,null,0,w);
        int [] arr2=bufferedImage1.getRGB(0,0,w,h,null,0,w);

        assertArrayInRange(arr1,arr2,2);
//        forEach(bufferedImage.getWidth(),bufferedImage.getHeight(),((x, y) -> {
//            assertEquals(bufferedImage.getRGB(x,y),bufferedImage1.getRGB(x,y));
//        }));

    }


    public static void randomInit(BufferedImage bufferedImage) {
            Random random = new Random();
            int w = bufferedImage.getWidth();
            int h = bufferedImage.getHeight();
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int val = random.nextInt(16000000);
                    bufferedImage.setRGB(i, j, val);
                }
            }
        }
    public static void assertArrayInRange(int[] ar1, int[] ar2, int delta) {
            assertEquals(ar1.length, ar2.length);
            assertTrue(ar1.length > 0);

            for (int i = 0; i < ar1.length; i++) {
                boolean isEqual = false;
                boolean isR = false, isG = false, isB = false;

                int r1, g1, b1, r2, g2, b2;
                r1 = ar1[i] >> 16 & 0xFF;
                g1 = ar1[i] >> 8 & 0xFF;
                b1 = ar1[i] & 0xFF;
                r2 = ar2[i] >> 16 & 0xFF;
                g2 = ar2[i] >> 8 & 0xFF;
                b2 = ar2[i] & 0xFF;
                for (int d = -delta; d <= delta; d++) {
                    if (r1 == (r2 + d))
                        isR = true;
                    if (g1 == (g2 + d))
                        isG = true;
                    if (b1 == (b2 + d))
                        isB = true;
                }
                isEqual = isR && isG && isB;
                assertTrue("in [" + i + "] value " + ar1[i] + "!=" + ar2[i], isEqual);
            }

        }

    @FunctionalInterface
    interface Loopable{
        void doInLoop(int x,int y);
    }
    private void forEach(int w,int h,MyBufferedImage.Loopable fun){
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                fun.doInLoop(i,j);
            }
        }
    }

}