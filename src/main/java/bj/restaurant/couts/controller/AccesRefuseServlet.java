package bj.restaurant.couts.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/acces-refuse")
public class AccesRefuseServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write("""
				<!DOCTYPE html>
				<html lang="fr"><head><meta charset="UTF-8"><title>Accès indisponible | Brish</title></head>
				<body><main><h1>Aucun module accessible</h1>
				<p>Votre compte ne dispose actuellement d’aucune autorisation. Contactez un administrateur.</p>
				<a href="%s/logout">Se déconnecter</a></main></body></html>
				""".formatted(request.getContextPath()));
	}
}
