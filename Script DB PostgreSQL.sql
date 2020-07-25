--nombre de la base de datos: entidadfinancieradb

CREATE TABLE cliente (
  cedula TEXT NOT NULL,
  nombres TEXT NULL,
  telefono TEXT NULL,
  email TEXT NULL,
  direccion TEXT NULL,
  PRIMARY KEY(cedula)
);

CREATE TABLE garantias (
  codGarantia SERIAL NOT NULL,
  clienteGarantia TEXT NOT NULL,
  tipoGarantia TEXT NULL,
  valorGarantia TEXT NULL,
  ubiGarantia TEXT NULL,
  PRIMARY KEY(codGarantia),
  FOREIGN KEY(clienteGarantia)
    REFERENCES cliente(cedula));

CREATE TABLE transacciones (
  codTrans SERIAL NOT NULL,
  clienteTrans TEXT NOT NULL,
  tipoTrans CHAR NULL,
  montoTrans DECIMAL NULL,
  tasaTrans DECIMAL NULL,
  numCuotas INTEGER  NULL,
  fechaSolicitud DATE NULL,
  fechaAprobacion DATE NULL,
  fechaIniciacion DATE NULL,
  fechaTermino DATE NULL,
  estadoSolicitud TEXT NULL,
  PRIMARY KEY(codTrans),
  FOREIGN KEY(clienteTrans)
    REFERENCES cliente(cedula));

CREATE TABLE cuotasPago (
  codCuota SERIAL NOT NULL,
  codComprobante TEXT NULL,
  transCuota INTEGER NOT NULL,
  mensCuota DECIMAL NULL,
  fechaPagoCuota DATE NULL,
  fechaEfectivaCuota DATE NULL,
  modalPagoCuota TEXT NULL,
  estadoCuota TEXT NULL,
  PRIMARY KEY(codCuota),
  FOREIGN KEY(transCuota)
    REFERENCES transacciones(codTrans));

CREATE TABLE cuentasBancarias (
  numCuentaBanc TEXT NOT NULL,
  clienteCuenta TEXT NOT NULL,
  bancoCuentaBanc TEXT NULL,
  tipoCuentaBanc CHAR NULL,
  PRIMARY KEY(numCuentaBanc),
  FOREIGN KEY(clienteCuenta)
    REFERENCES cliente(cedula));

CREATE TABLE prestamos (
  codPrestamo INTEGER NOT NULL,
  fiador TEXT NULL,
  PRIMARY KEY(codPrestamo),
  FOREIGN KEY(fiador)
    REFERENCES cliente(cedula),
  FOREIGN KEY(codPrestamo)
    REFERENCES transacciones(codTrans));

CREATE TABLE inversiones (
  codInversion INTEGER NOT NULL,
  cuentaPagoGeneral TEXT NOT NULL,
  PRIMARY KEY(codInversion),
  FOREIGN KEY(codInversion)
    REFERENCES transacciones(codTrans),
  FOREIGN KEY(cuentaPagoGeneral)
    REFERENCES cuentasBancarias(numCuentaBanc));

CREATE TABLE cuotasPagoInversion (
  codCuotaInversion INTEGER NOT NULL,
  cuentaPagoActual TEXT NOT NULL,
  PRIMARY KEY(codCuotaInversion),
  FOREIGN KEY(cuentaPagoActual)
    REFERENCES cuentasBancarias(numCuentaBanc),
  FOREIGN KEY(codCuotaInversion)
    REFERENCES cuotasPago(codCuota));

CREATE TABLE garantias_prestamo (
  codGarantia INTEGER NOT NULL,
  codPrestamo INTEGER NOT NULL,
  PRIMARY KEY(codGarantia, codPrestamo),
  FOREIGN KEY(codGarantia)
    REFERENCES garantias(codGarantia),
  FOREIGN KEY(codPrestamo)
    REFERENCES prestamos(codPrestamo));

CREATE TABLE usuarios (
  usuario TEXT NOT NULL,
  contrasena TEXT NOT NULL,
  permiso TEXT NOT NULL,
  PRIMARY KEY(usuario)
);

--Administrador por defecto
--INSERT INTO usuarios VALUES('admin','admin','admin');