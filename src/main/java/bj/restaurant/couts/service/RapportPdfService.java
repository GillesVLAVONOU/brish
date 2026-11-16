package bj.restaurant.couts.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;

@ApplicationScoped
public class RapportPdfService {
	private final Map<TypeRapport, JasperReport> rapportsCompiles = new HashMap<>();

	public byte[] genererPdf(
			TypeRapport type, Collection<?> lignes, Map<String, Object> parametres) throws JRException {
		Map<String, Object> parametresRapport = new HashMap<>(parametres);
		parametresRapport.putIfAbsent(JRParameter.REPORT_LOCALE, Locale.FRANCE);
		JasperPrint rapportRempli = JasperFillManager.fillReport(
				chargerRapport(type), parametresRapport, new JRBeanCollectionDataSource(lignes));
		ByteArrayOutputStream sortie = new ByteArrayOutputStream();
		JRPdfExporter exporteur = new JRPdfExporter();
		exporteur.setExporterInput(new SimpleExporterInput(rapportRempli));
		exporteur.setExporterOutput(new SimpleOutputStreamExporterOutput(sortie));
		exporteur.exportReport();
		return sortie.toByteArray();
	}

	private synchronized JasperReport chargerRapport(TypeRapport type) throws JRException {
		JasperReport rapport = rapportsCompiles.get(type);
		if (rapport != null) {
			return rapport;
		}
		String chemin = "reports/" + type.getFichier() + ".jasper";
		try (InputStream modele = getClass().getClassLoader().getResourceAsStream(chemin)) {
			if (modele == null) {
				throw new JRException("Rapport JasperReports précompilé introuvable : " + chemin);
			}
			Object objetCharge = JRLoader.loadObject(modele);
			if (!(objetCharge instanceof JasperReport rapportCharge)) {
				throw new JRException("Le fichier JasperReports précompilé est invalide : " + chemin);
			}
			rapport = rapportCharge;
			rapportsCompiles.put(type, rapport);
			return rapport;
		} catch (IOException exception) {
			throw new JRException("Impossible de lire le modèle JasperReports : " + chemin, exception);
		}
	}

	public enum TypeRapport {
		COUTS("rapport-couts"),
		ACHATS("rapport-achats"),
		MARGES("rapport-marges");

		private final String fichier;

		TypeRapport(String fichier) {
			this.fichier = fichier;
		}

		public String getFichier() {
			return fichier;
		}
	}
}
