package ImageCompression.Objects

import ImageCompression.Containers.BoxOfOPC
import ImageCompression.Containers.Matrix
import ImageCompression.Utils.Functions.Steganography
import ImageCompression.Utils.Objects.Flag

class ModuleOPC {
    private var matrix:Matrix
    private var opcs:BoxOfOPC
    private var isMatrix=false
    private var isOPCS=false

    constructor(matrix: Matrix){
        this.matrix=matrix
        opcs= BoxOfOPC(matrix.Width,matrix.Height,matrix.f.isEnlargement)
        isMatrix=true
    }
    constructor(boxOfOPC: BoxOfOPC,flag: Flag){
        this.opcs=boxOfOPC
        matrix= Matrix(opcs.width,opcs.height,flag)
        isOPCS=true
    }

    fun FromMatrixToOpcs(){
        Steganography.getInstance().
    }


    fun getMatrix():Matrix{
        if(isMatrix)
            return matrix
    }
    fun getOPCS():BoxOfOPC{
        if(isOPCS)
            return opcs
    }

}