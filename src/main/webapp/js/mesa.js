let mesasCache = [];
    let mesaIdEliminar = null;

    async function cargarMesas() {
        try {
            const res = await apiFetch('/mesas');
            mesasCache = res.data || [];
            renderTabla(mesasCache);
        } catch (err) {
            document.getElementById('tbodyMesas').innerHTML =
                `<tr><td colspan="4" style="text-align:center;color:var(--danger);padding:24px">${err.message}</td></tr>`;
        }
    }

    function renderTabla(lista) {
        const tbody = document.getElementById('tbodyMesas');
        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;padding:24px;color:var(--dark-grey)">Sin mesas registradas</td></tr>`;
            return;
        }
        tbody.innerHTML = lista.map(m => `
            <tr>
                <td><span>${m.numeroMesa}</span></td>
                <td>${m.ubicacion}</td>
                <td>${m.capacidad} personas</td>
                <td>
                    <a class="bx bxs-info-circle" style="color:#3f912e;cursor:pointer" title="Ver Detalles" onclick="verDetalle(${m.id})"></a>
                    <a class="bx bxs-edit amarillo" style="cursor:pointer" title="Editar" onclick="abrirModalEditar(${m.id})"></a>
                    <a class="bx bxs-trash rojo" style="cursor:pointer" title="Eliminar" onclick="abrirModalEliminar(${m.id})"></a>
                </td>
            </tr>
        `).join('');
    }

    function verDetalle(id) {
        const m = mesasCache.find(x => x.id === id);
        if (!m) return;
        document.getElementById('detalleContent').innerHTML = `
            <div class="detalle-item"><span class="detalle-label">ID</span><span class="detalle-valor">${m.id}</span></div>
            <div class="detalle-item"><span class="detalle-label">Nro. Mesa</span><span class="detalle-valor">${m.numeroMesa}</span></div>
            <div class="detalle-item"><span class="detalle-label">Ubicación</span><span class="detalle-valor">${m.ubicacion}</span></div>
            <div class="detalle-item"><span class="detalle-label">Capacidad</span><span class="detalle-valor">${m.capacidad} personas</span></div>
        `;
        openModal('modalDetalle');
    }

    function limpiarForm() {
        ['fId','fNumero','fCapacidad'].forEach(id => document.getElementById(id).value = '');
        document.getElementById('fUbicacion').value = '';
        ['eNumero','eUbicacion','eCapacidad'].forEach(id => document.getElementById(id).textContent = '');
    }

    function abrirModalNuevo() {
        limpiarForm();
        document.getElementById('modalFormTitle').textContent = 'Registrar Mesa';
        openModal('modalForm');
    }

    function abrirModalEditar(id) {
        const m = mesasCache.find(x => x.id === id);
        if (!m) return;
        limpiarForm();
        document.getElementById('fId').value        = m.id;
        document.getElementById('fNumero').value    = m.numeroMesa;
        document.getElementById('fUbicacion').value = m.ubicacion;
        document.getElementById('fCapacidad').value = m.capacidad;
        document.getElementById('modalFormTitle').textContent = 'Editar Mesa';
        openModal('modalForm');
    }

    function abrirModalEliminar(id) {
        const m = mesasCache.find(x => x.id === id);
        if (!m) return;
        mesaIdEliminar = id;
        document.getElementById('msgEliminar').textContent = `Mesa ${m.numeroMesa} — ${m.ubicacion} (${m.capacidad} personas)`;
        openModal('modalEliminar');
    }

    function validarForm() {
        let ok = true;
        if (!document.getElementById('fNumero').value || document.getElementById('fNumero').value < 1) {
            document.getElementById('eNumero').textContent = 'Número de mesa requerido'; ok = false;
        } else { document.getElementById('eNumero').textContent = ''; }

        if (!document.getElementById('fUbicacion').value) {
            document.getElementById('eUbicacion').textContent = 'Selecciona una ubicación'; ok = false;
        } else { document.getElementById('eUbicacion').textContent = ''; }

        if (!document.getElementById('fCapacidad').value || document.getElementById('fCapacidad').value < 1) {
            document.getElementById('eCapacidad').textContent = 'Capacidad requerida (mín. 1)'; ok = false;
        } else { document.getElementById('eCapacidad').textContent = ''; }

        return ok;
    }

    async function guardarMesa() {
        if (!validarForm()) return;
        const btn = document.getElementById('btnGuardar');
        btn.disabled = true;

        const id = document.getElementById('fId').value;
        const payload = {
            numeroMesa: parseInt(document.getElementById('fNumero').value),
            ubicacion:  document.getElementById('fUbicacion').value,
            capacidad:  parseInt(document.getElementById('fCapacidad').value)
        };

        try {
            if (id) {
                await apiFetch(`/mesas/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
                showToast('Mesa actualizada correctamente');
            } else {
                await apiFetch('/mesas', { method: 'POST', body: JSON.stringify(payload) });
                showToast('Mesa registrada correctamente');
            }
            closeModal('modalForm');
            await cargarMesas();
        } catch (err) {
            showToast(err.message, 'error');
        } finally {
            btn.disabled = false;
        }
    }

    async function confirmarEliminar() {
        if (!mesaIdEliminar) return;
        const btn = document.getElementById('btnConfirmarEliminar');
        btn.disabled = true;
        try {
            await apiFetch(`/mesas/${mesaIdEliminar}`, { method: 'DELETE' });
            showToast('Mesa eliminada correctamente');
            closeModal('modalEliminar');
            mesaIdEliminar = null;
            await cargarMesas();
        } catch (err) {
            showToast(err.message, 'error');
        } finally {
            btn.disabled = false;
        }
    }

    document.addEventListener('DOMContentLoaded', () => cargarMesas());

    window.addEventListener('click', e => {
        ['modalDetalle','modalForm','modalEliminar'].forEach(id => {
            const m = document.getElementById(id);
            if (e.target === m) closeModal(id);
        });
    });