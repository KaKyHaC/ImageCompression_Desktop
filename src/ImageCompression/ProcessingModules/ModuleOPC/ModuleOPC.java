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

public class ModuleOPC extends AbsModuleOPC{
    private enum State{OPC,Data}
    private TripleDataOpcMatrix tripleOPC;
//    private int widthOPC,heightOPC;
    private TripleShortMatrix tripleShort;
    private OpcConvertor a,b,c;
//    private Flag flag;
    private final State state;
    private boolean isReady=false;
    public boolean isAsyn;
//    private boolean isMatrix=false;
//    private boolean isOpcs=false;

    public ModuleOPC(TripleShortMatrix tripleShort,Flag flag,Boolean isAsyn){
        super(tripleShort,flag);
//        this.flag=flag;
        this.isAsyn=isAsyn;
        state=State.Data;
        
        a=new OpcConvertor(tripleShort.getA(),flag);
        b=new OpcConvertor(tripleShort.getB(),flag);
        c=new OpcConvertor(tripleShort.getC(),flag);
        
        this.tripleShort=tripleShort;

//        Size size=sizeOpcCalculate(tripleShort().getWidth(), tripleShort().getHeight());
//        int widthOPC= size.getWidth();
//        int heightOPC= size.getHeight();
//        int k=(tripleShort().getF().isEnlargement())?2:1;

        tripleOPC=new TripleDataOpcMatrix();
//        setTripleDataOpc(new TripleDataOpcMatrix());
//        getByteVectorContainer().setA(new DataOpc[widthOPC][heightOPC]);
//        getByteVectorContainer().setB(new DataOpc[widthOPC/k][heightOPC/k]);
//        getByteVectorContainer().setC(new DataOpc[widthOPC/k][heightOPC/k]);
    }
    public ModuleOPC(TripleDataOpcMatrix tripleDataOpc, Flag flag,Boolean isAsyn){
        super(tripleDataOpc,flag);
//        this.getByteVectorContainer() = getByteVectorContainer();

        a=new OpcConvertor(tripleDataOpc.getA(),flag);
        b=new OpcConvertor(tripleDataOpc.getB(),flag);
        c=new OpcConvertor(tripleDataOpc.getC(),flag);

        int widthOPC= tripleDataOpc.getA().length;
        int heightOPC= tripleDataOpc.getA()[0].length;
//        this.flag=flag;
        this.isAsyn=isAsyn;
        state=State.OPC;
        
        this.tripleOPC=tripleDataOpc;
        this.tripleShort=new TripleShortMatrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK, ImageCompression.Constants.State.DCT);
//        setTripleShort(new TripleShortMatrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK, State.DCT));
    }


    public void directOPC(){
        if(state==State.OPC||isReady)
            return;

        appendTimeManager("direct OPC");
        tripleOPC.setA(a.getDataOpcs());
        appendTimeManager("get A");
        tripleOPC.setB(b.getDataOpcs());
        appendTimeManager("get B");
        tripleOPC.setC(c.getDataOpcs());
        appendTimeManager("get C");

        isReady=true;
    }
    public void reverseOPC(){
        if(state==State.Data||isReady)
            return;

        appendTimeManager("start reOPC");
        tripleShort.setA(a.getDataOrigin());
        appendTimeManager("get A");
        tripleShort.setB(b.getDataOrigin());
        appendTimeManager("get B");
        tripleShort.setC(c.getDataOrigin());
        appendTimeManager("get C");

        isReady=true;
    }

    public void directOPCMultiThreads(){ //multy thred
        if(state==State.OPC||isReady)
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
                tripleOPC.setB(futures.get(1).get());
                appendTimeManager("get B");
                tripleOPC.setC(futures.get(2).get());
                appendTimeManager("get C");
                tripleOPC.setA(futures.get(0).get());
                appendTimeManager("get A");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        isReady=true;
    }
    public void reverseOPCMultiThreads(){// create tripleShort() with corect size of b and c (complite)  //multy thred
        if(state==State.Data||isReady)
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
            tripleShort.setB(futures.get(1).get());
            appendTimeManager("get B");
            tripleShort.setC(futures.get(2).get());
            appendTimeManager("get C");
            tripleShort.setA(futures.get(0).get());
            appendTimeManager("get A");
//            tripleShort().setC(futures.get(2).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        isReady=true;
    }

    public void directOPCParallel(){ //multy thread
        if(state==State.OPC||isReady)
            return;

        //omp parallel
        {
            tripleOPC.setA(a.getDataOpcs());
            tripleOPC.setB(b.getDataOpcs());
            tripleOPC.setC(c.getDataOpcs());
        }
        isReady=true;
    }
    public void reverseOPCParallel(){
        if(state==State.Data||isReady)
            return;

        //omp parallel
        {
            tripleShort.setA(a.getDataOrigin());
            tripleShort.setB(b.getDataOrigin());
            tripleShort.setC(c.getDataOrigin());
        }
        isReady=true;
    }

    public void directOpcGlobalBase(int n,int m){
        if(state==State.OPC||isReady)
            return;
        tripleOPC.setA(a.getDataOpcs(n,m)); //TODO set a
        tripleOPC.setB(b.getDataOpcs(n,m)); //TODO set a
        tripleOPC.setC(c.getDataOpcs(n,m)); //TODO set a

        isReady=true;
    }


    public TripleShortMatrix getMatrix(boolean isAsync) {
        if(state!=State.Data&&!isReady) {
            if(isAsync)
                reverseOPCMultiThreads();
            else
                reverseOPC();
        }
        
        return tripleShort;
    }

    public TripleDataOpcMatrix getBoxOfOpc(boolean isAsync) {
        if(state!=State.OPC&&!isReady){
            if(isAsync)
                directOPCMultiThreads();
            else
                directOPC();
        }
        
        return tripleOPC;
    }
    
    private void appendTimeManager(String s){
//        TimeManager.getInstance().append(s);
    }

    @NotNull
    @Override
    public TripleDataOpcMatrix direct(TripleShortMatrix shortMatrix) {
        return getBoxOfOpc(isAsyn);
    }

    @NotNull
    @Override
    public TripleShortMatrix reverce(TripleDataOpcMatrix dataOpcMatrix) {
        return getMatrix(isAsyn);
    }
}
