package pt.isel.ps.energysales.clients.application

import pt.isel.ps.energysales.clients.application.dto.CreateOfferInput
import pt.isel.ps.energysales.clients.application.dto.CreateOfferResult
import pt.isel.ps.energysales.clients.application.dto.DeleteOfferResult
import pt.isel.ps.energysales.clients.application.dto.SendOfferEmailResult
import pt.isel.ps.energysales.clients.domain.Offer

interface OfferService {
    suspend fun createOffer(input: CreateOfferInput): CreateOfferResult

    suspend fun getById(id: String): Offer?

    suspend fun deleteOffer(id: String): DeleteOfferResult

    suspend fun sendOfferByEmail(clientId: String): SendOfferEmailResult
}
