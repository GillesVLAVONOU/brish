<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Facture <c:out value="${achat.numeroFacture}"/> | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb">
                <a href="${pageContext.request.contextPath}/achats">Achats</a>
                <span aria-hidden="true">/</span>
                <strong>Facture <c:out value="${achat.numeroFacture}"/></strong>
            </div>
            <div class="module-header-actions">
                <a class="page-button secondary" href="${pageContext.request.contextPath}/achats">Retour à la liste</a>
            </div>
        </header>
        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card">
                    <span class="stat-label">FOURNISSEUR</span>
                    <strong class="stat-value"><c:out value="${achat.fournisseur.nom}"/></strong>
                    <span class="stat-trend trend-neutral"><c:out value="${achat.dateAchat}"/></span>
                </article>
                <article class="stat-card">
                    <span class="stat-label">NUMÉRO DE FACTURE</span>
                    <strong class="stat-value"><c:out value="${achat.numeroFacture}"/></strong>
                    <span class="stat-trend trend-neutral"><c:out value="${achat.lignes.size()}"/> ligne(s)</span>
                </article>
                <article class="stat-card">
                    <span class="stat-label">TOTAL DE LA FACTURE</span>
                    <strong class="stat-value"><fmt:formatNumber value="${achat.montantTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong>
                    <span class="stat-trend trend-neutral">Somme des lignes</span>
                </article>
            </section>
            <section class="page-card">
                <div class="page-toolbar">
                    <h2>Lignes de facture</h2>
                    <span class="muted">Consultation seulement</span>
                </div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead>
                        <tr><th>INGRÉDIENT</th><th>QUANTITÉ</th><th>UNITÉ</th><th>PRIX UNITAIRE</th><th>MONTANT</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${achat.lignes}" var="ligne">
                            <tr>
                                <td><strong><c:out value="${ligne.ingredient.nom}"/></strong></td>
                                <td><fmt:formatNumber value="${ligne.quantite}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="3"/></td>
                                <td><c:out value="${ligne.unite}"/></td>
                                <td><fmt:formatNumber value="${ligne.prixUnitaire}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                <td><fmt:formatNumber value="${ligne.montantTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <p class="muted">Cette facture est enregistrée à titre de suivi. Elle ne modifie ni les quantités en stock ni les coûts courants des ingrédients.</p>
            </section>
        </div>
    </main>
</div>
</body>
</html>
