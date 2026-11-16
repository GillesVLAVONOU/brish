<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Production <c:out value="${production.id}"/> | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/productions">Productions</a><span aria-hidden="true">/</span><strong>#<c:out value="${production.id}"/> · <c:out value="${production.recette.met.nom}"/></strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/productions">Retour à la liste</a></div>
        </header>
        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card"><span class="stat-label">MET / RECETTE</span><strong class="stat-value"><c:out value="${production.recette.met.nom}"/></strong><span class="stat-trend trend-neutral">Recette v<c:out value="${production.recette.version}"/></span></article>
                <article class="stat-card"><span class="stat-label">DATE DE PRODUCTION</span><strong class="stat-value"><c:out value="${dateAffichee}"/></strong></article>
                <article class="stat-card"><span class="stat-label">PORTIONS PRÉVUES</span><strong class="stat-value"><c:out value="${production.portionsPrevues}"/></strong><span class="stat-trend trend-neutral">prévues</span></article>
                <article class="stat-card"><span class="stat-label">ÉTAT</span><strong class="stat-value"><c:choose><c:when test="${production.brouillon}">Brouillon</c:when><c:otherwise>Terminée</c:otherwise></c:choose></strong></article>
            </section>

            <c:if test="${not empty erreurs}">
                <div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div>
            </c:if>

            <c:choose>
                <c:when test="${production.brouillon}">
                    <section class="page-card production-close-card">
                        <div class="page-toolbar">
                            <h2>Clôturer la production</h2>
                            <span class="muted">Saisissez les consommations réelles dans l'unité de base.</span>
                        </div>
                        <form action="${pageContext.request.contextPath}/productions" method="post" class="ingredient-form">
                            <input type="hidden" name="action" value="terminer">
                            <input type="hidden" name="id" value="<c:out value='${production.id}'/>">
                            <div class="field production-portions-field">
                                <label for="portionsReelles">Portions réellement produites *</label>
                                <input id="portionsReelles" name="portionsReelles" type="number" min="1" step="1" required value="<c:out value='${production.portionsReelles}'/>">
                            </div>
                            <div class="page-toolbar"><h2>Consommation réelle des ingrédients</h2></div>
                            <p class="muted">Le coût sera calculé avec la moyenne pondérée des achats antérieurs à la production, uniquement pour les achats dans la même unité de base. Si aucun prix compatible n'existe, la clôture sera refusée. Les quantités saisies ne décrémentent pas de stock.</p>
                            <div class="table-wrap">
                                <table class="list-table">
                                    <thead><tr><th>INGRÉDIENT</th><th>QUANTITÉ RECETTE</th><th>UNITÉ DE BASE</th><th>QUANTITÉ RÉELLEMENT CONSOMMÉE *</th></tr></thead>
                                    <tbody>
                                    <c:forEach items="${lignesFormulaire}" var="ligne" varStatus="statut">
                                        <tr>
                                            <td><strong><c:out value="${ligne.ingredientNom}"/></strong><input type="hidden" name="ingredientId" value="<c:out value='${ligne.ingredientId}'/>"></td>
                                            <td><c:out value="${ligne.quantiteRecette}"/></td>
                                            <td><c:out value="${ligne.unite}"/></td>
                                            <td><input class="consumption-input" name="quantiteReelle" type="number" min="0" step="0.001" required value="<c:out value='${ligne.quantiteReelle}'/>" aria-label="Quantité réellement consommée de ${ligne.ingredientNom}"></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="form-actions">
                                <a class="page-button secondary" href="${pageContext.request.contextPath}/productions">Annuler</a>
                                <button class="page-button" type="submit">Enregistrer les consommations et clôturer</button>
                            </div>
                        </form>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="stat-grid">
                        <article class="stat-card"><span class="stat-label">PORTIONS RÉELLES</span><strong class="stat-value"><c:out value="${production.portionsReelles}"/></strong></article>
                        <article class="stat-card"><span class="stat-label">COÛT MATIÈRE</span><strong class="stat-value"><fmt:formatNumber value="${dernierCout.coutMatiere}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                        <article class="stat-card"><span class="stat-label">COÛT TOTAL</span><strong class="stat-value"><fmt:formatNumber value="${dernierCout.coutTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                        <article class="stat-card"><span class="stat-label">COÛT PAR PORTION</span><strong class="stat-value"><fmt:formatNumber value="${dernierCout.coutUnitaire}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</strong></article>
                    </section>
                    <section class="page-card">
                        <div class="page-toolbar"><h2>Quantités réellement consommées</h2><span class="muted">Aucun mouvement de stock n'est effectué</span></div>
                        <div class="table-wrap">
                            <table class="list-table">
                                <thead><tr><th>INGRÉDIENT</th><th>QUANTITÉ CONSOMMÉE</th><th>UNITÉ</th><th>COÛT UNITAIRE APPLIQUÉ</th><th>COÛT TOTAL</th></tr></thead>
                                <tbody>
                                <c:forEach items="${lignesProduction}" var="ligne">
                                    <tr>
                                        <td><strong><c:out value="${ligne.ingredient.nom}"/></strong></td>
                                        <td><fmt:formatNumber value="${ligne.quantiteReelle}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="3"/></td>
                                        <td><c:out value="${ligne.unite}"/></td>
                                        <td><fmt:formatNumber value="${ligne.coutUnitaireApplique}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                        <td><fmt:formatNumber value="${ligne.coutTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>
                    <section class="page-card">
                        <div class="page-toolbar"><h2>Historique des calculs de coût</h2></div>
                        <div class="table-wrap">
                            <table class="list-table">
                                <thead><tr><th>DATE DU CALCUL</th><th>COÛT MATIÈRE</th><th>CHARGES</th><th>COÛT TOTAL</th><th>COÛT / PORTION</th></tr></thead>
                                <tbody>
                                <c:forEach items="${historiqueCouts}" var="cout">
                                    <tr>
                                        <td><c:out value="${cout.dateCalcul}"/></td>
                                        <td><fmt:formatNumber value="${cout.coutMatiere}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                        <td><fmt:formatNumber value="${cout.coutCharges}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                        <td><fmt:formatNumber value="${cout.coutTotal}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                        <td><fmt:formatNumber value="${cout.coutUnitaire}" groupingUsed="true" minFractionDigits="0" maxFractionDigits="2"/> ${devise}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>
</body>
</html>
