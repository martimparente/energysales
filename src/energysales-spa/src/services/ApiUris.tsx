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

    export const addTeamSeller = (teamId: string) => `${API_BASE_URL}/teams/${teamId}/sellers`;
    export const deleteTeamSeller = (teamId: string, sellerId: string) => `${API_BASE_URL}/teams/${teamId}/sellers/${sellerId}`;

    // Managers
    export const getManagerCandidates = () => `${API_BASE_URL}/users?role=MANAGER&available=true`;

    // Sellers
    export const getSellers = (lastKeySeen: string) => `${API_BASE_URL}/sellers?lastKeySeen=${lastKeySeen}`;
    export const getSellersWithNoTeam = (lastKeySeen: string) => `${API_BASE_URL}/sellers?lastKeySeen=${lastKeySeen}&noTeam=true`;
    export const getSeller = (sellerId: string) => `${API_BASE_URL}/sellers/${sellerId}`;
    export const createSeller = `${API_BASE_URL}/sellers`;
    export const updateSeller = (sellerId: string) => `${API_BASE_URL}/sellers/${sellerId}`;
    export const deleteSeller = (sellerId: string) => `${API_BASE_URL}/sellers/${sellerId}`;

    // Products
    export const getProducts = (lastKeySeen: string) => `${API_BASE_URL}/products?lastKeySeen=${lastKeySeen}`;
    export const getProduct = (productId: string) => `${API_BASE_URL}/products/${productId}`;
    export const createProduct = `${API_BASE_URL}/products`;
    export const updateProduct = (productId: string) => `${API_BASE_URL}/products/${productId}`;
    export const deleteProduct = (productId: string) => `${API_BASE_URL}/products/${productId}`;

    // Clients
    export const getClients = (lastKeySeen: string) => `${API_BASE_URL}/clients?lastKeySeen=${lastKeySeen}`;
    export const getClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`;
    export const createClient = `${API_BASE_URL}/clients`;
    export const updateClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`;
    export const deleteClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`;
}
