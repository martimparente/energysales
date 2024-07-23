import {useState} from 'react';
import {Box, Button, Center, Container, Group, Paper, rem, Text, TextInput, Title} from '@mantine/core';
import {useForm} from '@mantine/form';
import {IconArrowLeft} from '@tabler/icons-react';
import {Link, useNavigate} from "react-router-dom";
import {changePasswordAPI} from "../../services/AuthService.tsx";

export function ChangePasswordPage() {
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const form = useForm({
        initialValues: {
            password: '',
            confirmPassword: '',
        },
        validate: {
            password: (val) => (val.length >= 6 ? null : 'Password must be at least 6 characters long'),
            confirmPassword: (val, values) => (val === values.password ? null : 'Passwords do not match'),
        },
    });

    const handleSubmit = async () => {
        const {password} = form.values;

        setLoading(true);
        try {
            await changePasswordAPI(password);
            navigate('/login');
        } catch (error) {
            // The toast for errors is already handled in the service
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container size={460} my={30}>
            <Title ta="center">
                Change your password
            </Title>
            <Text c="dimmed" fz="sm" ta="center">
                Enter your new password below
            </Text>

            <Paper withBorder shadow="md" p={30} radius="md" mt="xl">
                <TextInput
                    required
                    label="New Password"
                    type="password"
                    placeholder="Enter new password"
                    value={form.values.password}
                    onChange={(event) => form.setFieldValue('password', event.currentTarget.value)}
                    error={form.errors.password && 'Password must be at least 6 characters long'}
                    radius="md"
                />
                <TextInput
                    required
                    label="Confirm Password"
                    type="password"
                    placeholder="Confirm new password"
                    value={form.values.confirmPassword}
                    onChange={(event) => form.setFieldValue('confirmPassword', event.currentTarget.value)}
                    error={form.errors.confirmPassword && 'Passwords do not match'}
                    radius="md"
                    mt="md"
                />
                <Group justify="space-between" mt="lg">

                    <Link to="/login">
                        <Center inline>
                            <IconArrowLeft style={{width: rem(12), height: rem(12)}} stroke={1.5}/>
                            <Box ml={5}>Back to the login page</Box>
                        </Center>
                    </Link>

                    <Button
                        color="orange"
                        onClick={form.onSubmit(handleSubmit)}
                        loading={loading}
                    >
                        Change password
                    </Button>
                </Group>
            </Paper>
        </Container>
    );
}