package pt.isel.ps.energysales.clients.domain

import kotlinx.datetime.LocalDateTime
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.services.domain.Service
import java.util.UUID

data class Offer(
    val id: Int,
    val createdAt: LocalDateTime,
    val createdBy: Seller,
    val client: Client,
    val service: Service,
    val link: OfferLink,
)

data class OfferLink(
    val uuid: UUID,
    val url: String,
    val due: LocalDateTime,
)
