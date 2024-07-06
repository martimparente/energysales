package pt.isel.ps.energysales.teams.data

import SellerEntity
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.services.data.entity.ServiceEntity
import pt.isel.ps.energysales.teams.data.entity.LocationEntity
import pt.isel.ps.energysales.teams.data.entity.TeamEntity
import pt.isel.ps.energysales.teams.data.table.TeamTable
import pt.isel.ps.energysales.teams.domain.Team
import pt.isel.ps.energysales.teams.domain.TeamDetails
import pt.isel.ps.energysales.users.data.entity.UserEntity

class PsqlTeamRepository : TeamRepository {
    override suspend fun getByName(name: String): Team? =
        dbQuery {
            TeamEntity
                .find { TeamTable.name eq name }
                .firstOrNull()
                ?.toTeam()
        }

    override suspend fun getById(id: Int): Team? =
        dbQuery {
            TeamEntity.findById(id)?.toTeam()
        }

    override suspend fun getByIdWithMembers(id: Int): TeamDetails =
        dbQuery {
            TeamEntity
                .findById(id)
                ?.let {
                    TeamDetails(
                        it.toTeam(),
                        it.sellers.map { it.toSeller() },
                    )
                } ?: throw IllegalArgumentException("Team not found")
        }

    override suspend fun teamExists(id: Int): Boolean =
        dbQuery {
            TeamEntity.findById(id) != null
        }

    override suspend fun teamExistsByName(name: String) =
        dbQuery {
            TeamEntity
                .find { TeamTable.name eq name }
                .count() > 0
        }

    override suspend fun create(team: Team): Int =
        dbQuery {
            TeamEntity
                .new {
                    name = team.name
                    location =
                        LocationEntity.new {
                            district = team.location.district
                        }
                    manager =
                        team.managerId?.let {
                            UserEntity.findById(it)
                        }
                }.id
                .value
        }

    override suspend fun getAll(): List<Team> =
        dbQuery {
            TeamEntity
                .all()
                .map { it.toTeam() }
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ): List<Team> =
        dbQuery {
            TeamEntity
                .find { TeamTable.id greater (lastKeySeen ?: 0) }
                .orderBy(TeamTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toTeam() }
                .toList()
        }

    override suspend fun update(team: Team): Team? =
        dbQuery {
            TeamEntity
                .findById(team.id)
                ?.also {
                    it.name = team.name
                    it.location = LocationEntity.findById(it.location.id.value) ?: LocationEntity.new {
                        district = team.location.district
                    }
                    it.manager = team.managerId?.let { UserEntity.findById(it) }
                }?.toTeam()
        }

    override suspend fun delete(team: Team): Boolean =
        dbQuery {
            TeamEntity
                .find { TeamTable.name eq team.name }
                .firstOrNull()
                ?.delete() ?: false
            true
        }

    override suspend fun getTeamSellers(id: Int): List<Seller> =
        dbQuery {
            TeamEntity
                .findById(id)
                ?.sellers
                ?.map { it.toSeller() }
                ?: emptyList()
        }

    override suspend fun addSellerToTeam(
        teamId: Int,
        sellerId: Int,
    ): Boolean =
        dbQuery {
            val seller = SellerEntity.findById(sellerId) ?: return@dbQuery false
            seller.team = TeamEntity.findById(teamId) ?: return@dbQuery false
            true
        }

    override suspend fun deleteSellerFromTeam(sellerId: Int): Boolean =
        dbQuery {
            val seller = SellerEntity.findById(sellerId) ?: return@dbQuery false
            seller.team = null
            true
        }

    override suspend fun addServiceToTeam(
        teamId: Int,
        serviceId: Int,
    ): Boolean =
        dbQuery {
            // add a service to team - is a many to many relationship on
            val team = TeamEntity.findById(teamId) ?: return@dbQuery false
            val service = ServiceEntity.findById(serviceId) ?: return@dbQuery false
            team.services = SizedCollection(team.services + service)
            true
        }
}
