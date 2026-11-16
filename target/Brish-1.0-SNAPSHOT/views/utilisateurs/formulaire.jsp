<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><c:choose><c:when test="${empty utilisateur.id}">Créer un utilisateur</c:when><c:otherwise>Modifier un utilisateur</c:otherwise></c:choose> | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/utilisateurs">Administration</a><span aria-hidden="true">/</span><strong><c:choose><c:when test="${empty utilisateur.id}">Nouvel utilisateur</c:when><c:otherwise>Modifier l'utilisateur</c:otherwise></c:choose></strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs">Retour à la liste</a></div>
        </header>
        <div class="module-body">
            <section class="page-card">
                <div class="page-toolbar"><h2><c:choose><c:when test="${empty utilisateur.id}">Créer un compte</c:when><c:otherwise>Modifier le compte</c:otherwise></c:choose></h2><span class="muted">Le mot de passe initial doit comporter au moins 12 caractères. Communiquez-le séparément à l'utilisateur.</span></div>
                <p class="muted user-form-description">Les nouveaux comptes héritent des autorisations de leur rôle. Vous pourrez régler leurs exceptions depuis la liste des utilisateurs.</p>
                <c:if test="${not empty erreurs}"><div class="form-alert user-form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div></c:if>
                <form action="${pageContext.request.contextPath}/utilisateurs" method="post" class="ingredient-form user-form">
                    <input type="hidden" name="action" value="enregistrer">
                    <input type="hidden" name="jetonCsrf" value="<c:out value='${jetonCsrf}'/>">
                    <c:if test="${not empty utilisateur.id}"><input type="hidden" name="id" value="<c:out value='${utilisateur.id}'/>"></c:if>
                    <div class="form-grid">
                        <div class="field"><label for="nom">Nom complet *</label><input id="nom" name="nom" maxlength="100" required value="<c:out value='${utilisateur.nom}'/>"></div>
                        <div class="field"><label for="email">Adresse e-mail *</label><input id="email" name="email" type="email" maxlength="150" required value="<c:out value='${utilisateur.email}'/>"></div>
                        <div class="field">
                            <label for="role">Rôle *</label>
                            <c:choose>
                                <c:when test="${utilisateur.id == sessionScope.utilisateurId}">
                                    <input type="hidden" name="role" value="ADMINISTRATEUR">
                                    <input id="role" value="Administrateur" readonly>
                                    <small>Votre propre rôle administrateur ne peut pas être modifié.</small>
                                </c:when>
                                <c:otherwise><select id="role" name="role" required>
                                    <option value="">Sélectionner un rôle</option>
                                    <c:forEach items="${roles}" var="role"><option value="<c:out value='${role.name()}'/>" ${utilisateur.role == role ? 'selected' : ''}><c:out value="${role.libelle}"/></option></c:forEach>
                                </select></c:otherwise>
                            </c:choose>
                        </div>
                        <div class="field"><label for="motDePasse"><c:choose><c:when test="${empty utilisateur.id}">Mot de passe provisoire *</c:when><c:otherwise>Nouveau mot de passe provisoire</c:otherwise></c:choose></label><input id="motDePasse" name="motDePasse" type="password" minlength="12" autocomplete="new-password" ${empty utilisateur.id ? 'required' : ''}><c:if test="${not empty utilisateur.id}"><small>Laissez vide pour conserver le mot de passe actuel.</small></c:if></div>
                        <div class="field"><label for="confirmationMotDePasse"><c:choose><c:when test="${empty utilisateur.id}">Confirmer le mot de passe *</c:when><c:otherwise>Confirmer le nouveau mot de passe</c:otherwise></c:choose></label><input id="confirmationMotDePasse" name="confirmationMotDePasse" type="password" minlength="12" autocomplete="new-password" ${empty utilisateur.id ? 'required' : ''}></div>
                    </div>
                    <div class="form-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs">Annuler</a><button class="page-button" type="submit">Enregistrer l'utilisateur</button></div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
