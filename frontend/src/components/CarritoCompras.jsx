import React, { useState } from 'react';
import {
  Box,
  Drawer,
  Typography,
  Button,
  Card,
  CardContent,
  TextField,
  IconButton,
  Divider,
  List,
  ListItem,
  Chip,
  Alert
} from '@mui/material';
import {
  ShoppingCart,
  X,
  Plus,
  Minus,
  Trash2,
  CreditCard,
  Percent
} from 'lucide-react';
import { formatCOP } from '../utils/formatters';

export default function CarritoCompras({ open, onClose, items = [], onUpdateItem, onRemoveItem, onClearCart, onCheckout }) {

  const calcularSubtotal = () => {
    return items.reduce((total, item) => {
      const subtotal = item.precioUnitario * item.cantidad;
      const descuentoItem = subtotal * (item.descuento / 100);
      return total + (subtotal - descuentoItem);
    }, 0);
  };

  const calcularIVA = () => {
    const subtotal = calcularSubtotal();
    return subtotal * 0.19; // 19% IVA
  };

  const calcularTotal = () => {
    const subtotal = calcularSubtotal();
    const iva = calcularIVA();
    return subtotal + iva;
  };

  const handleCantidadChange = (itemId, nuevaCantidad) => {
    if (nuevaCantidad <= 0) {
      onRemoveItem(itemId);
    } else {
      onUpdateItem(itemId, { cantidad: nuevaCantidad });
    }
  };

  const handleDescuentoChange = (itemId, descuento) => {
    const descuentoValido = Math.max(0, Math.min(100, descuento));
    onUpdateItem(itemId, { descuento: descuentoValido });
  };

  return (
    <Drawer
      anchor="right"
      open={open}
      onClose={onClose}
      PaperProps={{
        sx: {
          width: { xs: '100%', sm: 450 },
          background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
          color: '#fff'
        }
      }}
    >
      <Box sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column' }}>
        {/* Header */}
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 3 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Box
              sx={{
                width: 48,
                height: 48,
                borderRadius: '50%',
                background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
            >
              <ShoppingCart size={24} color="#fff" />
            </Box>
            <Box>
              <Typography variant="h6" sx={{ fontWeight: 700, color: '#fff' }}>
                Carrito de Compras
              </Typography>
              <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                {items.length} producto{items.length !== 1 ? 's' : ''}
              </Typography>
            </Box>
          </Box>
          
          <IconButton onClick={onClose} sx={{ color: 'rgba(255, 255, 255, 0.7)' }}>
            <X size={24} />
          </IconButton>
        </Box>

        {/* Lista de productos */}
        <Box sx={{ flex: 1, overflow: 'auto', mb: 3 }}>
          {items.length === 0 ? (
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                height: '200px',
                opacity: 0.6
              }}
            >
              <ShoppingCart size={64} color="rgba(255, 255, 255, 0.3)" />
              <Typography variant="body1" sx={{ mt: 2, color: 'rgba(255, 255, 255, 0.6)' }}>
                El carrito está vacío
              </Typography>
            </Box>
          ) : (
            <List sx={{ p: 0 }}>
              {items.map((item, index) => (
                <ListItem key={item.id || index} sx={{ p: 0, mb: 2 }}>
                  <Card
                    sx={{
                      background: 'rgba(255, 255, 255, 0.05)',
                      backdropFilter: 'blur(10px)',
                      borderRadius: '12px',
                      border: '1px solid rgba(255, 255, 255, 0.1)',
                      width: '100%'
                    }}
                  >
                    <CardContent sx={{ p: 2 }}>
                      {/* Nombre del producto y botón eliminar */}
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', mb: 2 }}>
                        <Box sx={{ flex: 1 }}>
                          <Typography variant="subtitle1" sx={{ fontWeight: 600, color: '#fff', mb: 0.5 }}>
                            {item.nombre}
                          </Typography>
                          <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                            {item.codigo}
                          </Typography>
                        </Box>
                        <IconButton
                          onClick={() => onRemoveItem(item.id)}
                          sx={{ color: '#f44336', p: 0.5 }}
                          size="small"
                        >
                          <Trash2 size={16} />
                        </IconButton>
                      </Box>

                      {/* Precio unitario */}
                      <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.8)', mb: 2 }}>
                        Precio: {formatCOP(item.precioUnitario)}
                      </Typography>

                      {/* Controles de cantidad */}
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <IconButton
                            onClick={() => handleCantidadChange(item.id, item.cantidad - 1)}
                            sx={{
                              color: '#9370db',
                              bgcolor: 'rgba(147, 112, 219, 0.1)',
                              '&:hover': { bgcolor: 'rgba(147, 112, 219, 0.2)' },
                              width: 32,
                              height: 32
                            }}
                          >
                            <Minus size={16} />
                          </IconButton>
                          
                          <TextField
                            value={item.cantidad}
                            onChange={(e) => handleCantidadChange(item.id, parseInt(e.target.value) || 1)}
                            type="number"
                            inputProps={{ min: 1, style: { textAlign: 'center' } }}
                            sx={{
                              width: 60,
                              '& .MuiOutlinedInput-root': {
                                color: '#fff',
                                '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.2)' },
                                '&:hover fieldset': { borderColor: 'rgba(147, 112, 219, 0.5)' },
                              }
                            }}
                          />
                          
                          <IconButton
                            onClick={() => handleCantidadChange(item.id, item.cantidad + 1)}
                            sx={{
                              color: '#9370db',
                              bgcolor: 'rgba(147, 112, 219, 0.1)',
                              '&:hover': { bgcolor: 'rgba(147, 112, 219, 0.2)' },
                              width: 32,
                              height: 32
                            }}
                          >
                            <Plus size={16} />
                          </IconButton>
                        </Box>
                      </Box>

                      {/* Descuento individual */}
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                        <Percent size={16} color="#ff9800" />
                        <TextField
                          label="Descuento %"
                          type="number"
                          value={item.descuento || 0}
                          onChange={(e) => handleDescuentoChange(item.id, parseFloat(e.target.value) || 0)}
                          inputProps={{ min: 0, max: 100, step: 0.01 }}
                          size="small"
                          sx={{
                            flex: 1,
                            '& .MuiOutlinedInput-root': {
                              color: '#fff',
                              '& fieldset': { borderColor: 'rgba(255, 255, 255, 0.2)' },
                              '&:hover fieldset': { borderColor: 'rgba(255, 152, 0, 0.5)' },
                            },
                            '& .MuiInputLabel-root': { color: 'rgba(255, 255, 255, 0.6)' }
                          }}
                        />
                      </Box>

                      {/* Subtotal */}
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                          Subtotal:
                        </Typography>
                        <Typography variant="subtitle1" sx={{ fontWeight: 600, color: '#4caf50' }}>
                          {formatCOP(item.precioUnitario * item.cantidad * (1 - (item.descuento || 0) / 100))}
                        </Typography>
                      </Box>
                    </CardContent>
                  </Card>
                </ListItem>
              ))}
            </List>
          )}
        </Box>

        {/* Resumen y acciones - Solo si hay items */}
        {items.length > 0 && (
          <>
            <Divider sx={{ borderColor: 'rgba(255, 255, 255, 0.1)', mb: 3 }} />
            


            {/* Resumen de totales */}
            <Card
              sx={{
                background: 'rgba(147, 112, 219, 0.1)',
                borderRadius: '12px',
                border: '1px solid rgba(147, 112, 219, 0.3)',
                mb: 3
              }}
            >
              <CardContent sx={{ p: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.8)' }}>
                    Subtotal:
                  </Typography>
                  <Typography variant="body2" sx={{ color: '#fff' }}>
                    {formatCOP(calcularSubtotal())}
                  </Typography>
                </Box>

                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="body2" sx={{ color: 'rgba(255, 255, 255, 0.8)' }}>
                    IVA (19%):
                  </Typography>
                  <Typography variant="body2" sx={{ color: '#fff' }}>
                    {formatCOP(calcularIVA())}
                  </Typography>
                </Box>
                
                <Divider sx={{ borderColor: 'rgba(255, 255, 255, 0.2)', my: 1 }} />
                
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                  <Typography variant="h6" sx={{ fontWeight: 700, color: '#fff' }}>
                    Total:
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 700, color: '#4caf50' }}>
                    {formatCOP(calcularTotal())}
                  </Typography>
                </Box>
              </CardContent>
            </Card>

            {/* Botones de acción */}
            <Box sx={{ display: 'flex', gap: 2 }}>
              <Button
                variant="outlined"
                onClick={onClearCart}
                sx={{
                  flex: 1,
                  borderColor: 'rgba(244, 67, 54, 0.5)',
                  color: '#ffcdd2',
                  '&:hover': {
                    borderColor: '#f44336',
                    background: 'rgba(244, 67, 54, 0.1)'
                  }
                }}
              >
                Cancelar
              </Button>
              
              <Button
                variant="contained"
                startIcon={<CreditCard size={20} />}
                onClick={() => onCheckout({ 
                  items, 
                  subtotal: calcularSubtotal(),
                  total: calcularTotal()
                })}
                sx={{
                  flex: 2,
                  background: 'linear-gradient(135deg, #4caf50 0%, #388e3c 100%)',
                  '&:hover': {
                    background: 'linear-gradient(135deg, #66bb6a 0%, #4caf50 100%)'
                  }
                }}
              >
                Vender
              </Button>
            </Box>
          </>
        )}
      </Box>
    </Drawer>
  );
}
