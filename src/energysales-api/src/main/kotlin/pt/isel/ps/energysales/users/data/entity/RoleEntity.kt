package pt.isel.ps.energysales.users.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Table
import pt.isel.ps.energysales.users.data.table.RoleTable
import pt.isel.ps.energysales.users.data.table.UserTable
import pt.isel.ps.energysales.users.domain.model.toRole

open class RoleEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<RoleEntity>(RoleTable)

    var name by RoleTable.name

    fun toRole() = name.toRole()
}

// Many-to-many relationship between users and roles
object UserRolesTable : Table() {
    val userId = reference("user_id", UserTable.id)
    val roleId = reference("role_id", RoleTable.id)

    override val primaryKey = PrimaryKey(userId, roleId)
}
