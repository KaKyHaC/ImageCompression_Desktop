package ImageCompressionLib.ProcessingModules

import ImageCompressionLib.Containers.Type.MyBufferedImage
import ImageCompressionLib.Containers.TripleShortMatrixOld
import ImageCompressionLib.Constants.State
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Utils.Objects.TimeManager

//import java.awt.image.MyBufferedImage;
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ModuleImage {
    //    private static final int SIZEOFBLOCK = 8;
    private var tripleShortMatrixOld: TripleShortMatrixOld? = null
    private var bitmap: MyBufferedImage? = null
    private var flag: Flag? = null

    private val byteVectorFromRGB: ByteVector
        get() {
            if (tripleShortMatrixOld!!.state != State.RGB)
                throw Exception("State not RGB")

            val vector = ByteVector(10)
            vector.append(tripleShortMatrixOld!!.Width.toShort())
            vector.append(tripleShortMatrixOld!!.Height.toShort())
            vector.append(flag!!.flag)

            for (i in 0 until tripleShortMatrixOld!!.Width) {
                for (j in 0 until tripleShortMatrixOld!!.Height) {
                    val r: Byte
                    val g: Byte
                    val b: Byte
                    assert(tripleShortMatrixOld!!.a[i][j] < 0xff)
                    assert(tripleShortMatrixOld!!.b[i][j] < 0xff)
                    assert(tripleShortMatrixOld!!.c[i][j] < 0xff)
                    r = tripleShortMatrixOld!!.a[i][j].toByte()
                    g = tripleShortMatrixOld!!.b[i][j].toByte()
                    b = tripleShortMatrixOld!!.c[i][j].toByte()
                    vector.append(r)
                    vector.append(g)
                    vector.append(b)
                }
            }
            return vector
        }

    //TODO make Async
    val bufferedImage: MyBufferedImage
        get() {
            when (tripleShortMatrixOld!!.state) {
                State.RGB -> FromRGBtoIBufferedImage()
                State.YBR -> FromYCbCrToIBufferedImage()
                State.Yenl -> {
                    PixelRestoration()
                    TimeManager.Instance.append("Restor")
                    FromYCbCrToIBufferedImage()
                    TimeManager.Instance.append("ybr to im")
                }
                State.bitmap -> {
                }
                else -> throw Exception("Sate not correct")
            }

            return bitmap!!
        }

    val rgbMatrixOld: TripleShortMatrixOld
        get() {
            when (tripleShortMatrixOld!!.state) {
                State.RGB -> {
                }
                State.YBR -> FromYBRtoRGB()
                State.Yenl -> {
                    PixelRestoration()
                    FromYBRtoRGB()
                }
                State.bitmap -> FromIBufferedImageToRGB()
                else -> throw Exception("state not correct")
            }

            return tripleShortMatrixOld!!
        }

    val byteVector: ByteVector
        get() {
            when (tripleShortMatrixOld!!.state) {
                State.RGB -> {
                }
                State.YBR -> FromYBRtoRGB()
                State.Yenl -> {
                    PixelRestoration()
                    FromYBRtoRGB()
                }
                State.bitmap -> FromIBufferedImageToRGB()
                else -> throw Exception("state not correct")
            }
            return byteVectorFromRGB
        }


    constructor(_b: MyBufferedImage, flag: Flag) {
        bitmap = _b
        tripleShortMatrixOld = TripleShortMatrixOld(bitmap!!.width, bitmap!!.height, State.bitmap)
        this.flag = flag
    }

    constructor(tripleShortMatrixOld: TripleShortMatrixOld, flag: Flag) {
        this.tripleShortMatrixOld = tripleShortMatrixOld
        bitmap = MyBufferedImage(tripleShortMatrixOld.Width, tripleShortMatrixOld.Height)//, MyBufferedImage.TYPE_3BYTE_BGR);
        this.flag = flag
    }

    constructor(vector: ByteVector, flag: Flag) {
        this.tripleShortMatrixOld = getMatrixFromByteVector(vector)
        bitmap = MyBufferedImage(tripleShortMatrixOld!!.Width, tripleShortMatrixOld!!.Height)//, MyBufferedImage.TYPE_3BYTE_BGR);
        this.flag = flag
    }

    //TODO string constructor
    //TODO fix threads
    private fun FromIBufferedImageToYCbCrParallelMatrix() {
        if (tripleShortMatrixOld!!.state == State.bitmap) {
            val w = bitmap!!.width
            val h = bitmap!!.height

            val executorService = Executors.newFixedThreadPool(4)
            val futures = arrayOfNulls<Future<*>>(4)

            val img = bitmap!!.getData()//convertTo2DWithoutUsingGetRGB(bitmap);
            futures[0] = executorService.submit { imageToYbrTask(0, 0, w / 2, h / 2, img) }
            futures[1] = executorService.submit { imageToYbrTask(w / 2, 0, w, h / 2, img) }
            futures[2] = executorService.submit { imageToYbrTask(0, h / 2, w / 2, h, img) }
            futures[3] = executorService.submit { imageToYbrTask(w / 2, h / 2, w, h, img) }

            for (future in futures) {
                try {
                    future!!.get()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }

            }
            tripleShortMatrixOld!!.state = State.YBR
        }

    }

    private fun FromIBufferedImageToYCbCr() {

        if (tripleShortMatrixOld!!.state == State.bitmap) {
            val img = bitmap!!.getData()//convertTo2DWithoutUsingGetRGB(bitmap);
            forEach(bitmap!!.width, bitmap!!.height, { x, y ->
                //                int pixelColor=bitmap.getRGB(x,y);
                val pixelColor = img[x][y]
                // получим цвет каждого пикселя
                val pixelRed = (pixelColor shr 16 and 0xFF).toDouble()
                val pixelGreen = (pixelColor shr 8 and 0xFF).toDouble()
                val pixelBlue = (pixelColor and 0xFF).toDouble()

                val vy = 0.299 * pixelRed + 0.587 * pixelGreen + 0.114 * pixelBlue
                val vcb = 128.0 - 0.168736 * pixelRed - 0.331264 * pixelGreen + 0.5 * pixelBlue
                val vcr = 128 + 0.5 * pixelRed - 0.418688 * pixelGreen - 0.081312 * pixelBlue

                //15.11
                //                   if(vy%1>=0.5)
                //                       vy++;
                //                   if(vcb%1>=0.5)
                //                       vcb++;
                //                   if(vcr%1>=0.5)
                //                       vcr++;


                tripleShortMatrixOld!!.a[x][y] = vy.toShort()
                tripleShortMatrixOld!!.b[x][y] = vcb.toShort()
                tripleShortMatrixOld!!.c[x][y] = vcr.toShort()

            })

            tripleShortMatrixOld!!.state = State.YBR
        }

    }

    private fun FromYCbCrToIBufferedImage() {
        if (tripleShortMatrixOld!!.state == State.YBR) {
            val pixelAlpha = 255 //for argb

            forEach(tripleShortMatrixOld!!.Width, tripleShortMatrixOld!!.Height, { x, y ->
                var r: Double
                var g: Double
                var b: Double
                r = tripleShortMatrixOld!!.a[x][y] + 1.402 * (tripleShortMatrixOld!!.c[x][y] - 128)
                g = tripleShortMatrixOld!!.a[x][y].toDouble() - 0.34414 * (tripleShortMatrixOld!!.b[x][y] - 128) - 0.71414 * (tripleShortMatrixOld!!.c[x][y] - 128)
                b = tripleShortMatrixOld!!.a[x][y] + 1.772 * (tripleShortMatrixOld!!.b[x][y] - 128)

                if (g < 0) g = 0.0//new
                if (r < 0) r = 0.0
                if (b < 0) b = 0.0

                if (r > 255) r = 255.0
                if (g > 255) g = 255.0
                if (b > 255) b = 255.0

                val pixelBlue = b.toInt() and 0xFF
                val pixelRed = r.toInt() and 0xFF
                val pixelGreen = g.toInt() and 0xFF

                //add
                //                if(r%1>=0.5)
                //                    pixelRed++;
                //                if(g%1>=0.5)
                //                    pixelGreen++;
                //                if(b%1>=0.5)
                //                    pixelBlue++;
                //


                //              int val = (pixelAlpha<<24)| (pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for argb
                val `val` = pixelRed shl 16 or (pixelGreen shl 8) or pixelBlue //for rgb

                // полученный результат вернём в MyBufferedImage
                bitmap!!.setRGB(x, y, `val`)
            })
            tripleShortMatrixOld!!.state = State.bitmap
        }
    }

    private fun FromIBufferedImageToRGB() {

        if (tripleShortMatrixOld!!.state == State.bitmap) {
            val Width = bitmap!!.width
            val Height = bitmap!!.height

            for (i in 0 until Width) {
                for (j in 0 until Height) {
                    //                    int pixelColor = rgb[i*Height + j];
                    val pixelColor = bitmap!!.getRGB(i, j)
                    // получим цвет каждого пикселя
                    tripleShortMatrixOld!!.a[i][j] = (pixelColor shr 16 and 0xFF).toShort()
                    tripleShortMatrixOld!!.b[i][j] = (pixelColor shr 8 and 0xFF).toShort()
                    tripleShortMatrixOld!!.c[i][j] = (pixelColor and 0xFF).toShort()

                }
            }
            tripleShortMatrixOld!!.state = State.RGB
        }
    }

    private fun FromRGBtoIBufferedImage() {
        if (tripleShortMatrixOld!!.state == State.RGB) {
            val Width = bitmap!!.width
            val Height = bitmap!!.height

            for (i in 0 until Width) {
                for (j in 0 until Height) {

                    val pixelAlpha = 255 //for argb
                    val pixelBlue = tripleShortMatrixOld!!.c[i][j].toInt() and 0xFF
                    val pixelRed = tripleShortMatrixOld!!.a[i][j].toInt() and 0xFF
                    val pixelGreen = tripleShortMatrixOld!!.b[i][j].toInt() and 0xFF
                    //                    int val =(pixelAlpha<<24)| (pixelRed<<16) | (pixelGreen<<8) | pixelBlue; //for argb
                    val `val` = pixelRed shl 16 or (pixelGreen shl 8) or pixelBlue //for rgb

                    // полученный результат вернём в MyBufferedImage
                    bitmap!!.setRGB(i, j, `val`)
                }
            }
            tripleShortMatrixOld!!.state = State.bitmap
        }
    }

    private fun FromYBRtoRGB() {

        if (tripleShortMatrixOld!!.state == State.YBR) {
            val Width = bitmap!!.width
            val Height = bitmap!!.height

            for (i in 0 until Width) {
                for (j in 0 until Height) {
                    var r: Double
                    var g: Double
                    var b: Double
                    r = tripleShortMatrixOld!!.a[i][j] + 1.402 * (tripleShortMatrixOld!!.c[i][j] - 128)
                    g = tripleShortMatrixOld!!.a[i][j].toDouble() - 0.34414 * (tripleShortMatrixOld!!.b[i][j] - 128) - 0.71414 * (tripleShortMatrixOld!!.c[i][j] - 128)
                    b = tripleShortMatrixOld!!.a[i][j] + 1.772 * (tripleShortMatrixOld!!.b[i][j] - 128)
                    //add
                    if (r % 1 >= 0.5)
                        r = (++r).toShort().toDouble()
                    if (g % 1 >= 0.5)
                        g = (++g).toShort().toDouble()
                    if (b % 1 >= 0.5)
                        b = (++b).toShort().toDouble()
                    //

                    if (g < 0) g = 0.0//new
                    if (r < 0) r = 0.0
                    if (b < 0) b = 0.0

                    if (r > 255) r = 255.0
                    if (g > 255) g = 255.0
                    if (b > 255) b = 255.0
                    tripleShortMatrixOld!!.a[i][j] = r.toShort()
                    tripleShortMatrixOld!!.b[i][j] = g.toShort()
                    tripleShortMatrixOld!!.c[i][j] = b.toShort()
                }
            }
            tripleShortMatrixOld!!.state = State.RGB
        }
    }

    private fun FromRGBtoYBR() {

        if (tripleShortMatrixOld!!.state == State.RGB) {
            val Width = bitmap!!.width
            val Height = bitmap!!.height

            for (i in 0 until Width) {
                for (j in 0 until Height) {
                    // получим цвет каждого пикселя
                    val pixelRed = tripleShortMatrixOld!!.a[i][j].toDouble()
                    val pixelGreen = tripleShortMatrixOld!!.b[i][j].toDouble()
                    val pixelBlue = tripleShortMatrixOld!!.c[i][j].toDouble()

                    val vy = 0.299 * pixelRed + 0.587 * pixelGreen + 0.114 * pixelBlue
                    val vcb = 128.0 - 0.168736 * pixelRed - 0.331264 * pixelGreen + 0.5 * pixelBlue
                    val vcr = 128 + 0.5 * pixelRed - 0.418688 * pixelGreen - 0.081312 * pixelBlue
                    //15.11
                    //                    if(vy%1>=0.5)
                    //                        vy++;
                    //                    if(vcb%1>=0.5)
                    //                        vcb++;
                    //                    if(vcr%1>=0.5)
                    //                        vcr++;

                    tripleShortMatrixOld!!.a[i][j] = vy.toShort()
                    tripleShortMatrixOld!!.b[i][j] = vcb.toShort()
                    tripleShortMatrixOld!!.c[i][j] = vcr.toShort()
                }
            }
            tripleShortMatrixOld!!.state = State.YBR
        }
    }

    private fun PixelEnlargement() {
        if (tripleShortMatrixOld!!.state == State.YBR && flag!!.isChecked(Flag.Parameter.Enlargement)) {
            val cWidth = tripleShortMatrixOld!!.Width / 2
            val cHeight = tripleShortMatrixOld!!.Height / 2
            val enlCb = Array(cWidth) { ShortArray(cHeight) }
            val enlCr = Array(cWidth) { ShortArray(cHeight) }
            for (i in 0 until cWidth) {
                for (j in 0 until cHeight) {
                    enlCb[i][j] = ((tripleShortMatrixOld!!.b[i * 2][j * 2].toInt() + tripleShortMatrixOld!!.b[i * 2 + 1][j * 2].toInt()
                            + tripleShortMatrixOld!!.b[i * 2][j * 2 + 1].toInt() + tripleShortMatrixOld!!.b[i * 2 + 1][j * 2 + 1].toInt()) / 4).toShort()
                    enlCr[i][j] = ((tripleShortMatrixOld!!.c[i * 2][j * 2].toInt() + tripleShortMatrixOld!!.c[i * 2 + 1][j * 2].toInt()
                            + tripleShortMatrixOld!!.c[i * 2][j * 2 + 1].toInt() + tripleShortMatrixOld!!.c[i * 2 + 1][j * 2 + 1].toInt()) / 4).toShort()
                }
            }
            tripleShortMatrixOld!!.b = enlCb
            tripleShortMatrixOld!!.c = enlCr
            tripleShortMatrixOld!!.state = State.Yenl
        }

    }

    private fun PixelRestoration() {

        if (tripleShortMatrixOld!!.state == State.Yenl && flag!!.isChecked(Flag.Parameter.Enlargement)) {
            val cWidth = tripleShortMatrixOld!!.Width / 2
            val cHeight = tripleShortMatrixOld!!.Height / 2
            val Width = tripleShortMatrixOld!!.Width
            val Height = tripleShortMatrixOld!!.Height
            val Cb = Array(Width) { ShortArray(Height) }
            val Cr = Array(Width) { ShortArray(Height) }
            for (i in 0 until cWidth) {
                for (j in 0 until cHeight) {
                    Cb[i * 2 + 1][j * 2 + 1] = tripleShortMatrixOld!!.b[i][j]
                    Cb[i * 2][j * 2 + 1] = Cb[i * 2 + 1][j * 2 + 1]
                    Cb[i * 2 + 1][j * 2] = Cb[i * 2][j * 2 + 1]
                    Cb[i * 2][j * 2] = Cb[i * 2 + 1][j * 2]
                    Cr[i * 2 + 1][j * 2 + 1] = tripleShortMatrixOld!!.c[i][j]
                    Cr[i * 2][j * 2 + 1] = Cr[i * 2 + 1][j * 2 + 1]
                    Cr[i * 2 + 1][j * 2] = Cr[i * 2][j * 2 + 1]
                    Cr[i * 2][j * 2] = Cr[i * 2 + 1][j * 2]
                }
            }
            tripleShortMatrixOld!!.b = Cb
            tripleShortMatrixOld!!.c = Cr

            tripleShortMatrixOld!!.state = State.YBR
        }
    }

    private fun minus128(arr: Array<ShortArray>) {
        for (i in arr.indices) {
            for (j in 0 until arr[0].size) {
                arr[i][j] = (arr[i][j]-128).toShort()
            }
        }
    }

    private fun plus128(arr: Array<ShortArray>) {
        for (i in arr.indices) {
            for (j in 0 until arr[0].size) {
                arr[i][j] = (arr[i][j]+128).toShort()
            }
        }
    }

    private fun minus128() {
        minus128(tripleShortMatrixOld!!.a)
        minus128(tripleShortMatrixOld!!.b)
        minus128(tripleShortMatrixOld!!.c)
    }

    private fun plus128() {
        plus128(tripleShortMatrixOld!!.a)
        plus128(tripleShortMatrixOld!!.b)
        plus128(tripleShortMatrixOld!!.c)
    }

    private fun getMatrixFromByteVector(vector: ByteVector): TripleShortMatrixOld {
        val w = vector.getNextShort().toInt()
        val h = vector.getNextShort().toInt()
        val flag = Flag(vector.getNextShort())
        val tripleShortMatrix = TripleShortMatrixOld(w, h, State.RGB)
        for (i in 0 until w) {
            for (j in 0 until h) {
                tripleShortMatrix.a[i][j] = (vector.getNext().toInt() and 0xff).toShort()
                tripleShortMatrix.b[i][j] = (vector.getNext().toInt() and 0xff).toShort()
                tripleShortMatrix.c[i][j] = (vector.getNext().toInt() and 0xff).toShort()
            }
        }
        return tripleShortMatrix
    }

    fun getYCbCrMatrix(isAsync: Boolean): TripleShortMatrixOld {
        when (tripleShortMatrixOld!!.state) {
            State.RGB -> FromRGBtoYBR()
            State.YBR -> {
            }
            State.Yenl -> PixelRestoration()
            State.bitmap -> if (isAsync)
                FromIBufferedImageToYCbCrParallelMatrix()
            else
                FromIBufferedImageToYCbCr()
            else -> throw Exception("State not correct")
        }

        return tripleShortMatrixOld!!
    }

    fun getYenlMatrix(isAsync: Boolean): TripleShortMatrixOld {
        TimeManager.Instance.append("start Yenl")
        when (tripleShortMatrixOld!!.state) {
            State.RGB -> {
                FromRGBtoYBR()
                PixelEnlargement()
            }
            State.YBR -> PixelEnlargement()
            State.Yenl -> {
            }
            State.bitmap -> {
                if (isAsync)
                    FromIBufferedImageToYCbCrParallelMatrix()
                else
                    FromIBufferedImageToYCbCr()

                TimeManager.Instance.append("im to ybr")
                PixelEnlargement()
                TimeManager.Instance.append("enl")
            }
            else -> throw Exception("State not correct")
        }
        return tripleShortMatrixOld!!
    }


    private fun imageToYbrTask(wStart: Int, hStart: Int, wEnd: Int, hEnd: Int, image: Array<IntArray>) {
        appendTimeManager("imageToYbrTask($wStart)($hStart)")
        val w = wEnd - wStart
        val h = hEnd - hStart
        val _a = Array(w) { ShortArray(h) }
        val _b = Array(w) { ShortArray(h) }
        val _c = Array(w) { ShortArray(h) }
        val rgb = Array(w) { IntArray(h) }
        appendTimeManager("mem" + wStart + hStart)
        for (i in 0 until w) {
            for (j in 0 until h) {
                rgb[i][j] = image[i + wStart][j + hStart]//image[ j + hStart][i + wStart];
            }
        }

        appendTimeManager("rgb cpy" + wStart + hStart)
        for (i in 0 until w) {
            for (j in 0 until h) {
                val pixelColor = rgb[i][j]
                // получим цвет каждого пикселя
                val pixelRed = (pixelColor shr 16 and 0xFF).toDouble()
                val pixelGreen = (pixelColor shr 8 and 0xFF).toDouble()
                val pixelBlue = (pixelColor and 0xFF).toDouble()

                val vy = 0.299 * pixelRed + 0.587 * pixelGreen + 0.114 * pixelBlue
                val vcb = 128.0 - 0.168736 * pixelRed - 0.331264 * pixelGreen + 0.5 * pixelBlue
                val vcr = 128 + 0.5 * pixelRed - 0.418688 * pixelGreen - 0.081312 * pixelBlue

                //15.11
                //                   if(vy%1>=0.5)
                //                       vy++;
                //                   if(vcb%1>=0.5)
                //                       vcb++;
                //                   if(vcr%1>=0.5)
                //                       vcr++;

                //                tripleShortMatrixOld.getA()[i][j] = (short) vy;
                //                tripleShortMatrixOld.getB()[i][j] = (short) vcb;
                //                tripleShortMatrixOld.getC()[i][j] = (short) vcr;

                _a[i][j] = vy.toShort()
                _b[i][j] = vcb.toShort()
                _c[i][j] = vcr.toShort()

            }
        }
        appendTimeManager("ybr calc" + wStart + hStart)
        for (i in wStart until wEnd) {
            for (j in hStart until hEnd) {
                tripleShortMatrixOld!!.a[i][j] = _a[i - wStart][j - hStart]
                tripleShortMatrixOld!!.b[i][j] = _b[i - wStart][j - hStart]
                tripleShortMatrixOld!!.c[i][j] = _c[i - wStart][j - hStart]
            }
        }
        appendTimeManager("ybr set" + wStart + hStart)
    }

    private fun appendTimeManager(s: String) {
        //        TimeManager.getInstance().append(s);
    }

    private fun convertTo2DWithoutUsingGetRGB(image: MyBufferedImage): Array<IntArray> {

        val pixels = image.getDataBuffer()//((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        val width = image.width
        val height = image.height
        val hasAlphaChannel = false// image.getAlphaRaster() != null;

        val result = Array(height) { IntArray(width) }
        if (hasAlphaChannel) {
            val pixelLength = 4
            var pixel = 0
            var row = 0
            var col = 0
            while (pixel < pixels.size) {
                var argb = 0
                argb += pixels[pixel].toInt() and 0xff shl 24 // alpha
                argb += pixels[pixel + 1].toInt() and 0xff // blue
                argb += pixels[pixel + 2].toInt() and 0xff shl 8 // green
                argb += pixels[pixel + 3].toInt() and 0xff shl 16 // red
                result[row][col] = argb
                col++
                if (col == width) {
                    col = 0
                    row++
                }
                pixel += pixelLength
            }
        } else {
            val pixelLength = 3
            var pixel = 0
            var row = 0
            var col = 0
            while (pixel < pixels.size) {
                var argb = 0
                argb += -16777216 // 255 alpha
                argb += pixels[pixel].toInt() and 0xff // blue
                argb += pixels[pixel + 1].toInt() and 0xff shl 8 // green
                argb += pixels[pixel + 2].toInt() and 0xff shl 16 // red
                result[row][col] = argb
                col++
                if (col == width) {
                    col = 0
                    row++
                }
                pixel += pixelLength
            }
        }

        return result
    }


    private fun forEach(w: Int, h: Int, `fun`: (x:Int,y:Int)->Unit) {
        for (i in 0 until w) {
            for (j in 0 until h) {
                `fun`.invoke(i, j)
            }
        }
    }
}
