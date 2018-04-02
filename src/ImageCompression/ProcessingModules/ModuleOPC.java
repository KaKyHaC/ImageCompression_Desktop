package ImageCompression.ProcessingModules;


import ImageCompression.Containers.TripleDataOpcMatrix;
import ImageCompression.Containers.TripleShortMatrix;
import ImageCompression.Constants.State;
import ImageCompression.Containers.Size;
import ImageCompression.Utils.Functions.OPCMultiThread;
import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Functions.DCTMultiThread;
import ImageCompression.Containers.Flag;
//import com.sun.glass.ui.Size;

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

    private TripleDataOpcMatrix tripleDataOpcMatrix;
//    private int widthOPC,heightOPC;
    private TripleShortMatrix tripleShortMatrix;
    private Flag flag;
    private boolean isMatrix=false;
        private boolean isOpcs=false;

    public ModuleOPC(final TripleShortMatrix tripleShortMatrix){
        this.tripleShortMatrix = tripleShortMatrix;
        isMatrix=true;
        this.flag= tripleShortMatrix.getF();

        Size size=sizeOpcCalculate(tripleShortMatrix.getWidth(), tripleShortMatrix.getHeight());
        int widthOPC= size.getWidth();
        int heightOPC= size.getHeight();
        int k=(tripleShortMatrix.getF().isEnlargement())?2:1;
        tripleDataOpcMatrix =new TripleDataOpcMatrix();
//        tripleDataOpcMatrix.setA(new DataOpc[widthOPC][heightOPC]);
//        tripleDataOpcMatrix.setB(new DataOpc[widthOPC/k][heightOPC/k]);
//        tripleDataOpcMatrix.setC(new DataOpc[widthOPC/k][heightOPC/k]);
    }
    public ModuleOPC(TripleDataOpcMatrix tripleDataOpcMatrix, Flag flag){
        this.tripleDataOpcMatrix = tripleDataOpcMatrix;
        int widthOPC= tripleDataOpcMatrix.getA().length;
        int heightOPC= tripleDataOpcMatrix.getA()[0].length;
        isOpcs=true;
        this.flag=flag;
        this.tripleShortMatrix =new TripleShortMatrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK,flag, State.DCT);
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
        int duWidth= size.getWidth();
        int duHeight= size.getHeight();
        int Width=dataOrigin.length;
        int Height=dataOrigin[0].length;

        DataOPC[][]dopc=new DataOPC[duWidth][duHeight];
        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];
        for (int i = 0; i < duWidth; i++) {
//            System.out.print(Thread.currentThread().getId());
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
                dopc[i][j]= OPCMultiThread.getDataOPC(buf, tripleShortMatrix.getF());
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

                buf= OPCMultiThread.getDataOrigin(dopc[i][j], tripleShortMatrix.getF());

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

        appendTimeManager("direct OPC");
        tripleDataOpcMatrix.setA(directOPC(tripleShortMatrix.getA()));
        appendTimeManager("get A");
        tripleDataOpcMatrix.setB(directOPC(tripleShortMatrix.getB()));
        appendTimeManager("get B");
        tripleDataOpcMatrix.setC(directOPC(tripleShortMatrix.getC()));
        appendTimeManager("get C");

        isOpcs=true;
    }
    public void reverseOPC(){
        if(!isOpcs)
            return;

        appendTimeManager("start reOPC");
        tripleShortMatrix.setA(reverceOPC(tripleDataOpcMatrix.getA()));
        appendTimeManager("get A");
        tripleShortMatrix.setB(reverceOPC(tripleDataOpcMatrix.getB()));
        appendTimeManager("get B");
        tripleShortMatrix.setC(reverceOPC(tripleDataOpcMatrix.getC()));
        appendTimeManager("get C");

        isMatrix=true;
    }

    public void directOPCMultiThreads(){ //multy thred
        if(!isMatrix)
            return;

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<DataOPC[][]>> futures=new ArrayList<Future<DataOPC[][]>>();

        appendTimeManager("direct OPC");
        futures.add(executorService.submit(()->directOPC(tripleShortMatrix.getA())));
        appendTimeManager("set A");
        futures.add(executorService.submit(()->directOPC(tripleShortMatrix.getB())));
        appendTimeManager("set B");
        futures.add(executorService.submit(()->directOPC(tripleShortMatrix.getC())));
        appendTimeManager("set C");

            try {
                tripleDataOpcMatrix.setB(futures.get(1).get());
                appendTimeManager("get B");
                tripleDataOpcMatrix.setC(futures.get(2).get());
                appendTimeManager("get C");
                tripleDataOpcMatrix.setA(futures.get(0).get());
                appendTimeManager("get A");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        isOpcs=true;
    }
    public void reverseOPCMultiThreads(){// create tripleShortMatrix with corect size of b and c (complite)  //multy thred
        if(!isOpcs)
            return;

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<short[][]>> futures=new ArrayList<Future<short[][]>>();

        appendTimeManager("start reOPC");
        futures.add(executorService.submit(()->reverceOPC(tripleDataOpcMatrix.getA())));
        appendTimeManager("start a");
        futures.add(executorService.submit(()->reverceOPC(tripleDataOpcMatrix.getB())));
        appendTimeManager("start b");
        futures.add(executorService.submit(()->reverceOPC(tripleDataOpcMatrix.getC())));
        appendTimeManager("start c");


        try {
            tripleShortMatrix.setB(futures.get(1).get());
            appendTimeManager("get B");
            tripleShortMatrix.setC(futures.get(2).get());
            appendTimeManager("get C");
            tripleShortMatrix.setA(futures.get(0).get());
            appendTimeManager("get A");
//            tripleShortMatrix.setC(futures.get(2).get());
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
            tripleDataOpcMatrix.setA(directOPC(tripleShortMatrix.getA()));
            tripleDataOpcMatrix.setB(directOPC(tripleShortMatrix.getB()));
            tripleDataOpcMatrix.setC(directOPC(tripleShortMatrix.getC()));
        }
        isOpcs=true;
    }
    public void reverseOPCParallel(){
        if(isOpcs)
            return;

        //omp parallel
        {
            tripleShortMatrix.setA(reverceOPC(tripleDataOpcMatrix.getA()));
            tripleShortMatrix.setB(reverceOPC(tripleDataOpcMatrix.getB()));
            tripleShortMatrix.setC(reverceOPC(tripleDataOpcMatrix.getC()));
        }
        isMatrix=true;
    }

    public void directOpcGlobalBase(int n,int m){
        if(!isMatrix)
            return;
        tripleDataOpcMatrix.setA(directOpcGlobalBase(n,m, tripleShortMatrix.getA())); //TODO set a
        tripleDataOpcMatrix.setB(directOpcGlobalBase(n,m, tripleShortMatrix.getB())); //TODO set a
        tripleDataOpcMatrix.setC(directOpcGlobalBase(n,m, tripleShortMatrix.getC())); //TODO set a

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
        int duWidth= size.getWidth();
        int duHeight= size.getHeight();

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
                dopc[i][j]= OPCMultiThread.findBase(buf, tripleShortMatrix.getF());

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
                dopc[i][j]= OPCMultiThread.directOPCwithFindedBase(buf,dopc[i][j], tripleShortMatrix.getF());

            }
        }
    }

    public TripleShortMatrix getMatrix(boolean isAsync) {
        if(!isMatrix) {
            if(isAsync)
                reverseOPCMultiThreads();
            else
                reverseOPC();
        }

        if(isMatrix)
            return tripleShortMatrix;
        return null;
    }

    public TripleDataOpcMatrix getBoxOfOpc(boolean isAsync) {
        if(!isOpcs){
            if(isAsync)
                directOPCMultiThreads();
            else
                directOPC();
        }

        if(isOpcs)
            return tripleDataOpcMatrix;
        return null;
    }
    public Flag getFlag() {
        return flag;
    }

    public boolean getTripleShortMatrix() {
        return isMatrix;
    }
    public boolean isOpcs() {
        return isOpcs;
    }

    private void appendTimeManager(String s){
//        TimeManager.getInstance().append(s);
    }
}
