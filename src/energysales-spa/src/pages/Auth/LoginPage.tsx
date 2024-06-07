import {Anchor, Button, Checkbox, Paper, PasswordInput, Text, TextInput, Title,} from '@mantine/core';
import classes from './AuthenticationImage.module.css';
import {useForm} from '@mantine/form';

import {Link} from "react-router-dom";
import {useAuth} from "../../providers/AuthContext.tsx";


export function LoginPage() {
    const form = useForm({
        initialValues: {
            username: '',
            password: '',
            terms: true,
        },

        validate: {
            username: (val) => ((val.length >= 5 && val.length <= 16) ? null : 'Invalid username'),
            password: (val) => (val.length <= 6 ? 'Password should include at least 6 characters' : null),
        },
    });

    const auth = useAuth()

    return (
        <div className={classes.wrapper}>
            <Paper className={classes.form} radius={0} p={30}>
                <Title order={2} className={classes.title} ta="center" mt="md" mb={50}>
                    Welcome back to EnergySales!
                </Title>


                <form onSubmit={form.onSubmit((values) => auth.login({
                    username: values.username,
                    password: values.password,
                }))}>
                    <TextInput
                        required
                        label="Username"
                        placeholder="Your Username"
                        value={form.values.username}
                        onChange={(event) => form.setFieldValue('username', event.currentTarget.value)}
                        error={form.errors.username && 'Invalid username'}
                        radius="md"
                    />
                    <PasswordInput
                        required
                        label="Password"
                        placeholder="Your password"
                        mt="md" size="md"
                        value={form.values.password}
                        onChange={(event) => form.setFieldValue('password', event.currentTarget.value)}
                        error={form.errors.password && 'Password should include at least 6 characters'}
                        radius="md"
                    />
                    <Checkbox
                        label="Keep me logged in"
                        mt="xl" size="md"
                        checked={form.values.terms}
                        color="orange"
                        onChange={(event) => form.setFieldValue('terms', event.currentTarget.checked)}
                    />
                    <Button type="submit" fullWidth mt="xl" size="md" color="orange">Login</Button>

                    <Text ta="center" mt="md">
                        <Link to="/forgot-password" replace={true}>Forgot password?</Link>
                        <Anchor<'a'> href="#" fw={700} onClick={(event) => event.preventDefault()}> </Anchor>
                    </Text>
                </form>
            </Paper>
        </div>
    );
}