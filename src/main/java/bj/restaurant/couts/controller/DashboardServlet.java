package bj.restaurant.couts.controller;

import bj.restaurant.couts.dto.RapportProductionLigne;
import bj.restaurant.couts.dto.RapportMargeLigne;
import bj.restaurant.couts.service.RapportService;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

/**
 *
 * @author Dell
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

	@Inject
	private RapportService rapportService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String section = request.getParameter("section");
		if ("parametres".equals(section)) {
			response.sendRedirect(request.getContextPath() + "/parametres");
			return;
		}
		LocalDateTime synchronisation = LocalDateTime.now();
		LocalDate fin = synchronisation.toLocalDate();
		LocalDate debut = fin.minusDays(6);
		List<RapportProductionLigne> productions = rapportService.productionsTerminees(debut, fin);
		BigDecimal totalMatieres = productions.stream()
				.map(RapportProductionLigne::getCoutMatiere)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalCharges = productions.stream()
				.map(RapportProductionLigne::getCoutCharges)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalCout = productions.stream()
				.map(RapportProductionLigne::getCoutTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		long totalPortions = productions.stream()
				.mapToLong(RapportProductionLigne::getPortionsReelles)
				.sum();
		BigDecimal coutMoyenPortion = totalPortions == 0 ? BigDecimal.ZERO
				: totalCout.divide(BigDecimal.valueOf(totalPortions), 2, RoundingMode.HALF_UP);
		List<RapportMargeLigne> marges = rapportService.marges(debut, fin);
		BigDecimal chiffreAffairesSemaine = marges.stream()
				.map(RapportMargeLigne::getChiffreAffaires)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal margeBruteSemaine = marges.stream()
				.map(RapportMargeLigne::getMargeBrute)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal tauxMargeSemaine = chiffreAffairesSemaine.signum() == 0 ? BigDecimal.ZERO
				: margeBruteSemaine.multiply(BigDecimal.valueOf(100))
						.divide(chiffreAffairesSemaine, 2, RoundingMode.HALF_UP);
		request.setAttribute("productionsSemaine", productions);
		request.setAttribute("totalMatieresSemaine", totalMatieres);
		request.setAttribute("totalChargesSemaine", totalCharges);
		request.setAttribute("totalCoutSemaine", totalCout);
		request.setAttribute("nombreProductionsSemaine", productions.size());
		request.setAttribute("totalPortionsSemaine", totalPortions);
		request.setAttribute("coutMoyenPortionSemaine", coutMoyenPortion);
		request.setAttribute("chiffreAffairesSemaine", chiffreAffairesSemaine);
		request.setAttribute("margeBruteSemaine", margeBruteSemaine);
		request.setAttribute("tauxMargeSemaine", tauxMargeSemaine);
		request.setAttribute("nombreVentesSemaine", marges.size());
		request.setAttribute("debutSemaine", debut);
		request.setAttribute("finSemaine", fin);
		request.setAttribute("derniereSynchronisation",
				synchronisation.format(DateTimeFormatter.ofPattern("'aujourd''hui à' HH:mm", Locale.FRENCH)));
		request.getRequestDispatcher("/views/dashboard.jsp").forward(request, response);
	}
}