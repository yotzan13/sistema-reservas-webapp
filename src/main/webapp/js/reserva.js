let reservasCache = [];
    let reservaIdAccion = null;
    let nuevoEstadoAccion = null;
    let mesaSeleccionada = null;
    let clientesData = [];

    // ─────────────────────────────────────
    // CARGA Y RENDER DE TABLA
    // ─────────────────────────────────────
    async function cargarReservas(fecha = null) {
        try {
            const url = fecha ? `/reservas/fecha/${fecha}` : '/reservas';
            const res = await apiFetch(url);
            reservasCache = res.data || [];
            renderTabla(reservasCache);
        } catch (err) {
            document.getElementById('tbodyReservas').innerHTML =
                `<tr><td colspan="10" style="text-align:center;color:var(--danger);padding:24px">${err.message}</td></tr>`;
        }
    }

    function renderTabla(lista) {
        const tbody = document.getElementById('tbodyReservas');
        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="10" style="text-align:center;padding:24px;color:var(--dark-grey)">Sin reservas</td></tr>`;
            return;
        }

        tbody.innerHTML = lista.map(r => {
            const transiciones = {
                'Pendiente':  [['Confirmado','bxs-check-circle verde','Confirmar'],['Cancelado','bxs-trash rojo','Cancelar']],
                'Confirmado': [['Finalizado','bxs-check-square','Finalizar'],['Cancelado','bxs-trash rojo','Cancelar']],
                'Finalizado': [],
                'Cancelado':  []
            };
            const botones = (transiciones[r.estado] || []).map(([estado, icon, title]) =>
                `<a class="bx ${icon}" style="cursor:pointer" title="${title}" onclick="abrirModalEstado(${r.id},'${estado}')"></a>`
            ).join(' ') || '<span style="color:var(--dark-grey);font-size:12px">—</span>';

            return `
            <tr>
                <td><span>${r.id}</span></td>
                <td>${r.nombreCliente} ${r.apellidoCliente}</td>
                <td>${r.fecha}</td>
                <td>${r.hora?.substring(0,5)}</td>
                <td>${r.numeroMesa}</td>
                <td>${r.cantidad}</td>
                <td>${r.ubicacion}</td>
                <td>${estadoBadge(r.estado)}</td>
                <td><a class="bx bxs-info-circle" style="color:#3f912e;cursor:pointer" title="Ver Detalles" onclick="verDetalle(${r.id})"></a></td>
                <td>${botones}</td>
            </tr>`;
        }).join('');
    }

    function filtrarPorFecha() {
        const f = document.getElementById('filtroFecha').value;
        if (!f) { showToast('Selecciona una fecha', 'error'); return; }
        cargarReservas(f);
    }

    function limpiarFiltro() {
        document.getElementById('filtroFecha').value = '';
        cargarReservas();
    }

    // ─────────────────────────────────────
    // MODAL DETALLE
    // ─────────────────────────────────────
    function verDetalle(id) {
        const r = reservasCache.find(x => x.id === id);
        if (!r) return;
        document.getElementById('detalleContent').innerHTML = `
            <div class="detalle-item"><span class="detalle-label">ID</span><span class="detalle-valor">${r.id}</span></div>
            <div class="detalle-item"><span class="detalle-label">Estado</span><span class="detalle-valor">${r.estado}</span></div>
            <div class="detalle-item"><span class="detalle-label">Cliente</span><span class="detalle-valor">${r.nombreCliente} ${r.apellidoCliente}</span></div>
            <div class="detalle-item"><span class="detalle-label">Teléfono</span><span class="detalle-valor">${r.telefonoCliente || '—'}</span></div>
            <div class="detalle-item"><span class="detalle-label">Correo</span><span class="detalle-valor">${r.correoCliente || '—'}</span></div>
            <div class="detalle-item"><span class="detalle-label">Fecha</span><span class="detalle-valor">${r.fecha}</span></div>
            <div class="detalle-item"><span class="detalle-label">Hora</span><span class="detalle-valor">${r.hora?.substring(0,5)}</span></div>
            <div class="detalle-item"><span class="detalle-label">Mesa</span><span class="detalle-valor">Mesa ${r.numeroMesa}</span></div>
            <div class="detalle-item"><span class="detalle-label">Ubicación</span><span class="detalle-valor">${r.ubicacion}</span></div>
            <div class="detalle-item"><span class="detalle-label">Personas</span><span class="detalle-valor">${r.cantidad}</span></div>
            <div class="detalle-item"><span class="detalle-label">Registrado por</span><span class="detalle-valor">${r.nombreUsuario}</span></div>
        `;
        openModal('modalDetalle');
    }

    // ─────────────────────────────────────
    // MODAL CAMBIO ESTADO
    // ─────────────────────────────────────
    function abrirModalEstado(id, nuevoEstado) {
        reservaIdAccion = id;
        nuevoEstadoAccion = nuevoEstado;

        const r = reservasCache.find(x => x.id === id);
        const config = {
            'Confirmado': { icon: 'bxs-check-circle', color: 'var(--success)', titulo: '¿Confirmar reserva?', btn: 'completed' },
            'Cancelado':  { icon: 'bxs-trash',        color: 'var(--danger)',  titulo: '¿Cancelar reserva?', btn: 'delete-action' },
            'Finalizado': { icon: 'bxs-check-square', color: 'var(--primary)', titulo: '¿Finalizar reserva?', btn: 'pending2' },
        };
        const cfg = config[nuevoEstado] || {};

        const iconEl  = document.getElementById('estadoIcon');
        iconEl.className = `bx ${cfg.icon}`;
        iconEl.style.color = cfg.color;

        document.getElementById('estadoTitulo').textContent = cfg.titulo;
        document.getElementById('estadoMsg').textContent =
            `Reserva #${r?.id} — ${r?.nombreCliente} ${r?.apellidoCliente} — Mesa ${r?.numeroMesa} — ${r?.fecha}`;

        const btn = document.getElementById('btnConfirmarEstado');
        btn.className = `status ${cfg.btn}`;
        btn.style.cssText = 'border:none;cursor:pointer;font-size:14px;font-weight:700;color:white';

        openModal('modalEstado');
    }

    async function confirmarCambioEstado() {
        const btn = document.getElementById('btnConfirmarEstado');
        btn.disabled = true;
        try {
            await apiFetch(`/reservas/${reservaIdAccion}/estado`, {
                method: 'PATCH',
                body: JSON.stringify({ estado: nuevoEstadoAccion })
            });
            showToast(`Reserva ${nuevoEstadoAccion.toLowerCase()} correctamente`);
            closeModal('modalEstado');
            await cargarReservas();
        } catch (err) {
            showToast(err.message, 'error');
        } finally {
            btn.disabled = false;
        }
    }

    // ─────────────────────────────────────
    // WIZARD NUEVA RESERVA
    // ─────────────────────────────────────
    function abrirModalNuevo() {
        // Reset wizard
        mesaSeleccionada = null;
        document.getElementById('rFecha').value    = '';
        document.getElementById('rHora').value     = '';
        document.getElementById('rCantidad').value = '';
        document.getElementById('rClienteId').value = '';
        document.getElementById('rBuscarCliente').value = '';
        document.getElementById('resultadosCliente').innerHTML = '';
        ['rNuevoNombre','rNuevoApellido','rNuevoDni','rNuevoTelefono','rNuevoCorreo'].forEach(id =>
            document.getElementById(id).value = '');
        ['eRFecha','eRHora','eRCantidad','eRMesa','eRCliente'].forEach(id =>
            document.getElementById(id).textContent = '');
        irPaso(1);
        openModal('modalNueva');
    }

    function irPaso(n) {
        [1,2,3].forEach(i => {
            document.getElementById(`step${i}`).classList.toggle('active', i === n);
            const ws = document.getElementById(`ws${i}`);
            ws.classList.remove('active','done');
            if (i === n) ws.classList.add('active');
            else if (i < n) ws.classList.add('done');
        });
    }

    async function irPaso2() {
        const fecha    = document.getElementById('rFecha').value;
        const hora     = document.getElementById('rHora').value;
        const cantidad = parseInt(document.getElementById('rCantidad').value);

        let ok = true;
        if (!fecha)        { document.getElementById('eRFecha').textContent = 'Selecciona una fecha'; ok = false; }
        else               { document.getElementById('eRFecha').textContent = ''; }
        if (!hora)         { document.getElementById('eRHora').textContent = 'Selecciona una hora'; ok = false; }
        else               { document.getElementById('eRHora').textContent = ''; }
        if (!cantidad || cantidad < 1) { document.getElementById('eRCantidad').textContent = 'Ingresa la cantidad de personas'; ok = false; }
        else               { document.getElementById('eRCantidad').textContent = ''; }
        if (!ok) return;

        // Validar anticipación 3 días
        const fechaSelec = new Date(fecha + 'T00:00:00');
        const limite = new Date();
        limite.setHours(0,0,0,0);
        limite.setDate(limite.getDate() + 3);
        if (fechaSelec < limite) {
            document.getElementById('eRFecha').textContent = 'La reserva requiere al menos 3 días de anticipación';
            return;
        }

        document.getElementById('listaMesas').innerHTML = '<p style="color:var(--dark-grey);font-size:13px">Buscando mesas...</p>';
        irPaso(2);

        try {
            const horaFmt = hora + ':00';
            const res = await apiFetch(`/reservas/disponibilidad?fecha=${fecha}&hora=${horaFmt}&cantidad=${cantidad}`);
            const mesas = res.data || [];
            const lista = document.getElementById('listaMesas');

            if (mesas.length === 0) {
                lista.innerHTML = '<p style="color:var(--danger);font-size:14px">No hay mesas disponibles para esa fecha, hora y cantidad.</p>';
            } else {
                lista.innerHTML = mesas.map(m => `
                    <div class="mesa-card" id="mc-${m.id}" onclick="seleccionarMesa(${m.id}, ${m.numeroMesa}, '${m.ubicacion}', ${m.capacidad})">
                        <span class="mesa-num">${m.numeroMesa}</span>
                        <div>
                            <div style="font-weight:600;font-size:14px">${m.ubicacion}</div>
                            <div class="mesa-info">Capacidad: ${m.capacidad} personas</div>
                        </div>
                    </div>
                `).join('');
            }
        } catch (err) {
            document.getElementById('listaMesas').innerHTML = `<p style="color:var(--danger)">${err.message}</p>`;
        }
    }

    function seleccionarMesa(id, numero, ubicacion, capacidad) {
        mesaSeleccionada = { id, numero, ubicacion, capacidad };
        document.querySelectorAll('.mesa-card').forEach(el => el.classList.remove('selected'));
        const card = document.getElementById(`mc-${id}`);
        if (card) card.classList.add('selected');
        document.getElementById('eRMesa').textContent = '';
    }

    function irPaso3() {
        if (!mesaSeleccionada) {
            document.getElementById('eRMesa').textContent = 'Selecciona una mesa';
            return;
        }
        // Cargar clientes para búsqueda
        apiFetch('/clientes').then(r => { clientesData = r.data || []; });
        irPaso(3);
    }

    function buscarClienteWizard(q) {
        const resultados = document.getElementById('resultadosCliente');
        if (!q || q.length < 2) { resultados.innerHTML = ''; return; }

        const filtrados = clientesData.filter(c =>
            `${c.nombre} ${c.apellido}`.toLowerCase().includes(q.toLowerCase()) ||
            c.dni.includes(q)
        );

        if (filtrados.length === 0) {
            resultados.innerHTML = '<p style="color:var(--dark-grey);font-size:13px">Sin resultados. Puedes registrar al cliente abajo.</p>';
            return;
        }

        resultados.innerHTML = filtrados.slice(0, 5).map(c => `
            <div class="cliente-result" onclick="seleccionarCliente(${c.id}, '${c.nombre}', '${c.apellido}')">
                <strong>${c.nombre} ${c.apellido}</strong> — DNI: ${c.dni}
            </div>
        `).join('');
    }

    function seleccionarCliente(id, nombre, apellido) {
        document.getElementById('rClienteId').value = id;
        document.getElementById('resultadosCliente').innerHTML =
            `<div class="cliente-seleccionado">✔ Cliente seleccionado: <strong>${nombre} ${apellido}</strong></div>`;
        document.getElementById('eRCliente').textContent = '';
    }

    async function registrarNuevoCliente() {
        const nombre   = document.getElementById('rNuevoNombre').value.trim();
        const apellido = document.getElementById('rNuevoApellido').value.trim();
        const dni      = document.getElementById('rNuevoDni').value.trim();
        const telefono = document.getElementById('rNuevoTelefono').value.trim();
        const correo   = document.getElementById('rNuevoCorreo').value.trim() || null;

        if (!nombre || !apellido || !dni || !telefono) {
            showToast('Completa los campos obligatorios del cliente', 'error');
            return;
        }

        try {
            const res = await apiFetch('/clientes', {
                method: 'POST',
                body: JSON.stringify({ nombre, apellido, dni, telefono, correo })
            });
            const c = res.data;
            seleccionarCliente(c.id, c.nombre, c.apellido);
            clientesData.push(c);
            showToast('Cliente registrado y seleccionado');
            ['rNuevoNombre','rNuevoApellido','rNuevoDni','rNuevoTelefono','rNuevoCorreo'].forEach(id =>
                document.getElementById(id).value = '');
        } catch (err) {
            showToast(err.message, 'error');
        }
    }

    async function registrarReserva() {
        const clienteId = parseInt(document.getElementById('rClienteId').value);
        if (!clienteId) {
            document.getElementById('eRCliente').textContent = 'Selecciona o registra un cliente';
            return;
        }

        const btn = document.getElementById('btnRegistrarReserva');
        btn.disabled = true;

        const payload = {
            fecha:     document.getElementById('rFecha').value,
            hora:      document.getElementById('rHora').value + ':00',
            cantidad:  parseInt(document.getElementById('rCantidad').value),
            mesaId:    mesaSeleccionada.id,
            clienteId: clienteId
        };

        try {
            await apiFetch('/reservas', { method: 'POST', body: JSON.stringify(payload) });
            showToast('Reserva registrada correctamente');
            closeModal('modalNueva');
            await cargarReservas();
        } catch (err) {
            showToast(err.message, 'error');
        } finally {
            btn.disabled = false;
        }
    }

    // ─────────────────────────────────────
    // INIT
    // ─────────────────────────────────────
    document.addEventListener('DOMContentLoaded', () => cargarReservas());

    window.addEventListener('click', e => {
        ['modalDetalle','modalEstado','modalNueva'].forEach(id => {
            const m = document.getElementById(id);
            if (e.target === m) closeModal(id);
        });
    });