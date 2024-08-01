package pt.isel.ps.energysales.clients.application

import pt.isel.ps.energysales.clients.application.dto.ClientCreationResult
import pt.isel.ps.energysales.clients.application.dto.ClientDeletingResult
import pt.isel.ps.energysales.clients.application.dto.ClientUpdatingResult
import pt.isel.ps.energysales.clients.application.dto.CreateClientInput
import pt.isel.ps.energysales.clients.application.dto.UpdateClientInput
import pt.isel.ps.energysales.clients.domain.Client

interface ClientService {
    suspend fun createClient(input: CreateClientInput): ClientCreationResult

    suspend fun getAllClients(): List<Client>

    suspend fun getAllClientsPaging(
        pageSize: Int,
        lastKeySeen: String?,
    ): List<Client>

    suspend fun getById(id: String): Client?

    suspend fun updateClient(input: UpdateClientInput): ClientUpdatingResult

    suspend fun deleteClient(id: String): ClientDeletingResult
}
