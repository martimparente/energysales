package pt.isel.ps.ecoenergy.teams.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.ecoenergy.sellers.domain.model.Seller
import pt.isel.ps.ecoenergy.teams.data.TeamRepository
import pt.isel.ps.ecoenergy.teams.domain.model.Location
import pt.isel.ps.ecoenergy.teams.domain.model.Person
import pt.isel.ps.ecoenergy.teams.domain.model.Team

class TeamService(
    private val teamRepository: TeamRepository,
) {
    // Create
    suspend fun createTeam(
        name: String,
        district: String,
        manager: Int?,
    ): TeamCreationResult =
        either {
            ensure(name.length in 3..50) { TeamCreationError.TeamInfoIsInvalid }
            ensure(district.length in 3..50) { TeamCreationError.TeamInfoIsInvalid }
            ensure(!teamRepository.teamExistsByName(name)) { TeamCreationError.TeamAlreadyExists }

            teamRepository.create(Team(-1, name, Location(district), if (manager != null) Person.create(manager) else null))
        }

    // Read
    suspend fun getAllTeams() = teamRepository.getAll()

    suspend fun getAllTeamsPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ) = teamRepository.getAllKeyPaging(pageSize, lastKeySeen)

    suspend fun getByName(name: String): Team? = teamRepository.getByName(name)

    suspend fun getById(id: Int): Team? = teamRepository.getById(id)

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
}

typealias TeamCreationResult = Either<TeamCreationError, Int>
typealias TeamReadingResult = Either<TeamReadingError, Team>
typealias TeamUpdatingResult = Either<TeamUpdatingError, Team>
typealias TeamDeletingResult = Either<TeamDeletingError, Boolean>

typealias TeamSellersReadingResult = Either<TeamSellersReadingError, List<Seller>>

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
}
