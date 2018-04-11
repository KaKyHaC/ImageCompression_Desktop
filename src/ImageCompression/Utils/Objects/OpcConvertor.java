package ImageCompression.Utils.Objects;

import ImageCompression.Containers.DataOpc;
import ImageCompression.Containers.Flag;
import ImageCompression.Containers.Size;
import ImageCompression.Utils.Functions.DCTMultiThread;
import ImageCompression.Utils.Functions.OPCMultiThread;

import static ImageCompression.Utils.Functions.OPCMultiThread.SIZEOFBLOCK;

public class OpcConvertor {
    enum State{Opc,Origin}
    final State state;
    short[][] dataOrigin;
    DataOpc[][] dataOpcs;
    Flag flag;
    boolean isReady=false;

    public OpcConvertor(short[][] dataOrigin, Flag flag) {
        this.dataOrigin = dataOrigin;
        this.flag = flag;
        state=State.Origin;
    }

    public OpcConvertor(DataOpc[][] dataOpcs, Flag flag) {
        this.dataOpcs = dataOpcs;
        this.flag = flag;
        state=State.Opc;
    }

    private DataOpc[][] directOPC(short[][]dataOrigin){

        Size size=sizeOpcCalculate(dataOrigin.length, dataOrigin[0].length);
        int duWidth= size.getWidth();
        int duHeight= size.getHeight();
        int Width=dataOrigin.length;
        int Height=dataOrigin[0].length;

        DataOpc[][]dopc=new DataOpc[duWidth][duHeight];
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
                dopc[i][j]= OPCMultiThread.getDataOpc(buf, flag);
            }
        }
        return dopc;
    }
    private short[][] reverceOPC(DataOpc[][]dopc){

        int duWidth=dopc.length;
        int duHeight=dopc[0].length;
        int Width=duWidth*SIZEOFBLOCK;
        int Height=duHeight*SIZEOFBLOCK;
        short[][] res=new short[Width][Height];

//        boolean DC=flag.isChecked(Flag.Parameter.DC);
        short[][]buf;//=new short[SIZEOFBLOCK][SIZEOFBLOCK];

        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {//j=3 erro

                buf= OPCMultiThread.getDataOrigin(dopc[i][j], flag);

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

    private DataOpc[][] directOpcGlobalBase(int n, int m, short[][]dataOrigin){
        DataOpc[][]dopc=createDataOpc(dataOrigin);

        findAllBase(dataOrigin,dopc);

        setMaxBaseForAll(n, m,dopc);

        directOPCwithGlobalBase(dataOrigin, dopc);

        return dopc;
    }
    private DataOpc[][] createDataOpc(short[][]dataOrigin){
        Size size=sizeOpcCalculate(dataOrigin.length, dataOrigin[0].length);
        int duWidth= size.getWidth();
        int duHeight= size.getHeight();

        DataOpc[][]dopc=new DataOpc[duWidth][duHeight];
        return dopc;
    }
    private void findAllBase(short[][]dataOrigin,DataOpc[][]dopc){
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
                dopc[i][j]= OPCMultiThread.findBase(buf, flag);

            }
        }
    }
    private void setMaxBaseForAll(int n,int m,DataOpc[][]dopc){
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
                        maxBase=findMaxInArry(maxBase,dopc[i][j].getBase());
                    }
                }

                i=IndexI;
                for(int a=0;a<n&&i<duWidth;a++,i++){
                    j=IndexJ;
                    for(int b=0;b<m&&j<duHeight;b++,j++){
                        dopc[i][j].setBase(maxBase);
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
    private void directOPCwithGlobalBase(short[][]dataOrigin,DataOpc[][]dopc){
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
                dopc[i][j]= OPCMultiThread.directOPCwithFindedBase(buf,dopc[i][j], flag);

            }
        }
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

    public short[][] getDataOrigin() {
        if(state==State.Opc&&!isReady) {
            dataOrigin = reverceOPC(dataOpcs);
            isReady=true;
        }

        return dataOrigin;
    }

    public DataOpc[][] getDataOpcs() {
        if(state==State.Origin&&!isReady) {
            dataOpcs = directOPC(dataOrigin);
            isReady=true;
        }

        return dataOpcs;
    }

    /**
     * calculate(if need) DataOpcs with global base for (nxm)
     * @param n - vertical size of same base
     * @param m - horizonlat size of same base
     * @return matrix of DataOpc with same base 
     */
    public DataOpc[][] getDataOpcs(int n,int m){
        if(state==State.Origin&&!isReady) {
            dataOpcs = directOpcGlobalBase(n,m,dataOrigin);
            isReady=true;
        }

        return dataOpcs;
    }
}
