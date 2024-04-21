package pt.isel.ps.ecoenergy.team.data

import pt.isel.ps.ecoenergy.team.domain.model.Team

interface TeamRepository {

    suspend fun create(team: Team): Int
    suspend fun getAll(): List<Team>
    suspend fun getAllKeyPaging(pageSize: Int, lastKeySeen: Int? = null): List<Team>
    suspend fun getByName(name: String): Team?
    suspend fun getById(id: Int): Team?
    suspend fun teamExists(id: Int): Boolean
    suspend fun teamExistsByName(name: String): Boolean
    suspend fun update(team: Team): Team?
    suspend fun delete(team: Team): Boolean
}
