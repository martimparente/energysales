import React from "react";
import {useLink, useRouterContext, useRouterType} from "@refinedev/core";
import {Center, Text} from "@mantine/core";
import {RefineLayoutThemedTitleProps} from "@refinedev/ui-types"
import logo from '../assets/logo.svg';

const defaultText = ""

export const Title: React.FC<RefineLayoutThemedTitleProps> = (
    {
        collapsed,
        icon = logo,
        text = defaultText,
        wrapperStyles = {},
    }) => {
    // const theme = useMantineTheme();
    const routerType = useRouterType();
    const Link = useLink();
    const {Link: LegacyLink} = useRouterContext();

    const ActiveLink = routerType === "legacy" ? LegacyLink : Link;

    return (
        <ActiveLink to="/" style={{all: "unset"}}>
            <Center
                style={{
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: collapsed ? "center" : "flex-start",
                    gap: "8px",
                    ...wrapperStyles,
                }}
            >
                <Text
                    lh={0}
                    fz="inherit"
                    // color={theme. === "dark" ? "brand.5" : "brand.6"}
                >
                    {icon}
                </Text>
                {!collapsed && (
                    <Text
                        fz="inherit"
                        // c={theme.colorScheme === "dark" ? "white" : "black"}
                    >
                        {text}
                    </Text>
                )}
            </Center>
        </ActiveLink>
    );
};
