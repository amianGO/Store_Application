import axios from 'axios';

// ============================================
// CONFIGURACIÓN AXIOS PARA MULTI-TENANT
// ============================================

// Determinar la URL base del API
const getBaseURL = () => {
  // En producción (Vercel), usar backend de Render
  if (window.location.hostname.includes('vercel.app')) {
    console.log('🌐 Detectado dominio de Vercel, usando backend de Render');
    return 'https://store-backend-4g34.onrender.com/api';
  }
  // En desarrollo, intentar variable de entorno primero, luego localhost
  const devURL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';
  console.log('🔧 Modo desarrollo, usando:', devURL);
  return devURL;
};

// Configuración base de Axios
const api = axios.create({
  baseURL: getBaseURL(),
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 60000, // 60 segundos (Render free tier puede tardar en operaciones pesadas)
});

console.log('✅ Axios configurado con baseURL:', api.defaults.baseURL);

// ============================================
// INTERCEPTOR DE REQUEST (Agregar JWT)
// ============================================
api.interceptors.request.use(
  (config) => {
    // Agregar token JWT si existe
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // Log para debugging
    console.log('📤 Request:', config.method.toUpperCase(), config.url);
    
    return config;
  },
  (error) => {
    console.error('❌ Error en request:', error);
    return Promise.reject(error);
  }
);

// ============================================
// INTERCEPTOR DE RESPONSE (Manejar errores)
// ============================================
api.interceptors.response.use(
  (response) => {
    console.log('✅ Response:', response.status, response.config.url);
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      
      console.error('❌ Error Response:', {
        status,
        url: error.config.url,
        message: data.mensaje || data.message || 'Error desconocido'
      });

      // Si el token es inválido o expiró (401 Unauthorized)
      if (status === 401) {
        console.warn('🔒 Token inválido o expirado. Redirigiendo al login...');
        
        // Limpiar localStorage
        localStorage.clear();
        
        // Redirigir al login
        window.location.href = '/login';
      }

      // Si no tiene acceso (403 Forbidden)
      if (status === 403) {
        console.warn('🚫 Acceso denegado');
      }

      // Error del servidor (500)
      if (status >= 500) {
        console.error('🔥 Error del servidor');
      }
    } else if (error.request) {
      console.error('📡 No se recibió respuesta del servidor:', error.request);
    } else {
      console.error('⚠️ Error configurando request:', error.message);
    }

    return Promise.reject(error);
  }
);

export default api;
