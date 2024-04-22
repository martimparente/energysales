package pt.isel.ps.ecoenergy.team.data

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.ecoenergy.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.ecoenergy.team.domain.model.Person
import pt.isel.ps.ecoenergy.team.domain.model.Team

fun TeamEntity.toTeam() =
    Team(
        id.value,
        name,
        location,
        manager?.let { Person(it.id.value, it.name, it.surname, it.email, it.role) },
    )

object TeamTable : IntIdTable("teams") {
    val name = varchar("name", 50).uniqueIndex()
    val location = varchar("location", 50)
    val manager = reference("manager", PersonTable).nullable()
}

object PersonTable : IntIdTable("persons") {
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val email = varchar("email", 50)
    val role = varchar("role", 50)
}

class TeamEntity(
    id: EntityID<Int>,
) : Entity<Int>(id) {
    companion object : EntityClass<Int, TeamEntity>(TeamTable)

    var name by TeamTable.name
    var location by TeamTable.location
    var manager by PersonEntity optionalReferencedOn TeamTable.manager
}

class PersonEntity(
    id: EntityID<Int>,
) : Entity<Int>(id) {
    companion object : EntityClass<Int, PersonEntity>(PersonTable)

    var name by PersonTable.name
    var surname by PersonTable.surname
    var email by PersonTable.email
    var role by PersonTable.role
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

    override suspend fun teamExists(id: Int): Boolean =
        dbQuery {
            TeamEntity.findById(id) != null
        }

    override suspend fun teamExistsByName(name: String) =
        dbQuery {
            TeamEntity
                .find { TeamTable.name eq name }
                .firstOrNull() != null
        }

    override suspend fun create(team: Team): Int =
        dbQuery {
            val newTeam =
                TeamEntity.new {
                    name = team.name
                    location = team.location
                    manager =
                        team.manager?.let {
                            PersonEntity.findById(it.uid)
                        }
                }
            newTeam.id.value
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
                .find { TeamTable.id greaterEq (lastKeySeen ?: 0) }
                .orderBy(TeamTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toTeam() }
                .toList()
        }

    override suspend fun update(team: Team): Team? =
        dbQuery {
            TeamEntity
                .findById(team.id)
                ?.also { teamEntity ->
                    teamEntity.name = team.name
                    teamEntity.location = team.location
                    teamEntity.manager = team.manager?.let { PersonEntity.findById(it.uid) }
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
}
