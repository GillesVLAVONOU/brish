<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Coûts de production | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>

    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Coûts de production</strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/rapports">Retour aux rapports</a></div>
        </header>

        <div class="module-body">
            <section class="page-card">
                <div class="page-toolbar"><h2>Coûts des productions clôturées</h2><span class="muted">Période basée sur la date de production; le dernier coût enregistré est retenu.</span></div>
                <form action="${pageContext.request.contextPath}/rapports/couts" method="get" class="report-filter-form">
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
                    <article class="stat-card"><span class="stat-label">PRODUCTIONS TERMINÉES</span><strong class="stat-value"><c:out value="${nombreProductions}"/></strong></article>
                    <article class="stat-card"><span class="stat-label">PORTIONS PRODUITES</span><strong class="stat-value"><c:out value="${totalPortions}"/></strong></article>
                    <article class="stat-card"><span class="stat-label">COÛT MATIÈRE</span><strong class="stat-value"><fmt:formatNumber value="${totalMatieres}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                    <article class="stat-card"><span class="stat-label">CHARGES</span><strong class="stat-value"><fmt:formatNumber value="${totalCharges}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                    <article class="stat-card"><span class="stat-label">COÛT TOTAL</span><strong class="stat-value"><fmt:formatNumber value="${totalCout}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                    <article class="stat-card"><span class="stat-label">COÛT MOYEN / PORTION</span><strong class="stat-value"><fmt:formatNumber value="${coutMoyenPortion}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                </section>
                <section class="page-card">
                    <div class="page-toolbar"><h2>Détail par production</h2><span class="muted"><c:out value="${debut}"/> — <c:out value="${fin}"/></span></div>
                    <div class="table-wrap"><table class="list-table">
                        <thead><tr><th>DATE</th><th>MET / RECETTE</th><th>PORTIONS</th><th>MATIÈRE</th><th>CHARGES</th><th>TOTAL</th><th>COÛT / PORTION</th></tr></thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty productions}"><tr><td colspan="7">Aucune production clôturée sur cette période.</td></tr></c:when>
                            <c:otherwise><c:forEach items="${productions}" var="production">
                                <tr>
                                    <td><c:out value="${production.dateAffichee}"/></td>
                                    <td><strong><c:out value="${production.metNom}"/></strong> · v<c:out value="${production.versionRecette}"/></td>
                                    <td><c:out value="${production.portionsReelles}"/></td>
                                    <td><fmt:formatNumber value="${production.coutMatiere}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    <td><fmt:formatNumber value="${production.coutCharges}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    <td><fmt:formatNumber value="${production.coutTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    <td><fmt:formatNumber value="${production.coutUnitaire}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
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
