package ImageCompression.Objects;

import ImageCompression.Containers.Matrix;
import javafx.stage.FileChooser;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;

public class MyImageTest {
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
    public void TestZeroImage() throws Exception {
        testBufferdImage(bufferedImage);
    }
    @Test
    public void TestRandomImage() throws Exception{
        Random random=new Random();
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                bufferedImage.setRGB(i,j,random.nextInt(16000000));
            }
        }
        bufferedImage.setRGB(0,0,12345);
        testBufferdImageInRange(bufferedImage,2);
    }
    @Test
    public void TestRandomImageInRange2() throws Exception{
        Random random=new Random();
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                bufferedImage.setRGB(i,j,random.nextInt(16000000));
            }
        }
        testBufferdImageInRange(bufferedImage,2);
    }
    @Test
    public void TestMyValue() throws Exception{
        bufferedImage.setRGB(2,2,12323);
        bufferedImage.setRGB(5,5,1231123);
        bufferedImage.setRGB(21,8,145323);
        bufferedImage.setRGB(24,22,1223323);
        testBufferdImageInRange(bufferedImage,2);
    }

    private void testBufferdImage(BufferedImage bufferedImage) throws Exception {
        MyImage myImage=new MyImage(bufferedImage,flag);
        Matrix matrix=myImage.getYenlMatrix();
        MyImage myImage1=new MyImage(matrix);
        BufferedImage bufferedImage1=myImage1.getBufferedImage();

        int [] arr1=bufferedImage.getRGB(0,0,w,h,null,0,w);
        int [] arr2=bufferedImage1.getRGB(0,0,w,h,null,0,w);

        assertArrayEquals(arr1,arr2);
    }
    private void testBufferdImageInRange(BufferedImage bufferedImage,int range) throws Exception {
        MyImage myImage=new MyImage(bufferedImage,flag);
        Matrix matrix=myImage.getYenlMatrix();
        MyImage myImage1=new MyImage(matrix);
        BufferedImage bufferedImage1=myImage1.getBufferedImage();

        int [] arr1=bufferedImage.getRGB(0,0,w,h,null,0,w);
        int [] arr2=bufferedImage1.getRGB(0,0,w,h,null,0,w);

        assertArrayInRange(arr1,arr2,range);
    }

    private void assertArrayInRange(int[] ar1,int[]ar2,int delta){
        assertEquals(ar1.length,ar2.length);
        assertTrue(ar1.length>0);

        for(int i=0;i<ar1.length;i++) {
            boolean isEqual = false;
            boolean isR=false,isG=false,isB=false;

            int r1,g1,b1,r2,g2,b2;
            r1=ar1[i]>>16 &0xFF;
            g1=ar1[i]>>8 &0xFF;
            b1=ar1[i] &0xFF;
            r2=ar2[i]>>16 &0xFF;
            g2=ar2[i]>>8 &0xFF;
            b2=ar2[i] &0xFF;
            for (int d = -delta; d <= delta; d++) {
                if (r1 == (r2 + d))
                    isR = true;
                if (g1 == (g2 + d))
                    isG = true;
                if (b1 == (b2 + d))
                    isB = true;
            }
            isEqual=isR&&isG&&isB;
            assertTrue("in [" + i + "] value " + ar1[i] + "!=" + ar2[i], isEqual);
        }

    }
    @Test
    public void TestRgbYbr(){
        int size=200;
        Random random=new Random();
        int [] arr=new int[size];
        for(int i=0;i<size;i++){
            arr[i]=random.nextInt(16000000);
        }
        TestRGBtoYBRtoRGB(arr);
    }
    private void TestRGBtoYBRtoRGB(int [] arr){
        for(int i=0;i<arr.length;i++){
            int pixelColor = arr[i];
            // получим цвет каждого пикселя
            double pixelRed = ((pixelColor)>>16&0xFF);
            double pixelGreen= ((pixelColor)>>8&0xFF);
            double pixelBlue=((pixelColor)&0xFF);


               /* double vy=((77.0/256.0)*pixelRed+(150.0/256.0)*pixelGreen+(29.0/256)*pixelBlue);
                double vcb=(((44.0/256.0)*pixelRed-(87.0/256.0)*pixelGreen+(131.0/256)*pixelBlue)+128.0);
                double vcr=(((131.0/256.0)*pixelRed-(110.0/256.0)*pixelGreen-(21.0/256)*pixelBlue)+128.0);*/

            double vy = (0.299 * pixelRed) + (0.587 * pixelGreen) + (0.114 * pixelBlue);
            double vcb = 128 - (0.168736 * pixelRed) - (0.331264 * pixelGreen) + (0.5 * pixelBlue);
            double vcr = 128 + (0.5 * pixelRed) - (0.418688 * pixelGreen) - (0.081312 * pixelBlue);

            //add
//                if(vy%1>0.5)
//                    vy++;
            //
            double r,g,b;
            r=(vy+1.402*(vcr-128));
            g=(vy-0.34414*(vcb-128)-0.71414*(vcr-128));
            b=(vy+1.772*(vcb-128));

            //add
            if(r%1>=0.5)
                r=(short)++r;
            if(g%1>=0.5)
                g=(short)++g;
            if(b%1>=0.5)
                b=(short)++b;
            //

            if(g<0)g=0;//new
            if(r<0)r=0;
            if(b<0)b=0;

            if(r>255)r=255;
            if(g>255)g=255;
            if(b>255)b=255;

            int val =((int)r<<16) | ((int)g<<8) | (int)b; //for rgb

            assertEquals(val,pixelColor);
        }
    }

}