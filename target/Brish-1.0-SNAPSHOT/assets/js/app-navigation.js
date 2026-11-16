(() => {
    const mainContent = document.querySelector(".main-content");
    const sidebar = document.querySelector(".sidebar");

    if (!mainContent || !sidebar) {
        return;
    }

    const appRoot = new URL(sidebar.querySelector("a[href]").href).pathname.replace(/\/[^/]*\/?$/, "");

    const updateActiveLink = (nextDocument) => {
        const nextLinks = Array.from(nextDocument.querySelectorAll(".sidebar a.nav-item"));

        sidebar.querySelectorAll("a.nav-item").forEach((link) => {
            const nextLink = nextLinks.find((candidate) => candidate.href === link.href);
            const isActive = Boolean(nextLink && nextLink.classList.contains("active"));
            link.classList.toggle("active", isActive);

            if (isActive) {
                link.setAttribute("aria-current", "page");
            } else {
                link.removeAttribute("aria-current");
            }
        });
    };

    const loadPage = async (url, addHistory = true, requestOptions = {}) => {
        mainContent.setAttribute("aria-busy", "true");

        try {
            const response = await fetch(url, {
                headers: { "X-Requested-With": "XMLHttpRequest", ...requestOptions.headers },
                ...requestOptions
            });
            const html = await response.text();
            const nextDocument = new DOMParser().parseFromString(html, "text/html");
            const nextContent = nextDocument.querySelector(".main-content, .module-content");

            if (!response.ok || !nextContent) {
                window.location.assign(url);
                return;
            }

            mainContent.replaceChildren(...Array.from(nextContent.childNodes, (node) => document.importNode(node, true)));
            updateActiveLink(nextDocument);
            document.title = nextDocument.title || document.title;

            if (addHistory) {
                window.history.pushState({}, "", response.url || url);
            }
        } catch (error) {
            window.location.assign(url);
        } finally {
            mainContent.removeAttribute("aria-busy");
        }
    };

    document.addEventListener("click", (event) => {
        const link = event.target.closest("a[href]");

        if (!link || event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
            return;
        }

        const url = new URL(link.href, window.location.href);
        const isAppPage = url.origin === window.location.origin
            && (url.pathname === appRoot || url.pathname.startsWith(`${appRoot}/`))
            && !/\/(login|logout)$/.test(url.pathname)
            && !link.hasAttribute("download")
            && (!link.target || link.target === "_self");

        if (isAppPage && (link.closest(".sidebar") || link.closest(".main-content"))) {
            event.preventDefault();
            loadPage(url.href);
        }
    });

    document.addEventListener("submit", (event) => {
        const form = event.target;

        if (!(form instanceof HTMLFormElement) || !form.matches("form[data-app-navigation]")) {
            return;
        }

        const url = new URL(form.getAttribute("action") || window.location.href, window.location.href);
        const formData = new FormData(form);
        event.preventDefault();

        if (form.method.toLowerCase() === "get") {
            formData.forEach((value, key) => {
                if (typeof value === "string" && value.trim()) {
                    url.searchParams.set(key, value.trim());
                } else {
                    url.searchParams.delete(key);
                }
            });
            loadPage(url.href);
            return;
        }

        const body = new URLSearchParams();
        formData.forEach((value, key) => {
            if (typeof value === "string") body.append(key, value);
        });
        loadPage(url.href, true, {
            method: form.method.toUpperCase(),
            headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
            body
        });
    });

    window.addEventListener("popstate", () => loadPage(window.location.href, false));
})();
