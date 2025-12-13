import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  Box,
  Container,
  Paper,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  Divider,
  Chip,
  CircularProgress
} from '@mui/material';
import {
  Business as BusinessIcon,
  PersonAdd as PersonAddIcon,
  Login as LoginIcon,
  People as PeopleIcon,
  CheckCircle as CheckCircleIcon,
  ArrowBack
} from '@mui/icons-material';
import api from '../../config/axios';

export default function BienvenidaEmpresa() {
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [tieneEmpleados, setTieneEmpleados] = useState(false);
  const [cantidadEmpleados, setCantidadEmpleados] = useState(0);

  // Datos de la empresa desde localStorage o location.state
  const empresaId = localStorage.getItem('empresaId') || location.state?.empresaId;
  const empresaNombre = localStorage.getItem('empresaNombre') || location.state?.empresaNombre;
  const tenantKey = localStorage.getItem('tenantKey') || location.state?.tenantKey;
  const schemaName = localStorage.getItem('schemaName') || location.state?.schemaName;

  useEffect(() => {
    const verificarSesion = async () => {
      const empresaId = localStorage.getItem('empresaId');
      const tenantKey = localStorage.getItem('tenantKey');

      if (!empresaId || !tenantKey) {
        navigate('/login');
        return;
      }

      try {
        const response = await api.get(`/auth/empresa/${empresaId}/tiene-empleados`);
        const tieneEmpleados = response.data?.tieneEmpleados || response.data === true;
        
        if (tieneEmpleados) {
          navigate('/login-empleado', {
            state: {
              empresaId,
              empresaNombre: localStorage.getItem('empresaNombre'),
              tenantKey
            }
          });
        } else {
          setTieneEmpleados(false);
        }
      } catch (error) {
        console.error('Error al verificar empleados:', error);
      } finally {
        setLoading(false);
      }
    };

    verificarSesion();
  }, [navigate]);

  const verificarEmpleados = async () => {
    try {
      console.log('📤 Verificando empleados de la empresa...');
      const response = await api.get(`/auth/empresa/${empresaId}/tiene-empleados`);

      console.log('✅ Verificación:', response.data);

      setTieneEmpleados(response.data.tieneEmpleados);
      setCantidadEmpleados(response.data.cantidadEmpleados || 0);
    } catch (error) {
      console.error('❌ Error al verificar empleados:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCrearPrimerEmpleado = () => {
    console.log('🚀 Navegando a crear primer empleado...');
    console.log('📋 Datos que se pasarán:', {
      empresaId: localStorage.getItem('empresaId'),
      empresaNombre: localStorage.getItem('empresaNombre'),
      tenantKey: localStorage.getItem('tenantKey')
    });

    navigate('/crear-primer-empleado', {
      state: {
        empresaId: localStorage.getItem('empresaId'),
        empresaNombre: localStorage.getItem('empresaNombre'),
        tenantKey: localStorage.getItem('tenantKey')
      }
    });
  };

  const handleLoginEmpleado = () => {
    navigate('/login-empleado', {
      state: {
        empresaId,
        empresaNombre,
        tenantKey,
        schemaName
      }
    });
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  if (loading) {
    return (
      <Box
        sx={{
          minHeight: '100vh',
          background: '#1e1e2f',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}
      >
        <CircularProgress sx={{ color: '#dda0dd' }} />
      </Box>
    );
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: '#1e1e2f',
        py: 6
      }}
    >
      <Container maxWidth="md">
        {/* Botón Cerrar Sesión */}
        <Button
          startIcon={<ArrowBack />}
          onClick={handleLogout}
          sx={{
            mb: 3,
            color: 'rgba(255, 255, 255, 0.8)',
            '&:hover': {
              color: '#ff9800'
            }
          }}
        >
          Cerrar Sesión
        </Button>

        {/* Header con Bienvenida */}
        <Paper
          elevation={3}
          sx={{
            p: 4,
            mb: 4,
            background: 'rgba(30, 30, 47, 0.95)',
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(147, 112, 219, 0.2)',
            borderRadius: 2,
            textAlign: 'center'
          }}
        >
          <BusinessIcon
            sx={{
              fontSize: 80,
              color: '#dda0dd',
              mb: 2
            }}
          />
          <Typography
            variant="h3"
            sx={{
              fontWeight: 'bold',
              background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              mb: 2
            }}
          >
            ¡Bienvenido!
          </Typography>
          <Typography
            variant="h5"
            sx={{
              color: 'white',
              mb: 2
            }}
          >
            {empresaNombre}
          </Typography>
          <Chip
            icon={<CheckCircleIcon />}
            label="Sesión Empresarial Activa"
            sx={{
              background: 'rgba(76, 175, 80, 0.2)',
              color: '#4caf50',
              border: '1px solid #4caf50'
            }}
          />
        </Paper>

        {/* Información de Empleados */}
        {tieneEmpleados && (
          <Paper
            sx={{
              p: 3,
              mb: 4,
              background: 'rgba(147, 112, 219, 0.1)',
              border: '1px solid rgba(147, 112, 219, 0.2)',
              borderRadius: 2,
              display: 'flex',
              alignItems: 'center',
              gap: 2
            }}
          >
            <PeopleIcon sx={{ fontSize: 40, color: '#2196f3' }} />
            <Box>
              <Typography variant="h6" sx={{ color: 'white' }}>
                Empleados Registrados: {cantidadEmpleados}
              </Typography>
              <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.7)' }}>
                Tu empresa ya tiene usuarios configurados
              </Typography>
            </Box>
          </Paper>
        )}

        {/* Opciones de Acceso */}
        <Typography
          variant="h5"
          sx={{
            color: 'white',
            mb: 3,
            fontWeight: 'bold',
            textAlign: 'center'
          }}
        >
          ¿Qué deseas hacer?
        </Typography>

        <Grid container spacing={3}>
          {/* Opción: Crear Primer Empleado */}
          {!tieneEmpleados && (
            <Grid item xs={12}>
              <Card
                sx={{
                  height: '100%',
                  background: 'rgba(30, 30, 47, 0.95)',
                  backdropFilter: 'blur(20px)',
                  border: '2px solid rgba(147, 112, 219, 0.3)',
                  borderRadius: 2,
                  cursor: 'pointer',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    transform: 'translateY(-8px)',
                    boxShadow: '0 12px 40px rgba(147, 112, 219, 0.4)',
                    border: '2px solid #dda0dd'
                  }
                }}
                onClick={handleCrearPrimerEmpleado}
              >
                <CardContent sx={{ textAlign: 'center', p: 4 }}>
                  <PersonAddIcon sx={{ fontSize: 60, color: '#4caf50', mb: 2 }} />
                  <Typography
                    variant="h5"
                    sx={{
                      fontWeight: 'bold',
                      color: 'white',
                      mb: 2
                    }}
                  >
                    Crear Primer Empleado Administrador
                  </Typography>
                  <Typography
                    variant="body1"
                    sx={{ color: 'rgba(255, 255, 255, 0.7)', mb: 3 }}
                  >
                    Es tu primera vez. Crea tu usuario administrador para gestionar el sistema.
                  </Typography>
                  <Button
                    variant="contained"
                    fullWidth
                    startIcon={<PersonAddIcon />}
                    onClick={handleCrearPrimerEmpleado}
                    sx={{
                      py: 1.5,
                      background: 'linear-gradient(135deg, #4caf50 0%, #45a049 100%)',
                      '&:hover': {
                        background: 'linear-gradient(135deg, #45a049 0%, #3d8b40 100%)'
                      }
                    }}
                  >
                    Crear Ahora
                  </Button>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Opción: Login de Empleado */}
          <Grid item xs={12}>
            <Card
              sx={{
                height: '100%',
                background: 'rgba(30, 30, 47, 0.95)',
                backdropFilter: 'blur(20px)',
                border: '2px solid rgba(147, 112, 219, 0.3)',
                borderRadius: 2,
                cursor: 'pointer',
                transition: 'all 0.3s ease',
                '&:hover': {
                  transform: 'translateY(-8px)',
                  boxShadow: '0 12px 40px rgba(33, 150, 243, 0.4)',
                  border: '2px solid #2196f3'
                }
              }}
              onClick={handleLoginEmpleado}
            >
              <CardContent sx={{ textAlign: 'center', p: 4 }}>
                <LoginIcon sx={{ fontSize: 60, color: '#2196f3', mb: 2 }} />
                <Typography
                  variant="h5"
                  sx={{
                    fontWeight: 'bold',
                    color: 'white',
                    mb: 2
                  }}
                >
                  {tieneEmpleados ? 'Iniciar Sesión como Empleado' : 'Ya tengo un usuario'}
                </Typography>
                <Typography
                  variant="body1"
                  sx={{ color: 'rgba(255, 255, 255, 0.7)', mb: 3 }}
                >
                  {tieneEmpleados
                    ? 'Accede al sistema con tu usuario y contraseña personal.'
                    : 'Si ya creaste tu usuario anteriormente, inicia sesión aquí.'}
                </Typography>
                <Button
                  variant="contained"
                  fullWidth
                  startIcon={<LoginIcon />}
                  onClick={handleLoginEmpleado}
                  sx={{
                    py: 1.5,
                    background: 'linear-gradient(135deg, #2196f3 0%, #1976d2 100%)',
                    '&:hover': {
                      background: 'linear-gradient(135deg, #1976d2 0%, #1565c0 100%)'
                    }
                  }}
                >
                  Acceder
                </Button>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        <Divider sx={{ my: 4, borderColor: 'rgba(147, 112, 219, 0.2)' }} />

        {/* Información adicional */}
        <Paper
          sx={{
            p: 3,
            background: 'rgba(147, 112, 219, 0.1)',
            border: '1px solid rgba(147, 112, 219, 0.2)',
            borderRadius: 2
          }}
        >
          <Typography
            variant="body2"
            sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
          >
            ℹ️ <strong>Información:</strong>
            <br />
            • <strong>Tenant Key:</strong> {tenantKey}
            <br />
            • <strong>Schema:</strong> {schemaName}
            <br />
            • Los datos de tu empresa están completamente aislados y seguros
          </Typography>
        </Paper>
      </Container>
    </Box>
  );
}
