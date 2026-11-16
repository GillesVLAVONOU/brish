<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Fiches recettes | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/mets">Mets</a><span aria-hidden="true">/</span><strong>Fiches recettes actives</strong></div>
            <div class="module-header-actions">
                <form class="module-header-actions" action="${pageContext.request.contextPath}/recettes" method="get">
                    <input class="page-search" type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Rechercher un met..." aria-label="Rechercher une recette">
                    <button class="page-button secondary" type="submit">Rechercher</button>
                </form>
                <a class="page-button" href="${pageContext.request.contextPath}/mets">Créer depuis un met</a>
            </div>
        </header>
        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card"><span class="stat-label">RECETTES ACTIVES</span><strong class="stat-value"><c:out value="${nombreRecettes}"/></strong><span class="stat-trend trend-neutral">Une version active par met</span></article>
            </section>
            <section class="page-card">
                <div class="page-toolbar"><h2>Fiches techniques en vigueur</h2><span class="muted">Les versions précédentes restent consultables depuis chaque met.</span></div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead><tr><th>MET</th><th>VERSION</th><th>DATE D'EFFET</th><th>RENDEMENT</th><th>INGRÉDIENTS</th><th>ACTIONS</th></tr></thead>
                        <tbody>
                        <c:forEach items="${recettes}" var="recette">
                            <tr>
                                <td><strong><c:out value="${recette.met.nom}"/></strong></td>
                                <td>v<c:out value="${recette.version}"/></td>
                                <td><c:out value="${recette.dateEffet}"/></td>
                                <td><c:out value="${recette.rendementPortions}"/> portions</td>
                                <td><c:out value="${recette.lignes.size()}"/></td>
                                <td>
                                    <a class="page-button secondary" href="${pageContext.request.contextPath}/recettes?action=details&amp;id=${recette.id}">Consulter</a>
                                    <a class="page-button secondary" href="${pageContext.request.contextPath}/recettes?action=modifier&amp;id=${recette.id}">Créer une version</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recettes}"><tr><td colspan="6" class="muted">Aucune fiche recette active. Créez d'abord un met, puis sa recette.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
