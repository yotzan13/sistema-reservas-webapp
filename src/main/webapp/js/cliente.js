let clientesCache = [];

    async function cargarClientes() {
        try {
            const res = await apiFetch('/clientes');
            clientesCache = res.data || [];
            renderTabla(clientesCache);
        } catch (err) {
            document.getElementById('tbodyClientes').innerHTML =
                `<tr><td colspan="7" style="text-align:center;color:var(--danger);padding:24px">${err.message}</td></tr>`;
        }
    }

    function renderTabla(lista) {
        const tbody = document.getElementById('tbodyClientes');
        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center;padding:24px;color:var(--dark-grey)">Sin clientes registrados</td></tr>`;
            return;
        }
        tbody.innerHTML = lista.map(c => `
            <tr>
                <td><span>${c.id}</span></td>
                <td>${c.nombre}</td>
                <td>${c.apellido}</td>
                <td>${c.dni}</td>
                <td>${c.telefono}</td>
                <td>${c.correo || '—'}</td>
                <td>
                    <a class="bx bxs-info-circle" style="color:#3f912e;cursor:pointer" title="Ver Detalles" onclick="verDetalle(${c.id})"></a>
                    <a class="bx bxs-edit amarillo" style="cursor:pointer" title="Editar" onclick="abrirModalEditar(${c.id})"></a>
                </td>
            </tr>
        `).join('');
    }

    function verDetalle(id) {
        const c = clientesCache.find(x => x.id === id);
        if (!c) return;
        document.getElementById('detalleContent').innerHTML = `
            <div class="detalle-item"><span class="detalle-label">ID</span><span class="detalle-valor">${c.id}</span></div>
            <div class="detalle-item"><span class="detalle-label">Nombre</span><span class="detalle-valor">${c.nombre}</span></div>
            <div class="detalle-item"><span class="detalle-label">Apellido</span><span class="detalle-valor">${c.apellido}</span></div>
            <div class="detalle-item"><span class="detalle-label">DNI</span><span class="detalle-valor">${c.dni}</span></div>
            <div class="detalle-item"><span class="detalle-label">Teléfono</span><span class="detalle-valor">${c.telefono}</span></div>
            <div class="detalle-item"><span class="detalle-label">Correo</span><span class="detalle-valor">${c.correo || '—'}</span></div>
        `;
        openModal('modalDetalle');
    }

    function limpiarForm() {
        ['fId','fNombre','fApellido','fDni','fTelefono','fCorreo'].forEach(id => document.getElementById(id).value = '');
        ['eNombre','eApellido','eDni','eTelefono'].forEach(id => document.getElementById(id).textContent = '');
    }

    function abrirModalNuevo() {
        limpiarForm();
        document.getElementById('modalFormTitle').textContent = 'Registrar Cliente';
        openModal('modalForm');
    }

    function abrirModalEditar(id) {
        const c = clientesCache.find(x => x.id === id);
        if (!c) return;
        limpiarForm();
        document.getElementById('fId').value       = c.id;
        document.getElementById('fNombre').value   = c.nombre;
        document.getElementById('fApellido').value = c.apellido;
        document.getElementById('fDni').value      = c.dni;
        document.getElementById('fTelefono').value = c.telefono;
        document.getElementById('fCorreo').value   = c.correo || '';
        document.getElementById('modalFormTitle').textContent = 'Editar Cliente';
        openModal('modalForm');
    }

    function validarForm() {
        let ok = true;
        const campos = [
            ['fNombre','eNombre','El nombre es obligatorio'],
            ['fApellido','eApellido','El apellido es obligatorio'],
            ['fDni','eDni','El DNI es obligatorio'],
            ['fTelefono','eTelefono','El teléfono es obligatorio'],
        ];
        campos.forEach(([campo, error, msg]) => {
            const v = document.getElementById(campo).value.trim();
            document.getElementById(error).textContent = v ? '' : msg;
            if (!v) ok = false;
        });
        return ok;
    }

    async function guardarCliente() {
        if (!validarForm()) return;
        const btn = document.getElementById('btnGuardar');
        btn.disabled = true;

        const id     = document.getElementById('fId').value;
        const payload = {
            nombre:   document.getElementById('fNombre').value.trim(),
            apellido: document.getElementById('fApellido').value.trim(),
            dni:      document.getElementById('fDni').value.trim(),
            telefono: document.getElementById('fTelefono').value.trim(),
            correo:   document.getElementById('fCorreo').value.trim() || null
        };

        try {
            if (id) {
                await apiFetch(`/clientes/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
                showToast('Cliente actualizado correctamente');
            } else {
                await apiFetch('/clientes', { method: 'POST', body: JSON.stringify(payload) });
                showToast('Cliente registrado correctamente');
            }
            closeModal('modalForm');
            await cargarClientes();
        } catch (err) {
            showToast(err.message, 'error');
        } finally {
            btn.disabled = false;
        }
    }

    // Búsqueda en tiempo real
    document.addEventListener('DOMContentLoaded', () => {
        cargarClientes();
        document.getElementById('busqueda').addEventListener('input', function() {
            const q = this.value.toLowerCase();
            renderTabla(clientesCache.filter(c =>
                `${c.nombre} ${c.apellido}`.toLowerCase().includes(q) ||
                c.dni.includes(q)
            ));
        });
    });

    // Cerrar modal al clicar fuera
    window.addEventListener('click', e => {
        ['modalDetalle','modalForm'].forEach(id => {
            const m = document.getElementById(id);
            if (e.target === m) closeModal(id);
        });
    });