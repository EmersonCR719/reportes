
package Reporte;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class VehiculosFacade extends AbstractFacade<Vehiculos> {

    @PersistenceContext(unitName = "TallerParcialPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VehiculosFacade() {
        super(Vehiculos.class);
    }
    
    // Método para buscar vehículos por marca
    public List<Vehiculos> findVehiculosByMarca(String marca) {
        return em.createQuery("SELECT v FROM Vehiculos v WHERE v.marca = :marca", Vehiculos.class)
                 .setParameter("marca", marca)
                 .getResultList();
    }
}

