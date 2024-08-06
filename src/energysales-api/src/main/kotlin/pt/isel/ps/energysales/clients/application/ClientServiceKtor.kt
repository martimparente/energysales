package pt.isel.ps.energysales.clients.application

import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.clients.application.dto.CreateClientError
import pt.isel.ps.energysales.clients.application.dto.CreateClientInput
import pt.isel.ps.energysales.clients.application.dto.CreateClientResult
import pt.isel.ps.energysales.clients.application.dto.DeleteClientError
import pt.isel.ps.energysales.clients.application.dto.DeleteClientResult
import pt.isel.ps.energysales.clients.application.dto.UpdateClientError
import pt.isel.ps.energysales.clients.application.dto.UpdateClientInput
import pt.isel.ps.energysales.clients.application.dto.UpdateClientResult
import pt.isel.ps.energysales.clients.data.ClientRepository
import pt.isel.ps.energysales.clients.domain.Client
import pt.isel.ps.energysales.teams.domain.Location

class ClientServiceKtor(
    private val clientRepository: ClientRepository,
) : ClientService {
    // Create
    override suspend fun createClient(input: CreateClientInput): CreateClientResult =
        either {
            ensure(input.name.length in 2..16) { CreateClientError.ClientNameIsInvalid }
            ensure(input.nif.length == 9) { CreateClientError.ClientInfoIsInvalid }
            ensure(input.phone.length == 9) { CreateClientError.ClientInfoIsInvalid }

            val client = Client(null, input.name, input.nif, input.phone, input.email, Location(input.location.district), input.sellerId)
            clientRepository.create(client)
        }

    // Read
    override suspend fun getAllClients() = clientRepository.getAll()

    override suspend fun getAllClientsPaging(
        pageSize: Int,
        lastKeySeen: String?,
    ) = clientRepository.getAllKeyPaging(pageSize, lastKeySeen)

    override suspend fun getById(id: String): Client? = clientRepository.getById(id)

    // Update
    override suspend fun updateClient(input: UpdateClientInput): UpdateClientResult =
        either {
            ensure(input.name?.length in 2..16) { UpdateClientError.ClientNameIsInvalid }
            ensure(input.phone?.length == 9) { UpdateClientError.ClientInfoIsInvalid }
            input.email?.let {
                ensure(isValidEmail(input.email)) { UpdateClientError.ClientEmailIsInvalid }
            }

            val client = clientRepository.getById(input.id) ?: raise(UpdateClientError.ClientNotFound)
            val patchedClient =
                client.copy(
                    name = input.name ?: client.name,
                    phone = input.phone ?: client.phone,
                    email = input.email ?: client.email,
                    location = input.location?.let { Location(it.district) } ?: client.location,
                    sellerId = input.sellerId ?: client.sellerId,
                )

            clientRepository.update(patchedClient) ?: raise(UpdateClientError.ClientNotFound)
        }

    override suspend fun deleteClient(id: String): DeleteClientResult =
        either {
            val client = clientRepository.getById(id)
            ensureNotNull(client) { DeleteClientError.ClientNotFound }
            clientRepository.delete(client)
        }
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
    return emailRegex.matches(email)
}
