package pt.isel.ps.ecoenergy.team.domain.service

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import pt.isel.ps.ecoenergy.team.data.TeamRepository
import pt.isel.ps.ecoenergy.team.domain.model.Person
import pt.isel.ps.ecoenergy.team.domain.model.Team


class TeamService(
    private val teamRepository: TeamRepository,
) {
    suspend fun createTeam(
        name: String,
        location: String,
        manager: Int?
    ): TeamCreationResult = either {
        ensure(name.length in 3..50) { TeamCreationError.TeamNameIsInvalid }
        ensure(location.length in 3..50) { TeamCreationError.TeamNameIsInvalid }
        ensure(!teamRepository.teamExists(name)) { TeamCreationError.TeamAlreadyExists }

        teamRepository.create(Team(name, location, manager?.let { Person.create(it) }))
    }

    suspend fun getAllTeams() = teamRepository.getAll()
    suspend fun getAllTeamsPaging(pageSize: Int, lastKeySeen: Int?) = teamRepository.getAllKeyPaging(pageSize, lastKeySeen)
    suspend fun getByName(name: String): Team? = teamRepository.getByName(name)

    suspend fun updateTeam(id: Int, name: String, location: String, manager: Int?): Team {
        val team = Team(name, location, manager?.let { Person.create(it) })
        return teamRepository.update(team)
    }
}

typealias TeamCreationResult = Either<TeamCreationError, Int>


sealed interface TeamCreationError {
    data object TeamAlreadyExists : TeamCreationError
    data object TeamNameIsInvalid : TeamCreationError
}


