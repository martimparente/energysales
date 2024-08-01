package pt.isel.ps.energysales.users.data.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import pt.isel.ps.energysales.users.data.table.UserCredentialsTable
import pt.isel.ps.energysales.users.domain.UserCredentials

class UserCredentialEntity(
    id: EntityID<Int>,
) : IntEntity(id) {
    companion object : IntEntityClass<UserCredentialEntity>(UserCredentialsTable)

    var username by UserCredentialsTable.username
    var password by UserCredentialsTable.password
    var salt by UserCredentialsTable.salt

    fun toUserCredentials() =
        UserCredentials(
            id.value.toString(),
            username,
            password,
            salt,
        )
}
