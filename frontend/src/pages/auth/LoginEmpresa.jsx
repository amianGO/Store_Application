import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
  Business as BusinessIcon,
  Email as EmailIcon,
  Lock as LockIcon,
  Visibility,
  VisibilityOff,
  ArrowBack
} from '@mui/icons-material';
import api from '../../config/axios';

export default function LoginEmpresa() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });

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

    if (!formData.email || !formData.password) {
      setError('Por favor complete todos los campos');
      return;
    }

    setLoading(true);

    try {
      // 1. Login de Empresa
      console.log('📤 Intentando login de empresa...');
      const response = await api.post('/auth/empresa/login', formData);

      console.log('✅ Login exitoso:', response.data);

      const { token, empresa } = response.data;

      // 2. Guardar datos de la empresa en localStorage
      localStorage.setItem('tokenEmpresa', token);
      localStorage.setItem('empresaId', empresa.id);
      localStorage.setItem('empresaNombre', empresa.nombre);
      localStorage.setItem('empresaEmail', empresa.email);
      localStorage.setItem('schemaName', empresa.schemaName);
      localStorage.setItem('tenantKey', empresa.tenantKey);

      // 3. Redirigir a página de bienvenida
      console.log('✅ Redirigiendo a bienvenida...');
      navigate('/bienvenida-empresa', {
        state: {
          empresaId: empresa.id,
          empresaNombre: empresa.nombre,
          tenantKey: empresa.tenantKey,
          schemaName: empresa.schemaName
        }
      });

    } catch (err) {
      console.error('❌ Error en login:', err);

      if (err.response?.data?.mensaje) {
        setError(err.response.data.mensaje);
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.response?.status === 401) {
        setError('Credenciales inválidas. Verifica tu email y contraseña.');
      } else {
        setError('Error al iniciar sesión. Por favor intente nuevamente.');
      }
    } finally {
      setLoading(false);
    }
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
        {/* Botón Volver */}
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/')}
          sx={{
            mb: 3,
            color: 'rgba(255, 255, 255, 0.8)',
            '&:hover': {
              color: '#dda0dd'
            }
          }}
        >
          Volver al inicio
        </Button>

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
            <BusinessIcon
              sx={{
                fontSize: 60,
                color: '#dda0dd',
                mb: 2
              }}
            />
            <Typography
              variant="h4"
              sx={{
                fontWeight: 'bold',
                background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                mb: 1
              }}
            >
              Acceso Empresarial
            </Typography>
            <Typography
              variant="body2"
              sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
            >
              Ingresa las credenciales de tu empresa
            </Typography>
          </Box>

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
              type="email"
              label="Email Empresarial"
              name="email"
              value={formData.email}
              onChange={handleChange}
              margin="normal"
              autoComplete="email"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <EmailIcon sx={{ color: '#dda0dd' }} />
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
                    borderColor: '#dda0dd'
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#9370db'
                  }
                },
                '& .MuiInputLabel-root': {
                  color: 'rgba(255, 255, 255, 0.7)'
                },
                '& .MuiInputLabel-root.Mui-focused': {
                  color: '#dda0dd'
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
                    <LockIcon sx={{ color: '#9370db' }} />
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
                    borderColor: '#dda0dd'
                  },
                  '&.Mui-focused fieldset': {
                    borderColor: '#9370db'
                  }
                },
                '& .MuiInputLabel-root': {
                  color: 'rgba(255, 255, 255, 0.7)'
                },
                '& .MuiInputLabel-root.Mui-focused': {
                  color: '#dda0dd'
                }
              }}
            />

            <Button
              type="submit"
              variant="contained"
              fullWidth
              disabled={loading}
              sx={{
                mt: 3,
                mb: 2,
                py: 1.5,
                background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #9370db 0%, #7b5cb8 100%)',
                  transform: 'translateY(-2px)',
                  boxShadow: '0 8px 25px rgba(147, 112, 219, 0.4)'
                },
                '&:disabled': {
                  background: 'rgba(147, 112, 219, 0.3)'
                },
                transition: 'all 0.3s ease'
              }}
            >
              {loading ? (
                <CircularProgress size={24} sx={{ color: 'white' }} />
              ) : (
                'Acceder'
              )}
            </Button>
          </form>

          <Divider sx={{ my: 3, borderColor: 'rgba(147, 112, 219, 0.2)' }} />

          {/* Links adicionales */}
          <Box sx={{ textAlign: 'center' }}>
            <Typography
              variant="body2"
              sx={{ color: 'rgba(255, 255, 255, 0.7)', mb: 2 }}
            >
              ¿No tienes una cuenta empresarial?
            </Typography>
            <Button
              variant="outlined"
              fullWidth
              onClick={() => navigate('/register')}
              sx={{
                borderColor: '#2196f3',
                color: '#2196f3',
                '&:hover': {
                  borderColor: '#1976d2',
                  background: 'rgba(33, 150, 243, 0.1)'
                }
              }}
            >
              Registrar Empresa
            </Button>
          </Box>

          {/* Nota informativa */}
          <Box
            sx={{
              mt: 4,
              p: 2,
              background: 'rgba(147, 112, 219, 0.1)',
              border: '1px solid rgba(147, 112, 219, 0.2)',
              borderRadius: 1
            }}
          >
            <Typography
              variant="caption"
              sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
            >
              ℹ️ <strong>Proceso de login:</strong>
              <br />
              1. Ingresas con las credenciales de tu empresa
              <br />
              2. Si es tu primera vez, crearás el primer usuario administrador
              <br />
              3. Luego iniciarás sesión con tu usuario personal
            </Typography>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}
