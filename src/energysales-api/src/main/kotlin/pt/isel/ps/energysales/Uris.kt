package pt.isel.ps.energysales

object Uris {
    const val API = "/api"

    // General routes
    const val HOME = "/"
    const val ABOUT = "/about"
    const val STATUS = "/status"
    const val VERSION = "/version"
    const val AVATAR = "/avatar"

    // Auth routes
    const val AUTH = "/auth"
    const val AUTH_LOGIN = "$AUTH/login"
    const val AUTH_RESET_PASSWORD = "$AUTH/reset-password"
    const val AUTH_CHANGE_PASSWORD = "$AUTH/change-password"

    // User routes
    const val USERS = "/users"
    const val USERS_BY_ID = "$USERS/{id}"
    const val USERS_ROLE = "$USERS/{id}/role"
    const val USER_CHANGE_PASSWORD = "$USERS/{id}/change-password"

    // Admin routes
    const val ADMIN = "/admin"
    const val ADMIN_HOME = "$ADMIN/me"
    const val ADMIN_SETTINGS = "$ADMIN/settings"

    // Partner routes
    const val PARTNERS = "/partners"
    const val PARTNERS_BY_ID = "$PARTNERS/{partnerId}"
    const val PARTNERS_HOME = "$PARTNERS/me"
    const val PARTNERS_SELLERS = "$PARTNERS/{partnerId}/sellers"
    const val PARTNERS_SELLER = "$PARTNERS/{partnerId}/sellers/{sellerId}"
    const val PARTNERS_SERVICES = "$PARTNERS/{partnerId}/services"
    const val PARTNERS_SERVICE = "$PARTNERS/{partnerId}/services/{serviceId}"
    const val PARTNERS_CLIENTS = "$PARTNERS/{partnerId}/clients"
    const val PARTNERS_CLIENT = "$PARTNERS/{partnerId}/clients/{clientId}"

    // Seller routes
    const val SELLERS = "/sellers"
    const val SELLERS_BY_ID = "$SELLERS/{id}"
    const val SELLERS_HOME = "$SELLERS/me"

    // Product routes
    const val SERVICES = "/services"
    const val SERVICES_BY_ID = "$SERVICES/{id}"

    // Client routes
    const val CLIENTS = "/clients"
    const val CLIENTS_BY_ID = "$CLIENTS/{id}"
    const val CLIENTS_OFFERS = "$CLIENTS/{id}/offers"
    const val CLIENTS_OFFERS_SEND_EMAIL = "$CLIENTS/{id}/offers/email"

    // Offer routes
    const val OFFERS = "/offers"
    const val OFFERS_BY_ID = "$OFFERS/{id}"
}
