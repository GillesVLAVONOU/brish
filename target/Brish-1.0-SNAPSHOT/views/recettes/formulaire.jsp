<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Recette | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/mets">Mets</a><span aria-hidden="true">/</span><strong>Recette de <c:out value="${met.nom}"/></strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/mets?action=details&amp;id=${met.id}">Retour au met</a></div>
        </header>
        <div class="module-body">
            <section class="page-card info-panel">
                <div class="page-toolbar">
                    <h2><c:choose><c:when test="${recetteSourceId == null}">Nouvelle fiche recette</c:when><c:otherwise>Créer une nouvelle version</c:otherwise></c:choose></h2>
                    <span class="muted">L'ancienne version restera disponible dans l'historique.</span>
                </div>
                <c:if test="${not empty erreurs}">
                    <div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div>
                </c:if>
                <form action="${pageContext.request.contextPath}/recettes" method="post" class="ingredient-form">
                    <input type="hidden" name="action" value="enregistrer">
                    <input type="hidden" name="metId" value="<c:out value='${met.id}'/>">
                    <c:if test="${recetteSourceId != null}"><input type="hidden" name="recetteSourceId" value="<c:out value='${recetteSourceId}'/>"></c:if>
                    <div class="form-grid">
                        <div class="field">
                            <label>Met</label>
                            <input value="<c:out value='${met.nom}'/>" readonly>
                        </div>
                        <div class="field">
                            <label for="rendementPortions">Rendement (portions) *</label>
                            <input id="rendementPortions" name="rendementPortions" type="number" min="1" step="1" required value="<c:out value='${rendementPortions}'/>">
                        </div>
                        <div class="field">
                            <label for="dateEffet">Date d'effet *</label>
                            <input id="dateEffet" name="dateEffet" type="date" max="<c:out value='${dateEffetMax}'/>" required value="<c:out value='${dateEffet}'/>">
                        </div>
                    </div>
                    <div class="page-toolbar">
                        <h2>Ingrédients</h2>
                        <c:choose>
                            <c:when test="${empty ingredientsActifs}"><button class="page-button secondary" type="button" id="ajouter-ligne" disabled>+ Ajouter un ingrédient</button></c:when>
                            <c:otherwise><button class="page-button secondary" type="button" id="ajouter-ligne">+ Ajouter un ingrédient</button></c:otherwise>
                        </c:choose>
                    </div>
                    <p class="muted">Les quantités doivent être exprimées dans l'unité de base indiquée pour chaque ingrédient. Aucune conversion automatique n'est appliquée.</p>
                    <c:if test="${empty ingredientsActifs}"><p class="muted">Aucun ingrédient actif. Ajoutez ou réactivez un ingrédient avant de créer une recette.</p></c:if>
                    <div id="lignes-recette">
                        <c:forEach items="${lignesFormulaire}" var="ligne" varStatus="statut">
                            <div class="form-grid recette-line">
                                <div class="field">
                                    <label for="ingredient-${statut.index}">Ingrédient *</label>
                                    <select id="ingredient-${statut.index}" name="ingredientId" required>
                                        <option value="">Sélectionner un ingrédient</option>
                                        <c:forEach items="${ingredientsActifs}" var="ingredient">
                                            <option value="<c:out value='${ingredient.id}'/>" data-unite="<c:out value='${ingredient.uniteBase}'/>" ${ligne.ingredientId == ingredient.id ? 'selected' : ''}><c:out value="${ingredient.nom}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="field">
                                    <label for="quantite-${statut.index}">Quantité *</label>
                                    <input id="quantite-${statut.index}" name="quantite" type="number" min="0.001" step="0.001" required value="<c:out value='${ligne.quantite}'/>">
                                </div>
                                <div class="field">
                                    <label>Unité de base</label>
                                    <input class="unite-base" value="<c:out value='${ligne.unite}'/>" readonly>
                                </div>
                                <div class="field line-action">
                                    <label>Action</label>
                                    <button class="page-button secondary retirer-ligne" type="button">Retirer</button>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <template id="modele-ligne">
                        <div class="form-grid recette-line">
                            <div class="field">
                                <label>Ingrédient *</label>
                                <select name="ingredientId" required>
                                    <option value="">Sélectionner un ingrédient</option>
                                    <c:forEach items="${ingredientsActifs}" var="ingredient">
                                        <option value="<c:out value='${ingredient.id}'/>" data-unite="<c:out value='${ingredient.uniteBase}'/>"><c:out value="${ingredient.nom}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="field"><label>Quantité *</label><input name="quantite" type="number" min="0.001" step="0.001" required></div>
                            <div class="field"><label>Unité de base</label><input class="unite-base" readonly></div>
                            <div class="field line-action"><label>Action</label><button class="page-button secondary retirer-ligne" type="button">Retirer</button></div>
                        </div>
                    </template>
                    <div class="form-actions">
                        <a class="page-button secondary" href="${pageContext.request.contextPath}/mets?action=details&amp;id=${met.id}">Annuler</a>
                        <c:choose>
                            <c:when test="${empty ingredientsActifs}"><button class="page-button" type="submit" disabled>Enregistrer la version</button></c:when>
                            <c:otherwise><button class="page-button" type="submit">Enregistrer la version</button></c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
<script>
    (function () {
        const container = document.getElementById("lignes-recette");
        const template = document.getElementById("modele-ligne");
        const addButton = document.getElementById("ajouter-ligne");
        addButton.addEventListener("click", function () {
            container.appendChild(template.content.cloneNode(true));
        });
        container.addEventListener("click", function (event) {
            if (event.target.classList.contains("retirer-ligne")) {
                const lignes = container.querySelectorAll(".recette-line");
                if (lignes.length > 1) event.target.closest(".recette-line").remove();
            }
        });
        container.addEventListener("change", function (event) {
            if (event.target.name === "ingredientId") {
                const unite = event.target.selectedOptions[0].dataset.unite || "";
                event.target.closest(".recette-line").querySelector(".unite-base").value = unite;
            }
        });
        container.querySelectorAll('select[name="ingredientId"]').forEach(function (select) {
            select.dispatchEvent(new Event("change", { bubbles: true }));
        });
    }());
</script>
</body>
</html>
