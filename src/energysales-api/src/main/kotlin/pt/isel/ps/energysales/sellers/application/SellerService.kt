package pt.isel.ps.energysales.sellers.application

import pt.isel.ps.energysales.sellers.application.dto.CreateSellerInput
import pt.isel.ps.energysales.sellers.application.dto.CreateSellerResult
import pt.isel.ps.energysales.sellers.application.dto.DeleteSellerResult
import pt.isel.ps.energysales.sellers.application.dto.GetAllSellerInput
import pt.isel.ps.energysales.sellers.application.dto.GetAllSellersResult
import pt.isel.ps.energysales.sellers.application.dto.UpdateSellerResult
import pt.isel.ps.energysales.sellers.domain.Seller

interface SellerService {
    suspend fun createSeller(info: CreateSellerInput): CreateSellerResult

    suspend fun getAllSellers(input: GetAllSellerInput): GetAllSellersResult

    suspend fun getAllSellersPaging(
        pageSize: Int,
        lastKeySeen: String?,
        noPartner: Boolean,
    ): List<Seller>

    suspend fun getById(id: String): Seller?

    suspend fun updateSeller(seller: Seller): UpdateSellerResult

    suspend fun deleteSeller(id: String): DeleteSellerResult
}
