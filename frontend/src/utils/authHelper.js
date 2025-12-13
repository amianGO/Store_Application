/**
 * Helper para manejar la autenticación y almacenamiento de datos del empleado
 */

/**
 * Guarda los datos del empleado después del login exitoso
 * @param {Object} loginResponse - Respuesta del endpoint de login
 */
export const guardarDatosEmpleado = (loginResponse) => {
  const { token, empleadoId, usuario, rol, nombre, apellido, cargo } = loginResponse;
  
  // Guardar token
  localStorage.setItem('token', token);
  
  // Guardar datos del empleado
  localStorage.setItem('empleadoId', empleadoId.toString());
  localStorage.setItem('empleadoNombre', `${nombre} ${apellido}`);
  localStorage.setItem('empleadoRol', rol);
  localStorage.setItem('empleadoUsuario', usuario);
  localStorage.setItem('empleadoCargo', cargo);
  
  console.log('✅ Datos del empleado guardados:', {
    empleadoId,
    nombre: `${nombre} ${apellido}`,
    rol,
    usuario,
    cargo
  });
};

/**
 * Obtiene el ID del empleado desde localStorage o token
 * @returns {number|null} - ID del empleado
 */
export const obtenerEmpleadoId = () => {
  // Intentar desde localStorage
  let empleadoId = localStorage.getItem('empleadoId');
  
  // Si no existe, extraer del token
  if (!empleadoId) {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        empleadoId = payload.empleadoId;
        if (empleadoId) {
          localStorage.setItem('empleadoId', empleadoId.toString());
        }
      } catch (error) {
        console.error('Error al extraer empleadoId del token:', error);
      }
    }
  }
  
  return empleadoId ? parseInt(empleadoId) : null;
};

/**
 * Obtiene el nombre completo del empleado
 * @returns {string} - Nombre del empleado
 */
export const obtenerNombreEmpleado = () => {
  let nombre = localStorage.getItem('empleadoNombre');
  
  if (!nombre) {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        nombre = payload.sub || 'Usuario';
      } catch (error) {
        nombre = 'Usuario';
      }
    }
  }
  
  return nombre || 'Usuario';
};

/**
 * Limpia todos los datos del empleado al cerrar sesión
 */
export const limpiarDatosEmpleado = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('empleadoId');
  localStorage.removeItem('empleadoNombre');
  localStorage.removeItem('empleadoRol');
  localStorage.removeItem('empleadoUsuario');
  localStorage.removeItem('empleadoCargo');
  console.log('🔒 Sesión cerrada - Datos eliminados');
};
