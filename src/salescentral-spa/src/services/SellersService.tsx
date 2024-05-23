import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Seller } from "../interfaces/Sellers";
import { ApiUris } from "./ApiUris"; // Adjust the import path as necessary

const AUTHORIZATION_HEADER = {
  "Content-Type": "application/json",
  Authorization: "Bearer " + localStorage.getItem("token"),
}

//CREATE hook (post new seller to api)
export function useCreateSeller() {
  return useMutation({
    mutationFn: async (seller: Seller) => {
      const sellerPayload = {
        name: seller.name,
        manager: seller.manager,
        location: { district: seller.district },
      };
      return fetch(ApiUris.createSeller, {
        method: "POST",
        headers: AUTHORIZATION_HEADER,
        body: JSON.stringify(sellerPayload),
      });
    },
  });
}

//READ hook (get sellers from api)
export function useGetSellers(lastKeySeen: string = "0") {
  return useQuery<Seller[]>({
    queryKey: ["sellers", lastKeySeen],
    queryFn: () =>
      fetch(ApiUris.getSellers(lastKeySeen), {
        headers: AUTHORIZATION_HEADER,
      }).then((res) => res.json()),
  });
}

export function useGetSeller(id: string) {
  return useQuery<Seller>({
    queryKey: [`seller-${id}`],
    queryFn: () =>
      fetch(ApiUris.getSeller(id), {
        headers: AUTHORIZATION_HEADER,
      }).then((res) => res.json()),
  })
}


//UPDATE hook (put seller in api)
export function useUpdateSeller() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (newSellerInfo: Seller) =>
      fetch(ApiUris.updateSeller(newSellerInfo.id), {
        method: "PUT",
        headers: AUTHORIZATION_HEADER,
        body: JSON.stringify(newSellerInfo),
      }),
    onMutate: (newSellerInfo: Seller) => {
      queryClient.setQueryData(["sellers"], (prevSellers: any) =>
        prevSellers?.map((prevSeller: Seller) =>
          prevSeller.id === newSellerInfo.id ? newSellerInfo : prevSeller
        )
      );
    },
  });
}

//DELETE hook (delete seller in api)
export function useDeleteSeller() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (sellerId: string) =>
      fetch(ApiUris.deleteSeller(sellerId), {
        method: "DELETE",
        headers: AUTHORIZATION_HEADER,
      }),
  });
}