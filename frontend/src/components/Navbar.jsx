import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  IconButton,
  Menu,
  MenuItem,
  Divider,
  Chip
} from '@mui/material';
import {
  LogOut,
  User,
  Package,
  Users,
  ShoppingCart,
  Building2
} from 'lucide-react';

export default function Navbar() {
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  
  // Estado para datos del empleado
  const [datosEmpleado, setDatosEmpleado] = useState({
    nombre: '',
    apellido: '',
    rol: '',
    cargo: ''
  });

  // Datos de la empresa (estáticos)
  const empresaNombre = localStorage.getItem('empresaNombre') || 'Mi Empresa';
  const tenantKey = localStorage.getItem('tenantKey') || '';

  useEffect(() => {
    const cargarDatosEmpleado = () => {
      // Obtener datos del localStorage
      const empleadoNombre = localStorage.getItem('empleadoNombre') || '';
      const rol = localStorage.getItem('empleadoRol') || '';
      const cargo = localStorage.getItem('empleadoCargo') || '';

      // Si empleadoNombre contiene el nombre completo (Nombre Apellido)
      if (empleadoNombre && empleadoNombre !== 'Usuario') {
        const [nombre = '', apellido = ''] = empleadoNombre.split(' ');
        setDatosEmpleado({ nombre, apellido, rol, cargo });
        console.log('✅ Datos cargados en Navbar:', { nombre, apellido, rol, cargo });
      } else {
        setDatosEmpleado({ 
          nombre: 'Usuario', 
          apellido: '', 
          rol: rol || 'EMPLEADO', 
          cargo 
        });
      }
    };

    // Cargar inmediatamente
    cargarDatosEmpleado();

    // Reintentar cada 200ms durante 2 segundos
    const interval = setInterval(cargarDatosEmpleado, 200);
    setTimeout(() => clearInterval(interval), 2000);

    return () => clearInterval(interval);
  }, []);

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    // Mantener solo datos de empresa
    const datosEmpresa = {
      empresaId: localStorage.getItem('empresaId'),
      empresaNombre: localStorage.getItem('empresaNombre'),
      tenantKey: localStorage.getItem('tenantKey'),
      tokenEmpresa: localStorage.getItem('tokenEmpresa')
    };
    
    localStorage.clear();
    
    // Restaurar datos de empresa
    Object.entries(datosEmpresa).forEach(([key, value]) => {
      if (value) localStorage.setItem(key, value);
    });
    
    navigate('/login-empleado');
  };

  const getRolColor = (rol) => {
    const colores = {
      'ADMIN': { bg: 'rgba(244, 67, 54, 0.2)', color: '#ef5350' },
      'VENDEDOR': { bg: 'rgba(76, 175, 80, 0.2)', color: '#66bb6a' },
      'INVENTARIO': { bg: 'rgba(255, 152, 0, 0.2)', color: '#ffa726' },
      'default': { bg: 'rgba(33, 150, 243, 0.2)', color: '#42a5f5' }
    };
    return colores[rol] || colores.default;
  };

  const rolColor = getRolColor(datosEmpleado.rol);

  return (
    <AppBar
      position="fixed"
      sx={{
        background: 'rgba(26, 26, 46, 0.95)',
        backdropFilter: 'blur(20px)',
        borderBottom: '1px solid rgba(147, 112, 219, 0.2)',
        boxShadow: '0 4px 30px rgba(0, 0, 0, 0.3)',
      }}
    >
      <Toolbar sx={{ justifyContent: 'space-between', py: 1 }}>
        {/* Logo y empresa */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Box
            sx={{
              background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
              p: 1,
              borderRadius: '12px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            <Building2 size={28} color="#fff" />
          </Box>
          
          <Box>
            <Typography
              variant="h6"
              sx={{
                fontWeight: 700,
                background: 'linear-gradient(135deg, #dda0dd 0%, #9370db 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                lineHeight: 1.2
              }}
            >
              {empresaNombre}
            </Typography>
            <Typography variant="caption" sx={{ color: 'rgba(255, 255, 255, 0.5)', fontSize: '0.75rem' }}>
              Tenant: {tenantKey}
            </Typography>
          </Box>
        </Box>

        {/* Navegación */}
        <Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 1 }}>
          <Button
            startIcon={<Package size={18} />}
            onClick={() => navigate('/dashboard')}
            sx={{
              color: 'rgba(255, 255, 255, 0.8)',
              textTransform: 'none',
              fontWeight: 500,
              '&:hover': { background: 'rgba(147, 112, 219, 0.1)', color: '#dda0dd' }
            }}
          >
            Productos
          </Button>
          
          <Button
            startIcon={<Users size={18} />}
            onClick={() => navigate('/clientes')}
            sx={{
              color: 'rgba(255, 255, 255, 0.8)',
              textTransform: 'none',
              fontWeight: 500,
              '&:hover': { background: 'rgba(147, 112, 219, 0.1)', color: '#dda0dd' }
            }}
          >
            Clientes
          </Button>
          
          <Button
            startIcon={<ShoppingCart size={18} />}
            onClick={() => navigate('/ventas')}
            sx={{
              color: 'rgba(255, 255, 255, 0.8)',
              textTransform: 'none',
              fontWeight: 500,
              '&:hover': { background: 'rgba(147, 112, 219, 0.1)', color: '#dda0dd' }
            }}
          >
            Ventas
          </Button>
          
          <Button
            startIcon={<User size={18} />}
            onClick={() => navigate('/empleados')}
            sx={{
              color: 'rgba(255, 255, 255, 0.8)',
              textTransform: 'none',
              fontWeight: 500,
              '&:hover': { background: 'rgba(147, 112, 219, 0.1)', color: '#dda0dd' }
            }}
          >
            Empleados
          </Button>
        </Box>

        {/* Usuario */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          {/* Info del usuario */}
          <Box sx={{ display: { xs: 'none', sm: 'block' }, textAlign: 'right' }}>
            <Typography variant="body2" sx={{ color: '#fff', fontWeight: 600, lineHeight: 1.2 }}>
              {datosEmpleado.nombre} {datosEmpleado.apellido}
            </Typography>
            <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end', mt: 0.5 }}>
              {datosEmpleado.rol && (
                <Chip
                  label={datosEmpleado.rol}
                  size="small"
                  sx={{
                    height: '18px',
                    fontSize: '0.65rem',
                    fontWeight: 600,
                    background: rolColor.bg,
                    color: rolColor.color,
                    border: 'none'
                  }}
                />
              )}
              {datosEmpleado.cargo && (
                <Chip
                  label={datosEmpleado.cargo}
                  size="small"
                  sx={{
                    height: '18px',
                    fontSize: '0.65rem',
                    fontWeight: 500,
                    background: 'rgba(147, 112, 219, 0.15)',
                    color: '#dda0dd',
                    border: 'none'
                  }}
                />
              )}
            </Box>
          </Box>

          {/* Avatar */}
          <IconButton
            onClick={handleMenuOpen}
            sx={{
              background: 'linear-gradient(135deg, #9370db 0%, #6a5acd 100%)',
              width: 40,
              height: 40,
              '&:hover': { background: 'linear-gradient(135deg, #a280e0 0%, #7b6bd4 100%)' }
            }}
          >
            <User size={20} color="#fff" />
          </IconButton>

          {/* Menú */}
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            PaperProps={{
              sx: {
                mt: 1.5,
                minWidth: 250,
                background: 'rgba(26, 26, 46, 0.95)',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(147, 112, 219, 0.2)',
                borderRadius: '12px',
                boxShadow: '0 8px 32px rgba(0, 0, 0, 0.4)'
              }
            }}
          >
            {/* Empresa */}
            <Box sx={{ px: 2, py: 1.5 }}>
              <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.5)' }}>Empresa</Typography>
              <Typography variant="body2" sx={{ color: '#dda0dd', fontWeight: 600 }}>{empresaNombre}</Typography>
              <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.4)' }}>{tenantKey}</Typography>
            </Box>

            <Divider sx={{ borderColor: 'rgba(147, 112, 219, 0.2)', my: 1 }} />

            {/* Usuario */}
            <Box sx={{ px: 2, py: 1.5 }}>
              <Typography variant="caption" sx={{ color: 'rgba(255,255,255,0.5)' }}>Usuario</Typography>
              <Typography variant="body2" sx={{ color: '#fff', fontWeight: 600 }}>
                {datosEmpleado.nombre} {datosEmpleado.apellido}
              </Typography>
              <Box sx={{ display: 'flex', gap: 1, mt: 0.5 }}>
                {datosEmpleado.rol && (
                  <Chip label={datosEmpleado.rol} size="small" sx={{ height: '20px', fontSize: '0.7rem', background: rolColor.bg, color: rolColor.color }} />
                )}
                {datosEmpleado.cargo && (
                  <Chip label={datosEmpleado.cargo} size="small" sx={{ height: '20px', fontSize: '0.7rem', background: 'rgba(147, 112, 219, 0.15)', color: '#dda0dd' }} />
                )}
              </Box>
            </Box>

            <Divider sx={{ borderColor: 'rgba(147, 112, 219, 0.2)', my: 1 }} />

            <MenuItem
              onClick={handleLogout}
              sx={{
                color: '#ef5350',
                gap: 1.5,
                py: 1.5,
                '&:hover': { background: 'rgba(244, 67, 54, 0.1)' }
              }}
            >
              <LogOut size={18} />
              Cerrar Sesión
            </MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  );
}