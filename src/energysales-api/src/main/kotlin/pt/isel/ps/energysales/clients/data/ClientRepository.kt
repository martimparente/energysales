package pt.isel.ps.energysales.clients.data

import pt.isel.ps.energysales.clients.domain.Client

interface ClientRepository {
    suspend fun create(client: Client): Int

    suspend fun getAll(): List<Client>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<Client>

    suspend fun getByNif(nif: String): Client?

    suspend fun getById(id: Int): Client?

    suspend fun clientExists(id: Int): Boolean

    suspend fun clientExistsByName(name: String): Boolean

    suspend fun update(client: Client): Client?

    suspend fun delete(client: Client): Boolean
}
