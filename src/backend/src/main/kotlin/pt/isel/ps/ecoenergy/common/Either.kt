package pt.isel.ps.ecoenergy.common

// Kotlin Either logic based on sealed classes
// Left is "exception" or error
// Right is data that can be anything(T)

sealed class Either<out L, out R> {
    data class Left<out L>(
        val value: L,
    ) : Either<L, Nothing>()

    data class Right<out R>(
        val value: R,
    ) : Either<Nothing, R>()
}
