<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Administration des utilisateurs | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>
    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Administration</strong></div>
            <div class="module-header-actions">
                <form class="module-header-actions" action="${pageContext.request.contextPath}/utilisateurs" method="get">
                    <input class="page-search" type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Nom ou e-mail..." aria-label="Rechercher un utilisateur">
                    <button class="page-button secondary" type="submit">Rechercher</button>
                </form>
                <a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs?action=droits-role&amp;role=GESTIONNAIRE">Autorisations des rôles</a>
                <a class="page-button" href="${pageContext.request.contextPath}/utilisateurs?action=nouveau">+ Ajouter un utilisateur</a>
            </div>
        </header>
        <div class="module-body">
            <c:if test="${not empty message}"><div class="form-success" role="status"><c:out value="${message}"/></div></c:if>
            <c:if test="${not empty erreurs}"><div class="form-alert" role="alert"><ul><c:forEach items="${erreurs}" var="erreur"><li><c:out value="${erreur}"/></li></c:forEach></ul></div></c:if>
            <section class="stat-grid">
                <article class="stat-card"><span class="stat-label">COMPTES</span><strong class="stat-value"><c:out value="${nombreUtilisateurs}"/></strong><span class="stat-trend trend-neutral">tous statuts</span></article>
                <article class="stat-card"><span class="stat-label">ACTIFS</span><strong class="stat-value"><c:out value="${nombreActifs}"/></strong><span class="stat-trend trend-neutral">accès autorisés</span></article>
                <article class="stat-card"><span class="stat-label">ADMINISTRATEURS ACTIFS</span><strong class="stat-value"><c:out value="${nombreAdministrateurs}"/></strong><span class="stat-trend trend-neutral">administration</span></article>
                <article class="stat-card"><span class="stat-label">GESTIONNAIRE / CUISINIERS ACTIFS</span><strong class="stat-value"><c:out value="${nombreGerants}"/> / <c:out value="${nombreCuisiniers}"/></strong><span class="stat-trend trend-neutral">comptes opérationnels</span></article>
            </section>
            <section class="page-card">
                <div class="page-toolbar"><h2>Utilisateurs du système</h2><span class="muted">Les comptes sont désactivés plutôt que supprimés pour préserver l'historique.</span></div>
                <div class="table-wrap">
                    <table class="list-table users-table">
                        <thead><tr><th>NOM</th><th>E-MAIL</th><th>RÔLE</th><th>STATUT</th><th>ACTIONS</th></tr></thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty utilisateurs}"><tr><td colspan="5">Aucun utilisateur trouvé.</td></tr></c:when>
                            <c:otherwise><c:forEach items="${utilisateurs}" var="utilisateur">
                                <tr>
                                    <td><strong><c:out value="${utilisateur.nom}"/></strong></td>
                                    <td><c:out value="${utilisateur.email}"/></td>
                                    <td><c:out value="${utilisateur.role.libelle}"/></td>
                                    <td><c:choose><c:when test="${utilisateur.actif}"><span class="row-badge bag-ok">Actif</span></c:when><c:otherwise><span class="row-badge bag-info">Désactivé</span></c:otherwise></c:choose></td>
                                    <td>
                                        <div class="table-actions">
                                        <a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs?action=modifier&amp;id=<c:out value='${utilisateur.id}'/>">Modifier</a>
                                        <c:if test="${utilisateur.role.name() != 'ADMINISTRATEUR'}"><a class="page-button secondary" href="${pageContext.request.contextPath}/utilisateurs?action=droits&amp;id=<c:out value='${utilisateur.id}'/>">Autorisations</a></c:if>
                                        <c:if test="${utilisateur.id != sessionScope.utilisateurId}">
                                            <form class="inline-form" action="${pageContext.request.contextPath}/utilisateurs" method="post">
                                                <input type="hidden" name="jetonCsrf" value="<c:out value='${jetonCsrf}'/>">
                                                <input type="hidden" name="id" value="<c:out value='${utilisateur.id}'/>">
                                                <c:choose>
                                                    <c:when test="${utilisateur.actif}"><button class="page-button secondary account-toggle-button" type="submit" name="action" value="desactiver" title="Désactiver ce compte" aria-label="Désactiver le compte de <c:out value='${utilisateur.nom}'/>"></button></c:when>
                                                    <c:otherwise><button class="page-button secondary account-toggle-button" type="submit" name="action" value="activer" title="Activer ce compte" aria-label="Activer le compte de <c:out value='${utilisateur.nom}'/>"></button></c:otherwise>
                                                </c:choose>
                                            </form>
                                        </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach></c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
