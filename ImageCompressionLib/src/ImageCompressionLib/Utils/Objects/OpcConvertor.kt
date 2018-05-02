//package ImageCompressionLib.Utils.Objects
//
//import ImageCompressionLib.Containers.*
//import ImageCompressionLib.Utils.Functions.DCTMultiThread
//import ImageCompressionLib.Utils.Functions.OPCMultiThread
//import ImageCompressionLib.Utils.Functions.OpcProcess
//
//class OpcConvertor {
//    enum class State {
//        Opc, Origin
//    }
//
//
//    private val shortMatrix: ShortMatrix
//    private val dataOpcMatrix: DataOpcMatrix
//    private val parameters:Parameters
//    var isReady = false
//        private set
//    var state: State
//        private set
//
//    private lateinit var splitedShortMatrix:Array<Array<ShortMatrix>>
//    private lateinit var splitedDataOpcMatrix:Array<Array<DataOpcMatrix>>
//
//
//    constructor(dataOrigin: Array<Array<Short>>, parameters: Parameters) {
//        this.shortMatrix = ShortMatrix(dataOrigin)
//        this.parameters=parameters
//        state = State.Origin
//        val size=calculataDataOpcMatrixSize(parameters)
//        dataOpcMatrix=DataOpcMatrix(size.width,size.height,parameters.unitSize)
//    }
//
//    constructor(dataOpcMatrix: Array<Array<DataOpc>>, parameters: Parameters){
//        this.dataOpcMatrix= DataOpcMatrix(dataOpcMatrix)
//        this.parameters=parameters
//        state = State.Opc
//        shortMatrix=ShortMatrix(parameters.imageSize.width,parameters.imageSize.height)
//    }
//    private fun createSplitedMatrix(){
//        splitedDataOpcMatrix=dataOpcMatrix.split(parameters.unitSize.width,parameters.unitSize.height)
//        splitedShortMatrix=shortMatrix.split(parameters.unitSize.width,parameters.unitSize.height)
//    }
//    private fun calculataDataOpcMatrixSize(parameters: Parameters):Size{
//        var w=parameters.imageSize.width/parameters.unitSize.width
//        var h=parameters.imageSize.height/parameters.unitSize.height
//        if(parameters.imageSize.width%parameters.unitSize.width!=0)w++
//        if(parameters.imageSize.height%parameters.unitSize.height!=0)h++
//        return Size(w,h)
//    }
//    private fun beforDirectOPC(){
//        //TODO
//    }
//    private fun directOPC(dataOrigin: Array<ShortArray>): Array<Array<DataOpcOld>> {
//
//        val size = sizeOpcCalculate(dataOrigin.size, dataOrigin[0].size)
//        val duWidth = size.width
//        val duHeight = size.height
//        val Width = dataOrigin.size
//        val Height = dataOrigin[0].size
//
//        val dopc = Array<Array<DataOpcOld>>(duWidth) { arrayOfNulls(duHeight) }
//        val buf = Array(SIZEOFBLOCK) { ShortArray(SIZEOFBLOCK) }
//        for (i in 0 until duWidth) {
//            //            System.out.print(Thread.currentThread().getId());
//            for (j in 0 until duHeight) {
//
//                for (x in 0 until SIZEOFBLOCK) {
//                    for (y in 0 until SIZEOFBLOCK) {
//                        var value: Short = 0
//                        val curX = i * SIZEOFBLOCK + x
//                        val curY = j * SIZEOFBLOCK + y
//                        if (curX < Width && curY < Height)
//                            value = dataOrigin[curX][curY]
//
//                        //                        if(x!=0||y!=0)
//                        //                            assert value<0xff:"value["+curX+"]["+curY+"]="+value;
//                        buf[x][y] = value
//                        // DU[i][j].setValue(val,x,y);
//                    }
//                }
//                dopc[i][j] = OPCMultiThread.getDataOpc(buf, flag)
//            }
//        }
//        return dopc
//    }
//
//    private fun reverceOPC(dopc: Array<Array<DataOpcOld>>): Array<ShortArray> {
//
//        val duWidth = dopc.size
//        val duHeight = dopc[0].size
//        val Width = duWidth * SIZEOFBLOCK
//        val Height = duHeight * SIZEOFBLOCK
//        val res = Array(Width) { ShortArray(Height) }
//
//        //        boolean DC=flag.isChecked(Flag.Parameter.DC);
//        var buf: Array<ShortArray>//=new short[SIZEOFBLOCK][SIZEOFBLOCK];
//
//        for (i in 0 until duWidth) {
//            for (j in 0 until duHeight) {//j=3 erro
//
//                buf = OPCMultiThread.getDataOrigin(dopc[i][j], flag)
//
//                for (x in 0 until DCTMultiThread.SIZEOFBLOCK) {
//                    for (y in 0 until DCTMultiThread.SIZEOFBLOCK) {
//
//                        val curX = i * DCTMultiThread.SIZEOFBLOCK + x
//                        val curY = j * DCTMultiThread.SIZEOFBLOCK + y
//                        if (curX < Width && curY < Height)
//                            res[curX][curY] = buf[x][y]
//                        // DU[i][j].setValue(val,x,y);
//
//                    }
//                }
//
//
//            }
//        }
//        return res
//    }
//
//    private fun directOpcGlobalBase(n: Int, m: Int, dataOrigin: Array<ShortArray>): Array<Array<DataOpcOld>> {
//        val dopc = createDataOpc(dataOrigin)
//
//        findAllBase(dataOrigin, dopc)
//
//        setMaxBaseForAll(n, m, dopc)
//
//        directOPCwithGlobalBase(dataOrigin, dopc)
//
//        return dopc
//    }
//
//    private fun createDataOpc(dataOrigin: Array<ShortArray>): Array<Array<DataOpcOld>> {
//        val size = sizeOpcCalculate(dataOrigin.size, dataOrigin[0].size)
//        val duWidth = size.width
//        val duHeight = size.height
//
//        return Array(duWidth) { arrayOfNulls(duHeight) }
//    }
//
//    private fun findAllBase(dataOrigin: Array<ShortArray>, dopc: Array<Array<DataOpcOld>>) {
//        val buf = Array(SIZEOFBLOCK) { ShortArray(SIZEOFBLOCK) }
//
//        val duWidth = dopc.size
//        val duHeight = dopc[0].size
//        val Width = dataOrigin.size
//        val Height = dataOrigin[0].size
//
//
//        for (i in 0 until duWidth) {
//            for (j in 0 until duHeight) {
//
//                for (x in 0 until DCTMultiThread.SIZEOFBLOCK) {
//                    for (y in 0 until DCTMultiThread.SIZEOFBLOCK) {
//                        var value: Short = 0
//                        val curX = i * DCTMultiThread.SIZEOFBLOCK + x
//                        val curY = j * DCTMultiThread.SIZEOFBLOCK + y
//                        if (curX < Width && curY < Height)
//                            value = dataOrigin[curX][curY]
//                        buf[x][y] = value
//                        // DU[i][j].setValue(val,x,y);
//
//                        //                        if(x!=0||y!=0)
//                        //                            assert value<0xff:"value["+curX+"]["+curY+"]="+value;
//                    }
//                }
//                dopc[i][j] = OPCMultiThread.findBase(buf, flag)
//
//            }
//        }
//    }
//
//    private fun setMaxBaseForAll(n: Int, m: Int, dopc: Array<Array<DataOpcOld>>) {
//        val duWidth = dopc.size
//        val duHeight = dopc[0].size
//        var i = 0
//        var j = 0
//        var IndexI = i
//        var IndexJ = j
//
//        while (i < duWidth) {
//            while (j < duHeight) {
//
//                var maxBase = ShortArray(SIZEOFBLOCK)
//                run {
//                    var a = 0
//                    while (a < n && i < duWidth) {
//                        j = IndexJ
//                        var b = 0
//                        while (b < m && j < duHeight) {
//                            maxBase = findMaxInArry(maxBase, dopc[i][j].base)
//                            b++
//                            j++
//                        }
//                        a++
//                        i++
//                    }
//                }
//
//                i = IndexI
//                var a = 0
//                while (a < n && i < duWidth) {
//                    j = IndexJ
//                    var b = 0
//                    while (b < m && j < duHeight) {
//                        dopc[i][j].base = maxBase
//                        b++
//                        j++
//                    }
//                    a++
//                    i++
//                }
//                i = IndexI
//                IndexJ += m
//                j = IndexJ
//            }
//            IndexI += n
//            i = IndexI
//            IndexJ = 0
//            j = IndexJ
//        }
//    }
//
//    private fun findMaxInArry(a: ShortArray, b: ShortArray): ShortArray {
//        assert(a.size == b.size)
//        for (i in a.indices) {
//            a[i] = if (a[i] > b[i]) a[i] else b[i]
//        }
//        return a
//    }
//
//    private fun directOPCwithGlobalBase(dataOrigin: Array<ShortArray>, dopc: Array<Array<DataOpcOld>>) {
//        val buf = Array(SIZEOFBLOCK) { ShortArray(SIZEOFBLOCK) }
//        val duWidth = dopc.size
//        val duHeight = dopc[0].size
//        val Width = dataOrigin.size
//        val Height = dataOrigin[0].size
//        for (i in 0 until duWidth) {
//            for (j in 0 until duHeight) {
//
//                for (x in 0 until DCTMultiThread.SIZEOFBLOCK) {
//                    for (y in 0 until DCTMultiThread.SIZEOFBLOCK) {
//                        var value: Short = 0
//                        val curX = i * DCTMultiThread.SIZEOFBLOCK + x
//                        val curY = j * DCTMultiThread.SIZEOFBLOCK + y
//                        if (curX < Width && curY < Height)
//                            value = dataOrigin[curX][curY]
//                        buf[x][y] = value
//                        // DU[i][j].setValue(val,x,y);
//                    }
//                }
//                dopc[i][j] = OPCMultiThread.directOPCwithFindedBase(buf, dopc[i][j], flag)
//
//            }
//        }
//    }
//
//    private fun sizeOpcCalculate(Width: Int, Height: Int): Size {
//        var widthOPC = Width / DCTMultiThread.SIZEOFBLOCK
//        var heightOPC = Height / DCTMultiThread.SIZEOFBLOCK
//        if (Width % DCTMultiThread.SIZEOFBLOCK != 0)
//            widthOPC++
//        if (Height % DCTMultiThread.SIZEOFBLOCK != 0)
//            heightOPC++
//        //   createMatrix();
//        return Size(widthOPC, heightOPC)
//    }
//
//    fun getDataOrigin(): Array<ShortArray> {
//        if (state == State.Opc && !isReady) {
//            dataOrigin = reverceOPC(dataOpcOlds)
//            isReady = true
//        }
//
//        return dataOrigin
//    }
//
//    fun getDataOpcOlds(): Array<Array<DataOpcOld>> {
//        if (state == State.Origin && !isReady) {
//            dataOpcOlds = directOPC(dataOrigin)
//            isReady = true
//        }
//
//        return dataOpcOlds
//    }
//
//    /**
//     * calculate(if need) DataOpcs with global base for (nxm)
//     * @param n - vertical size of same base
//     * @param m - horizonlat size of same base
//     * @return matrix of DataOpcOld with same base
//     */
//    fun getDataOpcs(n: Int, m: Int): Array<Array<DataOpcOld>> {
//        if (state == State.Origin && !isReady) {
//            dataOpcOlds = directOpcGlobalBase(n, m, dataOrigin)
//            isReady = true
//        }
//
//        return dataOpcOlds
//    }
//}