package pt.isel.ps.ecoenergy

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
    const val AUTH_CHANGE_PASSWORD = "$AUTH/change-password"

    // Admin routes
    const val ADMIN = "/admin"
    const val ADMIN_HOME = "$ADMIN/me"
    const val ADMIN_SETTINGS = "$ADMIN/settings"

    // Person routes
    const val PERSONS = "/persons"
    const val PERSONS_HOME = "$PERSONS/me"

    // Team routes
    const val TEAMS = "/teams"
    const val TEAMS_HOME = "$TEAMS/me"
    const val TEAMS_MEMBERS = "$TEAMS/members"
    const val TEAMS_MEMBER = "$TEAMS/members/{memberId}"
    const val TEAMS_PROJECTS = "$TEAMS/projects"

}
