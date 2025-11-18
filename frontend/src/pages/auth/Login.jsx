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

import {Lock, User} from 'lucide-react'

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
            const { token, empleadoId, usuario: user, rol, nombre, apellido, cargo } = response.data

            localStorage.setItem('token', token)
            localStorage.setItem('empleadoId', empleadoId)
            localStorage.setItem('usuario', user)
            localStorage.setItem('rol', rol)
            localStorage.setItem('nombre', nombre)
            localStorage.setItem('apellido', apellido)
            localStorage.setItem('cargo', cargo)
            
            navigate('/dashboard')
        } catch (err){
            setError(err.response?.data?.message || 'Credenciales Invalidas')
        } finally {
            setLoading(false)
        }
    }

    return (
        <Box
            sx={{
                minHeight: '100vh',
                width: '100vw',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
                padding: 2,
                position: 'fixed',
                top: 0,
                left: 0,
                overflow: 'hidden',
                '&::before':{
                    content: '""',
                    position: 'absolute',
                    top: '-50%',
                    right: '-50%',
                    width: '100%',
                    height: '100%',
                    background: 'radial-gradient(circle, rgba(147, 112, 219, 0.15) 0%, transparent 70%)',
                    animation: 'pulse 8s ease-in-out infinite'
                },
                '@keyframes pulse':{
                    '0%, 100%': { transform: 'scale(1)', opacity: 0.5 },
                    '50%': { transform: 'scale(1.1)', opacity: 0.3 },
                },
            }}
        >
            <Container maxWidth="sm">
                <Box 
                    sx={{
                        background: 'rgba(255, 255, 255, 0.05)',
                        backdropFilter: 'blur(20px)',
                        borderRadius: '24px',
                        border: '1px solid rgba(255, 255, 255, 0.1)',
                        boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
                        padding: {xs: 3, sm: 5},
                        position: 'relative',
                        zIndex: 1,
                        transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                        '&:hover':{
                            transform: 'translateY(-5px)',
                            boxShadow: '0 12px 48px rgba(147, 112, 219, 0.2)'
                        },
                    }}
                >
                    {/*Logo o Icono*/}
                    <Box
                        sx={{
                            display: 'flex',
                            justifyContent: 'center',
                            mb: 3
                        }}
                    >
                        <Box
                            sx={{
                                width: 80,
                                height: 80,
                                borderRadius: '50%',
                                background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                boxShadow: '0 8px 24px rgba(147, 112, 219, 0.4)',
                                animation: 'float 3s ease-in-out infinite',
                                '@keyframes float': {
                                    '0%, 100%': { transform: 'translateY(0px)' },
                                    '50%': { transform: 'translateY(-10px)' },
                                }
                            }}
                        >
                            <Lock size={40} color="#fff"/>
                        </Box>
                    </Box>

                    <Typography
                        component="h1"
                        variant="h4"
                        sx={{
                            mb: 1,
                            textAlign: 'center',
                            fontWeight: 700,
                            background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                            WebkitBackgroundClip: 'text',
                            WebkitTextFillColor: 'transparent',
                            backgroundClip: 'text'
                        }}
                    >
                        Bienvenido
                    </Typography>

                    <Typography
                        variant="body1"
                        sx={{
                            mb: 4,
                            textAlign: 'center',
                            color: 'rgba(255, 255, 255, 0.6)'
                        }}
                    >
                        Inicia Sesion para Continuar
                    </Typography>
                    {error && (
                        <Alert
                            severity="error"
                            sx={{
                                mb: 3,
                                background: 'rgba(244, 67, 54, 0.1)',
                                backdropFilter: 'blur(10px)',
                                border: '1px solid rgba(244, 67, 54, 0.3)',
                                color: '#ffcdd2'
                            }}
                        >
                            {error}
                        </Alert>
                    )}
                    <Box component="form" onSubmit={handleSubmit}>
                        <Box sx={{position: 'relative', mb: 2}}>
                            <User
                                size={20}
                                style={{
                                    position: 'absolute',
                                    left: 16,
                                    top: 30,
                                    color: 'rgba(255, 255, 255, 0.5)',
                                    zIndex: 1,
                                }}
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
                                sx={{
                                    '& .MuiOutlinedInput-root': {
                                    paddingLeft: '48px',
                                    background: 'rgba(255, 255, 255, 0.05)',
                                    borderRadius: '12px',
                                    transition: 'all 0.3s ease',
                                    '& fieldset': {
                                    borderColor: 'rgba(255, 255, 255, 0.1)',
                                    },
                                    '&:hover fieldset': {
                                    borderColor: 'rgba(147, 112, 219, 0.5)',
                                    },
                                    '&.Mui-focused fieldset': {
                                    borderColor: '#9370db',
                                    borderWidth: '2px',
                                    },
                                    '&.Mui-focused': {
                                    background: 'rgba(255, 255, 255, 0.08)',
                                    },
                                    },
                                    '& .MuiInputLabel-root': {
                                        color: 'rgba(255, 255, 255, 0.6)',
                                        marginLeft: '40px',
                                    },
                                    '& .MuiInputLabel-root.Mui-focused': {
                                        color: '#dda0dd',
                                        marginLeft: 0,
                                    },
                                    '& .MuiOutlinedInput-input': {
                                        color: '#fff',
                                    },
                                }}
                            />
                        </Box>

                        <Box sx={{position: 'relative', mb: 3}}>
                            <Lock 
                                size={20}
                                style={{
                                    position: 'absolute',
                                    left: 16,
                                    top: 30,
                                    color: 'rgba(255, 255, 255, 0.5)',
                                    zIndex: 1,
                                }}
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
                                sx={{
                                    '& .MuiOutlinedInput-root': {
                                    paddingLeft: '48px',
                                    background: 'rgba(255, 255, 255, 0.05)',
                                    borderRadius: '12px',
                                    transition: 'all 0.3s ease',
                                    '& fieldset': {
                                    borderColor: 'rgba(255, 255, 255, 0.1)',
                                    },
                                    '&:hover fieldset': {
                                    borderColor: 'rgba(147, 112, 219, 0.5)',
                                    },
                                    '&.Mui-focused fieldset': {
                                    borderColor: '#9370db',
                                    borderWidth: '2px',
                                    },
                                    '&.Mui-focused': {
                                    background: 'rgba(255, 255, 255, 0.08)',
                                    },
                                    },
                                    '& .MuiInputLabel-root': {
                                        color: 'rgba(255, 255, 255, 0.6)',
                                        marginLeft: '40px',
                                    },
                                    '& .MuiInputLabel-root.Mui-focused': {
                                        color: '#dda0dd',
                                        marginLeft: 0,
                                    },
                                    '& .MuiOutlinedInput-input': {
                                        color: '#fff',
                                    },
                                }}
                            />
                        </Box>

                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            disabled={loading}
                            sx={{
                                mt: 2,
                                mb: 3,
                                py: 1.5,
                                borderRadius: '12px',
                                background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
                                boxShadow: '0 4px 20px rgba(147, 112, 219, 0.4)',
                                fontSize: '1rem',
                                fontWeight: 600,
                                textTransform: 'none',
                                transition: 'all 0.3s ease',
                                '&:hover':{
                                    background: 'linear-gradient(135deg, #a280e0 0%, #7b6bd4 100%)',
                                    boxShadow: '0 6px 28px rgba(147, 112, 219, 0.6)',
                                    transform: 'translateY(-2px)'
                                },
                                '&:disabled':{
                                    background: 'rgba(147, 112, 219, 0.3)'
                                },
                            }}
                        >
                            {loading ? (
                                <CircularProgress size={24} sx={{ color: '#fff'}}/>
                            ): (
                                'Ingresar'
                            )}
                        </Button>

                        <Box sx={{ textAlign: 'center'}}>
                            <Typography
                                variant="body2"
                                sx={{
                                    color: 'rgba(255, 255, 255, 0.6)',
                                    '& a': {
                                        color: '#dda0dd',
                                        textDecoration: 'none',
                                        fontWeight: 600,
                                        transition: 'color 0.3s ease',
                                        '&:hover': {
                                            color: '#9370db',
                                            textDecoration: 'underline'
                                        }
                                    }
                                }}
                            >
                                ¿No tienes Cuenta? <Link to="/register">Registrate</Link>
                            </Typography>
                        </Box>
                    </Box>
                </Box>
            </Container>
        </Box>

    )
}