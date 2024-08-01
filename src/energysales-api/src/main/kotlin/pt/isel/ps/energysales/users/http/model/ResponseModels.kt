import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.users.domain.User
import java.util.Locale

@Serializable
data class UserJSON(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
) {
    companion object {
        fun fromUser(user: User) =
            UserJSON(
                id = user.id.toString(),
                name = user.name,
                surname = user.surname,
                email = user.email,
                role =
                    user.role.name[0] +
                        user
                            .role
                            .name
                            .substring(1)
                            .lowercase(Locale.getDefault()),
            )
    }
}

@Serializable
data class ManagerCreationJSON(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
)
