<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Marges et rentabilité | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>

    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Marges et rentabilité</strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/ventes">Enregistrer une vente</a></div>
        </header>

        <div class="module-body">
            <section class="page-card">
                <div class="page-toolbar"><h2>Rapport des marges</h2><span class="muted">Calcul basé sur les ventes réelles et le coût enregistré de chaque production.</span></div>
                <form action="${pageContext.request.contextPath}/rapports/marges" method="get" class="report-filter-form">
                    <div class="form-grid">
                        <div class="field"><label for="debut">Du *</label><input id="debut" name="debut" type="date" required value="<c:out value='${debut}'/>"></div>
                        <div class="field"><label for="fin">Au *</label><input id="fin" name="fin" type="date" required value="<c:out value='${fin}'/>"></div>
                    </div>
                    <div class="form-actions">
                        <button class="page-button" type="submit">Afficher le rapport</button>
                        <button class="page-button secondary" type="submit" name="format" value="pdf">Exporter en PDF</button>
                    </div>
                </form>
            </section>
            <c:if test="${not empty erreurs}"><div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div></c:if>
            <c:if test="${not empty debut and not empty fin and empty erreurs}">
                <section class="stat-grid">
                    <article class="stat-card"><span class="stat-label">PORTIONS VENDUES</span><strong class="stat-value"><c:out value="${totalPortionsVendues}"/></strong></article>
                    <article class="stat-card"><span class="stat-label">CHIFFRE D'AFFAIRES</span><strong class="stat-value"><fmt:formatNumber value="${chiffreAffaires}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                    <article class="stat-card"><span class="stat-label">COÛT DES PORTIONS VENDUES</span><strong class="stat-value"><fmt:formatNumber value="${coutAssocie}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                    <article class="stat-card"><span class="stat-label">MARGE BRUTE</span><strong class="stat-value"><fmt:formatNumber value="${margeBrute}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong><span class="stat-trend trend-neutral"><fmt:formatNumber value="${tauxMarge}" minFractionDigits="0" maxFractionDigits="2"/> % du chiffre d'affaires</span></article>
                </section>
                <section class="page-card">
                    <div class="page-toolbar"><h2>Détail des ventes</h2><span class="muted"><c:out value="${debut}"/> — <c:out value="${fin}"/></span></div>
                    <div class="table-wrap"><table class="list-table">
                        <thead><tr><th>DATE</th><th>MET / PRODUCTION</th><th>PORTIONS</th><th>PRIX UNITAIRE</th><th>CHIFFRE D'AFFAIRES</th><th>COÛT ASSOCIÉ</th><th>MARGE</th><th>TAUX</th></tr></thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty lignesMarge}"><tr><td colspan="8">Aucune vente enregistrée sur cette période.</td></tr></c:when>
                            <c:otherwise><c:forEach items="${lignesMarge}" var="ligne">
                                <tr>
                                    <td><c:out value="${ligne.dateVente}"/></td>
                                    <td><strong><c:out value="${ligne.metNom}"/></strong> · #<c:out value="${ligne.productionId}"/></td>
                                    <td><c:out value="${ligne.portionsVendues}"/></td>
                                    <td><fmt:formatNumber value="${ligne.prixUnitaire}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    <td><fmt:formatNumber value="${ligne.chiffreAffaires}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    <td><fmt:formatNumber value="${ligne.coutAssocie}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    <td><fmt:formatNumber value="${ligne.margeBrute}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    <td><fmt:formatNumber value="${ligne.tauxMarge}" minFractionDigits="0" maxFractionDigits="2"/> %</td>
                                </tr>
                            </c:forEach></c:otherwise>
                        </c:choose>
                        </tbody>
                    </table></div>
                </section>
            </c:if>
        </div>
    </main>
</div>
</body>
</html>
