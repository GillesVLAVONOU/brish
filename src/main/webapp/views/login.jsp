<%-- 
    Document   : loginjsp
    Created on : 15 oct. 2026, 01:12:47
    Author     : Dell
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>Connexion | Brish</title>
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-page">
	<main class="login-shell">
		<section class="login-story" aria-label="Suivi des coûts du restaurant">
			<a class="brand brand-light" href="${pageContext.request.contextPath}/login" aria-label="Brish, accueil">
				<img class="brand-mark-image" src="${pageContext.request.contextPath}/assets/images/logo-brish.png" alt="">
				<span>Brish<span class="brand-dot"></span></span>
			</a>
			<div class="story-copy">
				<p class="eyebrow eyebrow-light">PILOTAGE RESTAURANT</p>
				<h1>La précision<br>au menu.</h1>
				<p>Maîtrisez vos achats, vos recettes et le coût réel de chaque production.</p>
			</div>
			<div class="story-note">
				<span>Chaque ingrédient compte.</span>
			</div>
			<div class="story-orbit orbit-one" aria-hidden="true"></div>
			<div class="story-orbit orbit-two" aria-hidden="true"></div>
			<div class="story-stamp" aria-hidden="true"><span>COÛTS</span><strong>MAÎTRISÉS</strong><span>· DEPUIS LA CUISINE ·</span></div>
		</section>

		<section class="login-panel">
			<div class="login-topline">
			</div>
			<div class="login-content">
				<p class="eyebrow">BON RETOUR</p>
				<h2>Connectez-vous</h2>
				<p class="login-intro">Retrouvez le suivi de votre activité.</p>
				<% if ("1".equals(request.getParameter("motDePasseReinitialise"))) { %>
					<p class="form-success" role="status">Votre mot de passe a été modifié. Connectez-vous avec le nouveau.</p>
				<% } %>
				<% if ("1".equals(request.getParameter("adminInitialise"))) { %>
					<p class="form-alert" role="status">Compte administrateur créé. Connectez-vous avec vos identifiants.</p>
				<% } %>

				<% if (request.getAttribute("erreur") != null) { %>
					<p class="form-alert" role="alert"><%= request.getAttribute("erreur") %></p>
				<% } %>

				<form class="login-form" method="post" action="${pageContext.request.contextPath}/login">
					<label for="email">Adresse e-mail</label>
					<input id="email" name="email" type="email" autocomplete="username" placeholder="nom@restaurant.com" required>

					<div class="field-heading">
						<label for="password">Mot de passe</label>
						<a href="${pageContext.request.contextPath}/mot-de-passe-oublie">Oublié ?</a>
					</div>
					<input id="password" name="password" type="password" autocomplete="current-password" placeholder="Votre mot de passe" required>

					<label class="remember-option"><input type="checkbox" name="se-souvenir"><span>Garder ma session active</span></label>
					<button class="button button-primary login-submit" type="submit">Accéder à mon espace <span aria-hidden="true">→</span></button>
				</form>
			</div>
			<footer class="login-footer"><span>© 2026 Brish</span></footer>
		</section>
	</main>
</body>
</html>
