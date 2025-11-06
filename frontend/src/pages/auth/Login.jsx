import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axiosInstance from "../../config/axios";

import {
    Box,
    Button,
    TextField,
    Typography,
    Container,
    Alert,
    CircularProgress
} from '@mui/material';

export default function Login(){
    const [usuario, setUsuario] = useState('')
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const navigate = useNavigate()


    const handleSubmit = async (e) => {
        e.preventDefault()
        setError('')
        setLoading(true)
    
        try {
            const response = await axiosInstance.post('/auth/login', {usuario, password})
            const token = response.data.token

            localStorage.setItem('token', token)
            navigate('/dashboard')
        } catch (err){
            setError(err.response?.data?.message || 'Credenciales Invalidas')
        } finally {
            setLoading(false)
        }
    }

    return (
        <Container maxWidth>
            <Box
                sx={{
                    marginTop: 2,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center'
                }}
            >
                <Typography component = "h1" variant = "h5" sx = {{ mb: 2}}>
                    Iniciar Sesion
                </Typography>
                
                {error && <Alert severity="error">{error}</Alert>}

                <Box component="form" onSubmit={handleSubmit} sx={{mt: 1}}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="usuario"
                        label="Usuario"
                        name="usuario"
                        value={usuario}
                        onChange={(e) => setUsuario(e.target.value)}
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="password"
                        label="Contraseña"
                        name="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2}}
                        disabled={loading}
                    >
                        {loading ? <CircularProgress size={24} color="inherit"/> : 'Ingresar'}
                    </Button>

                    <Typography variant="body2">
                        ¿No tienes cuenta? <Link to="/register">Registrate</Link>
                    </Typography>
                </Box>
            </Box>
        </Container>
    )
}