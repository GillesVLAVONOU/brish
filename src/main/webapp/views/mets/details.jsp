<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><c:out value="${met.nom}"/> | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/mets">Mets</a><span aria-hidden="true">/</span><strong><c:out value="${met.nom}"/></strong></div>
            <div class="module-header-actions">
                <a class="page-button secondary" href="${pageContext.request.contextPath}/mets?action=modifier&amp;id=${met.id}">Modifier</a>
                <c:if test="${met.actif}"><a class="page-button" href="${pageContext.request.contextPath}/recettes?action=nouveau&amp;metId=${met.id}">Nouvelle recette</a></c:if>
            </div>
        </header>
        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card"><span class="stat-label">PRIX DE VENTE ACTUEL</span><strong class="stat-value"><fmt:formatNumber value="${met.prixVenteActuel}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                <article class="stat-card"><span class="stat-label">STATUT</span><strong class="stat-value"><c:choose><c:when test="${met.actif}">Actif</c:when><c:otherwise>Inactif</c:otherwise></c:choose></strong></article>
                <article class="stat-card"><span class="stat-label">VERSION ACTIVE</span><strong class="stat-value"><c:choose><c:when test="${met.recetteActive != null}">v<c:out value="${met.recetteActive.version}"/></c:when><c:otherwise>Aucune</c:otherwise></c:choose></strong></article>
            </section>
            <section class="page-card info-panel">
                <h3>Description</h3>
                <p><c:out value="${met.description}" default="Aucune description renseignée."/></p>
            </section>
            <section class="page-card">
                <div class="page-toolbar"><h2>Historique des recettes</h2><span class="muted">Les versions précédentes sont conservées</span></div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead><tr><th>VERSION</th><th>DATE D'EFFET</th><th>RENDEMENT</th><th>INGRÉDIENTS</th><th>STATUT</th><th>ACTIONS</th></tr></thead>
                        <tbody>
                        <c:forEach items="${historiqueRecettes}" var="recette">
                            <tr>
                                <td><strong>v<c:out value="${recette.version}"/></strong></td>
                                <td><c:out value="${recette.dateEffet}"/></td>
                                <td><c:out value="${recette.rendementPortions}"/> portions</td>
                                <td><c:out value="${recette.lignes.size()}"/></td>
                                <td><c:choose><c:when test="${recette.active}"><span class="row-badge bag-ok">Active</span></c:when><c:otherwise><span class="row-badge bag-info">Archivée</span></c:otherwise></c:choose></td>
                                <td><a class="page-button secondary" href="${pageContext.request.contextPath}/recettes?action=details&amp;id=${recette.id}">Consulter</a><c:if test="${met.actif}"><a class="page-button secondary" href="${pageContext.request.contextPath}/recettes?action=modifier&amp;id=${recette.id}">Créer une version</a></c:if></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty historiqueRecettes}"><tr><td colspan="6" class="muted">Aucune recette enregistrée pour ce met.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
