import {ThemedLayoutV2} from "@refinedev/mantine";

import {Sider} from "./Sider";
import {Footer} from "./Footer";
import {Outlet} from "react-router-dom";

export function MainLayout() {
    return (
        <ThemedLayoutV2 Sider={() => <Sider/>} Footer={Footer}>
            <Outlet/>
        </ThemedLayoutV2>
    )
}
