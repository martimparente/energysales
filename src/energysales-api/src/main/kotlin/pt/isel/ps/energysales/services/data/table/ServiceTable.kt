package pt.isel.ps.energysales.services.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

object ServiceTable : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val description = varchar("description", 255)
    val cycleName = varchar("cycle_name", 50)
    val cycleType = varchar("cycle_type", 50)
    val periodName = varchar("period_name", 50)
    val periodNumPeriods = integer("period_num_periods")
    val price = reference("price_id", PriceTable)
}
