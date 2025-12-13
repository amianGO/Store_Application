import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import {
  Grid, Card, CardContent, Typography, CircularProgress, Box, 
  TextField, InputAdornment, Chip, Dialog, DialogTitle, 
  DialogContent, DialogActions, IconButton, Button, Accordion, AccordionSummary, 
  AccordionDetails, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Divider
} from '@mui/material';
import { 
  Receipt, Search, Calendar, User, DollarSign, 
  Package, ChevronDown, Eye, X, FileText, Download
} from 'lucide-react';
import axiosInstance from '../../config/axios';
import { formatCOP } from '../../utils/formatters';
import { generateFacturaPDF } from '../../utils/pdfGenerator';

export default function VentasRealizadas() {
  const [loading, setLoading] = useState(true);
  const [facturas, setFacturas] = useState([]);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedFactura, setSelectedFactura] = useState(null);
  const [openDetailDialog, setOpenDetailDialog] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) navigate('/login');
    fetchFacturas();
  }, [navigate]);

  const fetchFacturas = async () => {
    try {
      const response = await axiosInstance.get('/facturas');
      console.log('📊 Respuesta completa del backend:', response.data);
      
      // El backend devuelve { success: true, facturas: [...], total: X, schemaName: 'empresa_X' }
      const facturasData = response.data?.facturas || [];
      
      console.log('📊 Facturas extraídas:', facturasData);
      console.log('📊 Primera factura (ejemplo):', facturasData[0]);
      
      setFacturas(facturasData);
    } catch (error) {
      console.error("❌ Error al cargar las facturas:", error);
      console.error("Detalles del error:", error.response?.data);
      setError('Error al cargar las facturas');
      setFacturas([]);
    } finally {
      setLoading(false);
    }
  };

  const facturasFiltradas = Array.isArray(facturas) ? facturas.filter(factura => {
    const numeroFactura = factura.numeroFactura || factura.numero || '';
    const clienteNombre = factura.clienteNombre || 
                         (factura.cliente ? `${factura.cliente.nombre} ${factura.cliente.apellido}` : '');
    const empleadoNombre = factura.empleadoNombre || 
                          (factura.empleado ? `${factura.empleado.nombre} ${factura.empleado.apellido}` : '');
    
    return numeroFactura.toLowerCase().includes(searchTerm.toLowerCase()) ||
           clienteNombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
           empleadoNombre.toLowerCase().includes(searchTerm.toLowerCase());
  }) : [];

  const handleOpenDetail = (factura) => {
    setSelectedFactura(factura);
    setOpenDetailDialog(true);
  };

  const handleCloseDetail = () => {
    setOpenDetailDialog(false);
    setSelectedFactura(null);
  };

  const handleDownloadPDF = async (factura) => {
    try {
      await generateFacturaPDF(factura);
    } catch (error) {
      console.error('Error al descargar PDF:', error);
      alert('Error al generar el PDF. Por favor intente nuevamente.');
    }
  };

  const getEstadoColor = (estado) => {
    switch (estado) {
      case 'COMPLETADA':
        return { bg: 'rgba(76, 175, 80, 0.2)', color: '#81c784' };
      case 'ANULADA':
        return { bg: 'rgba(244, 67, 54, 0.2)', color: '#e57373' };
      case 'PENDIENTE':
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
        <Box sx={{ mb: 4 }}>
          <Typography 
            variant='h4' 
            sx={{ 
              fontWeight: 700,
              background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
              mb: 1
            }}
          >
            Ventas Realizadas
          </Typography>
          <Typography 
            variant='body1' 
            sx={{ color: 'rgba(255,255,255,0.6)' }}
          >
            Historial completo de facturas y transacciones
          </Typography>
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
                    <Receipt size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Total Facturas
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {facturas.length}
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
                    <FileText size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Completadas
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {facturas.filter(f => f.estado === 'COMPLETADA').length}
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
                      Total Ventas
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {formatCOP(facturas.reduce((sum, f) => sum + (f.total || 0), 0))}
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
                    <Calendar size={24} color="#fff" />
                  </Box>
                  <Box>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                      Hoy
                    </Typography>
                    <Typography variant="h5" sx={{ color: '#fff', fontWeight: 700 }}>
                      {facturas.filter(f => {
                        const today = new Date().toDateString();
                        const facturaDate = new Date(f.fechaEmision).toDateString();
                        return today === facturaDate;
                      }).length}
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
              placeholder="Buscar por número de factura, cliente o empleado..."
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
              label={`${facturasFiltradas.length} facturas`}
              sx={{
                background: 'rgba(147, 112, 219, 0.2)',
                color: '#dda0dd',
                fontWeight: 600,
                borderRadius: '8px'
              }}
            />
          </Box>
        </Card>

        {/* Lista de facturas */}
        {facturasFiltradas.length === 0 ? (
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
            <Receipt size={64} color="rgba(255,255,255,0.3)" style={{ margin: '0 auto' }} />
            <Typography variant="h6" sx={{ color: 'rgba(255,255,255,0.6)', mt: 2 }}>
              No hay facturas disponibles
            </Typography>
          </Card>
        ) : (
          <Box sx={{ mb: 4 }}>
            {facturasFiltradas.map((factura, index) => (
              <Accordion 
                key={factura.id}
                sx={{
                  background: 'rgba(255, 255, 255, 0.05)',
                  backdropFilter: 'blur(20px)',
                  borderRadius: '16px !important',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  mb: 2,
                  '&:before': { display: 'none' },
                  '&.Mui-expanded': {
                    margin: '0 0 16px 0',
                    border: '1px solid rgba(147, 112, 219, 0.4)',
                  }
                }}
              >
                <AccordionSummary
                  expandIcon={<ChevronDown color="rgba(255,255,255,0.7)" />}
                  sx={{
                    '& .MuiAccordionSummary-content': {
                      alignItems: 'center',
                      py: 1
                    }
                  }}
                >
                  <Grid container alignItems="center" spacing={2}>
                    <Grid item xs={12} sm={3}>
                      <Box>
                        <Typography variant="h6" sx={{ color: '#fff', fontWeight: 600 }}>
                          {factura.numeroFactura || factura.numero || 'N/A'}
                        </Typography>
                        <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                          {new Date(factura.fechaEmision || factura.fecha).toLocaleDateString('es-CO', {
                            year: 'numeric',
                            month: 'short',
                            day: 'numeric'
                          })}
                        </Typography>
                      </Box>
                    </Grid>
                    
                    <Grid item xs={12} sm={3}>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <User size={16} color="rgba(255,255,255,0.5)" />
                        <Box>
                          <Typography variant="body2" sx={{ color: '#fff' }}>
                            {factura.clienteNombre || 
                             (factura.cliente ? `${factura.cliente.nombre} ${factura.cliente.apellido}` : 'N/A')}
                          </Typography>
                          <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.5)' }}>
                            Cliente
                          </Typography>
                        </Box>
                      </Box>
                    </Grid>
                    
                    <Grid item xs={12} sm={3}>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <DollarSign size={16} color="rgba(255,255,255,0.5)" />
                        <Typography variant="h6" sx={{ color: '#9370db', fontWeight: 600 }}>
                          {formatCOP(factura.total)}
                        </Typography>
                      </Box>
                    </Grid>
                    
                    <Grid item xs={12} sm={3}>
                      <Chip 
                        label={factura.estado}
                        size="small"
                        sx={{
                          background: getEstadoColor(factura.estado).bg,
                          color: getEstadoColor(factura.estado).color,
                          fontWeight: 600,
                          fontSize: '0.75rem'
                        }}
                      />
                    </Grid>
                  </Grid>
                </AccordionSummary>
                
                <AccordionDetails sx={{ pt: 0 }}>
                  <Divider sx={{ mb: 3, borderColor: 'rgba(255, 255, 255, 0.1)' }} />
                  
                  {/* Botón Ver Detalle */}
                  <Box sx={{ mb: 3, display: 'flex', justifyContent: 'flex-end' }}>
                    <Button
                      variant="outlined"
                      startIcon={<Eye size={18} />}
                      onClick={() => handleOpenDetail(factura)}
                      sx={{
                        borderColor: 'rgba(147, 112, 219, 0.5)',
                        color: '#9370db',
                        borderRadius: '12px',
                        textTransform: 'none',
                        fontWeight: 600,
                        '&:hover': {
                          borderColor: '#9370db',
                          background: 'rgba(147, 112, 219, 0.1)',
                          transform: 'translateY(-2px)'
                        },
                        transition: 'all 0.3s ease'
                      }}
                    >
                      Ver Detalle Completo
                    </Button>
                  </Box>
                  
                  <Grid container spacing={3}>
                    <Grid item xs={12} md={6}>
                      <Typography variant="subtitle2" sx={{ color: '#9370db', mb: 2, fontWeight: 600 }}>
                        Información del Empleado
                      </Typography>
                      <Box sx={{ pl: 2 }}>
                        <Typography variant="body2" sx={{ color: '#fff', mb: 1 }}>
                          {factura.empleadoNombre || 
                           (factura.empleado ? `${factura.empleado.nombre} ${factura.empleado.apellido}` : 'N/A')}
                        </Typography>
                        <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.6)' }}>
                          Vendedor responsable
                        </Typography>
                      </Box>
                    </Grid>
                    
                    <Grid item xs={12} md={6}>
                      <Typography variant="subtitle2" sx={{ color: '#9370db', mb: 2, fontWeight: 600 }}>
                        Resumen de Venta
                      </Typography>
                      <Box sx={{ pl: 2 }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                          <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)' }}>
                            Artículos:
                          </Typography>
                          <Typography variant="body2" sx={{ color: '#fff' }}>
                            {factura.detalles ? factura.detalles.length : 0}
                          </Typography>
                        </Box>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                          <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)' }}>
                            Total:
                          </Typography>
                          <Typography variant="body1" sx={{ color: '#9370db', fontWeight: 600 }}>
                            {formatCOP(factura.total)}
                          </Typography>
                        </Box>
                      </Box>
                    </Grid>
                  </Grid>
                  
                  {factura.detalles && factura.detalles.length > 0 && (
                    <Box sx={{ mt: 3 }}>
                      <Typography variant="subtitle2" sx={{ color: '#9370db', mb: 2, fontWeight: 600 }}>
                        Productos Vendidos
                      </Typography>
                      <TableContainer 
                        component={Paper} 
                        sx={{ 
                          background: 'rgba(255, 255, 255, 0.03)',
                          borderRadius: '12px'
                        }}
                      >
                        <Table size="small">
                          <TableHead>
                            <TableRow>
                              <TableCell sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                                Código
                              </TableCell>
                              <TableCell sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                                Producto
                              </TableCell>
                              <TableCell align="center" sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                                Cantidad
                              </TableCell>
                              <TableCell align="right" sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                                Precio Unit.
                              </TableCell>
                              <TableCell align="right" sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                                Subtotal
                              </TableCell>
                            </TableRow>
                          </TableHead>
                          <TableBody>
                            {factura.detalles.map((detalle, idx) => (
                              <TableRow key={idx}>
                                <TableCell sx={{ color: 'rgba(255,255,255,0.8)' }}>
                                  {detalle.productoCodigo}
                                </TableCell>
                                <TableCell sx={{ color: '#fff' }}>
                                  {detalle.productoNombre}
                                </TableCell>
                                <TableCell align="center" sx={{ color: '#fff' }}>
                                  {detalle.cantidad}
                                </TableCell>
                                <TableCell align="right" sx={{ color: '#fff' }}>
                                  {formatCOP(detalle.precioUnitario)}
                                </TableCell>
                                <TableCell align="right" sx={{ color: '#9370db', fontWeight: 600 }}>
                                  {formatCOP(detalle.subtotal)}
                                </TableCell>
                              </TableRow>
                            ))}
                          </TableBody>
                        </Table>
                      </TableContainer>
                    </Box>
                  )}
                </AccordionDetails>
              </Accordion>
            ))}
          </Box>
        )}
      </Box>

      {/* Dialog de detalle */}
      <Dialog 
        open={openDetailDialog} 
        onClose={handleCloseDetail}
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
        <DialogTitle sx={{ 
          color: '#fff', 
          fontWeight: 700,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          Detalle de Factura
          <IconButton 
            onClick={handleCloseDetail}
            sx={{ color: 'rgba(255,255,255,0.7)' }}
          >
            <X size={24} />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          {selectedFactura && (
            <Box sx={{ mt: 2 }}>
              {/* Información principal de la factura */}
              <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6}>
                  <Card sx={{ 
                    background: 'rgba(147, 112, 219, 0.1)', 
                    borderRadius: '12px',
                    p: 2
                  }}>
                    <Typography variant="subtitle2" sx={{ color: '#9370db', mb: 1, fontWeight: 600 }}>
                      Información de Factura
                    </Typography>
                    <Typography variant="h6" sx={{ color: '#fff', mb: 1 }}>
                      {selectedFactura.numeroFactura || selectedFactura.numero || 'N/A'}
                    </Typography>
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)' }}>
                      {new Date(selectedFactura.fechaEmision || selectedFactura.fecha).toLocaleString('es-CO')}
                    </Typography>
                  </Card>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Card sx={{ 
                    background: 'rgba(76, 175, 80, 0.1)', 
                    borderRadius: '12px',
                    p: 2
                  }}>
                    <Typography variant="subtitle2" sx={{ color: '#4caf50', mb: 1, fontWeight: 600 }}>
                      Total de Venta
                    </Typography>
                    <Typography variant="h4" sx={{ color: '#fff', fontWeight: 700 }}>
                      {formatCOP(selectedFactura.total)}
                    </Typography>
                  </Card>
                </Grid>
              </Grid>

              {/* Información de cliente y empleado */}
              <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" sx={{ color: '#9370db', mb: 2, fontWeight: 600 }}>
                    Cliente
                  </Typography>
                  {selectedFactura.cliente ? (
                    <Box>
                      <Typography variant="body1" sx={{ color: '#fff', mb: 1 }}>
                        {selectedFactura.cliente.nombre} {selectedFactura.cliente.apellido}
                      </Typography>
                      <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)', mb: 1 }}>
                        Cédula: {selectedFactura.cliente.cedula}
                      </Typography>
                      {selectedFactura.cliente.telefono && (
                        <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)', mb: 1 }}>
                          Teléfono: {selectedFactura.cliente.telefono}
                        </Typography>
                      )}
                      {selectedFactura.cliente.email && (
                        <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)' }}>
                          Email: {selectedFactura.cliente.email}
                        </Typography>
                      )}
                    </Box>
                  ) : (
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.5)' }}>
                      No disponible
                    </Typography>
                  )}
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" sx={{ color: '#9370db', mb: 2, fontWeight: 600 }}>
                    Empleado
                  </Typography>
                  {selectedFactura.empleado ? (
                    <Box>
                      <Typography variant="body1" sx={{ color: '#fff', mb: 1 }}>
                        {selectedFactura.empleado.nombre} {selectedFactura.empleado.apellido}
                      </Typography>
                      <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)' }}>
                        Vendedor responsable
                      </Typography>
                    </Box>
                  ) : (
                    <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.5)' }}>
                      No disponible
                    </Typography>
                  )}
                </Grid>
              </Grid>
              
              {/* Detalles de productos */}
              {selectedFactura.detalles && selectedFactura.detalles.length > 0 && (
                <Box>
                  <Typography variant="subtitle2" sx={{ color: '#9370db', mb: 2, fontWeight: 600 }}>
                    Productos Vendidos
                  </Typography>
                  <TableContainer 
                    component={Paper} 
                    sx={{ 
                      background: 'rgba(255, 255, 255, 0.05)',
                      borderRadius: '12px'
                    }}
                  >
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                            Código
                          </TableCell>
                          <TableCell sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                            Producto
                          </TableCell>
                          <TableCell sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                            Categoría
                          </TableCell>
                          <TableCell align="center" sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                            Cantidad
                          </TableCell>
                          <TableCell align="right" sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                            Precio Unit.
                          </TableCell>
                          <TableCell align="right" sx={{ color: 'rgba(255,255,255,0.7)', fontWeight: 600 }}>
                            Subtotal
                          </TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {selectedFactura.detalles.map((detalle, idx) => (
                          <TableRow key={idx}>
                            <TableCell sx={{ color: 'rgba(255,255,255,0.8)' }}>
                              {detalle.productoCodigo}
                            </TableCell>
                            <TableCell sx={{ color: '#fff' }}>
                              {detalle.productoNombre}
                            </TableCell>
                            <TableCell sx={{ color: 'rgba(255,255,255,0.8)' }}>
                              {detalle.productoCategoria}
                            </TableCell>
                            <TableCell align="center" sx={{ color: '#fff' }}>
                              {detalle.cantidad}
                            </TableCell>
                            <TableCell align="right" sx={{ color: '#fff' }}>
                              {formatCOP(detalle.precioUnitario)}
                            </TableCell>
                            <TableCell align="right" sx={{ color: '#9370db', fontWeight: 600 }}>
                              {formatCOP(detalle.subtotal)}
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </Box>
              )}
            </Box>
          )}
        </DialogContent>
        <DialogActions sx={{ p: 3, background: 'rgba(0,0,0,0.3)' }}>
          <Button
            onClick={() => handleDownloadPDF(selectedFactura)}
            variant="contained"
            startIcon={<Download size={20} />}
            sx={{
              background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
              color: '#fff',
              borderRadius: '12px',
              px: 3,
              py: 1.5,
              fontWeight: 600,
              textTransform: 'none',
              boxShadow: '0 4px 15px rgba(99, 102, 241, 0.4)',
              '&:hover': {
                background: 'linear-gradient(135deg, #5855eb 0%, #7c3aed 100%)',
                transform: 'translateY(-2px)',
                boxShadow: '0 6px 20px rgba(99, 102, 241, 0.6)'
              },
              transition: 'all 0.3s ease'
            }}
          >
            Descargar PDF
          </Button>
          <Button
            onClick={handleCloseDetail}
            sx={{
              color: 'rgba(255,255,255,0.7)',
              borderRadius: '12px',
              px: 3,
              py: 1.5,
              fontWeight: 600,
              textTransform: 'none',
              '&:hover': {
                backgroundColor: 'rgba(255,255,255,0.1)',
                color: '#fff'
              }
            }}
          >
            Cerrar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
