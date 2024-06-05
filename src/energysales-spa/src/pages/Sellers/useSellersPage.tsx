import {useCreateSeller, useDeleteSeller, useGetSellers, useUpdateSeller} from '../../services/SellersService';
import {useNavigate} from "react-router-dom"
import {CreateSellerInputModel, Seller, UpdateSellerInputModel} from "../../services/models/SellersModel.tsx";
import {Column} from '../../components/GenericTable';
import {useState} from "react";

export function useSellersPage() {

    const navigate = useNavigate();

    const {data: sellers, error: fetchError, isFetching} = useGetSellers();
    const {mutateAsync: createSeller} = useCreateSeller();
    const {mutateAsync: updateSeller} = useUpdateSeller("");
    const {mutateAsync: deleteSeller} = useDeleteSeller();

    const [error, setError] = useState<string | null>(null)

    const columns: Column[] = [
        {
            accessor: 'person.name',
            header: 'Name',
            sortable: true,
        },
        {
            accessor: 'person.surname',
            header: 'Surname',
            sortable: true,
        },
        {
            accessor: 'person.email',
            header: 'E-Mail',
            sortable: true,
        },
        {
            accessor: 'totalSales',
            header: 'Total Sales',
            sortable: true,
        },
        {
            accessor: 'team',
            header: 'Team',
            sortable: true,
        },
    ];

    if (fetchError && !error) {
        setError(fetchError.message);
    }

    return {
        columns,
        sellers,
        createSeller: async (input: CreateSellerInputModel) => await createSeller(input).catch(e => setError(e.message)),
        updateSeller: async (input: UpdateSellerInputModel) => await updateSeller(input).catch(e => setError(e)),
        deleteSeller: async (seller: Seller) => await deleteSeller(seller.person.id).catch(e => setError(e)),
        onShowClickHandler: (seller: Seller) => navigate(`/sellers/${seller.person.id}`),
        isFetching,
        error,
    }
}