import {useState} from 'react';
import {Code, Group} from '@mantine/core';
import {
    Icon2fa,
    IconBellRinging,
    IconDatabaseImport,
    IconFingerprint,
    IconKey,
    IconLogout,
    IconReceipt2,
    IconSettings,
    IconSwitchHorizontal,
} from '@tabler/icons-react';
import classes from './NavbarSimpleColored.module.css';
import {Link} from "react-router-dom";

const data = [
    {link: '/', label: 'Home', icon: IconBellRinging},
    {link: '/login', label: 'Login', icon: IconReceipt2},
    {link: '/forgotpassword', label: 'Forgot Password', icon: IconFingerprint},
    {link: '/teams', label: 'Teams', icon: IconKey},
    {link: '/sellers', label: 'Sellers', icon: IconDatabaseImport},
    {link: '/products', label: 'Products', icon: Icon2fa},
    {link: '/clients', label: 'Clients', icon: IconSettings},
];

export function Sider() {
    const [active, setActive] = useState('Billing');

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
                    <IconKey size={28} style={{color: 'white'}}/>
                    <Code fw={700} className={classes.version}>
                        v3.1.2
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