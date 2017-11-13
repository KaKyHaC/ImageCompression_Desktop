package ImageCompression.Objects;

import ImageCompression.Containers.BoxOfOPC;
import ImageCompression.Containers.Matrix;
import ImageCompression.Containers.State;
import ImageCompression.Utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Димка on 09.10.2016.
 */
public class ApplicationOPC {//singelton

    public interface FromBMPtoFile{
        void Update(int val);
    }
    public interface FromFileToBMP {
        void Update(int val);
    }

    final static int SIZEOFBLOCK=8;
    private Matrix matrix;
    private BoxOfOPC opcs;
    private int duWidth,duHeight;
    private int Width,Height;
    private FileStream fs;

    Parameters param=Parameters.getInstanse();
    Steganography st=Steganography.getInstance();

    static private ApplicationOPC Aopc=new ApplicationOPC();
    private ApplicationOPC(){}

    public static ApplicationOPC getInstance(){return new ApplicationOPC();}


    private void directOPC(short[][]dataOrigin,DataOPC[][]dopc){

        duWidth=dopc.length;
        duHeight=dopc[0].length;
        Width=dataOrigin.length;
        Height=dataOrigin[0].length;

        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];

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
                dopc[i][j]= OPCMultiThread.getDataOPC(buf,matrix.f);

            }
        }
    }
    private short[][] reverceOPC(DataOPC[][]dopc){

        duWidth=dopc.length;
        duHeight=dopc[0].length;
        Width=duWidth*SIZEOFBLOCK;
        Height=duHeight*SIZEOFBLOCK;
        short[][] res=new short[Width][Height];

        boolean DC=fs.getFlag().isDC();
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

    public void directOPC(){ //multy thred
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future futures[] = new Future[3];

        futures[0]=executorService.submit(()->directOPC(matrix.a,opcs.a));
        futures[1]=executorService.submit(()->directOPC(matrix.b,opcs.b));
        futures[2]=executorService.submit(()->directOPC(matrix.c,opcs.c));

        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    public void reverceOPC(){// create matrix with corect size of b and c (complite)  //multy thred
       ExecutorService executorService = Executors.newFixedThreadPool(3);
       List<Future<short[][]>> futures=new ArrayList<Future<short[][]>>();


        futures.add(executorService.submit(()->reverceOPC(opcs.a)));
        futures.add(executorService.submit(()->reverceOPC(opcs.b)));
        futures.add(executorService.submit(()->reverceOPC(opcs.c)));


        try {
            matrix.a=futures.get(0).get();
            matrix.b=futures.get(1).get();
            matrix.c=futures.get(2).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void WriteToFile(String file){

        fs.Write(file,opcs,matrix.f);
    }
    private void ReadFromFile(String file){

        opcs=fs.Read(file);

    }

    public void FromMatrixToFile(FromBMPtoFile As, Matrix matrix, String file){
        this.matrix = matrix;
        opcs=new BoxOfOPC(matrix.Width,matrix.Height,matrix.f.isEnlargement());
        duWidth=opcs.width;
        duHeight=opcs.height;
        Width=matrix.Width;
        Height=matrix.Height;

        fs=FileStream.getInstance();

        if(matrix.f.isSteganography()||param.isSteganography)//TODO interface
            st.WriteMassageFromFileToMatrix(matrix,param.PathReadMassage);

        if(matrix.f.isGlobalBase())
            directOpcGlobalBase(param.n,param.m);
        else
            directOPC();

        if(matrix.f.isPassword())
        {
            Encryption(Parameters.getPasswordFinal());
        }


        As.Update(70);
        WriteToFile(file);
    }
    public Matrix FromFileToMatrix( FromFileToBMP As,String file){
        fs=FileStream.getInstance();
        ReadFromFile(file);
        if(opcs.a.length==opcs.b.length){
            duWidth=opcs.width=opcs.b.length;
            duHeight=opcs.height=opcs.b[0].length;
        }

        else {
            duWidth = opcs.width = opcs.b.length * 2;// size like 2 Chromasity (we lose 0-7 pixels)
            duHeight = opcs.height = opcs.b[0].length * 2;
        }
        Width=duWidth*SIZEOFBLOCK;
        Height=duHeight*SIZEOFBLOCK;
        matrix = new Matrix(Width,Height,fs.getFlag());

        if(matrix.f.isPassword())
        {
            Encryption(Parameters.getPasswordFinal());
        }

        As.Update(30);
        reverceOPC();
        matrix.state= State.DCT;

        if(matrix.f.isSteganography()||param.isSteganography)//TODO interface
            st.ReadMassageFromMatrixtoFile(matrix,param.PathWriteMassage);

        return matrix;
    }

    /****************************************/

    private void directOpcGlobalBase(int n,int m){

        directOpcGlobalBase(n,m,matrix.a,opcs.a ); //TODO set a
        directOpcGlobalBase(n,m,matrix.b,opcs.b);
        directOpcGlobalBase(n,m,matrix.c,opcs.c);
    }
    private void directOpcGlobalBase(int n,int m,short[][]dataOrigin,DataOPC[][]dopc){
        duWidth=dopc.length;
        duHeight=dopc[0].length;
        Width=dataOrigin.length;
        Height=dataOrigin[0].length;

        findAllBase(dataOrigin,dopc);

        setMaxBaseForAll(n, m,dopc);

        directOPCwithGlobalBase(dataOrigin, dopc);

    }
    private void findAllBase(short[][]dataOrigin,DataOPC[][]dopc){
        short[][]buf=new short[SIZEOFBLOCK][SIZEOFBLOCK];

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


    private void Encryption(String key){
        Encryption.encode(opcs,key);

    }

}
