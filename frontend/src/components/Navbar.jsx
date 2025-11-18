import React from "react";
import {AppBar, Toolbar, Typography, Button} from '@mui/material'
import {useNavigate} from 'react-router-dom'

export default function Navbar(){

    const navigate = useNavigate()

    const handleLogout = () => {
        localStorage.removeItem('token')
        localStorage.removeItem('empleadoId')
        localStorage.removeItem('usuario')
        localStorage.removeItem('rol')
        localStorage.removeItem('nombre')
        localStorage.removeItem('apellido')
        localStorage.removeItem('cargo')
        navigate('/login')
    }

    return(
        <AppBar 
            sx={{
                background: 'rgba(30, 30, 47, 0.95)',
                backdropFilter: 'blur(20px)',
                borderBottom: '1px solid rgba(147, 112, 219, 0.2)',
                boxShadow: '0 4px 20px rgba(0, 0, 0, 0.3)',
                zIndex: 1100
            }} 
        >
            <Toolbar sx={{ display: "flex", justifyContent: "space-between"}} >
                <Typography 
                    variant="h6" 
                    sx={{ 
                        fontWeight: 'bold', 
                        cursor: 'pointer',
                        background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                        WebkitBackgroundClip: 'text',
                        WebkitTextFillColor: 'transparent',
                        backgroundClip: 'text'
                    }} 
                    onClick={() => navigate('/dashboard')}
                >
                    Tienda Adrian
                </Typography>
                <div>
                    <Button 
                        color="inherit" 
                        onClick={() => navigate('/dashboard')}
                        sx={{
                            mr: 1,
                            color: 'rgba(255, 255, 255, 0.8)',
                            '&:hover': {
                                color: '#dda0dd',
                                background: 'rgba(147, 112, 219, 0.1)'
                            }
                        }}
                    >
                        Dashboard
                    </Button>
                    <Button 
                        color="inherit" 
                        onClick={() => navigate('/clientes')}
                        sx={{
                            mr: 1,
                            color: 'rgba(255, 255, 255, 0.8)',
                            '&:hover': {
                                color: '#2196f3',
                                background: 'rgba(33, 150, 243, 0.1)'
                            }
                        }}
                    >
                        Clientes
                    </Button>
                    <Button 
                        color="inherit" 
                        onClick={() => navigate('/empleados')}
                        sx={{
                            mr: 1,
                            color: 'rgba(255, 255, 255, 0.8)',
                            '&:hover': {
                                color: '#9c27b0',
                                background: 'rgba(156, 39, 176, 0.1)'
                            }
                        }}
                    >
                        Empleados
                    </Button>
                    <Button 
                        color="inherit" 
                        onClick={() => navigate('/ventas')}
                        sx={{
                            mr: 1,
                            color: 'rgba(255, 255, 255, 0.8)',
                            '&:hover': {
                                color: '#ff9800',
                                background: 'rgba(255, 152, 0, 0.1)'
                            }
                        }}
                    >
                        Ventas
                    </Button>
                    <Button 
                        color="inherit" 
                        onClick={() => navigate('/productos/create')}
                        sx={{
                            mr: 1,
                            color: 'rgba(255, 255, 255, 0.8)',
                            '&:hover': {
                                color: '#4caf50',
                                background: 'rgba(76, 175, 80, 0.1)'
                            }
                        }}
                    >
                        + Producto
                    </Button>
                    <Button 
                        color="inherit" 
                        onClick={handleLogout}
                        sx={{
                            color: 'rgba(255, 255, 255, 0.8)',
                            '&:hover': {
                                color: '#ff9800',
                                background: 'rgba(255, 152, 0, 0.1)'
                            }
                        }}
                    >
                        Salir
                    </Button>
                </div>
            </Toolbar>
        </AppBar>
    )
}