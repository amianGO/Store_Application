import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Typography,
  Container,
  Alert,
  CircularProgress,
  Card,
  CardContent,
  Grid,
  Chip,
  Divider,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions
} from '@mui/material';
import {
  ArrowLeft,
  Edit,
  Package,
  DollarSign,
  ShoppingCart,
  TrendingDown,
  Tag,
  Hash,
  FileText,
  Calendar,
  Trash2,
  AlertTriangle
} from 'lucide-react';
import axiosInstance from '../../config/axios';
import Navbar from '../../components/Navbar';
import { formatCOP } from '../../utils/formatters';

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [producto, setProducto] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const fetchProducto = async () => {
      try {
        const response = await axiosInstance.get(`/productos/${id}`);
        setProducto(response.data);
      } catch (error) {
        setError('Error al cargar el producto');
        console.error('Error:', error);
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchProducto();
    }
  }, [id]);

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await axiosInstance.delete(`/productos/${id}`);
      navigate('/dashboard');
    } catch (error) {
      setError('Error al eliminar el producto');
      console.error('Error:', error);
    } finally {
      setDeleting(false);
      setDeleteDialogOpen(false);
    }
  };

  if (loading) {
    return (
      <Box
        sx={{
          minHeight: '100vh',
          width: '100vw',
          background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}
      >
        <CircularProgress sx={{ color: '#9370db' }} size={60} />
      </Box>
    );
  }

  if (error || !producto) {
    return (
      <Box
        sx={{
          minHeight: '100vh',
          width: '100vw',
          background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
          position: 'relative'
        }}
      >
        <Navbar />
        <Container maxWidth="md" sx={{ py: 4, paddingTop: '104px' }}>
          <Alert severity="error" sx={{ mb: 2 }}>
            {error || 'Producto no encontrado'}
          </Alert>
          <Button
            startIcon={<ArrowLeft size={20} />}
            onClick={() => navigate('/dashboard')}
            sx={{ color: 'rgba(255, 255, 255, 0.7)' }}
          >
            Volver al Dashboard
          </Button>
        </Container>
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
      
      <Container maxWidth="lg" sx={{ py: 4, paddingTop: '104px', position: 'relative', zIndex: 1 }}>
        {/* Header */}
        <Box sx={{ mb: 4 }}>
          <Button
            startIcon={<ArrowLeft size={20} />}
            onClick={() => navigate('/dashboard')}
            sx={{
              color: 'rgba(255, 255, 255, 0.7)',
              mb: 2,
              '&:hover': {
                color: '#dda0dd',
                background: 'rgba(147, 112, 219, 0.1)'
              }
            }}
          >
            Volver al Dashboard
          </Button>
          
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
            <Box>
              <Typography
                variant="h4"
                sx={{
                  fontWeight: 700,
                  background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text',
                  mb: 1
                }}
              >
                {producto.nombre}
              </Typography>
              
              <Typography
                variant="body1"
                sx={{
                  color: 'rgba(255, 255, 255, 0.6)',
                  display: 'flex',
                  alignItems: 'center',
                  gap: 1
                }}
              >
                <Hash size={16} />
                {producto.codigo}
              </Typography>
            </Box>

            <Box sx={{ display: 'flex', gap: 2 }}>
              <Button
                variant="contained"
                startIcon={<Edit size={20} />}
                onClick={() => navigate(`/productos/edit/${producto.id}`)}
                sx={{
                  background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
                  borderRadius: '12px',
                  textTransform: 'none',
                  fontWeight: 600,
                  px: 3,
                  py: 1.5,
                  boxShadow: '0 4px 20px rgba(147, 112, 219, 0.4)',
                  '&:hover': {
                    background: 'linear-gradient(135deg, #a280e0 0%, #7b6bd4 100%)',
                    transform: 'translateY(-2px)',
                    boxShadow: '0 6px 28px rgba(147, 112, 219, 0.6)',
                  },
                  transition: 'all 0.3s ease'
                }}
              >
                Editar Producto
              </Button>

              <Button
                variant="outlined"
                startIcon={<Trash2 size={20} />}
                onClick={() => setDeleteDialogOpen(true)}
                sx={{
                  borderColor: 'rgba(244, 67, 54, 0.5)',
                  color: '#ffcdd2',
                  borderRadius: '12px',
                  textTransform: 'none',
                  fontWeight: 600,
                  px: 3,
                  py: 1.5,
                  '&:hover': {
                    borderColor: '#f44336',
                    background: 'rgba(244, 67, 54, 0.1)',
                    transform: 'translateY(-2px)',
                  },
                  transition: 'all 0.3s ease'
                }}
              >
                Eliminar
              </Button>
            </Box>
          </Box>
        </Box>

        {/* Content */}
        <Grid container spacing={4}>
          {/* Información Principal */}
          <Grid item xs={12} md={8}>
            <Card
              sx={{
                background: 'rgba(255, 255, 255, 0.05)',
                backdropFilter: 'blur(20px)',
                borderRadius: '20px',
                border: '1px solid rgba(255, 255, 255, 0.1)',
                boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
                transition: 'transform 0.3s ease',
                '&:hover': { transform: 'translateY(-5px)' }
              }}
            >
              <CardContent sx={{ p: 4 }}>
                {/* Estado */}
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                  <Typography variant="h6" sx={{ color: '#fff', fontWeight: 600 }}>
                    Información General
                  </Typography>
                  <Chip
                    label={producto.estadoActivo ? 'Activo' : 'Inactivo'}
                    sx={{
                      background: producto.estadoActivo 
                        ? 'rgba(76, 175, 80, 0.2)' 
                        : 'rgba(244, 67, 54, 0.2)',
                      color: producto.estadoActivo ? '#81c784' : '#e57373',
                      fontWeight: 600,
                      border: `1px solid ${producto.estadoActivo ? 'rgba(76, 175, 80, 0.3)' : 'rgba(244, 67, 54, 0.3)'}`
                    }}
                  />
                </Box>

                <Divider sx={{ borderColor: 'rgba(255, 255, 255, 0.1)', mb: 3 }} />

                {/* Descripción */}
                <Box sx={{ mb: 4 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                    <FileText size={20} color="#9370db" />
                    <Typography variant="h6" sx={{ color: '#dda0dd', fontWeight: 600 }}>
                      Descripción
                    </Typography>
                  </Box>
                  <Typography
                    variant="body1"
                    sx={{
                      color: 'rgba(255, 255, 255, 0.8)',
                      backgroundColor: 'rgba(255, 255, 255, 0.02)',
                      padding: 2,
                      borderRadius: '12px',
                      border: '1px solid rgba(255, 255, 255, 0.05)',
                      lineHeight: 1.6
                    }}
                  >
                    {producto.descripcion}
                  </Typography>
                </Box>

                {/* Categoría */}
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Tag size={20} color="#9370db" />
                  <Typography variant="body1" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                    Categoría:
                  </Typography>
                  <Chip
                    label={producto.categoria}
                    sx={{
                      background: 'rgba(147, 112, 219, 0.2)',
                      color: '#dda0dd',
                      fontWeight: 600,
                      border: '1px solid rgba(147, 112, 219, 0.3)'
                    }}
                  />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          {/* Información Financiera y Stock */}
          <Grid item xs={12} md={4}>
            <Grid container spacing={3}>
              {/* Precios */}
              <Grid item xs={12}>
                <Card
                  sx={{
                    background: 'rgba(33, 150, 243, 0.15)',
                    backdropFilter: 'blur(20px)',
                    borderRadius: '16px',
                    border: '1px solid rgba(33, 150, 243, 0.3)',
                    transition: 'transform 0.3s ease',
                    '&:hover': { transform: 'translateY(-5px)' }
                  }}
                >
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                      <Box 
                        sx={{ 
                          p: 1.5, 
                          borderRadius: '12px', 
                          background: 'linear-gradient(135deg, #42a5f5 0%, #2196f3 100%)'
                        }}
                      >
                        <DollarSign size={24} color="#fff" />
                      </Box>
                      <Typography variant="h6" sx={{ color: '#fff', fontWeight: 600 }}>
                        Precios
                      </Typography>
                    </Box>
                    
                    <Box sx={{ mb: 2 }}>
                      <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                        Precio de Compra
                      </Typography>
                      <Typography variant="h6" sx={{ color: '#fff', fontWeight: 600 }}>
                        {formatCOP(producto.precioCompra)}
                      </Typography>
                    </Box>
                    
                    <Box>
                      <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                        Precio de Venta
                      </Typography>
                      <Typography variant="h6" sx={{ color: '#42a5f5', fontWeight: 600 }}>
                        {formatCOP(producto.precioVenta)}
                      </Typography>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>

              {/* Stock Actual */}
              <Grid item xs={12}>
                <Card
                  sx={{
                    background: producto.stock <= producto.stockMinimo 
                      ? 'rgba(255, 152, 0, 0.15)' 
                      : 'rgba(76, 175, 80, 0.15)',
                    backdropFilter: 'blur(20px)',
                    borderRadius: '16px',
                    border: `1px solid ${producto.stock <= producto.stockMinimo 
                      ? 'rgba(255, 152, 0, 0.3)' 
                      : 'rgba(76, 175, 80, 0.3)'}`,
                    transition: 'transform 0.3s ease',
                    '&:hover': { transform: 'translateY(-5px)' }
                  }}
                >
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                      <Box 
                        sx={{ 
                          p: 1.5, 
                          borderRadius: '12px', 
                          background: producto.stock <= producto.stockMinimo 
                            ? 'linear-gradient(135deg, #ffa726 0%, #ff9800 100%)'
                            : 'linear-gradient(135deg, #66bb6a 0%, #4caf50 100%)'
                        }}
                      >
                        <ShoppingCart size={24} color="#fff" />
                      </Box>
                      <Typography variant="h6" sx={{ color: '#fff', fontWeight: 600 }}>
                        Stock
                      </Typography>
                    </Box>
                    
                    <Box sx={{ mb: 2 }}>
                      <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                        Stock Actual
                      </Typography>
                      <Typography 
                        variant="h5" 
                        sx={{ 
                          color: producto.stock <= producto.stockMinimo ? '#ff9800' : '#4caf50',
                          fontWeight: 700
                        }}
                      >
                        {producto.stock} unidades
                      </Typography>
                    </Box>
                    
                    <Box>
                      <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                        Stock Mínimo
                      </Typography>
                      <Typography variant="body1" sx={{ color: 'rgba(255, 255, 255, 0.8)', fontWeight: 600 }}>
                        {producto.stockMinimo} unidades
                      </Typography>
                    </Box>

                    {producto.stock <= producto.stockMinimo && (
                      <Alert 
                        severity="warning" 
                        sx={{ 
                          mt: 2, 
                          background: 'rgba(255, 152, 0, 0.1)',
                          border: '1px solid rgba(255, 152, 0, 0.3)',
                          color: '#ffcc02',
                          '& .MuiAlert-icon': { color: '#ff9800' }
                        }}
                      >
                        Stock bajo
                      </Alert>
                    )}
                  </CardContent>
                </Card>
              </Grid>

              {/* Valor de Inventario */}
              <Grid item xs={12}>
                <Card
                  sx={{
                    background: 'rgba(147, 112, 219, 0.15)',
                    backdropFilter: 'blur(20px)',
                    borderRadius: '16px',
                    border: '1px solid rgba(147, 112, 219, 0.3)',
                    transition: 'transform 0.3s ease',
                    '&:hover': { transform: 'translateY(-5px)' }
                  }}
                >
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                      <Box 
                        sx={{ 
                          p: 1.5, 
                          borderRadius: '12px', 
                          background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)'
                        }}
                      >
                        <Package size={24} color="#fff" />
                      </Box>
                      <Typography variant="h6" sx={{ color: '#fff', fontWeight: 600 }}>
                        Valor Total
                      </Typography>
                    </Box>
                    
                    <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)', mb: 1 }}>
                      Valor en Inventario
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#dda0dd', fontWeight: 700 }}>
                      {formatCOP(producto.precioVenta * producto.stock)}
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>
          </Grid>
        </Grid>

        {/* Diálogo de confirmación de eliminación */}
        <Dialog
          open={deleteDialogOpen}
          onClose={() => setDeleteDialogOpen(false)}
          maxWidth="sm"
          fullWidth
          PaperProps={{
            sx: {
              background: 'rgba(255, 255, 255, 0.05)',
              backdropFilter: 'blur(20px)',
              borderRadius: '20px',
              border: '1px solid rgba(255, 255, 255, 0.1)',
            }
          }}
        >
          <DialogTitle
            sx={{
              color: '#fff',
              display: 'flex',
              alignItems: 'center',
              gap: 2,
              borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
              pb: 2
            }}
          >
            <AlertTriangle size={24} color="#f44336" />
            Confirmar Eliminación
          </DialogTitle>
          
          <DialogContent sx={{ py: 3 }}>
            <Typography sx={{ color: 'rgba(255, 255, 255, 0.8)', mb: 2 }}>
              ¿Estás seguro de que deseas eliminar este producto?
            </Typography>
            <Typography sx={{ color: 'rgba(255, 255, 255, 0.6)', fontSize: '0.9rem', mb: 2 }}>
              <strong style={{ color: '#dda0dd' }}>{producto?.nombre}</strong> será eliminado permanentemente de la base de datos.
            </Typography>
            <Typography sx={{ color: '#f44336', fontSize: '0.85rem', fontWeight: 600 }}>
              ⚠️ Esta acción no se puede deshacer.
            </Typography>
          </DialogContent>
          
          <DialogActions sx={{ p: 3, pt: 0 }}>
            <Button
              onClick={() => setDeleteDialogOpen(false)}
              sx={{
                color: 'rgba(255, 255, 255, 0.7)',
                borderColor: 'rgba(255, 255, 255, 0.3)',
                '&:hover': {
                  borderColor: 'rgba(255, 255, 255, 0.5)',
                  background: 'rgba(255, 255, 255, 0.05)'
                }
              }}
              variant="outlined"
            >
              Cancelar
            </Button>
            <Button
              onClick={handleDelete}
              disabled={deleting}
              startIcon={deleting ? <CircularProgress size={16} /> : <Trash2 size={16} />}
              sx={{
                background: 'linear-gradient(135deg, #f44336 0%, #d32f2f 100%)',
                color: '#fff',
                '&:hover': {
                  background: 'linear-gradient(135deg, #f66459 0%, #e53935 100%)',
                },
                '&:disabled': {
                  background: 'rgba(244, 67, 54, 0.3)'
                }
              }}
              variant="contained"
            >
              {deleting ? 'Eliminando...' : 'Confirmar'}
            </Button>
          </DialogActions>
        </Dialog>
      </Container>
    </Box>
  );
}
