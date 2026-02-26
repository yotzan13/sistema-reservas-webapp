package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailUtil {

    private static final String HOST     = "smtp.gmail.com";
    private static final int    PORT     = 587;
    private static final String FROM     = "tu_correo@gmail.com";       // Cambiar
    private static final String PASSWORD = "tu_app_password";           // App password de Gmail

    private EmailUtil() {}

    public static void enviarConfirmacion(String destinatario,
                                          String nombreCliente,
                                          String fecha,
                                          String hora,
                                          int numeroMesa) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Confirmación de reserva - El Sultán");

            String body = String.format("""
                    Estimado/a %s,
                    
                    Su reserva ha sido confirmada con los siguientes detalles:
                    
                    📅 Fecha: %s
                    🕐 Hora:  %s
                    🪑 Mesa:  Nro %d
                    
                    Recuerde que puede cancelar hasta 1 día antes de la fecha.
                    
                    ¡Gracias por elegirnos!
                    El equipo de El Sultán 🫙
                    """, nombreCliente, fecha, hora, numeroMesa);

            message.setText(body);
            Transport.send(message);

        } catch (MessagingException e) {
            System.err.println("Error al enviar correo a " + destinatario + ": " + e.getMessage());
        }
    }
}