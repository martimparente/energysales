package pt.isel.ps.salescentral.clients.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.salescentral.clients.domain.model.Client
import pt.isel.ps.salescentral.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.salescentral.teams.data.LocationEntity
import pt.isel.ps.salescentral.teams.data.LocationTable

object ClientTable : IntIdTable() {
    val name = varchar("name", 50)
    val nif = varchar("nif", 9).uniqueIndex()
    val phone = varchar("phone", 9).uniqueIndex()
    val location = reference("location", LocationTable)
}

class ClientEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<ClientEntity>(ClientTable)

    fun toClient() = Client(id.value, name, nif, phone, location.toLocation())

    var name by ClientTable.name
    var nif by ClientTable.nif
    var phone by ClientTable.phone
    var location by LocationEntity referencedOn ClientTable.location
}

class PsqlClientRepository : ClientRepository {
    override suspend fun getById(id: Int): Client? =
        dbQuery {
            ClientEntity.findById(id)?.toClient()
        }

    override suspend fun clientExists(id: Int): Boolean =
        dbQuery {
            ClientEntity.findById(id) != null
        }

    override suspend fun clientExistsByName(name: String) =
        dbQuery {
            ClientEntity
                .find { ClientTable.name eq name }
                .count() > 0
        }

    override suspend fun create(client: Client): Int =
        dbQuery {
            ClientEntity
                .new {
                    name = client.name
                    nif = client.nif
                    phone = client.phone
                    location = LocationEntity.find { LocationTable.district eq client.location.district }.first()
                }.id
                .value
        }

    override suspend fun getAll(): List<Client> =
        dbQuery {
            ClientEntity
                .all()
                .map { it.toClient() }
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ): List<Client> =
        dbQuery {
            ClientEntity
                .find { ClientTable.id greaterEq (lastKeySeen ?: 0) }
                .orderBy(ClientTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toClient() }
                .toList()
        }

    override suspend fun getByNif(nif: String): Client? =
        dbQuery {
            ClientEntity
                .find { ClientTable.nif eq nif }
                .firstOrNull()
                ?.toClient()
        }

    override suspend fun update(client: Client): Client? =
        dbQuery {
            ClientEntity
                .findById(client.id)
                ?.also { clientEntity ->
                    clientEntity.name = client.name
                }?.toClient()
        }

    override suspend fun delete(client: Client): Boolean =
        dbQuery {
            ClientEntity.findById(client.id)?.delete() ?: false
            true
        }
}
