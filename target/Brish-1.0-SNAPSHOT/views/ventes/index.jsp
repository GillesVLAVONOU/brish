<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Ventes | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Ventes</strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/rapports/marges">Rapport des marges</a></div>
        </header>
        <div class="module-body">
            <section class="page-heading">
                <div><p class="eyebrow">SUIVI DES VENTES</p><h1>Enregistrer une vente</h1><p class="heading-subtitle">Saisissez les portions vendues pour chaque production clôturée.</p></div>
            </section>
            <c:if test="${enregistre}"><div class="form-success" role="status">La vente a été enregistrée.</div></c:if>
            <c:if test="${not empty erreurs}"><div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div></c:if>
            <section class="page-card">
                <div class="page-toolbar">
                    <h2>Productions vendables</h2>
                    <span class="muted">Le prix saisi est conservé comme prix réellement appliqué.</span>
                </div>
                <form action="${pageContext.request.contextPath}/ventes" method="post" class="sales-form">
                    <input type="hidden" name="jetonCsrf" value="<c:out value='${jetonCsrf}'/>">
                    <div class="field sales-date-field">
                        <label for="dateVente">Date de vente *</label>
                        <input id="dateVente" name="dateVente" type="date" required max="<c:out value='${dateAujourdHui}'/>" value="<c:out value='${dateVente}'/>">
                    </div>
                    <div class="table-wrap">
                        <table class="list-table">
                            <thead><tr><th>MET / PRODUCTION</th><th>PORTIONS PRODUITES</th><th>DÉJÀ VENDUES</th><th>DISPONIBLES</th><th>PORTIONS VENDUES</th><th>PRIX UNITAIRE APPLIQUÉ</th></tr></thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${empty productionsVendables}">
                                    <tr><td colspan="6">Aucune production clôturée avec un coût enregistré et des portions disponibles.</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${productionsVendables}" var="production">
                                        <tr>
                                            <td><strong><c:out value="${production.production.recette.met.nom}"/></strong><br><span class="muted">#<c:out value="${production.production.id}"/> · <c:out value="${production.dateAffichee}"/></span><input type="hidden" name="productionId" value="<c:out value='${production.production.id}'/>"></td>
                                            <td><c:out value="${production.production.portionsReelles}"/></td>
                                            <td><c:out value="${production.portionsVendues}"/></td>
                                            <td><strong><c:out value="${production.portionsDisponibles}"/></strong></td>
                                            <td><input class="consumption-input" name="portionsVendues" type="number" min="0" max="<c:out value='${production.portionsDisponibles}'/>" step="1" value="<c:out value='${portionsSaisies[production.production.id]}' default='0'/>" aria-label="Portions vendues pour la production ${production.production.id}"></td>
                                            <td><span class="sales-price-field"><input class="consumption-input" name="prixUnitaire" type="number" min="0" step="0.01" value="<c:out value='${prixSaisis[production.production.id]}' default='${production.production.recette.met.prixVenteActuel}'/>" aria-label="Prix unitaire pour la production ${production.production.id}"><span class="sales-currency"><c:out value="${devise}"/></span></span></td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>
                    <div class="form-actions sales-form-actions">
                        <button class="page-button" type="submit" <c:if test="${empty productionsVendables}">disabled</c:if>>Enregistrer la vente</button>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
