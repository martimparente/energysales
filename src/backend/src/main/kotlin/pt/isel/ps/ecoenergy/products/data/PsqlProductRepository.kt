package pt.isel.ps.ecoenergy.products.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.ecoenergy.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.ecoenergy.products.domain.model.Product

object ProductTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
}

class ProductEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<ProductEntity>(ProductTable)

    fun toProduct() = Product(id.value, name)

    var name by ProductTable.name
}

class PsqlProductRepository : ProductRepository {
    override suspend fun getById(id: Int): Product? =
        dbQuery {
            ProductEntity.findById(id)?.toProduct()
        }

    override suspend fun productExists(id: Int): Boolean =
        dbQuery {
            ProductEntity.findById(id) != null
        }

    override suspend fun productExistsByName(name: String) =
        dbQuery {
            ProductEntity
                .find { ProductTable.name eq name }
                .count() > 0
        }

    override suspend fun create(product: Product): Int =
        dbQuery {
            ProductEntity
                .new {
                    name = product.name
                }.id
                .value
        }

    override suspend fun getAll(): List<Product> =
        dbQuery {
            ProductEntity
                .all()
                .map { it.toProduct() }
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ): List<Product> =
        dbQuery {
            ProductEntity
                .find { ProductTable.id greaterEq (lastKeySeen ?: 0) }
                .orderBy(ProductTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toProduct() }
                .toList()
        }

    override suspend fun getByName(name: String): Product? {
        TODO("Not yet implemented")
    }

    override suspend fun update(product: Product): Product? =
        dbQuery {
            ProductEntity
                .findById(product.id)
                ?.also { productEntity ->
                    productEntity.name = product.name
                }?.toProduct()
        }

    override suspend fun delete(product: Product): Boolean =
        dbQuery {
            ProductEntity
                .find { ProductTable.name eq product.name }
                .firstOrNull()
                ?.delete() ?: false
            true
        }
}
