import { useQuery, } from '@tanstack/react-query'
import { useState, useEffect } from 'react';
import { Pagination, Table, Text } from '@mantine/core';
import { usePagination } from '@mantine/hooks';
import MyTable from '../components/MyTable'
import { Button } from '@mantine/core';
import Example from '../components/PaginationTable';
import { showNotification, notifications } from "@mantine/notifications";
import { useNotification } from "@refinedev/core";
import MainLayout from '../layouts/MainLayout'


export function HomePage() {
    console.log("HomePage   started");

    return (
        <div>
            <h1>Home</h1>
        </div>
    )
}
