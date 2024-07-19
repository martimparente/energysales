import '@mantine/core/styles.css';
import {MantineProvider} from '@mantine/core';
import {ModalsProvider} from '@mantine/modals';
import {Outlet} from "react-router-dom";
import {QueryClient, QueryClientProvider} from '@tanstack/react-query'
import {theme} from './theme';
import {Refine} from "@refinedev/core";
import {MainLayout} from "./layouts/MainLayout.tsx";
import {AuthProvider} from "./context/useAuth.tsx";


const queryClient = new QueryClient();

export default function App() {
    return (
        <AuthProvider>
            <MantineProvider theme={theme}>
                <ModalsProvider>
                    <Refine>
                        <QueryClientProvider client={queryClient}>
                            <MainLayout>
                                <Outlet/>
                            </MainLayout>
                        </QueryClientProvider>
                    </Refine>
                </ModalsProvider>
            </MantineProvider>
        </AuthProvider>
    );
}
