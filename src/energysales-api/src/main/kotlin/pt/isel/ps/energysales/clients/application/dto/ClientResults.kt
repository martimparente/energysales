package pt.isel.ps.energysales.clients.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.clients.domain.Client

typealias ClientCreationResult = Either<ClientCreationError, String>
typealias ClientReadingResult = Either<ClientReadingError, Client>
typealias ClientUpdatingResult = Either<ClientUpdatingError, Client>
typealias ClientDeletingResult = Either<ClientDeletingError, Boolean>

sealed interface ClientCreationError {
    data object ClientAlreadyExists : ClientCreationError

    data object ClientInfoIsInvalid : ClientCreationError

    data object ClientNameIsInvalid : ClientCreationError

    data object ClientSurnameIsInvalid : ClientCreationError

    data object ClientEmailIsInvalid : ClientCreationError
}

sealed interface ClientReadingError {
    data object ClientAlreadyExists : ClientReadingError

    data object ClientNameIsInvalid : ClientReadingError
}

sealed interface ClientUpdatingError {
    data object ClientNotFound : ClientUpdatingError

    data object ClientInfoIsInvalid : ClientUpdatingError

    data object ClientNameIsInvalid : ClientUpdatingError

    data object ClientSurnameIsInvalid : ClientUpdatingError

    data object ClientEmailIsInvalid : ClientUpdatingError
}

sealed interface ClientDeletingError {
    data object ClientNotFound : ClientDeletingError

    data object ClientInfoIsInvalid : ClientDeletingError
}
