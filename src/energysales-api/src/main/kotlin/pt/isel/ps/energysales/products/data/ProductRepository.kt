package pt.isel.ps.energysales.products.data

import pt.isel.ps.energysales.products.domain.model.Product

interface ProductRepository {
    suspend fun create(product: Product): Int

    suspend fun getAll(): List<Product>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<Product>

    suspend fun getByName(name: String): Product?

    suspend fun getById(id: Int): Product?

    suspend fun productExists(id: Int): Boolean

    suspend fun productExistsByName(name: String): Boolean

    suspend fun update(product: Product): Product?

    suspend fun delete(product: Product): Boolean
}
