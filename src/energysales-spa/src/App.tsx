import '@mantine/core/styles.css'
import {MantineProvider} from '@mantine/core'
import {ModalsProvider} from '@mantine/modals'
import {QueryClient, QueryClientProvider} from '@tanstack/react-query'
import {theme} from './theme'
import {MainLayout} from './layouts/MainLayout.tsx'
import {AuthProvider} from './context/useAuth.tsx'
import {ToastContainer} from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'

export default function App() {
    return (
        <AuthProvider>
            <MantineProvider theme={theme}>
                <QueryClientProvider client={new QueryClient()}>
                    <ModalsProvider>
                        <MainLayout/>
                        <ToastContainer/>
                    </ModalsProvider>
                </QueryClientProvider>
            </MantineProvider>
        </AuthProvider>
    )
}
