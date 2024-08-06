package pt.isel.ps.energysales.partners.data.entity

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.partners.data.table.LocationTable
import pt.isel.ps.energysales.partners.data.table.PartnerTable
import pt.isel.ps.energysales.partners.domain.Location

class LocationEntity(
    id: EntityID<Int>,
) : Entity<Int>(id) {
    companion object : EntityClass<Int, LocationEntity>(LocationTable)

    var district by LocationTable.district
    val partners by PartnerEntity referrersOn PartnerTable.location

    fun toLocation() = Location(district)
}
