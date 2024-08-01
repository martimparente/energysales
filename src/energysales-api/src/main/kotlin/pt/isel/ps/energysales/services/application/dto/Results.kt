package pt.isel.ps.energysales.services.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.services.domain.Service

typealias CreateServiceResult = Either<CreateServiceError, String>
typealias GetServiceResult = Either<GetServiceError, Service>
typealias UpdateServiceResult = Either<UpdateServiceError, Service>
typealias DeleteServiceResult = Either<DeleteServiceError, Boolean>

sealed interface CreateServiceError {
    data object ServiceAlreadyExists : CreateServiceError

    data object ServiceInfoIsInvalid : CreateServiceError

    data object ServiceNameIsInvalid : CreateServiceError

    data object ServiceSurnameIsInvalid : CreateServiceError

    data object ServiceEmailIsInvalid : CreateServiceError
}

sealed interface GetServiceError {
    data object ServiceAlreadyExists : GetServiceError

    data object ServiceNameIsInvalid : GetServiceError
}

sealed interface UpdateServiceError {
    data object ServiceNotFound : UpdateServiceError

    data object ServiceInfoIsInvalid : UpdateServiceError

    data object ServiceNameIsInvalid : UpdateServiceError

    data object ServiceSurnameIsInvalid : UpdateServiceError

    data object ServiceEmailIsInvalid : UpdateServiceError
}

sealed interface DeleteServiceError {
    data object ServiceNotFound : DeleteServiceError

    data object ServiceInfoIsInvalid : DeleteServiceError
}
