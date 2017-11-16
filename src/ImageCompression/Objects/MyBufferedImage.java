package ImageCompression.Objects;

import ImageCompression.Containers.Matrix;
import ImageCompression.Containers.State;

import java.awt.image.BufferedImage;

public class MyBufferedImage {
    private static final int SIZEOFBLOCK = 8;
    private Matrix matrix;
    private BufferedImage bitmap;


    public MyBufferedImage(BufferedImage _b, Flag flag) {
        bitmap = _b;
        matrix = new Matrix(bitmap.getWidth(), bitmap.getHeight(), flag);
        matrix.state = State.bitmap;
    }
    public MyBufferedImage(Matrix matrix) throws Exception {
        this.matrix = matrix;
        bitmap = new BufferedImage(matrix.Width,matrix.Height,BufferedImage.TYPE_4BYTE_ABGR);
    }

    //TODO string constructor

    private void FromBufferedImageToYCbCr() {

        if (matrix.state == State.bitmap)
        {
            forEach(bitmap.getWidth(),bitmap.getHeight(),(x, y) -> {
                int pixelColor=bitmap.getRGB(x,y);
                // получим цвет каждого пикселя
                double pixelRed = ((pixelColor)>>16&0xFF);
                double pixelGreen= ((pixelColor)>>8&0xFF);
                double pixelBlue=((pixelColor)&0xFF);

                double vy = (0.299 * pixelRed) + (0.587 * pixelGreen) + (0.114 * pixelBlue);
                double vcb = 128 - (0.168736 * pixelRed) - (0.331264 * pixelGreen) + (0.5 * pixelBlue);
                double vcr = 128 + (0.5 * pixelRed) - (0.418688 * pixelGreen) - (0.081312 * pixelBlue);

                //15.11
                   if(vy%1>=0.5)
                       vy++;
                   if(vcb%1>=0.5)
                       vcb++;
                   if(vcr%1>=0.5)
                       vcr++;


                matrix.a[x][y] = (short) vy;
                matrix.b[x][y] = (short) vcb;
                matrix.c[x][y] = (short) vcr;

            });

            matrix.state = State.YBR;
        }

    }
    private void FromYCbCrToBufferedImage(){
        if(matrix.state==State.YBR){
            final int pixelAlpha=255; //for argb

            forEach(matrix.Width,matrix.Height,(x, y) -> {
                double r,g,b;
                r=(matrix.a[x][y]+1.402*(matrix.c[x][y]-128));
                g=(matrix.a[x][y]-0.34414*(matrix.b[x][y]-128)-0.71414*(matrix.c[x][y]-128));
                b=(matrix.a[x][y]+1.772*(matrix.b[x][y]-128));

                if(g<0)g=0;//new
                if(r<0)r=0;
                if(b<0)b=0;

                if(r>255)r=255;
                if(g>255)g=255;
                if(b>255)b=255;

                int pixelBlue=(int)b&0xFF;
                int pixelRed=(int)r&0xFF;
                int pixelGreen=(int)g&0xFF;

                //add
                if(r%1>=0.5)
                    pixelRed++;
                if(g%1>=0.5)
                    pixelGreen++;
                if(b%1>=0.5)
                    pixelBlue++;
                //


                int val = (pixelAlpha<<24)| (pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for argb
//                    int val =(pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for rgb

                // полученный результат вернём в BufferedImage
                bitmap.setRGB(x, y, val);
            });
            matrix.state=State.bitmap;
        }
    }

    private void FromBufferedImageToRGB() {

        if(matrix.state==State.bitmap) {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for (int i = 0; i < Width; i++) {
                for (int j = 0; j < Height; j++) {
//                    int pixelColor = rgb[i*Height + j];
                    int pixelColor=bitmap.getRGB(i,j);
                    // получим цвет каждого пикселя
                    matrix.a[i][j] = (short) ((pixelColor)>>16&0xFF);
                    matrix.b[i][j] = (short) ((pixelColor)>>8&0xFF);
                    matrix.c[i][j] = (short) ((pixelColor)&0xFF);

                }
            }
            matrix.state=State.RGB;
        }
    }
    private void FromRGBtoBufferedImage(){
        if(matrix.state==State.RGB)
        {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for(int i=0;i<Width;i++)
            {
                for(int j=0;j<Height;j++)
                {

                    int pixelAlpha=255; //for argb
                    int pixelBlue=matrix.c[i][j]&0xFF;
                    int pixelRed=matrix.a[i][j]&0xFF;
                    int pixelGreen=matrix.b[i][j]&0xFF;
                    int val =(pixelAlpha<<24)| (pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for argb
//                    int val =(pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for rgb

                    // полученный результат вернём в BufferedImage
                    bitmap.setRGB(i, j, val);
                }
            }
            matrix.state=State.bitmap;
        }
    }

    private void FromYBRtoRGB(){

        if(matrix.state==State.YBR) {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for(int i=0;i<Width;i++)
            {
                for(int j=0;j<Height;j++) {
                    double r, g, b;
                    r = (matrix.a[i][j] + 1.402 * (matrix.c[i][j] - 128));
                    g = (matrix.a[i][j] - 0.34414 * (matrix.b[i][j] - 128) - 0.71414 * (matrix.c[i][j] - 128));
                    b = (matrix.a[i][j] + 1.772 * (matrix.b[i][j] - 128));
                    //add
                    if (r % 1 >= 0.5)
                        r = (short) ++r;
                    if (g % 1 >= 0.5)
                        g = (short) ++g;
                    if (b % 1 >= 0.5)
                        b = (short) ++b;
                    //

                    if (g < 0) g = 0;//new
                    if (r < 0) r = 0;
                    if (b < 0) b = 0;

                    if (r > 255) r = 255;
                    if (g > 255) g = 255;
                    if (b > 255) b = 255;
                    matrix.a[i][j] = (short) r;
                    matrix.b[i][j] = (short) g;
                    matrix.c[i][j] = (short) b;
                }
            }
            matrix.state=State.RGB;
        }
    }
    private void FromRGBtoYBR() {

        if (matrix.state == State.RGB){
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for (int i = 0; i < Width; i++) {
                for (int j = 0; j < Height; j++) {
                    // получим цвет каждого пикселя
                    double pixelRed = matrix.a[i][j];
                    double pixelGreen = matrix.b[i][j];
                    double pixelBlue = matrix.c[i][j];

                    double vy = (0.299 * pixelRed) + (0.587 * pixelGreen) + (0.114 * pixelBlue);
                    double vcb = 128 - (0.168736 * pixelRed) - (0.331264 * pixelGreen) + (0.5 * pixelBlue);
                    double vcr = 128 + (0.5 * pixelRed) - (0.418688 * pixelGreen) - (0.081312 * pixelBlue);
                    //15.11
//                    if(vy%1>=0.5)
//                        vy++;
//                    if(vcb%1>=0.5)
//                        vcb++;
//                    if(vcr%1>=0.5)
//                        vcr++;

                    matrix.a[i][j] = (short) vy;
                    matrix.b[i][j] = (short) vcb;
                    matrix.c[i][j] = (short) vcr;
                }
            }
            matrix.state=State.YBR;
        }
    }

    private void PixelEnlargement(){
        if (matrix.state == State.YBR && matrix.f.isEnlargement()) {
            int cWidth=matrix.Width/2;
            int cHeight=matrix.Height/2;
            short[][] enlCb=new short[cWidth][cHeight];
            short[][] enlCr=new short[cWidth][cHeight];
            for (int i = 0; i < cWidth; i++) {
                for (int j = 0; j < cHeight; j++) {
                    enlCb[i][j] = (short)( (matrix.b[i * 2][j * 2] + matrix.b[i * 2 + 1][j * 2] + matrix.b[i * 2][j * 2 + 1] + matrix.b[i * 2 + 1][j * 2 + 1])/4);
                    enlCr[i][j] = (short)( (matrix.c[i * 2][j * 2] + matrix.c[i * 2 + 1][j * 2] + matrix.c[i * 2][j * 2 + 1] + matrix.c[i * 2 + 1][j * 2 + 1])/4);
                }
            }
            matrix.b=enlCb;
            matrix.c=enlCr;
            matrix.state=State.Yenl;
        }

    }
    private void PixelRestoration() {

        if (matrix.state == State.Yenl && matrix.f.isEnlargement()) {
            int cWidth=matrix.Width/2;
            int cHeight=matrix.Height/2;
            int Width=matrix.Width;
            int Height=matrix.Height;
            short[][]Cb=new short[Width][Height];
            short[][]Cr=new short[Width][Height];
            for (int i = 0; i < cWidth; i++) {
                for (int j = 0; j < cHeight; j++) {
                    Cb[i * 2][j * 2] = Cb[i * 2 + 1][j * 2] = Cb[i * 2][j * 2 + 1] = Cb[i * 2 + 1][j * 2 + 1] = matrix.b[i][j];
                    Cr[i * 2][j * 2] = Cr[i * 2 + 1][j * 2] = Cr[i * 2][j * 2 + 1] = Cr[i * 2 + 1][j * 2 + 1] = matrix.c[i][j];
                }
            }
            matrix.b=Cb;
            matrix.c=Cr;

            matrix.state=State.YBR;
        }
    }

    private void minus128(short [][] arr){
        for(int i=0;i<arr.length;i++)
        {
            for(int j=0;j<arr[0].length;j++){
                arr[i][j]-=128;
            }
        }
    }
    private void plus128(short [][] arr){
        for(int i=0;i<arr.length;i++)
        {
            for(int j=0;j<arr[0].length;j++){
                arr[i][j]+=128;
            }
        }
    }

    private void minus128(){
        minus128(matrix.a);
        minus128(matrix.b);
        minus128(matrix.c);
    }
    private void plus128(){
        plus128(matrix.a);
        plus128(matrix.b);
        plus128(matrix.c);
    }




    public BufferedImage getBufferedImage() {
        switch(matrix.state)
        {
            case RGB: FromRGBtoBufferedImage();
                break;
            case YBR: FromYCbCrToBufferedImage();
                break;
            case Yenl:PixelRestoration();FromYCbCrToBufferedImage();
                break;
            case bitmap:
                break;
            default:
                return null;
        }

        return bitmap;
    }

    public Matrix getYCbCrMatrix() {
        switch (matrix.state)
        {
            case RGB: FromRGBtoYBR();
                break;
            case YBR:
                break;
            case Yenl:PixelRestoration();
                break;
            case bitmap:FromBufferedImageToYCbCr();
                break;
            default:return null;

        }

        return matrix;
    }

    public Matrix getRGBMatrix() {
        switch (matrix.state)
        {
            case RGB:
                break;
            case YBR:FromYBRtoRGB();
                break;
            case Yenl:PixelRestoration();FromYBRtoRGB();
                break;
            case bitmap:FromBufferedImageToRGB();
                break;
            default:return null;

        }

        return matrix;
    }

    public Matrix getYenlMatrix()    {
        switch (matrix.state)
        {
            case RGB: FromRGBtoYBR();PixelEnlargement();
                break;
            case YBR:PixelEnlargement();
                break;
            case Yenl:
                break;
            case bitmap:FromBufferedImageToYCbCr();PixelEnlargement();
                break;
            default:return null;

        }
        return matrix;
    }


    @FunctionalInterface
    interface Loopable{
        void doInLoop(int x,int y);
    }
    private void forEach(int w,int h,Loopable fun){
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                fun.doInLoop(i,j);
            }
        }
    }
}
