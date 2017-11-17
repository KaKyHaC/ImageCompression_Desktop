package ImageCompression.Utils.Functions;


import ImageCompression.Containers.Matrix;
import ImageCompression.Utils.Objects.Parameters;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by Димка on 11.11.2016.
 */
public class Steganography {
    private static Steganography ourInstance = new Steganography();
    private final byte MAXBINARYPOS = (byte) Math.pow(2, 7);
    Parameters param = Parameters.getInstanse();


    private Steganography() {

    }

    public static Steganography getInstance() {
        return new Steganography();
    }

    public void ReadMassageFromMatrixtoFile(Matrix matrix, String file) {
        Byte[] res = ReadMassageFromMatrix(matrix);
        byte[] buf = new byte[res.length];
        for (int i = 0; i < res.length; i++)
            buf[i] = res[i];
        WriteByteArrayToFile(file, buf);
    }

    public Byte[] ReadMassageFromMatrix(Matrix matrix) {
        Byte[] bufC = ReadMassageFromArray(matrix.c, param.getBinaryValOfPos());
        Byte[] bufB = ReadMassageFromArray(matrix.b, param.getBinaryValOfPos());
        Byte[] bufA = ReadMassageFromArray(matrix.a, param.getBinaryValOfPos());

        Byte[] res = new Byte[bufC.length + bufB.length + bufA.length];
        System.arraycopy(bufC, 0, res, 0, bufC.length);
        System.arraycopy(bufB, 0, res, bufC.length, bufB.length);
        System.arraycopy(bufA, 0, res, bufC.length + bufB.length, bufA.length);
        return res;
    }

    public Matrix WriteMassageFromFileToMatrix(Matrix matrix, String file) {
        byte[] massage = ReadByteArrayFromFile(file);
        return WriteMassageFromByteArrayToMatrix(matrix, massage);
    }

    public Matrix WriteMassageFromByteArrayToMatrix(Matrix matrix, byte[] massage) {
        int binaryPos = param.getBinaryValOfPos();
        //byte[] massage=ReadByteArrayFromFile(File);
        int massLen = massage.length;
        int maxLen = CalulateMaxSizeOfMassage(matrix);
        if (massLen >= maxLen) {
            System.out.println("to much massage");
            return null;
        }
        int lenA = MaxSizePerArray(matrix.a);
        int lenB = MaxSizePerArray(matrix.b);
        int lenC = MaxSizePerArray(matrix.c);

        if (lenC < massLen) massLen -= lenC;
        else {
            lenC = massLen;
            massLen = 0;
        }
        if (lenB < massLen) massLen -= lenB;
        else {
            lenB = massLen;
            massLen = 0;
        }
        if (lenA < massLen) massLen -= lenA;
        else {
            lenA = massLen;
            massLen = 0;
        }

        byte[] bufA = new byte[lenA];
        byte[] bufB = new byte[lenB];
        byte[] bufC = new byte[lenC];

        System.arraycopy(massage, 0, bufC, 0, lenC);
        System.arraycopy(massage, lenC, bufB, 0, lenB);
        System.arraycopy(massage, lenB + lenC, bufA, 0, lenA);

        matrix.c = WriteMessagesToArray(matrix.c, bufC, binaryPos);
        matrix.b = WriteMessagesToArray(matrix.b, bufB, binaryPos);
        matrix.a = WriteMessagesToArray(matrix.a, bufA, binaryPos);

        return matrix;
    }

    private byte[] ReadByteArrayFromFile(String file) {
        try {
            FileInputStream fin = new FileInputStream(file);

            System.out.println("Размер файла: " + fin.available() + " байт(а)");

            byte[] buffer = new byte[fin.available()];
            // считаем файл в буфер
            fin.read(buffer, 0, fin.available());

            return buffer;

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private void WriteByteArrayToFile(String file, byte[] bytes) {
        try {
            FileOutputStream fin = new FileOutputStream(file);
            fin.write(bytes);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public int CalulateMaxSizeOfMassage(Matrix matrix) {
        return MaxSizePerArray(matrix.a) + MaxSizePerArray(matrix.b) + MaxSizePerArray(matrix.c);
    }

    private int MaxSizePerArray(short[][] arr) {
        int size = arr.length * arr[0].length;
        size /= 64;
        size *= 62;
        size /= 8;
        return size;
    }

    private short[][] WriteMessagesToArray(short[][] arr, byte[] massage, int BinaryPos) {
        massage = addEOF(massage);
        int index = 0;
        int posInByte = 0;
        int len = massage.length;
        for (int i = 0; i < arr.length; i++) {

            for (int j = 0; j < arr[0].length; j++) {
                if (index < len) {
                    if (!(i % 8 == 0 && j % 8 == 0)) {
                        boolean bit = ((massage[index] & 1) == 1);
                        arr[i][j] = (short) setChecked(arr[i][j], bit, BinaryPos);
                        if (posInByte < 7) {
                            massage[index] >>= 1;
                            posInByte++;
                        } else {
                            index++;
                            posInByte = 0;
                        }
                    }
                } else return arr;

            }
        }
        return arr;
    }

    private Byte[] ReadMassageFromArray(short[][] arr, int BinaryPos) {
        Vector<Byte> res = new Vector<>();

        int posInByte = 0;
        byte last = 0, cur_b = 0;

        for (int i = 0; i < arr.length; i++) {

            for (int j = 0; j < arr[0].length; j++) {
                if (last != Byte.MAX_VALUE && cur_b != Byte.MAX_VALUE) {
                    if (!(i % 8 == 0 && j % 8 == 0)) {
                        boolean bit = isChecked(arr[i][j], BinaryPos);
                        if (bit)
                            cur_b |= MAXBINARYPOS;
                        else
                            cur_b &= ~MAXBINARYPOS;
                        //cur_b|=(bit)?MAXBINARYPOS:0;
                        if (posInByte < 7) {
                            cur_b >>= 1;
                            posInByte++;
                        } else {
                            res.add(cur_b);
                            posInByte = 0;
                            last = cur_b;

                        }
                    }
                } else {
                    res.remove(res.size() - 1);
                    return res.toArray(new Byte[0]);
                }

            }
        }
        return res.toArray(new Byte[res.size()]);
    }

    private byte[] addEOF(byte[] origin) {
        byte[] res = new byte[origin.length + 2];
        for (int i = 0; i < origin.length; i++) {
            res[i] = origin[i];
        }
        res[origin.length] = Byte.MAX_VALUE;
        res[origin.length + 1] = Byte.MAX_VALUE;
        return res;
    }

    private boolean isChecked(int val, int BinaryValOfPos) {
        return (val & BinaryValOfPos) != 0;
    }

    private int setChecked(int val, boolean bit, int BinaryValOfPos) {
        if (bit == true) {
            val |= BinaryValOfPos;
        } else {
            val &= (~BinaryValOfPos);
        }
        return val;
    }
}
