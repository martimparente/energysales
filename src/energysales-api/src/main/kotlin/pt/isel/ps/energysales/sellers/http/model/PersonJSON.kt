package pt.isel.ps.energysales.sellers.http.model

import kotlinx.serialization.Serializable
import pt.isel.ps.energysales.sellers.domain.model.Person

@Serializable
data class PersonJSON(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
) {
    companion object {
        fun fromPerson(person: Person) =
            PersonJSON(
                person.id.toString(),
                person.name,
                person.surname,
                person.email,
                person.role.toString(),
            )
    }
}