package pt.isel.ps.energysales.partners.data

import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import pt.isel.ps.energysales.clients.data.entity.ClientEntity
import pt.isel.ps.energysales.partners.data.entity.LocationEntity
import pt.isel.ps.energysales.partners.data.entity.PartnerEntity
import pt.isel.ps.energysales.partners.data.table.PartnerServices
import pt.isel.ps.energysales.partners.data.table.PartnerTable
import pt.isel.ps.energysales.partners.domain.Partner
import pt.isel.ps.energysales.partners.domain.PartnerDetails
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.services.data.entity.ServiceEntity
import pt.isel.ps.energysales.users.data.entity.UserEntity

class PsqlPartnerRepository : PartnerRepository {
    override suspend fun getByName(name: String): Partner? =
        dbQuery {
            PartnerEntity
                .find { PartnerTable.name eq name }
                .firstOrNull()
                ?.toPartner()
        }

    override suspend fun getById(id: String): Partner? =
        dbQuery {
            PartnerEntity.findById(id.toInt())?.toPartner()
        }

    override suspend fun getByIdWithDetails(id: String): PartnerDetails? =
        dbQuery {
            PartnerEntity
                .findById(id.toInt())
                ?.let {
                    PartnerDetails(
                        it.toPartner(),
                        it.sellers.map { it.toSeller() },
                        it.services.map { it.toService() },
                    )
                }
        }

    override suspend fun partnerExists(id: String): Boolean =
        dbQuery {
            PartnerEntity.findById(id.toInt()) != null
        }

    override suspend fun partnerExistsByName(name: String): Boolean =
        dbQuery {
            PartnerEntity
                .find { PartnerTable.name eq name }
                .count() > 0
        }

    override suspend fun create(partner: Partner): Int =
        dbQuery {
            PartnerEntity
                .new {
                    name = partner.name
                    location =
                        LocationEntity.new {
                            district = partner.location.district
                        }
                    manager = partner.managerId?.let { UserEntity.findById(it.toInt()) }
                    avatarPath = partner.avatarPath
                }.id
                .value
        }

    override suspend fun getAll(): List<Partner> =
        dbQuery {
            PartnerEntity
                .all()
                .map { it.toPartner() }
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ): List<Partner> =
        dbQuery {
            PartnerEntity
                .find { PartnerTable.id greater (lastKeySeen ?: 0) }
                .orderBy(PartnerTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toPartner() }
                .toList()
        }

    override suspend fun update(partner: Partner): Partner? =
        dbQuery {
            PartnerEntity
                .findById(partner.id?.toInt()!!)
                ?.apply {
                    name = partner.name
                    location = LocationEntity.findById(location.id.value) ?: LocationEntity.new {
                        district = partner.location.district
                    }
                    manager = partner.managerId?.let { UserEntity.findById(it.toInt()) }
                    avatarPath = partner.avatarPath
                }?.toPartner()
        }

    override suspend fun delete(partner: Partner): Boolean =
        dbQuery {
            PartnerEntity
                .find { PartnerTable.name eq partner.name }
                .firstOrNull()
                ?.delete() ?: false
            true
        }

    override suspend fun getPartnerSellers(id: String): List<Seller> =
        dbQuery {
            PartnerEntity
                .findById(id.toInt())
                ?.sellers
                ?.map { it.toSeller() }
                ?: emptyList()
        }

    override suspend fun addSellerToPartner(
        partnerId: String,
        sellerId: String,
    ): Boolean =
        dbQuery {
            val seller = SellerEntity.findById(sellerId.toInt()) ?: return@dbQuery false
            seller.partner = PartnerEntity.findById(partnerId.toInt()) ?: return@dbQuery false
            true
        }

    override suspend fun deleteSellerFromPartner(sellerId: String): Boolean =
        dbQuery {
            val seller = SellerEntity.findById(sellerId.toInt()) ?: return@dbQuery false
            seller.partner = null
            true
        }

    override suspend fun addServiceToPartner(
        partnerId: String,
        serviceId: String,
    ): Boolean =
        dbQuery {
            val partner = PartnerEntity.findById(partnerId.toInt()) ?: return@dbQuery false
            val service = ServiceEntity.findById(serviceId.toInt()) ?: return@dbQuery false
            partner.services = SizedCollection(partner.services + service)
            true
        }

    override suspend fun deleteServiceFromPartner(
        partnerID: String,
        serviceId: String,
    ): Boolean =
        dbQuery {
            PartnerServices.deleteWhere {
                (PartnerServices.partner eq partnerID.toInt()) and (PartnerServices.service eq serviceId.toInt())
            } > 0
        }

    override suspend fun addClientToPartner(
        partnerId: String,
        clientId: String,
    ): Boolean =
        dbQuery {
            val partner = PartnerEntity.findById(partnerId.toInt()) ?: return@dbQuery false
            val client = ClientEntity.findById(clientId.toInt()) ?: return@dbQuery false
            partner.clients = SizedCollection(partner.clients + client)
            true
        }
}
