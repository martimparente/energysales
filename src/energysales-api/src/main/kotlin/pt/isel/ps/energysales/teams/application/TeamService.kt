package pt.isel.ps.energysales.teams.application

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.sellers.data.SellerRepository
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.teams.data.TeamRepository
import pt.isel.ps.energysales.teams.domain.Location
import pt.isel.ps.energysales.teams.domain.Team
import pt.isel.ps.energysales.teams.domain.TeamDetails

class TeamService(
    private val teamRepository: TeamRepository,
    private val sellerRepository: SellerRepository,
) {
    // Create
    suspend fun createTeam(
        name: String,
        district: String,
        managerId: Int?,
    ): TeamCreationResult =
        either {
            ensure(name.length in 3..50) { TeamCreationError.TeamInfoIsInvalid }
            ensure(district.length in 3..50) { TeamCreationError.TeamInfoIsInvalid }
            ensure(!teamRepository.teamExistsByName(name)) { TeamCreationError.TeamAlreadyExists }

            teamRepository.create(Team(-1, name, Location(district), managerId))
        }

    // Read
    suspend fun getAllTeams() = teamRepository.getAll()

    suspend fun getAllTeamsPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ) = teamRepository.getAllKeyPaging(pageSize, lastKeySeen)

    suspend fun getByName(name: String): Team? = teamRepository.getByName(name)

    suspend fun getById(id: Int): Team? = teamRepository.getById(id)

    suspend fun getByIdWithDetails(id: Int): TeamDetails? = teamRepository.getByIdWithDetails(id)

    // Update
    suspend fun updateTeam(team: Team): TeamUpdatingResult =
        either {
            ensure(team.name.length in 3..50) { TeamUpdatingError.TeamInfoIsInvalid }
            ensure(team.location.district.length in 3..50) { TeamUpdatingError.TeamInfoIsInvalid }
            ensure(teamRepository.teamExists(team.id)) { TeamUpdatingError.TeamNotFound }
            val updatedTeam = teamRepository.update(team)
            ensureNotNull(updatedTeam) { TeamUpdatingError.TeamNotFound } // todo check if this is necessary
        }

    suspend fun deleteTeam(id: Int): TeamDeletingResult =
        either {
            val team = teamRepository.getById(id)
            ensureNotNull(team) { TeamDeletingError.TeamNotFound }
            teamRepository.delete(team)
        }

    suspend fun getTeamSellers(id: Int): TeamSellersReadingResult =
        either {
            ensure(teamRepository.teamExists(id)) { TeamSellersReadingError.TeamNotFound }
            teamRepository.getTeamSellers(id)
        }

    suspend fun addSellerToTeam(
        teamId: Int,
        sellerId: Int,
    ): TeamAddSellerResult =
        either {
            ensure(teamRepository.teamExists(teamId)) { TeamSellersReadingError.TeamNotFound }
            ensureNotNull(sellerRepository.sellerExists(sellerId)) { TeamSellersReadingError.SellerNotFound }

            teamRepository.addSellerToTeam(teamId, sellerId)
        }

    suspend fun deleteSellerFromTeam(sellerId: Int): TeamDeleteSellerResult =
        either {
            // ensure(teamRepository.teamExists(id)) { TeamSellersReadingError.TeamNotFound } //todo ensure seller exists not team
            teamRepository.deleteSellerFromTeam(sellerId)
            true
        }

    suspend fun addServiceToTeam(
        teamId: Int,
        serviceId: Int,
    ): TeamAddServiceResult =
        either {
            ensure(teamRepository.teamExists(teamId)) { TeamAddServiceError.TeamNotFound }
            teamRepository.addServiceToTeam(teamId, serviceId)
        }

    suspend fun deleteServiceFromTeam(
        teamID: Int,
        serviceId: Int,
    ): TeamDeleteServiceResult =
        either {
            ensure(teamRepository.teamExists(teamID)) { TeamDeleteServiceError.TeamNotFound }
            teamRepository.deleteServiceFromTeam(teamID, serviceId)
        }

    suspend fun addClientToTeam(
        teamId: Int,
        clientId: Int,
    ): TeamAddClientResult =
        either {
            ensure(teamRepository.teamExists(teamId)) { TeamAddClientError.TeamNotFound }
            teamRepository.addClientToTeam(teamId, clientId)
        }
}

typealias TeamCreationResult = Either<TeamCreationError, Int>
typealias TeamReadingResult = Either<TeamReadingError, Team>
typealias TeamUpdatingResult = Either<TeamUpdatingError, Team>
typealias TeamDeletingResult = Either<TeamDeletingError, Boolean>

typealias TeamSellersReadingResult = Either<TeamSellersReadingError, List<Seller>>
typealias TeamAddSellerResult = Either<TeamSellersReadingError, Boolean>
typealias TeamDeleteSellerResult = Either<TeamSellersReadingError, Boolean>

typealias TeamAddServiceResult = Either<TeamAddServiceError, Boolean>
typealias TeamDeleteServiceResult = Either<TeamDeleteServiceError, Boolean>
typealias TeamAddClientResult = Either<TeamAddClientError, Boolean>

sealed interface TeamCreationError {
    data object TeamAlreadyExists : TeamCreationError

    data object TeamInfoIsInvalid : TeamCreationError
}

sealed interface TeamReadingError {
    data object TeamAlreadyExists : TeamReadingError

    data object TeamNameIsInvalid : TeamReadingError
}

sealed interface TeamUpdatingError {
    data object TeamNotFound : TeamUpdatingError

    data object TeamInfoIsInvalid : TeamUpdatingError
}

sealed interface TeamDeletingError {
    data object TeamNotFound : TeamDeletingError

    data object TeamInfoIsInvalid : TeamDeletingError
}

sealed interface TeamSellersReadingError {
    data object TeamNotFound : TeamSellersReadingError

    data object SellerNotFound : TeamSellersReadingError
}

sealed interface TeamAddSellerError {
    data object TeamNotFound : TeamAddSellerError

    data object SellerNotFound : TeamAddSellerError
}

sealed interface TeamDeleteSellerError {
    data object TeamNotFound : TeamDeleteSellerError

    data object SellerNotFound : TeamDeleteSellerError
}

sealed interface TeamAddServiceError {
    data object TeamNotFound : TeamAddServiceError

    data object ServiceNotFound : TeamAddServiceError
}

sealed interface TeamDeleteServiceError {
    data object TeamNotFound : TeamDeleteServiceError

    data object ServiceNotFound : TeamDeleteServiceError
}

sealed interface TeamAddClientError {
    data object TeamNotFound : TeamAddClientError

    data object SellerNotFound : TeamAddClientError
}

sealed interface TeamUpdateAvatarError {
    data object TeamNotFound : TeamUpdateAvatarError

    data object AvatarImgNotFound : TeamUpdateAvatarError
}
