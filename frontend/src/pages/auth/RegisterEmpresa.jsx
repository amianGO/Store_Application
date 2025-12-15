import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Grid,
  Alert,
  CircularProgress,
  InputAdornment,
  IconButton
} from '@mui/material';
import {
  Business as BusinessIcon,
  Email as EmailIcon,
  Lock as LockIcon,
  Phone as PhoneIcon,
  LocationOn as LocationIcon,
  Visibility,
  VisibilityOff,
  ArrowBack
} from '@mui/icons-material';
import api from '../../config/axios';

export default function RegisterEmpresa() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [formData, setFormData] = useState({
    nombre: '',
    nombreComercial: '',
    nit: '',
    email: '',
    password: '',
    confirmPassword: '',
    telefono: '',
    direccion: '',
    ciudad: '',
    pais: 'Colombia'
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const validateForm = () => {
    if (!formData.nombre || !formData.nit || !formData.email || !formData.password) {
      setError('Por favor complete todos los campos obligatorios');
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return false;
    }

    if (formData.password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres');
      return false;
    }

    // Validar patrón de contraseña
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=]).*$/;
    if (!passwordPattern.test(formData.password)) {
      setError('La contraseña debe contener: mayúscula, minúscula, número y carácter especial (@#$%^&+=)');
      return false;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(formData.email)) {
      setError('Email inválido');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!validateForm()) return;

    setLoading(true);

    try {
      console.log('🚀 Enviando petición a:', api.defaults.baseURL + '/auth/empresa/registro');
      console.log('📦 Datos a enviar:', formData);
      
      const response = await api.post('/auth/empresa/registro', formData);

      console.log('✅ Empresa registrada:', response.data);

      setSuccess('¡Empresa registrada exitosamente! Revisa tu email para verificar tu cuenta.');

      // Limpiar formulario
      setFormData({
        nombre: '',
        nombreComercial: '',
        nit: '',
        email: '',
        password: '',
        confirmPassword: '',
        telefono: '',
        direccion: '',
        ciudad: '',
        pais: 'Colombia'
      });

      // Redirigir al login después de 3 segundos
      setTimeout(() => {
        navigate('/login');
      }, 3000);

    } catch (err) {
      console.error('❌ Error al registrar empresa:', err);
      console.error('❌ Error response:', err.response);
      console.error('❌ Error request:', err.request);
      console.error('❌ Error message:', err.message);

      if (err.response?.data?.mensaje) {
        setError(err.response.data.mensaje);
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.message) {
        setError(`Error de red: ${err.message}`);
      } else {
        setError('Error al registrar empresa. Por favor intente nuevamente.');
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
        py: 4
      }}
    >
      <Container maxWidth="md">
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
              Registro de Empresa
            </Typography>
            <Typography
              variant="body2"
              sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
            >
              Crea tu cuenta empresarial y comienza a gestionar tu negocio
            </Typography>
          </Box>

          {/* Alertas */}
          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}
          {success && (
            <Alert severity="success" sx={{ mb: 3 }}>
              {success}
            </Alert>
          )}

          {/* Formulario */}
          <form onSubmit={handleSubmit}>
            <Grid container spacing={3}>
              {/* Información de la Empresa */}
              <Grid item xs={12}>
                <Typography
                  variant="h6"
                  sx={{ color: '#dda0dd', mb: 2, fontWeight: 'bold' }}
                >
                  Información de la Empresa
                </Typography>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Nombre de la Empresa"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <BusinessIcon sx={{ color: '#dda0dd' }} />
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
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Nombre Comercial (Opcional)"
                  name="nombreComercial"
                  value={formData.nombreComercial}
                  onChange={handleChange}
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: 'white',
                      '& fieldset': {
                        borderColor: 'rgba(147, 112, 219, 0.3)'
                      },
                      '&:hover fieldset': {
                        borderColor: '#dda0dd'
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="NIT"
                  name="nit"
                  value={formData.nit}
                  onChange={handleChange}
                  placeholder="900123456-7"
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: 'white',
                      '& fieldset': {
                        borderColor: 'rgba(147, 112, 219, 0.3)'
                      },
                      '&:hover fieldset': {
                        borderColor: '#dda0dd'
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Teléfono (Opcional)"
                  name="telefono"
                  value={formData.telefono}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <PhoneIcon sx={{ color: '#2196f3' }} />
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
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              {/* Ubicación */}
              <Grid item xs={12}>
                <Typography
                  variant="h6"
                  sx={{ color: '#2196f3', mb: 2, fontWeight: 'bold', mt: 2 }}
                >
                  Ubicación
                </Typography>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Dirección (Opcional)"
                  name="direccion"
                  value={formData.direccion}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <LocationIcon sx={{ color: '#2196f3' }} />
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
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Ciudad (Opcional)"
                  name="ciudad"
                  value={formData.ciudad}
                  onChange={handleChange}
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: 'white',
                      '& fieldset': {
                        borderColor: 'rgba(147, 112, 219, 0.3)'
                      },
                      '&:hover fieldset': {
                        borderColor: '#dda0dd'
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="País"
                  name="pais"
                  value={formData.pais}
                  onChange={handleChange}
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: 'white',
                      '& fieldset': {
                        borderColor: 'rgba(147, 112, 219, 0.3)'
                      },
                      '&:hover fieldset': {
                        borderColor: '#dda0dd'
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              {/* Credenciales */}
              <Grid item xs={12}>
                <Typography
                  variant="h6"
                  sx={{ color: '#4caf50', mb: 2, fontWeight: 'bold', mt: 2 }}
                >
                  Credenciales de Acceso
                </Typography>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  type="email"
                  label="Email Corporativo"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <EmailIcon sx={{ color: '#4caf50' }} />
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
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  type={showPassword ? 'text' : 'password'}
                  label="Contraseña"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <LockIcon sx={{ color: '#ff9800' }} />
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
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  type={showConfirmPassword ? 'text' : 'password'}
                  label="Confirmar Contraseña"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <LockIcon sx={{ color: '#ff9800' }} />
                      </InputAdornment>
                    ),
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                          edge="end"
                          sx={{ color: 'rgba(255, 255, 255, 0.5)' }}
                        >
                          {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
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
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              {/* Requisitos de contraseña */}
              <Grid item xs={12}>
                <Typography
                  variant="caption"
                  sx={{ color: 'rgba(255, 255, 255, 0.5)' }}
                >
                  * La contraseña debe tener mínimo 8 caracteres, incluir mayúscula, minúscula, número y carácter especial (@#$%^&+=)
                </Typography>
              </Grid>

              {/* Botones */}
              <Grid item xs={12}>
                <Button
                  type="submit"
                  variant="contained"
                  fullWidth
                  disabled={loading}
                  sx={{
                    py: 1.5,
                    mt: 2,
                    background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                    '&:hover': {
                      background: 'linear-gradient(135deg, #9370db 0%, #7b5cb8 100%)',
                      transform: 'translateY(-2px)',
                      boxShadow: '0 8px 25px rgba(147, 112, 219, 0.4)'
                    },
                    transition: 'all 0.3s ease'
                  }}
                >
                  {loading ? (
                    <CircularProgress size={24} sx={{ color: 'white' }} />
                  ) : (
                    'Registrar Empresa'
                  )}
                </Button>
              </Grid>

              <Grid item xs={12}>
                <Typography
                  variant="body2"
                  align="center"
                  sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
                >
                  ¿Ya tienes una cuenta?{' '}
                  <Button
                    onClick={() => navigate('/login')}
                    sx={{
                      color: '#dda0dd',
                      textTransform: 'none',
                      '&:hover': {
                        color: '#9370db',
                        background: 'transparent'
                      }
                    }}
                  >
                    Iniciar Sesión
                  </Button>
                </Typography>
              </Grid>
            </Grid>
          </form>
        </Paper>
      </Container>
    </Box>
  );
}
