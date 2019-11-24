package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Data.Containers.Quartet

object EnlargementUtils : IUtils<Quartet<Short>, Short> {

    override fun direct(input: Quartet<Short>) = input.values()
        .reduce { acc, sh -> (acc + sh).toShort() }
        .div(4)
        .toShort()

    override fun reverse(output: Short) = Quartet(
        output, output, output, output
    )
}