import styles from "./ActionsCellRenderer.module.css";
import {Button} from "@mantine/core";
import {IconTrash} from "@tabler/icons-react";
import {User} from "../makeData.ts";
import {CustomCellRendererProps} from "ag-grid-react";

interface UserActionsCellRenderer extends CustomCellRendererProps {
    onDeleteUserButtonClick?: (user: User) => Promise<Response>;
}

export const UserActionsCellRenderer = (params: UserActionsCellRenderer) => {
    return <div className={styles.buttonCell}>
        <Button
            onClick={() => {
                if (params.onDeleteUserButtonClick) {
                    params.onDeleteUserButtonClick(params.data);
                }
            }}
            c="red"
        >
            <IconTrash/>
        </Button>
    </div>
};