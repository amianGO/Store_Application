import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import { formatCOP } from './formatters';

export const generateFacturaPDF = async (factura) => {
  // Crear elemento temporal para el PDF
  const pdfElement = document.createElement('div');
  pdfElement.style.position = 'absolute';
  pdfElement.style.left = '-9999px';
  pdfElement.style.top = '-9999px';
  pdfElement.style.width = '800px';
  pdfElement.style.padding = '40px';
  pdfElement.style.backgroundColor = 'white';
  pdfElement.style.fontFamily = 'Arial, sans-serif';
  pdfElement.style.color = '#333';

  // Crear contenido HTML para la factura
  pdfElement.innerHTML = `
    <div style="max-width: 800px; margin: 0 auto; background: white; padding: 40px;">
      <!-- Encabezado -->
      <div style="text-align: center; margin-bottom: 40px; border-bottom: 3px solid #4caf50; padding-bottom: 20px;">
        <h1 style="color: #4caf50; margin: 0; font-size: 36px; font-weight: bold;">FACTURA DE VENTA</h1>
        <p style="margin: 10px 0 0 0; font-size: 16px; color: #666;">Sistema de Inventario</p>
      </div>

      <!-- Información de la factura -->
      <div style="display: flex; justify-content: space-between; margin-bottom: 30px;">
        <div style="flex: 1;">
          <h3 style="color: #333; margin-bottom: 15px; font-size: 18px;">Información de la Factura</h3>
          <div style="background: #f8f9fa; padding: 15px; border-radius: 8px;">
            <p style="margin: 5px 0;"><strong>Número:</strong> ${factura.numeroFactura}</p>
            <p style="margin: 5px 0;"><strong>Fecha:</strong> ${new Date(factura.fechaEmision).toLocaleString('es-CO')}</p>
            <p style="margin: 5px 0;"><strong>Estado:</strong> <span style="color: #4caf50; font-weight: bold;">${factura.estado}</span></p>
          </div>
        </div>
        
        <div style="flex: 1; margin-left: 30px;">
          <h3 style="color: #333; margin-bottom: 15px; font-size: 18px;">Información del Cliente</h3>
          <div style="background: #f8f9fa; padding: 15px; border-radius: 8px;">
            <p style="margin: 5px 0;"><strong>Nombre:</strong> ${factura.clienteNombre}</p>
            <p style="margin: 5px 0;"><strong>ID Cliente:</strong> ${factura.clienteId}</p>
          </div>
        </div>
      </div>

      <!-- Información del empleado -->
      <div style="margin-bottom: 30px;">
        <h3 style="color: #333; margin-bottom: 15px; font-size: 18px;">Atendido por</h3>
        <div style="background: #e3f2fd; padding: 15px; border-radius: 8px;">
          <p style="margin: 5px 0;"><strong>Empleado:</strong> ${factura.empleadoNombre}</p>
          <p style="margin: 5px 0;"><strong>ID Empleado:</strong> ${factura.empleadoId}</p>
        </div>
      </div>

      <!-- Tabla de productos -->
      <div style="margin-bottom: 30px;">
        <h3 style="color: #333; margin-bottom: 15px; font-size: 18px;">Detalle de Productos</h3>
        <table style="width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
          <thead>
            <tr style="background: #4caf50; color: white;">
              <th style="padding: 12px 8px; text-align: left; border: 1px solid #ddd;">Código</th>
              <th style="padding: 12px 8px; text-align: left; border: 1px solid #ddd;">Producto</th>
              <th style="padding: 12px 8px; text-align: center; border: 1px solid #ddd;">Cant.</th>
              <th style="padding: 12px 8px; text-align: right; border: 1px solid #ddd;">Precio Unit.</th>
              <th style="padding: 12px 8px; text-align: center; border: 1px solid #ddd;">Desc. %</th>
              <th style="padding: 12px 8px; text-align: right; border: 1px solid #ddd;">Subtotal</th>
            </tr>
          </thead>
          <tbody>
            ${factura.detalles.map((detalle, index) => `
              <tr style="background: ${index % 2 === 0 ? '#f8f9fa' : 'white'};">
                <td style="padding: 10px 8px; border: 1px solid #ddd; font-weight: 500;">${detalle.productoCodigo}</td>
                <td style="padding: 10px 8px; border: 1px solid #ddd;">
                  <div>
                    <div style="font-weight: 500; margin-bottom: 2px;">${detalle.productoNombre}</div>
                    <div style="font-size: 12px; color: #666;">${detalle.productoCategoria}</div>
                  </div>
                </td>
                <td style="padding: 10px 8px; border: 1px solid #ddd; text-align: center; font-weight: 500;">${detalle.cantidad}</td>
                <td style="padding: 10px 8px; border: 1px solid #ddd; text-align: right;">${formatCOP(detalle.precioUnitario)}</td>
                <td style="padding: 10px 8px; border: 1px solid #ddd; text-align: center;">
                  ${detalle.descuento > 0 ? `<span style="color: #ff9800; font-weight: 500;">${detalle.descuento}%</span>` : '-'}
                </td>
                <td style="padding: 10px 8px; border: 1px solid #ddd; text-align: right; font-weight: 500;">${formatCOP(detalle.subtotal)}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      </div>

      <!-- Totales -->
      <div style="display: flex; justify-content: flex-end; margin-bottom: 40px;">
        <div style="background: #f8f9fa; padding: 20px; border-radius: 8px; min-width: 300px; border-left: 4px solid #4caf50;">
          <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
            <span style="font-size: 16px;">Subtotal:</span>
            <span style="font-size: 16px; font-weight: 500;">${formatCOP(factura.subtotal)}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
            <span style="font-size: 16px;">IVA (19%):</span>
            <span style="font-size: 16px; font-weight: 500;">${formatCOP(factura.impuesto)}</span>
          </div>
          <div style="border-top: 2px solid #4caf50; padding-top: 10px; margin-top: 10px;">
            <div style="display: flex; justify-content: space-between;">
              <span style="font-size: 20px; font-weight: bold; color: #4caf50;">TOTAL:</span>
              <span style="font-size: 20px; font-weight: bold; color: #4caf50;">${formatCOP(factura.total)}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Pie de página -->
      <div style="text-align: center; border-top: 2px solid #e0e0e0; padding-top: 20px; color: #666;">
        <p style="margin: 5px 0; font-size: 14px;">Gracias por su compra</p>
        <p style="margin: 5px 0; font-size: 12px;">Sistema de Inventario - ${new Date().getFullYear()}</p>
        <p style="margin: 5px 0; font-size: 12px;">Factura generada automáticamente</p>
      </div>
    </div>
  `;

  // Agregar al DOM temporalmente
  document.body.appendChild(pdfElement);

  try {
    // Generar imagen del contenido
    const canvas = await html2canvas(pdfElement, {
      scale: 2,
      useCORS: true,
      allowTaint: false,
      backgroundColor: 'white'
    });

    // Crear PDF
    const pdf = new jsPDF('p', 'mm', 'a4');
    const imgData = canvas.toDataURL('image/png');
    
    // Calcular dimensiones para ajustar a la página
    const imgWidth = 210; // A4 width in mm
    const pageHeight = 295; // A4 height in mm
    const imgHeight = (canvas.height * imgWidth) / canvas.width;
    let heightLeft = imgHeight;
    let position = 0;

    // Agregar primera página
    pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
    heightLeft -= pageHeight;

    // Agregar páginas adicionales si es necesario
    while (heightLeft >= 0) {
      position = heightLeft - imgHeight;
      pdf.addPage();
      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;
    }

    // Descargar PDF
    pdf.save(`Factura_${factura.numeroFactura}.pdf`);

  } catch (error) {
    console.error('Error generando PDF:', error);
    alert('Error al generar el PDF. Por favor intente nuevamente.');
  } finally {
    // Limpiar elemento temporal
    document.body.removeChild(pdfElement);
  }
};
