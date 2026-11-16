<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Mets | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Mets</strong></div>
            <div class="module-header-actions">
                <form class="module-header-actions" action="${pageContext.request.contextPath}/mets" method="get">
                    <input class="page-search" type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Rechercher un met..." aria-label="Rechercher un met">
                    <button class="page-button secondary" type="submit">Rechercher</button>
                </form>
                <a class="page-button" href="${pageContext.request.contextPath}/mets?action=nouveau">+ Nouveau met</a>
            </div>
        </header>
        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card"><span class="stat-label">METS RÉFÉRENCÉS</span><strong class="stat-value"><c:out value="${nombreMets}"/></strong><span class="stat-trend trend-neutral">Catalogue complet</span></article>
                <article class="stat-card"><span class="stat-label">METS ACTIFS</span><strong class="stat-value"><c:out value="${nombreMetsActifs}"/></strong><span class="stat-trend trend-neutral">Disponibles pour recette</span></article>
            </section>
            <section class="page-card">
                <div class="page-toolbar"><h2>Catalogue des mets</h2><a class="text-link" href="${pageContext.request.contextPath}/recettes">Voir les fiches recettes →</a></div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead><tr><th>MET</th><th>VERSION ACTIVE</th><th>INGRÉDIENTS</th><th>PRIX DE VENTE</th><th>ÉTAT</th><th>ACTIONS</th></tr></thead>
                        <tbody>
                        <c:forEach items="${mets}" var="met">
                            <tr>
                                <td><strong><c:out value="${met.nom}"/></strong></td>
                                <td><c:choose><c:when test="${met.recetteActive != null}">v<c:out value="${met.recetteActive.version}"/></c:when><c:otherwise>Non définie</c:otherwise></c:choose></td>
                                <td><c:choose><c:when test="${met.recetteActive != null}"><c:out value="${met.recetteActive.lignes.size()}"/></c:when><c:otherwise>—</c:otherwise></c:choose></td>
                                <td><fmt:formatNumber value="${met.prixVenteActuel}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                <td><c:choose><c:when test="${met.actif}"><span class="row-badge bag-ok">Actif</span></c:when><c:otherwise><span class="row-badge bag-warn">Inactif</span></c:otherwise></c:choose></td>
                                <td>
                                    <a class="page-button secondary" href="${pageContext.request.contextPath}/mets?action=details&amp;id=${met.id}">Détails</a>
                                    <a class="page-button secondary" href="${pageContext.request.contextPath}/mets?action=modifier&amp;id=${met.id}">Modifier</a>
                                    <c:if test="${met.actif}">
                                        <a class="page-button secondary" href="${pageContext.request.contextPath}/recettes?action=nouveau&amp;metId=${met.id}">Recette</a>
                                        <form action="${pageContext.request.contextPath}/mets" method="post" class="inline-form">
                                            <input type="hidden" name="action" value="desactiver">
                                            <input type="hidden" name="id" value="<c:out value='${met.id}'/>">
                                            <button class="page-button secondary deactivate-icon-button" type="submit" title="Désactiver ce met" aria-label="Désactiver le met <c:out value='${met.nom}'/>"></button>
                                        </form>
                                    </c:if>
                                    <c:if test="${not met.actif}">
                                        <form action="${pageContext.request.contextPath}/mets" method="post" class="inline-form">
                                            <input type="hidden" name="action" value="activer">
                                            <input type="hidden" name="id" value="<c:out value='${met.id}'/>">
                                            <button class="page-button secondary" type="submit">Réactiver</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty mets}"><tr><td colspan="6" class="muted">Aucun met trouvé.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
