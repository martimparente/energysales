import { Table, TableData } from '@mantine/core';

interface Props {
    caption: string;
    head: string[];
    body: any[][];
}

export default function MyTable({ caption, head, body }: Props) {
    const tableData: TableData = {
        caption: caption,
        head: head,
        body: body,
    };

    return <Table data={tableData} />;
}