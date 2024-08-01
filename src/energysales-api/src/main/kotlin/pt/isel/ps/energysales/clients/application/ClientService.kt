package pt.isel.ps.energysales.clients.application

import pt.isel.ps.energysales.clients.application.dto.CreateClientInput
import pt.isel.ps.energysales.clients.application.dto.CreateClientResult
import pt.isel.ps.energysales.clients.application.dto.DeleteClientResult
import pt.isel.ps.energysales.clients.application.dto.UpdateClientInput
import pt.isel.ps.energysales.clients.application.dto.UpdateClientResult
import pt.isel.ps.energysales.clients.domain.Client

interface ClientService {
    suspend fun createClient(input: CreateClientInput): CreateClientResult

    suspend fun getAllClients(): List<Client>

    suspend fun getAllClientsPaging(
        pageSize: Int,
        lastKeySeen: String?,
    ): List<Client>

    suspend fun getById(id: String): Client?

    suspend fun updateClient(input: UpdateClientInput): UpdateClientResult

    suspend fun deleteClient(id: String): DeleteClientResult
}
