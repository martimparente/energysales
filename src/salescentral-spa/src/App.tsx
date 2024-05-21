import '@mantine/core/styles.css';
import { MantineProvider } from '@mantine/core';
import { ModalsProvider } from '@mantine/modals';
import { Router } from './Router';
import { theme } from './theme';
import { Refine } from "@refinedev/core";
import { authProvider } from "./providers/AuthProvider";
import { QueryClient, QueryClientProvider, } from '@tanstack/react-query'
import { Notifications } from '@mantine/notifications';
const queryClient = new QueryClient();

export default function App() {
  return (
    <MantineProvider theme={theme}>
      <ModalsProvider>
        <Refine authProvider={authProvider}>
          <QueryClientProvider client={queryClient}>
            <Router />
            <Notifications />
          </QueryClientProvider>
        </Refine>
      </ModalsProvider>
    </MantineProvider>
  );
}
