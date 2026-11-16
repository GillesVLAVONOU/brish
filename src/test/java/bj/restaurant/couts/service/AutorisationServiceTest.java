package bj.restaurant.couts.service;

import bj.restaurant.couts.dao.AutorisationDAO;
import bj.restaurant.couts.model.DroitsPersonnalises;
import bj.restaurant.couts.model.DroitsRole;
import bj.restaurant.couts.model.ModuleApplication;
import bj.restaurant.couts.model.Role;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AutorisationServiceTest {
	private AutorisationService service;
	private StubAutorisationDAO dao;

	@Before
	public void setUp() throws Exception {
		service = new AutorisationService();
		dao = new StubAutorisationDAO();
		Field field = AutorisationService.class.getDeclaredField("autorisationDAO");
		field.setAccessible(true);
		field.set(service, dao);
	}

	@Test
	public void droitsDeRoleManquantsUtilisentLesValeursParDefaut() {
		assertTrue(service.estAutorise(12, Role.GESTIONNAIRE, ModuleApplication.DASHBOARD, "GET"));
		assertTrue(service.estAutorise(12, Role.GESTIONNAIRE, ModuleApplication.DASHBOARD, "POST"));
	}

	@Test
	public void refusPersonnaliseRestePrioritaireSurLeDroitParDefaut() {
		dao.droitsPersonnalises.put(ModuleApplication.DASHBOARD, new DroitsPersonnalises(false, false));

		assertFalse(service.estAutorise(12, Role.GESTIONNAIRE, ModuleApplication.DASHBOARD, "GET"));
		assertFalse(service.estAutorise(12, Role.GESTIONNAIRE, ModuleApplication.DASHBOARD, "POST"));
	}

	private static class StubAutorisationDAO extends AutorisationDAO {
		private final Map<ModuleApplication, DroitsRole> droitsRole = new EnumMap<>(ModuleApplication.class);
		private final Map<ModuleApplication, DroitsPersonnalises> droitsPersonnalises =
				new EnumMap<>(ModuleApplication.class);

		@Override
		public Map<ModuleApplication, DroitsRole> chargerDroitsRole(Role role) {
			return droitsRole;
		}

		@Override
		public Map<ModuleApplication, DroitsPersonnalises> chargerDroitsPersonnalises(Integer utilisateurId) {
			return droitsPersonnalises;
		}
	}
}
