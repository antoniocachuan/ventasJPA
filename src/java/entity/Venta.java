

package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="VENTA")
public class Venta implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ven_id")
	private Long id;
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name = "cli_id", referencedColumnName="cli_id", nullable = false)
	private Cliente cliente;
	@Column(name = "ven_fecha")
   @Temporal(TemporalType.DATE)
	private Date fecha;
	@Column(name="ven_subtotal",precision=2,nullable=false)
	private Double subtotal;
	@Column(name="ven_impuesto",precision=2,nullable=false)
	private Double impuesto;
	@Column(name="ven_total",precision=2,nullable=false)
	private Double total;
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name = "emp_id", referencedColumnName="emp_id", nullable = false)
	private Empleado empleado;
	@OneToMany(fetch=FetchType.EAGER, mappedBy = "venta", cascade= CascadeType.ALL)
	private List<Detalle> items = null;


	public Venta() {
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getImpuesto() {
		return impuesto;
	}

	public void setImpuesto(Double impuesto) {
		this.impuesto = impuesto;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public List<Detalle> getItems() {
		if( items == null ){
			items = new ArrayList<Detalle>();
		}
		return items;
	}

	public void setItems(List<Detalle> items) {
		this.items = items;
	}



}
