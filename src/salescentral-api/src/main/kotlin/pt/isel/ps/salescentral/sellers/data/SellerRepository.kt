package pt.isel.ps.salescentral.sellers.data

import pt.isel.ps.salescentral.sellers.domain.model.Seller

interface SellerRepository {
    suspend fun create(seller: Seller): Int

    suspend fun getAll(): List<Seller>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<Seller>

    suspend fun getById(id: Int): Seller?

    suspend fun isEmailAvailable(email: String): Boolean

    suspend fun sellerExists(id: Int): Boolean

    suspend fun update(seller: Seller): Seller?

    suspend fun delete(seller: Seller): Boolean
}
