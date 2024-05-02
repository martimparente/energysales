package pt.isel.ps.ecoenergy.sellers.domain.model

import pt.isel.ps.ecoenergy.sellers.data.Role

data class Person(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val role: Role? = null,
) {
    companion object {
        fun create(id: Int) = Person(id, "", "", "")
    }
}
