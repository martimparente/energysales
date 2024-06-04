import {Box, Button, Center, Container, Group, Paper, rem, Text, TextInput, Title,} from '@mantine/core';
import {useForm} from '@mantine/form';
import {IconArrowLeft} from '@tabler/icons-react';
import classes from './ForgotPassword.module.css';
import {Link} from "react-router-dom";

export function ForgotPassword() {
    const form = useForm({
        initialValues: {
            email: '',
        },
        validate: {
            email: (val) => (/^\S+@\S+$/.test(val) ? null : 'Invalid email'),
        },
    });


    return (
        <Container size={460} my={30}>
            <Title className={classes.title} ta="center">
                Forgot your password?
            </Title>
            <Text c="dimmed" fz="sm" ta="center">
                Enter your email to get a reset link
            </Text>

            <Paper withBorder shadow="md" p={30} radius="md" mt="xl">
                <TextInput
                    required
                    label="Email"
                    placeholder="hello@energysales.pt"
                    value={form.values.email}
                    onChange={(event) => form.setFieldValue('email', event.currentTarget.value)}
                    error={form.errors.email && 'Invalid email'}
                    radius="md"
                />
                <Group justify="space-between" mt="lg" className={classes.controls}>
                    <Link to="/login">
                        <Center inline>
                            <IconArrowLeft style={{width: rem(12), height: rem(12)}} stroke={1.5}/>
                            <Box ml={5}>Back to the login page</Box>
                        </Center>
                    </Link>
                    <Button color="orange" className={classes.control}>Reset password</Button>
                </Group>
            </Paper>
        </Container>
    );
}