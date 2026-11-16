<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Démarrer une production | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/productions">Productions</a><span aria-hidden="true">/</span><strong>Démarrer</strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/productions">Retour à la liste</a></div>
        </header>
        <div class="module-body">
            <section class="page-card info-panel">
                <div class="page-toolbar"><h2>Démarrer une production</h2><span class="muted">La recette active sera figée pour cette production.</span></div>
                <c:if test="${not empty erreurs}">
                    <div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div>
                </c:if>
                <c:if test="${empty recettes}"><p class="muted">Aucune recette active. Créez un met et sa fiche recette avant de démarrer une production.</p></c:if>
                <form action="${pageContext.request.contextPath}/productions" method="post" class="ingredient-form">
                    <input type="hidden" name="action" value="demarrer">
                    <div class="form-grid">
                        <div class="field">
                            <label for="recetteId">Fiche recette active *</label>
                            <select id="recetteId" name="recetteId" required>
                                <option value="">Sélectionner un met</option>
                                <c:forEach items="${recettes}" var="recette">
                                    <option value="<c:out value='${recette.id}'/>" ${recetteId == recette.id ? 'selected' : ''}><c:out value="${recette.met.nom}"/> — v<c:out value="${recette.version}"/> (<c:out value="${recette.rendementPortions}"/> portions)</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="field">
                            <label for="portionsPrevues">Portions prévues *</label>
                            <input id="portionsPrevues" name="portionsPrevues" type="number" min="1" step="1" required value="<c:out value='${portionsPrevues}'/>">
                        </div>
                    </div>
                    <p class="muted">La production est créée en brouillon. À la clôture, vous saisirez les portions réellement produites et les quantités réellement consommées. Aucun stock n'est décrémenté.</p>
                    <div class="form-actions">
                        <a class="page-button secondary" href="${pageContext.request.contextPath}/productions">Annuler</a>
                        <c:choose>
                            <c:when test="${empty recettes}"><button class="page-button" type="submit" disabled>Démarrer</button></c:when>
                            <c:otherwise><button class="page-button" type="submit">Démarrer la production</button></c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
