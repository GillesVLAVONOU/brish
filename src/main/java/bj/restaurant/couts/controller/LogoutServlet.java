package bj.restaurant.couts.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession(false);
		String attendu = session == null ? null : (String) session.getAttribute("jetonDeconnexion");
		String recu = request.getParameter("jetonDeconnexion");
		if (attendu == null || recu == null
				|| !MessageDigest.isEqual(attendu.getBytes(StandardCharsets.UTF_8),
						recu.getBytes(StandardCharsets.UTF_8))) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		session.invalidate();
		response.sendRedirect(request.getContextPath() + "/login");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
}