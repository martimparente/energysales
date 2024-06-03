import {ThemedLayoutV2} from "@refinedev/mantine";

import {Sider} from "./Sider";
import {Outlet} from "react-router-dom";

export function MainLayout() {
    return (
        <ThemedLayoutV2 Sider={() => <Sider/>}>
            <Outlet/>
        </ThemedLayoutV2>
    )
}
