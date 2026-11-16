package bj.restaurant.couts.controller;

import bj.restaurant.couts.Entities.Fournisseur;
import bj.restaurant.couts.service.FournisseurService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/fournisseurs")
public class FournisseurServlet extends HttpServlet {
	@Inject
	private FournisseurService fournisseurService;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("nouveau".equals(action)) {
			preparerFormulaire(request, response, new Fournisseur());
			return;
		}
		if ("modifier".equals(action)) {
			Integer id = lireId(request.getParameter("id"));
			if (id == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			Fournisseur fournisseur = fournisseurService.trouverParId(id).orElse(null);
			if (fournisseur == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			preparerFormulaire(request, response, fournisseur);
			return;
		}

		afficherListe(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		if (!"enregistrer".equals(request.getParameter("action"))) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		Fournisseur fournisseur = new Fournisseur();
		List<String> erreurs = new ArrayList<>();
		String idParametre = request.getParameter("id");
		if (idParametre != null && !idParametre.isBlank()) {
			Integer id = lireId(idParametre);
			if (id == null) {
				erreurs.add("Identifiant de fournisseur invalide.");
			} else {
				fournisseur.setId(id);
			}
		}

		String nom = valeur(request, "nom");
		String telephone = valeur(request, "telephone");
		String email = valeur(request, "email");
		String adresse = valeur(request, "adresse");
		if (nom.isBlank()) erreurs.add("Le nom du fournisseur est obligatoire.");
		if (nom.length() > 150) erreurs.add("Le nom ne peut pas dépasser 150 caractères.");
		if (telephone.length() > 30) erreurs.add("Le téléphone ne peut pas dépasser 30 caractères.");
		if (email.length() > 150) erreurs.add("L'adresse e-mail ne peut pas dépasser 150 caractères.");
		if (!email.isBlank() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			erreurs.add("L'adresse e-mail n'est pas valide.");
		}
		if (adresse.length() > 255) erreurs.add("L'adresse ne peut pas dépasser 255 caractères.");
		fournisseur.setNom(nom);
		fournisseur.setTelephone(telephone);
		fournisseur.setEmail(email);
		fournisseur.setAdresse(adresse);

		if (erreurs.isEmpty()) {
			try {
				fournisseurService.enregistrer(fournisseur);
				response.sendRedirect(request.getContextPath() + "/fournisseurs");
				return;
			} catch (IllegalArgumentException exception) {
				erreurs.add(exception.getMessage());
			}
		}

		request.setAttribute("erreurs", erreurs);
		preparerFormulaire(request, response, fournisseur);
	}

	private void afficherListe(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String recherche = request.getParameter("recherche");
		request.setAttribute("fournisseurs", fournisseurService.rechercher(recherche));
		request.setAttribute("nombreFournisseurs", fournisseurService.compterTous());
		request.setAttribute("recherche", recherche == null ? "" : recherche);
		request.getRequestDispatcher("/views/fournisseurs/liste.jsp").forward(request, response);
	}

	private void preparerFormulaire(HttpServletRequest request, HttpServletResponse response, Fournisseur fournisseur)
			throws ServletException, IOException {
		request.setAttribute("formMode", true);
		request.setAttribute("fournisseur", fournisseur);
		request.getRequestDispatcher("/views/fournisseurs/formulaire.jsp").forward(request, response);
	}

	private String valeur(HttpServletRequest request, String nom) {
		String valeur = request.getParameter(nom);
		return valeur == null ? "" : valeur.trim();
	}

	private Integer lireId(String valeur) {
		if (valeur == null || valeur.isBlank()) return null;
		try {
			return Integer.valueOf(valeur);
		} catch (NumberFormatException exception) {
			return null;
		}
	}
}