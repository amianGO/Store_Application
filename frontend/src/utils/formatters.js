/**
 * Formatea un precio en pesos colombianos (COP)
 * @param {number} price - El precio a formatear
 * @returns {string} - El precio formateado con símbolo COP
 */
export const formatCOP = (price) => {
  if (price === null || price === undefined || isNaN(price)) {
    return 'COP $0';
  }
  
  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  }).format(price);
};

/**
 * Formatea un número sin símbolo de moneda pero con separadores
 * @param {number} number - El número a formatear
 * @returns {string} - El número formateado
 */
export const formatNumber = (number) => {
  if (number === null || number === undefined || isNaN(number)) {
    return '0';
  }
  
  return new Intl.NumberFormat('es-CO').format(number);
};
