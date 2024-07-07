package correo;

import java.util.List;
import java.util.Properties;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import Reporte.Vehiculos;

@RequestScoped
@ManagedBean(name="mail")
public class EnviarMensaje {

    private String mensaje;
    private String asunto;
    private String paraQuien;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getParaQuien() {
        return paraQuien;
    }

    public void setParaQuien(String paraQuien) {
        this.paraQuien = paraQuien;
    }

    public void enviarMensaje(List<Vehiculos> vehiculosList) {
        String smtp = "smtp.gmail.com";
        int port = 587;
        String username = "emersonalbornozalvarez@gmail.com";
        String password = "aqnh bsrt ijwq gtml";
        
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setSubject(asunto);
            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(paraQuien));

            StringBuilder body = new StringBuilder();
            body.append(mensaje).append("\n\nLista de Vehículos:\n");
            for (Vehiculos vehiculo : vehiculosList) {
                body.append("ID: ").append(vehiculo.getId())
                    .append(", Marca: ").append(vehiculo.getMarca())
                    .append(", Placa: ").append(vehiculo.getPlaca())
                    .append(", Modelo: ").append(vehiculo.getModelo())
                    .append(", Color: ").append(vehiculo.getColor())
                    .append("\n");
            }

            message.setText(body.toString());
            Transport.send(message);
            System.out.println("Mensaje enviado correctamente");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el mensaje");
        }
    }
}
