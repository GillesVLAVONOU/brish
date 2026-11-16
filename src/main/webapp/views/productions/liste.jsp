<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Productions | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Productions</strong></div>
            <div class="module-header-actions">
                <form class="module-header-actions" action="${pageContext.request.contextPath}/productions" method="get">
                    <input class="page-search" type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Rechercher un met..." aria-label="Rechercher une production">
                    <button class="page-button secondary" type="submit">Rechercher</button>
                </form>
                <a class="page-button" href="${pageContext.request.contextPath}/productions?action=nouveau">+ Nouvelle production</a>
            </div>
        </header>
        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card"><span class="stat-label">PRODUCTIONS</span><strong class="stat-value"><c:out value="${nombreProductions}"/></strong><span class="stat-trend trend-neutral">Historique complet</span></article>
                <article class="stat-card"><span class="stat-label">EN BROUILLON</span><strong class="stat-value"><c:out value="${nombreBrouillons}"/></strong><span class="stat-trend trend-neutral">À clôturer</span></article>
                <article class="stat-card"><span class="stat-label">PORTIONS PRODUITES</span><strong class="stat-value"><fmt:formatNumber value="${portionsTerminees}" groupingUsed="true"/></strong><span class="stat-trend trend-neutral">Productions terminées</span></article>
                <article class="stat-card"><span class="stat-label">COÛT MATIÈRE ENREGISTRÉ</span><strong class="stat-value"><fmt:formatNumber value="${coutTotalTermine}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong><span class="stat-trend trend-neutral">Coût moyen des achats compatibles</span></article>
            </section>
            <section class="page-card">
                <div class="page-toolbar"><h2>Historique des productions</h2><span class="muted">Les consommations sont saisies à la clôture.</span></div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead><tr><th>MET</th><th>RECETTE</th><th>DATE</th><th>PORTIONS PRÉVUES</th><th>PORTIONS RÉELLES</th><th>ÉTAT</th><th>DÉTAILS</th></tr></thead>
                        <tbody>
                        <c:forEach items="${productions}" var="production">
                            <tr>
                                <td><strong><c:out value="${production.recette.met.nom}"/></strong></td>
                                <td>v<c:out value="${production.recette.version}"/></td>
                                <td><c:out value="${production.dateProduction}"/></td>
                                <td><c:out value="${production.portionsPrevues}"/></td>
                                <td><c:choose><c:when test="${production.portionsReelles != null}"><c:out value="${production.portionsReelles}"/></c:when><c:otherwise>—</c:otherwise></c:choose></td>
                                <td><c:choose><c:when test="${production.brouillon}"><span class="row-badge bag-warn">Brouillon</span></c:when><c:otherwise><span class="row-badge bag-ok">Terminée</span></c:otherwise></c:choose></td>
                                <td><a class="page-button secondary" href="${pageContext.request.contextPath}/productions?action=details&amp;id=${production.id}">Consulter</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty productions}"><tr><td colspan="7" class="muted">Aucune production enregistrée.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
