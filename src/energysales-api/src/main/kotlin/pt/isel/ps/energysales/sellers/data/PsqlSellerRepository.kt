package pt.isel.ps.energysales.sellers.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import pt.isel.ps.energysales.auth.data.UserEntity
import pt.isel.ps.energysales.auth.data.UserTable
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.sellers.domain.model.Seller
import pt.isel.ps.energysales.teams.data.TeamEntity
import pt.isel.ps.energysales.teams.data.TeamTable

object SellerTable : IdTable<Int>() {
    val totalSales = float("total_sales").default(0.0f)
    val team = reference("team_id", TeamTable.id, ReferenceOption.SET_NULL).nullable()
    override val id: Column<EntityID<Int>> = reference("id", UserTable.id)
}

class SellerEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<SellerEntity>(SellerTable)

    var uid by UserEntity referencedOn UserTable.id
    var totalSales by SellerTable.totalSales
    var team by TeamEntity optionalReferencedOn SellerTable.team

    fun toSeller() =
        Seller(
            uid.id.value,
            totalSales,
            team?.id?.value,
        )
}

class PsqlSellerRepository : SellerRepository {
    override suspend fun getById(id: Int): Seller? =
        dbQuery {
            SellerEntity.findById(id)?.toSeller()
        }

    override suspend fun sellerExists(id: Int): Boolean =
        dbQuery {
            SellerEntity.findById(id) != null
        }

    override suspend fun create(seller: Seller): Int =
        dbQuery {
            val user = UserEntity.findById(seller.uid) ?: throw IllegalArgumentException("User not found")
            SellerEntity
                .new {
                    uid = user
                    totalSales = seller.totalSales
                    team = seller.team?.let { TeamEntity.findById(it) }
                }.id
                .value
        }

    override suspend fun getAll(): List<Seller> =
        dbQuery {
            SellerEntity
                .all()
                .with(SellerEntity::team)
                .map { Seller(it.uid.id.value, it.totalSales, it.team?.id?.value) } // todo teams empty?
                .toList()
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int?,
        noTeam: Boolean,
    ): List<Seller> =
        dbQuery {
            SellerEntity
                .find {
                    SellerTable.id greater (lastKeySeen ?: 0) and
                        (if (noTeam) SellerTable.team.isNull() else SellerTable.team.isNotNull())
                }.orderBy(SellerTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toSeller() }
                .toList()
        }

    override suspend fun update(seller: Seller): Seller? =
        dbQuery {
            SellerEntity.findById(seller.uid)?.let { team ->
                team.totalSales = seller.totalSales
                team.team = seller.team?.let { TeamEntity.findById(it) }
                team.toSeller()
            }
        }

    override suspend fun delete(seller: Seller): Boolean =
        dbQuery {
            SellerEntity.findById(seller.uid)?.delete() ?: false
            true
        }
}
