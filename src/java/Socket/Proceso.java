package Socket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/proceso")
public class Proceso {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println(session.getId() + " ha abierto una conexión");
        try {
            session.getBasicRemote().sendText("Conexión establecida");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Mensaje de " + session.getId() + ": " + message);
        // Envía el mensaje a todos los clientes conectados
        notificarUsuarios(message);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Sesión " + session.getId() + " finalizó la conexión");
    }

    public static void notificarUsuarios(String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        System.err.println("Error al enviar mensaje a la sesión " + session.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}