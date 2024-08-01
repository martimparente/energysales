package pt.isel.ps.energysales.clients.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.clients.domain.Client

typealias CreateClientResult = Either<CreateClientError, String>
typealias GetClientResult = Either<GetClientError, Client>
typealias UpdateClientResult = Either<UpdateClientError, Client>
typealias DeleteClientResult = Either<DeleteClientError, Boolean>

sealed interface CreateClientError {
    data object ClientAlreadyExists : CreateClientError

    data object ClientInfoIsInvalid : CreateClientError

    data object ClientNameIsInvalid : CreateClientError

    data object ClientSurnameIsInvalid : CreateClientError

    data object ClientEmailIsInvalid : CreateClientError
}

sealed interface GetClientError {
    data object ClientAlreadyExists : GetClientError

    data object ClientNameIsInvalid : GetClientError
}

sealed interface UpdateClientError {
    data object ClientNotFound : UpdateClientError

    data object ClientInfoIsInvalid : UpdateClientError

    data object ClientNameIsInvalid : UpdateClientError

    data object ClientSurnameIsInvalid : UpdateClientError

    data object ClientEmailIsInvalid : UpdateClientError
}

sealed interface DeleteClientError {
    data object ClientNotFound : DeleteClientError

    data object ClientInfoIsInvalid : DeleteClientError
}
