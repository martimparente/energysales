package pt.isel.ps.energysales.teams.data.entity

import SellerEntity
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table
import pt.isel.ps.energysales.sellers.data.table.SellerTable
import pt.isel.ps.energysales.services.data.entity.ServiceEntity
import pt.isel.ps.energysales.services.data.table.ServiceTable
import pt.isel.ps.energysales.teams.data.table.TeamTable
import pt.isel.ps.energysales.teams.domain.Team
import pt.isel.ps.energysales.users.data.entity.UserEntity

object TeamServices : Table() {
    val team = reference("team", TeamTable)
    val service = reference("service", ServiceTable)
    override val primaryKey = PrimaryKey(team, service)
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
            manager?.id?.value,
        )

    var name by TeamTable.name
    var location by LocationEntity referencedOn TeamTable.location
    var manager by UserEntity optionalReferencedOn TeamTable.manager
    val sellers by SellerEntity optionalReferrersOn SellerTable.team
    var services by ServiceEntity via TeamServices
}
