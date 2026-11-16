<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Autorisations par rôle | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/utilisateurs">Administration</a><span aria-hidden="true">/</span><strong>Autorisations par rôle</strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs">Retour à la liste</a></div>
        </header>
        <div class="module-body">
            <section class="page-card">
                <div class="page-toolbar">
                    <h2>Matrice des rôles</h2>
                    <form action="${pageContext.request.contextPath}/utilisateurs" method="get">
                        <input type="hidden" name="action" value="droits-role">
                        <label for="role">Rôle à configurer</label>
                        <select id="role" name="role" onchange="this.form.submit()">
                            <c:forEach items="${rolesModifiables}" var="roleOption">
                                <option value="<c:out value='${roleOption.name()}'/>" ${role == roleOption ? 'selected' : ''}><c:out value="${roleOption.libelle}"/></option>
                            </c:forEach>
                        </select>
                    </form>
                </div>
                <p class="muted authorization-description">Ces droits s’appliquent aux nouveaux comptes du rôle et aux comptes qui héritent de la matrice. Une autorisation de modification inclut la consultation. Les droits Administrateur restent complets et ne sont pas modifiables.</p>
                <c:if test="${not empty message}"><div class="form-success" role="status"><c:out value="${message}"/></div></c:if>
                <c:if test="${not empty erreurs}"><div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div></c:if>
                <form action="${pageContext.request.contextPath}/utilisateurs" method="post">
                    <input type="hidden" name="action" value="enregistrerDroitsRole">
                    <input type="hidden" name="role" value="<c:out value='${role.name()}'/>">
                    <input type="hidden" name="jetonCsrf" value="<c:out value='${jetonCsrf}'/>">
                    <div class="table-wrap">
                        <table class="list-table">
                            <thead><tr><th>MODULE</th><th>CONSULTATION</th><th>CRÉATION / MODIFICATION</th></tr></thead>
                            <tbody>
                            <c:forEach items="${droits}" var="droit">
                                <tr>
                                    <td><strong><c:out value="${droit.module.libelle}"/></strong></td>
                                    <td><label><input type="checkbox" name="consultation_<c:out value='${droit.module.name()}'/>" value="oui" ${droit.consultationRole ? 'checked' : ''}> Autorisée</label></td>
                                    <td><label><input type="checkbox" name="modification_<c:out value='${droit.module.name()}'/>" value="oui" ${droit.modificationRole ? 'checked' : ''}> Autorisée</label></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="form-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs">Annuler</a><button class="page-button" type="submit">Enregistrer la matrice</button></div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
