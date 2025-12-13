import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
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
  IconButton,
  Divider
} from '@mui/material';
import {
  PersonAdd as PersonAddIcon,
  Person as PersonIcon,
  Badge as BadgeIcon,
  Phone as PhoneIcon,
  Email as EmailIcon,
  Lock as LockIcon,
  Visibility,
  VisibilityOff,
  ArrowBack,
  Business as BusinessIcon
} from '@mui/icons-material';
import api from '../../config/axios';

export default function CrearPrimerEmpleado() {
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  // Obtener datos de la empresa desde localStorage o location.state
  const empresaId = localStorage.getItem('empresaId') || location.state?.empresaId;
  const empresaNombre = localStorage.getItem('empresaNombre') || location.state?.empresaNombre;
  const tenantKey = localStorage.getItem('tenantKey') || location.state?.tenantKey;

  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    documento: '',
    telefono: '',
    email: '',
    usuario: '',
    password: '',
    confirmPassword: '',
    cargo: 'Administrador',
    rol: 'ADMIN' // ✅ ADMIN (no ADMINISTRADOR)
  });

  useEffect(() => {
    // Verificar que hay datos de empresa
    if (!tenantKey) {
      console.warn('⚠️ No hay tenantKey. Redirigiendo a login de empresa...');
      navigate('/login');
    }
  }, [tenantKey, navigate]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

  const validateForm = () => {
    if (!formData.nombre || !formData.apellido || !formData.documento || !formData.usuario || !formData.password) {
      setError('Por favor complete todos los campos obligatorios');
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return false;
    }

    if (formData.password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
      return false;
    }

    // Validar patrón de contraseña
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=]).*$/;
    if (!passwordPattern.test(formData.password)) {
      setError('La contraseña debe contener: mayúscula, minúscula, número y carácter especial (@#$%^&+=)');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      console.log('📤 Creando primer empleado...');
      
      const empleadoData = {
        nombre: formData.nombre,
        apellido: formData.apellido,
        documento: formData.documento,
        telefono: formData.telefono,
        email: formData.email,
        usuario: formData.usuario,
        password: formData.password,
        confirmPassword: formData.confirmPassword,
        cargo: formData.cargo || 'Administrador',
        rol: formData.rol || 'ADMIN' // ✅ ADMIN (no ADMINISTRADOR)
      };

      console.log('Datos a enviar:', { ...empleadoData, password: '***' });

      // Usar el token de la empresa que ya está en localStorage
      const tokenEmpresa = localStorage.getItem('tokenEmpresa');
      
      // ✅ ENDPOINT CORRECTO: /api/empresas/empleados
      const response = await api.post('/empresas/empleados', empleadoData, {
        headers: {
          'Authorization': `Bearer ${tokenEmpresa}`
        }
      });
      
      console.log('✅ Primer empleado creado:', response.data);

      setSuccess('¡Empleado creado exitosamente!');

      setTimeout(() => {
        navigate('/login-empleado', {
          state: {
            mensaje: '¡Primer empleado creado exitosamente! Inicia sesión para continuar.',
            empresaId,
            empresaNombre,
            tenantKey
          }
        });
      }, 2000);

    } catch (err) {
      console.error('❌ Error al crear empleado:', err);
      console.error('❌ Error response data:', err.response?.data);
      console.error('❌ Error response status:', err.response?.status);
      console.error('❌ Error response headers:', err.response?.headers);
      console.error('❌ Error message:', err.message);

      if (err.response?.status === 500) {
        setError(`Error del servidor: ${err.response?.data?.message || 'Error interno. Por favor revisa los logs del backend.'}`);
      } else if (err.response?.data?.mensaje) {
        setError(err.response.data.mensaje);
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.response?.status === 400) {
        setError('Datos inválidos. Por favor verifica el formulario.');
      } else if (err.response?.status === 401) {
        setError('No autorizado. Por favor vuelve a iniciar sesión como empresa.');
      } else if (err.message) {
        setError(err.message);
      } else {
        setError('Error al crear el empleado. Por favor intente nuevamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleVolver = () => {
    navigate('/bienvenida-empresa');
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
          onClick={handleVolver}
          sx={{
            mb: 3,
            color: 'rgba(255, 255, 255, 0.8)',
            '&:hover': {
              color: '#dda0dd'
            }
          }}
        >
          Volver a Bienvenida
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
            <PersonAddIcon
              sx={{
                fontSize: 60,
                color: '#4caf50',
                mb: 2
              }}
            />
            <Typography
              variant="h4"
              sx={{
                fontWeight: 'bold',
                background: 'linear-gradient(135deg, #4caf50 0%, #45a049 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                mb: 1
              }}
            >
              Crear Primer Empleado
            </Typography>
            <Typography
              variant="body1"
              sx={{ color: 'rgba(255, 255, 255, 0.7)', mb: 2 }}
            >
              Este será el usuario administrador de <strong>{empresaNombre}</strong>
            </Typography>
            <Box
              sx={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: 1,
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

          {/* Alerta informativa sobre rol ADMIN */}
          <Alert 
            severity="info" 
            sx={{ 
              mb: 3,
              background: 'rgba(33, 150, 243, 0.1)',
              border: '1px solid rgba(33, 150, 243, 0.3)',
              '& .MuiAlert-icon': { color: '#42a5f5' },
              '& .MuiAlert-message': { color: 'rgba(255, 255, 255, 0.9)' }
            }}
          >
            Este empleado será creado con rol de <strong>ADMINISTRADOR</strong> y tendrá acceso completo al sistema.
          </Alert>

          {/* Formulario */}
          <form onSubmit={handleSubmit}>
            <Grid container spacing={3}>
              {/* Información Personal */}
              <Grid item xs={12}>
                <Typography
                  variant="h6"
                  sx={{ color: '#4caf50', mb: 2, fontWeight: 'bold' }}
                >
                  Información Personal
                </Typography>
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Nombre"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <PersonIcon sx={{ color: '#4caf50' }} />
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
                        borderColor: '#4caf50'
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
                  label="Apellido"
                  name="apellido"
                  value={formData.apellido}
                  onChange={handleChange}
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: 'white',
                      '& fieldset': {
                        borderColor: 'rgba(147, 112, 219, 0.3)'
                      },
                      '&:hover fieldset': {
                        borderColor: '#4caf50'
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
                  label="Documento de Identidad"
                  name="documento"
                  value={formData.documento}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <BadgeIcon sx={{ color: '#2196f3' }} />
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
                        borderColor: '#4caf50'
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
                        borderColor: '#4caf50'
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    }
                  }}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email (Opcional)"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <EmailIcon sx={{ color: '#2196f3' }} />
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
                        borderColor: '#4caf50'
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
                <Divider sx={{ my: 2, borderColor: 'rgba(147, 112, 219, 0.2)' }} />
                <Typography
                  variant="h6"
                  sx={{ color: '#ff9800', mb: 2, fontWeight: 'bold' }}
                >
                  Credenciales de Acceso
                </Typography>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="Nombre de Usuario"
                  name="usuario"
                  value={formData.usuario}
                  onChange={handleChange}
                  helperText="Usarás este nombre para iniciar sesión"
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <PersonIcon sx={{ color: '#ff9800' }} />
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
                        borderColor: '#4caf50'
                      }
                    },
                    '& .MuiInputLabel-root': {
                      color: 'rgba(255, 255, 255, 0.7)'
                    },
                    '& .MuiFormHelperText-root': {
                      color: 'rgba(255, 255, 255, 0.5)'
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
                        borderColor: '#4caf50'
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
                        borderColor: '#4caf50'
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
                  * La contraseña debe tener mínimo 6 caracteres, incluir mayúscula, minúscula, número y carácter especial (@#$%^&+=)
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
                    background: 'linear-gradient(135deg, #4caf50 0%, #45a049 100%)',
                    '&:hover': {
                      background: 'linear-gradient(135deg, #45a049 0%, #3d8b40 100%)',
                      transform: 'translateY(-2px)',
                      boxShadow: '0 8px 25px rgba(76, 175, 80, 0.4)'
                    },
                    '&:disabled': {
                      background: 'rgba(76, 175, 80, 0.3)'
                    },
                    transition: 'all 0.3s ease'
                  }}
                >
                  {loading ? (
                    <CircularProgress size={24} sx={{ color: 'white' }} />
                  ) : (
                    'Crear Usuario Administrador'
                  )}
                </Button>
              </Grid>
            </Grid>
          </form>

          {/* Nota informativa */}
          <Box
            sx={{
              mt: 4,
              p: 2,
              background: 'rgba(76, 175, 80, 0.1)',
              border: '1px solid rgba(76, 175, 80, 0.3)',
              borderRadius: 1
            }}
          >
            <Typography
              variant="body2"
              sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
            >
              ℹ️ <strong>Información importante:</strong>
              <br />
              • Este será el usuario administrador con todos los permisos
              <br />
              • Podrás crear más empleados después de iniciar sesión
              <br />
              • Guarda bien tu nombre de usuario y contraseña
            </Typography>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}
