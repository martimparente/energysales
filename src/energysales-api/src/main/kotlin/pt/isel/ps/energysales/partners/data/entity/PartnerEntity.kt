package pt.isel.ps.energysales.partners.data.entity

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.clients.data.entity.ClientEntity
import pt.isel.ps.energysales.partners.data.table.PartnerClients
import pt.isel.ps.energysales.partners.data.table.PartnerServices
import pt.isel.ps.energysales.partners.data.table.PartnerTable
import pt.isel.ps.energysales.partners.domain.Partner
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.sellers.data.table.SellerTable
import pt.isel.ps.energysales.services.data.entity.ServiceEntity
import pt.isel.ps.energysales.users.data.entity.UserEntity

class PartnerEntity(
    id: EntityID<Int>,
) : Entity<Int>(id) {
    companion object : EntityClass<Int, PartnerEntity>(PartnerTable)

    fun toPartner() =
        Partner(
            id.value.toString(),
            name,
            location.toLocation(),
            manager?.id?.value.toString(),
            avatarPath,
        )

    var name by PartnerTable.name
    var location by LocationEntity referencedOn PartnerTable.location
    var manager by UserEntity optionalReferencedOn PartnerTable.manager
    var avatarPath by PartnerTable.avatarPath
    val sellers by SellerEntity optionalReferrersOn SellerTable.partner
    var services by ServiceEntity via PartnerServices
    var clients by ClientEntity via PartnerClients
}
