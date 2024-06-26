package pt.isel.ps.energysales.sellers.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.auth.domain.model.Role
import pt.isel.ps.energysales.auth.domain.model.User
import pt.isel.ps.energysales.sellers.data.SellerRepository
import pt.isel.ps.energysales.sellers.domain.model.Seller
import pt.isel.ps.energysales.sellers.domain.service.dto.CreateSellerInput

class SellerService(
    private val sellerRepository: SellerRepository,
) {
    // Create
    suspend fun createSeller(info: CreateSellerInput): SellerCreationResult =
        either {
            val user = User(-1, info.name, info.surname, info.email, Role.SELLER)
            val seller = Seller(user, 0.0f, info.team)

            sellerRepository.create(seller)
        }

    // Read
    suspend fun getAllSellers() = sellerRepository.getAll()

    suspend fun getAllSellersPaging(
        pageSize: Int,
        lastKeySeen: Int?,
        noTeam: Boolean,
    ) = sellerRepository.getAllKeyPaging(pageSize, lastKeySeen, noTeam)

    // suspend fun getByName(name: String): Seller? = sellerRepository.getByName(name)

    suspend fun getById(id: Int): Seller? = sellerRepository.getById(id)

    // Update
    suspend fun updateSeller(seller: Seller): SellerUpdatingResult =
        either {
            val updatedSeller = sellerRepository.update(seller)
            ensureNotNull(updatedSeller) { SellerUpdatingError.SellerNotFound }
        }

    suspend fun deleteSeller(id: Int): SellerDeletingResult =
        either {
            val seller = sellerRepository.getById(id)
            ensureNotNull(seller) { SellerDeletingError.SellerNotFound }
            sellerRepository.delete(seller)
        }
}

typealias SellerCreationResult = Either<SellerCreationError, Int>
typealias SellerReadingResult = Either<SellerReadingError, Seller>
typealias SellerUpdatingResult = Either<SellerUpdatingError, Seller>
typealias SellerDeletingResult = Either<SellerDeletingError, Boolean>

sealed interface SellerCreationError {
    data object SellerAlreadyExists : SellerCreationError

    data object SellerInfoIsInvalid : SellerCreationError
}

sealed interface SellerReadingError {
    data object SellerAlreadyExists : SellerReadingError

    data object SellerNameIsInvalid : SellerReadingError
}

sealed interface SellerUpdatingError {
    data object SellerNotFound : SellerUpdatingError

    data object SellerInfoIsInvalid : SellerUpdatingError
}

sealed interface SellerDeletingError {
    data object SellerNotFound : SellerDeletingError

    data object SellerInfoIsInvalid : SellerDeletingError
}
