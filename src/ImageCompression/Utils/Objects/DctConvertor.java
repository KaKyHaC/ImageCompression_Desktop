package ImageCompression.Utils.Objects;


import ImageCompression.Constants.State;
import ImageCompression.Constants.TypeQuantization;
import ImageCompression.Containers.Flag;
import ImageCompression.Utils.Functions.DCTMultiThread;
import org.jetbrains.annotations.NotNull;

/**
 * Class for transformation between DCT and Origin
 * use min Size and max Time
 * Created by Димка on 08.08.2016.
 */
public class DctConvertor {
    public enum State{DCT,ORIGIN}
    private State state ;

    private short[][] dataOrigin;
    private short[][] dataProcessed;

    private int Width, Height;
    private int duWidth, duHeight;

    private TypeQuantization tq;
    private Flag flag;

//    private boolean isReady=false;

    public DctConvertor(short[][] dataOrigin, State state, TypeQuantization tq, Flag flag) {
        this.dataOrigin = dataOrigin;
        dataProcessed=dataOrigin;//= new short[dataOrigin.length][dataOrigin[0].length];// = dataOrigin
        Width = dataOrigin.length;
        Height = dataOrigin[0].length;
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

    /**
     * subtract the [0][0] element from each [%8][%8]
     */
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

    /**
     * copy 8x8 matrix from dataOrigin started at [i][j] into buffer
     * @param buffer - target matrix to copy
     * @return buffer
     */
    private short[][] fillBufferForDU(int i,int j,@NotNull short[][]buffer){
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

    @FunctionalInterface
    interface FIConvertor{
        short[][] convert(short[][] buf);
    }
    private short[][] directDCT(short[][] buf){
        if(flag.isChecked(Flag.Parameter.Alignment))
            minus128(buf);

        buf = DCTMultiThread.directDCT(buf);

        if(flag.getQuantization()== Flag.QuantizationState.First)
            DCTMultiThread.directQuantization(tq,buf);
        return buf;
    }
    private short[][] reverceDCT(short[][] buf){
        if(flag.getQuantization()== Flag.QuantizationState.First)
            DCTMultiThread.reverseQuantization(tq,buf);

        buf = DCTMultiThread.reverseDCT(buf);

        if(flag.isChecked(Flag.Parameter.Alignment))
            plus128(buf);

        return buf;
    }

    /**
     * set 8x8 matrix from buffer into dataProcessed started at [i][j]
     * @param buffer - matrix with information
     */
    private void fillDateProcessed(int i,int j,@NotNull short[][]buffer){
        for (int x = 0; x < DCTMultiThread.SIZEOFBLOCK; x++) {
            for (int y = 0; y < DCTMultiThread.SIZEOFBLOCK; y++) {

                int curX = i * DCTMultiThread.SIZEOFBLOCK + x;
                int curY = j * DCTMultiThread.SIZEOFBLOCK + y;
                if (curX< Width && curY < Height)
                    dataProcessed[curX][curY] = buffer[x][y];
            }
        }
    }

    /**
     * do transmormation between DCT and Origin states
     */
    private void dataProcessing() {
        short[][] buf = new short[DCTMultiThread.SIZEOFBLOCK][DCTMultiThread.SIZEOFBLOCK];
        if(state==State.DCT)
            preProsses();

        FIConvertor convertor=(state==State.ORIGIN)?this::directDCT:this::reverceDCT;

        for (int i = 0; i < duWidth; i++) {
            for (int j = 0; j < duHeight; j++) {
                buf=fillBufferForDU(i,j,buf);
                buf=convertor.convert(buf);
                fillDateProcessed(i,j,buf);
            }
        }
        if(state==State.ORIGIN)
            preProsses();


        if(state==State.ORIGIN)
            state=State.DCT;
        else if(state==State.DCT)
            state=State.ORIGIN;
//        isReady=true;
    }

    private void minus128(short [][] arr){
        for(int i=0;i<arr.length;i++) {
            for(int j=0;j<arr[0].length;j++){
                arr[i][j]-=128;
            }
        }
    }
    private void plus128(short [][] arr){
        for(int i=0;i<arr.length;i++) {
            for(int j=0;j<arr[0].length;j++){
                arr[i][j]+=128;
            }
        }
    }

    public State getState() {
        return state;
    }

    /**
     * Do main calculation if need
     * @return matrix with original date
     */
    public short[][] getMatrixOrigin() {
//        if(!isReady&&state==State.DCT)
//            dataProcessing();

        if(state==State.DCT)
            dataProcessing();

        return dataProcessed;
    }

    /**
     * Do main calculation if need
     * @return matrix with DCT date
     */
    public short[][] getMatrixDct() {
//        if(!isReady&&state==State.ORIGIN)
//            dataProcessing();

        if(state==State.ORIGIN)
            dataProcessing();

        return dataProcessed;
    }
}
