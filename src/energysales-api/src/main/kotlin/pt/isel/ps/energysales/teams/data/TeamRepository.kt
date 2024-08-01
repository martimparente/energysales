package pt.isel.ps.energysales.teams.data

import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.teams.domain.Team
import pt.isel.ps.energysales.teams.domain.TeamDetails

interface TeamRepository {
    suspend fun create(team: Team): Int

    suspend fun getAll(): List<Team>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<Team>

    suspend fun getByName(name: String): Team?

    suspend fun getById(id: String): Team?

    suspend fun getByIdWithDetails(id: String): TeamDetails?

    suspend fun teamExists(id: String): Boolean

    suspend fun teamExistsByName(name: String): Boolean

    suspend fun update(team: Team): Team?

    suspend fun delete(team: Team): Boolean

    suspend fun getTeamSellers(id: String): List<Seller>

    suspend fun addSellerToTeam(
        teamId: String,
        sellerId: String,
    ): Boolean

    suspend fun deleteSellerFromTeam(sellerId: String): Boolean

    suspend fun addServiceToTeam(
        teamId: String,
        serviceId: String,
    ): Boolean

    suspend fun deleteServiceFromTeam(
        teamID: String,
        serviceId: String,
    ): Boolean

    suspend fun addClientToTeam(
        teamId: String,
        clientId: String,
    ): Boolean
}
