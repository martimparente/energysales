package pt.isel.ps.energysales.users.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.users.data.table.UserTable
import pt.isel.ps.energysales.users.domain.model.User
import pt.isel.ps.energysales.users.domain.model.toRole

open class UserEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UserTable)

    var name by UserTable.name
    var surname by UserTable.surname
    var email by UserTable.email
    var role by RoleEntity referencedOn UserTable.role

    fun toUser() =
        User(
            id.value,
            name,
            surname,
            email,
            role.name.toRole(),
        )
}
