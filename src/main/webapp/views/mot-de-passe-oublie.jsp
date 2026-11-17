<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="referrer" content="no-referrer">
    <title>Mot de passe oublié | Brish</title>
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
                <p class="eyebrow eyebrow-light">VOTRE ESPACE, EN SÉCURITÉ</p>
                <h1>Reprenez<br>la main.</h1>
                <p>Un lien temporaire vous permettra de choisir un nouveau mot de passe.</p>
            </div>
            <div class="story-note"><span>Chaque ingrédient compte.</span></div>
            <div class="story-orbit orbit-one" aria-hidden="true"></div>
            <div class="story-orbit orbit-two" aria-hidden="true"></div>
        </section>

        <section class="login-panel">
            <div class="login-topline"></div>
            <div class="login-content">
                <p class="eyebrow">ACCÈS AU COMPTE</p>
                <h2>Mot de passe oublié ?</h2>
                <p class="login-intro">Saisissez l’adresse e-mail associée à votre compte.</p>
                <c:if test="${not empty message}">
                    <p class="form-success" role="status"><c:out value="${message}"/></p>
                </c:if>
                <c:if test="${not empty erreur}">
                    <p class="form-alert" role="alert"><c:out value="${erreur}"/></p>
                </c:if>
                <form class="login-form" method="post" action="${pageContext.request.contextPath}/mot-de-passe-oublie">
                    <input type="hidden" name="jetonCsrf" value="<c:out value='${jetonCsrf}'/>">
                    <label for="email">Adresse e-mail</label>
                    <input id="email" name="email" type="email" maxlength="150" autocomplete="email"
                           placeholder="nom@restaurant.com" required>
                    <button class="button button-primary login-submit" type="submit">Envoyer le lien <span aria-hidden="true">&#x2192;</span></button>
                </form>
            </div>
            <footer class="login-footer"><a href="${pageContext.request.contextPath}/login">Retour à la connexion</a></footer>
        </section>
    </main>
</body>
</html>
