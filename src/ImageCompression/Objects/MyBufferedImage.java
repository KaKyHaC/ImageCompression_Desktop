package ImageCompression.Objects;

import ImageCompression.Containers.Matrix;
import ImageCompression.Constants.State;
import ImageCompression.Utils.Objects.ByteVector;
import ImageCompression.Utils.Objects.Flag;
import ImageCompression.Utils.Objects.TimeManager;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyBufferedImage {
    private static final int SIZEOFBLOCK = 8;
    private Matrix matrix;
    private BufferedImage bitmap;


    public MyBufferedImage(BufferedImage _b, Flag flag) {
        bitmap = _b;
        matrix = new Matrix(bitmap.getWidth(), bitmap.getHeight(), flag,State.bitmap);
    }
    public MyBufferedImage(Matrix matrix) throws Exception {
        this.matrix = matrix;
        bitmap = new BufferedImage(matrix.getWidth(), matrix.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
    }
    public MyBufferedImage(ByteVector vector){
        this.matrix=getMatrixFromByteVector(vector);
        bitmap = new BufferedImage(matrix.getWidth(), matrix.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
    }

    //TODO string constructor

    private void FromBufferedImageToYCbCrParallelMatrix() {
        if (matrix.getState() == State.bitmap)
        {
            int w=bitmap.getWidth();
            int h=bitmap.getHeight();

            ExecutorService executorService = Executors.newFixedThreadPool(4);
            Future[] futures=new Future[4];

            futures[0] = executorService.submit(()-> imageToYbrTask(0,0,w/2,h/2));
            futures[1] = executorService.submit(()-> imageToYbrTask(w/2,0,w,h));
            futures[2] = executorService.submit(()-> imageToYbrTask(0,h/2,w,h));
            futures[3] = executorService.submit(()-> imageToYbrTask(w/2,h/2,w,h));

            for (Future future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            matrix.setState(State.YBR);
        }

    }
    private void FromBufferedImageToYCbCr() {

        if (matrix.getState() == State.bitmap)
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
//                   if(vy%1>=0.5)
//                       vy++;
//                   if(vcb%1>=0.5)
//                       vcb++;
//                   if(vcr%1>=0.5)
//                       vcr++;


                matrix.getA()[x][y] = (short) vy;
                matrix.getB()[x][y] = (short) vcb;
                matrix.getC()[x][y] = (short) vcr;

            });

            matrix.setState(State.YBR);
        }

    }
    private void FromYCbCrToBufferedImage(){
        if(matrix.getState() ==State.YBR){
            final int pixelAlpha=255; //for argb

            forEach(matrix.getWidth(), matrix.getHeight(),(x, y) -> {
                double r,g,b;
                r=(matrix.getA()[x][y]+1.402*(matrix.getC()[x][y]-128));
                g=(matrix.getA()[x][y]-0.34414*(matrix.getB()[x][y]-128)-0.71414*(matrix.getC()[x][y]-128));
                b=(matrix.getA()[x][y]+1.772*(matrix.getB()[x][y]-128));

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
//                if(r%1>=0.5)
//                    pixelRed++;
//                if(g%1>=0.5)
//                    pixelGreen++;
//                if(b%1>=0.5)
//                    pixelBlue++;
                //


//              int val = (pixelAlpha<<24)| (pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for argb
                int val =(pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for rgb

                // полученный результат вернём в BufferedImage
                bitmap.setRGB(x, y, val);
            });
            matrix.setState(State.bitmap);
        }
    }

    private void FromBufferedImageToRGB() {

        if(matrix.getState() ==State.bitmap) {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for (int i = 0; i < Width; i++) {
                for (int j = 0; j < Height; j++) {
//                    int pixelColor = rgb[i*Height + j];
                    int pixelColor=bitmap.getRGB(i,j);
                    // получим цвет каждого пикселя
                    matrix.getA()[i][j] = (short) ((pixelColor)>>16&0xFF);
                    matrix.getB()[i][j] = (short) ((pixelColor)>>8&0xFF);
                    matrix.getC()[i][j] = (short) ((pixelColor)&0xFF);

                }
            }
            matrix.setState(State.RGB);
        }
    }
    private void FromRGBtoBufferedImage(){
        if(matrix.getState() ==State.RGB)
        {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for(int i=0;i<Width;i++)
            {
                for(int j=0;j<Height;j++)
                {

                    int pixelAlpha=255; //for argb
                    int pixelBlue= matrix.getC()[i][j]&0xFF;
                    int pixelRed= matrix.getA()[i][j]&0xFF;
                    int pixelGreen= matrix.getB()[i][j]&0xFF;
//                    int val =(pixelAlpha<<24)| (pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for argb
                    int val =(pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for rgb

                    // полученный результат вернём в BufferedImage
                    bitmap.setRGB(i, j, val);
                }
            }
            matrix.setState(State.bitmap);
        }
    }

    private void FromYBRtoRGB(){

        if(matrix.getState() ==State.YBR) {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for(int i=0;i<Width;i++)
            {
                for(int j=0;j<Height;j++) {
                    double r, g, b;
                    r = (matrix.getA()[i][j] + 1.402 * (matrix.getC()[i][j] - 128));
                    g = (matrix.getA()[i][j] - 0.34414 * (matrix.getB()[i][j] - 128) - 0.71414 * (matrix.getC()[i][j] - 128));
                    b = (matrix.getA()[i][j] + 1.772 * (matrix.getB()[i][j] - 128));
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
                    matrix.getA()[i][j] = (short) r;
                    matrix.getB()[i][j] = (short) g;
                    matrix.getC()[i][j] = (short) b;
                }
            }
            matrix.setState(State.RGB);
        }
    }
    private void FromRGBtoYBR() {

        if (matrix.getState() == State.RGB){
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for (int i = 0; i < Width; i++) {
                for (int j = 0; j < Height; j++) {
                    // получим цвет каждого пикселя
                    double pixelRed = matrix.getA()[i][j];
                    double pixelGreen = matrix.getB()[i][j];
                    double pixelBlue = matrix.getC()[i][j];

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

                    matrix.getA()[i][j] = (short) vy;
                    matrix.getB()[i][j] = (short) vcb;
                    matrix.getC()[i][j] = (short) vcr;
                }
            }
            matrix.setState(State.YBR);
        }
    }

    private void PixelEnlargement(){
        if (matrix.getState() == State.YBR && matrix.getF().isEnlargement()) {
            int cWidth= matrix.getWidth() /2;
            int cHeight= matrix.getHeight() /2;
            short[][] enlCb=new short[cWidth][cHeight];
            short[][] enlCr=new short[cWidth][cHeight];
            for (int i = 0; i < cWidth; i++) {
                for (int j = 0; j < cHeight; j++) {
                    enlCb[i][j] = (short)( (matrix.getB()[i * 2][j * 2] + matrix.getB()[i * 2 + 1][j * 2] + matrix.getB()[i * 2][j * 2 + 1] + matrix.getB()[i * 2 + 1][j * 2 + 1])/4);
                    enlCr[i][j] = (short)( (matrix.getC()[i * 2][j * 2] + matrix.getC()[i * 2 + 1][j * 2] + matrix.getC()[i * 2][j * 2 + 1] + matrix.getC()[i * 2 + 1][j * 2 + 1])/4);
                }
            }
            matrix.setB(enlCb);
            matrix.setC(enlCr);
            matrix.setState(State.Yenl);
        }

    }
    private void PixelRestoration() {

        if (matrix.getState() == State.Yenl && matrix.getF().isEnlargement()) {
            int cWidth= matrix.getWidth() /2;
            int cHeight= matrix.getHeight() /2;
            int Width= matrix.getWidth();
            int Height= matrix.getHeight();
            short[][]Cb=new short[Width][Height];
            short[][]Cr=new short[Width][Height];
            for (int i = 0; i < cWidth; i++) {
                for (int j = 0; j < cHeight; j++) {
                    Cb[i * 2][j * 2] = Cb[i * 2 + 1][j * 2] = Cb[i * 2][j * 2 + 1] = Cb[i * 2 + 1][j * 2 + 1] = matrix.getB()[i][j];
                    Cr[i * 2][j * 2] = Cr[i * 2 + 1][j * 2] = Cr[i * 2][j * 2 + 1] = Cr[i * 2 + 1][j * 2 + 1] = matrix.getC()[i][j];
                }
            }
            matrix.setB(Cb);
            matrix.setC(Cr);

            matrix.setState(State.YBR);
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
        minus128(matrix.getA());
        minus128(matrix.getB());
        minus128(matrix.getC());
    }
    private void plus128(){
        plus128(matrix.getA());
        plus128(matrix.getB());
        plus128(matrix.getC());
    }

    private ByteVector getByteVectorFromRGB(){
        if(matrix.getState()!=State.RGB)
            return null;

        ByteVector vector=new ByteVector(10);
        vector.append((short)matrix.getWidth());
        vector.append((short)matrix.getHeight());
        vector.append(matrix.getF().getFlag());

        for(int i=0;i<matrix.getWidth();i++){
            for(int j=0;j<matrix.getHeight();j++){
                byte r,g,b;
                assert (matrix.getA()[i][j]<0xff);
                assert (matrix.getB()[i][j]<0xff);
                assert (matrix.getC()[i][j]<0xff);
                r=(byte)matrix.getA()[i][j];
                g=(byte)matrix.getB()[i][j];
                b=(byte)matrix.getC()[i][j];
                vector.append(r);
                vector.append(g);
                vector.append(b);
            }
        }
        return vector;
    }
    private Matrix getMatrixFromByteVector(ByteVector vector){
        int w=vector.getNextShort();
        int h=vector.getNextShort();
        Flag flag=new Flag(vector.getNextShort());
        Matrix matrix=new Matrix(w,h,flag,State.RGB);
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                matrix.getA()[i][j]=(short)(vector.getNext()&0xff);
                matrix.getB()[i][j]=(short)(vector.getNext()&0xff);
                matrix.getC()[i][j]=(short)(vector.getNext()&0xff);
            }
        }
        return matrix;
    }

    //TODO make Async
    public BufferedImage getBufferedImage() {
        switch(matrix.getState())
        {
            case RGB: FromRGBtoBufferedImage();
                break;
            case YBR: FromYCbCrToBufferedImage();
                break;
            case Yenl:
                PixelRestoration();
                TimeManager.getInstance().append("Restor");
                FromYCbCrToBufferedImage();
                TimeManager.getInstance().append("ybr to im");
                break;
            case bitmap:
                break;
            default:
                return null;
        }

        return bitmap;
    }

    public Matrix getYCbCrMatrix(boolean isAsync) {
        switch (matrix.getState())
        {
            case RGB: FromRGBtoYBR();
                break;
            case YBR:
                break;
            case Yenl:PixelRestoration();
                break;
            case bitmap:
                if(isAsync)
                    FromBufferedImageToYCbCrParallelMatrix();
                else
                    FromBufferedImageToYCbCr();
                break;
            default:return null;

        }

        return matrix;
    }

    public Matrix getRGBMatrix() {
        switch (matrix.getState())
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

    public Matrix getYenlMatrix(boolean isAsync){
        switch (matrix.getState())
        {
            case RGB: FromRGBtoYBR();PixelEnlargement();
                break;
            case YBR:PixelEnlargement();
                break;
            case Yenl:
                break;
            case bitmap:
                if(isAsync)
                    FromBufferedImageToYCbCrParallelMatrix();
                else
                    FromBufferedImageToYCbCr();

                TimeManager.getInstance().append("im to ybr");
                PixelEnlargement();
                TimeManager.getInstance().append("enl");
                break;
            default:return null;

        }
        return matrix;
    }

    public ByteVector getByteVector(){
        switch (matrix.getState())
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
        return getByteVectorFromRGB();
    }


    private void imageToYbrTask(int wStart,int hStart,int wEnd,int hEnd){
        for(int i=wStart;i<wEnd;i++){
            for(int j=hStart;j<hEnd;j++){
                int pixelColor=bitmap.getRGB(i,j);
                // получим цвет каждого пикселя
                double pixelRed = ((pixelColor)>>16&0xFF);
                double pixelGreen= ((pixelColor)>>8&0xFF);
                double pixelBlue=((pixelColor)&0xFF);

                double vy = (0.299 * pixelRed) + (0.587 * pixelGreen) + (0.114 * pixelBlue);
                double vcb = 128 - (0.168736 * pixelRed) - (0.331264 * pixelGreen) + (0.5 * pixelBlue);
                double vcr = 128 + (0.5 * pixelRed) - (0.418688 * pixelGreen) - (0.081312 * pixelBlue);

                //15.11
//                   if(vy%1>=0.5)
//                       vy++;
//                   if(vcb%1>=0.5)
//                       vcb++;
//                   if(vcr%1>=0.5)
//                       vcr++;

                matrix.getA()[i][j] = (short) vy;
                matrix.getB()[i][j] = (short) vcb;
                matrix.getC()[i][j] = (short) vcr;

            }
        }
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
