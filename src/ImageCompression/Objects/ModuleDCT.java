package ImageCompression.Objects;


import ImageCompression.Containers.Matrix;
import ImageCompression.Constants.State;
import ImageCompression.Constants.TypeQuantization;
import ImageCompression.Utils.Objects.DataUnitMatrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Димка on 19.09.2016.
 */
public class ModuleDCT {
    private Matrix matrix;
    private DataUnitMatrix a,b,c;

    public ModuleDCT(Matrix matrix) {
        this.matrix = matrix;
        if(matrix.getState() == State.Yenl)//new code . Does it is needed ?
            matrix.setState(State.YBR);
            a=new DataUnitMatrix(matrix.getA(), matrix.getA().length, matrix.getA()[0].length, matrix.getState(), TypeQuantization.luminosity, matrix.getF());
            b = new DataUnitMatrix(matrix.getB(), matrix.getB().length, matrix.getB()[0].length, matrix.getState(), TypeQuantization.Chromaticity, matrix.getF());
            c  = new DataUnitMatrix(matrix.getC(), matrix.getC().length, matrix.getC()[0].length, matrix.getState(), TypeQuantization.Chromaticity, matrix.getF());

    }

    public void dataProcessing() {
        if(matrix.getState() ==State.Yenl)//new code . Does it is needed ?
            matrix.setState(State.YBR);


        a.dataProcessing();
        b.dataProcessing();
        c.dataProcessing();

        matrix.setState(a.getState());
        if(matrix.getA().length> matrix.getB().length&& matrix.getState() ==State.YBR)
            matrix.setState(State.Yenl);
    }
    public void dataProcessingInThreads() {
        if(matrix.getState() ==State.Yenl)//new code . Does it is needed ?
            matrix.setState(State.YBR);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future[] futures=new Future[3];

        futures[0] = executorService.submit(()-> a.dataProcessing());
        futures[1] = executorService.submit(()-> b.dataProcessing());
        futures[2] = executorService.submit(()-> c.dataProcessing());

        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        matrix.setState(a.getState());
        if(matrix.getA().length> matrix.getB().length&& matrix.getState() ==State.YBR)
            matrix.setState(State.Yenl);
    }

    public Matrix getYCbCrMatrix(boolean isMultiThreads) {
        switch (matrix.getState()){
            case DCT:
                if(isMultiThreads)
                    dataProcessingInThreads();
                else
                    dataProcessing();
                break;
        }
        if(matrix.getState() ==State.YBR|| matrix.getState() ==State.Yenl)
            return matrix;
        return null;
    }
    public Matrix getDCTMatrix(boolean isMultiThreads){
        switch (matrix.getState()){
            case YBR:
            case Yenl:
                if(isMultiThreads)
                    dataProcessingInThreads();
                else
                    dataProcessing();
                break;
        }

        if(matrix.getState() ==State.DCT)
            return matrix;
        return null;
    }

    //    public Matrix getMatrix() {
//        return matrix;
//    }
}
