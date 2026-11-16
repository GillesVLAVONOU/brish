<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>Calcul des coûts | Brish</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="module-page">
<div class="module-shell">
    <%@ include file="/views/fragments/sidebar.jspf" %>

    <main class="module-content">
        <header class="module-header">
            <div class="module-breadcrumb"><span>${nomRestaurantHtml}</span><span aria-hidden="true">/</span><strong>Calcul des coûts</strong></div>
            <div class="module-header-actions"><button class="page-button secondary" type="button">Retour</button></div>
        </header>
        <div class="module-body section-grid">
            <section class="page-card info-panel">
                <h3>Calcul de production</h3>
                <form action="${pageContext.request.contextPath}/couts/calcul" method="post">
                    <div class="form-grid">
                        <div class="field"><label>Production</label><select><option>Attiéké poisson braisé</option><option>Pâte rouge au poulet</option><option>Amiwo sauce tomate</option></select></div>
                        <div class="field"><label>Quantité</label><input type="number" value="42"></div>
                        <div class="field"><label>Main d’œuvre</label><input type="text" value="24 000 ${devise}"></div>
                        <div class="field"><label>Énergie</label><input type="text" value="5 400 ${devise}"></div>
                    </div>
                    <div class="form-actions">
                        <button class="page-button secondary" type="reset">Réinitialiser</button>
                        <button class="page-button" type="submit">Calculer</button>
                    </div>
                </form>
            </section>
            <aside class="page-card info-panel">
                <h3>Résultat</h3>
                <div class="kpi-stack">
                    <div class="kpi-box"><strong>38 600 ${devise}</strong><span>Coût total</span></div>
                    <div class="kpi-box"><strong>919 ${devise}</strong><span>Coût / portion</span></div>
                    <div class="kpi-box"><strong>67,5 %</strong><span>Marge estimée</span></div>
                </div>
            </aside>
        </div>
    </main>
</div>
</body>
</html>
