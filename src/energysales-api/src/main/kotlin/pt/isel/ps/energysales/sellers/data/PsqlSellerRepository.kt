package pt.isel.ps.energysales.sellers.data

import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import pt.isel.ps.energysales.partners.data.entity.PartnerEntity
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.sellers.data.table.SellerTable
import pt.isel.ps.energysales.sellers.domain.Seller
import pt.isel.ps.energysales.users.data.entity.RoleEntity
import pt.isel.ps.energysales.users.data.entity.UserEntity
import pt.isel.ps.energysales.users.data.table.RoleTable
import pt.isel.ps.energysales.users.data.table.UserTable
import pt.isel.ps.energysales.users.domain.Role

class PsqlSellerRepository : SellerRepository {
    override suspend fun getById(id: String): Seller? =
        dbQuery {
            SellerEntity.findById(id.toInt())?.toSeller()
        }

    override suspend fun sellerExists(id: String): Boolean =
        dbQuery {
            SellerEntity.findById(id.toInt()) != null
        }

    override suspend fun create(seller: Seller): String =
        dbQuery {
            val userEntity =
                UserEntity.new {
                    name = seller.user.name
                    surname = seller.user.surname
                    email = seller.user.email
                    role = RoleEntity.find { RoleTable.name eq Role.SELLER.name }.first()
                }

            SellerEntity
                .new(userEntity.id.value) {
                    totalSales = seller.totalSales
                    partner = seller.partner?.let { PartnerEntity.findById(it.toInt()) }
                }.id
                .value
                .toString()
        }

    override suspend fun getAll(): List<Seller> =
        dbQuery {
            SellerEntity
                .all()
                .with(SellerEntity::partner)
                .map { it.toSeller() }
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: String?,
        noPartner: Boolean,
    ): List<Seller> =
        dbQuery {
            SellerEntity
                .find {
                    SellerTable.id greater (lastKeySeen!!.toInt()) and
                        (if (noPartner) SellerTable.partner.isNull() else SellerTable.partner.isNotNull())
                }.orderBy(SellerTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toSeller() }
                .toList()
        }

    override suspend fun update(seller: Seller): Seller? =
        dbQuery {
            SellerEntity
                .findById(seller.user.id!!.toInt())
                ?.apply {
                    user.name = seller.user.name
                    user.surname = seller.user.surname
                    user.email = seller.user.email
                    totalSales = seller.totalSales
                    partner = seller.partner?.let { PartnerEntity.findById(it.toInt()) }
                }?.toSeller()
        }

    override suspend fun delete(seller: Seller): Boolean =
        dbQuery {
            SellerEntity.findById(seller.user.id!!.toInt())?.delete() ?: false
            true
        }

    override suspend fun getSellersWithNoPartner(searchQuery: String?): List<Seller> =
        dbQuery {
            val query =
                SellerTable
                    .innerJoin(UserTable)
                    .select(SellerTable.columns)
                    .where {
                        if (searchQuery != null) {
                            SellerTable.partner.isNull() and (UserTable.name like "%$searchQuery%")
                        } else {
                            SellerTable.partner.isNull()
                        }
                    }

            SellerEntity.wrapRows(query).map { it.toSeller() }
        }
}
