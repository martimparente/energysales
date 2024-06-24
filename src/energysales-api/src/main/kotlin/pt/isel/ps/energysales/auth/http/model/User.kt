package pt.isel.ps.energysales.auth.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.auth.domain.model.Manager

@Serializable
data class ManagerJSON(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
) {
    companion object {
        fun fromManager(manager: Manager) =
            ManagerJSON(
                id = manager.id,
                name = manager.name,
                surname = manager.surname,
                email = manager.email,
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
