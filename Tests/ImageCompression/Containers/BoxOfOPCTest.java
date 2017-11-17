package ImageCompression.Containers;

import ImageCompression.Objects.BoxOfOPC;
import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Objects.Flag;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class BoxOfOPCTest {
    int size=200;
    Flag flag=new Flag("0");
    Matrix matrix=new Matrix(size,size,flag);
    @Before
    public void setUp() throws Exception {
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                matrix.a[i][j]=(short)(i+j);
                matrix.b[i][j]=(short)(i+j);
                matrix.c[i][j]=(short)(i+j);
            }
        }
    }

    @Test
    public void TestDefault(){
        BoxOfOPC boxOfOPC=new BoxOfOPC(matrix);
        DataOPC[][] a,b,c;
        a=boxOfOPC.getDopcA();
        b=boxOfOPC.getDopcB();
        c=boxOfOPC.getDopcC();
        BoxOfOPC boxOfOPC1=new BoxOfOPC(a,b,c,matrix.f);
        Matrix res=boxOfOPC1.getMatrix();

        AssertMatrixInRange(matrix,res,1,true);
    }
    @Test
    public void TestTime(){
        BoxOfOPC boxOfOPC=new BoxOfOPC(matrix);
        Date t1=new Date();
        boxOfOPC.directOPCMultiThreads();
        boxOfOPC.reverseOPCMultiThreads();
        Date t2=new Date();
        System.out.println("in multi threads t="+(t2.getTime()-t1.getTime()));

        Date t3=new Date();
        boxOfOPC.directOPC();
        boxOfOPC.reverseOPC();
        Date t4=new Date();
        System.out.println("in one thread t="+(t4.getTime()-t3.getTime()));

        Date t5=new Date();
        boxOfOPC.directOpcGlobalBase(5,5);
        boxOfOPC.reverseOPC();
        Date t6=new Date();
        System.out.println("Global Base t="+(t6.getTime()-t5.getTime()));

        assertTrue((t4.getTime()-t3.getTime())>(t2.getTime()-t1.getTime()));
    }

    @Test
    public void TestAssertFun(){
        Matrix a=new Matrix(size,size,flag);
        Matrix b=new Matrix(size,size,flag);
        AssertMatrixInRange(a,b,0,true);

        a.a[0][0]=1;
        AssertMatrixInRange(a,b,2,true);

        a.a[0][0]=200;
        a.c[0][0]=200;
        a.b[0][0]=200;
        AssertMatrixInRange(a,b,1,false);
    }

    private void AssertMatrixInRange(Matrix m1,Matrix m2,int delta,Boolean inRange){
        AssertArrayArrayInRange(m1.a,m2.a,delta,inRange);
        AssertArrayArrayInRange(m1.b,m2.b,delta,inRange);
        AssertArrayArrayInRange(m1.c,m2.c,delta,inRange);
    }
    private void AssertArrayArrayInRange(short[][] a,short[][] b,int delta,boolean inRange){
        boolean total=true;
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[0].length;j++){
                boolean isEqual=false;
                for(int d=-delta;d<=delta;d++){
                    if(a[i][j]==b[i][j]+d)
                        isEqual=true;
                }
                if(inRange)
                    assertTrue(isEqual);
                total=total&&isEqual;
            }
        }
        if(inRange)
            assertTrue(total);
        else
            assertFalse(total);
    }


}