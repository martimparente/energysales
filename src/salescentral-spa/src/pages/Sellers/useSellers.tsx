import { useGetSellers, useCreateSeller, useUpdateSeller, useDeleteSeller } from '../../services/SellersService';
import { useNavigate } from "react-router-dom"
import { CreateSellerInputModel, UpdateSellerInputModel } from "../../services/models/SellerModel";

export function useSellers() {

  const navigate = useNavigate();

  const { data, error, isFetching, isLoading } = useGetSellers();
  const { mutateAsync: createSeller } = useCreateSeller();
  const { mutateAsync: updateSeller } = useUpdateSeller();
  const { mutateAsync: deleteSeller } = useDeleteSeller();

  const columns = [
    {
      accessor: 'name',
      header: 'Name',
      sortable: true,
    },
    {
      accessor: 'manager',
      header: 'Manager',
      sortable: true,
    },
    {
      accessor: 'location.district',
      header: 'District',
      sortable: true,
    },
  ];

  return {
    columns,
    data,
    isLoading,
    createSeller: async (input: CreateSellerInputModel) => await createSeller(input),
    handleCreateSellerForm: () => { },
    updateSeller: async (input: UpdateSellerInputModel) => await updateSeller(input),
    handleUpdateSellerForm: () => { },
    deleteSeller: async (id: string) => await deleteSeller(id),
    isFetching,
    error,
  }
}