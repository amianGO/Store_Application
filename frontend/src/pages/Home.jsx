import React from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Box, 
  Container, 
  Typography, 
  Button, 
  Grid, 
  Card, 
  CardContent,
  AppBar,
  Toolbar
} from '@mui/material';
import {
  Store as StoreIcon,
  Inventory as InventoryIcon,
  People as PeopleIcon,
  Receipt as ReceiptIcon,
  Speed as SpeedIcon,
  Security as SecurityIcon
} from '@mui/icons-material';

export default function Home() {
  const navigate = useNavigate();

  const features = [
    {
      icon: <InventoryIcon sx={{ fontSize: 40, color: '#dda0dd' }} />,
      title: 'Gestión de Inventario',
      description: 'Control total de tus productos, stock y movimientos en tiempo real'
    },
    {
      icon: <ReceiptIcon sx={{ fontSize: 40, color: '#9370db' }} />,
      title: 'Punto de Venta',
      description: 'Sistema POS rápido y eficiente para agilizar tus ventas'
    },
    {
      icon: <PeopleIcon sx={{ fontSize: 40, color: '#2196f3' }} />,
      title: 'Multi-Usuario',
      description: 'Gestión de empleados con roles y permisos personalizados'
    },
    {
      icon: <SpeedIcon sx={{ fontSize: 40, color: '#4caf50' }} />,
      title: 'Reportes en Tiempo Real',
      description: 'Estadísticas y análisis para tomar mejores decisiones'
    },
    {
      icon: <SecurityIcon sx={{ fontSize: 40, color: '#ff9800' }} />,
      title: 'Seguridad Multi-Tenant',
      description: 'Datos aislados y seguros para cada empresa'
    },
    {
      icon: <StoreIcon sx={{ fontSize: 40, color: '#9c27b0' }} />,
      title: 'Múltiples Sucursales',
      description: 'Administra todas tus tiendas desde un solo lugar'
    }
  ];

  return (
    <Box sx={{ 
      minHeight: '100vh',
      background: '#1e1e2f',
      color: 'white'
    }}>
      {/* Navbar */}
      <AppBar 
        position="static" 
        sx={{
          background: 'rgba(30, 30, 47, 0.95)',
          backdropFilter: 'blur(20px)',
          borderBottom: '1px solid rgba(147, 112, 219, 0.2)',
          boxShadow: '0 4px 20px rgba(0, 0, 0, 0.3)'
        }}
      >
        <Toolbar sx={{ justifyContent: 'space-between' }}>
          <Typography 
            variant="h5" 
            sx={{ 
              fontWeight: 'bold',
              background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent'
            }}
          >
            Sistema Multi-Tenant
          </Typography>
          <Box>
            <Button 
              variant="outlined" 
              onClick={() => navigate('/login')}
              sx={{
                mr: 2,
                borderColor: '#dda0dd',
                color: '#dda0dd',
                '&:hover': {
                  borderColor: '#9370db',
                  background: 'rgba(147, 112, 219, 0.1)'
                }
              }}
            >
              Iniciar Sesión
            </Button>
            <Button 
              variant="contained"
              onClick={() => navigate('/register')}
              sx={{
                background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #9370db 0%, #7b5cb8 100%)'
                }
              }}
            >
              Registrarse
            </Button>
          </Box>
        </Toolbar>
      </AppBar>

      {/* Hero Section */}
      <Container maxWidth="lg" sx={{ pt: 12, pb: 8 }}>
        <Grid container spacing={4} alignItems="center">
          <Grid item xs={12} md={6}>
            <Typography 
              variant="h2" 
              sx={{ 
                fontWeight: 'bold', 
                mb: 3,
                background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent'
              }}
            >
              Gestiona tu Negocio de Forma Profesional
            </Typography>
            <Typography 
              variant="h6" 
              sx={{ 
                mb: 4, 
                color: 'rgba(255, 255, 255, 0.8)',
                lineHeight: 1.6
              }}
            >
              Sistema completo de punto de venta e inventario con arquitectura multi-tenant. 
              Cada empresa con su propia base de datos aislada y segura.
            </Typography>
            <Box sx={{ display: 'flex', gap: 2 }}>
              <Button 
                variant="contained" 
                size="large"
                onClick={() => navigate('/register')}
                sx={{
                  px: 4,
                  py: 1.5,
                  background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                  '&:hover': {
                    background: 'linear-gradient(135deg, #9370db 0%, #7b5cb8 100%)',
                    transform: 'translateY(-2px)',
                    boxShadow: '0 8px 25px rgba(147, 112, 219, 0.4)'
                  },
                  transition: 'all 0.3s ease'
                }}
              >
                Empezar Gratis
              </Button>
              <Button 
                variant="outlined" 
                size="large"
                onClick={() => navigate('/login')}
                sx={{
                  px: 4,
                  py: 1.5,
                  borderColor: '#2196f3',
                  color: '#2196f3',
                  '&:hover': {
                    borderColor: '#1976d2',
                    background: 'rgba(33, 150, 243, 0.1)'
                  }
                }}
              >
                Ver Demo
              </Button>
            </Box>
          </Grid>
          <Grid item xs={12} md={6}>
            <Box 
              sx={{
                background: 'rgba(147, 112, 219, 0.1)',
                borderRadius: 4,
                p: 4,
                border: '1px solid rgba(147, 112, 219, 0.3)',
                textAlign: 'center'
              }}
            >
              <StoreIcon sx={{ fontSize: 200, color: '#dda0dd', opacity: 0.8 }} />
            </Box>
          </Grid>
        </Grid>
      </Container>

      {/* Features Section */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography 
          variant="h3" 
          align="center" 
          sx={{ 
            mb: 6,
            fontWeight: 'bold',
            background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent'
          }}
        >
          Características Principales
        </Typography>
        <Grid container spacing={4}>
          {features.map((feature, index) => (
            <Grid item xs={12} sm={6} md={4} key={index}>
              <Card 
                sx={{
                  height: '100%',
                  background: 'rgba(30, 30, 47, 0.6)',
                  backdropFilter: 'blur(10px)',
                  border: '1px solid rgba(147, 112, 219, 0.2)',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'translateY(-8px)',
                    boxShadow: '0 12px 40px rgba(147, 112, 219, 0.3)',
                    border: '1px solid rgba(147, 112, 219, 0.5)'
                  }
                }}
              >
                <CardContent sx={{ textAlign: 'center', p: 4 }}>
                  <Box sx={{ mb: 2 }}>
                    {feature.icon}
                  </Box>
                  <Typography 
                    variant="h6" 
                    sx={{ 
                      mb: 2, 
                      fontWeight: 'bold',
                      color: 'white'
                    }}
                  >
                    {feature.title}
                  </Typography>
                  <Typography 
                    variant="body2" 
                    sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
                  >
                    {feature.description}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* CTA Section */}
      <Box 
        sx={{ 
          py: 8, 
          background: 'linear-gradient(135deg, rgba(147, 112, 219, 0.2) 0%, rgba(221, 160, 221, 0.1) 100%)',
          borderTop: '1px solid rgba(147, 112, 219, 0.3)',
          borderBottom: '1px solid rgba(147, 112, 219, 0.3)'
        }}
      >
        <Container maxWidth="md">
          <Typography 
            variant="h3" 
            align="center" 
            sx={{ 
              mb: 3,
              fontWeight: 'bold',
              color: 'white'
            }}
          >
            ¿Listo para comenzar?
          </Typography>
          <Typography 
            variant="h6" 
            align="center" 
            sx={{ 
              mb: 4,
              color: 'rgba(255, 255, 255, 0.8)'
            }}
          >
            Únete a cientos de empresas que ya confían en nuestro sistema
          </Typography>
          <Box sx={{ display: 'flex', justifyContent: 'center' }}>
            <Button 
              variant="contained" 
              size="large"
              onClick={() => navigate('/register')}
              sx={{
                px: 6,
                py: 2,
                fontSize: '1.1rem',
                background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #9370db 0%, #7b5cb8 100%)',
                  transform: 'scale(1.05)',
                  boxShadow: '0 12px 40px rgba(147, 112, 219, 0.5)'
                },
                transition: 'all 0.3s ease'
              }}
            >
              Crear Cuenta Gratis
            </Button>
          </Box>
        </Container>
      </Box>

      {/* Footer */}
      <Box 
        sx={{ 
          py: 4, 
          textAlign: 'center',
          borderTop: '1px solid rgba(147, 112, 219, 0.2)'
        }}
      >
        <Typography 
          variant="body2" 
          sx={{ color: 'rgba(255, 255, 255, 0.5)' }}
        >
          © 2025 Sistema Multi-Tenant. Todos los derechos reservados.
        </Typography>
      </Box>
    </Box>
  );
}
