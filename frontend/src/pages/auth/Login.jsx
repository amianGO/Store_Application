import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton,
  Divider
} from '@mui/material';
import {
  Person as PersonIcon,
  Lock as LockIcon,
  Business as BusinessIcon,
  Visibility,
  VisibilityOff,
  ArrowBack
} from '@mui/icons-material';
import api from '../../config/axios';
import { guardarDatosEmpleado } from '../../utils/authHelper';

export default function LoginEmpleado() {
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  // Obtener datos de la empresa desde localStorage o location.state
  const empresaId = localStorage.getItem('empresaId') || location.state?.empresaId;
  const empresaNombre = localStorage.getItem('empresaNombre') || location.state?.empresaNombre;
  const tenantKey = localStorage.getItem('tenantKey') || location.state?.tenantKey;
  const mensaje = location.state?.mensaje;

  const [formData, setFormData] = useState({
    usuario: '',
    password: '',
    tenantKey: tenantKey || ''
  });

  useEffect(() => {
    // Si no hay tenantKey, redirigir al login de empresa
    if (!tenantKey) {
      console.warn('⚠️ No hay tenantKey. Redirigiendo a login de empresa...');
      setError('No hay sesión de empresa activa. Serás redirigido...');
      setTimeout(() => navigate('/login'), 3000);
    }
  }, [tenantKey, navigate]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.usuario || !formData.password || !formData.tenantKey) {
      setError('Por favor complete todos los campos');
      return;
    }

    setLoading(true);

    try {
      console.log('📤 Intentando login de empleado...');
      console.log('Datos a enviar:', {
        usuario: formData.usuario,
        password: '***',
        tenantKey: formData.tenantKey
      });

      const response = await api.post('/auth/login', {
        usuario: formData.usuario,
        password: formData.password,
        tenantKey: formData.tenantKey
      });

      console.log('✅ Respuesta completa del login:', response.data);

      // La respuesta ya viene con todos los datos estructurados
      const { token, empleadoId, usuario, rol, nombre, apellido, cargo } = response.data;

      if (!token) {
        console.error('❌ No se recibió token en la respuesta');
        setError('Error: No se recibió token de autenticación');
        return;
      }

      // Usar el helper para guardar TODOS los datos
      guardarDatosEmpleado(response.data);

      console.log('✅ Datos guardados correctamente');
      console.log('✅ Redirigiendo al dashboard...');

      // Redirigir al dashboard
      navigate('/dashboard');

    } catch (err) {
      console.error('❌ Error en login de empleado:', err);

      if (err.response?.data?.mensaje) {
        setError(err.response.data.mensaje);
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.response?.status === 401) {
        setError('Credenciales inválidas. Verifica tu usuario, contraseña y tenant key.');
      } else if (err.response?.status === 404) {
        setError('Empresa no encontrada. Verifica el tenant key.');
      } else {
        setError('Error al iniciar sesión. Por favor intente nuevamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleVolver = () => {
    navigate('/bienvenida-empresa');
  };

  const handleCerrarSesionEmpresa = () => {
    // Limpiar TODOS los datos de localStorage
    localStorage.clear();
    
    console.log('🚪 Sesión de empresa cerrada completamente');
    
    // Redirigir al login principal de empresas
    navigate('/login', { 
      state: { 
        mensaje: 'Sesión cerrada exitosamente. Inicia sesión nuevamente.' 
      } 
    });
  };

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
      <Container maxWidth="sm">
        {/* Botones superiores */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
          <Button
            startIcon={<ArrowBack />}
            onClick={handleVolver}
            sx={{
              color: 'rgba(255, 255, 255, 0.8)',
              '&:hover': {
                color: '#2196f3'
              }
            }}
          >
            Volver a Bienvenida
          </Button>

          <Button
            variant="outlined"
            onClick={handleCerrarSesionEmpresa}
            sx={{
              borderColor: 'rgba(244, 67, 54, 0.5)',
              color: '#ef5350',
              '&:hover': {
                borderColor: '#ef5350',
                background: 'rgba(244, 67, 54, 0.1)'
              }
            }}
          >
            Cerrar Sesión de Empresa
          </Button>
        </Box>

        <Paper
          elevation={3}
          sx={{
            p: 4,
            background: 'rgba(30, 30, 47, 0.95)',
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(147, 112, 219, 0.2)',
            borderRadius: 2
          }}
        >
          {/* Header */}
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <PersonIcon
              sx={{
                fontSize: 60,
                color: '#2196f3',
                mb: 2
              }}
            />
            <Typography
              variant="h4"
              sx={{
                fontWeight: 'bold',
                background: 'linear-gradient(135deg, #2196f3 0%, #1976d2 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                mb: 1
              }}
            >
              Login de Empleado
            </Typography>
            <Typography
              variant="body2"
              sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
            >
              Accede con tus credenciales personales
            </Typography>
            {empresaNombre && (
              <Box
                sx={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: 1,
                  mt: 2,
                  px: 2,
                  py: 1,
                  background: 'rgba(147, 112, 219, 0.1)',
                  border: '1px solid rgba(147, 112, 219, 0.3)',
                  borderRadius: 1
                }}
              >
                <BusinessIcon sx={{ color: '#dda0dd', fontSize: 20 }} />
                <Typography variant="body2" sx={{ color: '#dda0dd' }}>
                  {empresaNombre}
                </Typography>
              </Box>
            )}
          </Box>

          {/* Mensaje de éxito */}
          {mensaje && (
            <Alert severity="success" sx={{ mb: 3 }}>
              {mensaje}
            </Alert>
          )}

          {/* Alerta de error */}
          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          {/* Formulario */}
          <form onSubmit={handleSubmit}>
            <TextField
              fullWidth
              required
              label="Nombre de Usuario"
              name="usuario"
              value={formData.usuario}
              onChange={handleChange}
              margin="normal"
              autoComplete="username"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PersonIcon sx={{ color: '#2196f3' }} />
                  </InputAdornment>
                ),
              }}
              sx={{
                '& .MuiOutlinedInput-root': {
                  color: 'white',
                  '& fieldset': {
                    borderColor: 'rgba(147, 112, 219, 0.3)'
                  },
                  '&:hover fieldset': {
                    borderColor: '#2196f3'
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#1976d2'
                  }
                },
                '& .MuiInputLabel-root': {
                  color: 'rgba(255, 255, 255, 0.7)'
                },
                '& .MuiInputLabel-root.Mui-focused': {
                  color: '#2196f3'
                }
              }}
            />

            <TextField
              fullWidth
              required
              type={showPassword ? 'text' : 'password'}
              label="Contraseña"
              name="password"
              value={formData.password}
              onChange={handleChange}
              margin="normal"
              autoComplete="current-password"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockIcon sx={{ color: '#2196f3' }} />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      onClick={() => setShowPassword(!showPassword)}
                      edge="end"
                      sx={{ color: 'rgba(255, 255, 255, 0.5)' }}
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{
                '& .MuiOutlinedInput-root': {
                  color: 'white',
                  '& fieldset': {
                    borderColor: 'rgba(147, 112, 219, 0.3)'
                  },
                  '&:hover fieldset': {
                    borderColor: '#2196f3'
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#1976d2'
                  }
                },
                '& .MuiInputLabel-root': {
                  color: 'rgba(255, 255, 255, 0.7)'
                },
                '& .MuiInputLabel-root.Mui-focused': {
                  color: '#2196f3'
                }
              }}
            />

            <TextField
              fullWidth
              required
              label="Tenant Key"
              name="tenantKey"
              value={formData.tenantKey}
              onChange={handleChange}
              margin="normal"
              helperText="Identificador único de tu empresa"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <BusinessIcon sx={{ color: '#9c27b0' }} />
                  </InputAdornment>
                ),
                readOnly: !!tenantKey, // Solo lectura si viene del estado
              }}
              sx={{
                '& .MuiOutlinedInput-root': {
                  color: 'white',
                  '& fieldset': {
                    borderColor: 'rgba(147, 112, 219, 0.3)'
                  },
                  '&:hover fieldset': {
                    borderColor: '#2196f3'
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#1976d2'
                  }
                },
                '& .MuiInputLabel-root': {
                  color: 'rgba(255, 255, 255, 0.7)'
                },
                '& .MuiInputLabel-root.Mui-focused': {
                  color: '#2196f3'
                },
                '& .MuiFormHelperText-root': {
                  color: 'rgba(255, 255, 255, 0.5)'
                }
              }}
            />

            <Button
              type="submit"
              variant="contained"
              fullWidth
              disabled={loading || !tenantKey}
              sx={{
                mt: 3,
                mb: 2,
                py: 1.5,
                background: 'linear-gradient(135deg, #2196f3 0%, #1976d2 100%)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #1976d2 0%, #1565c0 100%)',
                  transform: 'translateY(-2px)',
                  boxShadow: '0 8px 25px rgba(33, 150, 243, 0.4)'
                },
                '&:disabled': {
                  background: 'rgba(33, 150, 243, 0.3)'
                },
                transition: 'all 0.3s ease'
              }}
            >
              {loading ? (
                <CircularProgress size={24} sx={{ color: 'white' }} />
              ) : (
                'Iniciar Sesión'
              )}
            </Button>
          </form>

          <Divider sx={{ my: 3, borderColor: 'rgba(147, 112, 219, 0.2)' }} />

          {/* Nota informativa */}
          <Box
            sx={{
              p: 2,
              background: 'rgba(33, 150, 243, 0.1)',
              border: '1px solid rgba(33, 150, 243, 0.2)',
              borderRadius: 1
            }}
          >
            <Typography
              variant="caption"
              sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
            >
              ℹ️ <strong>¿No tienes usuario?</strong>
              <br />
              El administrador de tu empresa debe crear tu cuenta de empleado desde el sistema.
            </Typography>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}