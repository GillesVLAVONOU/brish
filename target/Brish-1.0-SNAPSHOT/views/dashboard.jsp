<%-- 
    Document   : dashboardjsp
    Created on : 15 oct. 2026, 01:12:47
    Author     : Dell
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<title>Dashboard | Brish</title>
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="app-page">
	<div class="app-shell">
		<%@ include file="/views/fragments/sidebar.jspf" %>

		<main class="main-content">
			<header class="topbar">
				<div class="breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Dashboard</strong></div>
				<div class="topbar-actions"><span class="today-label"><c:out value="${debutSemaine}"/> — <c:out value="${finSemaine}"/></span><button class="icon-button notification-button" type="button" aria-label="Notifications"><span aria-hidden="true">♧</span><i></i></button><span class="topbar-avatar"><c:out value="${initialesUtilisateur}"/></span></div>
			</header>

			<div class="dashboard-content">
				<section class="page-heading">
					<div><p class="eyebrow">VUE D'ENSEMBLE <span class="heading-period">· CETTE SEMAINE</span></p><h1>Bonjour, <span><c:out value="${sessionScope.nomUtilisateur}"/>.</span></h1><p class="heading-subtitle">Voici ce qui se passe dans votre cuisine.</p></div>
					<a class="button button-dark" href="${pageContext.request.contextPath}/productions"><span aria-hidden="true">＋</span> Nouvelle production</a>
				</section>

				<section class="metric-grid" aria-label="Indicateurs de la semaine">
					<article class="metric-card metric-highlight"><div class="metric-top"><span>COÛT DE PRODUCTION · 7 JOURS</span><span class="metric-symbol">↗</span></div><strong class="metric-value"><fmt:formatNumber value="${totalCoutSemaine}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> <small>${devise}</small></strong><div class="metric-foot"><span class="trend trend-neutral"><c:out value="${nombreProductionsSemaine}"/> clôturée(s)</span><span>sur la période</span></div></article>
					<article class="metric-card"><div class="metric-top"><span>PORTIONS PRODUITES</span><span class="metric-symbol symbol-muted">◷</span></div><strong class="metric-value"><fmt:formatNumber value="${totalPortionsSemaine}" groupingUsed="true"/></strong><div class="metric-foot"><span class="trend trend-neutral"><c:out value="${nombreProductionsSemaine}"/> production(s)</span><span>clôturée(s)</span></div></article>
					<article class="metric-card"><div class="metric-top"><span>COÛT MOYEN / PORTION</span><span class="metric-symbol symbol-muted">⌁</span></div><strong class="metric-value"><fmt:formatNumber value="${coutMoyenPortionSemaine}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> <small>${devise}</small></strong><div class="metric-foot"><span class="trend trend-neutral">Calculé</span><span>sur les portions clôturées</span></div></article>
					<article class="metric-card"><div class="metric-top"><span>MARGE BRUTE · 7 JOURS</span><a class="metric-symbol symbol-muted" href="${pageContext.request.contextPath}/rapports/marges" aria-label="Voir le rapport des marges">％</a></div><strong class="metric-value"><c:choose><c:when test="${nombreVentesSemaine gt 0}"><fmt:formatNumber value="${tauxMargeSemaine}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> <small>%</small></c:when><c:otherwise>—</c:otherwise></c:choose></strong><div class="metric-foot"><span class="trend trend-neutral"><fmt:formatNumber value="${margeBruteSemaine}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</span><span><c:out value="${nombreVentesSemaine}"/> ligne(s) vendue(s)</span></div></article>
				</section>

				<section class="dashboard-grid">
					<article class="panel production-panel">
						<div class="panel-heading"><div><p class="eyebrow">SUIVI OPÉRATIONNEL</p><h2>Productions récentes</h2></div><a class="text-link" href="${pageContext.request.contextPath}/productions">Tout voir <span aria-hidden="true">→</span></a></div>
						<div class="table-wrap"><table class="data-table"><thead><tr><th>MET</th><th>DATE</th><th>PORTIONS</th><th>COÛT TOTAL</th><th>COÛT / PORTION</th></tr></thead><tbody>
							<c:choose>
								<c:when test="${empty productionsSemaine}"><tr><td colspan="5">Aucune production clôturée sur les 7 derniers jours.</td></tr></c:when>
								<c:otherwise><c:forEach items="${productionsSemaine}" var="production" begin="0" end="3"><tr><td><strong><c:out value="${production.metNom}"/></strong> · v<c:out value="${production.versionRecette}"/></td><td><c:out value="${production.dateAffichee}"/></td><td><fmt:formatNumber value="${production.portionsReelles}" groupingUsed="true"/></td><td><fmt:formatNumber value="${production.coutTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td><td><strong><fmt:formatNumber value="${production.coutUnitaire}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></td></tr></c:forEach></c:otherwise>
							</c:choose>
						</tbody></table></div>
						<a class="mobile-all-link" href="${pageContext.request.contextPath}/productions">Voir toutes les productions →</a>
					</article>

					<aside class="panel cost-panel">
						<div class="panel-heading"><div><p class="eyebrow">RÉPARTITION · 7 JOURS</p><h2>Coûts enregistrés</h2></div></div>
						<div class="cost-total"><strong><fmt:formatNumber value="${totalCoutSemaine}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/></strong><span>${devise} au total</span></div>
						<ul class="legend-list"><li><span class="legend-name"><i class="legend-color color-ingredients"></i>Matières</span><strong><fmt:formatNumber value="${totalMatieresSemaine}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></li><li><span class="legend-name"><i class="legend-color color-other"></i>Charges</span><strong><fmt:formatNumber value="${totalChargesSemaine}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></li></ul>
					</aside>
				</section>

				<footer class="dashboard-footer"><span>Vos données sont à jour <i class="sync-dot"></i></span><span>Dernière synchronisation : <c:out value="${derniereSynchronisation}"/></span></footer>
			</div>
		</main>
	</div>
	<script src="${pageContext.request.contextPath}/assets/js/app-navigation.js"></script>
</body>
</html>
