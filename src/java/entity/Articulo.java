package entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "ARTICULO")
public class Articulo implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "art_id")
	private Long id;
	@Column(name = "art_codigo", length = 20, nullable = false)
	private String codigo;
	@Column(name = "art_nombre", length = 100, nullable = false)
	private String nombre;
	@Column(name = "art_precio", precision = 2, nullable = false)
	private Double precio;
	@Column(name = "art_stock", nullable = false)
	private Long stock;

	public Articulo() {
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Long getStock() {
		return stock;
	}

	public void setStock(Long stock) {
		this.stock = stock;
	}
}
