package ImageCompressionLib.ProcessingModules.ModuleOPC;


import ImageCompressionLib.Containers.*;
import ImageCompressionLib.Constants.State;
import ImageCompressionLib.Utils.Functions.OPCMultiThread;
import ImageCompressionLib.Utils.Functions.DCTMultiThread;
import ImageCompressionLib.Utils.Objects.OpcConvertor;
import org.jetbrains.annotations.NotNull;
//import com.sun.glass.ui.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ImageCompressionLib.Utils.Functions.OPCMultiThread.SIZEOFBLOCK;

/**
 * Created by Димка on 09.10.2016.
 */

public class ModuleOPC extends AbsModuleOPC{
//    private enum State{OPC,Data}
//    private TripleDataOpcMatrix tripleOPC;
//    private int widthOPC,heightOPC;
//    private TripleShortMatrix getTripleShort();
    private OpcConvertor a,b,c;
//    private Flag flag;
    private final State state;
    private boolean isReady=false;
    public boolean isAsyn;
//    private boolean isMatrix=false;
//    private boolean isOpcs=false;

    public ModuleOPC(TripleShortMatrix tripleShortMatrix,Flag flag,Boolean isAsyn){
        super(tripleShortMatrix,flag);
//        this.flag=flag;
        this.isAsyn=isAsyn;
        state=State.Data;
        
        a=new OpcConvertor(getTripleShort().getA(),flag);
        b=new OpcConvertor(getTripleShort().getB(),flag);
        c=new OpcConvertor(getTripleShort().getC(),flag);
        
//        this.getTripleShort()=getTripleShort();

//        Size size=sizeOpcCalculate(getTripleShort()().getWidth(), getTripleShort()().getHeight());
//        int widthOPC= size.getWidth();
//        int heightOPC= size.getHeight();
//        int k=(getTripleShort()().getF().isEnlargement())?2:1;

//        tripleOPC=new TripleDataOpcMatrix();
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
        
//        this.tripleOPC=tripleDataOpc;
//        this.getTripleShort()=new TripleShortMatrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK, ImageCompressionLib.Constants.State.DCT);
//        setTripleShort(new TripleShortMatrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK, State.DCT));
    }


    public void directOPC(){
        if(state==State.OPC||isReady)
            return;

        appendTimeManager("direct OPC");
        getTripleDataOpc().setA(a.getDataOpcs());
        appendTimeManager("get A");
        getTripleDataOpc().setB(b.getDataOpcs());
        appendTimeManager("get B");
        getTripleDataOpc().setC(c.getDataOpcs());
        appendTimeManager("get C");

        isReady=true;
    }
    public void reverseOPC(){
        if(state==State.Data||isReady)
            return;

        appendTimeManager("start reOPC");
        getTripleShort().setA(a.getDataOrigin());
        appendTimeManager("get A");
        getTripleShort().setB(b.getDataOrigin());
        appendTimeManager("get B");
        getTripleShort().setC(c.getDataOrigin());
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
                getTripleDataOpc().setB(futures.get(1).get());
                appendTimeManager("get B");
                getTripleDataOpc().setC(futures.get(2).get());
                appendTimeManager("get C");
                getTripleDataOpc().setA(futures.get(0).get());
                appendTimeManager("get A");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        isReady=true;
    }
    public void reverseOPCMultiThreads(){// create getTripleShort()() with corect size of b and c (complite)  //multy thred
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
            getTripleShort().setB(futures.get(1).get());
            appendTimeManager("get B");
            getTripleShort().setC(futures.get(2).get());
            appendTimeManager("get C");
            getTripleShort().setA(futures.get(0).get());
            appendTimeManager("get A");
//            getTripleShort()().setC(futures.get(2).get());
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
            getTripleDataOpc().setA(a.getDataOpcs());
            getTripleDataOpc().setB(b.getDataOpcs());
            getTripleDataOpc().setC(c.getDataOpcs());
        }
        isReady=true;
    }
    public void reverseOPCParallel(){
        if(state==State.Data||isReady)
            return;

        //omp parallel
        {
            getTripleShort().setA(a.getDataOrigin());
            getTripleShort().setB(b.getDataOrigin());
            getTripleShort().setC(c.getDataOrigin());
        }
        isReady=true;
    }

    public void directOpcGlobalBase(int n,int m){
        if(state==State.OPC||isReady)
            return;
        getTripleDataOpc().setA(a.getDataOpcs(n,m)); //TODO set a
        getTripleDataOpc().setB(b.getDataOpcs(n,m)); //TODO set a
        getTripleDataOpc().setC(c.getDataOpcs(n,m)); //TODO set a

        isReady=true;
    }


    @Deprecated
    public TripleShortMatrix getMatrix(boolean isAsync) {
        if(state!=State.Data&&!isReady) {
            if(isAsync)
                reverseOPCMultiThreads();
            else
                reverseOPC();
        }
        
        return getTripleShort();
    }

    @Deprecated
    public TripleDataOpcMatrix getBoxOfOpc(boolean isAsync) {
        if(state!=State.OPC&&!isReady){
            if(isAsync)
                directOPCMultiThreads();
            else
                directOPC();
        }
        
        return getTripleDataOpc();
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
