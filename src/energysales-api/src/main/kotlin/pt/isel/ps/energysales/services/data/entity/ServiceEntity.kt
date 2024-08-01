package pt.isel.ps.energysales.services.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.services.data.table.ServiceTable
import pt.isel.ps.energysales.services.domain.Service

class ServiceEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<ServiceEntity>(ServiceTable)

    fun toService() = Service(id.value.toString(), name, description, cycleName, cycleType, periodName, periodNumPeriods, price.toPrice())

    var name by ServiceTable.name
    var description by ServiceTable.description
    var cycleName by ServiceTable.cycleName
    var cycleType by ServiceTable.cycleType
    var periodName by ServiceTable.periodName
    var periodNumPeriods by ServiceTable.periodNumPeriods
    var price by PriceEntity referencedOn ServiceTable.price
}
