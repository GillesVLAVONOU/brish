<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Relevé des achats | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/rapports">Rapports</a><span aria-hidden="true">/</span><strong>Relevé des achats</strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/rapports">Retour aux rapports</a></div>
        </header>
        <div class="module-body">
            <section class="page-card">
                <div class="page-toolbar"><h2>Factures d'achat</h2><span class="muted">Les montants sont calculés à partir des lignes de factures enregistrées.</span></div>
                <form action="${pageContext.request.contextPath}/rapports/achats" method="get" class="report-filter-form">
                    <div class="form-grid">
                        <div class="field"><label for="debut">Du *</label><input id="debut" name="debut" type="date" required value="<c:out value='${debut}'/>"></div>
                        <div class="field"><label for="fin">Au *</label><input id="fin" name="fin" type="date" required value="<c:out value='${fin}'/>"></div>
                    </div>
                    <div class="form-actions">
                        <button class="page-button" type="submit">Afficher le relevé</button>
                        <button class="page-button secondary" type="submit" name="format" value="pdf">Exporter en PDF</button>
                    </div>
                </form>
            </section>
            <c:if test="${not empty erreurs}"><div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div></c:if>
            <c:if test="${not empty debut and not empty fin and empty erreurs}">
                <section class="stat-grid">
                    <article class="stat-card"><span class="stat-label">FACTURES</span><strong class="stat-value"><c:out value="${nombreFactures}"/></strong></article>
                    <article class="stat-card"><span class="stat-label">LIGNES ACHETÉES</span><strong class="stat-value"><c:out value="${nombreLignes}"/></strong></article>
                    <article class="stat-card"><span class="stat-label">MONTANT TOTAL</span><strong class="stat-value"><fmt:formatNumber value="${montantTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                </section>
                <section class="page-card">
                    <div class="page-toolbar"><h2>Détail des factures</h2><span class="muted"><c:out value="${debut}"/> — <c:out value="${fin}"/></span></div>
                    <div class="table-wrap"><table class="list-table">
                        <thead><tr><th>DATE</th><th>N° FACTURE</th><th>FOURNISSEUR</th><th>LIGNES</th><th>MONTANT</th><th>DÉTAILS</th></tr></thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty achats}"><tr><td colspan="6">Aucun achat enregistré sur cette période.</td></tr></c:when>
                            <c:otherwise><c:forEach items="${achats}" var="achat">
                                <tr>
                                    <td><c:out value="${achat.dateAchat}"/></td>
                                    <td><c:out value="${achat.numeroFacture}"/></td>
                                    <td><c:out value="${achat.fournisseurNom}"/></td>
                                    <td><c:out value="${achat.nombreLignes}"/></td>
                                    <td><fmt:formatNumber value="${achat.montantTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    <td><a href="${pageContext.request.contextPath}/achats?action=details&amp;id=<c:out value='${achat.achatId}'/>">Consulter</a></td>
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
