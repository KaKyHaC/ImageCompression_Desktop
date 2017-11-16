package ImageCompression.Objects;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static org.junit.Assert.*;

public class ImageTest {
    int size=200;
    BufferedImage bufferedImage;
    @Before
    public void setUp() throws Exception {
        bufferedImage=new BufferedImage(size,size,BufferedImage.TYPE_4BYTE_ABGR);
    }
    @Test
    public void TestGetSet()throws Exception{
        int val,res;
        Random random=new Random();
        for(int i=0;i<size;i++){
            val=random.nextInt(16000000);
            bufferedImage.setRGB(i,i,val);
            res=bufferedImage.getRGB(i,i);
            assertEquals(val,res);
        }
    }
    @Test
    public void TestColor()throws Exception{
        randomInit(bufferedImage);
        for(int i=0;i<size;i++){
            int res=bufferedImage.getRGB(i,i);
            int r,g,b;
            r=res>>16&0xFF;
            g=res>>8&0xFF;
            b=res&0xFF;
            Color color=new Color(res);

            assertEquals(r,color.getRed());
            assertEquals(g,color.getGreen());
            assertEquals(b,color.getBlue());
        }
    }
    @Test
    public void TestArrColor() throws Exception{
        randomInit(bufferedImage);
        int w=bufferedImage.getWidth();
        int h=bufferedImage.getHeight();
        int arr[]=bufferedImage.getRGB(0,0,w,h,null,0,w);
        for (int i = 0; i < w; i++) {
            for(int j=0;j<h;j++) {
                assertEquals(arr[i*h+j],bufferedImage.getRGB(i,j));
            }
        }
    }
    private void randomInit(BufferedImage bufferedImage) {
        Random random = new Random();
        int w=bufferedImage.getWidth();
        int h=bufferedImage.getHeight();
        for (int i = 0; i < w; i++) {
            for(int j=0;j<h;j++) {
                int val = random.nextInt(16000000);
                bufferedImage.setRGB(i, j, val);
            }
        }
    }


}