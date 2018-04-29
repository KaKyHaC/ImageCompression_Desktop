package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.DataOpcOld
import ImageCompressionLib.Containers.Flag
import ImageCompressionLib.Containers.Parameters
import java.math.BigInteger

class OPCUtils private constructor(){
    companion object {
        @JvmStatic val instance= OPCUtils()
    }
   /* private fun directOPC(parameters: Parameters, dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld) {
        if (flag.isChecked(Flag.Parameter.DCT))
            MakeUnSigned(dataOrigin, DataOpcOld)
        if (flag.isChecked(Flag.Parameter.DC))
            DCminus(dataOrigin, DataOpcOld)
        FindeBase(dataOrigin, DataOpcOld)

        if (flag.isChecked(Flag.Parameter.LongCode))
            OPCdirectUseOnlyLong(dataOrigin, DataOpcOld)
        else
            OPCdirect(dataOrigin, DataOpcOld)
    }

    private fun reverseOPC(flag: Flag, dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld) {
        if (flag.isChecked(Flag.Parameter.LongCode))
            OPCreverseUseOnlyLong(dataOrigin, DataOpcOld)
        else
            OPCreverse(dataOrigin, DataOpcOld)

        if (flag.isChecked(Flag.Parameter.DC))
            DCplus(dataOrigin, DataOpcOld)

        if (flag.isChecked(Flag.Parameter.DCT))
            MakeSigned(dataOrigin, DataOpcOld)
    }


    private fun FindeBase(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld): DataOpcOld {
        //        short[] base=new short[SIZEOFBLOCK];
        for (i in 0 until SIZEOFBLOCK) {
            //            assert (dataOrigin[i][0]<(short)(Byte.MAX_VALUE&0xFF));
            DataOpcOld.base[i] = dataOrigin[i][0]
            for (j in 0 until SIZEOFBLOCK) {
                if (DataOpcOld.base[i] < dataOrigin[i][j]) {
                    DataOpcOld.base[i] = dataOrigin[i][j]
                }
            }
            DataOpcOld.base[i]++
            //            assert DataOpcOld.getBase()[i]<0xff:"base ["+i+"]="+DataOpcOld.getBase()[i];
        }
        return DataOpcOld
    }

    private fun MakeUnSigned(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld): DataOpcOld {
        for (i in 0 until SIZEOFBLOCK) {
            for (j in 0 until SIZEOFBLOCK) {
                if (dataOrigin[i][j] < 0.toShort()) {
                    dataOrigin[i][j] = (dataOrigin[i][j] * -1).toShort()
                    DataOpcOld.sign[i][j] = false
                } else {
                    DataOpcOld.sign[i][j] = true
                }
            }
        }
        return DataOpcOld
    }

    private fun MakeSigned(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld): DataOpcOld {
        for (i in 0 until SIZEOFBLOCK) {
            for (j in 0 until SIZEOFBLOCK) {
                if (!DataOpcOld.sign[i][j]) {
                    dataOrigin[i][j] = (dataOrigin[i][j] * -1).toShort()
                }
            }
        }
        return DataOpcOld
    }

    private fun DCminus(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld) {
        DataOpcOld.DC = dataOrigin[0][0]
        dataOrigin[0][0] = 0
    }

    private fun DCplus(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld) {
        dataOrigin[0][0] = DataOpcOld.DC
    }


    private fun OPCdirect(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld) {//TODO diagonal for optimization
        DataOpcOld.N = OPCdirectLong(dataOrigin, DataOpcOld)
        *//*        BigInteger base= new BigInteger("1");
        for(int i=SIZEOFBLOCK-1;i>=0;i--)
        {
            for(int j=SIZEOFBLOCK-1;j>=0;j--)
            {
                if(dataOrigin[i][j]!=0)
                {
                    DataOpcOld.N=DataOpcOld.N.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j])));
                }
                base=base.multiply(BigInteger.valueOf(DataOpcOld.getBase()[i]));

            }
        }*//*
    }

    private fun OPCdirectLong(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld): BigInteger {

        var base: Long = 1
        var res: Long = 0
        var bufbase: Long = 1
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                bufbase = base * DataOpcOld.base[i]
                *//* if(bufbase<res)
                    System.out.println("res");*//*
                *//*                if(bufbase<base)
                    System.out.println("base");*//*
                if (bufbase > MAX_LONG) {//is true ?
                    //                    System.out.println("go");
                    return OPCdirectBI(res, base, i, j, dataOrigin, DataOpcOld)
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

    private fun OPCdirectBI(res: Long, baseval: Long, i1: Int, j1: Int, dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld): BigInteger {

        var `val` = BigInteger.valueOf(res)
        var base = BigInteger.valueOf(baseval)

        var i = i1
        var j = j1
        while (i >= 0) {
            while (j >= 0) {
                if (dataOrigin[i][j].toInt() != 0) {
                    `val` = `val`.add(base.multiply(BigInteger.valueOf(dataOrigin[i][j].toLong())))
                }
                base = base.multiply(BigInteger.valueOf(DataOpcOld.base[i].toLong()))
                j--
            }
            j = SIZEOFBLOCK - 1
            i--
        }
        return `val`
    }

    private fun OPCreverse(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld) {// method copy from C++ Project MAH
        var copy = BigInteger("1")
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                val a: BigInteger
                var b: BigInteger

                a = DataOpcOld.N.divide(copy)
                copy = copy.multiply(BigInteger.valueOf(DataOpcOld.base[i].toLong()))

                b = DataOpcOld.N.divide(copy)
                b = b.multiply(BigInteger.valueOf(DataOpcOld.base[i].toLong()))
                dataOrigin[i][j] = a.subtract(b).toShort()
            }
        }
    }

    private fun OPCdirectUseOnlyLong(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld) {
        var base: Long = 1
        var res: Long = 0
        var bufbase: Long
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                bufbase = base * DataOpcOld.base[i]
                if (bufbase > MAX_LONG)
                //is true ?
                {
                    DataOpcOld.vectorCode.add(res)
                    base = 1
                    res = 0
                    bufbase = base * DataOpcOld.base[i]
                }
                if (dataOrigin[i][j].toInt() != 0) {
                    res += base * dataOrigin[i][j]
                }
                base = bufbase
            }
        }
        DataOpcOld.vectorCode.add(res)
    }

    private fun OPCreverseUseOnlyLong(dataOrigin: Array<ShortArray>, DataOpcOld: DataOpcOld) {
        var copy: Long = 1
        var index = 0
        var curN = DataOpcOld.vectorCode.elementAt(index)
        var nextcopy: Long
        for (i in SIZEOFBLOCK - 1 downTo 0) {
            if (DataOpcOld.base[i].toInt() == 0)
            //for wrong password;
                DataOpcOld.base[i] = 1
            for (j in SIZEOFBLOCK - 1 downTo 0) {
                nextcopy = copy * DataOpcOld.base[i]
                if (nextcopy > MAX_LONG || nextcopy < 0) {
                    copy = 1
                    index++
                    nextcopy = copy * DataOpcOld.base[i]
                    if (index < DataOpcOld.vectorCode.size)
                        curN = DataOpcOld.vectorCode.elementAt(index)
                }
                val a: Long
                var b: Long


                a = curN / copy
                copy = nextcopy

                b = curN / copy
                b = b * DataOpcOld.base[i]
                dataOrigin[i][j] = (a - b).toShort()
            }
        }
    }


    fun getDataOrigin(dopc: DataOpcOld, flag: Flag): Array<ShortArray> {
        val dataOrigin = Array(SIZEOFBLOCK) { ShortArray(SIZEOFBLOCK) }
        reverseOPC(flag, dataOrigin, dopc)
        return dataOrigin
    }

    fun getDataOpc(dataOrigin: Array<ShortArray>, flag: Flag): DataOpcOld {
        val DataOpc = DataOpcOld()
        directOPC(flag, dataOrigin, DataOpc)
        return DataOpc
    }

    fun findBase(dataOrigin: Array<ShortArray>, flag: Flag): DataOpcOld {//TODO What it that doing ?
        val DataOpc = DataOpcOld()

        MakeUnSigned(dataOrigin, DataOpc)
        if (flag.isChecked(Flag.Parameter.DC)) {
            DCminus(dataOrigin, DataOpc)
        }
        FindeBase(dataOrigin, DataOpc)

        return DataOpc
    }

    fun directOPCwithFindedBase(dataOrigin: Array<ShortArray>, d: DataOpcOld, flag: Flag): DataOpcOld {

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
*/
}