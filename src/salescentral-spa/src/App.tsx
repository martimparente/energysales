import '@mantine/core/styles.css';
import { MantineProvider } from '@mantine/core';
import { Notifications } from "@mantine/notifications";
import { Router } from './Router';
import { theme } from './theme';

import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query'

const queryClient = new QueryClient()

"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJyZWFsbSIsImlzcyI6ImF1ZGllbmNlIiwidWlkIjoyLCJleHAiOjE3MTU1MjkxMDB9.6x0AMrKkVkrLbJ3RJv00mMbzWp02CTZO43CxOpaTFMY"

export default function App() {
  return (
    <MantineProvider theme={theme}>
      <QueryClientProvider client={queryClient}>
        <Notifications />
        <Router />
      </QueryClientProvider>
    </MantineProvider>
  );
}
