// Archivo temporal para verificar variables de entorno
console.log('🔧 Environment Check:');
console.log('VITE_API_URL:', import.meta.env.VITE_API_URL);
console.log('MODE:', import.meta.env.MODE);
console.log('DEV:', import.meta.env.DEV);
console.log('PROD:', import.meta.env.PROD);

export const checkEnv = () => {
  if (!import.meta.env.VITE_API_URL) {
    console.error('❌ VITE_API_URL is not defined!');
    return false;
  }
  console.log('✅ VITE_API_URL is defined:', import.meta.env.VITE_API_URL);
  return true;
};
