package pt.isel.ps.energysales.services.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SortOrder
import pt.isel.ps.energysales.plugins.DatabaseSingleton.dbQuery
import pt.isel.ps.energysales.services.domain.Service

object ServiceTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val description = varchar("description", 255)
    val cycleName = varchar("cycle_name", 50)
    val cycleType = varchar("cycle_type", 50)
    val periodName = varchar("period_name", 50)
    val periodNumPeriods = integer("period_num_periods")
}

class ServiceEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<ServiceEntity>(ServiceTable)

    fun toService() = Service(id.value, name, description, cycleName, cycleType, periodName, periodNumPeriods)

    var name by ServiceTable.name
    var description by ServiceTable.description
    var cycleName by ServiceTable.cycleName
    var cycleType by ServiceTable.cycleType
    var periodName by ServiceTable.periodName
    var periodNumPeriods by ServiceTable.periodNumPeriods
}

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
            ServiceEntity
                .findById(service.id)
                ?.also { serviceEntity ->
                    serviceEntity.name = service.name
                }?.toService()
        }

    override suspend fun delete(service: Service): Boolean =
        dbQuery {
            ServiceEntity
                .find { ServiceTable.name eq service.name }
                .firstOrNull()
                ?.delete() ?: false
            true
        }
}
