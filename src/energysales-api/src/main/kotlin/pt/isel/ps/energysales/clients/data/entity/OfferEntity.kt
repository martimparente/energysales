package pt.isel.ps.energysales.clients.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.clients.data.table.OfferLinkTable
import pt.isel.ps.energysales.clients.data.table.OfferTable
import pt.isel.ps.energysales.clients.domain.Offer
import pt.isel.ps.energysales.clients.domain.OfferLink
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.services.data.entity.ServiceEntity
import java.util.UUID

class OfferEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<OfferEntity>(OfferTable)

    fun toOffer() =
        Offer(
            id.value,
            createdAt,
            createdBy.toSeller(),
            client.toClient(),
            service.toService(),
            link.toOfferLink(),
        )

    var createdAt by OfferTable.createdAt
    var createdBy by SellerEntity referencedOn OfferTable.createdBy
    var client by ClientEntity referencedOn OfferTable.client
    var service by ServiceEntity referencedOn OfferTable.service
    var link by OfferLinkEntity referencedOn OfferTable.link
}

class OfferLinkEntity(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<OfferLinkEntity>(OfferLinkTable)

    fun toOfferLink() =
        OfferLink(
            id.value,
            url,
            due,
        )

    var url by OfferLinkTable.url
    var due by OfferLinkTable.due
}
