<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="fr_FR" />
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Ingrédients | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>

    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong><c:choose><c:when test="${formMode}">${ingredient.id == null ? 'Nouvel ingrédient' : 'Modifier l’ingrédient'}</c:when><c:otherwise>Ingrédients</c:otherwise></c:choose></strong></div>
            <div class="module-header-actions">
                <c:choose>
                    <c:when test="${formMode}"><a class="page-button secondary" href="${pageContext.request.contextPath}/ingredients">Retour à la liste</a></c:when>
                    <c:otherwise>
                        <form class="module-header-actions" action="${pageContext.request.contextPath}/ingredients" method="get" data-app-navigation>
                            <input class="page-search" type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Rechercher..." aria-label="Rechercher un ingrédient">
                            <button class="page-button secondary" type="submit">Rechercher</button>
                        </form>
                        <a class="page-button" href="${pageContext.request.contextPath}/ingredients?action=nouveau">+ Ajouter</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </header>

        <div class="module-body">
            <c:choose>
                <c:when test="${formMode}">
                    <section class="page-card info-panel">
                        <div class="page-toolbar"><h2><c:choose><c:when test="${ingredient.id == null}">Nouvel ingrédient</c:when><c:otherwise>Modifier l’ingrédient</c:otherwise></c:choose></h2></div>
                        <c:if test="${not empty erreurs}">
                            <div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/ingredients" method="post" data-app-navigation class="ingredient-form">
                            <input type="hidden" name="action" value="enregistrer">
                            <c:if test="${ingredient.id != null}"><input type="hidden" name="id" value="<c:out value='${ingredient.id}'/>" /></c:if>
                            <div class="form-grid">
                                <div class="field"><label for="nom">Nom *</label><input id="nom" name="nom" maxlength="120" required value="<c:out value='${ingredient.nom}'/>" /></div>
                                <div class="field"><label for="uniteBase">Unité de base *</label><input id="uniteBase" name="uniteBase" maxlength="20" required placeholder="g, mL, pièce..." value="<c:out value='${ingredient.uniteBase}'/>" /></div>
                                <div class="field"><label for="seuilAlerte">Seuil d’alerte</label><input id="seuilAlerte" name="seuilAlerte" type="number" min="0" step="0.001" value="<c:out value='${ingredient.seuilAlerte}'/>" /></div>
                            </div>
                            <div class="form-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/ingredients">Annuler</a><button class="page-button" type="submit">Enregistrer</button></div>
                        </form>
                    </section>
                </c:when>
                <c:otherwise>
            <section class="stat-grid">
                <article class="stat-card">
                    <span class="stat-label">INGRÉDIENTS</span>
                    <strong class="stat-value"><c:out value="${nombreIngredients}"/></strong>
                    <span class="stat-trend trend-neutral">Référencés</span>
                </article>
                <article class="stat-card">
                    <span class="stat-label">ACTIFS</span>
                    <strong class="stat-value"><c:out value="${nombreActifs}"/></strong>
                    <span class="stat-trend trend-neutral">Utilisables</span>
                </article>
                <article class="stat-card">
                    <span class="stat-label">INACTIFS</span>
                    <strong class="stat-value"><c:out value="${nombreIngredients - nombreActifs}"/></strong>
                    <span class="stat-trend trend-neutral">Désactivés</span>
                </article>
                <article class="stat-card">
                    <span class="stat-label">UNITÉS DE BASE</span>
                    <strong class="stat-value"><c:out value="${nombreUnitesBase}"/></strong>
                    <span class="stat-trend trend-neutral">Référencées</span>
                </article>
            </section>

            <section class="page-card">
                <div class="page-toolbar">
                    <h2>Liste des ingrédients</h2>
                    <span class="muted"> </span>
                </div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead>
                        <tr><th>INGRÉDIENT</th><th>UNITÉ DE BASE</th><th>SEUIL D’ALERTE</th><th>ÉTAT</th><th>ACTIONS</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${ingredients}" var="ingredient">
                            <tr>
                                <td><strong><c:out value="${ingredient.nom}"/></strong></td>
                                <td><c:out value="${ingredient.uniteBase}"/></td>
                                <td><fmt:formatNumber value="${ingredient.seuilAlerte}" maxFractionDigits="3" groupingUsed="true"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not ingredient.actif}"><span class="row-badge bag-info">INACTIF</span></c:when>
                                        <c:otherwise><span class="row-badge bag-ok">ACTIF</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="ingredient-actions">
                                    <a class="page-button secondary" href="${pageContext.request.contextPath}/ingredients?action=modifier&amp;id=${ingredient.id}">Modifier</a>
                                    <c:if test="${ingredient.actif}">
                                        <form action="${pageContext.request.contextPath}/ingredients" method="post" data-app-navigation>
                                            <input type="hidden" name="action" value="desactiver">
                                            <input type="hidden" name="id" value="<c:out value='${ingredient.id}'/>" />
                                            <button class="page-button secondary deactivate-icon-button" type="submit" title="Désactiver cet ingrédient" aria-label="Désactiver l’ingrédient <c:out value='${ingredient.nom}'/>"></button>
                                        </form>
                                    </c:if>
                                    <c:if test="${not ingredient.actif}"><span class="muted">Inactif</span></c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty ingredients}">
                            <tr><td class="empty-state" colspan="5">Aucun ingrédient trouvé.</td></tr>
                        </c:if>
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
