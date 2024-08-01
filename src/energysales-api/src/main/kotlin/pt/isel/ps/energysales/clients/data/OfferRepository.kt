package pt.isel.ps.energysales.clients.data

import pt.isel.ps.energysales.clients.domain.Offer

interface OfferRepository {
    suspend fun getById(id: String): Offer?

    suspend fun getByClient(clientId: String): Offer?

    suspend fun create(offer: Offer): String

    suspend fun delete(offer: Offer): Boolean
}
