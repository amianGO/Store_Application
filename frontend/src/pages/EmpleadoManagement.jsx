import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import api from '../config/axios';
import {
  Container,
  Typography,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  CircularProgress,
  Alert,
  TextField,
  InputAdornment,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  MenuItem,
  IconButton
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import PersonIcon from '@mui/icons-material/Person';
import AddIcon from '@mui/icons-material/Add';
import CloseIcon from '@mui/icons-material/Close';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';

export default function EmpleadoManagement() {
  console.log('🔥 EmpleadoManagement RENDERIZANDO');
  
  const navigate = useNavigate();
  const [empleados, setEmpleados] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [userRole, setUserRole] = useState(''); // Rol del usuario actual
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    documento: '',
    telefono: '',
    email: '',
    usuario: '',
    password: '',
    confirmPassword: '',
    cargo: '',
    rol: 'VENDEDOR'
  });
  const [formError, setFormError] = useState('');
  const [formSuccess, setFormSuccess] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    console.log('📌 useEffect: Cargando empleados...');
    
    // Obtener el rol del usuario desde el token
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        setUserRole(payload.rol || '');
        console.log('👤 Rol del usuario:', payload.rol);
      } catch (error) {
        console.error('Error al decodificar token:', error);
      }
    }
    
    cargarEmpleados();
  }, []);

  const cargarEmpleados = async () => {
    try {
      setLoading(true);
      setError('');
      
      console.log('📋 Cargando lista de empleados...');
      
      const response = await api.get('/empresas/empleados');
      
      console.log('✅ Empleados cargados:', response.data);
      
      if (response.data.success) {
        setEmpleados(response.data.empleados || []);
      } else {
        setEmpleados([]);
      }
      
    } catch (err) {
      console.error('❌ Error al cargar empleados:', err);
      setError('Error al cargar la lista de empleados');
      setEmpleados([]);
    } finally {
      setLoading(false);
    }
  };

  const empleadosFiltrados = empleados.filter(emp => 
    emp.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    emp.apellido?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    emp.usuario?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    emp.email?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getRolColor = (rol) => {
    const colores = {
      'ADMIN': 'error',
      'VENDEDOR': 'success',
      'CAJERO': 'warning',
      'INVENTARIO': 'info'
    };
    return colores[rol] || 'default';
  };

  const handleOpenDialog = () => {
    setOpenDialog(true);
    setFormError('');
    setFormSuccess('');
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setFormData({
      nombre: '',
      apellido: '',
      documento: '',
      telefono: '',
      email: '',
      usuario: '',
      password: '',
      confirmPassword: '',
      cargo: '',
      rol: 'VENDEDOR'
    });
    setFormError('');
    setFormSuccess('');
  };

  const handleFormChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setFormError('');
  };

  const validateForm = () => {
    if (!formData.nombre || !formData.apellido || !formData.documento || 
        !formData.usuario || !formData.password || !formData.cargo) {
      setFormError('Por favor complete todos los campos obligatorios');
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setFormError('Las contraseñas no coinciden');
      return false;
    }

    if (formData.password.length < 6) {
      setFormError('La contraseña debe tener al menos 6 caracteres');
      return false;
    }

    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=]).*$/;
    if (!passwordPattern.test(formData.password)) {
      setFormError('La contraseña debe contener: mayúscula, minúscula, número y carácter especial (@#$%^&+=)');
      return false;
    }

    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    setSubmitting(true);
    setFormError('');

    try {
      const response = await api.post('/empresas/empleados', formData);

      console.log('✅ Empleado creado:', response.data);
      setFormSuccess('¡Empleado creado exitosamente!');

      // Recargar lista después de 1 segundo
      setTimeout(() => {
        cargarEmpleados();
        handleCloseDialog();
      }, 1500);

    } catch (err) {
      console.error('❌ Error al crear empleado:', err);
      setFormError(err.response?.data?.message || 'Error al crear el empleado');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <Box sx={{ minHeight: '100vh', bgcolor: '#1e1e2f' }}>
        <Navbar />
        <Container sx={{ py: 4, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
          <CircularProgress />
        </Container>
      </Box>
    );
  }

  console.log('🎨 Renderizando return principal...');
  
  return (
    <Box sx={{ minHeight: '100vh', bgcolor: '#1e1e2f' }}>
      <Navbar />
      
      <Container maxWidth="xl" sx={{ py: 4, mt: 10 }}>
        {/* Header con título y botón */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
          <Typography variant="h4" sx={{ color: 'white', fontWeight: 'bold' }}>
            Gestión de Empleados
          </Typography>

          {/* Mostrar botón solo si el usuario es ADMIN */}
          {userRole === 'ADMIN' && (
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => {
                console.log('🟢 Botón Nuevo Empleado clickeado');
                handleOpenDialog();
              }}
              sx={{
                background: 'linear-gradient(135deg, #4caf50 0%, #45a049 100%)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #45a049 0%, #3d8b40 100%)',
                },
                px: 3,
                py: 1.5
              }}
            >
              Nuevo Empleado
            </Button>
          )}
        </Box>

        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        {/* Buscador */}
        <Box sx={{ mb: 3 }}>
          <TextField
            fullWidth
            placeholder="Buscar por nombre, usuario o email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon sx={{ color: 'rgba(255,255,255,0.5)' }} />
                </InputAdornment>
              ),
            }}
            sx={{
              '& .MuiOutlinedInput-root': {
                color: 'white',
                backgroundColor: 'rgba(255,255,255,0.05)',
                '& fieldset': {
                  borderColor: 'rgba(255,255,255,0.2)',
                },
                '&:hover fieldset': {
                  borderColor: 'rgba(255,255,255,0.3)',
                },
              },
            }}
          />
        </Box>

        {/* Tabla de empleados */}
        <TableContainer 
          component={Paper} 
          sx={{ 
            bgcolor: 'rgba(30, 30, 47, 0.95)',
            backdropFilter: 'blur(20px)',
          }}
        >
          <Table>
            <TableHead>
              <TableRow>
                <TableCell sx={{ color: '#dda0dd', fontWeight: 'bold' }}>Nombre</TableCell>
                <TableCell sx={{ color: '#dda0dd', fontWeight: 'bold' }}>Usuario</TableCell>
                <TableCell sx={{ color: '#dda0dd', fontWeight: 'bold' }}>Email</TableCell>
                <TableCell sx={{ color: '#dda0dd', fontWeight: 'bold' }}>Cargo</TableCell>
                <TableCell sx={{ color: '#dda0dd', fontWeight: 'bold' }}>Rol</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {empleadosFiltrados.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} align="center" sx={{ py: 8 }}>
                    <PersonIcon sx={{ fontSize: 80, color: 'rgba(255,255,255,0.3)', mb: 2 }} />
                    <Typography sx={{ color: 'rgba(255,255,255,0.5)' }}>
                      No se encontraron empleados
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                empleadosFiltrados.map((empleado) => (
                  <TableRow 
                    key={empleado.id}
                    sx={{ 
                      '&:hover': { 
                        bgcolor: 'rgba(255,255,255,0.05)' 
                      }
                    }}
                  >
                    <TableCell sx={{ color: 'white' }}>
                      {empleado.nombre} {empleado.apellido}
                    </TableCell>
                    <TableCell sx={{ color: 'rgba(255,255,255,0.7)' }}>
                      {empleado.usuario}
                    </TableCell>
                    <TableCell sx={{ color: 'rgba(255,255,255,0.7)' }}>
                      {empleado.email || '-'}
                    </TableCell>
                    <TableCell sx={{ color: 'rgba(255,255,255,0.7)' }}>
                      {empleado.cargo}
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={empleado.rol} 
                        color={getRolColor(empleado.rol)}
                        size="small"
                      />
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Contador */}
        <Box sx={{ mt: 2, textAlign: 'right' }}>
          <Typography sx={{ color: 'rgba(255,255,255,0.5)' }}>
            Total: {empleadosFiltrados.length} empleado(s)
          </Typography>
        </Box>

        {/* Diálogo para crear nuevo empleado */}
        <Dialog 
          open={openDialog} 
          onClose={handleCloseDialog}
          maxWidth="md"
          fullWidth
          PaperProps={{
            sx: {
              bgcolor: 'rgba(30, 30, 47, 0.98)',
              backdropFilter: 'blur(20px)',
            }
          }}
        >
          <DialogTitle sx={{ color: 'white', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6">Crear Nuevo Empleado</Typography>
            <IconButton onClick={handleCloseDialog} sx={{ color: 'white' }}>
              <CloseIcon />
            </IconButton>
          </DialogTitle>
          
          <DialogContent>
            {formError && (
              <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>
            )}
            {formSuccess && (
              <Alert severity="success" sx={{ mb: 2 }}>{formSuccess}</Alert>
            )}

            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Nombre"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
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
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
                  }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Documento"
                  name="documento"
                  value={formData.documento}
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
                  }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Teléfono"
                  name="telefono"
                  value={formData.telefono}
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
                  }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Usuario"
                  name="usuario"
                  value={formData.usuario}
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
                  }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  label="Cargo"
                  name="cargo"
                  value={formData.cargo}
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  select
                  label="Rol"
                  name="rol"
                  value={formData.rol}
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
                  }}
                >
                  <MenuItem value="ADMIN">Administrador</MenuItem>
                  <MenuItem value="VENDEDOR">Vendedor</MenuItem>
                  {/* <MenuItem value="CAJERO">Cajero</MenuItem> */}
                  {/* <MenuItem value="INVENTARIO">Inventario</MenuItem> */}
                </TextField>
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  required
                  type={showPassword ? 'text' : 'password'}
                  label="Contraseña"
                  name="password"
                  value={formData.password}
                  onChange={handleFormChange}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton onClick={() => setShowPassword(!showPassword)} sx={{ color: 'white' }}>
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
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
                  onChange={handleFormChange}
                  sx={{
                    '& .MuiOutlinedInput-root': { color: 'white', '& fieldset': { borderColor: 'rgba(255,255,255,0.3)' } },
                    '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' }
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.5)' }}>
                  * La contraseña debe tener mínimo 6 caracteres, incluir mayúscula, minúscula, número y carácter especial (@#$%^&+=)
                </Typography>
              </Grid>
            </Grid>
          </DialogContent>

          <DialogActions sx={{ px: 3, pb: 3 }}>
            <Button onClick={handleCloseDialog} sx={{ color: 'rgba(255,255,255,0.7)' }}>
              Cancelar
            </Button>
            <Button 
              onClick={handleSubmit} 
              variant="contained"
              disabled={submitting}
              sx={{
                background: 'linear-gradient(135deg, #4caf50 0%, #45a049 100%)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #45a049 0%, #3d8b40 100%)',
                }
              }}
            >
              {submitting ? <CircularProgress size={24} sx={{ color: 'white' }} /> : 'Crear Empleado'}
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
}
