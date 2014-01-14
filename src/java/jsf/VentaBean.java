package jsf;

import ejb.VentaEjbServiceLocal;
import entity.Articulo;
import entity.Cliente;
import entity.Empleado;
import entity.Venta;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import modelo.Carrito;
import modelo.CarritoItem;

@ManagedBean
@SessionScoped
public class VentaBean implements Serializable {

	private static final long serialVersionUID = 1;
	
	@EJB
	private VentaEjbServiceLocal ventaEjb;
	// Empleado
	private Empleado empleado = null;
	private String usuario = "";
	private String clave = "";
	private String mensaje = "";
	// Venta
	private Long idcliente;
	private Long idArticulo;
	private Double precio = 0.0;
	private Long cant = 1L;
	private Double subtotal = 0.0;
	private List<SelectItem> listaClientes = null;
	private List<SelectItem> listaArticulos = null;
	private Carrito carrito = new Carrito();
	// Consultas
	private Date fecha1;
	private Date fecha2;
	private List<Map<String, Object>> resumenVentas = null;
	private List<Map<String, Object>> resumenPorCliente = null;
	private List<Venta> ventasPorFecha = null;

	public VentaBean() {
	}

	public List<Map<String, Object>> getResumenPorCliente() {
		return resumenPorCliente;
	}

	public List<Venta> getVentasPorFecha() {
		return ventasPorFecha;
	}

	public Date getFecha1() {
		return fecha1;
	}

	public void setFecha1(Date fecha1) {
		this.fecha1 = fecha1;
	}

	public Date getFecha2() {
		return fecha2;
	}

	public void setFecha2(Date fecha2) {
		this.fecha2 = fecha2;
	}

	public Carrito getCarrito() {
		return carrito;
	}

	public List<Map<String, Object>> getResumenVentas() {
		return resumenVentas;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public Long getCant() {
		return cant;
	}

	public void setCant(Long cant) {
		this.cant = cant;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Long getIdcliente() {
		return idcliente;
	}

	public void setIdcliente(Long idcliente) {
		this.idcliente = idcliente;
	}

	public Long getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(Long idArticulo) {
		this.idArticulo = idArticulo;
	}

	public List<SelectItem> getListaArticulos() {
		if (listaArticulos == null) {
			try {
				listaArticulos = new ArrayList<SelectItem>();
				List<Articulo> lista = ventaEjb.consultarArticulos();
				listaArticulos.add(new SelectItem(0, "Seleccione un articulo"));
				for (Articulo o : lista) {
					SelectItem op = new SelectItem();
					op.setValue(o.getId());
					op.setLabel(o.getNombre());
					listaArticulos.add(op);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return listaArticulos;
	}

	public List<SelectItem> getListaClientes() {
		if (listaClientes == null) {
			try {
				listaClientes = new ArrayList<SelectItem>();
				listaClientes.add(new SelectItem(0, "Seleccione un cliente"));
				List<Cliente> lista = ventaEjb.consultarClientes();
				for (Cliente c : lista) {
					SelectItem op = new SelectItem();
					op.setValue(c.getId());
					op.setLabel(c.getNombre());
					listaClientes.add(op);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return listaClientes;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String doIngresar() {
		String destino = "ventas";
		try {
			empleado = ventaEjb.validarIngreso(usuario, clave);
		} catch (Exception e) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datos incorrectos.", "Intentelo de nuevo.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
			destino = "index";
                        e.printStackTrace();
		}
		return destino;
	}

	public String doSalir() {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		req.getSession(true).invalidate();
		return "index2";
	}

	public void modificarPrecio(ValueChangeEvent event) {
		try {
			Long idArt = (Long) event.getNewValue();
			Articulo a = ventaEjb.consultarArticulo(idArt);
			precio = a.getPrecio();
			subtotal = precio * cant;
		} catch (Exception e) {
			precio = 0.0;
		}
	}

	public void modificarCantidad(ValueChangeEvent event) {
		try {
			Long c = (Long) event.getNewValue();
			subtotal = precio * c;
		} catch (Exception e) {
			precio = 0.0;
		}
	}

	public String doAgregarItem() {
		try {
			Articulo a = ventaEjb.consultarArticulo(idArticulo);
			CarritoItem item = new CarritoItem();
			item.setId(a.getId());
			item.setCodigo(a.getCodigo());
			item.setNombre(a.getNombre());
			item.setPrecio(a.getPrecio());
			item.setCant(cant);
			item.setSubtotal(item.getPrecio() * item.getCant());
			carrito.add(item);
		} catch (Exception e) {
		}
		return "ventas";
	}

	public String doEliminarItem(Long id) {
		carrito.remove(id);
		return "ventas";
	}
	
	public String doEliminarItem(CarritoItem r) {
		carrito.remove(r);
		return "ventas";
	}

	public String doGrabar() {
		mensaje = "";
		try {
			carrito.setCliente(idcliente);
			carrito.setEmpleado(empleado.getId());
			ventaEjb.grabarVenta(carrito);
			carrito.clear();
			mensaje = "Proceso ok.";
		} catch (Exception e) {
			mensaje = e.getMessage();
			e.printStackTrace();
		}
		return "ventas";
	}

	public String doResumenVentas() {
		mensaje = "";
		try {
			resumenVentas = ventaEjb.resumenVentas();
		} catch (Exception e) {
		}
		return "resumenVentas";
	}
	
	public String doResumenPorCliente() {
		mensaje = "";
		try {
			resumenPorCliente = ventaEjb.resumenPorCliente();
		} catch (Exception e) {
		}
		return "resumenPorCliente";
	}

	public String doVentasPorFecha() {
		mensaje = "";
		try {
			System.err.println("X ERROR FECHA 1: " + fecha1);
			System.err.println("X ERROR FECHA 2: " + fecha2);
			ventasPorFecha = ventaEjb.consultarVentas(fecha1, fecha2);
		} catch (Exception e) {
		}
		return "consultarPorFecha";
	}
}
