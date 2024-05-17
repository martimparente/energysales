import '@mantine/core/styles.css';
import { MantineProvider } from '@mantine/core';
import { ModalsProvider } from '@mantine/modals';
import { Notifications } from "@mantine/notifications";
import { Router } from './Router';
import { theme } from './theme';
import { Refine} from "@refinedev/core";
import { authProvider } from "./providers/AuthProvider";
import {QueryClient,QueryClientProvider,} from '@tanstack/react-query'

const queryClient = new QueryClient()

export default function App() {
  return (
    <MantineProvider theme={theme}>
      <ModalsProvider>
      <Refine authProvider={authProvider}>
        <QueryClientProvider client={queryClient}>
          <Notifications />
          <Router />
        </QueryClientProvider>
        </Refine>
      </ModalsProvider>
    </MantineProvider>
  );
}
