package pt.isel.ps.energysales.clients.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.clients.domain.Offer

typealias CreateOfferResult = Either<CreateOfferError, CreateOfferOutput>
typealias GetOfferResult = Either<GetOfferError, Offer>
typealias DeleteOfferResult = Either<DeleteOfferError, Boolean>
typealias SendOfferEmailResult = Either<SendOfferEmailError, Boolean>

sealed interface CreateOfferError {
    data object OfferAlreadyExists : CreateOfferError

    data object OfferInfoIsInvalid : CreateOfferError

    data object OfferNameIsInvalid : CreateOfferError

    data object OfferSurnameIsInvalid : CreateOfferError

    data object OfferEmailIsInvalid : CreateOfferError
}

sealed interface GetOfferError {
    data object OfferAlreadyExists : GetOfferError

    data object OfferNameIsInvalid : GetOfferError
}

sealed interface OfferUpdatingError {
    data object OfferNotFound : OfferUpdatingError

    data object OfferInfoIsInvalid : OfferUpdatingError

    data object OfferNameIsInvalid : OfferUpdatingError

    data object OfferSurnameIsInvalid : OfferUpdatingError

    data object OfferEmailIsInvalid : OfferUpdatingError
}

sealed interface DeleteOfferError {
    data object OfferNotFound : DeleteOfferError

    data object OfferInfoIsInvalid : DeleteOfferError
}

sealed interface SendOfferEmailError {
    data object OfferNotFound : SendOfferEmailError
}
