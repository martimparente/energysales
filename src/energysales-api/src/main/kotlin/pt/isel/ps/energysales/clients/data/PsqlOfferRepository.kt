package pt.isel.ps.energysales.clients.data

import pt.isel.ps.energysales.clients.data.entity.ClientEntity
import pt.isel.ps.energysales.clients.data.entity.OfferEntity
import pt.isel.ps.energysales.clients.data.entity.OfferLinkEntity
import pt.isel.ps.energysales.clients.domain.Offer
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.services.data.entity.ServiceEntity

class PsqlOfferRepository : OfferRepository {
    override suspend fun getById(id: Int): Offer? =
        dbQuery {
            OfferEntity.findById(id)?.toOffer()
        }

    override suspend fun create(offer: Offer): Int =
        dbQuery {
            OfferEntity
                .new {
                    createdAt = offer.createdAt
                    createdBy = SellerEntity.findById(offer.createdBy.user.id)!!
                    client = ClientEntity.findById(offer.client.id)!!
                    service = ServiceEntity.findById(offer.service.id)!!
                    link =
                        OfferLinkEntity.new(offer.link.uuid) {
                            url = offer.link.url
                            due = offer.link.due
                        }
                }.id
                .value
        }

    override suspend fun delete(offer: Offer): Boolean =
        dbQuery {
            OfferEntity.findById(offer.id)?.delete() ?: false
            true
        }
}
