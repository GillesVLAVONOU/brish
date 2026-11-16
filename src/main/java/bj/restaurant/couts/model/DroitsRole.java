package bj.restaurant.couts.model;

public record DroitsRole(boolean consultation, boolean modification) {
	public DroitsRole avecModification() {
		return modification && !consultation
				? new DroitsRole(true, true)
				: this;
	}
}
