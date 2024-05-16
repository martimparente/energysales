/* import * as React from 'react'
import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from "../contexts/AuthContext"
import Button from 'react-bootstrap/Button'
import Form from 'react-bootstrap/Form'
import { Row } from "react-bootstrap"
import Container from "react-bootstrap/Container"

export function Signup() {
    const { signup } = useAuth()
    const [inputs, setInputs] = useState({ username: "", password: "", passwordRepeat: "" })
    const [error, setError] = useState(undefined)
    const [loading, setLoading] = useState(false)
    const [redirect, setRedirect] = useState(false)
    const location = useLocation()
    const navigate = useNavigate()

    if (redirect) {
        return <Navigate to={location.state?.source?.pathname || "/login"} replace={true} state={{ user: inputs.username }} />
    }

    function handleChange(ev: React.ChangeEvent<HTMLInputElement>) {
        const name = ev.target.name
        setInputs({ ...inputs, [name]: ev.target.value })
        setError(undefined)
    }

    async function handleSubmit(ev: React.FormEvent<HTMLFormElement>) {
        ev.preventDefault()

        if (inputs.password !== inputs.passwordRepeat) {
            return setError('Passwords do not match')
        }

        if (!inputs.password.match(/^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$/)) {
            return setError(
                'Passwords should have at least 8 characters and have one of each following types:\n' +
                '- Uppercase letters [A-Z] ' +
                '- Lowercase letters [a-z] ' +
                '- Numbers [0-9] ' +
                '-Symbols [~`!@#\$%^&*()_-+={[}]|\\:;\"\'<,>.?/]')
        }

        try {
            setLoading(true)
            await signup(inputs.username, inputs.password, inputs.passwordRepeat)
            setRedirect(true)
        } catch (err) {
            console.log(err.message)
            setError(err.message)
        }
        setLoading(false)
    }

    return (
        <div className="Auth-form-container">
            <Form onSubmit={handleSubmit} className="Auth-form">
                <div className="Auth-form-content">
                    <h2>Sign Up</h2>
                    <fieldset disabled={loading}>
                        <Form.Group className="mb-3" controlId="formBasicEmail">
                            <Form.Label>Username</Form.Label>
                            <Form.Control type="text" name="username" placeholder="Enter username"
                                value={inputs.username}
                                onChange={handleChange} required />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="formBasicPassword">
                            <Form.Label>Password</Form.Label>
                            <Form.Control type="password" name="password" placeholder="Password" value={inputs.password}
                                onChange={handleChange} required />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="formBasicPasswordRepeat">
                            <Form.Label>Confirm Password</Form.Label>
                            <Form.Control type="password" name="passwordRepeat" placeholder="Confirm Password"
                                value={inputs.passwordRepeat} onChange={handleChange} required />
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="formButtons">
                            <Container>
                                <Row><Button variant="dark" onClick={() => navigate('/login')}>Log In</Button></Row>
                                <Row><Button variant="dark" type="submit">Sign Up</Button></Row>
                                <Row><Form.Text className="text-muted">{error}</Form.Text></Row>
                            </Container>
                        </Form.Group>
                    </fieldset>
                </div>
            </Form>
        </div>
    )
}
 */