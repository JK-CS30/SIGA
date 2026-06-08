document.addEventListener("DOMContentLoaded", () => {

    // Resaltar menú activo
    const links = document.querySelectorAll(".sidebar nav a");

    links.forEach(link => {
        link.addEventListener("click", () => {

            links.forEach(l => l.classList.remove("active"));

            link.classList.add("active");
        });
    });

});