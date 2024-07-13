package pt.isel.ps.energysales.sellers.data

import pt.isel.ps.energysales.sellers.domain.Seller

interface SellerRepository {
    suspend fun create(seller: Seller): Int

    suspend fun getAll(): List<Seller>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
        noTeam: Boolean,
    ): List<Seller>

    suspend fun getById(id: Int): Seller?

    suspend fun sellerExists(id: Int): Boolean

    suspend fun update(seller: Seller): Seller?

    suspend fun delete(seller: Seller): Boolean

    suspend fun getSellersWithNoTeam(searchQuery: String?): List<Seller>
}
