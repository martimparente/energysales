import '@mantine/core/styles.css';
import {MantineProvider} from '@mantine/core';
import {ModalsProvider} from '@mantine/modals';
import {Router} from './Router';
import {theme} from './theme';
import {Refine} from "@refinedev/core";
import {authProvider} from "./providers/AuthProvider";
import {QueryClient, QueryClientProvider,} from '@tanstack/react-query'
import {Notifications} from '@mantine/notifications';

const queryClient = new QueryClient();

export default function App() {
    return (
        <MantineProvider theme={theme}>
            <ModalsProvider>
                <QueryClientProvider client={queryClient}>
                    <Refine authProvider={authProvider}>
                        <Notifications/>
                        <Router/>
                    </Refine>
                </QueryClientProvider>
            </ModalsProvider>
        </MantineProvider>
    );
}
