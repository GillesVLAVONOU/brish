<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="referrer" content="no-referrer">
    <title>Nouveau mot de passe | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-page">
    <main class="login-shell">
        <section class="login-story" aria-label="Suivi des coûts du restaurant">
            <a class="brand brand-light" href="${pageContext.request.contextPath}/login" aria-label="Brish, accueil">
                <img class="brand-mark-image" src="${pageContext.request.contextPath}/assets/images/logo-brish.png" alt="">
                <span>brish<span class="brand-dot">.</span></span>
            </a>
            <div class="story-copy">
                <p class="eyebrow eyebrow-light">VOTRE ESPACE, EN SÉCURITÉ</p>
                <h1>Un nouveau<br>départ.</h1>
                <p>Choisissez un mot de passe unique pour protéger votre compte Brish.</p>
            </div>
            <div class="story-note"><span>Chaque ingrédient compte.</span></div>
            <div class="story-orbit orbit-one" aria-hidden="true"></div>
            <div class="story-orbit orbit-two" aria-hidden="true"></div>
        </section>

        <section class="login-panel">
            <div class="login-topline"><span class="status-dot"></span><span>SÉCURISATION DU COMPTE</span></div>
            <div class="login-content">
                <p class="eyebrow">NOUVEAU MOT DE PASSE</p>
                <h2>Choisissez un mot de passe</h2>
                <p class="login-intro">Utilisez au moins 12 caractères.</p>
                <c:if test="${not empty erreur}">
                    <p class="form-alert" role="alert"><c:out value="${erreur}"/></p>
                </c:if>
                <c:choose>
                    <c:when test="${jetonValide}">
                        <form class="login-form" method="post" action="${pageContext.request.contextPath}/reinitialiser-mot-de-passe">
                            <input type="hidden" name="jetonCsrf" value="<c:out value='${jetonCsrf}'/>">
                            <input type="hidden" name="jeton" value="<c:out value='${jeton}'/>">
                            <label for="nouveauMotDePasse">Nouveau mot de passe</label>
                            <input id="nouveauMotDePasse" name="nouveauMotDePasse" type="password"
                                   autocomplete="new-password" minlength="12" maxlength="1024" required>
                            <label for="confirmation">Confirmer le mot de passe</label>
                            <input id="confirmation" name="confirmation" type="password"
                                   autocomplete="new-password" minlength="12" maxlength="1024" required>
                            <button class="button button-primary login-submit" type="submit">Enregistrer le mot de passe <span aria-hidden="true">&#x2192;</span></button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <p class="form-alert" role="alert">Ce lien est invalide ou a expiré. Demandez-en un nouveau.</p>
                        <a class="button button-primary" href="${pageContext.request.contextPath}/mot-de-passe-oublie">Demander un nouveau lien</a>
                    </c:otherwise>
                </c:choose>
            </div>
            <footer class="login-footer"><a href="${pageContext.request.contextPath}/login">Retour à la connexion</a><span>© 2026 Brish</span></footer>
        </section>
    </main>
</body>
</html>
