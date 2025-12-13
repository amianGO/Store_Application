import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
  InputAdornment
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
  Save
} from 'lucide-react';
import axiosInstance from '../../config/axios';
import Navbar from '../../components/Navbar';

const categorias = [
  'ELECTRONICA',
  'ROPA',
  'HOGAR',
  'DEPORTES',
  'JUGUETES',
  'FERRETERIA',
  'ALIMENTOS',
  'BEBIDAS',
  'PAPELERIA',
  'CALZADO',
  'LIMPIEZA',
  'OTROS'
];

export default function CreateProduct() {
  const [formData, setFormData] = useState({
    codigo: '',
    nombre: '',
    descripcion: '',
    precioCompra: '',
    precioVenta: '',
    stock: '',
    stockMinimo: '',
    categoria: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [userRole, setUserRole] = useState('');
  
  const navigate = useNavigate();

  // Verificar permisos y cargar datos iniciales
  useEffect(() => {
    // Obtener el rol del usuario desde el token
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const rol = payload.rol || '';
        setUserRole(rol);
        console.log('👤 Rol del usuario:', rol);
        
        // Si no es ADMIN, redirigir al dashboard
        if (rol !== 'ADMIN') {
          setError('No tienes permisos para crear productos. Solo usuarios ADMIN pueden crear productos.');
          setTimeout(() => {
            navigate('/dashboard');
          }, 3000);
          return;
        }
      } catch (error) {
        console.error('Error al decodificar token:', error);
        navigate('/login-empleado');
        return;
      }
    } else {
      navigate('/login-empleado');
      return;
    }
    
    // Generar código de producto
    generateProductCode();
  }, [navigate]);

  // Generar código de producto automáticamente
  const generateProductCode = async () => {
    try {
      const response = await axiosInstance.get('/productos');
      console.log('📦 Respuesta completa de productos:', response.data);
      
      // La API devuelve { success: true, productos: [...], total: X, schemaName: 'empresa_X' }
      const productosData = response.data?.productos || response.data || [];
      console.log('📦 Productos extraídos:', productosData);
      console.log('📦 Total de productos:', productosData.length);
      
      if (!Array.isArray(productosData)) {
        console.error('❌ productosData no es un array:', productosData);
        setFormData(prev => ({
          ...prev,
          codigo: 'PROD001'
        }));
        return;
      }
      
      // Encontrar el último número de producto
      let maxNumber = 0;
      productosData.forEach(producto => {
        if (producto.codigo && producto.codigo.startsWith('PROD')) {
          const number = parseInt(producto.codigo.replace('PROD', ''));
          console.log(`📌 Código encontrado: ${producto.codigo} → Número: ${number}`);
          if (!isNaN(number) && number > maxNumber) {
            maxNumber = number;
          }
        }
      });
      
      console.log(`✅ Máximo número encontrado: ${maxNumber}`);
      
      // Generar el siguiente código
      const nextNumber = maxNumber + 1;
      const newCode = `PROD${nextNumber.toString().padStart(3, '0')}`;
      
      console.log(`🆕 Nuevo código generado: ${newCode}`);
      
      setFormData(prev => ({
        ...prev,
        codigo: newCode
      }));
    } catch (error) {
      console.error('❌ Error al generar código:', error);
      // Código por defecto si hay error
      setFormData(prev => ({
        ...prev,
        codigo: 'PROD001'
      }));
    }
  };

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

      await axiosInstance.post('/productos', productData);
      setSuccess('Producto creado exitosamente');
      
      // Limpiar formulario
      setFormData({
        codigo: '',
        nombre: '',
        descripcion: '',
        precioCompra: '',
        precioVenta: '',
        stock: '',
        stockMinimo: '',
        categoria: ''
      });

      // Redirigir después de 2 segundos
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);

    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al crear el producto');
    } finally {
      setLoading(false);
    }
  };

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
            Crear Nuevo Producto
          </Typography>
          
          <Typography
            variant="body1"
            sx={{
              color: 'rgba(255, 255, 255, 0.6)'
            }}
          >
            Completa la información para agregar un nuevo producto al inventario
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
                  background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  boxShadow: '0 8px 24px rgba(147, 112, 219, 0.4)',
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
                        '&.Mui-focused fieldset': {
                          borderColor: '#9370db',
                          borderWidth: '2px',
                        },
                      },
                      '& .MuiInputLabel-root': {
                        color: 'rgba(255, 255, 255, 0.6)',
                      },
                      '& .MuiInputLabel-root.Mui-focused': {
                        color: '#dda0dd',
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
              </Grid>

              {/* Botones */}
              <Box sx={{ display: 'flex', gap: 2, mt: 4, justifyContent: 'center' }}>
                <Button
                  type="button"
                  variant="outlined"
                  onClick={() => navigate('/dashboard')}
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
                    background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
                    boxShadow: '0 4px 20px rgba(147, 112, 219, 0.4)',
                    fontSize: '1rem',
                    fontWeight: 600,
                    textTransform: 'none',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      background: 'linear-gradient(135deg, #a280e0 0%, #7b6bd4 100%)',
                      boxShadow: '0 6px 28px rgba(147, 112, 219, 0.6)',
                      transform: 'translateY(-2px)'
                    },
                    '&:disabled': {
                      background: 'rgba(147, 112, 219, 0.3)'
                    }
                  }}
                >
                  {loading ? 'Guardando...' : 'Guardar Producto'}
                </Button>
              </Box>
            </Box>
          </CardContent>
        </Card>
      </Container>
    </Box>
  );
}
