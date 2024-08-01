import {Seller} from '../../services/models/UserModel.tsx'
import {ReactSearchAutocomplete} from 'react-search-autocomplete'
import {Box} from '@mantine/core'
import React, {ReactElement} from 'react'

interface AddTeamSellerModalProps {
    availableSellers: Seller[]
    handleOnSellerSearch: (query: string) => void
    handleOnSellerSelect: (item: Seller) => void
    formatResult: (item: Seller) => ReactElement
}

export const AddTeamSellerModal: React.FC<AddTeamSellerModalProps> = ({
                                                                          availableSellers,
                                                                          handleOnSellerSearch,
                                                                          handleOnSellerSelect,
                                                                          formatResult
                                                                      }) => {
    return (
        <Box h={350} size={100}>
            <ReactSearchAutocomplete<Seller>
                items={availableSellers}
                onSearch={handleOnSellerSearch}
                onSelect={handleOnSellerSelect}
                formatResult={formatResult}
                maxResults={5}
                placeholder='Search Seller'
                styling={{
                    zIndex: 1,
                    backgroundColor: 'white' // TODO: Handle dark mode
                }}
            />
        </Box>
    )
}
