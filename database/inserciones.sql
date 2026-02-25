USE elsultan_db;

INSERT INTO Rol(nombre) VALUES
('Administrador'),
('Recepcionista'),
('Cliente');

INSERT INTO Usuario(nombreUsuario, contrasena, nombreCompleto, dni, telefono, correo, rol) VALUES
('martin','admin123','Martin Fernandez Martinez', '12345678', '987654323', 'martin@gmail.com', '1'),
('nicolas','nicolas','Nicolas Bravo Delgado', '12345671', '977654323', 'nicolas@gmail.com', '2'),
('olivia','olivia','Olivia Campos Mora', '12345672', '957654323', 'olivia@gmail.com', '2');

INSERT INTO Cliente(nombre, apellido, dni, telefono, correo) VALUES
('Amanda', 'Mendoza Ibañez', '12345673', '947654323', 'amanda@gmail.com'),
('Matias', 'Garcia Sanchez', '12345674', '937654323', 'matias@gmail.com'),
('Gabriela', 'Mamani Chavez', '12345675', '927654323', 'gabriela@gmail.com'),
('Francisco', 'Jimenez Rivera', '12345676', '989654323', 'francisco@gmail.com'),
('Jorge', 'Nuñez Guerrero', '12345677', '987654323', 'jorge@gmail.com');

INSERT INTO Mesa(numeroMesa, ubicacion, capacidad) VALUES
('10', 'Ventana', '3'),
('20', 'Interior', '4'),
('30', 'Ventana', '2'),
('40', 'Interior', '6'),
('50', 'Terraza', '4');

INSERT INTO Reserva(fecha, hora, cantidad, estado, mesa, cliente, usuario) VALUES
('2026-03-01', '19:00:00', 3, 'Confirmado', 1, 1, 2),
('2026-03-01', '20:00:00', 4, 'Pendiente', 2, 2, 2),
('2026-03-02', '18:30:00', 2, 'Finalizado', 3, 3, 3),
('2026-03-02', '21:00:00', 5, 'Confirmado', 4, 4, 3),
('2026-03-03', '19:30:00', 4, 'Cancelado', 5, 5, 3);

SELECT * FROM Rol;
SELECT * FROM Usuario;
SELECT * FROM Cliente;
SELECT * FROM Mesa;
SELECT * FROM Reserva;
