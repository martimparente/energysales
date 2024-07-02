package pt.isel.ps.energysales.services.http

import arrow.core.Either.Left
import arrow.core.Either.Right
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import pt.isel.ps.energysales.Uris
import pt.isel.ps.energysales.auth.http.model.Problem
import pt.isel.ps.energysales.auth.http.model.respondProblem
import pt.isel.ps.energysales.services.application.ServiceCreationError
import pt.isel.ps.energysales.services.application.ServiceDeletingError
import pt.isel.ps.energysales.services.application.ServiceService
import pt.isel.ps.energysales.services.application.ServiceUpdatingError
import pt.isel.ps.energysales.services.application.dto.CreateServiceInput
import pt.isel.ps.energysales.services.application.dto.UpdateServiceInput
import pt.isel.ps.energysales.services.http.model.CreateServiceRequest
import pt.isel.ps.energysales.services.http.model.ServiceJSON
import pt.isel.ps.energysales.services.http.model.UpdateServiceRequest

@Resource(Uris.SERVICES)
class ServiceResource(
    val lastKeySeen: Int? = null,
) {
    @Resource("{id}")
    class Id(
        val parent: ServiceResource = ServiceResource(),
        val id: Int,
    )
}

fun Route.serviceRoutes(serviceService: ServiceService) {
    get<ServiceResource> { queryParams ->
        val services = serviceService.getAllServicesPaging(10, queryParams.lastKeySeen)
        val servicesResponse = services.map { service -> ServiceJSON.fromService(service) }
        call.respond(servicesResponse)
    }

    post<ServiceResource> {
        val body = call.receive<CreateServiceRequest>()
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
                call.response.status(HttpStatusCode.Created)
                call.response.header("Location", "${Uris.SERVICES}/${res.value}")
            }

            is Left ->
                when (res.value) {
                    ServiceCreationError.ServiceAlreadyExists ->
                        call.respondProblem(
                            Problem.serviceEmailAlreadyInUse,
                            HttpStatusCode.Conflict,
                        )

                    ServiceCreationError.ServiceInfoIsInvalid ->
                        call.respondProblem(
                            Problem.serviceInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )

                    ServiceCreationError.ServiceEmailIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    ServiceCreationError.ServiceNameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                    ServiceCreationError.ServiceSurnameIsInvalid -> call.respondProblem(Problem.todo, HttpStatusCode.Continue)
                }
        }
    }

    get<ServiceResource.Id> { pathParams ->
        val service =
            serviceService.getById(pathParams.id)
                ?: return@get call.respondProblem(Problem.serviceNotFound, HttpStatusCode.NotFound)
        val serviceJson = ServiceJSON.fromService(service)
        call.response.status(HttpStatusCode.OK)
        call.respond(serviceJson)
    }

    put<ServiceResource.Id> { pathParams ->
        val body = call.receive<UpdateServiceRequest>()
        val input =
            UpdateServiceInput(
                pathParams.id,
                body.name,
                body.description,
                body.cycleName,
                body.cycleType,
                body.periodName,
                body.periodNumPeriods,
            )

        val res = serviceService.updateService(input)

        when (res) {
            is Right -> {
                call.response.status(HttpStatusCode.OK)
            }

            is Left -> {
                when (res.value) {
                    ServiceUpdatingError.ServiceNotFound -> call.respondProblem(Problem.serviceNotFound, HttpStatusCode.NotFound)
                    ServiceUpdatingError.ServiceInfoIsInvalid ->
                        call.respondProblem(
                            Problem.serviceInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )

                    ServiceUpdatingError.ServiceEmailIsInvalid -> TODO()
                    ServiceUpdatingError.ServiceNameIsInvalid -> TODO()
                    ServiceUpdatingError.ServiceSurnameIsInvalid -> TODO()
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
                    ServiceDeletingError.ServiceNotFound -> call.respondProblem(Problem.serviceNotFound, HttpStatusCode.NotFound)
                    ServiceDeletingError.ServiceInfoIsInvalid ->
                        call.respondProblem(
                            Problem.serviceInfoIsInvalid,
                            HttpStatusCode.BadRequest,
                        )
                }
        }
    }
}
