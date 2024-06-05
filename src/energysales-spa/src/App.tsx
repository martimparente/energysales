import '@mantine/core/styles.css';
import {MantineProvider} from '@mantine/core';
import {ModalsProvider} from '@mantine/modals';
import {RouterProvider} from "react-router-dom";
import {QueryClient, QueryClientProvider} from '@tanstack/react-query'
import {router} from './router/Router.tsx';
import {theme} from './theme';
import {Refine} from "@refinedev/core";


const queryClient = new QueryClient();

export default function App() {
    return (
        <MantineProvider theme={theme}>
            <ModalsProvider>
                <Refine>
                    <QueryClientProvider client={queryClient}>
                        <RouterProvider router={router}/>;
                    </QueryClientProvider>
                </Refine>
            </ModalsProvider>
        </MantineProvider>
    );
}
