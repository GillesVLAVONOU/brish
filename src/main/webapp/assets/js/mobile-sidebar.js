(() => {
    const sidebar = document.querySelector(".sidebar");
    const toggle = document.querySelector(".mobile-menu-toggle");
    const backdrop = document.querySelector(".sidebar-backdrop");

    if (!sidebar || !toggle || !backdrop) {
        return;
    }

    const closeMenu = () => {
        document.body.classList.remove("sidebar-open");
        toggle.setAttribute("aria-expanded", "false");
        toggle.setAttribute("aria-label", "Ouvrir le menu");
    };

    toggle.addEventListener("click", () => {
        const isOpen = document.body.classList.toggle("sidebar-open");
        toggle.setAttribute("aria-expanded", String(isOpen));
        toggle.setAttribute("aria-label", isOpen ? "Fermer le menu" : "Ouvrir le menu");

        if (isOpen) {
            sidebar.querySelector("a.nav-item.active")?.focus();
        }
    });

    backdrop.addEventListener("click", closeMenu);
    sidebar.addEventListener("click", (event) => {
        if (event.target.closest("a")) {
            closeMenu();
        }
    });

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape" && document.body.classList.contains("sidebar-open")) {
            closeMenu();
            toggle.focus();
        }
    });
})();
