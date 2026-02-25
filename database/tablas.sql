CREATE DATABASE elsultan_db;

USE elsultan_db;

CREATE TABLE Rol (
	id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    CONSTRAINT UQ_Rol_Nombre UNIQUE (nombre)
);

CREATE TABLE Usuario (
	id INT PRIMARY KEY AUTO_INCREMENT,
    nombreUsuario VARCHAR(100) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    nombreCompleto VARCHAR(150) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    telefono VARCHAR(20) NULL,
    correo VARCHAR(150) NULL,
    rol INT NOT NULL,
    CONSTRAINT UQ_Usuario_NombreUsuario UNIQUE (nombreUsuario),
    CONSTRAINT UQ_Usuario_Dni UNIQUE (dni),
    CONSTRAINT UQ_Usuario_Telefono UNIQUE (telefono),
    CONSTRAINT UQ_Usuario_Correo UNIQUE (correo),
    CONSTRAINT FK_Rol_Usuario FOREIGN KEY (rol) REFERENCES Rol(id)
);

CREATE TABLE Cliente (
	id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(150) NULL,
    CONSTRAINT UQ_Cliente_Dni UNIQUE (dni),
    CONSTRAINT UQ_Cliente_Correo UNIQUE (correo)
);

CREATE TABLE Mesa (
	id INT PRIMARY KEY AUTO_INCREMENT,
    numeroMesa INT NOT NULL,
    ubicacion VARCHAR(50),
    capacidad INT NOT NULL,
    CONSTRAINT CK_Mesa_Ubicacion CHECK (ubicacion IN ('Ventana', 'Interior', 'Terraza')),
    CONSTRAINT CK_Reserva_Capacidad CHECK (capacidad> 0)
);

CREATE TABLE Reserva (
	id INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATE NOT NULL DEFAULT (CURRENT_DATE),
    hora TIME NOT NULL,
    cantidad INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
    mesa INT NOT NULL,
    cliente INT NOT NULL,
    usuario INT NOT NULL,
    CONSTRAINT FK_Mesa_Reserva FOREIGN KEY (mesa) REFERENCES Mesa(id),
    CONSTRAINT FK_Cliente_Reserva FOREIGN KEY (cliente) REFERENCES Cliente(id),
    CONSTRAINT FK_Usuario_Reserva FOREIGN KEY (usuario) REFERENCES Usuario(id),
    CONSTRAINT UQ_Mesa_Fecha_Hora UNIQUE (mesa, fecha, hora),
    CONSTRAINT CK_Reserva_Estado CHECK (estado IN ('Pendiente', 'Confirmado', 'Cancelado', 'Finalizado'))
);

CREATE INDEX IDX_Reserva_Fecha ON Reserva(fecha);
CREATE INDEX IDX_Reserva_Cliente ON Reserva(cliente);




