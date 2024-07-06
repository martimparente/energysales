package pt.isel.ps.energysales.teams.data.entity

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.teams.data.table.LocationTable
import pt.isel.ps.energysales.teams.data.table.TeamTable
import pt.isel.ps.energysales.teams.domain.Location

class LocationEntity(
    id: EntityID<Int>,
) : Entity<Int>(id) {
    companion object : EntityClass<Int, LocationEntity>(LocationTable)

    var district by LocationTable.district
    val teams by TeamEntity referrersOn TeamTable.location

    fun toLocation() = Location(district)
}
