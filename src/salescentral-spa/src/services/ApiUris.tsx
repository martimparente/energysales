export namespace ApiUris {
         const API_BASE_URL = `http://localhost:8080/api`

         // Auth
         export const login = `${API_BASE_URL}/auth/login`
         export const register = `${API_BASE_URL}/auth/register`
         export const logout = `${API_BASE_URL}/auth/logout`


         // Teams
         export const getTeams = (lastKeySeen: string) => `${API_BASE_URL}/teams?lastKeySeen=${lastKeySeen}`
         export const getTeam = (teamId: string) => `${API_BASE_URL}/teams/${teamId}`
         export const createTeam = `${API_BASE_URL}/teams`
         export const updateTeam = (teamId: string) => `${API_BASE_URL}/teams/${teamId}`
         export const deleteTeam = (teamId: string) => `${API_BASE_URL}/teams/${teamId}`
}