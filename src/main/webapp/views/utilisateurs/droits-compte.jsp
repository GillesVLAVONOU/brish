<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Autorisations du compte | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><a href="${pageContext.request.contextPath}/utilisateurs">Administration</a><span aria-hidden="true">/</span><strong>Autorisations du compte</strong></div>
            <div class="module-header-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs">Retour à la liste</a></div>
        </header>
        <div class="module-body">
            <section class="page-card">
                <div class="page-toolbar"><h2><c:out value="${utilisateur.nom}"/> — <c:out value="${utilisateur.role.libelle}"/></h2></div>
                <c:if test="${not empty message}"><div class="form-success" role="status"><c:out value="${message}"/></div></c:if>
                <c:if test="${not empty erreurs}"><div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div></c:if>
                <c:choose>
                    <c:when test="${utilisateur.role.name() == 'ADMINISTRATEUR'}">
                        <p class="muted">Les administrateurs disposent toujours de tous les droits. Cette règle de sécurité ne peut pas être remplacée par une dérogation individuelle.</p>
                    </c:when>
                    <c:otherwise>
                        <p class="muted">« Hériter » reprend la matrice du rôle. Une modification autorisée donne également accès à la consultation du module.</p>
                        <form action="${pageContext.request.contextPath}/utilisateurs" method="post">
                            <input type="hidden" name="action" value="enregistrerDroitsCompte">
                            <input type="hidden" name="id" value="<c:out value='${utilisateur.id}'/>">
                            <input type="hidden" name="jetonCsrf" value="<c:out value='${jetonCsrf}'/>">
                            <div class="table-wrap">
                                <table class="list-table">
                                    <thead><tr><th>MODULE</th><th>DROITS DU RÔLE</th><th>CONSULTATION DU COMPTE</th><th>MODIFICATION DU COMPTE</th><th>ACCÈS EFFECTIF</th></tr></thead>
                                    <tbody>
                                    <c:forEach items="${droits}" var="droit">
                                        <tr>
                                            <td><strong><c:out value="${droit.module.libelle}"/></strong></td>
                                            <td><c:choose><c:when test="${droit.consultationRole}">Consultation</c:when><c:otherwise>Aucun</c:otherwise></c:choose><c:if test="${droit.modificationRole}"> + modification</c:if></td>
                                            <td><label class="sr-only" for="consultation_<c:out value='${droit.module.name()}'/>">Consultation — <c:out value="${droit.module.libelle}"/></label><select id="consultation_<c:out value='${droit.module.name()}'/>" name="consultation_<c:out value='${droit.module.name()}'/>"><option value="HERITER" ${droit.consultationPersonnalisee == 'HERITER' ? 'selected' : ''}>Hériter</option><option value="AUTORISER" ${droit.consultationPersonnalisee == 'AUTORISER' ? 'selected' : ''}>Autoriser</option><option value="REFUSER" ${droit.consultationPersonnalisee == 'REFUSER' ? 'selected' : ''}>Refuser</option></select></td>
                                            <td><label class="sr-only" for="modification_<c:out value='${droit.module.name()}'/>">Modification — <c:out value="${droit.module.libelle}"/></label><select id="modification_<c:out value='${droit.module.name()}'/>" name="modification_<c:out value='${droit.module.name()}'/>"><option value="HERITER" ${droit.modificationPersonnalisee == 'HERITER' ? 'selected' : ''}>Hériter</option><option value="AUTORISER" ${droit.modificationPersonnalisee == 'AUTORISER' ? 'selected' : ''}>Autoriser</option><option value="REFUSER" ${droit.modificationPersonnalisee == 'REFUSER' ? 'selected' : ''}>Refuser</option></select></td>
                                            <td><c:choose><c:when test="${droit.consultationEffective}">Consultation</c:when><c:otherwise>Aucun</c:otherwise></c:choose><c:if test="${droit.modificationEffective}"> + modification</c:if></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="form-actions"><a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs">Annuler</a><button class="page-button" type="submit">Enregistrer les autorisations</button></div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>
