package windows_app;

public class Parameters {
    static Parameters instanse = new Parameters();
    private static long MAXLONG = (long) Math.pow(2, 54); // long = 7x8 bits . why ?
    public int n = 4, m = 4;
    public String password = "0";
    public String PathAppDir ="/files";
    public String PathReadMassage = "/files/massage.txt";
    public String PathWriteMassage = "/files/resultMassage.txt";
    public boolean isSteganography = false;
    private int BitPerNumber;
    private int BinaryValOfPos = 1;

    private Parameters() {
    }

    public static Parameters getInstanse() {
        return instanse;
    }

    public static long getMAXLONG() {
        return instanse.MAXLONG;
    }

    public static void setMAXLONG(int numberofbits) {
        instanse.BitPerNumber = numberofbits;
        if (instanse.BitPerNumber > 0 && instanse.BitPerNumber <= 64)
            MAXLONG = (long) Math.pow(2, instanse.BitPerNumber);
    }

    public static void setSize(int n, int m) {
        instanse.n = n;
        instanse.m = m;
    }

    public static String getPasswordFinal() {
        String cpy = instanse.password;
        instanse.password = "0";
        return cpy;
    }

    public int getBinaryValOfPos() {
        return BinaryValOfPos;
    }

    public void setBinaryValOfPos(int posOfSteganogrephy) {
        BinaryValOfPos = (int) Math.pow(2, posOfSteganogrephy);
    }
}
