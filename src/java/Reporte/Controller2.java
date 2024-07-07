package Reporte;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import Socket.Proceso;

@ManagedBean(name="controller2")
@SessionScoped
public class Controller2 implements Serializable {

    @EJB
    private VehiculosFacade vehiculosFacade;

    private Vehiculos vehiculo = new Vehiculos();

    public Vehiculos getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculos vehiculo) {
        this.vehiculo = vehiculo;
    }

    public void agregarAuto() {
        vehiculosFacade.create(vehiculo);
        System.out.println("Vehículo agregado: " + vehiculo);

        // Notificar a todos los usuarios conectados
        Proceso.notificarUsuarios("Se ha agregado un nuevo vehículo: " + vehiculo);

        // Mostrar mensaje en JSF
        addMessage("Vehículo agregado correctamente!");

        vehiculo = new Vehiculos(); // Resetea la instancia de vehiculo para el siguiente input
    }
    
    public void notificarCantidadCarros() {
        int cantidad = obtenerCantidadDeCarros();
        // Notificar a todos los usuarios conectados
        Proceso.notificarUsuarios("Cantidad de carros en la base de datos: " + cantidad);

        // Mostrar mensaje en JSF
        addMessage("Cantidad de carros: " + cantidad);
    }

    public int obtenerCantidadDeCarros() {
        return vehiculosFacade.findAll().size(); // Obtener la cantidad de carros en la BD
    }

    public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}