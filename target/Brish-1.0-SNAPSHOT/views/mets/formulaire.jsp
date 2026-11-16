<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><c:choose><c:when test="${met.id == null}">Nouveau met</c:when><c:otherwise>Modifier le met</c:otherwise></c:choose> | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/mets">Mets</a><span aria-hidden="true">/</span><strong><c:choose><c:when test="${met.id == null}">Nouveau met</c:when><c:otherwise>Modifier le met</c:otherwise></c:choose></strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/mets">Retour à la liste</a></div>
        </header>
        <div class="module-body">
            <section class="page-card info-panel">
                <div class="page-toolbar"><h2><c:choose><c:when test="${met.id == null}">Créer un met</c:when><c:otherwise>Modifier le met</c:otherwise></c:choose></h2></div>
                <c:if test="${not empty erreurs}">
                    <div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div>
                </c:if>
                <form action="${pageContext.request.contextPath}/mets" method="post" class="ingredient-form">
                    <input type="hidden" name="action" value="enregistrer">
                    <c:if test="${met.id != null}"><input type="hidden" name="id" value="<c:out value='${met.id}'/>"></c:if>
                    <div class="form-grid">
                        <div class="field">
                            <label for="nom">Nom du met *</label>
                            <input id="nom" name="nom" maxlength="120" required value="<c:out value='${met.nom}'/>">
                        </div>
                        <div class="field">
                            <label for="prixVenteActuel">Prix de vente actuel (${devise}) *</label>
                            <input id="prixVenteActuel" name="prixVenteActuel" type="number" min="0" step="0.01" required value="<c:out value='${met.prixVenteActuel}'/>">
                        </div>
                        <div class="field">
                            <label for="description">Description</label>
                            <textarea id="description" name="description"><c:out value="${met.description}"/></textarea>
                        </div>
                    </div>
                    <div class="form-actions">
                        <a class="page-button secondary" href="${pageContext.request.contextPath}/mets">Annuler</a>
                        <button class="page-button" type="submit">Enregistrer</button>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
