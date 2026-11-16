package bj.restaurant.couts.controller;

import bj.restaurant.couts.dto.RapportAchatLigne;
import bj.restaurant.couts.dto.RapportMargeLigne;
import bj.restaurant.couts.dto.RapportProductionLigne;
import bj.restaurant.couts.Entities.ParametresRestaurant;
import bj.restaurant.couts.service.ParametreService;
import bj.restaurant.couts.service.RapportPdfService;
import bj.restaurant.couts.service.RapportService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;

@WebServlet({"/rapports", "/rapports/couts", "/rapports/marges", "/rapports/achats"})
public class RapportServlet extends HttpServlet {

	@Inject
	private RapportService rapportService;
	@Inject
	private RapportPdfService rapportPdfService;
	@Inject
	private ParametreService parametreService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();
		if ("pdf".equals(request.getParameter("format"))) {
			exporterPdf(path, request, response);
			return;
		}
		if ("/rapports/couts".equals(path)) {
			afficherCouts(request, response);
			return;
		}
		if ("/rapports/marges".equals(path)) {
			afficherMarges(request, response);
			return;
		}
		if ("/rapports/achats".equals(path)) {
			afficherAchats(request, response);
			return;
		}
		request.getRequestDispatcher("/views/rapports/index.jsp").forward(request, response);
	}

	private void exporterPdf(String path, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String debutSaisi = valeur(request, "debut");
		String finSaisie = valeur(request, "fin");
		if (debutSaisi.isEmpty() || finSaisie.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Les dates de début et de fin sont obligatoires pour exporter un rapport.");
			return;
		}

		try {
			LocalDate debut = LocalDate.parse(debutSaisi);
			LocalDate fin = LocalDate.parse(finSaisie);
			List<?> lignes;
			Map<String, Object> parametres = new HashMap<>();
			RapportPdfService.TypeRapport type;
			switch (path) {
				case "/rapports/couts" -> {
					type = RapportPdfService.TypeRapport.COUTS;
					List<RapportProductionLigne> productions = rapportService.productionsTerminees(debut, fin);
					lignes = productions;
					BigDecimal matieres = BigDecimal.ZERO;
					BigDecimal charges = BigDecimal.ZERO;
					BigDecimal coutTotal = BigDecimal.ZERO;
					long portions = 0;
					for (RapportProductionLigne production : productions) {
						matieres = matieres.add(production.getCoutMatiere());
						charges = charges.add(production.getCoutCharges());
						coutTotal = coutTotal.add(production.getCoutTotal());
						portions += production.getPortionsReelles();
					}
					parametres.put("nombreProductions", productions.size());
					parametres.put("totalPortions", portions);
					parametres.put("totalMatieres", matieres);
					parametres.put("totalCharges", charges);
					parametres.put("totalCout", coutTotal);
					parametres.put("coutMoyenPortion", portions == 0 ? BigDecimal.ZERO
							: coutTotal.divide(BigDecimal.valueOf(portions), 2, java.math.RoundingMode.HALF_UP));
					parametres.put("REPORT_TITLE", "Coûts des productions clôturées");
				}
				case "/rapports/achats" -> {
					type = RapportPdfService.TypeRapport.ACHATS;
					List<RapportAchatLigne> achats = rapportService.achats(debut, fin);
					lignes = achats;
					long nombreLignes = 0;
					BigDecimal montantTotal = BigDecimal.ZERO;
					for (RapportAchatLigne achat : achats) {
						nombreLignes += achat.getNombreLignes();
						montantTotal = montantTotal.add(achat.getMontantTotal());
					}
					parametres.put("nombreFactures", achats.size());
					parametres.put("nombreLignes", nombreLignes);
					parametres.put("montantTotal", montantTotal);
					parametres.put("REPORT_TITLE", "Relevé des achats");
				}
				case "/rapports/marges" -> {
					type = RapportPdfService.TypeRapport.MARGES;
					List<RapportMargeLigne> ventes = rapportService.marges(debut, fin);
					lignes = ventes;
					BigDecimal chiffreAffaires = BigDecimal.ZERO;
					BigDecimal coutAssocie = BigDecimal.ZERO;
					BigDecimal margeBrute = BigDecimal.ZERO;
					long portions = 0;
					for (RapportMargeLigne vente : ventes) {
						chiffreAffaires = chiffreAffaires.add(vente.getChiffreAffaires());
						coutAssocie = coutAssocie.add(vente.getCoutAssocie());
						margeBrute = margeBrute.add(vente.getMargeBrute());
						portions += vente.getPortionsVendues();
					}
					BigDecimal tauxMarge = chiffreAffaires.signum() == 0 ? BigDecimal.ZERO
							: margeBrute.multiply(BigDecimal.valueOf(100))
									.divide(chiffreAffaires, 2, java.math.RoundingMode.HALF_UP);
					parametres.put("totalPortions", portions);
					parametres.put("chiffreAffaires", chiffreAffaires);
					parametres.put("coutAssocie", coutAssocie);
					parametres.put("margeBrute", margeBrute);
					parametres.put("tauxMarge", tauxMarge);
					parametres.put("REPORT_TITLE", "Marges et rentabilité");
				}
				default -> {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			}

			ParametresRestaurant parametresRestaurant = parametreService.charger();
			parametres.put("RESTAURANT_NAME", parametresRestaurant.getNomRestaurant());
			parametres.put("PERIOD", "Du " + debut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
					+ " au " + fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			parametres.put("CURRENCY", parametresRestaurant.getLibelleDevise());
			byte[] pdf = rapportPdfService.genererPdf(type, lignes, parametres);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ type.getFichier() + "-" + debut + "-" + fin + ".pdf\"");
			response.setHeader("Cache-Control", "private, no-store");
			response.setContentLength(pdf.length);
			response.getOutputStream().write(pdf);
		} catch (DateTimeParseException exception) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Saisissez des dates valides.");
		} catch (IllegalArgumentException exception) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
		} catch (JRException exception) {
			throw new ServletException("Impossible de générer le rapport PDF.", exception);
		}
	}

	private void afficherMarges(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String debutSaisi = valeur(request, "debut");
		String finSaisie = valeur(request, "fin");
		List<String> erreurs = new ArrayList<>();
		List<RapportMargeLigne> lignes = List.of();
		BigDecimal chiffreAffaires = BigDecimal.ZERO;
		BigDecimal coutAssocie = BigDecimal.ZERO;
		BigDecimal margeBrute = BigDecimal.ZERO;
		long totalPortions = 0;
		if (!debutSaisi.isEmpty() || !finSaisie.isEmpty()) {
			try {
				LocalDate debut = LocalDate.parse(debutSaisi);
				LocalDate fin = LocalDate.parse(finSaisie);
				lignes = rapportService.marges(debut, fin);
				for (RapportMargeLigne ligne : lignes) {
					chiffreAffaires = chiffreAffaires.add(ligne.getChiffreAffaires());
					coutAssocie = coutAssocie.add(ligne.getCoutAssocie());
					margeBrute = margeBrute.add(ligne.getMargeBrute());
					totalPortions += ligne.getPortionsVendues();
				}
			} catch (DateTimeParseException exception) {
				erreurs.add("Saisissez des dates valides pour la période.");
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}
		BigDecimal tauxMarge = chiffreAffaires.signum() == 0 ? BigDecimal.ZERO
				: margeBrute.multiply(BigDecimal.valueOf(100))
						.divide(chiffreAffaires, 2, java.math.RoundingMode.HALF_UP);
		request.setAttribute("debut", debutSaisi);
		request.setAttribute("fin", finSaisie);
		request.setAttribute("erreurs", erreurs);
		request.setAttribute("lignesMarge", lignes);
		request.setAttribute("nombreLignesVente", lignes.size());
		request.setAttribute("totalPortionsVendues", totalPortions);
		request.setAttribute("chiffreAffaires", chiffreAffaires);
		request.setAttribute("coutAssocie", coutAssocie);
		request.setAttribute("margeBrute", margeBrute);
		request.setAttribute("tauxMarge", tauxMarge);
		request.getRequestDispatcher("/views/rapports/marges.jsp").forward(request, response);
	}

	private void afficherCouts(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String debutSaisi = valeur(request, "debut");
		String finSaisie = valeur(request, "fin");
		List<String> erreurs = new ArrayList<>();
		List<RapportProductionLigne> productions = List.of();
		BigDecimal totalMatieres = BigDecimal.ZERO;
		BigDecimal totalCharges = BigDecimal.ZERO;
		BigDecimal totalCout = BigDecimal.ZERO;
		long totalPortions = 0;
		if (!debutSaisi.isEmpty() || !finSaisie.isEmpty()) {
			try {
				LocalDate debut = LocalDate.parse(debutSaisi);
				LocalDate fin = LocalDate.parse(finSaisie);
				productions = rapportService.productionsTerminees(debut, fin);
				for (RapportProductionLigne production : productions) {
					totalMatieres = totalMatieres.add(production.getCoutMatiere());
					totalCharges = totalCharges.add(production.getCoutCharges());
					totalCout = totalCout.add(production.getCoutTotal());
					totalPortions += production.getPortionsReelles();
				}
			} catch (DateTimeParseException exception) {
				erreurs.add("Saisissez des dates valides pour la période.");
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}
		request.setAttribute("debut", debutSaisi);
		request.setAttribute("fin", finSaisie);
		request.setAttribute("erreurs", erreurs);
		request.setAttribute("productions", productions);
		request.setAttribute("nombreProductions", productions.size());
		request.setAttribute("totalPortions", totalPortions);
		request.setAttribute("totalMatieres", totalMatieres);
		request.setAttribute("totalCharges", totalCharges);
		request.setAttribute("totalCout", totalCout);
		request.setAttribute("coutMoyenPortion", totalPortions == 0 ? BigDecimal.ZERO
				: totalCout.divide(BigDecimal.valueOf(totalPortions), 2, java.math.RoundingMode.HALF_UP));
		request.getRequestDispatcher("/views/rapports/couts.jsp").forward(request, response);
	}

	private void afficherAchats(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String debutSaisi = valeur(request, "debut");
		String finSaisie = valeur(request, "fin");
		List<String> erreurs = new ArrayList<>();
		List<RapportAchatLigne> achats = List.of();
		BigDecimal montantTotal = BigDecimal.ZERO;
		long nombreLignes = 0;
		if (!debutSaisi.isEmpty() || !finSaisie.isEmpty()) {
			try {
				LocalDate debut = LocalDate.parse(debutSaisi);
				LocalDate fin = LocalDate.parse(finSaisie);
				achats = rapportService.achats(debut, fin);
				for (RapportAchatLigne achat : achats) {
					montantTotal = montantTotal.add(achat.getMontantTotal());
					nombreLignes += achat.getNombreLignes();
				}
			} catch (DateTimeParseException exception) {
				erreurs.add("Saisissez des dates valides pour la période.");
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}
		request.setAttribute("debut", debutSaisi);
		request.setAttribute("fin", finSaisie);
		request.setAttribute("erreurs", erreurs);
		request.setAttribute("achats", achats);
		request.setAttribute("nombreFactures", achats.size());
		request.setAttribute("nombreLignes", nombreLignes);
		request.setAttribute("montantTotal", montantTotal);
		request.getRequestDispatcher("/views/rapports/achats.jsp").forward(request, response);
	}

	private String valeur(HttpServletRequest request, String nom) {
		String valeur = request.getParameter(nom);
		return valeur == null ? "" : valeur.trim();
	}
}