package pt.isel.ps.energysales.clients.application

import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.clients.application.dto.ClientCreationError
import pt.isel.ps.energysales.clients.application.dto.ClientCreationResult
import pt.isel.ps.energysales.clients.application.dto.ClientDeletingError
import pt.isel.ps.energysales.clients.application.dto.ClientDeletingResult
import pt.isel.ps.energysales.clients.application.dto.ClientUpdatingError
import pt.isel.ps.energysales.clients.application.dto.ClientUpdatingResult
import pt.isel.ps.energysales.clients.application.dto.CreateClientInput
import pt.isel.ps.energysales.clients.application.dto.UpdateClientInput
import pt.isel.ps.energysales.clients.data.ClientRepository
import pt.isel.ps.energysales.clients.domain.Client
import pt.isel.ps.energysales.teams.domain.Location

class ClientServiceKtor(
    private val clientRepository: ClientRepository,
) : ClientService {
    // Create
    override suspend fun createClient(input: CreateClientInput): ClientCreationResult =
        either {
            ensure(input.name.length in 2..16) { ClientCreationError.ClientNameIsInvalid }
            ensure(input.nif.length == 9) { ClientCreationError.ClientInfoIsInvalid }
            ensure(input.phone.length == 9) { ClientCreationError.ClientInfoIsInvalid }
            ensure(input.location.district.length in 2..16) { ClientCreationError.ClientInfoIsInvalid }

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
    override suspend fun updateClient(input: UpdateClientInput): ClientUpdatingResult =
        either {
            ensure(input.name.length in 2..16) { ClientUpdatingError.ClientNameIsInvalid }
            ensure(input.nif.length == 9) { ClientUpdatingError.ClientInfoIsInvalid }
            ensure(input.phone.length == 9) { ClientUpdatingError.ClientInfoIsInvalid }
            ensure(input.location.district.length in 2..16) { ClientUpdatingError.ClientInfoIsInvalid }
            ensure(isValidEmail(input.email)) { ClientUpdatingError.ClientEmailIsInvalid }
            val client = clientRepository.getById(input.id)
            ensureNotNull(client) { ClientUpdatingError.ClientNotFound }

            val newClient =
                client.copy(
                    name = input.name,
                    nif = input.nif,
                    phone = input.phone,
                    location = Location(input.location.district),
                    sellerId = input.sellerId,
                )

            val updatedClient = clientRepository.update(newClient)
            ensureNotNull(updatedClient) { ClientUpdatingError.ClientNotFound }
        }

    override suspend fun deleteClient(id: String): ClientDeletingResult =
        either {
            val client = clientRepository.getById(id)
            ensureNotNull(client) { ClientDeletingError.ClientNotFound }
            clientRepository.delete(client)
        }
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
    return emailRegex.matches(email)
}
