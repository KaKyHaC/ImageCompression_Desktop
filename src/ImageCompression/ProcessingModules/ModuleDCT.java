package ImageCompression.ProcessingModules;


import ImageCompression.Containers.TripleShortMatrix;
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
    private TripleShortMatrix tripleShortMatrix;
    private DataUnitMatrix a,b,c;

    public ModuleDCT(TripleShortMatrix tripleShortMatrix) {
        this.tripleShortMatrix = tripleShortMatrix;
        if(tripleShortMatrix.getState() == State.Yenl)//new code . Does it is needed ?
            tripleShortMatrix.setState(State.YBR);
            a=new DataUnitMatrix(tripleShortMatrix.getA(), tripleShortMatrix.getA().length, tripleShortMatrix.getA()[0].length, tripleShortMatrix.getState(), TypeQuantization.luminosity, tripleShortMatrix.getF());
            b = new DataUnitMatrix(tripleShortMatrix.getB(), tripleShortMatrix.getB().length, tripleShortMatrix.getB()[0].length, tripleShortMatrix.getState(), TypeQuantization.Chromaticity, tripleShortMatrix.getF());
            c  = new DataUnitMatrix(tripleShortMatrix.getC(), tripleShortMatrix.getC().length, tripleShortMatrix.getC()[0].length, tripleShortMatrix.getState(), TypeQuantization.Chromaticity, tripleShortMatrix.getF());

    }

    public void dataProcessing() {
        if(tripleShortMatrix.getState() ==State.Yenl)//new code . Does it is needed ?
            tripleShortMatrix.setState(State.YBR);


        a.dataProcessing();
        b.dataProcessing();
        c.dataProcessing();

        tripleShortMatrix.setState(a.getState());
        if(tripleShortMatrix.getA().length> tripleShortMatrix.getB().length&& tripleShortMatrix.getState() ==State.YBR)
            tripleShortMatrix.setState(State.Yenl);
    }
    public void dataProcessingInThreads() {
        if(tripleShortMatrix.getState() ==State.Yenl)//new code . Does it is needed ?
            tripleShortMatrix.setState(State.YBR);

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

        tripleShortMatrix.setState(a.getState());
        if(tripleShortMatrix.getA().length> tripleShortMatrix.getB().length&& tripleShortMatrix.getState() ==State.YBR)
            tripleShortMatrix.setState(State.Yenl);
    }

    public TripleShortMatrix getYCbCrMatrix(boolean isMultiThreads) {
        switch (tripleShortMatrix.getState()){
            case DCT:
                if(isMultiThreads)
                    dataProcessingInThreads();
                else
                    dataProcessing();
                break;
        }
        if(tripleShortMatrix.getState() ==State.YBR|| tripleShortMatrix.getState() ==State.Yenl)
            return tripleShortMatrix;
        return null;
    }
    public TripleShortMatrix getDCTMatrix(boolean isMultiThreads){
        switch (tripleShortMatrix.getState()){
            case YBR:
            case Yenl:
                if(isMultiThreads)
                    dataProcessingInThreads();
                else
                    dataProcessing();
                break;
        }

        if(tripleShortMatrix.getState() ==State.DCT)
            return tripleShortMatrix;
        return null;
    }

    //    public TripleShortMatrix getMatrix() {
//        return tripleShortMatrix;
//    }
}
