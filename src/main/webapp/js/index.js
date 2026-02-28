// ============================================================
// CONFIGURACIÓN
// ============================================================
const API_BASE = 'http://localhost:8080/sistema-reservas/api';

// ============================================================
// AUTH HELPERS
// ============================================================
function getToken()        { return sessionStorage.getItem('token'); }
function getUsuario()      { return JSON.parse(sessionStorage.getItem('usuario') || 'null'); }
function isLoggedIn()      { return !!getToken(); }

function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = 'login.html';
    }
}

function logout() {
    const token = getToken();
    if (token) {
        fetch(`${API_BASE}/auth/logout`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        }).finally(() => {
            sessionStorage.clear();
            window.location.href = 'login.html';
        });
    } else {
        sessionStorage.clear();
        window.location.href = 'login.html';
    }
}

// ============================================================
// FETCH WRAPPER
// ============================================================
async function apiFetch(path, options = {}) {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
    const json = await res.json();

    if (!res.ok || json.success === false) {
        throw new Error(json.message || `Error ${res.status}`);
    }
    return json; // { success, message, data }
}

// ============================================================
// SIDEBAR ACTIVO
// ============================================================
function initSidebar() {
    const currentPage = window.location.pathname.split('/').pop();
    document.querySelectorAll('.sidebar .side-menu li a:not(.logout)').forEach(link => {
        link.parentElement.classList.toggle('active', link.getAttribute('href') === currentPage);
    });

    const menuBar = document.querySelector('.content header .bx.bx-menu');
    const sideBar = document.querySelector('.sidebar');
    if (menuBar && sideBar) {
        menuBar.addEventListener('click', () => sideBar.classList.toggle('close'));
    }

    // Nombre usuario en header
    const usuario = getUsuario();
    const nameEl = document.querySelector('.notif.cambio');
    if (nameEl && usuario) nameEl.textContent = usuario.nombreCompleto;

    // Logout
    document.querySelectorAll('a.logout').forEach(a => {
        a.addEventListener('click', e => { e.preventDefault(); logout(); });
    });
}

// ============================================================
// MODAL HELPERS
// ============================================================
function openModal(id)  { document.getElementById(id).style.display = 'flex'; }
function closeModal(id) { document.getElementById(id).style.display = 'none'; }

function showToast(msg, type = 'success') {
    let toast = document.getElementById('toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast';
        document.body.appendChild(toast);
    }
    toast.textContent = msg;
    toast.className = `toast toast-${type} show`;
    clearTimeout(toast._t);
    toast._t = setTimeout(() => toast.classList.remove('show'), 3500);
}

function confirmDialog(msg) {
    return window.confirm(msg);
}

// ============================================================
// ESTADO BADGE
// ============================================================
function estadoBadge(estado) {
    const map = {
        'Pendiente':  'pending',
        'Confirmado': 'completed',
        'Finalizado': 'pending2',
        'Cancelado':  'delete-action'
    };
    return `<span class="status ${map[estado] || ''}">${estado}</span>`;
}

// ============================================================
// INIT GLOBAL
// ============================================================
document.addEventListener('DOMContentLoaded', () => {
    const page = window.location.pathname.split('/').pop();
    if (page !== 'login.html') requireAuth();
    if (page !== 'login.html') initSidebar();
});
