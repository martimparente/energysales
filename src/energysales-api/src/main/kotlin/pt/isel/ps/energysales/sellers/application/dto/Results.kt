package pt.isel.ps.energysales.sellers.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.sellers.domain.Seller

typealias CreateSellerResult = Either<CreateSellerError, String>
typealias GetSellerResult = Either<GetSellerError, Seller>
typealias GetAllSellersResult = Either<GetSellerError, List<Seller>>
typealias UpdateSellerResult = Either<UpdateSellerError, Seller>
typealias DeleteSellerResult = Either<DeleteSellerError, Boolean>

sealed interface CreateSellerError {
    data object SellerAlreadyExists : CreateSellerError

    data object SellerInfoIsInvalid : CreateSellerError
}

sealed interface GetSellerError {
    data object SellerAlreadyExists : GetSellerError

    data object SellerNameIsInvalid : GetSellerError
}

sealed interface UpdateSellerError {
    data object SellerNotFound : UpdateSellerError

    data object SellerInfoIsInvalid : UpdateSellerError
}

sealed interface DeleteSellerError {
    data object SellerNotFound : DeleteSellerError

    data object SellerInfoIsInvalid : DeleteSellerError
}
