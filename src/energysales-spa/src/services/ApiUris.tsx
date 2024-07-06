export namespace ApiUris {
    const API_BASE_URL = `http://localhost:8080/api`;

    // Auth
    export const login = `${API_BASE_URL}/auth/login`;
    export const register = `${API_BASE_URL}/auth/register`;
    export const logout = `${API_BASE_URL}/auth/logout`;

    // Teams
    export const getTeams = (lastKeySeen: string) => `${API_BASE_URL}/teams?lastKeySeen=${lastKeySeen}`;
    export const getTeam = (teamId: string) => `${API_BASE_URL}/teams/${teamId}`;
    export const getTeamDetails = (teamId: string) => `${API_BASE_URL}/teams/${teamId}?include=members`;
    export const createTeam = `${API_BASE_URL}/teams`;
    export const updateTeam = (teamId: string) => `${API_BASE_URL}/teams/${teamId}`;
    export const deleteTeam = (teamId: string) => `${API_BASE_URL}/teams/${teamId}`;

    export const addTeamUser = (teamId: string) => `${API_BASE_URL}/teams/${teamId}/users`;
    export const deleteTeamUser = (teamId: string, userId: string) => `${API_BASE_URL}/teams/${teamId}/users/${userId}`;

    // Managers
    export const getManagerCandidates = () => `${API_BASE_URL}/users?role=MANAGER&available=true`;

    // Users
    export const getUsers = (lastKeySeen: string) => `${API_BASE_URL}/users?lastKeySeen=${lastKeySeen}`;
    export const getUsersWithNoTeam = (lastKeySeen: string) => `${API_BASE_URL}/users?lastKeySeen=${lastKeySeen}&noTeam=true`;
    export const getUser = (userId: string) => `${API_BASE_URL}/users/${userId}`;
    export const createUser = `${API_BASE_URL}/users`;
    export const updateUser = (userId: string) => `${API_BASE_URL}/users/${userId}`;
    export const deleteUser = (userId: string) => `${API_BASE_URL}/users/${userId}`;

    // Services
    export const getServices = (lastKeySeen: string) => `${API_BASE_URL}/services?lastKeySeen=${lastKeySeen}`;
    export const getService = (serviceId: string) => `${API_BASE_URL}/services/${serviceId}`;
    export const createService = `${API_BASE_URL}/services`;
    export const updateService = (serviceId: string) => `${API_BASE_URL}/services/${serviceId}`;
    export const deleteService = (serviceId: string) => `${API_BASE_URL}/services/${serviceId}`;

    // Clients
    export const getClients = (lastKeySeen: string) => `${API_BASE_URL}/clients?lastKeySeen=${lastKeySeen}`;
    export const getClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`;
    export const createClient = `${API_BASE_URL}/clients`;
    export const updateClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`;
    export const deleteClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`;
}
