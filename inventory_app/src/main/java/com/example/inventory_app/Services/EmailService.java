package com.example.inventory_app.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Servicio para envío de correos electrónicos.
 * 
 * @author Sistema Multi-Tenant
 * @version 1.0
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from:noreply@tuapp.com}")
    private String emailFrom;

    @Value("${app.email.name:Sistema Multi-Tenant}")
    private String emailName;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Envía email de verificación de cuenta.
     * 
     * @param toEmail Email del destinatario
     * @param nombreEmpresa Nombre de la empresa
     * @param tokenVerificacion Token único de verificación
     */
    public void enviarEmailVerificacion(String toEmail, String nombreEmpresa, String tokenVerificacion) {
        try {
            String subject = "Verifica tu cuenta - " + emailName;
            String verificationLink = baseUrl + "/api/auth/verificar-email?token=" + tokenVerificacion;
            
            String htmlContent = construirEmailVerificacion(nombreEmpresa, verificationLink);
            
            enviarEmailHTML(toEmail, subject, htmlContent);
            
            System.out.println("[EMAIL-SERVICE] ✓ Email de verificación enviado a: " + toEmail);
            System.out.println("[EMAIL-SERVICE] Link de verificación: " + verificationLink);
            
        } catch (Exception e) {
            System.err.println("[EMAIL-SERVICE] ✗ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
            // No lanzar excepción para no bloquear el registro
        }
    }

    /**
     * Construye el HTML del email de verificación.
     */
    private String construirEmailVerificacion(String nombreEmpresa, String verificationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .header {
                        background-color: #4CAF50;
                        color: white;
                        padding: 20px;
                        text-align: center;
                        border-radius: 5px 5px 0 0;
                    }
                    .content {
                        background-color: #f9f9f9;
                        padding: 30px;
                        border: 1px solid #ddd;
                        border-top: none;
                    }
                    .button {
                        display: inline-block;
                        padding: 12px 30px;
                        background-color: #4CAF50;
                        color: white !important;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                        font-weight: bold;
                    }
                    .footer {
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #ddd;
                        font-size: 12px;
                        color: #777;
                        text-align: center;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border: 1px solid #ffc107;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🎉 ¡Bienvenido a %s!</h1>
                </div>
                <div class="content">
                    <h2>Hola, %s</h2>
                    
                    <p>Gracias por registrarte en nuestro sistema multi-tenant. Para completar tu registro y activar tu cuenta, por favor verifica tu dirección de correo electrónico.</p>
                    
                    <p style="text-align: center;">
                        <a href="%s" class="button">
                            ✅ Verificar mi cuenta
                        </a>
                    </p>
                    
                    <div class="warning">
                        <strong>⚠️ Importante:</strong>
                        <ul>
                            <li>Este link es válido por 24 horas</li>
                            <li>Solo debes hacer clic una vez</li>
                            <li>No compartas este enlace con nadie</li>
                        </ul>
                    </div>
                    
                    <p>Si no puedes hacer clic en el botón, copia y pega este enlace en tu navegador:</p>
                    <p style="background-color: #eee; padding: 10px; border-radius: 5px; word-break: break-all;">
                        %s
                    </p>
                    
                    <p>Si no creaste esta cuenta, puedes ignorar este correo de forma segura.</p>
                </div>
                <div class="footer">
                    <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                    <p>&copy; 2025 Sistema Multi-Tenant. Todos los derechos reservados.</p>
                </div>
            </body>
            </html>
            """.formatted(emailName, nombreEmpresa, verificationLink, verificationLink);
    }

    /**
     * Envía un email con contenido HTML.
     */
    private void enviarEmailHTML(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(emailFrom, emailName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            
            mailSender.send(message);
            
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Error al configurar el remitente del email", e);
        }
    }

    /**
     * Envía email de bienvenida con primer empleado creado.
     */
    public void enviarEmailPrimerEmpleado(String toEmail, String nombreEmpresa, String usuario, String tenantKey) {
        try {
            String subject = "¡Tu cuenta de empleado está lista!";
            
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>🎉 ¡Bienvenido a %s!</h2>
                    
                    <p>Tu cuenta de empleado ha sido creada exitosamente.</p>
                    
                    <h3>📋 Datos de acceso:</h3>
                    <ul>
                        <li><strong>Usuario:</strong> %s</li>
                        <li><strong>Tenant Key:</strong> %s</li>
                        <li><strong>Empresa:</strong> %s</li>
                    </ul>
                    
                    <p>Usa estos datos para iniciar sesión en el sistema.</p>
                    
                    <p style="color: #ff9800;">
                        ⚠️ <strong>Importante:</strong> Cambia tu contraseña después del primer inicio de sesión.
                    </p>
                    
                    <hr>
                    <p style="font-size: 12px; color: #777;">
                        Este es un correo automático, por favor no respondas.
                    </p>
                </body>
                </html>
                """.formatted(nombreEmpresa, usuario, tenantKey, nombreEmpresa);
            
            enviarEmailHTML(toEmail, subject, htmlContent);
            
            System.out.println("[EMAIL-SERVICE] ✓ Email de bienvenida enviado a: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("[EMAIL-SERVICE] ✗ Error al enviar email: " + e.getMessage());
        }
    }
}
