package ImageCompressionLib.ProcessingModules.ModuleOPC;


import ImageCompressionLib.Containers.*;
import ImageCompressionLib.Containers.Type.Flag;
import ImageCompressionLib.Utils.Objects.OpcConvertorOld;
import org.jetbrains.annotations.NotNull;
//import com.sun.glass.ui.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Димка on 09.10.2016.
 */

public class ModuleOPC extends AbsModuleOPC{
//    private enum State{OPC,Data}
//    private TripleDataOpcMatrix tripleOPC;
//    private int widthOPC,heightOPC;
//    private TripleShortMatrix getTripleShort();
    private OpcConvertorOld a,b,c;
//    private Flag flag;
    private final State state;
    private boolean isReady=false;
    public boolean isAsyn;
//    private boolean isMatrix=false;
//    private boolean isOpcs=false;

    public ModuleOPC(TripleShortMatrix tripleShortMatrix, Flag flag, Boolean isAsyn){
        super(tripleShortMatrix,flag);
//        this.flag=flag;
        this.isAsyn=isAsyn;
        state=State.Data;
        
        a=new OpcConvertorOld(getTripleShort().getA(),flag);
        b=new OpcConvertorOld(getTripleShort().getB(),flag);
        c=new OpcConvertorOld(getTripleShort().getC(),flag);
        
//        this.getTripleShort()=getTripleShort();

//        Size size=sizeOpcCalculate(getTripleShort()().getWidth(), getTripleShort()().getHeight());
//        int widthOPC= size.getWidth();
//        int heightOPC= size.getHeight();
//        int k=(getTripleShort()().getF().isEnlargement())?2:1;

//        tripleOPC=new TripleDataOpcMatrix();
//        setTripleDataOpc(new TripleDataOpcMatrix());
//        getByteVectorContainer().setA(new DataOpcOld[widthOPC][heightOPC]);
//        getByteVectorContainer().setB(new DataOpcOld[widthOPC/k][heightOPC/k]);
//        getByteVectorContainer().setC(new DataOpcOld[widthOPC/k][heightOPC/k]);
    }
    public ModuleOPC(TripleDataOpcMatrix tripleDataOpc, Flag flag,Boolean isAsyn){
        super(tripleDataOpc,flag);
//        this.getByteVectorContainer() = getByteVectorContainer();

        a=new OpcConvertorOld(tripleDataOpc.getA(),flag);
        b=new OpcConvertorOld(tripleDataOpc.getB(),flag);
        c=new OpcConvertorOld(tripleDataOpc.getC(),flag);

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
        getTripleDataOpc().setA(a.getDataOpcOlds());
        appendTimeManager("get A");
        getTripleDataOpc().setB(b.getDataOpcOlds());
        appendTimeManager("get B");
        getTripleDataOpc().setC(c.getDataOpcOlds());
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
        List<Future<DataOpcOld[][]>> futures=new ArrayList<Future<DataOpcOld[][]>>();

        appendTimeManager("direct OPC");
        futures.add(executorService.submit(new Callable<DataOpcOld[][]>() {
            @Override
            public DataOpcOld[][] call() throws Exception {
                return a.getDataOpcOlds();
            }
        }));
        futures.add(executorService.submit(new Callable<DataOpcOld[][]>() {
            @Override
            public DataOpcOld[][] call() throws Exception {
                return b.getDataOpcOlds();
            }
        }));
        futures.add(executorService.submit(new Callable<DataOpcOld[][]>() {
            @Override
            public DataOpcOld[][] call() throws Exception {
                return c.getDataOpcOlds();
            }
        }));

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

        futures.add(executorService.submit(new Callable<short[][]>() {
            @Override
            public short[][] call() throws Exception {
                return a.getDataOrigin();
            }
        }));
        futures.add(executorService.submit(new Callable<short[][]>() {
            @Override
            public short[][] call() throws Exception {
                return b.getDataOrigin();
            }
        }));
        futures.add(executorService.submit(new Callable<short[][]>() {
            @Override
            public short[][] call() throws Exception {
                return c.getDataOrigin();
            }
        }));

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
            getTripleDataOpc().setA(a.getDataOpcOlds());
            getTripleDataOpc().setB(b.getDataOpcOlds());
            getTripleDataOpc().setC(c.getDataOpcOlds());
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
