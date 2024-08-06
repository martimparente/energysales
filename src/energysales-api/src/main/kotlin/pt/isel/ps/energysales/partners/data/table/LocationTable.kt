package pt.isel.ps.energysales.partners.data.table

import org.jetbrains.exposed.dao.id.IntIdTable
import pt.isel.ps.energysales.partners.domain.District

object LocationTable : IntIdTable() {
    val district = enumerationByName("district", 50, District::class)
}
