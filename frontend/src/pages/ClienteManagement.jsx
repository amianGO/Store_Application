import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import {
  Grid, Card, CardContent, Typography, CircularProgress, Box, 
  TextField, Button, InputAdornment, Chip, Dialog, DialogTitle, 
  DialogContent, DialogActions, MenuItem, Fab
} from '@mui/material';
import { 
  Users, Search, Plus, Edit, Trash2, Phone, Mail, 
  MapPin, Calendar, User 
} from 'lucide-react';
import axiosInstance from '../config/axios';

export default function ClienteManagement() {
  const [loading, setLoading] = useState(true);
  const [clientes, setClientes] = useState([]);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [editingCliente, setEditingCliente] = useState(null);
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    documento: '',
    telefono: '',
    email: '',
    direccion: '',
    tipoCliente: 'REGULAR'
  });

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) navigate('/login');
    fetchClientes();
  }, [navigate]);

  const fetchClientes = async () => {
    try {
      const response = await axiosInstance.get('/clientes');
      setClientes(response.data || []);
    } catch (error) {
      console.error("Error al cargar los clientes", error);
      setError('Error al cargar los clientes');
      setClientes([]);
    } finally {
      setLoading(false);
    }
  };

  const clientesFiltrados = Array.isArray(clientes) ? clientes.filter(cliente =>
    cliente.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cliente.apellido.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cliente.documento.includes(searchTerm) ||
    cliente.email.toLowerCase().includes(searchTerm.toLowerCase())
  ) : [];

  const handleOpenDialog = (cliente = null) => {
    if (cliente) {
      setEditingCliente(cliente);
      setFormData({
        nombre: cliente.nombre,
        apellido: cliente.apellido,
        documento: cliente.documento,
        telefono: cliente.telefono || '',
        email: cliente.email || '',
        direccion: cliente.direccion || '',
        tipoCliente: cliente.tipoCliente || 'REGULAR'
      });
    } else {
      setEditingCliente(null);
      setFormData({
        nombre: '',
        apellido: '',
        documento: '',
        telefono: '',
        email: '',
        direccion: '',
        tipoCliente: 'REGULAR'
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingCliente(null);
    setFormData({
      nombre: '',
      apellido: '',
      documento: '',
      telefono: '',
      email: '',
      direccion: '',
      tipoCliente: 'REGULAR'
    });
  };

  const handleSubmit = async () => {
    try {
      if (editingCliente) {
        await axiosInstance.put(`/clientes/${editingCliente.id}`, formData);
      } else {
        await axiosInstance.post('/clientes', formData);
      }
      fetchClientes();
      handleCloseDialog();
    } catch (error) {
      console.error('Error al guardar cliente:', error);
      alert('Error al guardar cliente');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Está seguro de eliminar este cliente?')) {
      try {
        await axiosInstance.delete(`/clientes/${id}`);
        fetchClientes();
      } catch (error) {
        console.error('Error al eliminar cliente:', error);
        alert('Error al eliminar cliente');
      }
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
            Gestión de Clientes
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
            Agregar Cliente
          </Button>
        </Box>

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
              placeholder="Buscar por nombre, documento o email..."
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
              label={`${clientesFiltrados.length} clientes`}
              sx={{
                background: 'rgba(147, 112, 219, 0.2)',
                color: '#dda0dd',
                fontWeight: 600,
                borderRadius: '8px'
              }}
            />
          </Box>
        </Card>

        {/* Grid de clientes */}
        {clientesFiltrados.length === 0 ? (
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
              No hay clientes disponibles
            </Typography>
          </Card>
        ) : (
          <Grid container spacing={3}>
            {clientesFiltrados.map((cliente) => (
              <Grid item xs={12} sm={6} md={4} key={cliente.id}>
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
                        {cliente.nombre} {cliente.apellido}
                      </Typography>
                      <Chip 
                        label={cliente.tipoCliente || 'REGULAR'}
                        size="small"
                        sx={{
                          background: 'rgba(76, 175, 80, 0.2)',
                          color: '#81c784',
                          fontWeight: 600,
                          fontSize: '0.75rem'
                        }}
                      />
                    </Box>

                    <Box sx={{ mb: 2 }}>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                        <User size={16} color="rgba(255,255,255,0.5)" />
                        <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.7)' }}>
                          {cliente.documento}
                        </Typography>
                      </Box>
                      
                      {cliente.telefono && (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                          <Phone size={16} color="rgba(255,255,255,0.5)" />
                          <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.7)' }}>
                            {cliente.telefono}
                          </Typography>
                        </Box>
                      )}
                      
                      {cliente.email && (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
                          <Mail size={16} color="rgba(255,255,255,0.5)" />
                          <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.7)' }}>
                            {cliente.email}
                          </Typography>
                        </Box>
                      )}
                      
                      {cliente.direccion && (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <MapPin size={16} color="rgba(255,255,255,0.5)" />
                          <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.7)' }}>
                            {cliente.direccion}
                          </Typography>
                        </Box>
                      )}
                    </Box>

                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
                      <Button
                        size="small"
                        startIcon={<Edit size={16} />}
                        onClick={() => handleOpenDialog(cliente)}
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
                        onClick={() => handleDelete(cliente.id)}
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

      {/* Dialog para crear/editar cliente */}
      <Dialog 
        open={openDialog} 
        onClose={handleCloseDialog}
        maxWidth="sm"
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
          {editingCliente ? 'Editar Cliente' : 'Agregar Cliente'}
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
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Dirección"
                multiline
                rows={2}
                value={formData.direccion}
                onChange={(e) => setFormData({...formData, direccion: e.target.value})}
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
            <Grid item xs={12}>
              <TextField
                fullWidth
                select
                label="Tipo de Cliente"
                value={formData.tipoCliente}
                onChange={(e) => setFormData({...formData, tipoCliente: e.target.value})}
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
                <MenuItem value="REGULAR">Regular</MenuItem>
                <MenuItem value="VIP">VIP</MenuItem>
                <MenuItem value="CORPORATIVO">Corporativo</MenuItem>
              </TextField>
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
            {editingCliente ? 'Actualizar' : 'Guardar'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
