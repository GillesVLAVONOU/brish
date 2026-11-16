<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Nouvel achat | Brish</title>
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
                <strong>Nouvel achat</strong>
            </div>
            <div class="module-header-actions">
                <a class="page-button secondary" href="${pageContext.request.contextPath}/achats">Retour à la liste</a>
            </div>
        </header>
        <div class="module-body">
            <section class="page-card info-panel">
                <div class="page-toolbar">
                    <h2>Enregistrer une facture</h2>
                    <span class="muted">L'achat est archivé sans modifier les stocks ni les coûts courants.</span>
                </div>
                <c:if test="${not empty erreurs}">
                    <div class="form-alert" role="alert">
                        <ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul>
                    </div>
                </c:if>
                <form action="${pageContext.request.contextPath}/achats" method="post" class="ingredient-form" id="achat-form">
                    <input type="hidden" name="action" value="enregistrer">
                    <div class="form-grid">
                        <div class="field">
                            <label for="fournisseurId">Fournisseur *</label>
                            <select id="fournisseurId" name="fournisseurId" required>
                                <option value="">Sélectionner un fournisseur</option>
                                <c:forEach items="${fournisseurs}" var="fournisseur">
                                    <option value="<c:out value='${fournisseur.id}'/>" ${fournisseurId == fournisseur.id ? 'selected' : ''}><c:out value="${fournisseur.nom}"/></option>
                                </c:forEach>
                            </select>
                            <c:if test="${empty fournisseurs}"><small>Créez d'abord un fournisseur.</small></c:if>
                        </div>
                        <div class="field">
                            <label for="numeroFacture">Numéro de facture *</label>
                            <input id="numeroFacture" name="numeroFacture" maxlength="80" required value="<c:out value='${numeroFacture}'/>">
                        </div>
                        <div class="field">
                            <label for="dateAchat">Date de l'achat *</label>
                            <input id="dateAchat" name="dateAchat" type="date" required value="<c:out value='${dateAchat}'/>">
                        </div>
                    </div>

                    <div class="page-toolbar">
                        <h2>Lignes de facture</h2>
                        <button class="page-button secondary" type="button" id="ajouter-ligne">+ Ajouter une ligne</button>
                    </div>
                    <c:if test="${empty ingredientsActifs}">
                        <p class="muted">Aucun ingrédient actif. Ajoutez ou réactivez un ingrédient avant de saisir un achat.</p>
                    </c:if>
                    <div id="lignes-achat">
                        <c:forEach items="${lignesFormulaire}" var="ligne" varStatus="statut">
                            <div class="form-grid achat-line">
                                <div class="field">
                                    <label for="ingredient-${statut.index}">Ingrédient *</label>
                                    <select id="ingredient-${statut.index}" name="ingredientId" required>
                                        <option value="">Sélectionner</option>
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
                                    <label for="unite-${statut.index}">Unité *</label>
                                    <input id="unite-${statut.index}" name="unite" maxlength="20" required placeholder="kg, g, pièce..." value="<c:out value='${ligne.unite}'/>">
                                </div>
                                <div class="field">
                                    <label for="prix-${statut.index}">Prix unitaire (${devise}) *</label>
                                    <input id="prix-${statut.index}" name="prixUnitaire" type="number" min="0" step="0.01" required value="<c:out value='${ligne.prixUnitaire}'/>">
                                </div>
                                <div class="field line-action">
                                    <label>Action</label>
                                    <button class="page-button secondary retirer-ligne" type="button">Retirer</button>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <template id="modele-ligne">
                        <div class="form-grid achat-line">
                            <div class="field">
                                <label>Ingrédient *</label>
                                <select name="ingredientId" required>
                                    <option value="">Sélectionner</option>
                                    <c:forEach items="${ingredientsActifs}" var="ingredient">
                                        <option value="<c:out value='${ingredient.id}'/>" data-unite="<c:out value='${ingredient.uniteBase}'/>"><c:out value="${ingredient.nom}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="field">
                                <label>Quantité *</label>
                                <input name="quantite" type="number" min="0.001" step="0.001" required>
                            </div>
                            <div class="field">
                                <label>Unité *</label>
                                <input name="unite" maxlength="20" required placeholder="kg, g, pièce...">
                            </div>
                            <div class="field">
                                <label>Prix unitaire (${devise}) *</label>
                                <input name="prixUnitaire" type="number" min="0" step="0.01" required>
                            </div>
                            <div class="field line-action">
                                <label>Action</label>
                                <button class="page-button secondary retirer-ligne" type="button">Retirer</button>
                            </div>
                        </div>
                    </template>
                    <div class="form-actions">
                        <a class="page-button secondary" href="${pageContext.request.contextPath}/achats">Annuler</a>
                        <c:choose>
                            <c:when test="${empty fournisseurs or empty ingredientsActifs}"><button class="page-button" type="submit" disabled>Enregistrer la facture</button></c:when>
                            <c:otherwise><button class="page-button" type="submit">Enregistrer la facture</button></c:otherwise>
                        </c:choose>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
<script>
    (function () {
        const container = document.getElementById("lignes-achat");
        const template = document.getElementById("modele-ligne");
        document.getElementById("ajouter-ligne").addEventListener("click", function () {
            container.appendChild(template.content.cloneNode(true));
        });
        container.addEventListener("click", function (event) {
            if (event.target.classList.contains("retirer-ligne")) {
                const lignes = container.querySelectorAll(".achat-line");
                if (lignes.length > 1) event.target.closest(".achat-line").remove();
            }
        });
        container.addEventListener("change", function (event) {
            if (event.target.name === "ingredientId") {
                const unite = event.target.selectedOptions[0].dataset.unite;
                const champUnite = event.target.closest(".achat-line").querySelector('[name="unite"]');
                if (!champUnite.value && unite) champUnite.value = unite;
            }
        });
    }());
</script>
</body>
</html>
