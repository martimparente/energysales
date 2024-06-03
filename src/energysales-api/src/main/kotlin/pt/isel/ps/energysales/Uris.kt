package pt.isel.ps.energysales

object Uris {
    const val API = "/api"

    // General routes
    const val HOME = "/"
    const val ABOUT = "/about"
    const val STATUS = "/status"
    const val VERSION = "/version"

    // Auth routes
    const val AUTH = "/auth"
    const val AUTH_SIGNUP = "$AUTH/signup"
    const val AUTH_LOGIN = "$AUTH/login"
    const val AUTH_RESET_PASSWORD = "$AUTH/reset-password"

    // User routes
    const val USERS = "/users"
    const val USERS_ROLES = "$USERS/{id}/roles"
    const val USERS_ROLE = "$USERS/{id}/roles/{role-id}"
    const val USER_CHANGE_PASSWORD = "$USERS/{id}/change-password"

    // Admin routes
    const val ADMIN = "/admin"
    const val ADMIN_HOME = "$ADMIN/me"
    const val ADMIN_SETTINGS = "$ADMIN/settings"

    // Person routes
    const val PERSONS = "/persons"
    const val PERSONS_HOME = "$PERSONS/me"

    // Team routes
    const val TEAMS = "/teams"
    const val TEAMS_BY_ID = "$TEAMS/{id}"
    const val TEAMS_HOME = "$TEAMS/me"
    const val TEAMS_SELLERS = "$TEAMS/{id}/sellers"
    const val TEAMS_SELLER = "$TEAMS/{id}/sellers/{id}"
    const val TEAMS_MEMBER = "$TEAMS/members/{memberId}"
    const val TEAMS_PROJECTS = "$TEAMS/projects"

    // Seller routes
    const val SELLERS = "/sellers"
    const val SELLERS_BY_ID = "$SELLERS/{id}"
    const val SELLERS_HOME = "$SELLERS/me"

    // Product routes
    const val PRODUCT = "/products"
    const val PRODUCT_BY_ID = "$PRODUCT/{id}"

    // Client routes
    const val CLIENT = "/clients"
    const val CLIENT_BY_ID = "$CLIENT/{id}"
}