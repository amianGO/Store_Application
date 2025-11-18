import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  TextField,
  Typography,
  Container,
  Alert,
  CircularProgress,
  MenuItem,
  Grid,
  Card,
  CardContent,
  InputAdornment,
  FormControlLabel,
  Switch
} from '@mui/material';
import {
  Package,
  DollarSign,
  Hash,
  FileText,
  ShoppingCart,
  TrendingDown,
  Tag,
  ArrowLeft,
  Save,
  Power
} from 'lucide-react';
import axiosInstance from '../../config/axios';
import Navbar from '../../components/Navbar';

const categorias = [
  'ELECTRONICA',
  'ROPA',
  'HOGAR',
  'DEPORTES',
  'LIBROS',
  'JUGUETES',
  'BELLEZA',
  'ALIMENTOS',
  'OTROS'
];

export default function EditProduct() {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    codigo: '',
    nombre: '',
    descripcion: '',
    precioCompra: '',
    precioVenta: '',
    stock: '',
    stockMinimo: '',
    categoria: '',
    estadoActivo: true
  });
  const [loading, setLoading] = useState(false);
  const [loadingProduct, setLoadingProduct] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Cargar datos del producto
  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await axiosInstance.get(`/productos/${id}`);
        const producto = response.data;
        
        setFormData({
          codigo: producto.codigo || '',
          nombre: producto.nombre || '',
          descripcion: producto.descripcion || '',
          precioCompra: producto.precioCompra?.toString() || '',
          precioVenta: producto.precioVenta?.toString() || '',
          stock: producto.stock?.toString() || '',
          stockMinimo: producto.stockMinimo?.toString() || '',
          categoria: producto.categoria || '',
          estadoActivo: producto.estadoActivo !== undefined ? producto.estadoActivo : true
        });
      } catch (err) {
        setError('Error al cargar el producto');
        console.error('Error:', err);
      } finally {
        setLoadingProduct(false);
      }
    };

    if (id) {
      fetchProduct();
    }
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      // Convertir los valores numéricos
      const productData = {
        ...formData,
        precioCompra: parseFloat(formData.precioCompra),
        precioVenta: parseFloat(formData.precioVenta),
        stock: parseInt(formData.stock),
        stockMinimo: parseInt(formData.stockMinimo)
      };

      console.log('Datos enviados al backend:', productData);
      await axiosInstance.put(`/productos/${id}`, productData);
      setSuccess('Producto actualizado exitosamente');
      
      // Redirigir después de 2 segundos
      setTimeout(() => {
        navigate(`/productos/detail/${id}`);
      }, 2000);

    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al actualizar el producto');
    } finally {
      setLoading(false);
    }
  };

  if (loadingProduct) {
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
      
      <Container maxWidth="md" sx={{ py: 4, paddingTop: '104px', position: 'relative', zIndex: 1 }}>
        {/* Header */}
        <Box sx={{ mb: 4 }}>
          <Button
            startIcon={<ArrowLeft size={20} />}
            onClick={() => navigate(`/productos/detail/${id}`)}
            sx={{
              color: 'rgba(255, 255, 255, 0.7)',
              mb: 2,
              '&:hover': {
                color: '#dda0dd',
                background: 'rgba(147, 112, 219, 0.1)'
              }
            }}
          >
            Volver al Detalle
          </Button>
          
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
            Editar Producto
          </Typography>
          
          <Typography
            variant="body1"
            sx={{
              color: 'rgba(255, 255, 255, 0.6)'
            }}
          >
            Modifica la información del producto
          </Typography>
        </Box>

        {/* Formulario */}
        <Card
          sx={{
            background: 'rgba(255, 255, 255, 0.05)',
            backdropFilter: 'blur(20px)',
            borderRadius: '24px',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
            transition: 'transform 0.3s ease, box-shadow 0.3s ease',
            '&:hover': {
              transform: 'translateY(-5px)',
              boxShadow: '0 12px 48px rgba(147, 112, 219, 0.2)'
            }
          }}
        >
          <CardContent sx={{ p: 4 }}>
            {/* Logo/Icono */}
            <Box
              sx={{
                display: 'flex',
                justifyContent: 'center',
                mb: 3
              }}
            >
              <Box
                sx={{
                  width: 80,
                  height: 80,
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg, #ff9800 0%, #f57c00 100%)', // Color naranja para edición
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  boxShadow: '0 8px 24px rgba(255, 152, 0, 0.4)',
                  animation: 'float 3s ease-in-out infinite',
                  '@keyframes float': {
                    '0%, 100%': { transform: 'translateY(0px)' },
                    '50%': { transform: 'translateY(-10px)' },
                  }
                }}
              >
                <Package size={40} color="#fff" />
              </Box>
            </Box>

            {/* Alertas */}
            {error && (
              <Alert
                severity="error"
                sx={{
                  mb: 3,
                  background: 'rgba(244, 67, 54, 0.1)',
                  backdropFilter: 'blur(10px)',
                  border: '1px solid rgba(244, 67, 54, 0.3)',
                  color: '#ffcdd2'
                }}
              >
                {error}
              </Alert>
            )}

            {success && (
              <Alert
                severity="success"
                sx={{
                  mb: 3,
                  background: 'rgba(76, 175, 80, 0.1)',
                  backdropFilter: 'blur(10px)',
                  border: '1px solid rgba(76, 175, 80, 0.3)',
                  color: '#c8e6c9'
                }}
              >
                {success}
              </Alert>
            )}

            {/* Formulario */}
            <Box component="form" onSubmit={handleSubmit}>
              <Grid container spacing={3}>
                {/* Código */}
                <Grid item xs={12} sm={6}>
                  <TextField
                    required
                    fullWidth
                    name="codigo"
                    label="Código del Producto"
                    value={formData.codigo}
                    disabled
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Hash size={20} color="rgba(255,255,255,0.5)" />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        background: 'rgba(255, 255, 255, 0.03)',
                        borderRadius: '12px',
                        transition: 'all 0.3s ease',
                        '& fieldset': {
                          borderColor: 'rgba(255, 255, 255, 0.1)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(147, 112, 219, 0.3)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiOutlinedInput-input': {
                        color: 'rgba(255, 255, 255, 0.7)',
                      },
                    }}
                  />
                </Grid>

                {/* Nombre */}
                <Grid item xs={12} sm={6}>
                  <TextField
                    required
                    fullWidth
                    name="nombre"
                    label="Nombre del Producto"
                    value={formData.nombre}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Package size={20} color="rgba(255,255,255,0.5)" />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        background: 'rgba(255, 255, 255, 0.05)',
                        borderRadius: '12px',
                        transition: 'all 0.3s ease',
                        '& fieldset': {
                          borderColor: 'rgba(255, 255, 255, 0.1)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(147, 112, 219, 0.5)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#9370db',
                          borderWidth: '2px',
                        },
                        '&.Mui-focused': {
                          background: 'rgba(255, 255, 255, 0.08)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: '#dda0dd',
                      },
                      '& .MuiOutlinedInput-input': {
                        color: '#fff',
                      },
                    }}
                  />
                </Grid>

                {/* Descripción */}
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    multiline
                    rows={3}
                    name="descripcion"
                    label="Descripción"
                    value={formData.descripcion}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start" sx={{ alignSelf: 'flex-start', mt: '14px' }}>
                          <FileText size={20} color="rgba(255,255,255,0.5)" />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        background: 'rgba(255, 255, 255, 0.05)',
                        borderRadius: '12px',
                        transition: 'all 0.3s ease',
                        '& fieldset': {
                          borderColor: 'rgba(255, 255, 255, 0.1)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(147, 112, 219, 0.5)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#9370db',
                          borderWidth: '2px',
                        },
                        '&.Mui-focused': {
                          background: 'rgba(255, 255, 255, 0.08)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: '#dda0dd',
                      },
                      '& .MuiOutlinedInput-input': {
                        color: '#fff',
                      },
                    }}
                  />
                </Grid>

                {/* Precio de Compra */}
                <Grid item xs={12} sm={6}>
                  <TextField
                    required
                    fullWidth
                    type="number"
                    name="precioCompra"
                    label="Precio de Compra"
                    value={formData.precioCompra}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <DollarSign size={20} color="rgba(255,255,255,0.5)" />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        background: 'rgba(255, 255, 255, 0.05)',
                        borderRadius: '12px',
                        transition: 'all 0.3s ease',
                        '& fieldset': {
                          borderColor: 'rgba(255, 255, 255, 0.1)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(147, 112, 219, 0.5)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#9370db',
                          borderWidth: '2px',
                        },
                        '&.Mui-focused': {
                          background: 'rgba(255, 255, 255, 0.08)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: '#dda0dd',
                      },
                      '& .MuiOutlinedInput-input': {
                        color: '#fff',
                      },
                    }}
                  />
                </Grid>

                {/* Precio de Venta */}
                <Grid item xs={12} sm={6}>
                  <TextField
                    required
                    fullWidth
                    type="number"
                    name="precioVenta"
                    label="Precio de Venta"
                    value={formData.precioVenta}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <DollarSign size={20} color="rgba(255,255,255,0.5)" />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        background: 'rgba(255, 255, 255, 0.05)',
                        borderRadius: '12px',
                        transition: 'all 0.3s ease',
                        '& fieldset': {
                          borderColor: 'rgba(255, 255, 255, 0.1)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(147, 112, 219, 0.5)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#9370db',
                          borderWidth: '2px',
                        },
                        '&.Mui-focused': {
                          background: 'rgba(255, 255, 255, 0.08)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: '#dda0dd',
                      },
                      '& .MuiOutlinedInput-input': {
                        color: '#fff',
                      },
                    }}
                  />
                </Grid>

                {/* Stock */}
                <Grid item xs={12} sm={4}>
                  <TextField
                    required
                    fullWidth
                    type="number"
                    name="stock"
                    label="Stock Actual"
                    value={formData.stock}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <ShoppingCart size={20} color="rgba(255,255,255,0.5)" />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        background: 'rgba(255, 255, 255, 0.05)',
                        borderRadius: '12px',
                        transition: 'all 0.3s ease',
                        '& fieldset': {
                          borderColor: 'rgba(255, 255, 255, 0.1)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(147, 112, 219, 0.5)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#9370db',
                          borderWidth: '2px',
                        },
                        '&.Mui-focused': {
                          background: 'rgba(255, 255, 255, 0.08)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: '#dda0dd',
                      },
                      '& .MuiOutlinedInput-input': {
                        color: '#fff',
                      },
                    }}
                  />
                </Grid>

                {/* Stock Mínimo */}
                <Grid item xs={12} sm={4}>
                  <TextField
                    required
                    fullWidth
                    type="number"
                    name="stockMinimo"
                    label="Stock Mínimo"
                    value={formData.stockMinimo}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <TrendingDown size={20} color="rgba(255,255,255,0.5)" />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        background: 'rgba(255, 255, 255, 0.05)',
                        borderRadius: '12px',
                        transition: 'all 0.3s ease',
                        '& fieldset': {
                          borderColor: 'rgba(255, 255, 255, 0.1)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(147, 112, 219, 0.5)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#9370db',
                          borderWidth: '2px',
                        },
                        '&.Mui-focused': {
                          background: 'rgba(255, 255, 255, 0.08)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: '#dda0dd',
                      },
                      '& .MuiOutlinedInput-input': {
                        color: '#fff',
                      },
                    }}
                  />
                </Grid>

                {/* Categoría */}
                <Grid item xs={12} sm={4}>
                  <TextField
                    required
                    fullWidth
                    select
                    name="categoria"
                    label="Categoría"
                    value={formData.categoria}
                    onChange={handleChange}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Tag size={20} color="rgba(255,255,255,0.5)" />
                        </InputAdornment>
                      ),
                    }}
                    sx={{
                      '& .MuiOutlinedInput-root': {
                        background: 'rgba(255, 255, 255, 0.05)',
                        borderRadius: '12px',
                        transition: 'all 0.3s ease',
                        '& fieldset': {
                          borderColor: 'rgba(255, 255, 255, 0.1)',
                        },
                        '&:hover fieldset': {
                          borderColor: 'rgba(147, 112, 219, 0.5)',
                        },
                        '&.Mui-focused fieldset': {
                          borderColor: '#9370db',
                          borderWidth: '2px',
                        },
                        '&.Mui-focused': {
                          background: 'rgba(255, 255, 255, 0.08)',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: '#dda0dd',
                      },
                      '& .MuiSelect-select': {
                        color: '#fff',
                      },
                      '& .MuiSvgIcon-root': {
                        color: 'rgba(255, 255, 255, 0.5)',
                      },
                    }}
                  >
                    {categorias.map((categoria) => (
                      <MenuItem key={categoria} value={categoria}>
                        {categoria}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>

                {/* Estado Activo */}
                <Grid item xs={12}>
                  <Card
                    sx={{
                      background: 'rgba(255, 255, 255, 0.02)',
                      borderRadius: '12px',
                      border: '1px solid rgba(255, 255, 255, 0.1)',
                      p: 2
                    }}
                  >
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                      <Power size={20} color="#9370db" />
                      <Box sx={{ flex: 1 }}>
                        <Typography variant="body1" sx={{ color: '#fff', fontWeight: 600 }}>
                          Estado del Producto
                        </Typography>
                        <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                          {formData.estadoActivo ? 'El producto está activo y visible' : 'El producto está inactivo y oculto'}
                        </Typography>
                      </Box>
                      <FormControlLabel
                        control={
                          <Switch
                            checked={formData.estadoActivo}
                            onChange={(e) => setFormData(prev => ({ ...prev, estadoActivo: e.target.checked }))}
                            sx={{
                              '& .MuiSwitch-switchBase.Mui-checked': {
                                color: '#4caf50',
                                '&:hover': {
                                  backgroundColor: 'rgba(76, 175, 80, 0.08)',
                                },
                              },
                              '& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track': {
                                backgroundColor: '#4caf50',
                              },
                              '& .MuiSwitch-track': {
                                backgroundColor: 'rgba(255, 255, 255, 0.3)',
                              },
                            }}
                          />
                        }
                        label=""
                        sx={{ m: 0 }}
                      />
                    </Box>
                  </Card>
                </Grid>
              </Grid>

              {/* Botones */}
              <Box sx={{ display: 'flex', gap: 2, mt: 4, justifyContent: 'center' }}>
                <Button
                  type="button"
                  variant="outlined"
                  onClick={() => navigate(`/productos/detail/${id}`)}
                  sx={{
                    py: 1.5,
                    px: 4,
                    borderRadius: '12px',
                    borderColor: 'rgba(255, 255, 255, 0.3)',
                    color: 'rgba(255, 255, 255, 0.8)',
                    fontSize: '1rem',
                    fontWeight: 600,
                    textTransform: 'none',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      borderColor: '#ff9800',
                      color: '#ff9800',
                      background: 'rgba(255, 152, 0, 0.1)',
                      transform: 'translateY(-2px)'
                    }
                  }}
                >
                  Cancelar
                </Button>

                <Button
                  type="submit"
                  variant="contained"
                  disabled={loading}
                  startIcon={loading ? <CircularProgress size={20} sx={{ color: '#fff' }} /> : <Save size={20} />}
                  sx={{
                    py: 1.5,
                    px: 4,
                    borderRadius: '12px',
                    background: 'linear-gradient(135deg, #ff9800 0%, #f57c00 100%)', // Color naranja para edición
                    boxShadow: '0 4px 20px rgba(255, 152, 0, 0.4)',
                    fontSize: '1rem',
                    fontWeight: 600,
                    textTransform: 'none',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      background: 'linear-gradient(135deg, #ffa726 0%, #ff8f00 100%)',
                      boxShadow: '0 6px 28px rgba(255, 152, 0, 0.6)',
                      transform: 'translateY(-2px)'
                    },
                    '&:disabled': {
                      background: 'rgba(255, 152, 0, 0.3)'
                    }
                  }}
                >
                  {loading ? 'Actualizando...' : 'Actualizar Producto'}
                </Button>
              </Box>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
}
