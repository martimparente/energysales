package pt.isel.ps.energysales.services.data

import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.services.data.entity.PriceEntity
import pt.isel.ps.energysales.services.data.entity.ServiceEntity
import pt.isel.ps.energysales.services.data.table.ServiceTable
import pt.isel.ps.energysales.services.domain.Service

class PsqlServiceRepository : ServiceRepository {
    override suspend fun getById(id: Int): Service? =
        dbQuery {
            ServiceEntity.findById(id)?.toService()
        }

    override suspend fun serviceExists(id: Int): Boolean =
        dbQuery {
            ServiceEntity.findById(id) != null
        }

    override suspend fun serviceExistsByName(name: String) =
        dbQuery {
            ServiceEntity
                .find { ServiceTable.name eq name }
                .count() > 0
        }

    override suspend fun create(service: Service): Int =
        dbQuery {
            ServiceEntity
                .new {
                    name = service.name
                    description = service.description
                    cycleName = service.cycleName
                    cycleType = service.cycleType
                    periodName = service.periodName
                    periodNumPeriods = service.periodNumPeriods
                    price =
                        PriceEntity.new {
                            ponta = service.price.ponta
                            cheia = service.price.cheia
                            vazio = service.price.vazio
                            superVazio = service.price.superVazio
                            operadorMercado = service.price.operadorMercado
                            gdo = service.price.gdo
                            omip = service.price.omip
                            margem = service.price.margem
                        }
                }.id
                .value
        }

    override suspend fun getAll(): List<Service> =
        dbQuery {
            ServiceEntity
                .all()
                .map { it.toService() }
        }

    override suspend fun getAllKeyPaging(
        pageSize: Int,
        lastKeySeen: Int?,
    ): List<Service> =
        dbQuery {
            ServiceEntity
                .find { ServiceTable.id greaterEq (lastKeySeen ?: 0) }
                .orderBy(ServiceTable.id to SortOrder.ASC)
                .limit(pageSize)
                .map { it.toService() }
                .toList()
        }

    override suspend fun getByName(name: String): Service? {
        TODO("Not yet implemented")
    }

    override suspend fun update(service: Service): Service? =
        dbQuery {
            ServiceEntity.findById(service.id)?.apply {
                name = service.name
                description = service.description
                cycleName = service.cycleName
                cycleType = service.cycleType
                periodName = service.periodName
                periodNumPeriods = service.periodNumPeriods
                price.apply {
                    ponta = service.price.ponta
                    cheia = service.price.cheia
                    vazio = service.price.vazio
                    superVazio = service.price.superVazio
                    operadorMercado = service.price.operadorMercado
                    gdo = service.price.gdo
                    omip = service.price.omip
                    margem = service.price.margem
                }
            }?.toService()
        }


    override suspend fun delete(service: Service): Boolean =
        dbQuery {
            ServiceEntity.findById(service.id)?.delete() ?: false
            true
        }
}
