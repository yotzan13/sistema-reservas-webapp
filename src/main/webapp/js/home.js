document.addEventListener('DOMContentLoaded', async () => {
        const hoy = new Date().toISOString().split('T')[0];

        try {
            // Reservas de hoy
            const rHoy = await apiFetch(`/reservas?fecha=${hoy}`);
            const reservasHoy = rHoy.data || [];

            document.getElementById('cTotal').textContent      = reservasHoy.length;
            document.getElementById('cPendientes').textContent = reservasHoy.filter(r => r.estado === 'Pendiente').length;
            document.getElementById('cConfirmadas').textContent= reservasHoy.filter(r => r.estado === 'Confirmado').length;

            // Clientes
            const rCli = await apiFetch('/clientes');
            document.getElementById('cClientes').textContent = (rCli.data || []).length;

            // Tabla pendientes (todas, no solo hoy)
            const rAll = await apiFetch('/reservas');
            const pendientes = (rAll.data || []).filter(r => r.estado === 'Pendiente');
            const tbody = document.getElementById('tbodyPendientes');

            if (pendientes.length === 0) {
                tbody.innerHTML = `<tr><td colspan="8" style="text-align:center;color:var(--dark-grey);padding:24px">Sin reservas pendientes</td></tr>`;
            } else {
                tbody.innerHTML = pendientes.map(r => `
                    <tr>
                        <td><span>${r.id}</span></td>
                        <td>${r.nombreCliente} ${r.apellidoCliente}</td>
                        <td>${r.fecha}</td>
                        <td>${r.hora?.substring(0,5)}</td>
                        <td>${r.numeroMesa}</td>
                        <td>${r.cantidad}</td>
                        <td>${r.ubicacion}</td>
                        <td>${estadoBadge(r.estado)}</td>
                    </tr>
                `).join('');
            }

        } catch (err) {
            console.error(err);
            document.getElementById('tbodyPendientes').innerHTML =
                `<tr><td colspan="8" style="text-align:center;color:var(--danger);padding:24px">Error al cargar datos</td></tr>`;
        }
    });