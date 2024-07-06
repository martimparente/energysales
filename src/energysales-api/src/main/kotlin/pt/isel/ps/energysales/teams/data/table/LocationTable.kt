package pt.isel.ps.energysales.teams.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

object LocationTable : IntIdTable() {
    val district = varchar("district", 50).uniqueIndex()
}
