package pt.isel.ps.ecoenergy.teams.domain.model

data class Person(
    val uid: Int,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
) {
    companion object {
        fun create(uid: Int): Person = Person(uid, "", "", "", "")
    }
}
