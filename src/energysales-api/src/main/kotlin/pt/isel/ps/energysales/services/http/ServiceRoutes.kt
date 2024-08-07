package pt.isel.ps.energysales.services.http

import CreateServiceRequest
import PatchServiceRequest
import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.plugins.respondProblem
import pt.isel.ps.energysales.services.application.ServiceServiceKtor
import pt.isel.ps.energysales.services.application.dto.CreateServiceError
import pt.isel.ps.energysales.services.application.dto.CreateServiceInput
import pt.isel.ps.energysales.services.application.dto.DeleteServiceError
import pt.isel.ps.energysales.services.application.dto.UpdateServiceError
import pt.isel.ps.energysales.services.application.dto.UpdateServiceInput
import pt.isel.ps.energysales.services.http.model.PriceJSON
import pt.isel.ps.energysales.services.http.model.ServiceJSON
import pt.isel.ps.energysales.services.http.model.ServiceProblem

@Resource(Uris.SERVICES)
class ServiceResource(
    val lastKeySeen: String? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: ServiceResource = ServiceResource(),
        val id: String,
    )
}

fun Route.serviceRoutes(serviceService: ServiceServiceKtor) {
    get<ServiceResource> { queryParams ->
        val services = serviceService.getAllServicesPaging(10, queryParams.lastKeySeen)
        val servicesResponse = services.map { service -> ServiceJSON.fromService(service) }
        call.respond(servicesResponse)
    }

    post<ServiceResource> {
        val body =
            CreateServiceRequest(
                "newService1",
                "newDescription1",
                "newCycleName1",
                "newCycleType1",
                "newPeriodName1",
                1,
                PriceJSON(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f),
            )
        val input =
            CreateServiceInput(
                body.name,
                body.description,
                body.cycleName,
                body.cycleType,
                body.periodName,
                body.periodNumPeriods,
                body.price,
            )
        val res = serviceService.createService(input)

        when (res) {
            is Right -> {
                call.response.header("Location", "${Uris.SERVICES}/${res.value}")
                return@post call.response.status(HttpStatusCode.Created)
            }

            is Left -> {
                when (res.value) {
                    CreateServiceError.ServiceAlreadyExists -> call.respondProblem(ServiceProblem.serviceEmailAlreadyInUse)
                    CreateServiceError.ServiceInfoIsInvalid -> call.respondProblem(ServiceProblem.serviceInfoIsInvalid)
                    CreateServiceError.ServiceEmailIsInvalid -> call.respondProblem(ServiceProblem.todo)
                    CreateServiceError.ServiceNameIsInvalid -> call.respondProblem(ServiceProblem.todo)
                    CreateServiceError.ServiceSurnameIsInvalid -> call.respondProblem(ServiceProblem.todo)
                }
            }
        }
    }

    get<ServiceResource.Id> { pathParams ->
        val service =
            serviceService.getById(pathParams.id)
                ?: return@get call.respondProblem(ServiceProblem.serviceNotFound)
        val serviceJson = ServiceJSON.fromService(service)
        call.response.status(HttpStatusCode.OK)
        call.respond(serviceJson)
    }

    patch<ServiceResource.Id> { pathParams ->
        val body = call.receive<PatchServiceRequest>()
        val input =
            UpdateServiceInput(
                pathParams.id,
                body.name,
                body.description,
                body.cycleName,
                body.cycleType,
                body.periodName,
                body.periodNumPeriods,
                body.price,
            )
        val res = serviceService.updateService(input)

        when (res) {
            is Right -> call.respond(ServiceJSON.fromService(res.value))

            is Left -> {
                when (res.value) {
                    UpdateServiceError.ServiceNotFound -> call.respondProblem(ServiceProblem.serviceNotFound)
                    UpdateServiceError.ServiceInfoIsInvalid -> call.respondProblem(ServiceProblem.serviceInfoIsInvalid)
                    UpdateServiceError.ServiceEmailIsInvalid -> call.respondProblem(ServiceProblem.serviceEmailIsInvalid)
                    UpdateServiceError.ServiceNameIsInvalid -> call.respondProblem(ServiceProblem.serviceNameIsInvalid)
                    UpdateServiceError.ServiceSurnameIsInvalid -> call.respondProblem(ServiceProblem.serviceSurnameIsInvalid)
                }
            }
        }
    }

    delete<ServiceResource.Id> { pathParams ->
        val res = serviceService.deleteService(pathParams.id)

        when (res) {
            is Right -> call.respond(HttpStatusCode.OK)
            is Left ->
                when (res.value) {
                    DeleteServiceError.ServiceNotFound -> call.respondProblem(ServiceProblem.serviceNotFound)
                    DeleteServiceError.ServiceInfoIsInvalid -> call.respondProblem(ServiceProblem.serviceInfoIsInvalid)
                }
        }
    }
}
