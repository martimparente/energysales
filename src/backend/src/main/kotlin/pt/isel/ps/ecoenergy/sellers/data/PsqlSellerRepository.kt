package pt.isel.ps.ecoenergy.sellers.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import pt.isel.ps.ecoenergy.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.ecoenergy.sellers.domain.model.Person
import pt.isel.ps.ecoenergy.sellers.domain.model.Seller
import pt.isel.ps.ecoenergy.teams.data.TeamEntity
import pt.isel.ps.ecoenergy.teams.data.TeamTable

enum class Role { SELLER, ADMIN }

object PersonTable : IntIdTable() {
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val email = varchar("email", 254).uniqueIndex()
    val role = enumerationByName("role", 255, Role::class).nullable()
}

object SellerTable : IdTable<Int>() {
    val totalSales = float("total_sales").default(0.0f)
    override val id: Column<EntityID<Int>> = reference("person_id", PersonTable.id).uniqueIndex()
}

object TeamSeller : Table() {
    val team = reference("team_id", TeamTable.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val seller = reference("person_id", SellerTable.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    override val primaryKey = PrimaryKey(team, seller)
}

open class PersonEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<PersonEntity>(PersonTable)

    fun toPerson() =
        Person(
            id.value,
            name,
            surname,
            email,
            role,
        )

    var name by PersonTable.name
    var surname by PersonTable.surname
    var email by PersonTable.email
    var role by PersonTable.role
}

class SellerEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<SellerEntity>(SellerTable)

    fun toSeller() =
        Seller(
            person.toPerson(),
            totalSales,
            teams.map { id.value },
        )

    var person by PersonEntity referencedOn SellerTable.id
    var totalSales by SellerTable.totalSales
    var teams by TeamEntity.via(TeamSeller.seller, TeamSeller.team)
}

class PsqlSellerRepository : SellerRepository {
    override suspend fun getById(id: Int): Seller? =
        dbQuery {
            SellerEntity.findById(id)?.toSeller()
        }

    override suspend fun isEmailAvailable(email: String): Boolean =
        dbQuery {
            PersonEntity
                .find { PersonTable.email eq email }
                .empty()
        }

    override suspend fun sellerExists(id: Int): Boolean =
        dbQuery {
            SellerEntity.findById(id) != null
        }

    override suspend fun create(seller: Seller): Int =
        dbQuery {
            var person =
                PersonEntity.new {
                    name = seller.person.name
                    surname = seller.person.surname
                    email = seller.person.email
                    role = seller.person.role
                }
            SellerEntity
                .new(person.id.value) {
                    totalSales = seller.totalSales
                }.person
                .id
                .value
        }

    override suspend fun getAll(): List<Seller> =
        dbQuery {
            SellerEntity
                .all()
                .with(SellerEntity::teams)
                .map { Seller(it.person.toPerson(), it.totalSales, it.teams.map { team -> team.id.value }) } // todo teams empty?
                .toList()
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ): List<Seller> =
        dbQuery {
            println(
                SellerEntity
                    .find { SellerTable.id eq 1 }
                    .firstOrNull()
                    ?.person
                    ?.name,
            )
            /*SellerEntity
                .find { SellerTable.id greaterEq (lastKeySeen ?: 0) }
                .orderBy(SellerTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { Seller(it.toPerson(), it.totalSales, it.teams.map { team -> team.id.value }) }
                .toList()*/
            emptyList()
        }

    override suspend fun update(seller: Seller): Seller? =
        dbQuery {
            SellerEntity
                .findById(seller.person.id)
                ?.also { sellerEntity ->
                    sellerEntity.person.name = seller.person.name
                    sellerEntity.person.surname = seller.person.surname
                    sellerEntity.person.email = seller.person.email
                    sellerEntity.totalSales = seller.totalSales
                    sellerEntity.teams = TeamEntity.forIds(seller.teams)
                }
            Seller(seller.person, seller.totalSales, seller.teams)
        }

    override suspend fun delete(seller: Seller): Boolean =
        dbQuery {
            SellerEntity
                .find { SellerTable.id eq seller.person.id }
                .firstOrNull()
                ?.delete() ?: false
            true
        }
}
