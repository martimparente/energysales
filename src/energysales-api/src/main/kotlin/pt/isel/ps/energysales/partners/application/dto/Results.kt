package pt.isel.ps.energysales.partners.application.dto

import arrow.core.Either
import pt.isel.ps.energysales.sellers.domain.Seller

typealias CreatePartnerResult = Either<CreatePartnerError, Int>
typealias GetPartnerSellersResult = Either<GetPartnerSellersError, List<Seller>>
typealias UpdatePartnerResult = Either<UpdatePartnerError, Unit>
typealias DeletePartnerResult = Either<DeletePartnerError, Unit>
typealias AddPartnerSellerResult = Either<AddPartnerSellerError, Unit>
typealias DeletePartnerSellerResult = Either<DeletePartnerSellerError, Unit>
typealias AddPartnerPartnerResult = Either<AddPartnerServiceError, Unit>
typealias DeletePartnerServiceResult = Either<DeletePartnerServiceError, Unit>
typealias AddPartnerClientResult = Either<AddPartnerClientError, Unit>
typealias AddPartnerAvatarResult = Either<AddPartnerAvatarError, String>

sealed interface CreatePartnerError {
    data object PartnerAlreadyExists : CreatePartnerError

    data object PartnerInfoIsInvalid : CreatePartnerError
}

sealed interface GetPartnerSellersError {
    data object PartnerNotFound : GetPartnerSellersError

    data object SellerNotFound : GetPartnerSellersError
}

sealed interface UpdatePartnerError {
    data object PartnerNotFound : UpdatePartnerError

    data object PartnerInfoIsInvalid : UpdatePartnerError
}

sealed interface DeletePartnerError {
    data object PartnerNotFound : DeletePartnerError

    data object PartnerInfoIsInvalid : DeletePartnerError
}

sealed interface AddPartnerSellerError {
    data object PartnerNotFound : AddPartnerSellerError

    data object SellerNotFound : AddPartnerSellerError
}

sealed interface DeletePartnerSellerError {
    data object PartnerNotFound : DeletePartnerSellerError

    data object SellerNotFound : DeletePartnerSellerError
}

sealed interface AddPartnerServiceError {
    data object PartnerNotFound : AddPartnerServiceError

    data object ServiceNotFound : AddPartnerServiceError
}

sealed interface DeletePartnerServiceError {
    data object PartnerNotFound : DeletePartnerServiceError

    data object ServiceNotFound : DeletePartnerServiceError
}

sealed interface AddPartnerClientError {
    data object PartnerNotFound : AddPartnerClientError

    data object SellerNotFound : AddPartnerClientError
}

sealed interface AddPartnerAvatarError {
    data object PartnerNotFound : AddPartnerAvatarError

    data object AvatarImgNotFound : AddPartnerAvatarError
}
