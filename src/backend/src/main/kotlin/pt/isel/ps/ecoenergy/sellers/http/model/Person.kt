package pt.isel.ps.ecoenergy.sellers.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.ecoenergy.sellers.domain.model.Person

@Serializable
data class PersonJSON(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
) {
    companion object {
        fun fromPerson(person: Person) =
            PersonJSON(
                person.id,
                person.name,
                person.surname,
                person.email,
                person.role.toString(),
            )
    }
}
