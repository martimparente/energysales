package pt.isel.ps.energysales.teams.application

import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import pt.isel.ps.energysales.sellers.data.SellerRepository
import pt.isel.ps.energysales.teams.application.dto.AddTeamAvatarError
import pt.isel.ps.energysales.teams.application.dto.AddTeamAvatarResult
import pt.isel.ps.energysales.teams.application.dto.AddTeamClientError
import pt.isel.ps.energysales.teams.application.dto.AddTeamClientResult
import pt.isel.ps.energysales.teams.application.dto.AddTeamSellerError
import pt.isel.ps.energysales.teams.application.dto.AddTeamSellerResult
import pt.isel.ps.energysales.teams.application.dto.AddTeamServiceError
import pt.isel.ps.energysales.teams.application.dto.AddTeamServiceResult
import pt.isel.ps.energysales.teams.application.dto.CreateTeamError
import pt.isel.ps.energysales.teams.application.dto.CreateTeamInput
import pt.isel.ps.energysales.teams.application.dto.CreateTeamResult
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamError
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamResult
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamSellerResult
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamServiceError
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamServiceResult
import pt.isel.ps.energysales.teams.application.dto.GetTeamSellersError
import pt.isel.ps.energysales.teams.application.dto.GetTeamSellersResult
import pt.isel.ps.energysales.teams.application.dto.UpdateTeamError
import pt.isel.ps.energysales.teams.application.dto.UpdateTeamInput
import pt.isel.ps.energysales.teams.application.dto.UpdateTeamResult
import pt.isel.ps.energysales.teams.data.TeamRepository
import pt.isel.ps.energysales.teams.domain.Location
import pt.isel.ps.energysales.teams.domain.Team
import pt.isel.ps.energysales.teams.domain.TeamDetails

class TeamServiceKtor(
    private val teamRepository: TeamRepository,
    private val sellerRepository: SellerRepository,
) : TeamService {
    // Create
    override suspend fun createTeam(input: CreateTeamInput): CreateTeamResult =
        either {
            ensure(input.name.length in 3..50) { CreateTeamError.TeamInfoIsInvalid }
            ensure(!teamRepository.teamExistsByName(input.name)) { CreateTeamError.TeamAlreadyExists }

            val team = Team(null, input.name, Location(input.location.district), input.managerId)
            teamRepository.create(team)
        }

    // Read
    override suspend fun getAllTeams() = teamRepository.getAll()

    override suspend fun getAllTeamsPaging(
        limit: Int,
        lastKeySeen: String?,
    ) = teamRepository.getAllKeyPaging(limit, lastKeySeen?.toIntOrNull())

    override suspend fun getByName(name: String): Team? = teamRepository.getByName(name)

    override suspend fun getById(id: String): Team? = teamRepository.getById(id)

    override suspend fun getByIdWithDetails(id: String): TeamDetails? = teamRepository.getByIdWithDetails(id)

    // Update
    override suspend fun updateTeam(input: UpdateTeamInput): UpdateTeamResult =
        either {
            ensure(input.name?.length in 3..50) { UpdateTeamError.TeamInfoIsInvalid }
            val team = teamRepository.getById(input.id) ?: raise(UpdateTeamError.TeamNotFound)
            val patchedTeam =
                team.copy(
                    name = input.name ?: team.name,
                    location = input.location ?: team.location,
                    managerId = input.managerId ?: team.managerId,
                )

            val updatedTeam = teamRepository.update(patchedTeam)
            ensureNotNull(updatedTeam) { UpdateTeamError.TeamNotFound }
        }

    override suspend fun deleteTeam(id: String): DeleteTeamResult =
        either {
            val team = teamRepository.getById(id)
            ensureNotNull(team) { DeleteTeamError.TeamNotFound }
            teamRepository.delete(team)
        }

    override suspend fun getTeamSellers(id: String): GetTeamSellersResult =
        either {
            ensure(teamRepository.teamExists(id)) { GetTeamSellersError.TeamNotFound }
            teamRepository.getTeamSellers(id)
        }

    override suspend fun addTeamSeller(
        id: String,
        sellerId: String,
    ): AddTeamSellerResult =
        either {
            ensure(teamRepository.teamExists(id)) { AddTeamSellerError.TeamNotFound }
            ensureNotNull(sellerRepository.sellerExists(sellerId)) { AddTeamSellerError.SellerNotFound }

            teamRepository.addSellerToTeam(id, sellerId)
        }

    override suspend fun deleteTeamSeller(sellerId: String): DeleteTeamSellerResult =
        either {
            teamRepository.deleteSellerFromTeam(sellerId)
        }

    override suspend fun addTeamService(
        id: String,
        serviceId: String,
    ): AddTeamServiceResult =
        either {
            ensure(teamRepository.teamExists(id)) { AddTeamServiceError.TeamNotFound }
            teamRepository.addServiceToTeam(id, serviceId)
        }

    override suspend fun deleteTeamService(
        id: String,
        serviceId: String,
    ): DeleteTeamServiceResult =
        either {
            ensure(teamRepository.teamExists(id)) { DeleteTeamServiceError.TeamNotFound }
            teamRepository.deleteServiceFromTeam(id, serviceId)
        }

    override suspend fun addTeamClient(
        id: String,
        clientId: String,
    ): AddTeamClientResult =
        either {
            ensure(teamRepository.teamExists(id)) { AddTeamClientError.TeamNotFound }
            teamRepository.addClientToTeam(id, clientId)
        }

    override suspend fun addTeamAvatar(
        teamId: String,
        avatarPath: String,
    ): AddTeamAvatarResult =
        either {
            val team =
                teamRepository.getById(teamId)
                    ?: raise(AddTeamAvatarError.TeamNotFound)
            val teamToUpdate = team.copy(avatarPath = avatarPath)
            val updatedTeam = teamRepository.update(teamToUpdate) ?: raise(AddTeamAvatarError.TeamNotFound)
            updatedTeam.avatarPath ?: raise(AddTeamAvatarError.TeamNotFound)
        }
}
