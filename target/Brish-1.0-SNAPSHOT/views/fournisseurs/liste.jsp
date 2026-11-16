<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Fournisseurs | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>

    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Fournisseurs</strong></div>
            <div class="module-header-actions">
                <form class="module-header-actions" action="${pageContext.request.contextPath}/fournisseurs" method="get">
                    <input class="page-search" type="search" name="recherche" value="<c:out value='${recherche}'/>" placeholder="Rechercher..." aria-label="Rechercher un fournisseur">
                    <button class="page-button secondary" type="submit">Rechercher</button>
                </form>
                <a class="page-button" href="${pageContext.request.contextPath}/fournisseurs?action=nouveau">+ Nouveau fournisseur</a>
            </div>
        </header>
        <div class="module-body">
            <section class="stat-grid">
                <article class="stat-card">
                    <span class="stat-label">FOURNISSEURS RÉFÉRENCÉS</span>
                    <strong class="stat-value"><c:out value="${nombreFournisseurs}"/></strong>
                    <span class="stat-trend trend-neutral">Dans le catalogue</span>
                </article>
            </section>
            <section class="page-card">
                <div class="page-toolbar">
                    <h2>Partenaires fournisseurs</h2>
                    <span class="muted"><c:out value="${nombreFournisseurs}"/> fournisseur(s)</span>
                </div>
                <div class="table-wrap">
                    <table class="list-table">
                        <thead>
                        <tr><th>FOURNISSEUR</th><th>TÉLÉPHONE</th><th>E-MAIL</th><th>ADRESSE</th><th>ACTIONS</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${fournisseurs}" var="fournisseur">
                            <tr>
                                <td><strong><c:out value="${fournisseur.nom}"/></strong></td>
                                <td><c:out value="${fournisseur.telephone}" default="—"/></td>
                                <td><c:out value="${fournisseur.email}" default="—"/></td>
                                <td><c:out value="${fournisseur.adresse}" default="—"/></td>
                                <td><a class="page-button secondary" href="${pageContext.request.contextPath}/fournisseurs?action=modifier&amp;id=${fournisseur.id}">Modifier</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty fournisseurs}">
                            <tr><td colspan="5" class="muted">Aucun fournisseur trouvé.</td></tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
