import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import CarritoCompras from '../components/CarritoCompras';
import {Grid, Card, CardContent, Typography, CircularProgress, Box, TextField, Button, InputAdornment, Chip, IconButton, MenuItem, Fab, Badge, Dialog, DialogTitle, DialogContent, DialogActions, List, ListItem, ListItemText, ListItemButton} from '@mui/material'
import {ShoppingCart, TrendingUp, DollarSign, Package, Filter, Trash2, Plus, Search, User, X} from 'lucide-react'
import axiosInstance from '../config/axios';
import { formatCOP } from '../utils/formatters';
import { obtenerEmpleadoId } from '../utils/authHelper';

export default function Dashboard() {
  const [loading, setLoading] = useState(true)
  const [productos, setProductos] = useState([])
  const [error, setError] = useState('')
  const [searchTerm, setSearchTerm] = useState('')
  const [filterCategoria, setFilterCategoria] = useState('TODAS')
  const [filterEstado, setFilterEstado] = useState('TODOS')
  const [userRole, setUserRole] = useState('') // Rol del usuario
  
  // Estados del carrito
  const [carritoOpen, setCarritoOpen] = useState(false)
  const [carritoItems, setCarritoItems] = useState([])
  const [productosSeleccionados, setProductosSeleccionados] = useState(new Set())
  
  // Estados para la venta
  const [clienteSelectorOpen, setClienteSelectorOpen] = useState(false)
  const [clientes, setClientes] = useState([])
  const [clienteSeleccionado, setClienteSeleccionado] = useState(null)
  const [ventaData, setVentaData] = useState(null)
  const [searchCliente, setSearchCliente] = useState('')

  const navigate = useNavigate();

  // Cargar carrito desde localStorage al iniciar
  useEffect(() => {
    const carritoGuardado = localStorage.getItem('carrito');
    const productosSeleccionadosGuardados = localStorage.getItem('productosSeleccionados');
    
    if (carritoGuardado) {
      try {
        const items = JSON.parse(carritoGuardado);
        setCarritoItems(items);
        console.log('🛒 Carrito cargado desde localStorage:', items);
      } catch (error) {
        console.error('Error al cargar carrito:', error);
      }
    }
    
    if (productosSeleccionadosGuardados) {
      try {
        const ids = JSON.parse(productosSeleccionadosGuardados);
        setProductosSeleccionados(new Set(ids));
        console.log('✅ Productos seleccionados cargados:', ids);
      } catch (error) {
        console.error('Error al cargar productos seleccionados:', error);
      }
    }
  }, []);

  // Guardar carrito en localStorage cada vez que cambia
  useEffect(() => {
    if (carritoItems.length > 0) {
      localStorage.setItem('carrito', JSON.stringify(carritoItems));
      console.log('💾 Carrito guardado en localStorage');
    } else {
      localStorage.removeItem('carrito');
    }
  }, [carritoItems]);

  // Guardar productos seleccionados en localStorage
  useEffect(() => {
    if (productosSeleccionados.size > 0) {
      localStorage.setItem('productosSeleccionados', JSON.stringify([...productosSeleccionados]));
    } else {
      localStorage.removeItem('productosSeleccionados');
    }
  }, [productosSeleccionados]);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    // Obtener el rol del usuario desde el token
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const rol = payload.rol || '';
      setUserRole(rol);
      console.log('👤 Rol del usuario en Dashboard:', rol);
    } catch (error) {
      console.error('Error al decodificar token:', error);
      navigate('/login-empleado');
      return;
    }

    const fetchProductos = async () => {
      try {
        const response = await axiosInstance.get('/productos')
        console.log('📦 Productos recibidos:', response.data);
        
        // La API devuelve { success: true, productos: [...], total: X, schemaName: 'empresa_X' }
        const productosData = response.data?.productos || response.data || [];
        setProductos(productosData)
      } catch (error) {
        console.log("Error al cargar los productos", error)
        setError('Error al cargar los productos')
        setProductos([])
      } finally {
        setLoading(false)
      }
    };

    const fetchClientes = async () => {
      try {
        const response = await axiosInstance.get('/clientes')
        console.log('📊 Clientes recibidos:', response.data);
        
        // Asegurar que siempre sea un array
        const clientesData = Array.isArray(response.data) 
          ? response.data 
          : (response.data?.data || response.data?.clientes || []);
        
        console.log('📊 Clientes procesados como array:', clientesData);
        setClientes(clientesData);
      } catch (error) {
        console.log("Error al cargar los clientes", error)
        setClientes([])
      }
    };

  fetchProductos();
  fetchClientes();
  }, [navigate]);

  // Obtener categorias unicas
  const categorias = ['TODAS', ...new Set(Array.isArray(productos) ? productos.map(p => p.categoria) : [])];

  // Filtrar Productos
  const productosFiltrados = Array.isArray(productos) ? productos.filter(prod => {
    const matchSearch = prod.nombre.toLowerCase().includes(searchTerm.toLowerCase()) || prod.codigo.toLowerCase().includes(searchTerm.toLowerCase());

    const matchCategoria = filterCategoria === 'TODAS' || prod.categoria === filterCategoria;

    // Usar 'activo' en lugar de 'estadoActivo'
    const matchEstado = filterEstado === 'TODOS' ||
                        (filterEstado === 'ACTIVO' && prod.activo) ||
                        (filterEstado === 'INACTIVO' && !prod.activo);
    return matchSearch && matchCategoria && matchEstado;
  }) : [];

  // Calcular estadisticas
  const stats = {
    total: Array.isArray(productos) ? productos.length : 0,
    activos: Array.isArray(productos) ? productos.filter(p => p.activo).length : 0,
    stockBajo: Array.isArray(productos) ? productos.filter(p => p.stock < 10).length : 0,
    valorTotal: Array.isArray(productos) ? productos.reduce((sum, p) => sum + (p.precioVenta * p.stock), 0) : 0
  };

  // Funciones del carrito
  const agregarAlCarrito = (producto) => {
    const itemExistente = carritoItems.find(item => item.id === producto.id);
    
    if (itemExistente) {
      // Si ya existe, incrementar cantidad
      setCarritoItems(prev => 
        prev.map(item => 
          item.id === producto.id 
            ? { ...item, cantidad: item.cantidad + 1 }
            : item
        )
      );
    } else {
      // Si no existe, agregar nuevo item
      setCarritoItems(prev => [...prev, {
        id: producto.id,
        codigo: producto.codigo,
        nombre: producto.nombre,
        precioUnitario: producto.precioVenta,
        cantidad: 1,
        descuento: 0,
        stockDisponible: producto.stock
      }]);
    }
    
    setProductosSeleccionados(prev => new Set([...prev, producto.id]));
  };

  const actualizarItemCarrito = (itemId, cambios) => {
    setCarritoItems(prev =>
      prev.map(item => item.id === itemId ? { ...item, ...cambios } : item)
    );
  };



  const removerDelCarrito = (itemId) => {
    setCarritoItems(prev => prev.filter(item => item.id !== itemId));
    setProductosSeleccionados(prev => {
      const nuevos = new Set(prev);
      nuevos.delete(itemId);
      return nuevos;
    });
  };

  const limpiarCarrito = () => {
    setCarritoItems([]);
    setProductosSeleccionados(new Set());
    setCarritoOpen(false);
    // Limpiar localStorage
    localStorage.removeItem('carrito');
    localStorage.removeItem('productosSeleccionados');
    console.log('🗑️ Carrito limpiado de localStorage');
  };

  const procesarVenta = async (datosVenta) => {
    try {
      // Abrir selector de cliente
      setVentaData(datosVenta);
      setClienteSelectorOpen(true);
    } catch (error) {
      console.error('Error al procesar venta:', error);
    }
  };

  const clientesFiltrados = Array.isArray(clientes) ? clientes.filter(cliente =>
    `${cliente.nombre} ${cliente.apellido}`.toLowerCase().includes(searchCliente.toLowerCase()) ||
    (cliente.cedula || cliente.documento || '').includes(searchCliente)
  ) : [];

  const confirmarVenta = async () => {
    if (!clienteSeleccionado || !ventaData) return;

    try {
      // Obtener empleadoId usando el helper
      const empleadoId = obtenerEmpleadoId();
      
      if (!empleadoId) {
        alert('❌ Error: No se pudo identificar el empleado. Por favor cierre sesión e inicie sesión nuevamente.');
        return;
      }

      console.log('✅ Empleado ID para la venta:', empleadoId);

      // Preparar datos de la factura según el formato esperado por la API
      const facturaData = {
        clienteId: clienteSeleccionado.id,
        empleadoId: parseInt(empleadoId),
        metodoPago: ventaData.metodoPago || 'EFECTIVO',
        impuesto: ventaData.impuesto || 0,
        descuento: ventaData.descuentoTotal || 0,
        notas: ventaData.notas || '',
        detalles: carritoItems.map(item => ({
          productoId: item.id,
          cantidad: item.cantidad,
          descuento: item.descuento || 0
        }))
      };

      console.log('📝 Creando factura:', facturaData);

      const response = await axiosInstance.post('/facturas', facturaData);
      
      console.log('✅ Factura creada:', response.data);
      
      // Limpiar carrito y cerrar dialogs
      limpiarCarrito();
      setClienteSelectorOpen(false);
      setClienteSeleccionado(null);
      setVentaData(null);
      
      // Mostrar número de factura desde la respuesta
      const numeroFactura = response.data?.numeroFactura || response.data?.factura?.numeroFactura || 'N/A';
      alert(`✅ Venta realizada exitosamente!\nFactura N° ${numeroFactura}`);
      
    } catch (error) {
      console.error('❌ Error al crear factura:', error);
      console.error('Detalles del error:', error.response?.data);
      
      const mensajeError = error.response?.data?.message || 
                          error.response?.data?.mensaje || 
                          'Error al procesar la venta. Intente nuevamente.';
      
      alert(`❌ ${mensajeError}`);
    }
  };

  if (loading) {
    return(
      <Box sx={{ display: 'flex', justifyContent:'center', alignItems:'center', height: '100hv'}}>
        <CircularProgress/>
      </Box>
    )
  }

  return(
    <Box
      sx={{
        minHeight: '100vh',
        width: '100vw',
        background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
        position: 'relative',
        overflow: 'auto',
        '&::before':{
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
        '@keyframes pulse':{
          '0%, 100%': { transform: 'scale(1)', opacity: 0.5 },
          '50%': { transform: 'scale(1.1)', opacity: 0.3 },
        },
      }}
    >
      <Navbar />
      <Box 
        sx={{ 
          padding: 4,
          paddingTop: '88px', // Espacio para el navbar fijo
          position: 'relative',
          zIndex: 1,
          minHeight: '100vh'
        }}
      >
        {/* Header con título y botones de acción */}
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
            Gestión de Productos
          </Typography>

          {/* Mostrar botón "Agregar Producto" solo si el usuario es ADMIN */}
          {userRole === 'ADMIN' && (
            <Button
              variant="contained"
              startIcon={<Plus size={20} />}
              onClick={() => navigate('/productos/create')}
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
              Agregar Producto
            </Button>
          )}
        </Box>

        {/* Tarjetas de estadísticas */}
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
                    <Package size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Total Productos
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {stats.total}
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
                    <TrendingUp size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Productos Activos
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {stats.activos}
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
                    <ShoppingCart size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Stock Bajo
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {stats.stockBajo}
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
                    <DollarSign size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Valor Inventario
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {formatCOP(stats.valorTotal)}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* Barra de búsqueda y filtros */}
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
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap', alignItems: 'center' }}>
            {/* Búsqueda */}
            <TextField
              placeholder="Buscar por nombre o código..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              sx={{
                flex: 1,
                minWidth: '250px',
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

            {/* Filtro de Categoría */}
            <TextField
              select
              value={filterCategoria}
              onChange={(e) => setFilterCategoria(e.target.value)}
              sx={{
                minWidth: '180px',
                '& .MuiOutlinedInput-root': {
                  background: 'rgba(255, 255, 255, 0.05)',
                  borderRadius: '12px',
                  '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                  '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                  '&.Mui-focused fieldset': { borderColor: '#9370db', borderWidth: '2px' },
                },
                '& .MuiSelect-select': { color: '#fff' },
                '& .MuiSvgIcon-root': { color: 'rgba(255, 255, 255, 0.5)' },
              }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Filter size={20} color="rgba(255,255,255,0.5)" />
                  </InputAdornment>
                ),
              }}
            >
              {categorias.map((cat) => (
                <MenuItem key={cat} value={cat}>{cat}</MenuItem>
              ))}
            </TextField>

            {/* Filtro de Estado */}
            <TextField
              select
              value={filterEstado}
              onChange={(e) => setFilterEstado(e.target.value)}
              sx={{
                minWidth: '150px',
                '& .MuiOutlinedInput-root': {
                  background: 'rgba(255, 255, 255, 0.05)',
                  borderRadius: '12px',
                  '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.1)' },
                  '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                  '&.Mui-focused fieldset': { borderColor: '#9370db', borderWidth: '2px' },
                },
                '& .MuiSelect-select': { color: '#fff' },
                '& .MuiSvgIcon-root': { color: 'rgba(255, 255, 255, 0.5)' },
              }}
            >
              <MenuItem value="TODOS">Todos</MenuItem>
              <MenuItem value="ACTIVO">Activos</MenuItem>
              <MenuItem value="INACTIVO">Inactivos</MenuItem>
            </TextField>

            {/* Contador de resultados */}
            <Chip 
              label={`${productosFiltrados.length} productos`}
              sx={{
                background: 'rgba(147, 112, 219, 0.2)',
                color: '#dda0dd',
                fontWeight: 600,
                borderRadius: '8px'
              }}
            />
          </Box>
        </Card>

        {/* Grid de productos */}
        {productosFiltrados.length === 0 ? (
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
            <Package size={64} color="rgba(255,255,255,0.3)" style={{ margin: '0 auto' }} />
            <Typography variant="h6" sx={{ color: 'rgba(255,255,255,0.6)', mt: 2 }}>
              No hay productos disponibles
            </Typography>
          </Card>
        ) : (
          <Grid container spacing={3}>
            {productosFiltrados.map((prod) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={prod.id}>
                <Card 
                  onClick={() => agregarAlCarrito(prod)}
                  onDoubleClick={(e) => {
                    e.stopPropagation();
                    navigate(`/productos/detail/${prod.id}`);
                  }}
                  sx={{
                    background: productosSeleccionados.has(prod.id) 
                      ? 'rgba(147, 112, 219, 0.15)' 
                      : 'rgba(255, 255, 255, 0.05)',
                    backdropFilter: 'blur(20px)',
                    borderRadius: '16px',
                    border: productosSeleccionados.has(prod.id)
                      ? '2px solid rgba(147, 112, 219, 0.6)'
                      : '1px solid rgba(255, 255, 255, 0.1)',
                    transition: 'all 0.3s ease',
                    cursor: 'pointer',
                    position: 'relative',
                    '&:hover': {
                      transform: 'translateY(-8px)',
                      boxShadow: productosSeleccionados.has(prod.id)
                        ? '0 12px 48px rgba(147, 112, 219, 0.5)'
                        : '0 12px 48px rgba(147, 112, 219, 0.3)',
                      border: productosSeleccionados.has(prod.id)
                        ? '2px solid rgba(147, 112, 219, 0.8)'
                        : '1px solid rgba(147, 112, 219, 0.4)'
                    }
                  }}
                >
                  <CardContent>
                    {/* Indicador de producto en carrito */}
                    {productosSeleccionados.has(prod.id) && (
                      <Box
                        sx={{
                          position: 'absolute',
                          top: 8,
                          left: 8,
                          background: 'linear-gradient(135deg, #4caf50 0%, #388e3c 100%)',
                          borderRadius: '50%',
                          width: 24,
                          height: 24,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          zIndex: 1
                        }}
                      >
                        <ShoppingCart size={12} color="#fff" />
                      </Box>
                    )}

                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 2 }}>
                      <Typography 
                        variant='h6' 
                        sx={{ 
                          fontWeight: 700, 
                          color: '#fff',
                          fontSize: '1.1rem'
                        }}
                      >
                        {prod.nombre}
                      </Typography>
                      <Chip 
                        label={prod.activo ? 'Activo' : 'Inactivo'}
                        size="small"
                        sx={{
                          background: prod.activo 
                            ? 'rgba(76, 175, 80, 0.2)' 
                            : 'rgba(244, 67, 54, 0.2)',
                          color: prod.activo ? '#81c784' : '#e57373',
                          fontWeight: 600,
                          fontSize: '0.75rem'
                        }}
                      />
                    </Box>

                    <Typography variant='body2' sx={{ color: 'rgba(255,255,255,0.5)', mb: 2 }}>
                      Código: {prod.codigo}
                    </Typography>

                    <Box sx={{ 
                      display: 'flex', 
                      justifyContent: 'space-between', 
                      mb: 1,
                      p: 1.5,
                      background: 'rgba(147, 112, 219, 0.1)',
                      borderRadius: '8px'
                    }}>
                      <Box>
                        <Typography variant='caption' sx={{ color: 'rgba(255,255,255,0.5)' }}>
                          Compra
                        </Typography>
                        <Typography variant='body1' sx={{ color: '#fff', fontWeight: 600 }}>
                          {formatCOP(prod.precioCompra)}
                        </Typography>
                      </Box>
                      <Box sx={{ textAlign: 'right' }}>
                        <Typography variant='caption' sx={{ color: 'rgba(255,255,255,0.5)' }}>
                          Venta
                        </Typography>
                        <Typography variant='body1' sx={{ color: '#9370db', fontWeight: 600 }}>
                          {formatCOP(prod.precioVenta)}
                        </Typography>
                      </Box>
                    </Box>

                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3 }}>
                      <Box sx={{ flex: 1, pr: 2 }}>
                        <Typography variant='caption' sx={{ color: 'rgba(255,255,255,0.5)' }}>
                          Stock
                        </Typography>
                        <Typography 
                          variant='body1' 
                          sx={{ 
                            color: prod.stock < 10 ? '#ff9800' : '#4caf50',
                            fontWeight: 700
                          }}
                        >
                          {prod.stock} unidades
                        </Typography>
                      </Box>
                      <Box sx={{ flex: 1, textAlign: 'right', pl: 2 }}>
                        <Typography variant='caption' sx={{ color: 'rgba(255,255,255,0.5)' }}>
                          Categoría
                        </Typography>
                        <Typography variant='body2' sx={{ color: '#dda0dd', fontWeight: 600 }}>
                          {prod.categoria}
                        </Typography>
                      </Box>
                    </Box>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>
        )}
      </Box>

      {/* Botón flotante del carrito */}
      <Fab
        onClick={() => setCarritoOpen(true)}
        sx={{
          position: 'fixed',
          bottom: 24,
          right: 24,
          background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
          color: '#fff',
          '&:hover': {
            background: 'linear-gradient(135deg, #a280e0 0%, #7b6bd4 100%)',
            transform: 'scale(1.05)',
          },
          transition: 'all 0.3s ease',
          zIndex: 1000
        }}
      >
        <Badge badgeContent={carritoItems.length} color="error">
          <ShoppingCart size={24} />
        </Badge>
      </Fab>

      {/* Componente del carrito */}
      <CarritoCompras
        open={carritoOpen}
        onClose={() => setCarritoOpen(false)}
        items={carritoItems}
        onUpdateItem={actualizarItemCarrito}
        onRemoveItem={removerDelCarrito}
        onClearCart={limpiarCarrito}
        onCheckout={procesarVenta}
      />

      {/* Dialog selector de cliente */}
      <Dialog 
        open={clienteSelectorOpen} 
        onClose={() => setClienteSelectorOpen(false)}
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
        <DialogTitle sx={{ 
          color: '#fff', 
          fontWeight: 700,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          Seleccionar Cliente
          <IconButton 
            onClick={() => setClienteSelectorOpen(false)}
            sx={{ color: 'rgba(255,255,255,0.7)' }}
          >
            <X size={24} />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            placeholder="Buscar por nombre o cédula..."
            value={searchCliente}
            onChange={(e) => setSearchCliente(e.target.value)}
            sx={{
              mb: 2,
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
          
          <List sx={{ maxHeight: '300px', overflow: 'auto' }}>
            {clientesFiltrados.map((cliente) => (
              <ListItem key={cliente.id} disablePadding>
                <ListItemButton
                  selected={clienteSeleccionado?.id === cliente.id}
                  onClick={() => setClienteSeleccionado(cliente)}
                  sx={{
                    borderRadius: '8px',
                    mb: 1,
                    background: clienteSeleccionado?.id === cliente.id 
                      ? 'rgba(147, 112, 219, 0.2)' 
                      : 'rgba(255, 255, 255, 0.05)',
                    '&:hover': {
                      background: 'rgba(147, 112, 219, 0.15)'
                    }
                  }}
                >
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, width: '100%' }}>
                    <User size={20} color="rgba(255,255,255,0.7)" />
                    <ListItemText
                      primary={
                        <Typography sx={{ color: '#fff', fontWeight: 600 }}>
                          {cliente.nombre} {cliente.apellido}
                        </Typography>
                      }
                      secondary={
                        <Typography sx={{ color: 'rgba(255,255,255,0.6)', fontSize: '0.85rem' }}>
                          Cédula: {cliente.cedula}
                          {cliente.telefono && ` • Tel: ${cliente.telefono}`}
                        </Typography>
                      }
                    />
                  </Box>
                </ListItemButton>
              </ListItem>
            ))}
          </List>
          
          {clientesFiltrados.length === 0 && (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <User size={48} color="rgba(255,255,255,0.3)" style={{ margin: '0 auto' }} />
              <Typography sx={{ color: 'rgba(255,255,255,0.6)', mt: 2 }}>
                No se encontraron clientes
              </Typography>
              <Button
                variant="outlined"
                onClick={() => navigate('/clientes')}
                sx={{
                  mt: 2,
                  borderColor: 'rgba(147, 112, 219, 0.5)',
                  color: '#9370db',
                  '&:hover': {
                    borderColor: '#9370db',
                    background: 'rgba(147, 112, 219, 0.1)'
                  }
                }}
              >
                Ir a Gestión de Clientes
              </Button>
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ p: 3 }}>
          <Button 
            onClick={() => setClienteSelectorOpen(false)}
            sx={{ color: 'rgba(255,255,255,0.7)' }}
          >
            Cancelar
          </Button>
          <Button 
            onClick={confirmarVenta}
            disabled={!clienteSeleccionado}
            variant="contained"
            sx={{
              background: !clienteSeleccionado 
                ? 'rgba(255,255,255,0.1)' 
                : 'linear-gradient(135deg, #4caf50 0%, #388e3c 100%)',
              '&:hover': {
                background: !clienteSeleccionado 
                  ? 'rgba(255,255,255,0.1)' 
                  : 'linear-gradient(135deg, #66bb6a 0%, #4caf50 100%)',
              }
            }}
          >
            Confirmar Venta
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}