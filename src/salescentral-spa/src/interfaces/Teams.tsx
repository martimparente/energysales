export interface Team {
         id: string;
         name: string;
         manager: string;
         location: Location;
}

interface Location {
         district: string;
}