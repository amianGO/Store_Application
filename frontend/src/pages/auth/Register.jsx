import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axiosInstance from "../../config/axios";

import { Box, Button, TextField, Typography, Container, Alert, CircularProgress } from "@mui/material";

export default function Register(){
    const [nombre, setNombre] = useState('')
    const [apellido, setApellido] = useState('')
    const [documento, setDocumento] = useState('')
    const [usuario, setUsuario] = useState('')
    const [password, setPassword] = useState('')
    const [telefono, setTelefono] = useState('')
    const [email, setEmail] = useState('')
    const [cargo, setCargo] = useState('')
    const [rol, setRol] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const navigate = useNavigate()

    const handleSubmit = async (e) => {
        e.preventDefault()
        setError('')
        setLoading(true)

        try {
            const response = await axiosInstance.post('/auth/register', {nombre, apellido, documento, usuario, password, telefono, email, cargo, rol})

            navigate('/login')
        } catch (err){
            setError(err.response?.data?.message || 'Faltan datos importantes')
        } finally {
            setLoading(false)
        }
    }

    return(
        <Container>
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center'
                }}
            >
                <Typography component="h1" variant="h5" sx={{mb: 2}} >
                    Registrarse
                </Typography>

                {error && <Alert severity="error" >{error}</Alert>}

                <Box component="form" onSubmit={handleSubmit} sx={{mt: 1}} >
                    <TextField 
                        margin="normal"
                        required
                        fullWidth
                        id="nombre"
                        label="Nombre"
                        name="nombre"
                        value={nombre}
                        onChange={(e) => setNombre(e.target.value)}
                    />
                    <TextField 
                        margin="normal"
                        required
                        fullWidth
                        id="apellido"
                        label="Apellido"
                        name="apellido"
                        value={apellido}
                        onChange={(e) => setApellido(e.target.value)}
                    />

                    <TextField 
                        margin="normal"
                        required
                        fullWidth
                        id="documento"
                        label="Documento"
                        name="documento"
                        value={documento}
                        onChange={(e) => setDocumento(e.target.value)}
                    />

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
                        label="Password"
                        name="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <TextField 
                        margin="normal"
                        required
                        fullWidth
                        id="telefono"
                        label="Telefono"
                        name="telefono"
                        value={telefono}
                        onChange={(e) => setTelefono(e.target.value)}
                    />

                    <TextField 
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="Email"
                        name="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />

                    <TextField 
                        margin="normal"
                        required
                        fullWidth
                        id="cargo"
                        label="Cargo"
                        name="cargo"
                        value={cargo}
                        onChange={(e) => setCargo(e.target.value)}
                    />

                    <TextField 
                        margin="normal"
                        required
                        fullWidth
                        id="rol"
                        label="Rol"
                        name="rol"
                        value={rol}
                        onChange={(e) => setRol(e.target.value)}
                    />

                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mx: 3, mb: 2 }}
                        disabled={loading}
                    >
                        {loading ? <CircularProgress size={24} color="inherit" />: 'Registrar'}
                    </Button>

                    <Typography variant="body2">
                        ¿Tienes cuenta? <Link to="/login">Inicia Sesion</Link>
                    </Typography>

                </Box>
            </Box>
        </Container>
    )
}