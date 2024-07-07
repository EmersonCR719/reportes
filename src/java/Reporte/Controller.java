package Reporte;

import Socket.Proceso;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import correo.EnviarMensaje;
import javax.faces.bean.RequestScoped;

@RequestScoped
@ManagedBean
public class Controller {

    @EJB
    VehiculosFacade vf;
    private List<Vehiculos> list;

    JasperPrint jasperPrint;

    @ManagedProperty(value = "#{controller2}")
    private Controller2 controller2;

    @ManagedProperty(value = "#{mail}")
    private EnviarMensaje correo;

    public List<Vehiculos> getList() {
        if (list == null) {
            list = vf.findAll();
        }
        return list;
    }

    public void agregarAuto() {
        controller2.agregarAuto();
    }

    public void setList(List<Vehiculos> list) {
        this.list = list;
    }

    public void init() throws JRException {
        list = getList();
        if (list == null || list.isEmpty()) {
            throw new JRException("La lista de vehículos está vacía.");
        }

        // Imprimir la lista para depuración
        for (Vehiculos v : list) {
            System.out.println(v);
        }

        JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(list);
        String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reporteCompleto.jasper");
        jasperPrint = JasperFillManager.fillReport(reportPath, new HashMap<>(), beanCollectionDataSource);
    }

    public void PDF() throws JRException, IOException {
        init();
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=report.pdf");
        ServletOutputStream servletOutStream = httpServletResponse.getOutputStream();
        try {
            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutStream);
        } catch (JRException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        FacesContext.getCurrentInstance().responseComplete();
        servletOutStream.close();
    }

    // Método para obtener la lista de vehículos Mazda
    public List<Vehiculos> getVehiculosMazda() {
        return vf.findVehiculosByMarca("Mazda");
    }

    // Método para generar el reporte PDF solo con los vehículos Mazda
    public void PDFMazda() throws JRException, IOException {
        List<Vehiculos> vehiculosMazda = getVehiculosMazda();
        if (vehiculosMazda != null && !vehiculosMazda.isEmpty()) {
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(vehiculosMazda);
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("reporteMazda.jasper");
            jasperPrint = JasperFillManager.fillReport(reportPath, new HashMap<>(), beanCollectionDataSource);

            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=reporte_mazda.pdf");
            ServletOutputStream servletOutStream = httpServletResponse.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutStream);

            FacesContext.getCurrentInstance().responseComplete();
            servletOutStream.close();
        } else {
            System.out.println("No se encontraron vehículos Mazda para generar el reporte.");
        }
    }

    //Enviar lista de vehiculos
    public void enviarListaVehiculos() {
        List<Vehiculos> vehiculosList = getList();
        if (vehiculosList != null) {
            correo.enviarMensaje(vehiculosList);
        } else {
            System.out.println("La lista de vehículos es nula.");
        }
    }

    //getter y setter controller2
    public Controller2 getController2() {
        return controller2;
    }

    public void setController2(Controller2 controller2) {
        this.controller2 = controller2;
    }

    // Getter y setter para correo
    public EnviarMensaje getCorreo() {
        return correo;
    }

    public void setCorreo(EnviarMensaje correo) {
        this.correo = correo;
    }

    //cantidad de vehiculos
    public int getCantidadVehiculos() {
        List<Vehiculos> vehiculosList = getList();
        return vehiculosList != null ? vehiculosList.size() : 0;
    }

    public void notificarCantidadCarros() {
        int cantidadCarros = controller2.obtenerCantidadDeCarros();
        String mensaje = "Cantidad de carros en la base de datos: " + cantidadCarros;
        try {
            Proceso.notificarUsuarios(mensaje);
            System.out.println("Notificación enviada: " + mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
