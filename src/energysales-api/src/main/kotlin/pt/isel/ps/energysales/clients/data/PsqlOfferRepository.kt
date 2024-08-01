package pt.isel.ps.energysales.clients.data

import pt.isel.ps.energysales.clients.data.entity.ClientEntity
import pt.isel.ps.energysales.clients.data.entity.OfferEntity
import pt.isel.ps.energysales.clients.data.entity.OfferLinkEntity
import pt.isel.ps.energysales.clients.data.table.OfferTable
import pt.isel.ps.energysales.clients.domain.Offer
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.services.data.entity.ServiceEntity

class PsqlOfferRepository : OfferRepository {
    override suspend fun getById(id: String): Offer? =
        dbQuery {
            OfferEntity.findById(id.toInt())?.toOffer()
        }

    override suspend fun getByClient(clientId: String): Offer? =
        dbQuery {
            OfferEntity
                .find {
                    OfferTable.client eq clientId.toInt()
                }.firstOrNull()
                ?.toOffer()
        }

    override suspend fun create(offer: Offer): String =
        dbQuery {
            val sellerEntity =
                SellerEntity.findById(
                    offer
                        .createdBy
                        .user
                        .id!!
                        .toInt(),
                )
                    ?: throw IllegalArgumentException("Seller not found with id: ${offer.createdBy.user.id}")

            val clientEntity =
                ClientEntity.findById(offer.client.id!!.toInt())
                    ?: throw IllegalArgumentException("Client not found with id: ${offer.client.id}")

            val serviceEntity =
                ServiceEntity.findById(offer.service.id)
                    ?: throw IllegalArgumentException("Service not found with id: ${offer.service.id}")

            val offerLinkEntity =
                OfferLinkEntity.new(offer.link.uuid) {
                    url = offer.link.url
                    due = offer.link.due
                }

            OfferEntity
                .new {
                    createdAt = offer.createdAt
                    createdBy = sellerEntity
                    client = clientEntity
                    service = serviceEntity
                    link = offerLinkEntity
                }.id
                .value
                .toString()
        }

    override suspend fun delete(offer: Offer): Boolean =
        dbQuery {
            OfferEntity.findById(offer.id!!.toInt())?.delete() ?: false
            true
        }
}
