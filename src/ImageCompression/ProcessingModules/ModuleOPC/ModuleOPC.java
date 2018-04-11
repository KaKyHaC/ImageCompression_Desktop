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

//    private TripleDataOpcMatrix getByteVectorContainer();
//    private int widthOPC,heightOPC;
//    private TripleShortMatrix getTripleShort();
    private OpcConvertor a,b,c;
    private Flag flag;
    private boolean isMatrix=false;
    private boolean isOpcs=false;

    public ModuleOPC(TripleShortMatrix getTripleShort,Flag flag){
        super(getTripleShort,flag);
//        this.getTripleShort() = getTripleShort();
        isMatrix=true;
        this.flag= flag;

        a=new OpcConvertor(getTripleShort().getA(),flag);
        b=new OpcConvertor(getTripleShort().getB(),flag);
        c=new OpcConvertor(getTripleShort().getC(),flag);

//        Size size=sizeOpcCalculate(getTripleShort().getWidth(), getTripleShort().getHeight());
//        int widthOPC= size.getWidth();
//        int heightOPC= size.getHeight();
//        int k=(getTripleShort().getF().isEnlargement())?2:1;

        setTripleDataOpc(new TripleDataOpcMatrix());
//        getByteVectorContainer().setA(new DataOpc[widthOPC][heightOPC]);
//        getByteVectorContainer().setB(new DataOpc[widthOPC/k][heightOPC/k]);
//        getByteVectorContainer().setC(new DataOpc[widthOPC/k][heightOPC/k]);
    }
    public ModuleOPC(TripleDataOpcMatrix getTripleDataOpc, Flag flag){
        super(getTripleDataOpc,flag);
//        this.getByteVectorContainer() = getByteVectorContainer();

        a=new OpcConvertor(getTripleDataOpc().getA(),flag);
        b=new OpcConvertor(getTripleDataOpc().getB(),flag);
        c=new OpcConvertor(getTripleDataOpc().getC(),flag);

        int widthOPC= getTripleDataOpc().getA().length;
        int heightOPC= getTripleDataOpc().getA()[0].length;
        isOpcs=true;
        this.flag=flag;
        setTripleShort(new TripleShortMatrix(widthOPC*SIZEOFBLOCK,heightOPC*SIZEOFBLOCK, State.DCT));
    }


    public void directOPC(){
        if(!isMatrix)
            return;

        appendTimeManager("direct OPC");
        getTripleDataOpc().setA(a.getDataOpcs());
        appendTimeManager("get A");
        getTripleDataOpc().setB(b.getDataOpcs());
        appendTimeManager("get B");
        getTripleDataOpc().setC(c.getDataOpcs());
        appendTimeManager("get C");

        isOpcs=true;
    }
    public void reverseOPC(){
        if(!isOpcs)
            return;

        appendTimeManager("start reOPC");
        getTripleShort().setA(a.getDataOrigin());
        appendTimeManager("get A");
        getTripleShort().setB(b.getDataOrigin());
        appendTimeManager("get B");
        getTripleShort().setC(c.getDataOrigin());
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
        isOpcs=true;
    }
    public void reverseOPCMultiThreads(){// create getTripleShort() with corect size of b and c (complite)  //multy thred
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
            getTripleShort().setB(futures.get(1).get());
            appendTimeManager("get B");
            getTripleShort().setC(futures.get(2).get());
            appendTimeManager("get C");
            getTripleShort().setA(futures.get(0).get());
            appendTimeManager("get A");
//            getTripleShort().setC(futures.get(2).get());
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
            getTripleDataOpc().setA(a.getDataOpcs());
            getTripleDataOpc().setB(b.getDataOpcs());
            getTripleDataOpc().setC(c.getDataOpcs());
        }
        isOpcs=true;
    }
    public void reverseOPCParallel(){
        if(isOpcs)
            return;

        //omp parallel
        {
            getTripleShort().setA(a.getDataOrigin());
            getTripleShort().setB(b.getDataOrigin());
            getTripleShort().setC(c.getDataOrigin());
        }
        isMatrix=true;
    }

    public void directOpcGlobalBase(int n,int m){
        if(!isMatrix)
            return;
        getTripleDataOpc().setA(a.getDataOpcs(n,m)); //TODO set a
        getTripleDataOpc().setB(b.getDataOpcs(n,m)); //TODO set a
        getTripleDataOpc().setC(c.getDataOpcs(n,m)); //TODO set a

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
            return getTripleShort();
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
            return getTripleDataOpc();
        return null;
    }

    public boolean isTripleShortMatrix() {
        return isMatrix;
    }
    public boolean isOpcs() {
        return isOpcs;
    }


    private void appendTimeManager(String s){
//        TimeManager.getInstance().append(s);
    }

    @NotNull
    @Override
    public TripleDataOpcMatrix direct(TripleShortMatrix shortMatrix) {
        return getBoxOfOpc(true);
    }

    @NotNull
    @Override
    public TripleShortMatrix reverce(TripleDataOpcMatrix dataOpcMatrix) {
        return getMatrix(true);
    }
}
