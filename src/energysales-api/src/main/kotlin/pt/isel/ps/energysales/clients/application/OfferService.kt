package pt.isel.ps.energysales.clients.application

import pt.isel.ps.energysales.clients.application.dto.CreateOfferInput
import pt.isel.ps.energysales.clients.application.dto.OfferCreationResult
import pt.isel.ps.energysales.clients.application.dto.OfferDeletingResult
import pt.isel.ps.energysales.clients.application.dto.SendOfferEmailResult
import pt.isel.ps.energysales.clients.domain.Offer

interface OfferService {
    suspend fun createOffer(input: CreateOfferInput): OfferCreationResult

    suspend fun getById(id: String): Offer?

    suspend fun deleteOffer(id: String): OfferDeletingResult

    suspend fun sendOfferByEmail(clientId: String): SendOfferEmailResult
}
