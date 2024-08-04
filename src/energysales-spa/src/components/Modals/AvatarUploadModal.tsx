import {Button, FileInput, rem, Text} from '@mantine/core'
import React from 'react'
import {IconPhotoUp} from '@tabler/icons-react'

interface AvatarUploadModalProps {
    currAvatarUrl: string | null
    onFileAdded: (file: File | null) => void
    onAvatarDelete: () => void
}

export const AvatarUploadModal: React.FC<AvatarUploadModalProps> = ({currAvatarUrl, onFileAdded, onAvatarDelete}) => {
    return (
        <div>
            <Text size='sm' mb='md'>
                Choose an image for the Avatar. Click 'Confirm' to save the changes.
            </Text>
            <FileInput
                leftSection={<IconPhotoUp style={{width: rem(18), height: rem(18)}} stroke={1.5}/>}
                placeholder='Select file'
                label='Upload image'
                accept='image/*'
                onChange={onFileAdded}
            />

            {currAvatarUrl !== null && (
                <div>
                    <Button color='red' mt='md' onClick={onAvatarDelete}>
                        Delete current Avatar
                    </Button>
                </div>
            )}
        </div>
    )
}
