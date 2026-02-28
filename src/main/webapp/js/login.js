const API_BASE = 'http://localhost:8080/sistema-reservas/api';

        // Si ya está logueado, redirigir
        if (sessionStorage.getItem('token')) {
            window.location.href = 'home.html';
        }

        async function doLogin() {
            const nombreUsuario = document.getElementById('username').value.trim();
            const contrasena    = document.getElementById('password').value;
            const errorMsg      = document.getElementById('errorMsg');
            const btn           = document.getElementById('btnLogin');

            errorMsg.textContent = '';
            if (!nombreUsuario || !contrasena) {
                errorMsg.textContent = 'Completa todos los campos.';
                return;
            }

            btn.disabled = true;
            btn.textContent = 'Ingresando...';

            try {
                const res = await fetch(`${API_BASE}/auth/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ nombreUsuario, contrasena })
                });
                const json = await res.json();

                if (!res.ok || !json.success) {
                    throw new Error(json.message || 'Credenciales incorrectas');
                }

                const { token, nombreCompleto, rol } = json.data;
                sessionStorage.setItem('token', token);
                sessionStorage.setItem('usuario', JSON.stringify({ nombreCompleto, rol }));
                window.location.href = 'home.html';

            } catch (err) {
                errorMsg.textContent = err.message;
                btn.disabled = false;
                btn.textContent = 'Ingresar';
            }
        }

        document.getElementById('btnLogin').addEventListener('click', doLogin);
        document.addEventListener('keydown', e => { if (e.key === 'Enter') doLogin(); });