package pt.isel.ps.energysales.services.application

import pt.isel.ps.energysales.services.application.dto.CreateServiceInput
import pt.isel.ps.energysales.services.application.dto.CreateServiceResult
import pt.isel.ps.energysales.services.application.dto.DeleteServiceResult
import pt.isel.ps.energysales.services.application.dto.UpdateServiceInput
import pt.isel.ps.energysales.services.application.dto.UpdateServiceResult
import pt.isel.ps.energysales.services.domain.Service

interface ServiceService {
    suspend fun createService(input: CreateServiceInput): CreateServiceResult

    suspend fun getAllServices(): List<Service>

    suspend fun getAllServicesPaging(
        pageSize: Int,
        lastKeySeen: String?,
    ): List<Service>

    suspend fun getById(id: String): Service?

    suspend fun updateService(input: UpdateServiceInput): UpdateServiceResult

    suspend fun deleteService(id: String): DeleteServiceResult
}
