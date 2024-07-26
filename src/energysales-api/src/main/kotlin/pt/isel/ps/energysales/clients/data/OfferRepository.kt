package pt.isel.ps.energysales.clients.data

import pt.isel.ps.energysales.clients.domain.Offer

interface OfferRepository {
    suspend fun getById(id: Int): Offer?

    suspend fun getByClient(clientId: Int): Offer?

    suspend fun create(offer: Offer): Int

    suspend fun delete(offer: Offer): Boolean
}
