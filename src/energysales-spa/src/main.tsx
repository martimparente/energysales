import ReactDOM from 'react-dom/client'
import {router} from './router/Router.tsx'
import {RouterProvider} from 'react-router-dom'

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement)

root.render(
    // <StrictMode>
    <RouterProvider router={router}/>
    // </StrictMode>
)
