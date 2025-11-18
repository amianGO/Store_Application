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
    CircularProgress,
    MenuItem,
    Grid,
    InputAdornment 
} from "@mui/material";
import {UserPlus, User, Lock, Mail, Phone, Briefcase, Shield} from 'lucide-react'

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
    const roles = ['CAJERO', 'VENDEDOR', 'INVENTARIO']

    const textFieldStyles = {
        '& .MuiOutlinedInput-root': {
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
        },
        '& .MuiInputLabel-root.Mui-focused': {
            color: '#dda0dd',
        },
        '& .MuiOutlinedInput-input': {
            color: '#fff',
        },
        '& .MuiSelect-select': {
            color: '#fff',
        },
        '& .MuiSvgIcon-root': {
            color: 'rgba(255, 255, 255, 0.5)',
        },
    };

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
        <Box
            sx={{
                minHeight: '100vh',
                width: '100vw',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
                padding: 3,
                position: 'relative',
                overflowY: 'auto',
                '&::before':{
                    content: '""',
                    position: 'fixed',
                    top: '-50%',
                    right: '-50%',
                    width: '100%',
                    height: '100%',
                    background: 'radial-gradient(circle, rgba(147, 112, 219, 0.15) 0%, transparent 70%)',
                    animation: 'pulse 8s ease-in-out infinite',
                    pointerEvents: 'none'
                },
                '@keyframes pulse':{
                    '0%, 100%': { transform: 'scale(1)', opacity: 0.5 },
                    '50%': { transform: 'scale(1.1)', opacity: 0.3 },
                },
            }}
        >
            <Container maxWidth="sm" sx={{ py: 4 }}>
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
                    {/* Logo/Icono */}
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
                            <UserPlus size={40} color="#fff"/>
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
                        Crear Cuenta
                    </Typography>

                    <Typography
                        variant="body1"
                        sx={{
                            mb: 4,
                            textAlign: 'center',
                            color: 'rgba(255, 255, 255, 0.6)'
                        }}
                    >
                        Completa el formulario para registrarte
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
                        <Grid container spacing={2}> 
                            {/* Nombre */}
                            <Grid item xs={12} sm={6}>
                                <TextField 
                                    margin="normal"
                                    required
                                    fullWidth
                                    id="nombre"
                                    label="Nombre"
                                    name="nombre"
                                    value={nombre}
                                    onChange={(e) => setNombre(e.target.value)}
                                    InputProps={{
                                        startAdornment: (
                                            <InputAdornment position="start">
                                                <User size={20} color="rgba(255,255,255,0.5)" />
                                            </InputAdornment>
                                        ),
                                    }}
                                    sx={{
                                        '& .MuiOutlinedInput-root': {
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
                                        },
                                        '& .MuiInputLabel-root.Mui-focused': {
                                            color: '#dda0dd',
                                        },
                                        '& .MuiOutlinedInput-input': {
                                            color: '#fff',
                                        },
                                    }}
                                />
                            </Grid> 

                                {/* Apellido */}
                                <Grid item xs={12} sm={6}>
                                    <TextField 
                                        margin="normal"
                                        required
                                        fullWidth
                                        id="apellido"
                                        label="Apellido"
                                        name="apellido"
                                        value={apellido}
                                        onChange={(e) => setApellido(e.target.value)}
                                        InputProps={{
                                            startAdornment: (
                                                <InputAdornment position="start">
                                                    <User size={20} color="rgba(255,255,255,0.5)" />
                                                </InputAdornment>
                                            ),
                                        }}
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
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
                                            },
                                            '& .MuiInputLabel-root.Mui-focused': {
                                                color: '#dda0dd',
                                            },
                                            '& .MuiOutlinedInput-input': {
                                                color: '#fff',
                                            },
                                        }}
                                    />
                                </Grid>

                                {/* Documento */}
                                <Grid item xs={12} sm={6}>
                                    <TextField 
                                        margin="normal"
                                        required
                                        fullWidth
                                        id="documento"
                                        label="Documento"
                                        name="documento"
                                        value={documento}
                                        onChange={(e) => setDocumento(e.target.value)}
                                        // ... (Estilos del TextField)
                                        sx={{
                                            '& .MuiOutlinedInput-root': {
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
                                            },
                                            '& .MuiInputLabel-root.Mui-focused': {
                                                color: '#dda0dd',
                                            },
                                            '& .MuiOutlinedInput-input': {
                                                color: '#fff',
                                            },
                                        }}
                                    />
                                </Grid>

                                {/* Teléfono */}
                                <Grid item xs={12} sm={6}>
                                    <Box sx={{position: 'relative'}}>
                                        <Phone
                                            size={20}
                                            style={{
                                                position: 'absolute',
                                                left: 16,
                                                top: 30, // Ajuste de posición si es necesario
                                                color: 'rgba(255, 255, 255, 0.5)',
                                                zIndex: 1,
                                            }}
                                        />
                                        <TextField 
                                            margin="normal"
                                            required
                                            fullWidth
                                            id="telefono"
                                            label="Teléfono"
                                            name="telefono"
                                            value={telefono}
                                            onChange={(e) => setTelefono(e.target.value)}
                                            // ... (Estilos del TextField)
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
                                </Grid>

                                {/* Email */}
                                <Grid item xs={12}>
                                    <Box sx={{position: 'relative'}}>
                                        <Mail
                                            size={20}
                                            style={{
                                                position: 'absolute',
                                                left: 16,
                                                top: 30, // Ajuste de posición si es necesario
                                                color: 'rgba(255, 255, 255, 0.5)',
                                                zIndex: 1,
                                            }}
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
                                            // ... (Estilos del TextField)
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
                                </Grid>

                                {/* Usuario */}
                                <Grid item xs={12} sm={6}>
                                    <Box sx={{position: 'relative'}}>
                                        <User
                                            size={20}
                                            style={{
                                                position: 'absolute',
                                                left: 16,
                                                top: 30, // Ajuste de posición si es necesario
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
                                            // ... (Estilos del TextField)
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
                                </Grid>

                                {/* Password */}
                                <Grid item xs={12} sm={6}>
                                    <Box sx={{position: 'relative'}}>
                                        <Lock
                                            size={20}
                                            style={{
                                                position: 'absolute',
                                                left: 16,
                                                top: 30, // Ajuste de posición si es necesario
                                                color: 'rgba(255, 255, 255, 0.5)',
                                                zIndex: 1,
                                            }}
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
                                            // ... (Estilos del TextField)
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
                                </Grid>

                                {/* Cargo */}
                                <Grid item xs={12} sm={6}>
                                    <Box sx={{position: 'relative'}}>
                                        <Briefcase
                                            size={20}
                                            style={{
                                                position: 'absolute',
                                                left: 16,
                                                top: 30, // Ajuste de posición si es necesario
                                                color: 'rgba(255, 255, 255, 0.5)',
                                                zIndex: 1,
                                            }}
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
                                            // ... (Estilos del TextField)
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
                                </Grid>

                                {/* Rol - Select/ComboBox */}
                                <Grid item xs={12} sm={6}>
                                    <Box sx={{position: 'relative'}}>
                                        <Shield
                                            size={20}
                                            style={{
                                                position: 'absolute',
                                                left: 16,
                                                top: 30, // Ajuste de posición si es necesario
                                                color: 'rgba(255, 255, 255, 0.5)',
                                                zIndex: 1,
                                            }}
                                        />
                                        <TextField 
                                            margin="normal"
                                            required
                                            fullWidth
                                            select
                                            id="rol"
                                            label="Rol"
                                            name="rol"
                                            value={rol}
                                            onChange={(e) => setRol(e.target.value)}
                                            // ... (Estilos del TextField)
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
                                                '& .MuiSelect-select': {
                                                    color: '#fff',
                                                },
                                                '& .MuiSvgIcon-root': {
                                                    color: 'rgba(255, 255, 255, 0.5)',
                                                },
                                            }}
                                        >
                                            {roles.map((option) => (
                                                <MenuItem key={option} value={option}>
                                                    {option}
                                                </MenuItem>
                                            ))}
                                        </TextField>
                                    </Box>
                                </Grid>
                            </Grid>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            disabled={loading}
                            sx={{
                                mt: 3,
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
                                'Crear Cuenta'
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
                                ¿Ya tienes cuenta? <Link to="/login">Inicia Sesión</Link>
                            </Typography>
                        </Box>
                    </Box>
                </Box>
            </Container>
        </Box>
    )
}