package pt.isel.ps.energysales.clients.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import pt.isel.ps.energysales.clients.application.dto.CreateOfferInput
import pt.isel.ps.energysales.clients.application.dto.CreateOfferOutput
import pt.isel.ps.energysales.clients.data.ClientRepository
import pt.isel.ps.energysales.clients.data.OfferRepository
import pt.isel.ps.energysales.clients.domain.Offer
import pt.isel.ps.energysales.clients.domain.OfferLink
import pt.isel.ps.energysales.sellers.data.SellerRepository
import pt.isel.ps.energysales.services.data.ServiceRepository
import java.util.UUID

class OfferService(
    private val offerRepository: OfferRepository,
    private val sellerRepository: SellerRepository,
    private val clientRepository: ClientRepository,
    private val serviceRepository: ServiceRepository,
) {
    // Create
    suspend fun createOffer(input: CreateOfferInput): OfferCreationResult =
        either {
            ensure(input.dueInDays > 0) { OfferCreationError.OfferInfoIsInvalid }
            val seller = sellerRepository.getById(input.createdBy.toInt())
            ensureNotNull(seller) { OfferCreationError.OfferInfoIsInvalid }
            val client = clientRepository.getById(input.clientId.toInt())
            ensureNotNull(client) { OfferCreationError.OfferInfoIsInvalid }
            val service = serviceRepository.getById(input.serviceId.toInt())
            ensureNotNull(service) { OfferCreationError.OfferInfoIsInvalid }

            // Generate due date time based on current date time and due in days
            val currentDateTime = Clock.System.now()
            val dueDateTime =
                currentDateTime
                    .plus(input.dueInDays.toLong(), DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                    .toLocalDateTime(TimeZone.currentSystemDefault())

            val offerUUID = UUID.randomUUID()

            val offer =
                Offer(
                    -1,
                    currentDateTime.toLocalDateTime(TimeZone.currentSystemDefault()),
                    seller,
                    client,
                    service,
                    OfferLink(offerUUID, "https://TODO.COM/" + offerUUID, dueDateTime),
                )
            offerRepository.create(offer)
            CreateOfferOutput(offer.link.url, dueDateTime.format(LocalDateTime.Formats.ISO))
        }

    suspend fun getById(id: Int): Offer? = offerRepository.getById(id)

    suspend fun deleteOffer(id: Int): OfferDeletingResult =
        either {
            val offer = offerRepository.getById(id)
            ensureNotNull(offer) { OfferDeletingError.OfferNotFound }
            offerRepository.delete(offer)
        }
}

typealias OfferCreationResult = Either<OfferCreationError, CreateOfferOutput>
typealias OfferReadingResult = Either<OfferReadingError, Offer>
typealias OfferDeletingResult = Either<OfferDeletingError, Boolean>

sealed interface OfferCreationError {
    data object OfferAlreadyExists : OfferCreationError

    data object OfferInfoIsInvalid : OfferCreationError

    data object OfferNameIsInvalid : OfferCreationError

    data object OfferSurnameIsInvalid : OfferCreationError

    data object OfferEmailIsInvalid : OfferCreationError

//    data object OfferSurnameIsInvalid : OfferCreationError
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
