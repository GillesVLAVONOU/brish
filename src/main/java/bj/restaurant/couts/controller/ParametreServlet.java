package bj.restaurant.couts.controller;

import bj.restaurant.couts.Entities.ParametresRestaurant;
import bj.restaurant.couts.service.ParametreService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/parametres")
public class ParametreServlet extends HttpServlet {

	@Inject
	private ParametreService parametreService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		afficher(request, response, parametreService.charger(), List.of(),
				"1".equals(request.getParameter("enregistre")));
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (!"enregistrer".equals(request.getParameter("action"))) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String nom = valeur(request, "nomRestaurant");
		String adresse = valeur(request, "adresse");
		String telephone = valeur(request, "telephone");
		String devise = valeur(request, "codeDevise");
		ParametresRestaurant parametres = parametreService.charger();
		if (devise.isBlank()) {
			devise = parametres.getCodeDevise();
		}
		List<String> erreurs = new ArrayList<>();
		try {
			parametreService.mettreAJour(nom, adresse, telephone, devise);
			response.sendRedirect(request.getContextPath() + "/parametres?enregistre=1");
			return;
		} catch (IllegalArgumentException exception) {
			erreurs.add(exception.getMessage());
		}
		parametres.setNomRestaurant(nom);
		parametres.setAdresse(adresse);
		parametres.setTelephone(telephone);
		parametres.setCodeDevise(devise);
		afficher(request, response, parametres, erreurs, false);
	}

	private void afficher(HttpServletRequest request, HttpServletResponse response,
			ParametresRestaurant parametres, List<String> erreurs, boolean enregistre)
			throws ServletException, IOException {
		request.setAttribute("parametres", parametres);
		request.setAttribute("deviseModifiable", parametreService.deviseModifiable());
		request.setAttribute("erreurs", erreurs);
		request.setAttribute("enregistre", enregistre);
		request.getRequestDispatcher("/views/parametres.jsp").forward(request, response);
	}

	private String valeur(HttpServletRequest request, String nom) {
		String valeur = request.getParameter(nom);
		return valeur == null ? "" : valeur.trim();
	}
}
