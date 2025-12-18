import axios from 'axios';

// ============================================
// CONFIGURACIÓN AXIOS PARA MULTI-TENANT
// ============================================

// Determinar la URL base del API
const getBaseURL = () => {
  // En producción (Vercel), usar backend de Render
  if (window.location.hostname.includes('vercel.app')) {
    console.log('Detectado dominio de Vercel, usando backend de Render');
    return 'https://store-backend-4g34.onrender.com/api';
  }
  // En desarrollo, intentar variable de entorno primero, luego localhost
  const devURL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  console.log('Modo desarrollo, usando:', devURL);
  return devURL;
};

// Configuración base de Axios
const api = axios.create({
  baseURL: getBaseURL(),
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 120000, // 120 segundos (registro de empresa tarda mucho en Render free tier)
});

console.log('Axios configurado con baseURL:', api.defaults.baseURL);

// ============================================
// INTERCEPTOR DE REQUEST (Agregar JWT)
// ============================================
api.interceptors.request.use(
  (config) => {
    // No adjuntar Authorization a endpoints públicos de auth
    const url = config.url || '';
    const isAuthEndpoint = url.startsWith('/auth') || url.includes('/api/auth');

  // Log para debugging
  console.log('Request:', config.method ? config.method.toUpperCase() : 'GET', config.url);

    if (!isAuthEndpoint) {
      // Agregar token JWT si existe y no está visiblemente expirado
      const token = localStorage.getItem('token');
      if (token) {
        try {
          // Decodificar payload del JWT para una comprobación básica de expiración
          const payload = JSON.parse(atob(token.split('.')[1]));
          const exp = payload.exp;
          if (exp && typeof exp === 'number') {
            const now = Math.floor(Date.now() / 1000);
            if (exp > now) {
              config.headers.Authorization = `Bearer ${token}`;
            } else {
              console.warn('JWT expirado en localStorage, removiendo token');
              localStorage.removeItem('token');
            }
          } else {
            // Si no tiene exp, adjuntarlo (con precaución)
              config.headers.Authorization = `Bearer ${token}`;
          }
        } catch (e) {
          console.warn('Error al parsear JWT desde localStorage, removiendo token', e);
          localStorage.removeItem('token');
        }
      }
    } else {
      console.log('Petición a endpoint de auth detectada, no se adjuntará Authorization');
    }
    
    return config;
  },
  (error) => {
    console.error('Error en request:', error);
    return Promise.reject(error);
  }
);

// ============================================
// INTERCEPTOR DE RESPONSE (Manejar errores)
// ============================================
api.interceptors.response.use(
  (response) => {
    console.log('Response:', response.status, response.config.url);
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      
  console.error('Error Response:', {
        status,
        url: error.config.url,
        message: data.mensaje || data.message || 'Error desconocido'
      });

      // Si el token es inválido o expiró (401 Unauthorized)
      if (status === 401) {
        console.warn('Token inválido o expirado. Removiendo token y redirigiendo al login...');

        // Remover sólo la clave token para no borrar otros datos locales
        localStorage.removeItem('token');

        // Redirigir al login
        window.location.href = '/login';
      }

      // Si no tiene acceso (403 Forbidden)
      if (status === 403) {
        console.warn('Acceso denegado');
      }

      // Error del servidor (500)
      if (status >= 500) {
        console.error('Error del servidor');
      }
    } else if (error.request) {
      console.error('No se recibió respuesta del servidor:', error.request);
    } else {
      console.error('Error configurando request:', error.message);
    }

    return Promise.reject(error);
  }
);

export default api;
