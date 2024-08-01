package pt.isel.ps.energysales.clients.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.clients.domain.Offer

typealias OfferCreationResult = Either<OfferCreationError, CreateOfferOutput>
typealias OfferReadingResult = Either<OfferReadingError, Offer>
typealias OfferDeletingResult = Either<OfferDeletingError, Boolean>
typealias SendOfferEmailResult = Either<SendOfferEmailError, Boolean>

sealed interface OfferCreationError {
    data object OfferAlreadyExists : OfferCreationError

    data object OfferInfoIsInvalid : OfferCreationError

    data object OfferNameIsInvalid : OfferCreationError

    data object OfferSurnameIsInvalid : OfferCreationError

    data object OfferEmailIsInvalid : OfferCreationError
}

sealed interface OfferReadingError {
    data object OfferAlreadyExists : OfferReadingError

    data object OfferNameIsInvalid : OfferReadingError
}

sealed interface OfferUpdatingError {
    data object OfferNotFound : OfferUpdatingError

    data object OfferInfoIsInvalid : OfferUpdatingError

    data object OfferNameIsInvalid : OfferUpdatingError

    data object OfferSurnameIsInvalid : OfferUpdatingError

    data object OfferEmailIsInvalid : OfferUpdatingError
}

sealed interface OfferDeletingError {
    data object OfferNotFound : OfferDeletingError

    data object OfferInfoIsInvalid : OfferDeletingError
}

sealed interface SendOfferEmailError {
    data object OfferNotFound : SendOfferEmailError
}
