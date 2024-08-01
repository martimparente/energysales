package pt.isel.ps.energysales.teams.data.table

import org.jetbrains.exposed.dao.id.IntIdTable
import pt.isel.ps.energysales.teams.domain.District

object LocationTable : IntIdTable() {
    val district = enumerationByName("district", 50, District::class)
}
