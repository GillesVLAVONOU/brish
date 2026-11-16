<%-- 
    Document   : indexjsp
    Created on : 15 oct. 2026, 01:12:47
    Author     : Dell
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Rapports | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>

    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Rapports</strong></div>
            <div class="module-header-actions"></div>
        </header>
        <div class="module-body section-grid">
            <section class="page-card info-panel">
                <h3>Rapports disponibles</h3>
                <div class="kpi-stack">
                    <div class="kpi-box"><strong><a href="${pageContext.request.contextPath}/rapports/couts">Coûts de production</a></strong><span>Suivi de toutes les charges et coûts unitaires</span></div>
                    <div class="kpi-box"><strong><a href="${pageContext.request.contextPath}/rapports/achats">Relevé des achats</a></strong><span>Factures, fournisseurs et montants sur une période choisie</span></div>
                    <div class="kpi-box"><strong><a href="${pageContext.request.contextPath}/rapports/marges">Marges et rentabilité</a></strong><span>Chiffre d'affaires, coût des portions vendues et marge brute réelle.</span></div>
                    <div class="kpi-box"><strong><a href="${pageContext.request.contextPath}/ventes">Suivi des ventes</a></strong><span>Enregistrement quotidien des portions vendues par production.</span></div>
                </div>
            </section>
            <aside class="page-card info-panel">
                <h3>Performance</h3>
                <div class="kpi-stack">
                    <div class="kpi-box"><strong>Données réelles</strong><span>Les rapports sont calculés à partir des productions clôturées et des factures enregistrées.</span></div>
                    <div class="kpi-box"><strong>Période au choix</strong><span>Choisissez les dates de début et de fin dans chaque rapport.</span></div>
                </div>
            </aside>
        </div>
    </main>
</div>
</body>
</html>
