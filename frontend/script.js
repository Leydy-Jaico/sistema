const API_URL = "http://localhost:8080/api/v1/clientes";

const form = document.getElementById("formCliente");
const clienteId = document.getElementById("clienteId");
const dni = document.getElementById("dni");
const nombre = document.getElementById("nombre");
const apellido = document.getElementById("apellido");
const email = document.getElementById("email");
const telefono = document.getElementById("telefono");
const tablaClientes = document.getElementById("tablaClientes");
const alerta = document.getElementById("alerta");
const tituloFormulario = document.getElementById("tituloFormulario");
const btnGuardar = document.getElementById("btnGuardar");
const btnCancelar = document.getElementById("btnCancelar");

document.addEventListener("DOMContentLoaded", listarClientes);

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const cliente = {
        dni: dni.value.trim(),
        nombre: nombre.value.trim(),
        apellido: apellido.value.trim(),
        email: email.value.trim(),
        telefono: telefono.value.trim()
    };

    try {
        if (clienteId.value) {
            await actualizarCliente(clienteId.value, cliente);
        } else {
            await crearCliente(cliente);
        }

        limpiarFormulario();
        await listarClientes();
    } catch (error) {
        mostrarAlerta(error.message, "danger");
    }
});

document.getElementById("btnBuscar").addEventListener("click", buscarClientes);
document.getElementById("btnLimpiar").addEventListener("click", () => {
    document.getElementById("buscarDni").value = "";
    document.getElementById("buscarNombre").value = "";
    listarClientes();
});
document.getElementById("btnActualizar").addEventListener("click", listarClientes);
btnCancelar.addEventListener("click", limpiarFormulario);

async function crearCliente(cliente) {
    const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cliente)
    });

    if (!response.ok) {
        throw new Error(await obtenerMensajeError(response));
    }

    mostrarAlerta("Cliente registrado correctamente.", "success");
}

async function listarClientes() {
    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error(await obtenerMensajeError(response));
        }

        const clientes = await response.json();
        renderizarTabla(clientes);
    } catch (error) {
        mostrarAlerta(error.message, "danger");
    }
}

async function buscarClientes() {
    const valorDni = document.getElementById("buscarDni").value.trim();
    const valorNombre = document.getElementById("buscarNombre").value.trim();

    let url = API_URL;

    if (valorDni) {
        url += `?dni=${encodeURIComponent(valorDni)}`;
    } else if (valorNombre) {
        url += `?nombre=${encodeURIComponent(valorNombre)}`;
    }

    try {
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error(await obtenerMensajeError(response));
        }

        const data = await response.json();
        const clientes = Array.isArray(data) ? data : [data];
        renderizarTabla(clientes);
    } catch (error) {
        renderizarTabla([]);
        mostrarAlerta(error.message, "danger");
    }
}

function renderizarTabla(clientes) {
    tablaClientes.innerHTML = "";

    if (!clientes.length) {
        tablaClientes.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-secondary py-4">
                    No se encontraron clientes.
                </td>
            </tr>
        `;
        return;
    }

    clientes.forEach(cliente => {
        const fila = document.createElement("tr");

        fila.innerHTML = `
            <td>${cliente.id}</td>
            <td>${escapeHtml(cliente.dni)}</td>
            <td>${escapeHtml(cliente.nombre)}</td>
            <td>${escapeHtml(cliente.apellido)}</td>
            <td>${escapeHtml(cliente.email)}</td>
            <td>${escapeHtml(cliente.telefono || "")}</td>
            <td class="acciones">
                <button class="btn btn-warning btn-sm me-1" onclick='cargarEdicion(${JSON.stringify(cliente)})'>
                    Editar
                </button>
                <button class="btn btn-danger btn-sm" onclick="eliminarCliente(${cliente.id})">
                    Eliminar
                </button>
            </td>
        `;

        tablaClientes.appendChild(fila);
    });
}

window.cargarEdicion = function(cliente) {
    clienteId.value = cliente.id;
    dni.value = cliente.dni;
    nombre.value = cliente.nombre;
    apellido.value = cliente.apellido;
    email.value = cliente.email;
    telefono.value = cliente.telefono || "";

    tituloFormulario.textContent = "Editar cliente";
    btnGuardar.textContent = "Guardar cambios";
    btnCancelar.classList.remove("d-none");

    window.scrollTo({ top: 0, behavior: "smooth" });
};

async function actualizarCliente(id, cliente) {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cliente)
    });

    if (!response.ok) {
        throw new Error(await obtenerMensajeError(response));
    }

    mostrarAlerta("Cliente actualizado correctamente.", "success");
}

window.eliminarCliente = async function(id) {
    if (!confirm("¿Seguro que deseas eliminar este cliente?")) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error(await obtenerMensajeError(response));
        }

        mostrarAlerta("Cliente eliminado correctamente.", "success");
        await listarClientes();
    } catch (error) {
        mostrarAlerta(error.message, "danger");
    }
};

function limpiarFormulario() {
    form.reset();
    clienteId.value = "";
    tituloFormulario.textContent = "Registrar cliente";
    btnGuardar.textContent = "Registrar";
    btnCancelar.classList.add("d-none");
}

function mostrarAlerta(mensaje, tipo) {
    alerta.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${escapeHtml(mensaje)}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    setTimeout(() => {
        alerta.innerHTML = "";
    }, 5000);
}

async function obtenerMensajeError(response) {
    try {
        const data = await response.json();

        if (data.errores) {
            return Object.values(data.errores).join(" | ");
        }

        return data.detail || data.title || "Ocurrió un error.";
    } catch {
        return "No fue posible procesar la respuesta del servidor.";
    }
}

function escapeHtml(valor) {
    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
