

package ejb;

import entity.Articulo;
import entity.Cliente;
import entity.Empleado;
import entity.Venta;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import modelo.Carrito;

@Local
public interface VentaEjbServiceLocal {

	Empleado validarIngreso(String usuario, String clave) throws Exception ;
	List<Cliente> consultarClientes() throws Exception;
	List<Articulo> consultarArticulos() throws Exception;
	Articulo consultarArticulo(Long idArt) throws Exception;
	void grabarVenta(Carrito carrito) throws Exception;
	List<Map<String,Object>> resumenVentas() throws Exception;
	List<Venta> consultarVentas(Date fecha1, Date fecha2) throws Exception;
	List<Map<String,Object>> resumenPorCliente() throws Exception;
}
