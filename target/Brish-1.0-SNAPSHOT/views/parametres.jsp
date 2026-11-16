<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Paramètres | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a><span aria-hidden="true">/</span><strong>Paramètres</strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/dashboard">Retour</a></div>
        </header>
        <div class="module-body">
            <section class="page-card">
                <div class="page-toolbar">
                    <h2>Profil du restaurant</h2>
                    <span class="muted">Ces informations sont partagées dans l'application.</span>
                </div>
                <c:if test="${enregistre}"><div class="form-success" role="status">Les paramètres ont été enregistrés.</div></c:if>
                <c:if test="${not empty erreurs}"><div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div></c:if>
                <form action="${pageContext.request.contextPath}/parametres" method="post" class="ingredient-form settings-form">
                    <input type="hidden" name="action" value="enregistrer">
                    <div class="form-grid">
                        <div class="field">
                            <label for="nomRestaurant">Nom du restaurant *</label>
                            <input id="nomRestaurant" name="nomRestaurant" maxlength="120" required value="<c:out value='${parametres.nomRestaurant}'/>">
                        </div>
                        <div class="field">
                            <label for="adresse">Adresse / ville</label>
                            <input id="adresse" name="adresse" maxlength="255" value="<c:out value='${parametres.adresse}'/>">
                        </div>
                        <div class="field">
                            <label for="telephone">Téléphone</label>
                            <input id="telephone" name="telephone" type="tel" maxlength="30" value="<c:out value='${parametres.telephone}'/>">
                        </div>
                        <div class="field">
                            <label for="codeDevise">Devise *</label>
                            <select id="codeDevise" name="codeDevise" ${deviseModifiable ? '' : 'disabled'}>
                                <option value="XOF" ${parametres.codeDevise == 'XOF' ? 'selected' : ''}>XOF — Franc CFA BCEAO (FCFA)</option>
                                <option value="XAF" ${parametres.codeDevise == 'XAF' ? 'selected' : ''}>XAF — Franc CFA BEAC</option>
                                <option value="NGN" ${parametres.codeDevise == 'NGN' ? 'selected' : ''}>NGN — Naira nigérian</option>
                                <option value="GHS" ${parametres.codeDevise == 'GHS' ? 'selected' : ''}>GHS — Cedi ghanéen</option>
                                <option value="EUR" ${parametres.codeDevise == 'EUR' ? 'selected' : ''}>EUR — Euro</option>
                                <option value="USD" ${parametres.codeDevise == 'USD' ? 'selected' : ''}>USD — Dollar américain</option>
                            </select>
                            <c:if test="${not deviseModifiable}"><small>La devise est verrouillée car des prix, achats ou coûts existent déjà. Une conversion explicite sera nécessaire pour la modifier.</small></c:if>
                        </div>
                    </div>
                    <div class="form-actions">
                        <a class="page-button secondary" href="${pageContext.request.contextPath}/dashboard">Annuler</a>
                        <button class="page-button" type="submit">Enregistrer les paramètres</button>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
