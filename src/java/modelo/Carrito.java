package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Carrito implements Serializable{
	
	private static final long serialVersionUID = 1;

	private Long cliente;
	private Long empleado;
	private List<CarritoItem> items;

	public Carrito() {
		items = new ArrayList<CarritoItem>();
	}

	public Long getCliente() {
		return cliente;
	}

	public void setCliente(Long cliente) {
		this.cliente = cliente;
	}

	public Long getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Long empleado) {
		this.empleado = empleado;
	}

	public List<CarritoItem> getItems() {
		return items;
	}

	public void add(CarritoItem item) {
		// Verifica que el item no sea nulo
		if (item == null) {
			return;
		}
		// Busca el item, si lo encuentra suma la cantidad
		boolean encontro = false;
		for (CarritoItem i : items) {
			if ((long)i.getId() == (long)item.getId()) {
				i.setCant(i.getCant() + item.getCant());
				i.setSubtotal(i.getSubtotal() + item.getSubtotal());
				encontro = true;
			}
		}
		// Si no lo encuentra, lo agrega
		if (!encontro) {
			items.add(item);
		}
		// Elimina los items que tienen cantidades menores o iguales que cero.
		int k = 0;
		while (k < items.size()) {
			if (items.get(k).getCant() <= 0) {
				items.remove(k);
			} else {
				k++;
			}
		}
	}

	public Double getTotal() {
		Double t = 0.0;
		for (CarritoItem i : items) {
			t += i.getSubtotal();
		}
		return t;
	}

	public Double getImporte() {
		Double i;
		i = getTotal() / 1.18;
		return i;
	}

	public Double getImpuesto() {
		Double imp;
		imp = getTotal() - getImporte();
		return imp;
	}

	public void remove(Long id) {
		for (CarritoItem i : items) {
			if ((long) i.getId() == (long) id) {
				items.remove(i);
				break;
			}
		}
	}

	public void remove(CarritoItem r) {
		items.remove(r);
	}

	public void clear() {
		items.clear();
	}
}
