package pt.isel.ps.energysales.clients.data

import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.energysales.clients.data.entity.ClientEntity
import pt.isel.ps.energysales.clients.data.table.ClientTable
import pt.isel.ps.energysales.clients.domain.Client
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.sellers.data.entity.SellerEntity
import pt.isel.ps.energysales.teams.data.entity.LocationEntity
import pt.isel.ps.energysales.teams.data.table.LocationTable

class PsqlClientRepository : ClientRepository {
    override suspend fun getById(id: String): Client? =
        dbQuery {
            ClientEntity.find { ClientTable.id eq id.toInt() }.firstOrNull()?.toClient()
        }

    override suspend fun clientExists(id: String): Boolean =
        dbQuery {
            ClientEntity.find { ClientTable.id eq id.toInt() }.count() > 0
        }

    override suspend fun clientExistsByName(name: String) =
        dbQuery {
            ClientEntity
                .find { ClientTable.name eq name }
                .count() > 0
        }

    override suspend fun create(client: Client): String =
        dbQuery {
            ClientEntity
                .new {
                    name = client.name
                    nif = client.nif
                    phone = client.phone
                    email = client.email
                    location = LocationEntity.find { LocationTable.district eq client.location.district }.first()
                    seller = client.sellerId?.let { SellerEntity.findById(it.toInt()) }
                }.id
                .value
                .toString()
        }

    override suspend fun getAll(): List<Client> =
        dbQuery {
            ClientEntity
                .all()
                .map { it.toClient() }
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: String?,
    ): List<Client> =
        dbQuery {
            val query =
                if (lastKeySeen != null) {
                    ClientEntity
                        .find { ClientTable.id greater lastKeySeen.toInt() }
                        .orderBy(ClientTable.id to SortOrder.ASC)
                        .limit(pageSize)
                } else {
                    ClientEntity
                        .all()
                        .orderBy(ClientTable.id to SortOrder.ASC)
                        .limit(pageSize)
                }
            query.map { it.toClient() }.toList()
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
                .find { ClientTable.id eq client.id?.toInt() }
                .firstOrNull()
                ?.also { clientEntity ->
                    clientEntity.name = client.name
                }?.toClient()
        }

    override suspend fun delete(client: Client): Boolean =
        dbQuery {
            ClientEntity.find { ClientTable.id eq client.id?.toInt() }.firstOrNull()?.delete() ?: false
            true
        }
}
