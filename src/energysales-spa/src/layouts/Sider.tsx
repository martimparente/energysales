import React, {useState} from 'react';
import {Code, Group} from '@mantine/core';
import {
    IconBrandAsana,
    IconBuilding,
    IconBulb,
    IconHome,
    IconKey,
    IconLogin2,
    IconLogout,
    IconSettings,
    IconSwitchHorizontal,
    IconUsersGroup,
} from '@tabler/icons-react';
import classes from './NavbarSimpleColored.module.css';
import {Link} from "react-router-dom";

const data = [
    {link: '/', label: 'Home', icon: IconHome},
    {link: '/login', label: 'Login', icon: IconLogin2},
    {link: '/forgotpassword', label: 'Forgot Password', icon: IconKey},
    {link: '/teams', label: 'Teams', icon: IconBrandAsana},
    {link: '/sellers', label: 'Sellers', icon: IconUsersGroup},
    {link: '/products', label: 'Products', icon: IconBulb},
    {link: '/clients', label: 'Clients', icon: IconBuilding},
    {link: '/settings', label: 'Settings', icon: IconSettings},
];

const energySalesIcon = <img src="/src/assets/logo+name.svg" width="150" height="50"/>

export function Sider() {
    const [active, setActive] = useState('');

    const links = data.map((item) => (
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
                        v0.0.1
                    </Code>
                </Group>
                {links}
            </div>

            <div className={classes.footer}>
                <a href="#" className={classes.link} onClick={(event) => event.preventDefault()}>
                    <IconSwitchHorizontal className={classes.linkIcon} stroke={1.5}/>
                    <span>Change account</span>
                </a>

                <a href="#" className={classes.link} onClick={(event) => event.preventDefault()}>
                    <IconLogout className={classes.linkIcon} stroke={1.5}/>
                    <span>Logout</span>
                </a>
            </div>
        </nav>
    );
}