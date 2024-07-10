package pt.isel.ps.energysales.clients.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.clients.application.dto.CreateClientInput
import pt.isel.ps.energysales.clients.application.dto.UpdateClientInput
import pt.isel.ps.energysales.clients.data.ClientRepository
import pt.isel.ps.energysales.clients.domain.Client
import pt.isel.ps.energysales.teams.domain.Location

class ClientService(
    private val clientRepository: ClientRepository,
) {
    // Create
    suspend fun createClient(input: CreateClientInput): ClientCreationResult =
        either {
            ensure(input.name.length in 2..16) { ClientCreationError.ClientNameIsInvalid }
            ensure(input.nif.length == 9) { ClientCreationError.ClientInfoIsInvalid }
            ensure(input.phone.length == 9) { ClientCreationError.ClientInfoIsInvalid }
            ensure(input.district.length in 2..16) { ClientCreationError.ClientInfoIsInvalid }
            // ensure(!clientRepository.clientExistsByName(name)) { ClientCreationError.ClientAlreadyExists } //TODO SHOULD CHECK HERE OR LET SQL HANDLE IT?

            val client = Client(-1, input.name, input.nif, input.phone, Location(input.district), input.teamId, input.sellerId)
            clientRepository.create(client)
        }

    // Read
    suspend fun getAllClients() = clientRepository.getAll()

    suspend fun getAllClientsPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ) = clientRepository.getAllKeyPaging(pageSize, lastKeySeen)

    // suspend fun getByName(name: String): Client? = clientRepository.getByName(name)

    suspend fun getById(id: Int): Client? = clientRepository.getById(id)

    // Update
    suspend fun updateClient(input: UpdateClientInput): ClientUpdatingResult =
        either {
            ensure(input.name.length in 2..16) { ClientUpdatingError.ClientNameIsInvalid }
            ensure(input.nif.length == 9) { ClientUpdatingError.ClientInfoIsInvalid }
            ensure(input.phone.length == 9) { ClientUpdatingError.ClientInfoIsInvalid }
            ensure(input.district.length in 2..16) { ClientUpdatingError.ClientInfoIsInvalid }
            val client = clientRepository.getById(input.id)
            ensureNotNull(client) { ClientUpdatingError.ClientNotFound }

            val newClient =
                client.copy(
                    name = input.name,
                    nif = input.nif,
                    phone = input.phone,
                    location = Location(input.district),
                    teamId = input.teamId,
                    sellerId = input.sellerId,
                )

            val updatedClient = clientRepository.update(newClient)
            ensureNotNull(updatedClient) { ClientUpdatingError.ClientNotFound }
        }

    suspend fun deleteClient(id: Int): ClientDeletingResult =
        either {
            val client = clientRepository.getById(id)
            ensureNotNull(client) { ClientDeletingError.ClientNotFound }
            clientRepository.delete(client)
        }
}

typealias ClientCreationResult = Either<ClientCreationError, Int>
typealias ClientReadingResult = Either<ClientReadingError, Client>
typealias ClientUpdatingResult = Either<ClientUpdatingError, Client>
typealias ClientDeletingResult = Either<ClientDeletingError, Boolean>

sealed interface ClientCreationError {
    data object ClientAlreadyExists : ClientCreationError

    data object ClientInfoIsInvalid : ClientCreationError

    data object ClientNameIsInvalid : ClientCreationError

    data object ClientSurnameIsInvalid : ClientCreationError

    data object ClientEmailIsInvalid : ClientCreationError

//    data object ClientSurnameIsInvalid : ClientCreationError
}

sealed interface ClientReadingError {
    data object ClientAlreadyExists : ClientReadingError

    data object ClientNameIsInvalid : ClientReadingError
}

sealed interface ClientUpdatingError {
    data object ClientNotFound : ClientUpdatingError

    data object ClientInfoIsInvalid : ClientUpdatingError

    data object ClientNameIsInvalid : ClientUpdatingError

    data object ClientSurnameIsInvalid : ClientUpdatingError

    data object ClientEmailIsInvalid : ClientUpdatingError
}

sealed interface ClientDeletingError {
    data object ClientNotFound : ClientDeletingError

    data object ClientInfoIsInvalid : ClientDeletingError
}

private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$".toRegex()
    return emailRegex.matches(email)
}
