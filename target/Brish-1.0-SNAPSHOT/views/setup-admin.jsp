<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>Créer l’administrateur | Brish</title>
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-page">
	<main class="login-shell">
		<section class="login-story" aria-label="Initialisation de Brish">
			<a class="brand brand-light" href="${pageContext.request.contextPath}/login" aria-label="Brish">
				<span class="brand-mark" aria-hidden="true">B</span>
				<span>brish<span class="brand-dot">.</span></span>
			</a>
			<div class="story-copy">
				<p class="eyebrow eyebrow-light">PREMIÈRE CONFIGURATION</p>
				<h1>Votre espace<br>commence ici.</h1>
				<p>Créez le compte administrateur qui sécurisera l’accès à votre espace de gestion.</p>
			</div>
			<div class="story-note"><span>Une configuration, puis accès protégé.</span></div>
			<div class="story-orbit orbit-one" aria-hidden="true"></div>
			<div class="story-orbit orbit-two" aria-hidden="true"></div>
		</section>

		<section class="login-panel">
			<div class="login-topline"><span class="status-dot"></span><span>CRÉATION ADMINISTRATEUR</span></div>
			<div class="login-content">
				<p class="eyebrow">COMPTE INITIAL</p>
				<h2>Créer l’administrateur</h2>
				<p class="login-intro">Cette étape n’est disponible que tant qu’aucun administrateur n’existe.</p>

				<c:if test="${not empty erreur}">
					<p class="form-alert" role="alert"><c:out value="${erreur}"/></p>
				</c:if>

				<form class="login-form" method="post" action="${pageContext.request.contextPath}/setup-admin">
					<input type="hidden" name="token" value="<c:out value='${sessionScope.adminSetupToken}'/>" />
					<label for="nom">Nom complet</label>
					<input id="nom" name="nom" type="text" maxlength="120" autocomplete="name" required value="<c:out value='${nom}'/>" />

					<label for="email">Adresse e-mail</label>
					<input id="email" name="email" type="email" maxlength="180" autocomplete="username" required value="<c:out value='${email}'/>" />

					<label for="password">Mot de passe (12 caractères minimum)</label>
					<input id="password" name="password" type="password" minlength="12" autocomplete="new-password" required />

					<label for="confirmation">Confirmer le mot de passe</label>
					<input id="confirmation" name="confirmation" type="password" minlength="12" autocomplete="new-password" required />

					<button class="button button-primary login-submit" type="submit">Créer le compte <span aria-hidden="true">→</span></button>
				</form>
			</div>
			<footer class="login-footer"><span>Brish · Accès initial local uniquement</span></footer>
		</section>
	</main>
</body>
</html>