package pt.isel.ps.energysales.sellers.data

import pt.isel.ps.energysales.sellers.domain.Seller

interface SellerRepository {
    suspend fun create(seller: Seller): String

    suspend fun getAll(): List<Seller>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: String? = null,
        noPartner: Boolean,
    ): List<Seller>

    suspend fun getById(id: String): Seller?

    suspend fun sellerExists(id: String): Boolean

    suspend fun update(seller: Seller): Seller?

    suspend fun delete(seller: Seller): Boolean

    suspend fun getSellersWithNoPartner(searchQuery: String?): List<Seller>
}
