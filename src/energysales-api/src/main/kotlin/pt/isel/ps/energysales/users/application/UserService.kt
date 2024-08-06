package pt.isel.ps.energysales.users.application

import pt.isel.ps.energysales.users.application.dto.ChangeUserPasswordResult
import pt.isel.ps.energysales.users.application.dto.CreateUserInput
import pt.isel.ps.energysales.users.application.dto.DeleteUserResult
import pt.isel.ps.energysales.users.application.dto.GetUsersResult
import pt.isel.ps.energysales.users.application.dto.ResetPasswordResult
import pt.isel.ps.energysales.users.application.dto.RoleAssignResult
import pt.isel.ps.energysales.users.application.dto.RoleReadingResult
import pt.isel.ps.energysales.users.application.dto.TokenCreationResult
import pt.isel.ps.energysales.users.application.dto.UpdateUserInput
import pt.isel.ps.energysales.users.application.dto.UpdateUserResult
import pt.isel.ps.energysales.users.application.dto.UserCreationResult
import pt.isel.ps.energysales.users.application.dto.UserReadingResult
import pt.isel.ps.energysales.users.http.UserQueryParams

interface UserService {
    suspend fun createUser(input: CreateUserInput): UserCreationResult

    suspend fun createToken(
        username: String,
        password: String,
    ): TokenCreationResult

    suspend fun changeUserPassword(
        id: String,
        oldPassword: String,
        newPassword: String,
        repeatNewPassword: String,
    ): ChangeUserPasswordResult

    suspend fun resetPassword(email: String): ResetPasswordResult

    suspend fun getUserRole(id: String): RoleReadingResult

    suspend fun changeUserRole(
        id: String,
        role: String,
    ): RoleAssignResult

    suspend fun getUsers(params: UserQueryParams): GetUsersResult

    suspend fun getUser(id: String): UserReadingResult

    suspend fun updateUser(input: UpdateUserInput): UpdateUserResult

    suspend fun deleteUser(id: String): DeleteUserResult
}
