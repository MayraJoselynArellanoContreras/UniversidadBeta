/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package DonativosYPagos;

import DonativosYPagos.DonativosYPagos;
import java.awt.Color;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import util.ConexionBD;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 *
 * @author contr
 */
public class Donativos extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Donativos.class.getName());

    /**
     * Creates new form Donativos
     */
    public Donativos() {
        initComponents();
        // --- Configurar navegación por tabulación ---
this.setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {

    @Override
    public java.awt.Component getComponentAfter(java.awt.Container aContainer, java.awt.Component aComponent) {

        if (aComponent.equals(txtIdDonativo)) return txtFechaRegistro;
        if (aComponent.equals(txtFechaRegistro)) return txtIdGarantia;
        if (aComponent.equals(txtIdGarantia)) return txtNombreDonador;
        if (aComponent.equals(txtNombreDonador)) return txtFechaGarantia;
        if (aComponent.equals(txtFechaGarantia)) return txtCantidadGarantizada;
        if (aComponent.equals(txtCantidadGarantizada)) return txtCantidadRecibida;
        if (aComponent.equals(txtCantidadRecibida)) return comboMetodo;
        if (aComponent.equals(comboMetodo)) return txtNumTDC;
        if (aComponent.equals(txtNumTDC)) return comboNumPagos;
        if (aComponent.equals(comboNumPagos)) return comboCorporacion;
        if (aComponent.equals(comboCorporacion)) return txtObservaciones;
        if (aComponent.equals(txtObservaciones)) return botonRegistrar;
        if (aComponent.equals(botonRegistrar)) return botonEliminarDonativo;
        if (aComponent.equals(botonEliminarDonativo)) return tablaHistorial;
        if (aComponent.equals(tablaHistorial)) return botonVolver;

        return txtIdDonativo; // inicio del ciclo
    }

    @Override
    public java.awt.Component getComponentBefore(java.awt.Container aContainer, java.awt.Component aComponent) {

        if (aComponent.equals(txtFechaRegistro)) return txtIdDonativo;
        if (aComponent.equals(txtIdGarantia)) return txtFechaRegistro;
        if (aComponent.equals(txtNombreDonador)) return txtIdGarantia;
        if (aComponent.equals(txtFechaGarantia)) return txtNombreDonador;
        if (aComponent.equals(txtCantidadGarantizada)) return txtFechaGarantia;
        if (aComponent.equals(txtCantidadRecibida)) return txtCantidadGarantizada;
        if (aComponent.equals(comboMetodo)) return txtCantidadRecibida;
        if (aComponent.equals(txtNumTDC)) return comboMetodo;
        if (aComponent.equals(comboNumPagos)) return txtNumTDC;
        if (aComponent.equals(comboCorporacion)) return comboNumPagos;
        if (aComponent.equals(txtObservaciones)) return comboCorporacion;
        if (aComponent.equals(botonRegistrar)) return txtObservaciones;
        if (aComponent.equals(botonEliminarDonativo)) return botonRegistrar;
        if (aComponent.equals(tablaHistorial)) return botonEliminarDonativo;
        if (aComponent.equals(botonVolver)) return tablaHistorial;

        return botonVolver; // final del ciclo
    }

    @Override public java.awt.Component getDefaultComponent(java.awt.Container c) { return txtIdDonativo; }
    @Override public java.awt.Component getFirstComponent(java.awt.Container c) { return txtIdDonativo; }
    @Override public java.awt.Component getLastComponent(java.awt.Container c) { return botonVolver; }
});

        setLocationRelativeTo(null);
        cargarMetodosPago();
        cargarNumeroPagos();
        cargarCorporaciones();
        cargarTabla();
        cargarProximoIdDonativo();
        habilitarCampoTarjeta();
    }
    
    // Clases internas para manejo de items en combos
    class CorporacionItem {
        private int id;
        private String nombre;
        
        public CorporacionItem(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
        
        public int getId() { return id; }
        
        @Override
        public String toString() {
            return nombre;
        }
    }
    
    private void cargarMetodosPago() {
        comboMetodo.addItem("Seleccione método...");
        comboMetodo.addItem("Cheque");
        comboMetodo.addItem("Tarjeta de Crédito");
        comboMetodo.addItem("Transferencia");
        comboMetodo.addItem("Efectivo");
    }
    
    private void cargarNumeroPagos() {
        comboNumPagos.removeAllItems();
        comboNumPagos.addItem("Seleccione pagos...");
        for (int i = 1; i <= 12; i++) {
            comboNumPagos.addItem(String.valueOf(i));
        }
    }
    
    private void cargarCorporaciones() {
        comboCorporacion.removeAllItems();
        comboCorporacion.addItem(new CorporacionItem(0, "Seleccione corporación..."));
        
        String sql = "SELECT idCorporacion, nombre FROM Corporacion ORDER BY nombre";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                comboCorporacion.addItem(
                    new CorporacionItem(
                        rs.getInt("idCorporacion"),
                        rs.getString("nombre")
                    )
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar corporaciones: " + e.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
  private void cargarProximoIdDonativo() {
    try (Connection con = ConexionBD.getConexion()) {
        String sql = "SELECT ISNULL(IDENT_CURRENT('Donativo'), 0) + 1 AS siguiente";
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                int siguienteId = rs.getInt("siguiente");
                txtIdDonativo.setText(String.valueOf(siguienteId));
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener próximo ID: " + e.getMessage());
        txtIdDonativo.setText("");
    }
}
    
private void cargarTabla() {
    String sql = """
        SELECT 
            D.idDonativo,
            D.fechaGarantia,
            D.cantidadGarantizada,
            D.cantidadRecibida,
            D.metodoPago,
            D.numeroPagos,
            DO.nombre as donador,
            C.nombre as corporacion,
            D.Observaciones,
            DO.circuloDonador  -- Agregar esta columna
        FROM Donativo D
        LEFT JOIN Donador DO ON D.idDonador = DO.idDonador
        LEFT JOIN Corporacion C ON D.idCorporacion = C.idCorporacion
        ORDER BY D.idDonativo DESC
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        javax.swing.table.DefaultTableModel modelo = 
            new javax.swing.table.DefaultTableModel(
                new Object[]{"ID", "Fecha", "Garantía", "Recibido", "Método", 
                           "Pagos", "Donador", "Corporación", "Observaciones", "Círculo"}, 0) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getInt("idDonativo"),
                rs.getString("fechaGarantia"),
                rs.getBigDecimal("cantidadGarantizada"),
                rs.getBigDecimal("cantidadRecibida"),
                rs.getString("metodoPago"),
                rs.getInt("numeroPagos"),
                rs.getString("donador"),
                rs.getString("corporacion"),
                rs.getString("Observaciones"),
                rs.getString("circuloDonador")  // Mostrar círculo
            });
        }
        
        tablaHistorial.setModel(modelo);
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error al cargar donativos: " + e.getMessage(),
            "Error BD",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void habilitarCampoTarjeta() {
        String metodo = comboMetodo.getSelectedItem().toString();
        boolean esTarjeta = metodo.equals("Tarjeta de Crédito");
        
        txtNumTDC.setEnabled(esTarjeta);
        txtNumTDC.setBackground(esTarjeta ? Color.WHITE : new Color(240, 240, 240));
        
        if (!esTarjeta) {
            txtNumTDC.setText("");
        }
    }
    
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        boolean hayErrores = false;
        
        // Validar donador
        String idDonador = txtIdGarantia.getText().trim();
        if (idDonador.isEmpty() || txtNombreDonador.getText().trim().isEmpty()) {
            errores.append("• Debe buscar y seleccionar un donador\n");
            txtIdGarantia.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
        
        // Validar fecha de la garantia
        String fecha = txtFechaGarantia.getText().trim();
        if (fecha.isEmpty() || fecha.equals("dd/mm/aaaa")) {
            errores.append("• La fecha es obligatoria\n");
            txtFechaGarantia.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        } else if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
            errores.append("• Formato de fecha inválido (dd/mm/aaaa)\n");
            txtFechaGarantia.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
        
        // Validar fecha de registro del donativo
        String fechaR = txtFechaRegistro.getText().trim();
        if (fecha.isEmpty() || fecha.equals("dd/mm/aaaa")) {
            errores.append("• La fecha es obligatoria\n");
            txtFechaRegistro.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        } else if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
            errores.append("• Formato de fecha inválido (dd/mm/aaaa)\n");
            txtFechaRegistro.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
        
        // Validar cantidades
        String cantidadG = txtCantidadGarantizada.getText().trim();
        if (cantidadG.isEmpty()) {
            errores.append("• La cantidad garantizada es obligatoria\n");
            txtCantidadGarantizada.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        } else {
            try {
                BigDecimal valor = new BigDecimal(cantidadG);
                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    errores.append("• La cantidad debe ser mayor a 0\n");
                    txtCantidadGarantizada.setBackground(new Color(255, 200, 200));
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errores.append("• Formato de cantidad inválido\n");
                txtCantidadGarantizada.setBackground(new Color(255, 200, 200));
                hayErrores = true;
            }
        }
        
        String cantidadR = txtCantidadRecibida.getText().trim();
        if (cantidadR.isEmpty()) {
            errores.append("• La cantidad recibida es obligatoria\n");
            txtCantidadRecibida.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        } else {
            try {
                BigDecimal valor = new BigDecimal(cantidadR);
                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    errores.append("• La cantidad debe ser mayor a 0\n");
                    txtCantidadRecibida.setBackground(new Color(255, 200, 200));
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errores.append("• Formato de cantidad inválido\n");
                txtCantidadRecibida.setBackground(new Color(255, 200, 200));
                hayErrores = true;
            }
        }
        
        // Validar método de pago
        if (comboMetodo.getSelectedIndex() <= 0) {
            errores.append("• Debe seleccionar un método de pago\n");
            comboMetodo.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        } else {
            String metodo = comboMetodo.getSelectedItem().toString();
            if (metodo.equals("Tarjeta de Crédito")) {
                String numTarjeta = txtNumTDC.getText().trim();
                if (numTarjeta.isEmpty()) {
                    errores.append("• Número de tarjeta es obligatorio\n");
                    txtNumTDC.setBackground(new Color(255, 200, 200));
                    hayErrores = true;
                } else if (!numTarjeta.matches("\\d{13,19}")) {
                    errores.append("• Tarjeta debe tener 13-19 dígitos\n");
                    txtNumTDC.setBackground(new Color(255, 200, 200));
                    hayErrores = true;
                }
            }
        }
        
        // Validar número de pagos
        if (comboNumPagos.getSelectedIndex() <= 0) {
            errores.append("• Debe seleccionar número de pagos\n");
            comboNumPagos.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
        
        // Validar corporación
        if (comboCorporacion.getSelectedIndex() <= 0) {
            errores.append("• Debe seleccionar una corporación\n");
            comboCorporacion.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
        
        if (hayErrores) {
            JOptionPane.showMessageDialog(this,
                "Por favor corrija los siguientes errores:\n\n" + errores.toString(),
                "Validación fallida",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void buscarDonador() {
        String criterio = JOptionPane.showInputDialog(this,
            "Ingrese ID o nombre del donador:",
            "Buscar Donador",
            JOptionPane.QUESTION_MESSAGE);
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            buscarDonadorEnBD(criterio.trim());
        }
    }
    
    private void buscarDonadorEnBD(String criterio) {
        String sql = """
            SELECT idDonador, nombre 
            FROM Donador 
            WHERE idDonador LIKE ? OR nombre LIKE ?
            ORDER BY nombre
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String parametro = "%" + criterio + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                txtIdGarantia.setText(rs.getString("idDonador"));
                txtNombreDonador.setText(rs.getString("nombre"));
                
                JOptionPane.showMessageDialog(this,
                    "✅ Donador encontrado\n\n" +
                    "ID: " + rs.getString("idDonador") + "\n" +
                    "Nombre: " + rs.getString("nombre"),
                    "Búsqueda exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se encontró ningún donador con: " + criterio,
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error en búsqueda: " + e.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
 private void registrarDonativo() {
    if (!validarCampos()) {
        return;
    }
    
    // Verificar qué dice el botón
    String textoBoton = botonRegistrar.getText();
    
    if (textoBoton.equals("Registrar Donativo")) {
        guardarNuevoDonativo();
    } else if (textoBoton.equals("Actualizar Donativo")) {
        actualizarDonativo();
    } else {
        // Por defecto, registrar como nuevo
        guardarNuevoDonativo();
    }
}
    
private void guardarNuevoDonativo() {
    if (!validarCampos()) {
        return;
    }
    
    Connection con = null;
    try {
        con = ConexionBD.getConexion();
        con.setAutoCommit(false); // Iniciar transacción
        
        // 1. Insertar el donativo
        String sqlDonativo = """
            INSERT INTO Donativo (
                idDonador, fechaGarantia, cantidadGarantizada,
                cantidadRecibida, metodoPago, numeroPagos,
                numeroTarjeta, idCorporacion, Observaciones
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        BigDecimal cantidadRecibida = new BigDecimal(txtCantidadRecibida.getText().trim());
        String idDonador = txtIdGarantia.getText().trim();
        
        try (PreparedStatement ps = con.prepareStatement(sqlDonativo, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, idDonador);
            ps.setString(2, convertirFechaSQL(txtFechaGarantia.getText().trim()));
            ps.setBigDecimal(3, new BigDecimal(txtCantidadGarantizada.getText().trim()));
            ps.setBigDecimal(4, cantidadRecibida);
            ps.setString(5, comboMetodo.getSelectedItem().toString());
            ps.setInt(6, Integer.parseInt(comboNumPagos.getSelectedItem().toString()));
            
            if (comboMetodo.getSelectedItem().toString().equals("Tarjeta de Crédito")) {
                ps.setString(7, txtNumTDC.getText().trim());
            } else {
                ps.setString(7, null);
            }
            
            CorporacionItem corp = (CorporacionItem) comboCorporacion.getSelectedItem();
            ps.setInt(8, corp.getId());
            
            String observaciones = txtObservaciones.getText().trim();
            ps.setString(9, observaciones.isEmpty() ? null : observaciones);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                int idGenerado = 0;
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
                
                // 2. ACTUALIZAR TOTAL DONADO Y CÍRCULO DEL DONADOR
                actualizarCirculoDonador(con, idDonador, cantidadRecibida);
                
                // Confirmar transacción
                con.commit();
                
                JOptionPane.showMessageDialog(this,
                    "✅ Donativo registrado exitosamente\n\n" +
                    "ID Donativo: " + idGenerado + "\n" +
                    "Donador: " + txtNombreDonador.getText() + "\n" +
                    "Monto: $" + String.format("%,.2f", cantidadRecibida) + "\n" +
                    "Círculo actualizado automáticamente",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                limpiarCampos();
                 cargarProximoIdDonativo();
                cargarTabla();            }
        }
        
    } catch (SQLException e) {
        try {
            if (con != null) {
                con.rollback(); // Revertir en caso de error
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(this,
            "Error al guardar donativo:\n" + e.getMessage(),
            "Error BD",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        
    } finally {
        try {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

private void actualizarCirculoDonador(Connection con, String idDonador, BigDecimal montoDonativo) throws SQLException {
    // 1. Sumar al total donado
    String sqlSumar = """
        UPDATE Donador 
        SET totalDonado = ISNULL(totalDonado, 0) + ?
        WHERE idDonador = ?
        """;
    
    try (PreparedStatement ps = con.prepareStatement(sqlSumar)) {
        ps.setBigDecimal(1, montoDonativo);
        ps.setString(2, idDonador);
        ps.executeUpdate();
    }
    
    // 2. Obtener el nuevo total donado
    String sqlObtenerTotal = """
        SELECT ISNULL(totalDonado, 0) as total 
        FROM Donador 
        WHERE idDonador = ?
        """;
    
    BigDecimal totalDonado = BigDecimal.ZERO;
    try (PreparedStatement ps = con.prepareStatement(sqlObtenerTotal)) {
        ps.setString(1, idDonador);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            totalDonado = rs.getBigDecimal("total");
        }
    }
    
    // 3. Calcular y actualizar el círculo basado en el total acumulado
    String circulo = calcularCirculoDonador(totalDonado);
    
    String sqlActualizarCirculo = """
        UPDATE Donador 
        SET circuloDonador = ?
        WHERE idDonador = ?
        """;
    
    try (PreparedStatement ps = con.prepareStatement(sqlActualizarCirculo)) {
        ps.setString(1, circulo);
        ps.setString(2, idDonador);
        ps.executeUpdate();
    }
    
    // Opcional: Mostrar en consola el cambio
    System.out.println("Donador " + idDonador + 
                      " - Total donado: $" + String.format("%,.2f", totalDonado) + 
                      " - Círculo: " + circulo);
}
    
private void actualizarDonativo() {
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Actualizar donativo ID: " + txtIdDonativo.getText() + "?\n" +
        "Nota: Esto actualizará el círculo del donador.",
        "Confirmar actualización",
        JOptionPane.YES_NO_OPTION);
    
    if (confirmacion != JOptionPane.YES_OPTION) {
        return;
    }
    
    Connection con = null;
    try {
        con = ConexionBD.getConexion();
        con.setAutoCommit(false);
        
        // 1. Obtener el monto anterior del donativo
        String sqlObtenerAnterior = """
            SELECT cantidadRecibida, idDonador 
            FROM Donativo 
            WHERE idDonativo = ?
            """;
        
        BigDecimal montoAnterior = BigDecimal.ZERO;
        String idDonador = "";
        
        try (PreparedStatement ps = con.prepareStatement(sqlObtenerAnterior)) {
            ps.setInt(1, Integer.parseInt(txtIdDonativo.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                montoAnterior = rs.getBigDecimal("cantidadRecibida");
                idDonador = rs.getString("idDonador");
            }
        }
        
        // 2. Actualizar el donativo
        BigDecimal nuevoMonto = new BigDecimal(txtCantidadRecibida.getText().trim());
        String sqlActualizar = """
            UPDATE Donativo SET
                idDonador = ?, fechaGarantia = ?, cantidadGarantizada = ?,
                cantidadRecibida = ?, metodoPago = ?, numeroPagos = ?,
                numeroTarjeta = ?, idCorporacion = ?, Observaciones = ?
            WHERE idDonativo = ?
            """;
        
        try (PreparedStatement ps = con.prepareStatement(sqlActualizar)) {
            ps.setString(1, txtIdGarantia.getText().trim());
            ps.setString(2, convertirFechaSQL(txtFechaGarantia.getText().trim()));
            ps.setBigDecimal(3, new BigDecimal(txtCantidadGarantizada.getText().trim()));
            ps.setBigDecimal(4, nuevoMonto);
            ps.setString(5, comboMetodo.getSelectedItem().toString());
            ps.setInt(6, Integer.parseInt(comboNumPagos.getSelectedItem().toString()));
            
            if (comboMetodo.getSelectedItem().toString().equals("Tarjeta de Crédito")) {
                ps.setString(7, txtNumTDC.getText().trim());
            } else {
                ps.setString(7, null);
            }
            
            CorporacionItem corp = (CorporacionItem) comboCorporacion.getSelectedItem();
            ps.setInt(8, corp.getId());
            
            String observaciones = txtObservaciones.getText().trim();
            ps.setString(9, observaciones.isEmpty() ? null : observaciones);
            
            ps.setInt(10, Integer.parseInt(txtIdDonativo.getText().trim()));
            
            int filas = ps.executeUpdate();
            
            if (filas > 0 && !idDonador.isEmpty()) {
                // 3. Ajustar el total donado (nuevo - anterior)
                BigDecimal diferencia = nuevoMonto.subtract(montoAnterior);
                
                if (!diferencia.equals(BigDecimal.ZERO)) {
                    // Actualizar total donado
                    String sqlAjustarTotal = """
                        UPDATE Donador 
                        SET totalDonado = ISNULL(totalDonado, 0) + ?
                        WHERE idDonador = ?
                        """;
                    
                    try (PreparedStatement ps2 = con.prepareStatement(sqlAjustarTotal)) {
                        ps2.setBigDecimal(1, diferencia);
                        ps2.setString(2, idDonador);
                        ps2.executeUpdate();
                    }
                    
                    // Recalcular círculo
                    String sqlObtenerTotal = """
                        SELECT ISNULL(totalDonado, 0) as total 
                        FROM Donador 
                        WHERE idDonador = ?
                        """;
                    
                    BigDecimal totalDonado = BigDecimal.ZERO;
                    try (PreparedStatement ps3 = con.prepareStatement(sqlObtenerTotal)) {
                        ps3.setString(1, idDonador);
                        ResultSet rs = ps3.executeQuery();
                        if (rs.next()) {
                            totalDonado = rs.getBigDecimal("total");
                        }
                    }
                    
                    String circulo = calcularCirculoDonador(totalDonado);
                    
                    String sqlActualizarCirculo = """
                        UPDATE Donador 
                        SET circuloDonador = ?
                        WHERE idDonador = ?
                        """;
                    
                    try (PreparedStatement ps4 = con.prepareStatement(sqlActualizarCirculo)) {
                        ps4.setString(1, circulo);
                        ps4.setString(2, idDonador);
                        ps4.executeUpdate();
                    }
                }
                
                con.commit();
                
                JOptionPane.showMessageDialog(this,
                    "✅ Donativo actualizado exitosamente\n" +
                    "Círculo del donador recalculado automáticamente",
                    "Actualización exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                limpiarCampos();
                cargarTabla();
            }
        }
        
    } catch (SQLException e) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(this,
            "Error al actualizar donativo:\n" + e.getMessage(),
            "Error BD",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        
    } finally {
        try {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


    
    private String convertirFechaSQL(String fecha) {
        try {
            SimpleDateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat salida = new SimpleDateFormat("yyyy-MM-dd");
            return salida.format(entrada.parse(fecha));
        } catch (Exception e) {
            return LocalDate.now().toString();
        }
    }
    
private void eliminarDonativo() {
    int fila = tablaHistorial.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
            "Seleccione un donativo de la tabla para eliminar",
            "Selección requerida",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int idDonativo = (int) tablaHistorial.getValueAt(fila, 0);
    String donador = tablaHistorial.getValueAt(fila, 6).toString();
    
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Eliminar donativo ID: " + idDonativo + "?\n\n" +
        "Donador: " + donador + "\n\n" +
        "⚠️ Esto restará el monto del total donado\n" +
        "y recalculará el círculo del donador.\n\n" +
        "Esta acción no se puede deshacer",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirmacion != JOptionPane.YES_OPTION) {
        return;
    }
    
    Connection con = null;
    try {
        con = ConexionBD.getConexion();
        con.setAutoCommit(false);
        
        // 1. Obtener datos del donativo antes de eliminar
        String sqlObtenerDatos = """
            SELECT cantidadRecibida, idDonador 
            FROM Donativo 
            WHERE idDonativo = ?
            """;
        
        BigDecimal montoEliminar = BigDecimal.ZERO;
        String idDonador = "";
        
        try (PreparedStatement ps = con.prepareStatement(sqlObtenerDatos)) {
            ps.setInt(1, idDonativo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                montoEliminar = rs.getBigDecimal("cantidadRecibida");
                idDonador = rs.getString("idDonador");
            }
        }
        
        // 2. Eliminar el donativo
        String sqlEliminar = "DELETE FROM Donativo WHERE idDonativo = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sqlEliminar)) {
            ps.setInt(1, idDonativo);
            int filas = ps.executeUpdate();
            
            if (filas > 0 && !idDonador.isEmpty()) {
                // 3. Restar del total donado
                String sqlRestarTotal = """
                    UPDATE Donador 
                    SET totalDonado = ISNULL(totalDonado, 0) - ?
                    WHERE idDonador = ?
                    """;
                
                try (PreparedStatement ps2 = con.prepareStatement(sqlRestarTotal)) {
                    ps2.setBigDecimal(1, montoEliminar);
                    ps2.setString(2, idDonador);
                    ps2.executeUpdate();
                }
                
                // 4. Recalcular círculo
                String sqlObtenerTotal = """
                    SELECT ISNULL(totalDonado, 0) as total 
                    FROM Donador 
                    WHERE idDonador = ?
                    """;
                
                BigDecimal totalDonado = BigDecimal.ZERO;
                try (PreparedStatement ps3 = con.prepareStatement(sqlObtenerTotal)) {
                    ps3.setString(1, idDonador);
                    ResultSet rs = ps3.executeQuery();
                    if (rs.next()) {
                        totalDonado = rs.getBigDecimal("total");
                    }
                }
                
                String circulo = calcularCirculoDonador(totalDonado);
                
                String sqlActualizarCirculo = """
                    UPDATE Donador 
                    SET circuloDonador = ?
                    WHERE idDonador = ?
                    """;
                
                try (PreparedStatement ps4 = con.prepareStatement(sqlActualizarCirculo)) {
                    ps4.setString(1, circulo);
                    ps4.setString(2, idDonador);
                    ps4.executeUpdate();
                }
                
                con.commit();
                
                JOptionPane.showMessageDialog(this,
                    "✅ Donativo eliminado exitosamente\n" +
                    "Círculo del donador actualizado automáticamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTabla();
                cargarProximoIdDonativo();
            }
        }
        
    } catch (SQLException e) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(this,
            "Error al eliminar donativo:\n" + e.getMessage(),
            "Error BD",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        
    } finally {
        try {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    
    private void buscarDonativo() {
        String criterio = JOptionPane.showInputDialog(this,
            "Ingrese ID del donativo a buscar:",
            "Buscar Donativo",
            JOptionPane.QUESTION_MESSAGE);
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            buscarGarantiaEnBD(criterio.trim());
        }
    }
    
private void buscarGarantiaEnBD(String idGarantia) {
    String sql = """
        SELECT G.*, DO.nombre as nombreDonador, DO.idDonador
        FROM Garantia G
        LEFT JOIN Donador DO ON G.idDonador = DO.idDonador
        WHERE G.idGarantia = ?
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, Integer.parseInt(idGarantia));
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            // Autocompletar ID y nombre del donador
            txtIdGarantia.setText(rs.getString("idDonador"));
            txtNombreDonador.setText(rs.getString("nombreDonador"));
            
            // Autocompletar fecha de garantía
            String fechaBD = rs.getString("fechaGarantia");
            if (fechaBD != null) {
                try {
                    SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat salida = new SimpleDateFormat("dd/MM/yyyy");
                    txtFechaGarantia.setText(salida.format(entrada.parse(fechaBD)));
                    txtFechaGarantia.setForeground(Color.BLACK);
                } catch (Exception e) {
                    txtFechaGarantia.setText(fechaBD);
                }
            }
            
            JOptionPane.showMessageDialog(this,
                "✅ Garantía encontrada\n\n" +
                "ID Garantía: " + idGarantia + "\n" +
                "Donador: " + rs.getString("nombreDonador"),
                "Búsqueda exitosa",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No se encontró garantía con ID: " + idGarantia,
                "Sin resultados",
                JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException | NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Error en búsqueda: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void cargarDonativoEnFormulario(ResultSet rs) throws SQLException {
        txtIdGarantia.setText(String.valueOf(rs.getInt("idGarantia")));
    txtIdGarantia.setText(rs.getString("idDonador"));          // Autocompletar ID donador
    txtNombreDonador.setText(rs.getString("nombreDonador")); // Autocompletar nombre
    
    // Fecha de garantía se autocompleta automáticamente
    String fechaBD = rs.getString("fechaGarantia");
    if (fechaBD != null) {
        try {
            SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat salida = new SimpleDateFormat("dd/MM/yyyy");
            txtFechaGarantia.setText(salida.format(entrada.parse(fechaBD)));
            txtFechaGarantia.setForeground(Color.BLACK);
        } catch (Exception e) {
            txtFechaGarantia.setText(fechaBD);
        }
    }
        
        // Cantidades
        txtCantidadGarantizada.setText(rs.getBigDecimal("cantidadGarantizada").toString());
        txtCantidadRecibida.setText(rs.getBigDecimal("cantidadRecibida").toString());
        
        // Método de pago
        String metodo = rs.getString("metodoPago");
        for (int i = 0; i < comboMetodo.getItemCount(); i++) {
            if (comboMetodo.getItemAt(i).equals(metodo)) {
                comboMetodo.setSelectedIndex(i);
                break;
            }
        }
        
        // Número de pagos
        String numPagos = String.valueOf(rs.getInt("numeroPagos"));
        for (int i = 0; i < comboNumPagos.getItemCount(); i++) {
            if (comboNumPagos.getItemAt(i).equals(numPagos)) {
                comboNumPagos.setSelectedIndex(i);
                break;
            }
        }
        
        // Tarjeta
        String tarjeta = rs.getString("numeroTarjeta");
        if (tarjeta != null) {
            txtNumTDC.setText(tarjeta);
        }
        
        // Corporación
        int idCorp = rs.getInt("idCorporacion");
        for (int i = 0; i < comboCorporacion.getItemCount(); i++) {
            Object item = comboCorporacion.getItemAt(i);
            if (item instanceof CorporacionItem) {
                CorporacionItem corp = (CorporacionItem) item;
                if (corp.getId() == idCorp) {
                    comboCorporacion.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Observaciones
        txtObservaciones.setText(rs.getString("Observaciones"));
    }
    
private void editarDonativoDesdeTabla() {
    int fila = tablaHistorial.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
            "Seleccione un donativo de la tabla para editar",
            "Selección requerida",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int idDonativo = (int) tablaHistorial.getValueAt(fila, 0);
    buscarGarantiaEnBD(String.valueOf(idDonativo));
}
    
 private void limpiarCampos() {
    // SIEMPRE cargar el próximo ID para nuevo registro
    cargarProximoIdDonativo();
    
    txtIdGarantia.setText("");
    txtNombreDonador.setText("");
    txtFechaGarantia.setText("dd/mm/aaaa");
    txtFechaGarantia.setForeground(Color.GRAY);
    txtCantidadGarantizada.setText("");
    txtCantidadRecibida.setText("");
    txtNumTDC.setText("");
    txtObservaciones.setText("");
    
    comboMetodo.setSelectedIndex(0);
    comboNumPagos.setSelectedIndex(0);
    comboCorporacion.setSelectedIndex(0);
    
    txtNumTDC.setEnabled(false);
    txtNumTDC.setBackground(new Color(240, 240, 240));
    
    // Asegurar que el botón dice "Registrar"
    botonRegistrar.setText("Registrar Donativo");
    tablaHistorial.clearSelection();
    
    // Restaurar colores de fondo
    txtIdGarantia.setBackground(Color.WHITE);
    txtFechaGarantia.setBackground(Color.WHITE);
    txtCantidadGarantizada.setBackground(Color.WHITE);
    txtCantidadRecibida.setBackground(Color.WHITE);
    comboMetodo.setBackground(Color.WHITE);
    comboNumPagos.setBackground(Color.WHITE);
    comboCorporacion.setBackground(Color.WHITE);
    
}
    
    // Agregar este método en tu clase Donativos.java
private String calcularCirculoDonador(BigDecimal montoDonativo) {
    double monto = montoDonativo.doubleValue();
    
    if (monto >= 100000.00) {
        return "Círculo Fundadores";
    } else if (monto >= 50000.00) {
        return "Círculo del Presidente";
    } else if (monto >= 25000.00) {
        return "Círculo Platino";
    } else if (monto >= 10000.00) {
        return "Círculo Oro";
    } else if (monto >= 5000.00) {
        return "Círculo Plata";
    } else if (monto >= 1000.00) {
        return "Círculo Bronce";
    } else if (monto >= 500.00) {
        return "Círculo Amigos";
    } else if (monto >= 250.00) {
        return "Círculo Contribuyente";
    } else if (monto >= 100.00) {
        return "Círculo Apoyo";
    } else if (monto >= 1.00) {
        return "Círculo Básico";
    } else {
        return "Sin Círculo";
    }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar1 = new java.awt.MenuBar();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        botonRegistrar = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaHistorial = new javax.swing.JTable();
        botonEliminarDonativo = new javax.swing.JButton();
        botonBuscarGarantia = new javax.swing.JButton();
        comboMetodo = new javax.swing.JComboBox<>();
        comboNumPagos = new javax.swing.JComboBox<>();
        txtNumTDC = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        botonBuscarDonativo = new javax.swing.JButton();
        botonEditarDonativo = new javax.swing.JButton();
        botonLimpiar = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtIdDonativo = new javax.swing.JTextField();
        comboCorporacion = new javax.swing.JComboBox();
        txtIdGarantia = new javax.swing.JTextField();
        txtNombreDonador = new javax.swing.JTextField();
        txtFechaGarantia = new javax.swing.JTextField();
        txtCantidadGarantizada = new javax.swing.JTextField();
        txtCantidadRecibida = new javax.swing.JTextField();
        botonVolver = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtFechaRegistro = new javax.swing.JTextField();
        botonGestionarPagos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 204));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\folder-download.png")); // NOI18N
        jLabel1.setText("Registro de Donativo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(354, 354, 354)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("id Garantia:");

        jLabel3.setText("Fecha de garantia:");

        jLabel4.setText("Cantidad garantizada:");

        jLabel5.setText("Cantidad recibida: ");

        jLabel6.setText("Metodo de pago:");

        jLabel7.setText("Numero de pagos:");

        jLabel8.setText("Tarjeta de credito: ");

        jLabel9.setText("Corporacion que aporta: ");

        jLabel10.setText("Observaciones: ");

        botonRegistrar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\check.png")); // NOI18N
        botonRegistrar.setText("Registrar Donativo");
        botonRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRegistrarActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setText("Historial de Donativos:");

        tablaHistorial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaHistorial);

        botonEliminarDonativo.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\cross.png")); // NOI18N
        botonEliminarDonativo.setText("Eliminar Donativo");
        botonEliminarDonativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarDonativoActionPerformed(evt);
            }
        });

        botonBuscarGarantia.setBackground(new java.awt.Color(204, 255, 153));
        botonBuscarGarantia.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\chart-tree.png")); // NOI18N
        botonBuscarGarantia.setText("Buscar Garantia");
        botonBuscarGarantia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarGarantiaActionPerformed(evt);
            }
        });

        comboMetodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMetodoActionPerformed(evt);
            }
        });

        comboNumPagos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        txtObservaciones.setColumns(20);
        txtObservaciones.setRows(5);
        jScrollPane2.setViewportView(txtObservaciones);

        botonBuscarDonativo.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\search-alt.png")); // NOI18N
        botonBuscarDonativo.setText("Buscar Donativo");
        botonBuscarDonativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarDonativoActionPerformed(evt);
            }
        });

        botonEditarDonativo.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\edit.png")); // NOI18N
        botonEditarDonativo.setText("Editar Donativo");
        botonEditarDonativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEditarDonativoActionPerformed(evt);
            }
        });

        botonLimpiar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\broom.png")); // NOI18N
        botonLimpiar.setText("Limpiar Casillas");
        botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarActionPerformed(evt);
            }
        });

        jLabel12.setText("Id Donativo");

        txtIdDonativo.setEditable(false);

        txtIdGarantia.setEditable(false);
        txtIdGarantia.setText("id de la garantia");

        txtNombreDonador.setEditable(false);
        txtNombreDonador.setBackground(new java.awt.Color(255, 255, 255));
        txtNombreDonador.setText("Nombre del donador");

        txtFechaGarantia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaGarantiaActionPerformed(evt);
            }
        });

        botonVolver.setBackground(new java.awt.Color(102, 204, 0));
        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        jLabel14.setText("Fecha de registro del donativo: ");

        botonGestionarPagos.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\money (1).png")); // NOI18N
        botonGestionarPagos.setText("Gestionar Pagos");
        botonGestionarPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGestionarPagosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(150, 150, 150))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel10))
                                .addGap(44, 44, 44)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNumTDC, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtIdDonativo, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboNumPagos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCantidadRecibida, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCantidadGarantizada, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtFechaGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(34, 34, 34)
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtIdGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtNombreDonador, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(44, 44, 44)
                                        .addComponent(botonBuscarGarantia))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel7))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(botonRegistrar, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(botonEliminarDonativo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(botonBuscarDonativo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(botonLimpiar, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(botonEditarDonativo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(botonGestionarPagos, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(24, 24, 24)
                .addComponent(botonVolver))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(botonVolver))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtIdDonativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtIdGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombreDonador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonBuscarGarantia))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtFechaGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(txtFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonRegistrar))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(txtCantidadGarantizada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(botonEliminarDonativo)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(txtCantidadRecibida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(7, 7, 7)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(comboMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(botonBuscarDonativo)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboNumPagos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonEditarDonativo))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtNumTDC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(botonLimpiar)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10))
                                .addGap(28, 28, 28))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(botonGestionarPagos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonBuscarGarantiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarGarantiaActionPerformed
  String idBuscado = JOptionPane.showInputDialog(
        this,
        "Ingrese el ID de la garantía:",
        "Buscar Garantía",
        JOptionPane.QUESTION_MESSAGE
    );
    
    if (idBuscado != null) {
        idBuscado = idBuscado.trim();
        if (!idBuscado.isEmpty()) {
            buscarGarantiaEnBD(idBuscado);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Debe ingresar un ID para buscar", 
                "ID vacío", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    }//GEN-LAST:event_botonBuscarGarantiaActionPerformed

    
    private void botonVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolverActionPerformed
        DonativosYPagos d = new DonativosYPagos();
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_botonVolverActionPerformed

    private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_botonLimpiarActionPerformed

    private void botonRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRegistrarActionPerformed
        registrarDonativo();
    }//GEN-LAST:event_botonRegistrarActionPerformed

    private void comboMetodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMetodoActionPerformed
        habilitarCampoTarjeta();
    }//GEN-LAST:event_comboMetodoActionPerformed

    private void botonEliminarDonativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarDonativoActionPerformed
    eliminarDonativo();
    }//GEN-LAST:event_botonEliminarDonativoActionPerformed

    private void botonEditarDonativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarDonativoActionPerformed
       editarDonativoDesdeTabla();
    }//GEN-LAST:event_botonEditarDonativoActionPerformed

    private void botonBuscarDonativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarDonativoActionPerformed
        buscarDonativo();
    }//GEN-LAST:event_botonBuscarDonativoActionPerformed

    private void txtFechaGarantiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaGarantiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaGarantiaActionPerformed

    private void botonGestionarPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGestionarPagosActionPerformed
        Pagos p = new Pagos();
        p.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_botonGestionarPagosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Donativos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonBuscarDonativo;
    private javax.swing.JButton botonBuscarGarantia;
    private javax.swing.JButton botonEditarDonativo;
    private javax.swing.JButton botonEliminarDonativo;
    private javax.swing.JButton botonGestionarPagos;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonRegistrar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JComboBox comboCorporacion;
    private javax.swing.JComboBox<String> comboMetodo;
    private javax.swing.JComboBox<String> comboNumPagos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JTable tablaHistorial;
    private javax.swing.JTextField txtCantidadGarantizada;
    private javax.swing.JTextField txtCantidadRecibida;
    private javax.swing.JTextField txtFechaGarantia;
    private javax.swing.JTextField txtFechaRegistro;
    private javax.swing.JTextField txtIdDonativo;
    private javax.swing.JTextField txtIdGarantia;
    private javax.swing.JTextField txtNombreDonador;
    private javax.swing.JTextField txtNumTDC;
    private javax.swing.JTextArea txtObservaciones;
    // End of variables declaration//GEN-END:variables
}
