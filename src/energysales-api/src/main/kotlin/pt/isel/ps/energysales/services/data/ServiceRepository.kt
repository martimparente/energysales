package pt.isel.ps.energysales.services.data

import pt.isel.ps.energysales.services.domain.Service

interface ServiceRepository {
    suspend fun create(service: Service): String

    suspend fun getAll(): List<Service>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: String? = null,
    ): List<Service>

    suspend fun getByName(name: String): Service?

    suspend fun getById(id: String): Service?

    suspend fun serviceExists(id: String): Boolean

    suspend fun serviceExistsByName(name: String): Boolean

    suspend fun update(service: Service): Service?

    suspend fun delete(service: Service): Boolean
}
