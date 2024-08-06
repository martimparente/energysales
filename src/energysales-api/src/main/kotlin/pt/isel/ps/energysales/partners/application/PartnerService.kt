package pt.isel.ps.energysales.partners.application

import pt.isel.ps.energysales.partners.application.dto.AddPartnerAvatarResult
import pt.isel.ps.energysales.partners.application.dto.AddPartnerClientResult
import pt.isel.ps.energysales.partners.application.dto.AddPartnerPartnerResult
import pt.isel.ps.energysales.partners.application.dto.AddPartnerSellerResult
import pt.isel.ps.energysales.partners.application.dto.CreatePartnerInput
import pt.isel.ps.energysales.partners.application.dto.CreatePartnerResult
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerSellerResult
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerServiceResult
import pt.isel.ps.energysales.partners.application.dto.GetPartnerSellersResult
import pt.isel.ps.energysales.partners.application.dto.UpdatePartnerInput
import pt.isel.ps.energysales.partners.application.dto.UpdatePartnerResult
import pt.isel.ps.energysales.partners.domain.Partner
import pt.isel.ps.energysales.partners.domain.PartnerDetails
import pt.isel.ps.energysales.partners.application.dto.DeletePartnerResult as DeletePartnerResult1

interface PartnerService {
    suspend fun getAllPartnerPaging(
        limit: Int,
        lastKeySeen: String?,
    ): List<Partner>

    suspend fun createPartner(input: CreatePartnerInput): CreatePartnerResult

    suspend fun getById(id: String): Partner?

    suspend fun getByIdWithDetails(id: String): PartnerDetails?

    suspend fun getByName(name: String): Partner?

    suspend fun getAllPartner(): List<Partner>

    suspend fun updatePartner(input: UpdatePartnerInput): UpdatePartnerResult

    suspend fun deletePartner(id: String): DeletePartnerResult1

    suspend fun getPartnerSellers(id: String): GetPartnerSellersResult

    suspend fun addPartnerSeller(
        id: String,
        sellerId: String,
    ): AddPartnerSellerResult

    suspend fun deletePartnerSeller(sellerId: String): DeletePartnerSellerResult

    suspend fun addPartnerService(
        id: String,
        serviceId: String,
    ): AddPartnerPartnerResult

    suspend fun deletePartnerService(
        id: String,
        serviceId: String,
    ): DeletePartnerServiceResult

    suspend fun addPartnerClient(
        id: String,
        clientId: String,
    ): AddPartnerClientResult

    suspend fun addPartnerAvatar(
        partnerId: String,
        avatarPath: String,
    ): AddPartnerAvatarResult
}
