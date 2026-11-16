package bj.restaurant.couts.dto;

import bj.restaurant.couts.Entities.Production;
import java.time.format.DateTimeFormatter;

public class ProductionVendable {
	private final Production production;
	private final long portionsVendues;

	public ProductionVendable(Production production, long portionsVendues) {
		this.production = production;
		this.portionsVendues = portionsVendues;
	}

	public Production getProduction() { return production; }
	public long getPortionsVendues() { return portionsVendues; }
	public int getPortionsDisponibles() {
		return production.getPortionsReelles() - Math.toIntExact(portionsVendues);
	}
	public String getDateAffichee() {
		return production.getDateProduction().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
	}
}
