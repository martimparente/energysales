package pt.isel.ps.salescentral.teams.data

import pt.isel.ps.salescentral.sellers.domain.model.Seller
import pt.isel.ps.salescentral.teams.domain.model.Team
import pt.isel.ps.salescentral.teams.domain.model.TeamDetails

interface TeamRepository {
    suspend fun create(team: Team): Int

    suspend fun getAll(): List<Team>

    suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int? = null,
    ): List<Team>

    suspend fun getByName(name: String): Team?

    suspend fun getById(id: Int): Team?

    suspend fun getByIdWithMembers(id: Int): TeamDetails?

    suspend fun teamExists(id: Int): Boolean

    suspend fun teamExistsByName(name: String): Boolean

    suspend fun update(team: Team): Team?

    suspend fun delete(team: Team): Boolean

    suspend fun getTeamSellers(id: Int): List<Seller>

    suspend fun addSellerToTeam(
        teamId: Int,
        sellerId: Int,
    ): Boolean
}
