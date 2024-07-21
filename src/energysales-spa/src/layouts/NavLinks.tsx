import {IconBrandAsana, IconBuilding, IconBulb, IconHome, IconSettings, IconUsersGroup} from "@tabler/icons-react";
import {Link} from 'react-router-dom';
import {UserProfile} from "../services/models/UserModel.tsx";

/**
 ⚠️ - Make sure the roles are the same as the protected routes in the Router
 Otherwise the user will see the links but won't be able to access the pages
 */
const navLinks = [
    {link: '/', label: 'Home', icon: IconHome, roles: ['ADMIN', 'MANAGER']},
    {link: '/teams', label: 'Teams', icon: IconBrandAsana, roles: ['ADMIN']},
    {link: '/users', label: 'Users', icon: IconUsersGroup, roles: ['ADMIN']},
    {link: '/services', label: 'Services', icon: IconBulb, roles: ['ADMIN']},
    {link: '/clients', label: 'Clients', icon: IconBuilding, roles: ['SELLER']},
    {link: '/settings', label: 'Settings', icon: IconSettings, roles: ['ADMIN', 'SELLER']},
];

// TODO HIGHLIGHT THE SELECTED LINK
const generateLinks = (userRole: string) => {
    return navLinks
        .filter(item => item.roles.includes(userRole))
        .map((item) => (
            <div key={item.label}>
                <Link data-active={item.label} to={item.link}>
                    <item.icon stroke={1.5}/>
                    <span>{item.label}</span>
                </Link>
            </div>

        ));
};

export function NavLinks({user}: { user: UserProfile | null }) {
    return (
        <div>
            {generateLinks(user ? user.role : "NOUSER")}
        </div>
    );
}