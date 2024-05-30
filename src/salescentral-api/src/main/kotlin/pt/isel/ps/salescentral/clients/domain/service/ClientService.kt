package pt.isel.ps.salescentral.clients.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.salescentral.clients.data.ClientRepository
import pt.isel.ps.salescentral.clients.domain.model.Client
import pt.isel.ps.salescentral.teams.domain.model.Location

class ClientService(
    private val clientRepository: ClientRepository,
) {
    // Create
    suspend fun createClient(
        name: String,
        nif: String,
        phone: String,
        district: String,
    ): ClientCreationResult =
        either {
            ensure(name.length in 2..16) { ClientCreationError.ClientNameIsInvalid }
            ensure(nif.length == 9) { ClientCreationError.ClientInfoIsInvalid }
            ensure(phone.length == 9) { ClientCreationError.ClientInfoIsInvalid }
            ensure(district.length in 2..16) { ClientCreationError.ClientInfoIsInvalid }
            // ensure(!clientRepository.clientExistsByName(name)) { ClientCreationError.ClientAlreadyExists } //TODO SHOULD CHECK HERE OR LET SQL HANDLE IT?

            clientRepository.create(
                Client(-1, name, nif, phone, Location(district)),
            )
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
    suspend fun updateClient(client: Client): ClientUpdatingResult =
        either {
            ensure(client.name.length in 2..16) { ClientUpdatingError.ClientNameIsInvalid }
            ensure(client.nif.length == 9) { ClientUpdatingError.ClientInfoIsInvalid }
            ensure(client.phone.length == 9) { ClientUpdatingError.ClientInfoIsInvalid }
            ensure(client.location.district.length in 2..16) { ClientUpdatingError.ClientInfoIsInvalid }
            ensure(clientRepository.clientExists(client.id)) { ClientUpdatingError.ClientNotFound }
            val updatedClient = clientRepository.update(client)
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
