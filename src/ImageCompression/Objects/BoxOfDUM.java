package ImageCompression.Objects;


import ImageCompression.Containers.Matrix;
import ImageCompression.Containers.State;
import ImageCompression.Containers.TypeQuantization;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Димка on 19.09.2016.
 */
public class BoxOfDUM {
    private Matrix matrix;
    private DataUnitMatrix a,b,c;

    public BoxOfDUM(Matrix matrix) {
        this.matrix = matrix;
        if(matrix.state== State.Yenl)//new code . Does it is needed ?
            matrix.state=State.YBR;
            a=new DataUnitMatrix(matrix.a,matrix.a.length,matrix.a[0].length,matrix.state, TypeQuantization.luminosity,matrix.f);
            b = new DataUnitMatrix(matrix.b, matrix.b.length, matrix.b[0].length, matrix.state, TypeQuantization.Chromaticity,matrix.f);
            c  = new DataUnitMatrix(matrix.c, matrix.c.length, matrix.c[0].length, matrix.state, TypeQuantization.Chromaticity,matrix.f);

    }

    public void dataProcessing() {
        if(matrix.state==State.Yenl)//new code . Does it is needed ?
            matrix.state=State.YBR;


        a.dataProcessing();
        b.dataProcessing();
        c.dataProcessing();

        matrix.state=a.getState();
        if(matrix.a.length>matrix.b.length&&matrix.state==State.YBR)
            matrix.state=State.Yenl;
    }
    public void dataProcessingInThreads() {
        if(matrix.state==State.Yenl)//new code . Does it is needed ?
            matrix.state=State.YBR;

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

        matrix.state=a.getState();
        if(matrix.a.length>matrix.b.length&&matrix.state==State.YBR)
            matrix.state=State.Yenl;
    }

    public Matrix getMatrix() {
        return matrix;
    }
}
