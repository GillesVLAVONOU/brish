<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Recette de <c:out value="${recette.met.nom}"/> | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/mets?action=details&amp;id=${recette.met.id}"><c:out value="${recette.met.nom}"/></a><span aria-hidden="true">/</span><strong>Recette v<c:out value="${recette.version}"/></strong></div>
            <div class="module-header-actions">
                <a class="page-button secondary" href="${pageContext.request.contextPath}/mets?action=details&amp;id=${recette.met.id}">Historique du met</a>
                <c:if test="${recette.met.actif}"><a class="page-button" href="${pageContext.request.contextPath}/recettes?action=modifier&amp;id=${recette.id}">Créer une nouvelle version</a></c:if>
            </div>
        </header>
        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card"><span class="stat-label">MET</span><strong class="stat-value"><c:out value="${recette.met.nom}"/></strong></article>
                <article class="stat-card"><span class="stat-label">VERSION</span><strong class="stat-value">v<c:out value="${recette.version}"/></strong><span class="stat-trend trend-neutral"><c:out value="${recette.dateEffet}"/></span></article>
                <article class="stat-card"><span class="stat-label">RENDEMENT</span><strong class="stat-value"><c:out value="${recette.rendementPortions}"/></strong><span class="stat-trend trend-neutral">portions</span></article>
                <article class="stat-card"><span class="stat-label">ÉTAT</span><strong class="stat-value"><c:choose><c:when test="${recette.active}">Active</c:when><c:otherwise>Archivée</c:otherwise></c:choose></strong></article>
            </section>
            <section class="page-card">
                <div class="page-toolbar"><h2>Composition</h2><span class="muted">Quantités exprimées dans l'unité de base de chaque ingrédient</span></div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead><tr><th>INGRÉDIENT</th><th>QUANTITÉ</th><th>UNITÉ DE BASE</th></tr></thead>
                        <tbody>
                        <c:forEach items="${recette.lignes}" var="ligne">
                            <tr><td><strong><c:out value="${ligne.ingredient.nom}"/></strong></td><td><c:out value="${ligne.quantite}"/></td><td><c:out value="${ligne.unite}"/></td></tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>
            <section class="page-card">
                <div class="page-toolbar"><h2>Versions de la recette</h2><span class="muted">Historique conservé pour les productions passées</span></div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead><tr><th>VERSION</th><th>DATE D'EFFET</th><th>RENDEMENT</th><th>ÉTAT</th><th>ACTIONS</th></tr></thead>
                        <tbody>
                        <c:forEach items="${historiqueRecettes}" var="version">
                            <tr>
                                <td><strong>v<c:out value="${version.version}"/></strong></td>
                                <td><c:out value="${version.dateEffet}"/></td>
                                <td><c:out value="${version.rendementPortions}"/> portions</td>
                                <td><c:choose><c:when test="${version.active}"><span class="row-badge bag-ok">Active</span></c:when><c:otherwise><span class="row-badge bag-info">Archivée</span></c:otherwise></c:choose></td>
                                <td><a class="page-button secondary" href="${pageContext.request.contextPath}/recettes?action=details&amp;id=${version.id}">Consulter</a><c:if test="${recette.met.actif}"><a class="page-button secondary" href="${pageContext.request.contextPath}/recettes?action=modifier&amp;id=${version.id}">Créer une version</a></c:if></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
