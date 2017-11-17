package ImageCompression.Utils.Functions;


import ImageCompression.Containers.BoxOfOPC;
import ImageCompression.Utils.Objects.DataOPC;
import ImageCompression.Utils.Objects.Flag;
import ImageCompression.Utils.Objects.Parameters;

import java.io.*;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by Димка on 28.09.2016.
 */
public class FileStream {

    private DataOPC[][] a,b,c;
    private Flag flag;
    private Parameters param=Parameters.getInstanse();


    final String DIR_SD = "ImageCompresion";
    final String LOG_TAG = "myLogs";

    private BufferedWriting bw;
    private DataInputStream br;



    int height,width;

    static FileStream tmp;
    private FileStream() {  }
    public static FileStream getInstance() {
        tmp=new FileStream();
        return tmp;
    }




    private void OpenFileSDWrite() {
        // пр
        // оверяем доступность SD
//        if (!Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
//            return;
//        }
        bw=BufferedWriting.getInstance();
       /* // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // создаем каталог
        sdPath.mkdirs();*//*
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(Path);
        try {
            // открываем поток для записи
            bw = new DataOutputStream(new FileOutputStream(sdFile));
            // пишем данные
            //bw.write("Содержимое файла на SD");
            // закрываем поток
            //bw.close();
            //Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    private void CloseFileSDWrite(String file)    {
         {
            if (bw != null)
                bw.Write(file);
        }

    }

    private void OpenFileSDRead(String Path) {
        // проверяем доступность SD
//        if (!Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
//            return;
//        }
       /* // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        // формируем объект File, который содержит путь к файлу*/
        File sdFile = new File(Path);
        try {
            // открываем поток для чтения
            br = new DataInputStream(new FileInputStream(sdFile));
            /*String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void CloseFileSDRead()    {
        try {
            if (br != null)
                br.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
/*---------------------------------------

/*-----------------------------------------*/
    private void WriteDateOPC(DataOPC d){
         {
            if (flag.isDC())
                bw.writeShort( d.getDC());
                if (flag.isLongCode()) {
                Vector<Long> code = d.getVectorCode();
                int len = code.size();
                bw.writeByte((byte) len);
                for (long val : code) {

                    bw.writeLong( val);
                }
            } else {
                byte[] code = d.BinaryStringGet();
                int len = code.length;
                bw.writeByte ((byte) len);
                for(byte val:code)
                    bw.writeByte(val);
            }
            for(byte val:d.SignToString())
                bw.writeByte(val);

            //ShortArrayToStream(bw,d.FromBaseToString());
        }

    }

    private void WriteDateOPC(DataOPC[][] a) {

        int w=a.length;
        int h=a[0].length;
        short[] size=new short[]{(short)w,(short)h};
        {
            bw.writeShort((short)w);
            bw.writeShort((short)h);
        }
        for(int i=0;i<w;i++)
        {
            for(int j=0;j<h;j++)
            {
                WriteDateOPC(a[i][j]);
            }
        }
        /*if(flag.isDC()&&flag.isLongCode())
        for(int i=0;i<w;i++)
            for(int j=0;j<h;j++)
                WriteDate.isDCisLong(bw,a[i][j]);
        else if(flag.isDC()&&!flag.isLongCode())
            for(int i=0;i<w;i++)
                for(int j=0;j<h;j++)
                    WriteDate.isDCnoLong(bw,a[i][j]);
        if(!flag.isDC()&&flag.isLongCode())
            for(int i=0;i<w;i++)
                for(int j=0;j<h;j++)
                    WriteDate.noDCisLong(bw,a[i][j]);
        if(!flag.isDC()&&!flag.isLongCode())
            for(int i=0;i<w;i++)
                for(int j=0;j<h;j++)
                    WriteDate.noDCnoLong(bw,a[i][j]);
*/

    }
    private DataOPC[][] ReadDateOPC(int zero){

        try {

            int w = br.readShort();
            int h = br.readShort();
            //TODO constructor
            DataOPC[][] d = new DataOPC[w][h];

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    d[i][j] = new DataOPC();
                    if (flag.isDC())
                        d[i][j].setDC(br.readShort());

                    int len = br.readByte() & 0xFF;//does it is needed ?

                    if (flag.isLongCode()) {
                        Vector<Long> v = new Vector<>(len);
                        for (int x = 0; x < len; x++)
                            v.add(br.readLong());
                        d[i][j].setVectorCode(v);
                    } else {
                        byte[] code=new byte[len];
                        for(int i1=0;i1<len;i1++){
                            code[i1]=br.readByte();
                        }
                        d[i][j].BinaryStringSet(code);

                    }

                    byte[] code=new byte[8];
                    for(int i1=0;i1<8;i1++){
                        code[i1]=br.readByte();
                    }
                    d[i][j].SingFromString(code);//length of sing array

                }
            }
            return d;
        }catch (IOException e){
            return null;
        }

    }

    private void WriteDateOPC() {
        ShortToStream(bw,flag.getFlag());
        WriteDateOPC(a);
        WriteDateOPC(b);
        WriteDateOPC(c);

    }
    private void ReadDateOPC() {

        flag=new Flag(ShortFromStream(br));
        a=ReadDateOPC(0);
        b=ReadDateOPC(0);
        c=ReadDateOPC(0);

    }

    private void WriteBase(DataOPC[][] a){
        int w=a.length;
        int h=a[0].length;
     /*   ShortToStream(bw,(short)w);
        ShortToStream(bw,(short)h);*/
        {
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    // WriteStringToStream(bw,GetStringOfBase(a[i][j]));
                    for(short val:a[i][j].FromBaseToString())
                    {
                        bw.writeShort(val);
                    }

                }
            }
        }
    }
    private void ReadBase(DataOPC[][]a){
        int w=a.length;
        int h=a[0].length;
        try {
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    //GetBaseFromString(ReadStringFromStream(br,9),a[i][j]);
                    short[] code = new short[8];
                    for (int i1 = 0; i1 < 8; i1++) {
                        code[i1] = br.readShort();
                    }
                    a[i][j].FromStringToBase(code);
                }
            }
        }catch (IOException r){

        }
    }

    private void WriteBase(){// like Code (dinamic size by argument)
        if(flag.isGlobalBase())
            WriteGlobalBase();
        else{
        WriteBase(a);
        WriteBase(b);
        WriteBase(c);}
    }
    private void ReadBase(){
        if(flag.isGlobalBase())
            ReadGlobalBase();
        else {
            ReadBase(a);
            ReadBase(b);
            ReadBase(c);
        }
    }



    private void WriteBase(String File){
        OpenFileSDWrite();
        WriteBase();
        CloseFileSDWrite(File);
    }
    private void ReadBase(String File){
        OpenFileSDRead(File);
        ReadBase();
        CloseFileSDRead();
    }
    private void WriteCode(String File){

        OpenFileSDWrite();
        WriteDateOPC();
        if(flag.isOneFile()==true)
            WriteBase();
        CloseFileSDWrite(File);

    }
    private void ReadCode(String File){
        OpenFileSDRead(File);
        ReadDateOPC();
        if(flag.isOneFile()==true)
            ReadBase();
        CloseFileSDRead();
    }


    public void Write(String file, BoxOfOPC box, Flag flag){
        this.flag=flag;
        a=box.a;
        b=box.b;
        c=box.c;
        WriteCode(file+".bar");
        if(flag.isOneFile()==false)
            WriteBase(file+".bas");
    }
    public BoxOfOPC Read(String file){


        ReadCode(file+".bar");
        if(flag.isOneFile()==false)
            ReadBase(file+".bas");

        BoxOfOPC box=new BoxOfOPC(0,0,flag.isEnlargement());
        box.a=a;
        box.b=b;
        box.c=c;

        return box;
    }


    public Flag getFlag() {
        return flag;
    }
    public void setFlag(Flag flag) {
        this.flag = flag;
    }


    private void ShortArrayToStream(DataOutputStream dos,short[] val){
        try{
            for(int i=0;i<val.length;i++)
                dos.writeShort(val[i]);
        }catch (IOException e)
        {

        }
    }
    private short[] ShortArrayFromStream(DataInputStream dis,int length){
        short[] val=new short[length];
        try
        {
            for(int i=0;i<length;i++)
                val[i]= dis.readShort();
        }catch (IOException e)
        {

        }
        return val;
    }
    private void ByteArrayToSream(DataOutputStream dos, byte[] val){
        try{
            for(int i=0;i<val.length;i++)
                dos.writeByte(val[i]);
        }catch (IOException e)
        {

        }
    }
    private byte[] ByteArrayFromStream(DataInputStream dis,int length){
        byte[] val=new byte[length];
        try
        {
            for(int i=0;i<length;i++)
                val[i]= dis.readByte();
        }catch (IOException e)
        {

        }
        return val;
    }
    private void ByteToSream(DataOutputStream dos,byte val){
        try{

            dos.writeByte(val);
        }catch (IOException e)
        {

        }
    }
    private int ByteFromStream(DataInputStream dis){

        byte res=-1;
        try
        {

            res= dis.readByte();
        }catch (IOException e)
        {

        }
        return res;
    }
    private void ShortToStream(BufferedWriting dos,short val){
        {

            dos.writeShort(val);
        }
    }
    private short ShortFromStream(DataInputStream dis){
        short val=-1;
        try
        {

            val= dis.readShort();
        }catch (IOException e)
        {

        }
        return val;
    }
    private void LongToStream(DataOutputStream dos,long val){
        try{

            dos.writeLong(val);
        }catch (IOException e)
        {

        }
    }
    private long LongFromStream(DataInputStream dis){
        long val=-1;
        try
        {

            val= dis.readLong();
        }catch (IOException e)
        {

        }
        return val;
    }

    private void WriteGlobalBase(DataOPC[][] a){
        int w=a.length;
        int h=a[0].length;
        {
            int i = 0;
            int j = 0;
            int IndexI=i,IndexJ=j;

            for (; i < w; IndexI+=param.n,i=IndexI,IndexJ=0,j=IndexJ) {
                for (; j < h; i=IndexI,IndexJ+=param.m,j=IndexJ) {
                    for(short val:a[i][j].FromBaseToString())
                    {
                        bw.writeShort(val);
                    }

                }
            }
        }
    }
    private void ReadGlobalBase(DataOPC[][] a){
        int w=a.length;
        int h=a[0].length;
        try {
            int i = 0;
            int j = 0;
            int IndexI=i,IndexJ=j;

            for (; i < w; IndexI+=param.n,i=IndexI,IndexJ=0,j=IndexJ) {
                for (; j < h; i = IndexI, IndexJ += param.m, j = IndexJ) {
                    short[] code = new short[8];
                    for (int i1 = 0; i1 < 8; i1++) {
                        code[i1] = br.readShort();
                    }

                    for (int x = 0; x < param.n && i < w; x++, i++) {
                        j = IndexJ;
                        for (int b = 0; b < param.m && j < h; b++, j++) {
                            a[i][j].FromStringToBase(code);
                        }
                    }

                }

            }
        }catch (IOException r){
        }
    }

    private void WriteGlobalBase(){
        {
            bw.writeByte((byte)param.n);
            bw.writeByte((byte)param.m);
        }
        WriteGlobalBase(a);
        WriteGlobalBase(b);
        WriteGlobalBase(c);
    }
    private void ReadGlobalBase(){
        try{
            param.n=br.readByte();
            param.m=br.readByte();
        }catch (IOException e){}

        ReadGlobalBase(a);
        ReadGlobalBase(b);
        ReadGlobalBase(c);
    }
}

class WriteDate{
    public static void noDCnoLong(DataOutputStream bw,DataOPC d){
        try {

                byte[] code = d.BinaryStringGet();
                int len = code.length;
                bw.writeByte ((byte) len);
                for(byte val:code)
                    bw.writeByte(val);

            for(byte val:d.SignToString())
                bw.writeByte(val);

            //ShortArrayToStream(bw,d.FromBaseToString());
        }catch (IOException e)
        {

        }

    }
    public static void noDCisLong(DataOutputStream bw,DataOPC d){
        try {
            Vector<Long> code = d.getVectorCode();
            int len = code.size();
            bw.writeByte((byte) len);
            for (long val : code) {

                bw.writeLong( val);
            }

            for(byte val:d.SignToString())
                bw.writeByte(val);

            //ShortArrayToStream(bw,d.FromBaseToString());
        }catch (IOException e)
        {

        }
    }
    public static void isDCnoLong(DataOutputStream bw,DataOPC d){
        try {

                bw.writeShort( d.getDC());

                byte[] code = d.BinaryStringGet();
                int len = code.length;
                bw.writeByte ((byte) len);
                for(byte val:code)
                    bw.writeByte(val);

            for(byte val:d.SignToString())
                bw.writeByte(val);

            //ShortArrayToStream(bw,d.FromBaseToString());
        }catch (IOException e)
        {

        }
    }
    public static void isDCisLong(DataOutputStream bw,DataOPC d){
        try {
            bw.writeShort( d.getDC());
            Vector<Long> code = d.getVectorCode();
            int len = code.size();
            bw.writeByte((byte) len);
            for (long val : code) {
                bw.writeLong( val);
            }
            for(byte val:d.SignToString())
                bw.writeByte(val);
        }catch (IOException e)
        {

        }
    }
}

class BufferedWriting{
    private static BufferedWriting instance=new BufferedWriting();
    private BufferedWriting(){};

    //private FileOutputStream fos;
    private byte[] date;
    LinkedList<Byte> llByte;

    public static BufferedWriting getInstance(){
        instance.llByte=new LinkedList<>();
        return instance;
    }
    public void Write(String file){
        Byte[] ptr=llByte.toArray(new Byte[llByte.size()]);
        date=new byte[ptr.length];
        for (int i = 0; i < ptr.length; i++) {
            date[i]=ptr[i];
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(date);
            fos.close();
        }catch (IOException e){

        }
    }

    public void writeByte(byte val){
        llByte.add(val);
    }
    public void writeShort(short val){
        /*byte lW=(byte)val;
        byte hW=(byte)(val>>8);*/
        llByte.add((byte)(val>>8));
        llByte.add((byte)(val));
     /*   if(1==0)
            throw new IOException("lol");*/
    }
    public void writeLong(long val){
       /* byte ahW,alW,bhW,blW;
        blW=(byte)val;
        bhW=(byte)(val>>8);
        alW=(byte)(val>>16);
        ahW=(byte)(val>>32);*/

        llByte.add((byte)(val>>54));
        llByte.add((byte)(val>>48));
        llByte.add((byte)(val>>40));
        llByte.add((byte)(val>>32));
        llByte.add((byte)(val>>24));
        llByte.add((byte)(val>>16));
        llByte.add((byte)(val>>8));
        llByte.add((byte)val);

    }
}
