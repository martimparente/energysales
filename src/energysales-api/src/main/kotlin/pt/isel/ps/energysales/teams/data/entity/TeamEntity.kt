package pt.isel.ps.energysales.teams.data.entity

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.clients.data.entity.ClientEntity
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.sellers.data.table.SellerTable
import pt.isel.ps.energysales.services.data.entity.ServiceEntity
import pt.isel.ps.energysales.teams.data.table.TeamClients
import pt.isel.ps.energysales.teams.data.table.TeamServices
import pt.isel.ps.energysales.teams.data.table.TeamTable
import pt.isel.ps.energysales.teams.domain.Team
import pt.isel.ps.energysales.users.data.entity.UserEntity

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
            avatarPath,
        )

    var name by TeamTable.name
    var location by LocationEntity referencedOn TeamTable.location
    var manager by UserEntity optionalReferencedOn TeamTable.manager
    var avatarPath by TeamTable.avatarPath
    val sellers by SellerEntity optionalReferrersOn SellerTable.team
    var services by ServiceEntity via TeamServices
    var clients by ClientEntity via TeamClients
}
