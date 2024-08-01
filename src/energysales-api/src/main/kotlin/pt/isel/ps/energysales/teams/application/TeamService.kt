package pt.isel.ps.energysales.teams.application

import pt.isel.ps.energysales.teams.application.dto.AddTeamAvatarResult
import pt.isel.ps.energysales.teams.application.dto.AddTeamClientResult
import pt.isel.ps.energysales.teams.application.dto.AddTeamSellerResult
import pt.isel.ps.energysales.teams.application.dto.AddTeamServiceResult
import pt.isel.ps.energysales.teams.application.dto.CreateTeamInput
import pt.isel.ps.energysales.teams.application.dto.CreateTeamResult
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamSellerResult
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamServiceResult
import pt.isel.ps.energysales.teams.application.dto.GetTeamSellersResult
import pt.isel.ps.energysales.teams.application.dto.UpdateTeamInput
import pt.isel.ps.energysales.teams.application.dto.UpdateTeamResult
import pt.isel.ps.energysales.teams.domain.Team
import pt.isel.ps.energysales.teams.domain.TeamDetails
import pt.isel.ps.energysales.teams.application.dto.DeleteTeamResult as DeleteTeamResult1

interface TeamService {
    suspend fun getAllTeamsPaging(
        limit: Int,
        lastKeySeen: String?,
    ): List<Team>

    suspend fun createTeam(input: CreateTeamInput): CreateTeamResult

    suspend fun getById(id: String): Team?

    suspend fun getByIdWithDetails(id: String): TeamDetails?

    suspend fun getByName(name: String): Team?

    suspend fun getAllTeams(): List<Team>

    suspend fun updateTeam(input: UpdateTeamInput): UpdateTeamResult

    suspend fun deleteTeam(id: String): DeleteTeamResult1

    suspend fun getTeamSellers(id: String): GetTeamSellersResult

    suspend fun addTeamSeller(
        id: String,
        sellerId: String,
    ): AddTeamSellerResult

    suspend fun deleteTeamSeller(sellerId: String): DeleteTeamSellerResult

    suspend fun addTeamService(
        id: String,
        serviceId: String,
    ): AddTeamServiceResult

    suspend fun deleteTeamService(
        id: String,
        serviceId: String,
    ): DeleteTeamServiceResult

    suspend fun addTeamClient(
        id: String,
        clientId: String,
    ): AddTeamClientResult

    suspend fun addTeamAvatar(
        teamId: String,
        avatarPath: String,
    ): AddTeamAvatarResult
}
