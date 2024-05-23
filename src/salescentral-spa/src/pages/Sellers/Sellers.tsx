import { GenericTable } from '../../components/GenericTable';
import { Seller } from '../../interfaces/Sellers';
import { useSellers } from './useSellers';

export function SellersPage() {
  const {
    columns,
    data,
    createSeller,
    handleCreateSellerForm,
    updateSeller,
    handleUpdateSellerForm,
    deleteSeller,
    isFetching,
    error } = useSellers();

  return (
    <GenericTable<Seller>
      columns={columns}
      data={data}
      createResource={createSeller}
      handleCreateResourceForm={handleCreateSellerForm}
      updateResource={updateSeller}
      handleUpdateResourceForm={handleUpdateSellerForm}
      deleteResource={deleteSeller}
      isFetching={isFetching}
      error={error}
    />
  );
}