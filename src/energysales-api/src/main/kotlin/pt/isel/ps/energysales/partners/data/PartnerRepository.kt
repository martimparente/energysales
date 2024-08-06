package pt.isel.ps.energysales.partners.data

import pt.isel.ps.energysales.partners.domain.Partner
import pt.isel.ps.energysales.partners.domain.PartnerDetails
import pt.isel.ps.energysales.sellers.domain.Seller

interface PartnerRepository {
    suspend fun create(partner: Partner): Int

    suspend fun getAll(): List<Partner>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<Partner>

    suspend fun getByName(name: String): Partner?

    suspend fun getById(id: String): Partner?

    suspend fun getByIdWithDetails(id: String): PartnerDetails?

    suspend fun partnerExists(id: String): Boolean

    suspend fun partnerExistsByName(name: String): Boolean

    suspend fun update(partner: Partner): Partner?

    suspend fun delete(partner: Partner): Boolean

    suspend fun getPartnerSellers(id: String): List<Seller>

    suspend fun addSellerToPartner(
        partnerId: String,
        sellerId: String,
    ): Boolean

    suspend fun deleteSellerFromPartner(sellerId: String): Boolean

    suspend fun addServiceToPartner(
        partnerId: String,
        serviceId: String,
    ): Boolean

    suspend fun deleteServiceFromPartner(
        partnerID: String,
        serviceId: String,
    ): Boolean

    suspend fun addClientToPartner(
        partnerId: String,
        clientId: String,
    ): Boolean
}
