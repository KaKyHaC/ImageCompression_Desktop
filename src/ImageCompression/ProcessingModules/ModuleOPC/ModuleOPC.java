package ImageCompression.ProcessingModules.ModuleOPC;


import ImageCompression.Containers.*;
import ImageCompression.Constants.State;
import ImageCompression.Utils.Functions.OPCMultiThread;
import ImageCompression.Utils.Functions.DCTMultiThread;
import ImageCompression.Utils.Objects.OpcConvertor;
import org.jetbrains.annotations.NotNull;
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

public class ModuleOPC implements IModuleOPC{

    private TripleDataOpcMatrix tripleDataOpcMatrix;
//    private int widthOPC,heightOPC;
    private TripleShortMatrix tripleShortMatrix;
    private OpcConvertor a,b,c;
    private Flag flag;
    private boolean isMatrix=false;
    private boolean isOpcs=false;

    public ModuleOPC(final TripleShortMatrix tripleShortMatrix,Flag flag){
        this.tripleShortMatrix = tripleShortMatrix;
        isMatrix=true;
        this.flag= flag;

        a=new OpcConvertor(tripleShortMatrix.getA(),flag);
        b=new OpcConvertor(tripleShortMatrix.getB(),flag);
        c=new OpcConvertor(tripleShortMatrix.getC(),flag);

//        Size size=sizeOpcCalculate(tripleShortMatrix.getWidth(), tripleShortMatrix.getHeight());
//        int widthOPC= size.getWidth();
//        int heightOPC= size.getHeight();
//        int k=(tripleShortMatrix.getF().isEnlargement())?2:1;

        tripleDataOpcMatrix =new TripleDataOpcMatrix();
//        tripleDataOpcMatrix.setA(new DataOpc[widthOPC][heightOPC]);
//        tripleDataOpcMatrix.setB(new DataOpc[widthOPC/k][heightOPC/k]);
//        tripleDataOpcMatrix.setC(new DataOpc[widthOPC/k][heightOPC/k]);
    }
    public ModuleOPC(TripleDataOpcMatrix tripleDataOpcMatrix, Flag flag){
        this.tripleDataOpcMatrix = tripleDataOpcMatrix;

        a=new OpcConvertor(tripleDataOpcMatrix.getA(),flag);
        b=new OpcConvertor(tripleDataOpcMatrix.getB(),flag);
        c=new OpcConvertor(tripleDataOpcMatrix.getC(),flag);

        int widthOPC= tripleDataOpcMatrix.getA().length;
        int heightOPC= tripleDataOpcMatrix.getA()[0].length;
        isOpcs=true;
        this.flag=flag;
        this.tripleShortMatrix =new TripleShortMatrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK, State.DCT);
    }


    public void directOPC(){
        if(!isMatrix)
            return;

        appendTimeManager("direct OPC");
        tripleDataOpcMatrix.setA(a.getDataOpcs());
        appendTimeManager("get A");
        tripleDataOpcMatrix.setB(b.getDataOpcs());
        appendTimeManager("get B");
        tripleDataOpcMatrix.setC(c.getDataOpcs());
        appendTimeManager("get C");

        isOpcs=true;
    }
    public void reverseOPC(){
        if(!isOpcs)
            return;

        appendTimeManager("start reOPC");
        tripleShortMatrix.setA(a.getDataOrigin());
        appendTimeManager("get A");
        tripleShortMatrix.setB(b.getDataOrigin());
        appendTimeManager("get B");
        tripleShortMatrix.setC(c.getDataOrigin());
        appendTimeManager("get C");

        isMatrix=true;
    }

    public void directOPCMultiThreads(){ //multy thred
        if(!isMatrix)
            return;

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<DataOpc[][]>> futures=new ArrayList<Future<DataOpc[][]>>();

        appendTimeManager("direct OPC");
        futures.add(executorService.submit(()->a.getDataOpcs()));
        appendTimeManager("set A");
        futures.add(executorService.submit(()->b.getDataOpcs()));
        appendTimeManager("set B");
        futures.add(executorService.submit(()->c.getDataOpcs()));
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
        futures.add(executorService.submit(()->a.getDataOrigin()));
        appendTimeManager("start a");
        futures.add(executorService.submit(()->b.getDataOrigin()));
        appendTimeManager("start b");
        futures.add(executorService.submit(()->c.getDataOrigin()));
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
            tripleDataOpcMatrix.setA(a.getDataOpcs());
            tripleDataOpcMatrix.setB(b.getDataOpcs());
            tripleDataOpcMatrix.setC(c.getDataOpcs());
        }
        isOpcs=true;
    }
    public void reverseOPCParallel(){
        if(isOpcs)
            return;

        //omp parallel
        {
            tripleShortMatrix.setA(a.getDataOrigin());
            tripleShortMatrix.setB(b.getDataOrigin());
            tripleShortMatrix.setC(c.getDataOrigin());
        }
        isMatrix=true;
    }

    public void directOpcGlobalBase(int n,int m){
        if(!isMatrix)
            return;
        tripleDataOpcMatrix.setA(a.getDataOpcs(n,m)); //TODO set a
        tripleDataOpcMatrix.setB(b.getDataOpcs(n,m)); //TODO set a
        tripleDataOpcMatrix.setC(c.getDataOpcs(n,m)); //TODO set a

        isOpcs=true;
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

    public boolean isTripleShortMatrix() {
        return isMatrix;
    }
    public boolean isOpcs() {
        return isOpcs;
    }

    @NotNull
    @Override
    public TripleShortMatrix getTripleShortMatrix() {
        if(!isMatrix) {
                reverseOPCMultiThreads();
        }

        return tripleShortMatrix;
    }

    @NotNull
    @Override
    public TripleDataOpcMatrix getTripleDataOpcMatrix() {
        if(!isOpcs){
            directOPCMultiThreads();
        }

        return tripleDataOpcMatrix;
    }

    private void appendTimeManager(String s){
//        TimeManager.getInstance().append(s);
    }
}
