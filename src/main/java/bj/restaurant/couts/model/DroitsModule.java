package bj.restaurant.couts.model;

public class DroitsModule {
	private ModuleApplication module;
	private boolean consultationRole;
	private boolean modificationRole;
	private String consultationPersonnalisee = "HERITER";
	private String modificationPersonnalisee = "HERITER";
	private boolean consultationEffective;
	private boolean modificationEffective;

	public ModuleApplication getModule() { return module; }
	public void setModule(ModuleApplication module) { this.module = module; }
	public boolean isConsultationRole() { return consultationRole; }
	public void setConsultationRole(boolean consultationRole) { this.consultationRole = consultationRole; }
	public boolean isModificationRole() { return modificationRole; }
	public void setModificationRole(boolean modificationRole) { this.modificationRole = modificationRole; }
	public String getConsultationPersonnalisee() { return consultationPersonnalisee; }
	public void setConsultationPersonnalisee(String consultationPersonnalisee) {
		this.consultationPersonnalisee = consultationPersonnalisee;
	}
	public String getModificationPersonnalisee() { return modificationPersonnalisee; }
	public void setModificationPersonnalisee(String modificationPersonnalisee) {
		this.modificationPersonnalisee = modificationPersonnalisee;
	}
	public boolean isConsultationEffective() { return consultationEffective; }
	public void setConsultationEffective(boolean consultationEffective) {
		this.consultationEffective = consultationEffective;
	}
	public boolean isModificationEffective() { return modificationEffective; }
	public void setModificationEffective(boolean modificationEffective) {
		this.modificationEffective = modificationEffective;
	}
}
