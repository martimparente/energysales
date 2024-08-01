import {ReactSearchAutocomplete} from 'react-search-autocomplete'
import {Box} from '@mantine/core'
import React, {ReactElement} from 'react'
import {Service} from '../../services/models/ServiceModel.tsx'

interface AddTeamServiceModalProps {
    availableServices: Service[]
    handleOnServiceSearch: (query: string) => void
    handleOnServiceSelect: (item: Service) => void
    formatResult: (item: Service) => ReactElement
}

export const AddTeamServiceModal: React.FC<AddTeamServiceModalProps> = ({
                                                                            availableServices,
                                                                            handleOnServiceSearch,
                                                                            handleOnServiceSelect,
                                                                            formatResult
                                                                        }) => {
    return (
        <Box h={500} size={100}>
            <ReactSearchAutocomplete<Service>
                items={availableServices}
                onSearch={handleOnServiceSearch}
                onSelect={handleOnServiceSelect}
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
