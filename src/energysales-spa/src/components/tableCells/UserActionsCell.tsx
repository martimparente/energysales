import styles from './ActionsCellRenderer.module.css'
import {Button} from '@mantine/core'
import {IconTrash} from '@tabler/icons-react'
import {CustomCellRendererProps} from 'ag-grid-react'

interface UserActionsCellRenderer extends CustomCellRendererProps {
    onDeleteButtonClick?: (rowData: any) => Promise<Response>
}

interface ClientActionsCellRenderer extends CustomCellRendererProps {
    onMakeOfferButtonClick?: (rowData: any) => void
    onDeleteButtonClick?: (rowData: any) => Promise<Response>
}

export const UserActionsCellRenderer = (params: UserActionsCellRenderer) => {
    return (
        <div className={styles.buttonCell}>
            <Button
                onClick={() => {
                    console.log(params.data)
                    if (params.onDeleteButtonClick) {
                        params.onDeleteButtonClick(params.data)
                    }
                }}
                c='red'
            >
                <IconTrash/>
            </Button>
        </div>
    )
}

export const ClientActionsCellRenderer = (params: ClientActionsCellRenderer) => {
    return (
        <div className={styles.buttonCell}>
            <Button
                onClick={() => {
                    if (params.onDeleteButtonClick) {
                        params.onDeleteButtonClick(params.data)
                    }
                }}
                color='red'
            >
                <IconTrash/>
            </Button>
            <Button
                onClick={() => {
                    if (params.onMakeOfferButtonClick) {
                        params.onMakeOfferButtonClick(params.data)
                    }
                }}
                color='blue'
            >
                Offer
            </Button>
        </div>
    )
}
