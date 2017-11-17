package ImageCompression.Objects;


import ImageCompression.Containers.Matrix;
import ImageCompression.Utils.Functions.OPCMultiThread;
import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Functions.DCTMultiThread;
import ImageCompression.Utils.Objects.Flag;

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

public class BoxOfOPC {

    private DataOPC[][] a,b,c;
    private int widthOPC,heightOPC;
    private Matrix matrix;
    private Flag flag;
    private boolean isMatrix=false;
    private boolean isOpcs=false;

    public BoxOfOPC(final Matrix matrix){
        this.matrix=matrix;
        isMatrix=true;
        this.flag=matrix.f;

        sizeOpcCalculate(matrix.Width,matrix.Height);
        int k=(matrix.f.isEnlargement())?2:1;
        a=new DataOPC[widthOPC][heightOPC];
        b=new DataOPC[widthOPC/k][heightOPC/k];
        c=new DataOPC[widthOPC/k][heightOPC/k];
    }
    public BoxOfOPC(DataOPC[][] a, DataOPC[][]b, DataOPC[][]c, Flag flag){
        this.a=a;
        this.b=b;
        this.c=c;
        widthOPC=a.length;
        heightOPC=a[0].length;
        isOpcs=true;
        this.flag=flag;
        this.matrix=new Matrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK,flag);
    }
    private void sizeOpcCalculate(int Width,int Height) {
        widthOPC = Width / DCTMultiThread.SIZEOFBLOCK;
        heightOPC = Height / DCTMultiThread.SIZEOFBLOCK;
        if (Width % DCTMultiThread.SIZEOFBLOCK != 0)
            widthOPC++;
        if (Height % DCTMultiThread.SIZEOFBLOCK != 0)
            heightOPC++;
        //   createMatrix();
    }

    private DataOPC[][] directOPC(short[][]dataOrigin){
        int duWidth=widthOPC;
        int duHeight=heightOPC;
        int Width=dataOrigin.length;
        int Height=dataOrigin[0].length;

        DataOPC[][]dopc=new DataOPC[widthOPC][heightOPC];
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
                        buf[x][y] = value;
                        // DU[i][j].setValue(val,x,y);
                    }
                }
                dopc[i][j]= OPCMultiThread.getDataOPC(buf,matrix.f);
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

                buf= OPCMultiThread.getDataOrigin(dopc[i][j],matrix.f);

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
        a=directOPC(matrix.a);
        b=directOPC(matrix.b);
        c=directOPC(matrix.c);

        isOpcs=true;
    }
    public void reverseOPC(){
        if(!isOpcs)
            return;

        matrix.a=reverceOPC(a);
        matrix.b=reverceOPC(b);
        matrix.c=reverceOPC(c);

        isMatrix=true;
    }

    public void directOPCMultiThreads(){ //multy thred
        if(!isMatrix)
            return;

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<DataOPC[][]>> futures=new ArrayList<Future<DataOPC[][]>>();

        futures.add(executorService.submit(()->directOPC(matrix.a)));
        futures.add(executorService.submit(()->directOPC(matrix.b)));
        futures.add(executorService.submit(()->directOPC(matrix.c)));

            try {
                a=futures.get(0).get();
                b=futures.get(1).get();
                c=futures.get(2).get();
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

        futures.add(executorService.submit(()->reverceOPC(a)));
        futures.add(executorService.submit(()->reverceOPC(b)));
        futures.add(executorService.submit(()->reverceOPC(c)));


        try {
            matrix.a=futures.get(0).get();
            matrix.b=futures.get(1).get();
            matrix.c=futures.get(2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        isMatrix=true;
    }

    public void directOpcGlobalBase(int n,int m){
        if(!isMatrix)
            return;
        directOpcGlobalBase(n,m,matrix.a,a ); //TODO set a
        directOpcGlobalBase(n,m,matrix.b,b);
        directOpcGlobalBase(n,m,matrix.c,c);

        isOpcs=true;
    }
    private void directOpcGlobalBase(int n,int m,short[][]dataOrigin,DataOPC[][]dopc){
        findAllBase(dataOrigin,dopc);

        setMaxBaseForAll(n, m,dopc);

        directOPCwithGlobalBase(dataOrigin, dopc);

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
                    }
                }
                dopc[i][j]= OPCMultiThread.findBase(buf,matrix.f);

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
                dopc[i][j]= OPCMultiThread.directOPCwithFindedBase(buf,dopc[i][j],matrix.f);

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
    public DataOPC[][] getDopcA(){
        if(!isOpcs)
            directOPC();

        if(isOpcs)
            return a;
        return null;
    }
    public DataOPC[][] getDopcB(){
        if(!isOpcs)
            directOPC();

        if(isOpcs)
            return b;
        return null;
    }
    public DataOPC[][] getDopcC(){
        if(!isOpcs)
            directOPC();

        if(isOpcs)
            return c;
        return null;
    }

    public Flag getFlag() {
        return flag;
    }
}
