package pt.isel.ps.energysales.teams.data

import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import pt.isel.ps.energysales.clients.data.entity.ClientEntity
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.services.data.entity.ServiceEntity
import pt.isel.ps.energysales.teams.data.entity.LocationEntity
import pt.isel.ps.energysales.teams.data.entity.TeamEntity
import pt.isel.ps.energysales.teams.data.table.TeamServices
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

    override suspend fun getById(id: String): Team? =
        dbQuery {
            TeamEntity.findById(id.toInt())?.toTeam()
        }

    override suspend fun getByIdWithDetails(id: String): TeamDetails? =
        dbQuery {
            TeamEntity
                .findById(id.toInt())
                ?.let {
                    TeamDetails(
                        it.toTeam(),
                        it.sellers.map { it.toSeller() },
                        it.services.map { it.toService() },
                    )
                }
        }

    override suspend fun teamExists(id: String): Boolean =
        dbQuery {
            TeamEntity.findById(id.toInt()) != null
        }

    override suspend fun teamExistsByName(name: String): Boolean =
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
                    manager = team.managerId?.let { UserEntity.findById(it.toInt()) }
                    avatarPath = team.avatarPath
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
                .findById(team.id?.toInt()!!)
                ?.apply {
                    name = team.name
                    location = LocationEntity.findById(location.id.value) ?: LocationEntity.new {
                        district = team.location.district
                    }
                    manager = team.managerId?.let { UserEntity.findById(it.toInt()) }
                    avatarPath = team.avatarPath
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

    override suspend fun getTeamSellers(id: String): List<Seller> =
        dbQuery {
            TeamEntity
                .findById(id.toInt())
                ?.sellers
                ?.map { it.toSeller() }
                ?: emptyList()
        }

    override suspend fun addSellerToTeam(
        teamId: String,
        sellerId: String,
    ): Boolean =
        dbQuery {
            val seller = SellerEntity.findById(sellerId.toInt()) ?: return@dbQuery false
            seller.team = TeamEntity.findById(teamId.toInt()) ?: return@dbQuery false
            true
        }

    override suspend fun deleteSellerFromTeam(sellerId: String): Boolean =
        dbQuery {
            val seller = SellerEntity.findById(sellerId.toInt()) ?: return@dbQuery false
            seller.team = null
            true
        }

    override suspend fun addServiceToTeam(
        teamId: String,
        serviceId: String,
    ): Boolean =
        dbQuery {
            val team = TeamEntity.findById(teamId.toInt()) ?: return@dbQuery false
            val service = ServiceEntity.findById(serviceId.toInt()) ?: return@dbQuery false
            team.services = SizedCollection(team.services + service)
            true
        }

    override suspend fun deleteServiceFromTeam(
        teamID: String,
        serviceId: String,
    ): Boolean =
        dbQuery {
            TeamServices.deleteWhere {
                (TeamServices.team eq teamID.toInt()) and (TeamServices.service eq serviceId.toInt())
            } > 0
        }

    override suspend fun addClientToTeam(
        teamId: String,
        clientId: String,
    ): Boolean =
        dbQuery {
            val team = TeamEntity.findById(teamId.toInt()) ?: return@dbQuery false
            val client = ClientEntity.findById(clientId.toInt()) ?: return@dbQuery false
            team.clients = SizedCollection(team.clients + client)
            true
        }
}
