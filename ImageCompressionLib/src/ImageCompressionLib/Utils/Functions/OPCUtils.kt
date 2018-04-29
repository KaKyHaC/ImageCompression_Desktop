package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.DataOpc
import ImageCompressionLib.Containers.Flag
import java.math.BigInteger

class OPCUtils private constructor(){
    companion object {
        @JvmStatic val instance= OPCUtils()
    }
    private fun directOPC(flag: Flag, dataOrigin: Array<ShortArray>, DataOpc: DataOpc) {
        if (flag.isChecked(Flag.Parameter.DCT))
            MakeUnSigned(dataOrigin, DataOpc)
        if (flag.isChecked(Flag.Parameter.DC))
            DCminus(dataOrigin, DataOpc)
        FindeBase(dataOrigin, DataOpc)

        if (flag.isChecked(Flag.Parameter.LongCode))
            OPCdirectUseOnlyLong(dataOrigin, DataOpc)
        else
            OPCdirect(dataOrigin, DataOpc)
    }

    private fun reverseOPC(flag: Flag, dataOrigin: Array<ShortArray>, DataOpc: DataOpc) {
        if (flag.isChecked(Flag.Parameter.LongCode))
            OPCreverseUseOnlyLong(dataOrigin, DataOpc)
        else
            OPCreverse(dataOrigin, DataOpc)

        if (flag.isChecked(Flag.Parameter.DC))
            DCplus(dataOrigin, DataOpc)

        if (flag.isChecked(Flag.Parameter.DCT))
            MakeSigned(dataOrigin, DataOpc)
    }


    private fun FindeBase(dataOrigin: Array<ShortArray>, DataOpc: DataOpc): DataOpc {
        //        short[] base=new short[SIZEOFBLOCK];
        for (i in 0 until SIZEOFBLOCK) {
            //            assert (dataOrigin[i][0]<(short)(Byte.MAX_VALUE&0xFF));
            DataOpc.base[i] = dataOrigin[i][0]
            for (j in 0 until SIZEOFBLOCK) {
                if (DataOpc.base[i] < dataOrigin[i][j]) {
                    DataOpc.base[i] = dataOrigin[i][j]
                }
            }
            DataOpc.base[i]++
            //            assert DataOpc.getBase()[i]<0xff:"base ["+i+"]="+DataOpc.getBase()[i];
        }
        return DataOpc
    }

    private fun MakeUnSigned(dataOrigin: Array<ShortArray>, DataOpc: DataOpc): DataOpc {
        for (i in 0 until SIZEOFBLOCK) {
            for (j in 0 until SIZEOFBLOCK) {
                if (dataOrigin[i][j] < 0.toShort()) {
                    dataOrigin[i][j] = (dataOrigin[i][j] * -1).toShort()
                    DataOpc.sign[i][j] = false
                } else {
                    DataOpc.sign[i][j] = true
                }
            }
        }
        return DataOpc
    }

    private fun MakeSigned(dataOrigin: Array<ShortArray>, DataOpc: DataOpc): DataOpc {
        for (i in 0 until SIZEOFBLOCK) {
            for (j in 0 until SIZEOFBLOCK) {
                if (!DataOpc.sign[i][j]) {
                    dataOrigin[i][j] = (dataOrigin[i][j] * -1).toShort()
                }
            }
        }
        return DataOpc
    }

    private fun DCminus(dataOrigin: Array<ShortArray>, DataOpc: DataOpc) {
        DataOpc.DC = dataOrigin[0][0]
        dataOrigin[0][0] = 0
    }

    private fun DCplus(dataOrigin: Array<ShortArray>, DataOpc: DataOpc) {
        dataOrigin[0][0] = DataOpc.DC
    }


    private fun OPCdirect(dataOrigin: Array<ShortArray>, DataOpc: DataOpc) {//TODO diagonal for optimization
        DataOpc.N = OPCdirectLong(dataOrigin, DataOpc)
        /*        BigInteger base= new BigInteger("1");
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                if(dataOrigin[i][j]!=0)
                {
                    DataOpc.N=DataOpc.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(DataOpc.getBase()[i]));

            }
        }*/
    }

    private fun OPCdirectLong(dataOrigin: Array<ShortArray>, DataOpc: DataOpc): BigInteger {

        var base: Long = 1
        var res: Long = 0
        var bufbase: Long = 1
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                bufbase = base * DataOpc.base[i]
                /* if(bufbase<res)
                    System.out.println("res");*/
                /*                if(bufbase<base)
                    System.out.println("base");*/
                if (bufbase > MAX_LONG) {//is true ?
                    //                    System.out.println("go");
                    return OPCdirectBI(res, base, i, j, dataOrigin, DataOpc)
                }
                if (dataOrigin[i][j].toInt() != 0) {
                    res += base * dataOrigin[i][j]
                }
                base = bufbase

            }
        }
        //        if(bufbase<res)System.out.println("AAAA");
        return BigInteger.valueOf(res)
    }

    private fun OPCdirectBI(res: Long, baseval: Long, i1: Int, j1: Int, dataOrigin: Array<ShortArray>, DataOpc: DataOpc): BigInteger {

        var `val` = BigInteger.valueOf(res)
        var base = BigInteger.valueOf(baseval)

        var i = i1
        var j = j1
        while (i >= 0) {
            while (j >= 0) {
                if (dataOrigin[i][j].toInt() != 0) {
                    `val` = `val`.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j].toLong())))
                }
                base = base.multiply(BigInteger.valueOf(DataOpc.base[i].toLong()))
                j--
            }
            j = SIZEOFBLOCK - 1
            i--
        }
        return `val`
    }

    private fun OPCreverse(dataOrigin: Array<ShortArray>, DataOpc: DataOpc) {// method copy from C++ Project MAH
        var copy = BigInteger("1")
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                val a: BigInteger
                var b: BigInteger

                a = DataOpc.N.divide(copy)
                copy = copy.multiply(BigInteger.valueOf(DataOpc.base[i].toLong()))

                b = DataOpc.N.divide(copy)
                b = b.multiply(BigInteger.valueOf(DataOpc.base[i].toLong()))
                dataOrigin[i][j] = a.subtract(b).toShort()
            }
        }
    }

    private fun OPCdirectUseOnlyLong(dataOrigin: Array<ShortArray>, DataOpc: DataOpc) {
        var base: Long = 1
        var res: Long = 0
        var bufbase: Long
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                bufbase = base * DataOpc.base[i]
                if (bufbase > MAX_LONG)
                //is true ?
                {
                    DataOpc.vectorCode.add(res)
                    base = 1
                    res = 0
                    bufbase = base * DataOpc.base[i]
                }
                if (dataOrigin[i][j].toInt() != 0) {
                    res += base * dataOrigin[i][j]
                }
                base = bufbase
            }
        }
        DataOpc.vectorCode.add(res)
    }

    private fun OPCreverseUseOnlyLong(dataOrigin: Array<ShortArray>, DataOpc: DataOpc) {
        var copy: Long = 1
        var index = 0
        var curN = DataOpc.vectorCode.elementAt(index)
        var nextcopy: Long
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            if (DataOpc.base[i].toInt() == 0)
            //for wrong password;
                DataOpc.base[i] = 1
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                nextcopy = copy * DataOpc.base[i]
                if (nextcopy > MAX_LONG || nextcopy < 0) {
                    copy = 1
                    index++
                    nextcopy = copy * DataOpc.base[i]
                    if (index < DataOpc.vectorCode.size)
                        curN = DataOpc.vectorCode.elementAt(index)
                }
                val a: Long
                var b: Long


                a = curN / copy
                copy = nextcopy

                b = curN / copy
                b = b * DataOpc.base[i]
                dataOrigin[i][j] = (a - b).toShort()
            }
        }
    }


    fun getDataOrigin(dopc: DataOpc, flag: Flag): Array<ShortArray> {
        val dataOrigin = Array(SIZEOFBLOCK) { ShortArray(SIZEOFBLOCK) }
        reverseOPC(flag, dataOrigin, dopc)
        return dataOrigin
    }

    fun getDataOpc(dataOrigin: Array<ShortArray>, flag: Flag): DataOpc {
        val DataOpc = DataOpc()
        directOPC(flag, dataOrigin, DataOpc)
        return DataOpc
    }

    fun findBase(dataOrigin: Array<ShortArray>, flag: Flag): DataOpc {//TODO What it that doing ?
        val DataOpc = DataOpc()

        MakeUnSigned(dataOrigin, DataOpc)
        if (flag.isChecked(Flag.Parameter.DC)) {
            DCminus(dataOrigin, DataOpc)
        }
        FindeBase(dataOrigin, DataOpc)

        return DataOpc
    }

    fun directOPCwithFindedBase(dataOrigin: Array<ShortArray>, d: DataOpc, flag: Flag): DataOpc {

        MakeUnSigned(dataOrigin, d)
        if (flag.isChecked(Flag.Parameter.DC)) {
            DCminus(dataOrigin, d)
        }

        if (flag.isChecked(Flag.Parameter.LongCode))
            OPCdirectUseOnlyLong(dataOrigin, d)
        else
            OPCdirect(dataOrigin, d)
        return d
    }

}