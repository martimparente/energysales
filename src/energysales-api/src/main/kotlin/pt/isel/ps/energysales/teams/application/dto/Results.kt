package pt.isel.ps.energysales.teams.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.sellers.domain.Seller

typealias CreateTeamResult = Either<CreateTeamError, Int>
typealias UpdateTeamResult = Either<UpdateTeamError, Unit>
typealias DeleteTeamResult = Either<DeleteTeamError, Unit>
typealias GetTeamSellersResult = Either<GetTeamSellersError, List<Seller>>
typealias AddTeamSellerResult = Either<AddTeamSellerError, Unit>
typealias DeleteTeamSellerResult = Either<DeleteTeamSellerError, Unit>
typealias AddTeamServiceResult = Either<AddTeamServiceError, Unit>
typealias DeleteTeamServiceResult = Either<DeleteTeamServiceError, Unit>
typealias AddTeamClientResult = Either<AddTeamClientError, Unit>

sealed interface CreateTeamError {
    data object TeamAlreadyExists : CreateTeamError

    data object TeamInfoIsInvalid : CreateTeamError
}

sealed interface GetTeamSellersError {
    data object TeamNotFound : GetTeamSellersError

    data object SellerNotFound : GetTeamSellersError
}

sealed interface UpdateTeamError {
    data object TeamNotFound : UpdateTeamError

    data object TeamInfoIsInvalid : UpdateTeamError
}

sealed interface DeleteTeamError {
    data object TeamNotFound : DeleteTeamError

    data object TeamInfoIsInvalid : DeleteTeamError
}

sealed interface AddTeamSellerError {
    data object TeamNotFound : AddTeamSellerError

    data object SellerNotFound : AddTeamSellerError
}

sealed interface DeleteTeamSellerError {
    data object TeamNotFound : DeleteTeamSellerError

    data object SellerNotFound : DeleteTeamSellerError
}

sealed interface AddTeamServiceError {
    data object TeamNotFound : AddTeamServiceError

    data object ServiceNotFound : AddTeamServiceError
}

sealed interface DeleteTeamServiceError {
    data object TeamNotFound : DeleteTeamServiceError

    data object ServiceNotFound : DeleteTeamServiceError
}

sealed interface AddTeamClientError {
    data object TeamNotFound : AddTeamClientError

    data object SellerNotFound : AddTeamClientError
}
