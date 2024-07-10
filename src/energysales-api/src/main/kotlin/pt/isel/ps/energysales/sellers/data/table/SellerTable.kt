package pt.isel.ps.energysales.sellers.data.table

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import pt.isel.ps.energysales.teams.data.table.TeamTable
import pt.isel.ps.energysales.users.data.table.UserTable

object SellerTable : IdTable<Int>() {
    override val id: Column<EntityID<Int>> = reference("uid", UserTable)
    override val primaryKey = PrimaryKey(id)

    val totalSales = float("total_sales").default(0.0f)
    val team = reference("team_id", TeamTable.id, ReferenceOption.SET_NULL).nullable()
}
