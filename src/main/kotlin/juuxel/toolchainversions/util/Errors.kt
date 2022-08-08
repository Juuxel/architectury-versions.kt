package juuxel.toolchainversions.util

import arrow.core.Either
import kotlin.system.exitProcess

object ErrorScope {
    fun <B> Either<String, B>.bind(): B =
        when (this) {
            is Either.Left -> {
                System.err.println(value)
                exitProcess(1)
            }
            is Either.Right -> value
        }
}
