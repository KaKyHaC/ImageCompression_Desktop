package ImageCompression.Utils.Objects;


import ImageCompression.Constants.State;
import ImageCompression.Constants.TypeQuantization;
import ImageCompression.Utils.Functions.DCTMultiThread;

/**
 * Created by Димка on 08.08.2016.
 */

public class DataUnitMatrix {


    private State state ;
    private short[][] dataOrigin;
    private short[][] dataProcessed;
    private long AC;
    private int Width, Height;
    private int duWidth, duHeight;
    private TypeQuantization tq;
    private Flag flag;



    public DataUnitMatrix(short[][] dataOrigin, int width, int height, State state, TypeQuantization tq,Flag flag) {
        this.dataOrigin = dataOrigin;
        dataProcessed=dataOrigin;
        Width = width;
        Height = height;
        this.tq = tq;
        this.flag=flag;

        this.state=state;
        sizeCalculate();




    }
    private void sizeCalculate() {
        duWidth = Width / DCTMultiThread.SIZEOFBLOCK;
        duHeight = Height / DCTMultiThread.SIZEOFBLOCK;
        if (Width % DCTMultiThread.SIZEOFBLOCK != 0)
            duWidth++;
        if (Height % DCTMultiThread.SIZEOFBLOCK != 0)
            duHeight++;

     //   createMatrix();


    }


    private void preProsses() {
        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                int curX = i * DCTMultiThread.SIZEOFBLOCK;
                int curY = j * DCTMultiThread.SIZEOFBLOCK;
                if(i!=0&&j!=0)
                    dataOrigin[curX][curY]=(short)(dataOrigin[0][0]-dataOrigin[curX][curY]);
            }
        }
    }
    private short[][] fillBufferforDU(int i,int j,short[][]buffer){
        for (int x = 0; x < DCTMultiThread.SIZEOFBLOCK; x++) {
            for (int y = 0; y < DCTMultiThread.SIZEOFBLOCK; y++) {
                short value = 0;
                int curX = i * DCTMultiThread.SIZEOFBLOCK + x;
                int curY = j * DCTMultiThread.SIZEOFBLOCK + y;
                if (curX< Width && curY < Height)
                    value = dataOrigin[curX][curY];
                buffer[x][y] = value;
                // DU[i][j].setValue(val,x,y);
            }
        }
        return buffer;
    }
    private short[][] MainCode(short[][] buf){

        if(state==State.YBR) {

            if(flag.isAlignment())
            minus128(buf);

            buf = DCTMultiThread.directDCT(buf);

            if(flag.getQuantization()== Flag.QuantizationState.First)
                DCTMultiThread.directQuantization(tq,buf);
        }
        else if(state==State.DCT)
        {
            if(flag.getQuantization()== Flag.QuantizationState.First)
                DCTMultiThread.reverseQuantization(tq,buf);
            buf = DCTMultiThread.reverseDCT(buf);

            if(flag.isAlignment())
            plus128(buf);
        }

        return buf;
    }
    private void fillDateProcessed(int i,int j,short[][]buffer){
        for (int x = 0; x < DCTMultiThread.SIZEOFBLOCK; x++) {
            for (int y = 0; y < DCTMultiThread.SIZEOFBLOCK; y++) {

                int curX = i * DCTMultiThread.SIZEOFBLOCK + x;
                int curY = j * DCTMultiThread.SIZEOFBLOCK + y;
                if (curX< Width && curY < Height)
                    dataProcessed[curX][curY] = buffer[x][y];
            }
        }
    }

    public void dataProcessing() {
        short[][] buf = new short[DCTMultiThread.SIZEOFBLOCK][DCTMultiThread.SIZEOFBLOCK];
        if(state==State.DCT)
            preProsses();

        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {

                buf=fillBufferforDU(i,j,buf);

                buf=MainCode(buf);

                //-------------------directQuantization
                fillDateProcessed(i,j,buf);

            }
        }
        if(state==State.YBR)
            preProsses();


        if(state==State.YBR)
            state=State.DCT;
        else if(state==State.DCT)
            state=State.YBR;

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

    public State getState() {
        return state;
    }

}
