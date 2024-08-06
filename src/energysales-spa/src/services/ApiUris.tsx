export namespace ApiUris {
    export const STATIC_RESOURCES_URL = `http://localhost:8080/`
    export const API_BASE_URL = `http://localhost:8080/api`

    // Auth
    export const login = `${API_BASE_URL}/auth/login`
    export const resetPassword = `${API_BASE_URL}/auth/reset-password`
    export const changePassword = `${API_BASE_URL}/auth/change-password`

    // Partners
    export const getPartners = (lastKeySeen: string) => `${API_BASE_URL}/partners?lastKeySeen=${lastKeySeen}`
    export const getPartner = (partnerId: string) => `${API_BASE_URL}/partners/${partnerId}`
    export const getPartnerDetails = (partnerId: string) => `${API_BASE_URL}/partners/${partnerId}?include=details`
    export const createPartner = `${API_BASE_URL}/partners`
    export const updatePartner = (partnerId: string) => `${API_BASE_URL}/partners/${partnerId}`
    export const deletePartner = (partnerId: string) => `${API_BASE_URL}/partners/${partnerId}`

    export const getAvailableSellers = (searchQuery: string) => `${API_BASE_URL}/sellers?&noTeam=true&searchQuery=${searchQuery}`
    export const addPartnerSeller = (partnerId: string) => `${API_BASE_URL}/partners/${partnerId}/sellers`
    export const deletePartnerSeller = (partnerId: string, sellerId: string) => `${API_BASE_URL}/partners/${partnerId}/sellers/${sellerId}`
    export const uploadAvatar = (partnerId: string) => `${API_BASE_URL}/partners/${partnerId}/avatar`

    // Partner Services
    export const addPartnerService = (partnerId: string) => `${API_BASE_URL}/partners/${partnerId}/services`
    export const deletePartnerService = (partnerId: string, serviceId: string) => `${API_BASE_URL}/partners/${partnerId}/services/${serviceId}`

    // Managers
    export const getManagerCandidates = () => `${API_BASE_URL}/users?role=MANAGER&available=true`

    // Users
    export const getUsers = (lastKeySeen: string) => `${API_BASE_URL}/users?lastKeySeen=${lastKeySeen}`
    export const getUser = (userId: string) => `${API_BASE_URL}/users/${userId}`
    export const createUser = `${API_BASE_URL}/users`
    export const updateUser = (userId: string) => `${API_BASE_URL}/users/${userId}`
    export const deleteUser = (userId: string) => `${API_BASE_URL}/users/${userId}`

    // Services
    export const getServices = (lastKeySeen: string) => `${API_BASE_URL}/services?lastKeySeen=${lastKeySeen}`
    export const getService = (serviceId: string) => `${API_BASE_URL}/services/${serviceId}`
    export const createService = `${API_BASE_URL}/services`
    export const updateService = (serviceId: string) => `${API_BASE_URL}/services/${serviceId}`
    export const deleteService = (serviceId: string) => `${API_BASE_URL}/services/${serviceId}`

    // Clients
    export const getClients = (lastKeySeen: string) => `${API_BASE_URL}/clients?lastKeySeen=${lastKeySeen}`
    export const getClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`
    export const createClient = `${API_BASE_URL}/clients`
    export const updateClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`
    export const deleteClient = (clientId: string) => `${API_BASE_URL}/clients/${clientId}`
    export const makeOffer = (clientId: string) => `${API_BASE_URL}/clients/${clientId}/offers`
    export const sendOfferEmail = (clientId: string) => `${API_BASE_URL}/clients/${clientId}/offers/email`
}
