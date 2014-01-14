
package ejb;

import entity.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import modelo.Carrito;
import modelo.CarritoItem;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class VentaEjbService implements VentaEjbServiceLocal {

	@PersistenceContext(unitName = "VentasJPAPU")
	EntityManager em;
	@Resource
	private UserTransaction utx;
	
	@Override
	public Empleado validarIngreso(String usuario, String clave) throws Exception {
		Empleado emp;
		String q = "select e from Empleado e where e.usuario = :usu and e.clave = :cla";
		Query query = em.createQuery(q);
		query.setParameter("usu", usuario);
		query.setParameter("cla", clave);
		emp = (Empleado) query.getSingleResult();
		if(emp == null){
			throw new Exception("Datos incorrectos.");
		}
		return emp;
	}

	@Override
	public List<Cliente> consultarClientes() throws Exception {
		List<Cliente> lista = null;
		String q = "select c from Cliente c";
		Query query = em.createQuery(q);
		lista = query.getResultList();
		return lista;
	}

	@Override
	public List<Articulo> consultarArticulos() throws Exception {
		List<Articulo> lista = null;
		String q = "select a from Articulo a";
		Query query = em.createQuery(q);
		lista = query.getResultList();
		return lista;
	}

	@Override
	public Articulo consultarArticulo(Long idArt) throws Exception {
		Articulo art;
		String q = "select a from Articulo a where a.id = :id";
		Query query = em.createQuery(q);
		query.setParameter("id", idArt);
		art =  (Articulo) query.getSingleResult();
		if(art == null){
			throw new Exception("Datos incorrectos.");
		}
		return art;
	}

	@Override
	public void grabarVenta(Carrito carrito) throws Exception {
		try {
			utx.begin();
			// Obteniendo el objeto Cliente
			String q = "select c from Cliente c where c.id = :id";
			Query query = em.createQuery(q);
			query.setParameter("id", carrito.getCliente());
			Cliente cliente = (Cliente) query.getSingleResult();
			// Obteniendo el objeto Empleado
			q = "select e from Empleado e where e.id = :id";
			query = em.createQuery(q);
			query.setParameter("id", carrito.getEmpleado());
			Empleado empleado = (Empleado) query.getSingleResult();
			// Capturando la fecha de la base de datos
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			query = em.createNativeQuery("select sysdate() as fecha");
			String fecha = query.getSingleResult().toString();
			// Creando el objeto Venta
			Venta venta = new Venta();
			venta.setCliente(cliente);
			venta.setEmpleado(empleado);
			venta.setSubtotal(carrito.getImporte());
			venta.setImpuesto(carrito.getImpuesto());
			venta.setTotal(carrito.getTotal());
			venta.setFecha(dateFormat.parse(fecha));
			// Agregando los items a la venta
			for (CarritoItem i : carrito.getItems()) {
				Detalle det = new Detalle();
				Articulo art = this.consultarArticulo(i.getId());
				art.setStock((long)art.getStock() - (long)i.getCant());
				det.setArticulo(art);
				det.setCantidad(i.getCant());
				det.setPrecio(i.getPrecio());
				det.setSubtotal(i.getSubtotal());
				det.setVenta(venta);
				venta.getItems().add(det);
			}
			// Grabamos la venta
			em.persist(venta);
			// Confirmar Tx
			utx.commit();
		} catch (Exception ex) {
			utx.rollback();
			throw ex;
		}
	}

	@Override
	public List<Map<String, Object>> resumenVentas() throws Exception {
		List<Map<String,Object>> lista = new ArrayList<Map<String, Object>>();
		String q = "select a, sum(d.cantidad), sum(d.subtotal) "
				  + "from Articulo a, Detalle d "
				  + "where a = d.articulo "
				  + "group by a";
		Query query = em.createQuery(q);
		List listaAux =  query.getResultList();
		for (Object object : listaAux) {
			Object[] rec = (Object[]) object;
			Articulo a = (Articulo) rec[0];
			Map<String,Object> fila = new HashMap<String, Object>();
			fila.put("id", a.getId());
			fila.put("nombre", a.getNombre());
			fila.put("cantidad", rec[1]);
			fila.put("importe", rec[2]);
			lista.add(fila);
		}
		return lista;
	}

	@Override
	public List<Venta> consultarVentas(Date fecha1, Date fecha2) throws Exception {
		List<Venta> lista;
		Calendar c =Calendar.getInstance();
		c.setTime(fecha2);
		c.add(Calendar.DAY_OF_MONTH, 1);
		fecha2 = c.getTime();
		String q = "select v "
				  + "from Venta v "
				  + "where v.fecha >= :fe1 and v.fecha < :fe2" ;
		System.err.println("ERROR FECHA 1: " + fecha1);
		System.err.println("ERROR FECHA 2: " + fecha2);
		Query query = em.createQuery(q);
		query.setParameter("fe1", fecha1);
		query.setParameter("fe2", fecha2);
		lista = query.getResultList();
		return lista;
	}

	@Override
	public List<Map<String, Object>> resumenPorCliente() throws Exception {
		List<Map<String,Object>> lista = new ArrayList<Map<String, Object>>();
		String q = "select c, sum(d.cantidad), sum(d.subtotal) "
				  + "from Cliente c, Venta v, Detalle d "
				  + "where c = v.cliente and v = d.venta "
				  + "group by c";
		Query query = em.createQuery(q);
		List listaAux =  query.getResultList();
		for (Object object : listaAux) {
			Object[] rec = (Object[]) object;
			Cliente c = (Cliente) rec[0];
			Map<String,Object> fila = new HashMap<String, Object>();
			fila.put("id", c.getId());
			fila.put("nombre", c.getNombre());
			fila.put("cantidad", rec[1]);
			fila.put("importe", rec[2]);
			lista.add(fila);
		}
		return lista;
	}
    
 
}
