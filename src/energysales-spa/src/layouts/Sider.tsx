import {useState} from 'react';
import {Code, Group} from '@mantine/core';
import {IconLogout,} from '@tabler/icons-react';
import classes from './NavbarSimpleColored.module.css';
import {Link} from "react-router-dom";
import logoName from '../assets/logo+name.svg';
import {sidebarLinks} from "../router/Router.tsx";
import {useAuth} from "../context/useAuth.tsx";


const energySalesIcon = <img src={logoName} width="150" height="50" alt="Logo"/>

export function Sider() {
    const [active, setActive] = useState('');
    const {logout} = useAuth();

    const links = sidebarLinks.filter(item => item.roles.includes("admin")).map((item) => (
        <Link
            className={classes.link}
            data-active={item.label === active || undefined}
            to={item.link}
            key={item.label}
            onClick={() => setActive(item.label)}
        >
            <item.icon className={classes.linkIcon} stroke={1.5}/>
            <span>{item.label}</span>
        </Link>
    ));

    return (
        <nav className={classes.navbar}>
            <div className={classes.navbarMain}>
                <Group className={classes.header} justify="space-between">
                    {energySalesIcon}
                    <Code fw={700} className={classes.version}>
                        v0.1.0
                    </Code>
                </Group>
                {links}
            </div>

            <div className={classes.footer}>
                <a href="#" className={classes.link} onClick={(e) => {
                    e.preventDefault()
                    logout()
                }}>
                    <IconLogout className={classes.linkIcon} stroke={1.5}/>
                    <span>Logout</span>
                </a>
            </div>
        </nav>
    );
}