
function toggleSidebar() {
    const body = document.body;
    body.classList.toggle('sidebar-collapsed');
}

function toggleDetails(index) {
            const detailRow = document.getElementById('detail-' + index);
            const icon = document.getElementById('icon-' + index);
            
            if (detailRow.classList.contains('show')) {
                detailRow.classList.remove('show');
                icon.classList.replace('fa-solid fa-chevron-up', 'fa-solid fa-chevron-down');
            } else {
                detailRow.classList.add('show');
                icon.classList.replace('fa-solid fa-chevron-down', 'fa-solid fa-chevron-up');
            }
        }