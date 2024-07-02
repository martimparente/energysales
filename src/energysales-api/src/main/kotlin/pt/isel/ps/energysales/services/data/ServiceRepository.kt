package pt.isel.ps.energysales.services.data

import pt.isel.ps.energysales.services.domain.Service

interface ServiceRepository {
    suspend fun create(service: Service): Int

    suspend fun getAll(): List<Service>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<Service>

    suspend fun getByName(name: String): Service?

    suspend fun getById(id: Int): Service?

    suspend fun serviceExists(id: Int): Boolean

    suspend fun serviceExistsByName(name: String): Boolean

    suspend fun update(service: Service): Service?

    suspend fun delete(service: Service): Boolean
}
