package ImageCompression.ProcessingModules;


import ImageCompression.Containers.Flag;
import ImageCompression.Containers.TripleShortMatrix;
import ImageCompression.Constants.State;
import ImageCompression.Constants.TypeQuantization;
import ImageCompression.Utils.Objects.DctConvertor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Димка on 19.09.2016.
 */
public class ModuleDCT {
    private TripleShortMatrix tripleShortMatrix;
    private DctConvertor a,b,c;
//    private TripleShortMatrix resTripleShortMatrix;
    private Flag flag;

    public ModuleDCT(TripleShortMatrix tripleShortMatrix,Flag flag) {
        this.tripleShortMatrix = tripleShortMatrix;
//        this.resTripleShortMatrix=tripleShortMatrix;
        this.flag=flag;

        if(tripleShortMatrix.getState() == State.Yenl)//new code . Does it is needed ?
            tripleShortMatrix.setState(State.YBR);

        DctConvertor.State state=(tripleShortMatrix.getState()==State.DCT)? DctConvertor.State.DCT: DctConvertor.State.ORIGIN;

        a=new DctConvertor(tripleShortMatrix.getA(), state, TypeQuantization.luminosity, flag);
        b=new DctConvertor(tripleShortMatrix.getB(), state, TypeQuantization.Chromaticity, flag);
        c=new DctConvertor(tripleShortMatrix.getC(), state, TypeQuantization.Chromaticity, flag);

    }


    @FunctionalInterface
    private interface IThraedable{
        void doFor(DctConvertor e);
    }
    public void dataProcessing(IThraedable forEach) {
        if(tripleShortMatrix.getState() ==State.Yenl)//new code . Does it is needed ?
            tripleShortMatrix.setState(State.YBR);

        forEach.doFor(a);
        forEach.doFor(b);
        forEach.doFor(c);

        State state=a.getState()== DctConvertor.State.DCT?State.DCT:State.YBR;
        tripleShortMatrix.setState(state);
        if(tripleShortMatrix.getA().length> tripleShortMatrix.getB().length&& tripleShortMatrix.getState() ==State.YBR)
            tripleShortMatrix.setState(State.Yenl);
    }
    public void dataProcessingInThreads(IThraedable forEach) {
        if(tripleShortMatrix.getState() ==State.Yenl)//new code . Does it is needed ?
            tripleShortMatrix.setState(State.YBR);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future[] futures=new Future[3];

        futures[0] = executorService.submit(()->forEach.doFor(a));
        futures[1] = executorService.submit(()->forEach.doFor(b));
        futures[2] = executorService.submit(()->forEach.doFor(c));

        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        State state=a.getState()== DctConvertor.State.DCT?State.DCT:State.YBR;
        tripleShortMatrix.setState(state);
        if(tripleShortMatrix.getA().length> tripleShortMatrix.getB().length&& tripleShortMatrix.getState() ==State.YBR)
            tripleShortMatrix.setState(State.Yenl);
    }

    public TripleShortMatrix getYCbCrMatrix(boolean isMultiThreads) {
        switch (tripleShortMatrix.getState()){
            case DCT:
                if(isMultiThreads)
                    dataProcessingInThreads(DctConvertor::getMatrixOrigin);
                else
                    dataProcessing(DctConvertor::getMatrixOrigin);
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
                    dataProcessingInThreads(DctConvertor::getMatrixDct);
                else
                    dataProcessing(DctConvertor::getMatrixDct);
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
