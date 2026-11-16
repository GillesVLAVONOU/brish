package bj.restaurant.couts.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRSaver;

public final class RapportTemplateCompiler {
	private RapportTemplateCompiler() {
	}

	public static void main(String[] arguments) throws IOException, JRException {
		Path dossierRapports = Path.of(arguments[0]);
		Files.createDirectories(dossierRapports);
		for (RapportPdfService.TypeRapport type : RapportPdfService.TypeRapport.values()) {
			String cheminModele = "reports/" + type.getFichier() + ".jrxml";
			try (InputStream modele = RapportTemplateCompiler.class.getClassLoader()
					.getResourceAsStream(cheminModele)) {
				if (modele == null) {
					throw new IOException("Modèle JasperReports introuvable : " + cheminModele);
				}
				JasperReport rapport = JasperCompileManager.compileReport(modele);
				JRSaver.saveObject(rapport, dossierRapports.resolve(type.getFichier() + ".jasper").toString());
			}
		}
	}
}
