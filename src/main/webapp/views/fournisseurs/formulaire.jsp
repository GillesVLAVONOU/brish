<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><c:choose><c:when test="${fournisseur.id == null}">Nouveau fournisseur</c:when><c:otherwise>Modifier le fournisseur</c:otherwise></c:choose> | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb">
                <a href="${pageContext.request.contextPath}/fournisseurs">Fournisseurs</a>
                <span aria-hidden="true">/</span>
                <strong><c:choose><c:when test="${fournisseur.id == null}">Nouveau fournisseur</c:when><c:otherwise>Modifier le fournisseur</c:otherwise></c:choose></strong>
            </div>
            <div class="module-header-actions">
                <a class="page-button secondary" href="${pageContext.request.contextPath}/fournisseurs">Retour à la liste</a>
            </div>
        </header>
        <div class="module-body">
            <section class="page-card info-panel">
                <div class="page-toolbar">
                    <h2><c:choose><c:when test="${fournisseur.id == null}">Nouveau fournisseur</c:when><c:otherwise>Modifier le fournisseur</c:otherwise></c:choose></h2>
                </div>
                <c:if test="${not empty erreurs}">
                    <div class="form-alert" role="alert">
                        <ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul>
                    </div>
                </c:if>
                <form action="${pageContext.request.contextPath}/fournisseurs" method="post" class="ingredient-form">
                    <input type="hidden" name="action" value="enregistrer">
                    <c:if test="${fournisseur.id != null}"><input type="hidden" name="id" value="<c:out value='${fournisseur.id}'/>"></c:if>
                    <div class="form-grid">
                        <div class="field">
                            <label for="nom">Nom *</label>
                            <input id="nom" name="nom" maxlength="150" required value="<c:out value='${fournisseur.nom}'/>">
                        </div>
                        <div class="field">
                            <label for="telephone">Téléphone</label>
                            <input id="telephone" name="telephone" maxlength="30" value="<c:out value='${fournisseur.telephone}'/>">
                        </div>
                        <div class="field">
                            <label for="email">E-mail</label>
                            <input id="email" name="email" type="email" maxlength="150" value="<c:out value='${fournisseur.email}'/>">
                        </div>
                        <div class="field">
                            <label for="adresse">Adresse</label>
                            <input id="adresse" name="adresse" maxlength="255" value="<c:out value='${fournisseur.adresse}'/>">
                        </div>
                    </div>
                    <div class="form-actions">
                        <a class="page-button secondary" href="${pageContext.request.contextPath}/fournisseurs">Annuler</a>
                        <button class="page-button" type="submit">Enregistrer</button>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
