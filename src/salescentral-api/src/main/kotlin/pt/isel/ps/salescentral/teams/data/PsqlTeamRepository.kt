package pt.isel.ps.salescentral.teams.data

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.salescentral.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.salescentral.sellers.data.PersonEntity
import pt.isel.ps.salescentral.sellers.data.PersonTable
import pt.isel.ps.salescentral.sellers.data.SellerEntity
import pt.isel.ps.salescentral.sellers.data.SellerTable
import pt.isel.ps.salescentral.sellers.domain.model.Seller
import pt.isel.ps.salescentral.teams.domain.model.Location
import pt.isel.ps.salescentral.teams.domain.model.Person
import pt.isel.ps.salescentral.teams.domain.model.Team
import pt.isel.ps.salescentral.teams.domain.model.TeamDetails

object TeamTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val location = reference("location", LocationTable)
    val manager = reference("manager", PersonTable).nullable()
}

object LocationTable : IntIdTable() {
    val district = varchar("district", 50).uniqueIndex()
}

class LocationEntity(
    id: EntityID<Int>,
) : Entity<Int>(id) {
    companion object : EntityClass<Int, LocationEntity>(LocationTable)

    fun toLocation() =
        Location(
            district,
        )

    var district by LocationTable.district
    val teams by TeamEntity referrersOn TeamTable.location
}

class TeamEntity(
    id: EntityID<Int>,
) : Entity<Int>(id) {
    companion object : EntityClass<Int, TeamEntity>(TeamTable)

    fun toTeam() =
        Team(
            id.value,
            name,
            location.toLocation(),
            manager?.let { Person(it.id.value, it.name, it.surname, it.email, it.role.toString()) },
        )

    var name by TeamTable.name
    var location by LocationEntity referencedOn TeamTable.location
    var manager by PersonEntity optionalReferencedOn TeamTable.manager
    val sellers by SellerEntity optionalReferrersOn SellerTable.team
}

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
                        team.manager?.let {
                            PersonEntity.findById(it.uid)
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
                    it.manager = team.manager?.let { PersonEntity.findById(it.uid) }
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
}
