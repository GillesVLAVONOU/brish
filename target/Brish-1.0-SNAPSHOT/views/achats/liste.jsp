<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Achats | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>

    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Achats</strong></div>
            <div class="module-header-actions">
                <form class="module-header-actions" action="${pageContext.request.contextPath}/achats" method="get">
                    <input class="page-search" type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Facture, fournisseur, ingrédient..." aria-label="Rechercher un achat">
                    <button class="page-button secondary" type="submit">Rechercher</button>
                </form>
                <a class="page-button" href="${pageContext.request.contextPath}/achats?action=nouveau">+ Nouvel achat</a>
            </div>
        </header>

        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card">
                    <span class="stat-label">FACTURES ENREGISTRÉES</span>
                    <strong class="stat-value"><c:out value="${nombreAchats}"/></strong>
                    <span class="stat-trend trend-neutral">Toutes périodes</span>
                </article>
                <article class="stat-card">
                    <span class="stat-label">MONTANT TOTAL ENREGISTRÉ</span>
                    <strong class="stat-value"><fmt:formatNumber value="${montantTotalAchats}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong>
                    <span class="stat-trend trend-neutral">Calculé à partir des lignes de facture</span>
                </article>
            </section>

            <section class="page-card">
                <div class="page-toolbar"><h2>Historique des achats</h2><span class="muted"><c:out value="${nombreAchats}"/> facture(s)</span></div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead>
                        <tr><th>FACTURE</th><th>FOURNISSEUR</th><th>DATE</th><th>LIGNES</th><th>MONTANT</th><th>DÉTAILS</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${achats}" var="achat">
                            <tr>
                                <td><strong><c:out value="${achat.numeroFacture}"/></strong></td>
                                <td><c:out value="${achat.fournisseur.nom}"/></td>
                                <td><c:out value="${achat.dateAchat}"/></td>
                                <td><c:out value="${achat.lignes.size()}"/></td>
                                <td><fmt:formatNumber value="${achat.montantTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                <td><a class="page-button secondary" href="${pageContext.request.contextPath}/achats?action=details&amp;id=${achat.id}">Consulter</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty achats}">
                            <tr><td colspan="6" class="muted">Aucun achat trouvé.</td></tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
