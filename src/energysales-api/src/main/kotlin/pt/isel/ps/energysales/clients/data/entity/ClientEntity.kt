package pt.isel.ps.energysales.clients.data.entity

import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.clients.data.table.ClientTable
import pt.isel.ps.energysales.clients.domain.Client
import pt.isel.ps.energysales.teams.data.entity.LocationEntity
import pt.isel.ps.energysales.teams.data.entity.TeamEntity

class ClientEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<ClientEntity>(ClientTable)

    fun toClient() = Client(id.value, name, nif, phone, location.toLocation(), team.id.value, seller?.id?.value)

    var name by ClientTable.name
    var nif by ClientTable.nif
    var phone by ClientTable.phone
    var location by LocationEntity referencedOn ClientTable.location
    var team by TeamEntity referencedOn ClientTable.team
    var seller by SellerEntity optionalReferencedOn ClientTable.seller
}
