package bj.restaurant.couts.service;

import bj.restaurant.couts.dto.RapportAchatLigne;
import bj.restaurant.couts.dto.RapportMargeLigne;
import bj.restaurant.couts.dto.RapportProductionLigne;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class RapportPdfServiceTest {
	private final RapportPdfService service = new RapportPdfService();

	@Test
	public void genereLesTroisRapportsAvecDonnees() throws Exception {
		assertPdf(service.genererPdf(RapportPdfService.TypeRapport.COUTS,
				List.of(new RapportProductionLigne(1, LocalDateTime.of(2026, 11, 15, 12, 0),
						"Mets", 1, 2, montant("100"), montant("20"), montant("120"), montant("60"))),
				parametresCommuns(Map.of(
						"nombreProductions", 1,
						"totalPortions", 2L,
						"totalMatieres", montant("100"),
						"totalCharges", montant("20"),
						"totalCout", montant("120"),
						"coutMoyenPortion", montant("60")))));

		assertPdf(service.genererPdf(RapportPdfService.TypeRapport.ACHATS,
				List.of(new RapportAchatLigne(1, LocalDate.of(2026, 11, 15),
						"FACT-1", "Fournisseur", 2L, montant("250"))),
				parametresCommuns(Map.of(
						"nombreFactures", 1,
						"nombreLignes", 2L,
						"montantTotal", montant("250")))));

		assertPdf(service.genererPdf(RapportPdfService.TypeRapport.MARGES,
				List.of(new RapportMargeLigne(LocalDate.of(2026, 11, 15),
						"Mets", 1, 2, montant("150"), montant("60"))),
				parametresCommuns(Map.of(
						"totalPortions", 2L,
						"chiffreAffaires", montant("300"),
						"coutAssocie", montant("120"),
						"margeBrute", montant("180"),
						"tauxMarge", montant("60")))));
	}

	@Test
	public void genereUnPdfQuandAucuneLigneNEstDisponible() throws Exception {
		Map<String, Object> parametres = parametresCommuns(Map.of(
				"nombreFactures", 0,
				"nombreLignes", 0L,
				"montantTotal", BigDecimal.ZERO));

		assertPdf(service.genererPdf(RapportPdfService.TypeRapport.ACHATS, List.of(), parametres));
	}

	private Map<String, Object> parametresCommuns(Map<String, Object> specifiques) {
		java.util.HashMap<String, Object> parametres = new java.util.HashMap<>(specifiques);
		parametres.put("REPORT_TITLE", "Rapport de test");
		parametres.put("RESTAURANT_NAME", "Brish");
		parametres.put("PERIOD", "Du 15/11/2026 au 15/11/2026");
		parametres.put("CURRENCY", "FCFA");
		return parametres;
	}

	private BigDecimal montant(String valeur) {
		return new BigDecimal(valeur);
	}

	private void assertPdf(byte[] contenu) {
		assertEquals("%PDF-", new String(contenu, 0, 5, StandardCharsets.US_ASCII));
	}
}
