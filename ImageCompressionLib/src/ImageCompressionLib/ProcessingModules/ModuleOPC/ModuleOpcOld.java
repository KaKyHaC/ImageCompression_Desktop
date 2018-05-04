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
@Deprecated
public class ModuleOpcOld extends AbsModuleOPC{
//    private enum State{OPC,Data}
//    private TripleDataOpcMatrixOld tripleOPC;
//    private int widthOPC,heightOPC;
//    private TripleShortMatrixOld getTripleShortOld();
    private OpcConvertorOld a,b,c;
//    private Flag flag;
    private final State state;
    private boolean isReady=false;
    public boolean isAsyn;
//    private boolean isMatrix=false;
//    private boolean isOpcs=false;

    public ModuleOpcOld(TripleShortMatrixOld tripleShortMatrixOld, Flag flag, Boolean isAsyn){
        super(tripleShortMatrixOld,flag);
//        this.flag=flag;
        this.isAsyn=isAsyn;
        state=State.Data;
        
        a=new OpcConvertorOld(getTripleShortOld().getA(),flag);
        b=new OpcConvertorOld(getTripleShortOld().getB(),flag);
        c=new OpcConvertorOld(getTripleShortOld().getC(),flag);
        
//        this.getTripleShortOld()=getTripleShortOld();

//        Size size=sizeOpcCalculate(getTripleShortOld()().getWidth(), getTripleShortOld()().getHeight());
//        int widthOPC= size.getWidth();
//        int heightOPC= size.getHeight();
//        int k=(getTripleShortOld()().getF().isEnlargement())?2:1;

//        tripleOPC=new TripleDataOpcMatrixOld();
//        setTripleDataOpcOld(new TripleDataOpcMatrixOld());
//        getByteVectorContainer().setA(new DataOpcOld[widthOPC][heightOPC]);
//        getByteVectorContainer().setB(new DataOpcOld[widthOPC/k][heightOPC/k]);
//        getByteVectorContainer().setC(new DataOpcOld[widthOPC/k][heightOPC/k]);
    }
    public ModuleOpcOld(TripleDataOpcMatrixOld tripleDataOpc, Flag flag, Boolean isAsyn){
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
        
//        this.tripleOPC=tripleDataOpcOld;
//        this.getTripleShortOld()=new TripleShortMatrixOld(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK, ImageCompressionLib.Constants.State.DCT);
//        setTripleShortOld(new TripleShortMatrixOld(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK, State.DCT));
    }


    public void directOPC(){
        if(state==State.OPC||isReady)
            return;

        appendTimeManager("direct OPC");
        getTripleDataOpcOld().setA(a.getDataOpcOlds());
        appendTimeManager("get A");
        getTripleDataOpcOld().setB(b.getDataOpcOlds());
        appendTimeManager("get B");
        getTripleDataOpcOld().setC(c.getDataOpcOlds());
        appendTimeManager("get C");

        isReady=true;
    }
    public void reverseOPC(){
        if(state==State.Data||isReady)
            return;

        appendTimeManager("start reOPC");
        getTripleShortOld().setA(a.getDataOrigin());
        appendTimeManager("get A");
        getTripleShortOld().setB(b.getDataOrigin());
        appendTimeManager("get B");
        getTripleShortOld().setC(c.getDataOrigin());
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
                getTripleDataOpcOld().setB(futures.get(1).get());
                appendTimeManager("get B");
                getTripleDataOpcOld().setC(futures.get(2).get());
                appendTimeManager("get C");
                getTripleDataOpcOld().setA(futures.get(0).get());
                appendTimeManager("get A");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        isReady=true;
    }
    public void reverseOPCMultiThreads(){// create getTripleShortOld()() with corect size of b and c (complite)  //multy thred
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
            getTripleShortOld().setB(futures.get(1).get());
            appendTimeManager("get B");
            getTripleShortOld().setC(futures.get(2).get());
            appendTimeManager("get C");
            getTripleShortOld().setA(futures.get(0).get());
            appendTimeManager("get A");
//            getTripleShortOld()().setC(futures.get(2).get());
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
            getTripleDataOpcOld().setA(a.getDataOpcOlds());
            getTripleDataOpcOld().setB(b.getDataOpcOlds());
            getTripleDataOpcOld().setC(c.getDataOpcOlds());
        }
        isReady=true;
    }
    public void reverseOPCParallel(){
        if(state==State.Data||isReady)
            return;

        //omp parallel
        {
            getTripleShortOld().setA(a.getDataOrigin());
            getTripleShortOld().setB(b.getDataOrigin());
            getTripleShortOld().setC(c.getDataOrigin());
        }
        isReady=true;
    }

    public void directOpcGlobalBase(int n,int m){
        if(state==State.OPC||isReady)
            return;
        getTripleDataOpcOld().setA(a.getDataOpcs(n,m)); //TODO set a
        getTripleDataOpcOld().setB(b.getDataOpcs(n,m)); //TODO set a
        getTripleDataOpcOld().setC(c.getDataOpcs(n,m)); //TODO set a

        isReady=true;
    }


    @Deprecated
    public TripleShortMatrixOld getMatrix(boolean isAsync) {
        if(state!=State.Data&&!isReady) {
            if(isAsync)
                reverseOPCMultiThreads();
            else
                reverseOPC();
        }
        
        return getTripleShortOld();
    }

    @Deprecated
    public TripleDataOpcMatrixOld getBoxOfOpc(boolean isAsync) {
        if(state!=State.OPC&&!isReady){
            if(isAsync)
                directOPCMultiThreads();
            else
                directOPC();
        }
        
        return getTripleDataOpcOld();
    }

    private void appendTimeManager(String s){
//        TimeManager.getInstance().append(s);
    }

    @NotNull
    @Override
    public TripleDataOpcMatrixOld direct(TripleShortMatrixOld shortMatrix) {
        return getBoxOfOpc(isAsyn);
    }

    @NotNull
    @Override
    public TripleShortMatrixOld reverce(TripleDataOpcMatrixOld dataOpcMatrix) {
        return getMatrix(isAsyn);
    }
}
