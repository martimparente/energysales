package pt.isel.ps.energysales.clients.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.clients.data.table.ClientTable
import pt.isel.ps.energysales.clients.domain.Client
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.teams.data.entity.LocationEntity

class ClientEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<ClientEntity>(ClientTable)

    fun toClient() = Client(id.value.toString(), name, nif, phone, email, location.toLocation(), seller?.id?.value.toString())

    var name by ClientTable.name
    var nif by ClientTable.nif
    var phone by ClientTable.phone
    var email by ClientTable.email
    var location by LocationEntity referencedOn ClientTable.location
    var seller by SellerEntity optionalReferencedOn ClientTable.seller
}
