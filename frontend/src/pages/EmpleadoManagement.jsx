import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import {
  Grid, Card, CardContent, Typography, CircularProgress, Box, 
  TextField, Button, InputAdornment, Chip, Dialog, DialogTitle, 
  DialogContent, DialogActions, MenuItem
} from '@mui/material';
import { 
  Users, Search, Plus, Edit, Trash2, Phone, Mail, 
  MapPin, User, Briefcase, Calendar
} from 'lucide-react';
import axiosInstance from '../config/axios';

export default function EmpleadoManagement() {
  const [loading, setLoading] = useState(true);
  const [empleados, setEmpleados] = useState([]);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [editingEmpleado, setEditingEmpleado] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    documento: '',
    usuario: '',
    password: '',
    telefono: '',
    email: '',
    cargo: 'VENDEDOR',
    rol: 'VENDEDOR'
  });

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) navigate('/login');
    fetchEmpleados();
  }, [navigate]);

  const fetchEmpleados = async () => {
    try {
      const response = await axiosInstance.get('/empleados');
      setEmpleados(response.data || []);
    } catch (error) {
      console.error("Error al cargar los empleados", error);
      setError('Error al cargar los empleados');
      setEmpleados([]);
    } finally {
      setLoading(false);
    }
  };

  const empleadosFiltrados = Array.isArray(empleados) ? empleados.filter(empleado =>
    empleado.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    empleado.apellido.toLowerCase().includes(searchTerm.toLowerCase()) ||
    empleado.documento.includes(searchTerm) ||
    (empleado.email && empleado.email.toLowerCase().includes(searchTerm.toLowerCase())) ||
    (empleado.cargo && empleado.cargo.toLowerCase().includes(searchTerm.toLowerCase()))
  ) : [];

  const handleOpenDialog = (empleado = null) => {
    if (empleado) {
      setEditingEmpleado(empleado);
      setFormData({
        nombre: empleado.nombre,
        apellido: empleado.apellido,
        documento: empleado.documento,
        usuario: empleado.usuario,
        password: '', // No mostrar password por seguridad
        telefono: empleado.telefono || '',
        email: empleado.email || '',
        cargo: empleado.cargo || 'VENDEDOR',
        rol: empleado.rol || 'VENDEDOR'
      });
    } else {
      setEditingEmpleado(null);
      setFormData({
        nombre: '',
        apellido: '',
        documento: '',
        usuario: '',
        password: '',
        telefono: '',
        email: '',
        cargo: 'VENDEDOR',
        rol: 'VENDEDOR'
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingEmpleado(null);
    setFormData({
      nombre: '',
      apellido: '',
      documento: '',
      usuario: '',
      password: '',
      telefono: '',
      email: '',
      cargo: 'VENDEDOR',
      rol: 'VENDEDOR'
    });
  };

  const handleSubmit = async () => {
    try {
      const dataToSend = { ...formData };
      
      // Si estamos editando y la contraseña está vacía, no la enviamos
      if (editingEmpleado && !formData.password) {
        delete dataToSend.password;
      }
      
      if (editingEmpleado) {
        await axiosInstance.put(`/empleados/${editingEmpleado.id}`, dataToSend);
      } else {
        await axiosInstance.post('/empleados', dataToSend);
      }
      fetchEmpleados();
      handleCloseDialog();
    } catch (error) {
      console.error('Error al guardar empleado:', error);
      alert('Error al guardar empleado: ' + (error.response?.data?.message || error.message));
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este empleado?')) {
      try {
        await axiosInstance.delete(`/empleados/${id}`);
        fetchEmpleados();
      } catch (error) {
        console.error('Error al eliminar empleado:', error);
        alert('Error al eliminar empleado');
      }
    }
  };

  const getCargoColor = (cargo) => {
    switch (cargo) {
      case 'GERENTE':
        return { bg: 'rgba(147, 112, 219, 0.2)', color: '#dda0dd' };
      case 'VENDEDOR':
        return { bg: 'rgba(76, 175, 80, 0.2)', color: '#81c784' };
      case 'CAJERO':
        return { bg: 'rgba(33, 150, 243, 0.2)', color: '#64b5f6' };
      case 'ADMINISTRADOR':
        return { bg: 'rgba(255, 152, 0, 0.2)', color: '#ffb74d' };
      default:
        return { bg: 'rgba(158, 158, 158, 0.2)', color: '#bdbdbd' };
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        width: '100vw',
        background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
        position: 'relative',
        overflow: 'auto',
        '&::before': {
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
        '@keyframes pulse': {
          '0%, 100%': { transform: 'scale(1)', opacity: 0.5 },
          '50%': { transform: 'scale(1.1)', opacity: 0.3 },
        },
      }}
    >
      <Navbar />
      <Box 
        sx={{ 
          padding: 4,
          paddingTop: '88px',
          position: 'relative',
          zIndex: 1,
          minHeight: '100vh'
        }}
      >
        {/* Header */}
        <Box 
          sx={{ 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            mb: 4,
            flexWrap: 'wrap',
            gap: 2
          }}
        >
          <Typography 
            variant='h4' 
            sx={{ 
              fontWeight: 700,
              background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text'
            }}
          >
            Gestión de Empleados
          </Typography>

          <Button
            variant="contained"
            startIcon={<Plus size={20} />}
            onClick={() => handleOpenDialog()}
            sx={{
              background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
              borderRadius: '12px',
              textTransform: 'none',
              fontWeight: 600,
              px: 3,
              boxShadow: '0 4px 20px rgba(147, 112, 219, 0.4)',
              '&:hover': {
                background: 'linear-gradient(135deg, #a280e0 0%, #7b6bd4 100%)',
                transform: 'translateY(-2px)',
                boxShadow: '0 6px 28px rgba(147, 112, 219, 0.6)',
              },
              transition: 'all 0.3s ease'
            }}
          >
            Agregar Empleado
          </Button>
        </Box>

        {/* Estadísticas */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card 
              sx={{ 
                background: 'rgba(147, 112, 219, 0.15)',
                backdropFilter: 'blur(10px)',
                borderRadius: '16px',
                border: '1px solid rgba(147, 112, 219, 0.3)',
                transition: 'transform 0.3s ease',
                '&:hover': { transform: 'translateY(-5px)' }
              }}
            >
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Box 
                    sx={{ 
                      p: 1.5, 
                      borderRadius: '12px', 
                      background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)'
                    }}
                  >
                    <Users size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Total Empleados
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {empleados.length}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card 
              sx={{ 
                background: 'rgba(76, 175, 80, 0.15)',
                backdropFilter: 'blur(10px)',
                borderRadius: '16px',
                border: '1px solid rgba(76, 175, 80, 0.3)',
                transition: 'transform 0.3s ease',
                '&:hover': { transform: 'translateY(-5px)' }
              }}
            >
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Box 
                    sx={{ 
                      p: 1.5, 
                      borderRadius: '12px', 
                      background: 'linear-gradient(135deg, #66bb6a 0%, #4caf50 100%)'
                    }}
                  >
                    <Briefcase size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Vendedores
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {empleados.filter(e => e.cargo === 'VENDEDOR').length}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card 
              sx={{ 
                background: 'rgba(255, 152, 0, 0.15)',
                backdropFilter: 'blur(10px)',
                borderRadius: '16px',
                border: '1px solid rgba(255, 152, 0, 0.3)',
                transition: 'transform 0.3s ease',
                '&:hover': { transform: 'translateY(-5px)' }
              }}
            >
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Box 
                    sx={{ 
                      p: 1.5, 
                      borderRadius: '12px', 
                      background: 'linear-gradient(135deg, #ffa726 0%, #ff9800 100%)'
                    }}
                  >
                    <User size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Gerentes
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {empleados.filter(e => e.cargo === 'GERENTE').length}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card 
              sx={{ 
                background: 'rgba(33, 150, 243, 0.15)',
                backdropFilter: 'blur(10px)',
                borderRadius: '16px',
                border: '1px solid rgba(33, 150, 243, 0.3)',
                transition: 'transform 0.3s ease',
                '&:hover': { transform: 'translateY(-5px)' }
              }}
            >
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Box 
                    sx={{ 
                      p: 1.5, 
                      borderRadius: '12px', 
                      background: 'linear-gradient(135deg, #42a5f5 0%, #2196f3 100%)'
                    }}
                  >
                    <Calendar size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Cajeros
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {empleados.filter(e => e.cargo === 'CAJERO').length}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Barra de búsqueda */}
        <Card 
          sx={{ 
            background: 'rgba(255, 255, 255, 0.05)',
            backdropFilter: 'blur(20px)',
            borderRadius: '16px',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            mb: 3,
            p: 3
          }}
        >
          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
            <TextField
              placeholder="Buscar por nombre, documento, email o cargo..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              sx={{
                flex: 1,
                '& .MuiOutlinedInput-root': {
                  background: 'rgba(255, 255, 255, 0.05)',
                  borderRadius: '12px',
                  '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                  '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                  '&.Mui-focused fieldset': { borderColor: '#9370db', borderWidth: '2px' },
                },
                '& .MuiOutlinedInput-input': { color: '#fff' },
              }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search size={20} color="rgba(255,255,255,0.5)" />
                  </InputAdornment>
                ),
              }}
            />
            
            <Chip 
              label={`${empleadosFiltrados.length} empleados`}
              sx={{
                background: 'rgba(147, 112, 219, 0.2)',
                color: '#dda0dd',
                fontWeight: 600,
                borderRadius: '8px'
              }}
            />
          </Box>
        </Card>

        {/* Grid de empleados */}
        {empleadosFiltrados.length === 0 ? (
          <Card 
            sx={{ 
              background: 'rgba(255, 255, 255, 0.05)',
              backdropFilter: 'blur(20px)',
              borderRadius: '16px',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              p: 4,
              textAlign: 'center'
            }}
          >
            <Users size={64} color="rgba(255,255,255,0.3)" style={{ margin: '0 auto' }} />
            <Typography variant="h6" sx={{ color: 'rgba(255,255,255,0.6)', mt: 2 }}>
              No hay empleados disponibles
            </Typography>
          </Card>
        ) : (
          <Grid container spacing={3}>
            {empleadosFiltrados.map((empleado) => (
              <Grid item xs={12} sm={6} md={4} key={empleado.id}>
                <Card 
                  sx={{
                    background: 'rgba(255, 255, 255, 0.05)',
                    backdropFilter: 'blur(20px)',
                    borderRadius: '16px',
                    border: '1px solid rgba(255, 255, 255, 0.1)',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      transform: 'translateY(-8px)',
                      boxShadow: '0 12px 48px rgba(147, 112, 219, 0.3)',
                      border: '1px solid rgba(147, 112, 219, 0.4)'
                    }
                  }}
                >
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 2 }}>
                      <Typography 
                        variant='h6' 
                        sx={{ 
                          fontWeight: 700, 
                          color: '#fff',
                          fontSize: '1.1rem'
                        }}
                      >
                        {empleado.nombre} {empleado.apellido}
                      </Typography>
                      <Chip 
                        label={empleado.cargo || 'VENDEDOR'}
                        size="small"
                        sx={{
                          background: getCargoColor(empleado.cargo).bg,
                          color: getCargoColor(empleado.cargo).color,
                          fontWeight: 600,
                          fontSize: '0.75rem'
                        }}
                      />
                    </Box>

                    <Box sx={{ mb: 2 }}>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                        <User size={16} color="rgba(255,255,255,0.5)" />
                        <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.7)' }}>
                          {empleado.documento}
                        </Typography>
                      </Box>
                      
                      {empleado.telefono && (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                          <Phone size={16} color="rgba(255,255,255,0.5)" />
                          <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.7)' }}>
                            {empleado.telefono}
                          </Typography>
                        </Box>
                      )}
                      
                      {empleado.email && (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                          <Mail size={16} color="rgba(255,255,255,0.5)" />
                          <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.7)' }}>
                            {empleado.email}
                          </Typography>
                        </Box>
                      )}
                      
                      {empleado.direccion && (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <MapPin size={16} color="rgba(255,255,255,0.5)" />
                          <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.7)' }}>
                            {empleado.direccion}
                          </Typography>
                        </Box>
                      )}
                    </Box>

                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
                      <Button
                        size="small"
                        startIcon={<Edit size={16} />}
                        onClick={() => handleOpenDialog(empleado)}
                        sx={{
                          color: '#9370db',
                          '&:hover': { backgroundColor: 'rgba(147, 112, 219, 0.1)' }
                        }}
                      >
                        Editar
                      </Button>
                      <Button
                        size="small"
                        startIcon={<Trash2 size={16} />}
                        onClick={() => handleDelete(empleado.id)}
                        sx={{
                          color: '#f44336',
                          '&:hover': { backgroundColor: 'rgba(244, 67, 54, 0.1)' }
                        }}
                      >
                        Eliminar
                      </Button>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>

      {/* Dialog para crear/editar empleado */}
      <Dialog 
        open={openDialog} 
        onClose={handleCloseDialog}
        maxWidth="md"
        fullWidth
        PaperProps={{
          sx: {
            background: 'rgba(26, 26, 46, 0.95)',
            backdropFilter: 'blur(20px)',
            borderRadius: '16px',
            border: '1px solid rgba(147, 112, 219, 0.3)',
          }
        }}
      >
        <DialogTitle sx={{ color: '#fff', fontWeight: 700 }}>
          {editingEmpleado ? 'Editar Empleado' : 'Agregar Empleado'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Nombre"
                value={formData.nombre}
                onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                required
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiOutlinedInput-input': { color: '#fff' },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Apellido"
                value={formData.apellido}
                onChange={(e) => setFormData({...formData, apellido: e.target.value})}
                required
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiOutlinedInput-input': { color: '#fff' },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Documento"
                value={formData.documento}
                onChange={(e) => setFormData({...formData, documento: e.target.value})}
                required
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiOutlinedInput-input': { color: '#fff' },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Cargo"
                value={formData.cargo}
                onChange={(e) => setFormData({...formData, cargo: e.target.value})}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiSelect-select': { color: '#fff' },
                  '& .MuiSvgIcon-root': { color: 'rgba(255, 255, 255, 0.5)' },
                }}
              >
                <MenuItem value="VENDEDOR">Vendedor</MenuItem>
                <MenuItem value="CAJERO">Cajero</MenuItem>
                <MenuItem value="GERENTE">Gerente</MenuItem>
                <MenuItem value="ADMINISTRADOR">Administrador</MenuItem>
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Teléfono"
                value={formData.telefono}
                onChange={(e) => setFormData({...formData, telefono: e.target.value})}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiOutlinedInput-input': { color: '#fff' },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Usuario"
                value={formData.usuario}
                onChange={(e) => setFormData({...formData, usuario: e.target.value})}
                required
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiOutlinedInput-input': { color: '#fff' },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Contraseña"
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                required={!editingEmpleado}
                placeholder={editingEmpleado ? "Dejar vacío para mantener actual" : ""}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiOutlinedInput-input': { color: '#fff' },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Rol"
                value={formData.rol}
                onChange={(e) => setFormData({...formData, rol: e.target.value})}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiSelect-select': { color: '#fff' },
                  '& .MuiSvgIcon-root': { color: 'rgba(255, 255, 255, 0.5)' },
                }}
              >
                <MenuItem value="VENDEDOR">Vendedor</MenuItem>
                <MenuItem value="CAJERO">Cajero</MenuItem>
                <MenuItem value="INVENTARIO">Inventario</MenuItem>
                <MenuItem value="ADMIN">Administrador</MenuItem>
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    background: 'rgba(255, 255, 255, 0.05)',
                    '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                    '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                    '&.Mui-focused fieldset': { borderColor: '#9370db' },
                  },
                  '& .MuiInputLabel-root': { color: 'rgba(255,255,255,0.7)' },
                  '& .MuiOutlinedInput-input': { color: '#fff' },
                }}
              />
            </Grid>

          </Grid>
        </DialogContent>
        <DialogActions sx={{ p: 3 }}>
          <Button 
            onClick={handleCloseDialog}
            sx={{ color: 'rgba(255,255,255,0.7)' }}
          >
            Cancelar
          </Button>
          <Button 
            onClick={handleSubmit}
            variant="contained"
            sx={{
              background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
              '&:hover': {
                background: 'linear-gradient(135deg, #a280e0 0%, #7b6bd4 100%)',
              }
            }}
          >
            {editingEmpleado ? 'Actualizar' : 'Guardar'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
