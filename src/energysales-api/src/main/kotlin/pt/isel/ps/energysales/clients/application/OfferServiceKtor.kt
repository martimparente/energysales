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
import pt.isel.ps.energysales.clients.application.dto.CreateOfferError
import pt.isel.ps.energysales.clients.application.dto.CreateOfferInput
import pt.isel.ps.energysales.clients.application.dto.CreateOfferOutput
import pt.isel.ps.energysales.clients.application.dto.CreateOfferResult
import pt.isel.ps.energysales.clients.application.dto.DeleteOfferError
import pt.isel.ps.energysales.clients.application.dto.DeleteOfferResult
import pt.isel.ps.energysales.clients.application.dto.SendOfferEmailError
import pt.isel.ps.energysales.clients.application.dto.SendOfferEmailResult
import pt.isel.ps.energysales.clients.data.ClientRepository
import pt.isel.ps.energysales.clients.data.OfferRepository
import pt.isel.ps.energysales.clients.domain.Offer
import pt.isel.ps.energysales.clients.domain.OfferLink
import pt.isel.ps.energysales.email.MailService
import pt.isel.ps.energysales.sellers.data.SellerRepository
import pt.isel.ps.energysales.services.data.ServiceRepository
import java.util.UUID

class OfferServiceKtor(
    private val offerRepository: OfferRepository,
    private val sellerRepository: SellerRepository,
    private val clientRepository: ClientRepository,
    private val serviceRepository: ServiceRepository,
    private val mailService: MailService,
) : OfferService {
    // Create
    override suspend fun createOffer(input: CreateOfferInput): CreateOfferResult =
        either {
            ensure(input.dueInDays > 0) { CreateOfferError.OfferInfoIsInvalid }
            val seller = sellerRepository.getById(input.createdBy)
            ensureNotNull(seller) { CreateOfferError.OfferInfoIsInvalid }
            val client = clientRepository.getById(input.clientId)
            ensureNotNull(client) { CreateOfferError.OfferInfoIsInvalid }
            val service = serviceRepository.getById(input.serviceId)
            ensureNotNull(service) { CreateOfferError.OfferInfoIsInvalid }

            // Generate due date time based on current date time and due in days
            val currentDateTime = Clock.System.now()
            val dueDateTime =
                currentDateTime
                    .plus(input.dueInDays.toLong(), DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                    .toLocalDateTime(TimeZone.currentSystemDefault())

            val offerUUID = UUID.randomUUID()

            val offer =
                Offer(
                    null,
                    currentDateTime.toLocalDateTime(TimeZone.currentSystemDefault()),
                    seller,
                    client,
                    service,
                    OfferLink(offerUUID, "https://TODO.COM/" + offerUUID, dueDateTime),
                )
            offerRepository.create(offer)
            CreateOfferOutput(offer.link.url, dueDateTime.format(LocalDateTime.Formats.ISO))
        }

    override suspend fun getById(id: String): Offer? = offerRepository.getById(id)

    override suspend fun deleteOffer(id: String): DeleteOfferResult =
        either {
            val offer = offerRepository.getById(id)
            ensureNotNull(offer) { DeleteOfferError.OfferNotFound }
            offerRepository.delete(offer)
        }

    override suspend fun sendOfferByEmail(clientId: String): SendOfferEmailResult =
        either {
            val client = clientRepository.getById(clientId)
            ensureNotNull(client) { SendOfferEmailError.OfferNotFound }
            val offer = offerRepository.getByClient(clientId)
            ensureNotNull(offer) { SendOfferEmailError.OfferNotFound }

            val res = mailService.sendOfferEmail(client.email, client.name, offer.link.url)
            when (res) {
                is Either.Left -> false
                is Either.Right -> true
            }
        }
}
