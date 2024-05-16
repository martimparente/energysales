package pt.isel.ps.ecoenergy.sellers.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.ecoenergy.sellers.data.Role
import pt.isel.ps.ecoenergy.sellers.data.SellerRepository
import pt.isel.ps.ecoenergy.sellers.domain.model.Person
import pt.isel.ps.ecoenergy.sellers.domain.model.Seller

class SellerService(
    private val sellerRepository: SellerRepository,
) {
    // Create
    suspend fun createSeller(
        name: String,
        surname: String,
        email: String,
    ): SellerCreationResult =
        either {
            ensure(name.length in 2..16) { SellerCreationError.SellerNameIsInvalid }
            ensure(surname.length in 2..16) { SellerCreationError.SellerSurnameIsInvalid }
            ensure(isValidEmail(email)) { SellerCreationError.SellerEmailIsInvalid }
            ensure(sellerRepository.isEmailAvailable(email)) { SellerCreationError.SellerAlreadyExists }

            sellerRepository.create(
                Seller(
                    person = Person(-1, name, surname, email, Role.SELLER),
                    totalSales = 0.0f,
                    team = null,
                ),
            )
        }

    // Read
    suspend fun getAllSellers() = sellerRepository.getAll()

    suspend fun getAllSellersPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ) = sellerRepository.getAllKeyPaging(pageSize, lastKeySeen)

    // suspend fun getByName(name: String): Seller? = sellerRepository.getByName(name)

    suspend fun getById(id: Int): Seller? = sellerRepository.getById(id)

    // Update
    suspend fun updateSeller(seller: Seller): SellerUpdatingResult =
        either {
            ensure(seller.person.name.length in 2..16) { SellerUpdatingError.SellerNameIsInvalid }
            ensure(seller.person.surname.length in 2..16) { SellerUpdatingError.SellerSurnameIsInvalid }
            ensure(isValidEmail(seller.person.email)) { SellerUpdatingError.SellerEmailIsInvalid }
            ensure(!sellerRepository.isEmailAvailable(seller.person.email)) { SellerUpdatingError.SellerNotFound }
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

    data object SellerNameIsInvalid : SellerCreationError

    data object SellerSurnameIsInvalid : SellerCreationError

    data object SellerEmailIsInvalid : SellerCreationError

//    data object SellerSurnameIsInvalid : SellerCreationError
}

sealed interface SellerReadingError {
    data object SellerAlreadyExists : SellerReadingError

    data object SellerNameIsInvalid : SellerReadingError
}

sealed interface SellerUpdatingError {
    data object SellerNotFound : SellerUpdatingError

    data object SellerInfoIsInvalid : SellerUpdatingError

    data object SellerNameIsInvalid : SellerUpdatingError

    data object SellerSurnameIsInvalid : SellerUpdatingError

    data object SellerEmailIsInvalid : SellerUpdatingError
}

sealed interface SellerDeletingError {
    data object SellerNotFound : SellerDeletingError

    data object SellerInfoIsInvalid : SellerDeletingError
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
    return emailRegex.matches(email)
}
