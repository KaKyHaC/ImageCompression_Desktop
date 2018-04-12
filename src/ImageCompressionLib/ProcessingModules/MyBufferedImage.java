package ImageCompressionLib.ProcessingModules;

import ImageCompressionLib.Containers.TripleShortMatrix;
import ImageCompressionLib.Constants.State;
import ImageCompressionLib.Containers.ByteVector;
import ImageCompressionLib.Containers.Flag;
import ImageCompressionLib.Utils.Objects.TimeManager;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyBufferedImage {
    private static final int SIZEOFBLOCK = 8;
    private TripleShortMatrix tripleShortMatrix;
    private BufferedImage bitmap;
    private Flag flag;


    public MyBufferedImage(BufferedImage _b, Flag flag) {
        bitmap = _b;
        tripleShortMatrix = new TripleShortMatrix(bitmap.getWidth(), bitmap.getHeight(),State.bitmap);
        this.flag=flag;
    }
    public MyBufferedImage(TripleShortMatrix tripleShortMatrix,Flag flag){
        this.tripleShortMatrix = tripleShortMatrix;
        bitmap = new BufferedImage(tripleShortMatrix.getWidth(), tripleShortMatrix.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        this.flag=flag;
    }
    public MyBufferedImage(ByteVector vector,Flag flag){
        this.tripleShortMatrix =getMatrixFromByteVector(vector);
        bitmap = new BufferedImage(tripleShortMatrix.getWidth(), tripleShortMatrix.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        this.flag=flag;
    }

    //TODO string constructor
//TODO fix threads
    private void FromBufferedImageToYCbCrParallelMatrix() {
        if (tripleShortMatrix.getState() == State.bitmap)
        {
            int w=bitmap.getWidth();
            int h=bitmap.getHeight();

            ExecutorService executorService = Executors.newFixedThreadPool(4);
            Future[] futures=new Future[4];

            int [][]img=convertTo2DWithoutUsingGetRGB(bitmap);
            futures[0] = executorService.submit(()-> imageToYbrTask(0,0,w/2,h/2,img));
            futures[1] = executorService.submit(()-> imageToYbrTask(w/2,0,w,h,img));
            futures[2] = executorService.submit(()-> imageToYbrTask(0,h/2,w,h,img));
            futures[3] = executorService.submit(()-> imageToYbrTask(w/2,h/2,w,h,img));

            for (Future future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            tripleShortMatrix.setState(State.YBR);
        }

    }
    private void FromBufferedImageToYCbCr() {

        if (tripleShortMatrix.getState() == State.bitmap)
        {
            int [][]img =convertTo2DWithoutUsingGetRGB(bitmap);
            forEach(bitmap.getWidth(),bitmap.getHeight(),(x, y) -> {
//                int pixelColor=bitmap.getRGB(x,y);
                int pixelColor=img[y][x];
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


                tripleShortMatrix.getA()[x][y] = (short) vy;
                tripleShortMatrix.getB()[x][y] = (short) vcb;
                tripleShortMatrix.getC()[x][y] = (short) vcr;

            });

            tripleShortMatrix.setState(State.YBR);
        }

    }
    private void FromYCbCrToBufferedImage(){
        if(tripleShortMatrix.getState() ==State.YBR){
            final int pixelAlpha=255; //for argb

            forEach(tripleShortMatrix.getWidth(), tripleShortMatrix.getHeight(),(x, y) -> {
                double r,g,b;
                r=(tripleShortMatrix.getA()[x][y]+1.402*(tripleShortMatrix.getC()[x][y]-128));
                g=(tripleShortMatrix.getA()[x][y]-0.34414*(tripleShortMatrix.getB()[x][y]-128)-0.71414*(tripleShortMatrix.getC()[x][y]-128));
                b=(tripleShortMatrix.getA()[x][y]+1.772*(tripleShortMatrix.getB()[x][y]-128));

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
            tripleShortMatrix.setState(State.bitmap);
        }
    }

    private void FromBufferedImageToRGB() {

        if(tripleShortMatrix.getState() ==State.bitmap) {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for (int i = 0; i < Width; i++) {
                for (int j = 0; j < Height; j++) {
//                    int pixelColor = rgb[i*Height + j];
                    int pixelColor=bitmap.getRGB(i,j);
                    // получим цвет каждого пикселя
                    tripleShortMatrix.getA()[i][j] = (short) ((pixelColor)>>16&0xFF);
                    tripleShortMatrix.getB()[i][j] = (short) ((pixelColor)>>8&0xFF);
                    tripleShortMatrix.getC()[i][j] = (short) ((pixelColor)&0xFF);

                }
            }
            tripleShortMatrix.setState(State.RGB);
        }
    }
    private void FromRGBtoBufferedImage(){
        if(tripleShortMatrix.getState() ==State.RGB)
        {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for(int i=0;i<Width;i++)
            {
                for(int j=0;j<Height;j++)
                {

                    int pixelAlpha=255; //for argb
                    int pixelBlue= tripleShortMatrix.getC()[i][j]&0xFF;
                    int pixelRed= tripleShortMatrix.getA()[i][j]&0xFF;
                    int pixelGreen= tripleShortMatrix.getB()[i][j]&0xFF;
//                    int val =(pixelAlpha<<24)| (pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for argb
                    int val =(pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for rgb

                    // полученный результат вернём в BufferedImage
                    bitmap.setRGB(i, j, val);
                }
            }
            tripleShortMatrix.setState(State.bitmap);
        }
    }

    private void FromYBRtoRGB(){

        if(tripleShortMatrix.getState() ==State.YBR) {
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for(int i=0;i<Width;i++)
            {
                for(int j=0;j<Height;j++) {
                    double r, g, b;
                    r = (tripleShortMatrix.getA()[i][j] + 1.402 * (tripleShortMatrix.getC()[i][j] - 128));
                    g = (tripleShortMatrix.getA()[i][j] - 0.34414 * (tripleShortMatrix.getB()[i][j] - 128) - 0.71414 * (tripleShortMatrix.getC()[i][j] - 128));
                    b = (tripleShortMatrix.getA()[i][j] + 1.772 * (tripleShortMatrix.getB()[i][j] - 128));
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
                    tripleShortMatrix.getA()[i][j] = (short) r;
                    tripleShortMatrix.getB()[i][j] = (short) g;
                    tripleShortMatrix.getC()[i][j] = (short) b;
                }
            }
            tripleShortMatrix.setState(State.RGB);
        }
    }
    private void FromRGBtoYBR() {

        if (tripleShortMatrix.getState() == State.RGB){
            int Width=bitmap.getWidth();
            int Height=bitmap.getHeight();

            for (int i = 0; i < Width; i++) {
                for (int j = 0; j < Height; j++) {
                    // получим цвет каждого пикселя
                    double pixelRed = tripleShortMatrix.getA()[i][j];
                    double pixelGreen = tripleShortMatrix.getB()[i][j];
                    double pixelBlue = tripleShortMatrix.getC()[i][j];

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

                    tripleShortMatrix.getA()[i][j] = (short) vy;
                    tripleShortMatrix.getB()[i][j] = (short) vcb;
                    tripleShortMatrix.getC()[i][j] = (short) vcr;
                }
            }
            tripleShortMatrix.setState(State.YBR);
        }
    }

    private void PixelEnlargement(){
        if (tripleShortMatrix.getState() == State.YBR && flag.isChecked(Flag.Parameter.Enlargement)) {
            int cWidth= tripleShortMatrix.getWidth() /2;
            int cHeight= tripleShortMatrix.getHeight() /2;
            short[][] enlCb=new short[cWidth][cHeight];
            short[][] enlCr=new short[cWidth][cHeight];
            for (int i = 0; i < cWidth; i++) {
                for (int j = 0; j < cHeight; j++) {
                    enlCb[i][j] = (short)( (tripleShortMatrix.getB()[i * 2][j * 2] + tripleShortMatrix.getB()[i * 2 + 1][j * 2]
                            + tripleShortMatrix.getB()[i * 2][j * 2 + 1] + tripleShortMatrix.getB()[i * 2 + 1][j * 2 + 1])/4);
                    enlCr[i][j] = (short)( (tripleShortMatrix.getC()[i * 2][j * 2] + tripleShortMatrix.getC()[i * 2 + 1][j * 2]
                            + tripleShortMatrix.getC()[i * 2][j * 2 + 1] + tripleShortMatrix.getC()[i * 2 + 1][j * 2 + 1])/4);
                }
            }
            tripleShortMatrix.setB(enlCb);
            tripleShortMatrix.setC(enlCr);
            tripleShortMatrix.setState(State.Yenl);
        }

    }
    private void PixelRestoration() {

        if (tripleShortMatrix.getState() == State.Yenl && flag.isChecked(Flag.Parameter.Enlargement)) {
            int cWidth= tripleShortMatrix.getWidth() /2;
            int cHeight= tripleShortMatrix.getHeight() /2;
            int Width= tripleShortMatrix.getWidth();
            int Height= tripleShortMatrix.getHeight();
            short[][]Cb=new short[Width][Height];
            short[][]Cr=new short[Width][Height];
            for (int i = 0; i < cWidth; i++) {
                for (int j = 0; j < cHeight; j++) {
                    Cb[i * 2][j * 2] = Cb[i * 2 + 1][j * 2] = Cb[i * 2][j * 2 + 1] = Cb[i * 2 + 1][j * 2 + 1] = tripleShortMatrix.getB()[i][j];
                    Cr[i * 2][j * 2] = Cr[i * 2 + 1][j * 2] = Cr[i * 2][j * 2 + 1] = Cr[i * 2 + 1][j * 2 + 1] = tripleShortMatrix.getC()[i][j];
                }
            }
            tripleShortMatrix.setB(Cb);
            tripleShortMatrix.setC(Cr);

            tripleShortMatrix.setState(State.YBR);
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
        minus128(tripleShortMatrix.getA());
        minus128(tripleShortMatrix.getB());
        minus128(tripleShortMatrix.getC());
    }
    private void plus128(){
        plus128(tripleShortMatrix.getA());
        plus128(tripleShortMatrix.getB());
        plus128(tripleShortMatrix.getC());
    }

    private ByteVector getByteVectorFromRGB(){
        if(tripleShortMatrix.getState()!=State.RGB)
            return null;

        ByteVector vector=new ByteVector(10);
        vector.append((short) tripleShortMatrix.getWidth());
        vector.append((short) tripleShortMatrix.getHeight());
        vector.append(flag.getFlag());

        for(int i = 0; i< tripleShortMatrix.getWidth(); i++){
            for(int j = 0; j< tripleShortMatrix.getHeight(); j++){
                byte r,g,b;
                assert (tripleShortMatrix.getA()[i][j]<0xff);
                assert (tripleShortMatrix.getB()[i][j]<0xff);
                assert (tripleShortMatrix.getC()[i][j]<0xff);
                r=(byte) tripleShortMatrix.getA()[i][j];
                g=(byte) tripleShortMatrix.getB()[i][j];
                b=(byte) tripleShortMatrix.getC()[i][j];
                vector.append(r);
                vector.append(g);
                vector.append(b);
            }
        }
        return vector;
    }
    private TripleShortMatrix getMatrixFromByteVector(ByteVector vector){
        int w=vector.getNextShort();
        int h=vector.getNextShort();
        Flag flag=new Flag(vector.getNextShort());
        TripleShortMatrix tripleShortMatrix =new TripleShortMatrix(w,h,State.RGB);
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                tripleShortMatrix.getA()[i][j]=(short)(vector.getNext()&0xff);
                tripleShortMatrix.getB()[i][j]=(short)(vector.getNext()&0xff);
                tripleShortMatrix.getC()[i][j]=(short)(vector.getNext()&0xff);
            }
        }
        return tripleShortMatrix;
    }

    //TODO make Async
    public BufferedImage getBufferedImage() {
        switch(tripleShortMatrix.getState())
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

    public TripleShortMatrix getYCbCrMatrix(boolean isAsync) {
        switch (tripleShortMatrix.getState())
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

        return tripleShortMatrix;
    }

    public TripleShortMatrix getRGBMatrix() {
        switch (tripleShortMatrix.getState())
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

        return tripleShortMatrix;
    }

    public TripleShortMatrix getYenlMatrix(boolean isAsync){
        TimeManager.getInstance().append("start Yenl");
        switch (tripleShortMatrix.getState())
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
        return tripleShortMatrix;
    }

    public ByteVector getByteVector(){
        switch (tripleShortMatrix.getState())
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


    private void imageToYbrTask(int wStart,int hStart,int wEnd,int hEnd,int [][] image) {
        appendTimeManager("imageToYbrTask(" + wStart + ")(" + hStart + ")");
        int w = wEnd - wStart;
        int h = hEnd - hStart;
        short[][] _a = new short[w][h];
        short[][] _b = new short[w][h];
        short[][] _c = new short[w][h];
        int[][] rgb = new int[w][h];
        appendTimeManager("mem"+wStart+hStart);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb[i][j] = image[ j + hStart][i + wStart];
            }
        }

        appendTimeManager("rgb cpy"+wStart+hStart);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int pixelColor = rgb[i][j];
                // получим цвет каждого пикселя
                double pixelRed = ((pixelColor) >> 16 & 0xFF);
                double pixelGreen = ((pixelColor) >> 8 & 0xFF);
                double pixelBlue = ((pixelColor) & 0xFF);

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

//                tripleShortMatrix.getA()[i][j] = (short) vy;
//                tripleShortMatrix.getB()[i][j] = (short) vcb;
//                tripleShortMatrix.getC()[i][j] = (short) vcr;

                _a[i][j] = (short) vy;
                _b[i][j] = (short) vcb;
                _c[i][j] = (short) vcr;

            }
        }
        appendTimeManager("ybr calc"+wStart+hStart);
        for (int i = wStart; i < wEnd; i++) {
            for (int j = hStart; j < hEnd; j++) {
                tripleShortMatrix.getA()[i][j] = _a[i - wStart][j - hStart];
                tripleShortMatrix.getB()[i][j] = _b[i - wStart][j - hStart];
                tripleShortMatrix.getC()[i][j] = _c[i - wStart][j - hStart];
            }
        }
        appendTimeManager("ybr set"+wStart+hStart);
    }
    private void appendTimeManager(String s){
//        TimeManager.getInstance().append(s);
    }
    private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
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
