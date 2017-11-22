package ImageCompression.Objects;


import ImageCompression.Containers.BoxOfOpc;
import ImageCompression.Containers.Matrix;
import ImageCompression.Constants.State;
import ImageCompression.Utils.Functions.OPCMultiThread;
import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Functions.DCTMultiThread;
import ImageCompression.Utils.Objects.Flag;
import com.sun.glass.ui.Size;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ImageCompression.Utils.Functions.OPCMultiThread.SIZEOFBLOCK;

/**
 * Created by Димка on 09.10.2016.
 */

public class ModuleOPC {

    private BoxOfOpc boxOfOpc;
//    private int widthOPC,heightOPC;
    private Matrix matrix;
    private Flag flag;
    private boolean isMatrix=false;
    private boolean isOpcs=false;

    public ModuleOPC(final Matrix matrix){
        this.matrix=matrix;
        isMatrix=true;
        this.flag= matrix.getF();

        Size size=sizeOpcCalculate(matrix.getWidth(), matrix.getHeight());
        int widthOPC=size.width;
        int heightOPC=size.height;
        int k=(matrix.getF().isEnlargement())?2:1;
        boxOfOpc=new BoxOfOpc();
//        boxOfOpc.setA(new DataOPC[widthOPC][heightOPC]);
//        boxOfOpc.setB(new DataOPC[widthOPC/k][heightOPC/k]);
//        boxOfOpc.setC(new DataOPC[widthOPC/k][heightOPC/k]);
    }
    public ModuleOPC(BoxOfOpc boxOfOpc, Flag flag){
        this.boxOfOpc=boxOfOpc;
        int widthOPC=boxOfOpc.getA().length;
        int heightOPC=boxOfOpc.getA()[0].length;
        isOpcs=true;
        this.flag=flag;
        this.matrix=new Matrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK,flag, State.DCT);
    }

    private Size sizeOpcCalculate(int Width, int Height) {
        int widthOPC = Width / DCTMultiThread.SIZEOFBLOCK;
        int heightOPC = Height / DCTMultiThread.SIZEOFBLOCK;
        if (Width % DCTMultiThread.SIZEOFBLOCK != 0)
            widthOPC++;
        if (Height % DCTMultiThread.SIZEOFBLOCK != 0)
            heightOPC++;
        //   createMatrix();
        return new Size(widthOPC,heightOPC);
    }

    private DataOPC[][] directOPC(short[][]dataOrigin){
        Size size=sizeOpcCalculate(dataOrigin.length, dataOrigin[0].length);
        int duWidth= size.width;
        int duHeight= size.height;
        int Width=dataOrigin.length;
        int Height=dataOrigin[0].length;

        DataOPC[][]dopc=new DataOPC[duWidth][duHeight];
        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                for (int x = 0; x < SIZEOFBLOCK; x++) {
                    for (int y = 0; y < SIZEOFBLOCK; y++) {
                        short value = 0;
                        int curX=i * SIZEOFBLOCK + x;
                        int curY=j * SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            value = dataOrigin[curX][curY];

//                        if(x!=0||y!=0)
//                            assert value<0xff:"value["+curX+"]["+curY+"]="+value;
                        buf[x][y] = value;
                        // DU[i][j].setValue(val,x,y);
                    }
                }
                dopc[i][j]= OPCMultiThread.getDataOPC(buf, matrix.getF());
            }
        }
        return dopc;
    }
    private short[][] reverceOPC(DataOPC[][]dopc){

        int duWidth=dopc.length;
        int duHeight=dopc[0].length;
        int Width=duWidth*SIZEOFBLOCK;
        int Height=duHeight*SIZEOFBLOCK;
        short[][] res=new short[Width][Height];

        boolean DC=flag.isDC();
        short[][]buf;//=new short[SIZEOFBLOCK][SIZEOFBLOCK];

        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {//j=3 erro

                buf= OPCMultiThread.getDataOrigin(dopc[i][j], matrix.getF());

                for (int x = 0; x < DCTMultiThread.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DCTMultiThread.SIZEOFBLOCK; y++) {

                        int curX=i * DCTMultiThread.SIZEOFBLOCK + x;
                        int curY=j * DCTMultiThread.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            res[curX][curY] = buf[x][y];
                        // DU[i][j].setValue(val,x,y);

                    }
                }


            }
        }
        return res;
    }

    public void directOPC(){
        if(!isMatrix)
            return;
        boxOfOpc.setA(directOPC(matrix.getA()));
        boxOfOpc.setB(directOPC(matrix.getB()));
        boxOfOpc.setC(directOPC(matrix.getC()));

        isOpcs=true;
    }
    public void reverseOPC(){
        if(!isOpcs)
            return;

        matrix.setA(reverceOPC(boxOfOpc.getA()));
        matrix.setB(reverceOPC(boxOfOpc.getB()));
        matrix.setC(reverceOPC(boxOfOpc.getC()));

        isMatrix=true;
    }

    public void directOPCMultiThreads(){ //multy thred
        if(!isMatrix)
            return;

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<DataOPC[][]>> futures=new ArrayList<Future<DataOPC[][]>>();

        futures.add(executorService.submit(()->directOPC(matrix.getA())));
        futures.add(executorService.submit(()->directOPC(matrix.getB())));
        futures.add(executorService.submit(()->directOPC(matrix.getC())));

            try {
                boxOfOpc.setA(futures.get(0).get());
                boxOfOpc.setB(futures.get(1).get());
                boxOfOpc.setC(futures.get(2).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        isOpcs=true;
    }
    public void reverseOPCMultiThreads(){// create matrix with corect size of b and c (complite)  //multy thred
        if(isOpcs)
            return;

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<short[][]>> futures=new ArrayList<Future<short[][]>>();

        futures.add(executorService.submit(()->reverceOPC(boxOfOpc.getA())));
        futures.add(executorService.submit(()->reverceOPC(boxOfOpc.getB())));
        futures.add(executorService.submit(()->reverceOPC(boxOfOpc.getC())));


        try {
            matrix.setA(futures.get(0).get());
            matrix.setB(futures.get(1).get());
            matrix.setC(futures.get(2).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        isMatrix=true;
    }

    public void directOPCParallel(){ //multy thread
        if(!isMatrix)
            return;

        //omp parallel
        {
            boxOfOpc.setA(directOPC(matrix.getA()));
            boxOfOpc.setB(directOPC(matrix.getB()));
            boxOfOpc.setC(directOPC(matrix.getC()));
        }
        isOpcs=true;
    }
    public void reverseOPCParallel(){
        if(isOpcs)
            return;

        //omp parallel
        {
            matrix.setA(reverceOPC(boxOfOpc.getA()));
            matrix.setB(reverceOPC(boxOfOpc.getB()));
            matrix.setC(reverceOPC(boxOfOpc.getC()));
        }
        isMatrix=true;
    }

    public void directOpcGlobalBase(int n,int m){
        if(!isMatrix)
            return;
        boxOfOpc.setA(directOpcGlobalBase(n,m, matrix.getA())); //TODO set a
        boxOfOpc.setB(directOpcGlobalBase(n,m, matrix.getB())); //TODO set a
        boxOfOpc.setC(directOpcGlobalBase(n,m, matrix.getC())); //TODO set a

        isOpcs=true;
    }
    private DataOPC[][] directOpcGlobalBase(int n,int m,short[][]dataOrigin){
        DataOPC[][]dopc=createDataOPC(dataOrigin);

        findAllBase(dataOrigin,dopc);

        setMaxBaseForAll(n, m,dopc);

        directOPCwithGlobalBase(dataOrigin, dopc);

        return dopc;
    }
    private DataOPC[][] createDataOPC(short[][]dataOrigin){
        Size size=sizeOpcCalculate(dataOrigin.length, dataOrigin[0].length);
        int duWidth= size.width;
        int duHeight= size.height;

        DataOPC[][]dopc=new DataOPC[duWidth][duHeight];
        return dopc;
    }
    private void findAllBase(short[][]dataOrigin,DataOPC[][]dopc){
        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];

        int duWidth=dopc.length;
        int duHeight=dopc[0].length;
        int Width=dataOrigin.length;
        int Height=dataOrigin[0].length;


        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                for (int x = 0; x < DCTMultiThread.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DCTMultiThread.SIZEOFBLOCK; y++) {
                        short value = 0;
                        int curX=i * DCTMultiThread.SIZEOFBLOCK + x;
                        int curY=j * DCTMultiThread.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            value = dataOrigin[curX][curY];
                        buf[x][y] = value;
                        // DU[i][j].setValue(val,x,y);

//                        if(x!=0||y!=0)
//                            assert value<0xff:"value["+curX+"]["+curY+"]="+value;
                    }
                }
                dopc[i][j]= OPCMultiThread.findBase(buf, matrix.getF());

            }
        }
    }
    private void setMaxBaseForAll(int n,int m,DataOPC[][]dopc){
        int duWidth=dopc.length;
        int duHeight=dopc[0].length;
        int i = 0;
        int j = 0;
        int IndexI=i,IndexJ=j;

        for (; i < duWidth; IndexI+=n,i=IndexI,IndexJ=0,j=IndexJ) {
            for (; j < duHeight; i=IndexI,IndexJ+=m,j=IndexJ) {

                short [] maxBase=new short[SIZEOFBLOCK];
                for(int a=0;a<n&&i<duWidth;a++,i++){
                    j=IndexJ;
                    for(int b=0;b<m&&j<duHeight;b++,j++){
                        maxBase=findMaxInArry(maxBase,dopc[i][j].base);
                    }
                }

                i=IndexI;
                for(int a=0;a<n&&i<duWidth;a++,i++){
                    j=IndexJ;
                    for(int b=0;b<m&&j<duHeight;b++,j++){
                        dopc[i][j].base=maxBase;
                    }
                }
            }
        }
    }
    private short[] findMaxInArry(short[] a,short[] b){
        assert (a.length==b.length);
        for(int i=0;i<a.length;i++){
            a[i]=(a[i]>b[i])?a[i]:b[i];
        }
        return a;
    }
    private void directOPCwithGlobalBase(short[][]dataOrigin,DataOPC[][]dopc){
        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        int duWidth=dopc.length;
        int duHeight=dopc[0].length;
        int Width=dataOrigin.length;
        int Height=dataOrigin[0].length;
        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                for (int x = 0; x < DCTMultiThread.SIZEOFBLOCK; x++) {
                    for (int y = 0; y < DCTMultiThread.SIZEOFBLOCK; y++) {
                        short value = 0;
                        int curX=i * DCTMultiThread.SIZEOFBLOCK + x;
                        int curY=j * DCTMultiThread.SIZEOFBLOCK + y;
                        if (curX< Width && curY < Height)
                            value = dataOrigin[curX][curY];
                        buf[x][y] = value;
                        // DU[i][j].setValue(val,x,y);
                    }
                }
                dopc[i][j]= OPCMultiThread.directOPCwithFindedBase(buf,dopc[i][j], matrix.getF());

            }
        }
    }

    public Matrix getMatrix() {
        if(!isMatrix)
            reverseOPC();

        if(isMatrix)
            return matrix;
        return null;
    }

    public BoxOfOpc getBoxOfOpc() {
        if(!isOpcs)
            directOPC();

        if(isOpcs)
            return boxOfOpc;
        return null;
    }
    public Flag getFlag() {
        return flag;
    }
}
