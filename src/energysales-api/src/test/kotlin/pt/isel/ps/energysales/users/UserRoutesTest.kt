package pt.isel.ps.energysales.users

import io.kotest.assertions.ktor.client.shouldHaveContentType
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import pt.isel.ps.energysales.BaseRouteTest
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.plugins.ProblemJSON
import pt.isel.ps.energysales.users.http.model.ChangePasswordRequest
import pt.isel.ps.energysales.users.http.model.CreateUserRequest
import pt.isel.ps.energysales.users.http.model.LoginRequest
import pt.isel.ps.energysales.users.http.model.LoginResponse
import pt.isel.ps.energysales.users.http.model.PatchUserRequest
import pt.isel.ps.energysales.users.http.model.RoleRequest
import pt.isel.ps.energysales.users.http.model.UserJSON
import pt.isel.ps.energysales.users.http.model.UserProblem
import kotlin.test.Test

class UserRoutesTest : BaseRouteTest() {
    @Test
    fun `Create User - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser",
                            "SecurePass123!",
                            "SecurePass123!",
                            "Test",
                            "User",
                            "test@test.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.Created)
                }
        }

    @Test
    fun `Create User - Invalid username length`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "ab",
                            "SecurePass123!",
                            "SecurePass123!",
                            "Test",
                            "User",
                            "test@test.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userUsernameIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userUsernameIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - Username already exists`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "adminUser",
                            "SecurePass123!",
                            "SecurePass123!",
                            "Test",
                            "User",
                            "test@test.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userAlreadyExists.type)
                    response.shouldHaveStatus(UserProblem.userAlreadyExists.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - Password mismatch`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser",
                            "SecurePass123!",
                            "SecurePass321!",
                            "Test",
                            "User",
                            "test@test.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.passwordMismatch.type)
                    response.shouldHaveStatus(UserProblem.passwordMismatch.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - Insecure Password`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser",
                            "insecurepassword",
                            "insecurepassword",
                            "Test",
                            "User",
                            "test@test.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.insecurePassword.type)
                    response.shouldHaveStatus(UserProblem.insecurePassword.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser",
                            "SecurePass123!",
                            "SecurePass123!",
                            "Test",
                            "User",
                            "test@test.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get User by ID - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                }.also { response ->
                    val user = response.call.response.body<UserJSON>()
                    user.id.shouldBe("1")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Get User by ID - Not Found`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", -1)
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userNotFound.type)
                    response.shouldHaveStatus(HttpStatusCode.NotFound)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get User by ID - Bad request`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "paramTypeInvalid")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.badRequest.type)
                    response.shouldHaveStatus(UserProblem.badRequest.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get User by ID - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("id", "1")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update User - Success`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        PatchUserRequest(
                            name = "updatedUsername",
                            surname = "UpdatedFirstName",
                            email = "updated@test.com",
                            role = "SELLER",
                        ),
                    )
                }.also { response ->
                    response.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Update User - Invalid name length`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        PatchUserRequest(
                            name = "a",
                            surname = "UpdatedFirstName",
                            email = "updated@test.com",
                            role = "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userNameIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userNameIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update User - User not found`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "-1")
                    setBody(
                        PatchUserRequest(
                            name = "updatedUsername",
                            surname = "UpdatedFirstName",
                            email = "updated@test.com",
                            role = "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userNotFound.type)
                    response.shouldHaveStatus(UserProblem.userNotFound.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update User - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $sellerToken")
                    parameter("id", "1")
                    setBody(
                        PatchUserRequest(
                            name = "updatedUsername",
                            surname = "UpdatedFirstName",
                            email = "updated@test.com",
                            role = "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Login - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_LOGIN) {
                    setBody(LoginRequest("adminUser", "SecurePass123!"))
                }.also { response ->
                    response.body<LoginResponse>().token.shouldStartWith("ey")
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.parse("application/json; charset=UTF-8"))
                }
        }

    @Test
    fun `Login - User not found`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_LOGIN) {
                    setBody(LoginRequest("nonExistUser", "SecurePass123!"))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userOrPasswordAreInvalid.type)
                    response.shouldHaveStatus(UserProblem.userOrPasswordAreInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Login - Password invalid`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.AUTH_LOGIN) {
                    setBody(LoginRequest("testUser", "wrongPassword"))
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userOrPasswordAreInvalid.type)
                    response.shouldHaveStatus(UserProblem.userOrPasswordAreInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Roles from User - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.USERS_ROLE) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.OK)
                    it.shouldHaveContentType(ContentType.parse("application/json; charset=UTF-8"))
                }
        }

    @Test
    fun `Change User Role - Success`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.USERS_ROLE) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(RoleRequest("NONE"))
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.Created)
                }
        }

    @Test
    fun `Change User Role - Forbidden - No permission Role`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.USERS_ROLE) {
                    headers.append("Authorization", "Bearer $sellerToken")
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.forbidden.type)
                    response.shouldHaveStatus(UserProblem.forbidden.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Change Password - Success`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        ChangePasswordRequest(
                            "SecurePass123!",
                            "SecurePass123!",
                            "SecurePass123!",
                        ),
                    )
                }.also {
                    it.shouldHaveStatus(HttpStatusCode.OK)
                }
        }

    @Test
    fun `Change Password - Wrong password`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "2")
                    setBody(
                        ChangePasswordRequest(
                            "wrongPassword123!",
                            "SecurePass123!",
                            "SecurePass123!",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.wrongPassword.type)
                    response.shouldHaveStatus(UserProblem.wrongPassword.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Change Password - Password mismatch`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "2")
                    setBody(
                        ChangePasswordRequest(
                            "SecurePass123!",
                            "SecurePass123!",
                            "notMatchingPass!",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.passwordMismatch.type)
                    response.shouldHaveStatus(UserProblem.passwordMismatch.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Get Users - Filter Candidates Manager - Success`() =
        testApplication {
            testClient()
                .get(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("role", "MANAGER")
                    parameter("available", "true")
                }.also { response ->
                    val users = response.call.response.body<List<UserJSON>>()
                    response.shouldHaveStatus(HttpStatusCode.OK)
                    response.shouldHaveContentType(ContentType.parse("application/json; charset=UTF-8"))
                }
        }

    @Test
    fun `Create User - User Email is Invalid`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser",
                            "SecurePass123!",
                            "SecurePass123!",
                            "Test",
                            "User",
                            "invalid-email",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userEmailIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userEmailIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - User Name is Invalid`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser1",
                            "SecurePass123!",
                            "SecurePass123!",
                            "",
                            "User",
                            "test@test.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userNameIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userNameIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - User Surname is Invalid`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser",
                            "SecurePass123!",
                            "SecurePass123!",
                            "Test",
                            "",
                            "test@test.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userSurnameIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userSurnameIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - User Email Already Used`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser",
                            "SecurePass123!",
                            "SecurePass123!",
                            "Test",
                            "User",
                            "1@mail.com",
                            "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userEmailAlreadyUsed.type)
                    response.shouldHaveStatus(UserProblem.userEmailAlreadyUsed.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Create User - User Role is Invalid`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USERS) {
                    headers.append("Authorization", "Bearer $adminToken")
                    setBody(
                        CreateUserRequest(
                            "newTestUser",
                            "SecurePass123!",
                            "SecurePass123!",
                            "Test",
                            "User",
                            "test@test.com",
                            "INVALID_ROLE",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userRoleIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userRoleIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update User - User Email is Invalid`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        PatchUserRequest(
                            name = "updatedUsername",
                            surname = "UpdatedFirstName",
                            email = "invalid-email",
                            role = "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userEmailIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userEmailIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update User - User Email Already Used`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        PatchUserRequest(
                            name = "updatedUsername",
                            surname = "UpdatedFirstName",
                            email = "1@mail.com",
                            role = "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userEmailAlreadyUsed.type)
                    response.shouldHaveStatus(UserProblem.userEmailAlreadyUsed.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update User - User Name is Invalid`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        PatchUserRequest(
                            name = "",
                            surname = "surname",
                            email = "updated@test.com",
                            role = "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userNameIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userNameIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update User - User Surname is Invalid`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        PatchUserRequest(
                            name = "updatedUsername",
                            surname = "",
                            email = "updated@test.com",
                            role = "SELLER",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userSurnameIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userSurnameIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Update User - User Role is Invalid`() =
        testApplication {
            testClient()
                .patch(Uris.API + Uris.USERS_BY_ID) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        PatchUserRequest(
                            name = "updatedUsername",
                            surname = "UpdatedFirstName",
                            email = "updated@test.com",
                            role = "INVALID_ROLE",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userRoleIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userRoleIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Change User Role - Role Not Found`() =
        testApplication {
            testClient()
                .put(Uris.API + Uris.USERS_ROLE) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(RoleRequest("INVALID_ROLE")) // Invalid role
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.userRoleIsInvalid.type)
                    response.shouldHaveStatus(UserProblem.userRoleIsInvalid.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Change Password - Insecure Password`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        ChangePasswordRequest(
                            "SecurePass123!",
                            "insecurepassword",
                            "insecurepassword",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.insecurePassword.type)
                    response.shouldHaveStatus(UserProblem.insecurePassword.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }

    @Test
    fun `Change Password - Password is Wrong`() =
        testApplication {
            testClient()
                .post(Uris.API + Uris.USER_CHANGE_PASSWORD) {
                    headers.append("Authorization", "Bearer $adminToken")
                    parameter("id", "1")
                    setBody(
                        ChangePasswordRequest(
                            "invalid",
                            "SecurePass123!",
                            "SecurePass123!",
                        ),
                    )
                }.also { response ->
                    response.body<ProblemJSON>().type.shouldBeEqual(UserProblem.wrongPassword.type)
                    response.shouldHaveStatus(UserProblem.wrongPassword.status)
                    response.shouldHaveContentType(ContentType.Application.ProblemJson)
                }
        }
}
