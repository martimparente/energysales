export interface CreateTeamInputModel {
         name: string,
         manager: string,
         location: string 
};

export interface CreateTeamOutputModel {
         name: string,
         manager: string,
         location: string 
};

export interface UpdateTeamInputModel {
         id: string,
         name: string,
         manager: string,
         location: string 
};
