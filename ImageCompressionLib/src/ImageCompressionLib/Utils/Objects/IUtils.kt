package ImageCompressionLib.Utils.Objects

interface IUtils<I, O> {
    fun direct(input: I): O
    fun reverse(output: O): I
}