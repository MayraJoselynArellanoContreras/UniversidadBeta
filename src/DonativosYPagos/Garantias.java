/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package DonativosYPagos;

import DonativosYPagos.DonativosYPagos;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
public class Garantias extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Garantias.class.getName());

    /**
     * Creates new form Donativos
     */
    public Garantias() {
  initComponents();
  setLocationRelativeTo(null);

java.util.List<java.awt.Component> ordenTab = java.util.Arrays.asList(
    txtIdGarantia,
    txtFechaGarantia,
    txtIdDonador,
    txtNombreDonador,
    txtCantidadGarantizada,
    txtCantidadEnviada,
    comboMetodoPago,
    txtNumTDC,
    comboNumeroPagos,
    comboCorporacion,
    txtDireccionCorporacion,
    botonRegistrar,
    botonEditarDonativo,     
    botonEliminarDonativo,
    botonBuscarDonativo,
    botonLimpiar,
    botonVolver,
    tablaGarantias
);

this.setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {
    @Override
    public java.awt.Component getComponentAfter(java.awt.Container aContainer, java.awt.Component aComponent) {
        int idx = ordenTab.indexOf(aComponent);
        return ordenTab.get((idx + 1) % ordenTab.size());
    }

    @Override
    public java.awt.Component getComponentBefore(java.awt.Container aContainer, java.awt.Component aComponent) {
        int idx = ordenTab.indexOf(aComponent);
        return ordenTab.get((idx - 1 + ordenTab.size()) % ordenTab.size());
    }

    @Override
    public java.awt.Component getDefaultComponent(java.awt.Container aContainer) {
        return ordenTab.get(0);
    }

    @Override
    public java.awt.Component getFirstComponent(java.awt.Container aContainer) {
        return ordenTab.get(0);
    }

    @Override
    public java.awt.Component getLastComponent(java.awt.Container aContainer) {
        return ordenTab.get(ordenTab.size() - 1);
    }
});


javax.swing.KeyStroke enter = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0);
javax.swing.KeyStroke tab = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, 0);

this.getRootPane().getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "tabFocus");
this.getRootPane().getActionMap().put("tabFocus", new javax.swing.AbstractAction() {
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        java.awt.Component c = javax.swing.FocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        java.awt.Component next = getFocusTraversalPolicy().getComponentAfter(getContentPane(), c);
        if (next != null) next.requestFocus();
    }
});

    cargarMetodosPago();
    cargarNumeroPagos();
    cargarCorporaciones();
    cargarTabla();
    cargarProximoIdGarantia();
    configurarPlaceholderFecha();
    habilitarCampoTarjeta();
    

    comboCorporacion.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            autocompletarDireccionCorporacion();
        }
    });
}


class CorporacionItem {
    private int id;
    private String nombre;
    private String direccion;
    
    public CorporacionItem(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion != null ? direccion : "";
    }
    
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    
    @Override
    public String toString() {
        return nombre;
    }
}

private void configurarPlaceholderFecha() {
    txtFechaGarantia.setText("dd/mm/aaaa");
    txtFechaGarantia.setForeground(Color.GRAY);
    txtFechaGarantia.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
            if (txtFechaGarantia.getText().equals("dd/mm/aaaa")) {
                txtFechaGarantia.setText("");
                txtFechaGarantia.setForeground(Color.BLACK);
            }
        }
        
        public void focusLost(FocusEvent e) {
            if (txtFechaGarantia.getText().isEmpty()) {
                txtFechaGarantia.setText("dd/mm/aaaa");
                txtFechaGarantia.setForeground(Color.GRAY);
            }
        }
    });
}

private void cargarMetodosPago() {
    comboMetodoPago.addItem("Seleccione método...");
    comboMetodoPago.addItem("Cheque");
    comboMetodoPago.addItem("Tarjeta de Crédito");
    comboMetodoPago.addItem("Transferencia");
    comboMetodoPago.addItem("Efectivo");
}

private void cargarNumeroPagos() {
    comboNumeroPagos.removeAllItems();
    comboNumeroPagos.addItem("Seleccione pagos...");
    for (int i = 1; i <= 12; i++) {
        comboNumeroPagos.addItem(String.valueOf(i));
    }
}

private void cargarCorporaciones() {
    comboCorporacion.removeAllItems();
    comboCorporacion.addItem(new CorporacionItem(0, "Seleccione corporación...", ""));
    
    String sql = "SELECT idCorporacion, nombre, direccion FROM Corporacion ORDER BY nombre";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            comboCorporacion.addItem(
                new CorporacionItem(
                    rs.getInt("idCorporacion"),
                    rs.getString("nombre"),
                    rs.getString("direccion")
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

private void autocompletarDireccionCorporacion() {
    Object selectedItem = comboCorporacion.getSelectedItem();
    if (selectedItem instanceof CorporacionItem) {
        CorporacionItem corporacion = (CorporacionItem) selectedItem;
        if (corporacion.getId() > 0 && corporacion.getDireccion() != null) {
            txtDireccionCorporacion.setText(corporacion.getDireccion());
        } else {
            txtDireccionCorporacion.setText("");
        }
    } else {
        txtDireccionCorporacion.setText("");
    }
}

private void cargarProximoIdGarantia() {
    try (Connection con = ConexionBD.getConexion()) {
        String sql = "SELECT ISNULL(IDENT_CURRENT('Garantia'), 0) + 1 AS siguiente";
        
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                int siguienteId = rs.getInt("siguiente");
                txtIdGarantia.setText(String.valueOf(siguienteId));
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener próximo ID: " + e.getMessage());
        txtIdGarantia.setText("");
    }
}

private void cargarTabla() {
    String sql = """
        SELECT 
            G.idGarantia,
            G.fechaGarantia,
            G.cantidadGarantizada,
            G.cantidadEnviada,
            G.metodoPago,
            G.numeroPagos,
            DO.nombre as donador,
            C.nombre as corporacion,
            G.direccionCorporacion
        FROM Garantia G
        LEFT JOIN Donador DO ON G.idDonador = DO.idDonador
        LEFT JOIN Corporacion C ON G.idCorporacion = C.idCorporacion
        ORDER BY G.idGarantia DESC
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        javax.swing.table.DefaultTableModel modelo = 
            new javax.swing.table.DefaultTableModel(
                new Object[]{"ID", "Fecha", "Garantía", "Recibido", "Método", 
                           "Pagos", "Donador", "Corporación", "Dirección"}, 0) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getInt("idGarantia"),
                rs.getString("fechaGarantia"),
                rs.getBigDecimal("cantidadGarantizada"),
                rs.getBigDecimal("cantidadEnviada"),
                rs.getString("metodoPago"),
                rs.getInt("numeroPagos"),
                rs.getString("donador"),
                rs.getString("corporacion"),
                rs.getString("direccionCorporacion"),
            });
        }
        
        tablaGarantias.setModel(modelo);
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error al cargar garantías: " + e.getMessage(),
            "Error BD",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void habilitarCampoTarjeta() {
    String metodo = comboMetodoPago.getSelectedItem().toString();
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
    

    String idDonador = txtIdDonador.getText().trim();
    if (idDonador.isEmpty() || txtNombreDonador.getText().trim().isEmpty()) {
        errores.append("• Debe buscar y seleccionar un donador\n");
        txtIdDonador.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }
    

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
    
    String cantidadR = txtCantidadEnviada.getText().trim();
    if (cantidadR.isEmpty()) {
        errores.append("• La cantidad recibida es obligatoria\n");
        txtCantidadEnviada.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else {
        try {
            BigDecimal valor = new BigDecimal(cantidadR);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                errores.append("• La cantidad debe ser mayor a 0\n");
                txtCantidadEnviada.setBackground(new Color(255, 200, 200));
                hayErrores = true;
            }
        } catch (NumberFormatException e) {
            errores.append("• Formato de cantidad inválido\n");
            txtCantidadEnviada.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
    }
    

    if (comboMetodoPago.getSelectedIndex() <= 0) {
        errores.append("• Debe seleccionar un método de pago\n");
        comboMetodoPago.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else {
        String metodo = comboMetodoPago.getSelectedItem().toString();
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
    
    if (comboNumeroPagos.getSelectedIndex() <= 0) {
        errores.append("• Debe seleccionar número de pagos\n");
        comboNumeroPagos.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }
    
    // Validar corporación
    if (comboCorporacion.getSelectedIndex() <= 0) {
        errores.append("• Debe seleccionar una corporación\n");
        comboCorporacion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }
    

    String direccion = txtDireccionCorporacion.getText().trim();
    if (direccion.isEmpty()) {
        errores.append("• La dirección de la corporación es obligatoria\n");
        txtDireccionCorporacion.setBackground(new Color(255, 200, 200));
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
            txtIdDonador.setText(rs.getString("idDonador"));
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

private void registrarGarantia() {
    if (!validarCampos()) {
        return;
    }
    
    String textoBoton = botonRegistrar.getText();
    
    if (textoBoton.equals("Registrar Garantía")) {
        guardarNuevaGarantia();
    } else if (textoBoton.equals("Actualizar Garantía")) {
        actualizarGarantia();
    } else {
        guardarNuevaGarantia();
    }
}

private void guardarNuevaGarantia() {
    if (!validarCampos()) {
        return;
    }
    
    Connection con = null;
    try {
        con = ConexionBD.getConexion();
        con.setAutoCommit(false);
        
        // 1. Insertar la garantía
        String sqlGarantia = """
            INSERT INTO Garantia (
                idDonador, fechaGarantia, cantidadGarantizada,
                cantidadEnviada, metodoPago, numeroPagos,
                numeroTarjeta, idCorporacion, direccionCorporacion
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        BigDecimal cantidadEnviada = new BigDecimal(txtCantidadEnviada.getText().trim());
        String idDonador = txtIdDonador.getText().trim();
        
        try (PreparedStatement ps = con.prepareStatement(sqlGarantia, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, idDonador);
            ps.setString(2, convertirFechaSQL(txtFechaGarantia.getText().trim()));
            ps.setBigDecimal(3, new BigDecimal(txtCantidadGarantizada.getText().trim()));
            ps.setBigDecimal(4, cantidadEnviada);
            ps.setString(5, comboMetodoPago.getSelectedItem().toString());
            ps.setInt(6, Integer.parseInt(comboNumeroPagos.getSelectedItem().toString()));
            
            if (comboMetodoPago.getSelectedItem().toString().equals("Tarjeta de Crédito")) {
                ps.setString(7, txtNumTDC.getText().trim());
            } else {
                ps.setString(7, null);
            }
            
            CorporacionItem corp = (CorporacionItem) comboCorporacion.getSelectedItem();
            ps.setInt(8, corp.getId());
            ps.setString(9, txtDireccionCorporacion.getText().trim());
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                int idGenerado = 0;
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                }
                
                // 2. ACTUALIZAR TOTAL DONADO Y CÍRCULO DEL DONADOR
                actualizarCirculoDonador(con, idDonador, cantidadEnviada);
                
                // Confirmar transacción
                con.commit();
                
                JOptionPane.showMessageDialog(this,
                    "✅ Garantía registrada exitosamente\n\n" +
                    "ID Garantía: " + idGenerado + "\n" +
                    "Donador: " + txtNombreDonador.getText() + "\n" +
                    "Monto: $" + String.format("%,.2f", cantidadEnviada) + "\n" +
                    "Corporación: " + corp.getNombre() + "\n" +
                    "Círculo actualizado automáticamente",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                limpiarCampos();
                cargarProximoIdGarantia();
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
            "Error al guardar garantía:\n" + e.getMessage(),
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
    
    // 3. Calcular y actualizar el círculo
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
}

private void actualizarGarantia() {
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Actualizar garantía ID: " + txtIdGarantia.getText() + "?\n" +
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
        
        // 1. Obtener el monto anterior de la garantía
        String sqlObtenerAnterior = """
            SELECT cantidadRecibida, idDonador 
            FROM Garantia 
            WHERE idGarantia = ?
            """;
        
        BigDecimal montoAnterior = BigDecimal.ZERO;
        String idDonador = "";
        
        try (PreparedStatement ps = con.prepareStatement(sqlObtenerAnterior)) {
            ps.setInt(1, Integer.parseInt(txtIdGarantia.getText().trim()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                montoAnterior = rs.getBigDecimal("cantidadRecibida");
                idDonador = rs.getString("idDonador");
            }
        }
        
        // 2. Actualizar la garantía
        BigDecimal nuevoMonto = new BigDecimal(txtCantidadEnviada.getText().trim());
        String sqlActualizar = """
            UPDATE Garantia SET
                idDonador = ?, fechaGarantia = ?, cantidadGarantizada = ?,
                cantidadRecibida = ?, metodoPago = ?, numeroPagos = ?,
                numeroTarjeta = ?, idCorporacion = ?, direccionCorporacion = ?, Observaciones = ?
            WHERE idGarantia = ?
            """;
        
        try (PreparedStatement ps = con.prepareStatement(sqlActualizar)) {
            ps.setString(1, txtIdDonador.getText().trim());
            ps.setString(2, convertirFechaSQL(txtFechaGarantia.getText().trim()));
            ps.setBigDecimal(3, new BigDecimal(txtCantidadGarantizada.getText().trim()));
            ps.setBigDecimal(4, nuevoMonto);
            ps.setString(5, comboMetodoPago.getSelectedItem().toString());
            ps.setInt(6, Integer.parseInt(comboNumeroPagos.getSelectedItem().toString()));
            
            if (comboMetodoPago.getSelectedItem().toString().equals("Tarjeta de Crédito")) {
                ps.setString(7, txtNumTDC.getText().trim());
            } else {
                ps.setString(7, null);
            }
            
            CorporacionItem corp = (CorporacionItem) comboCorporacion.getSelectedItem();
            ps.setInt(8, corp.getId());
            ps.setString(9, txtDireccionCorporacion.getText().trim());
            
            ps.setInt(10, Integer.parseInt(txtIdGarantia.getText().trim()));
            
            int filas = ps.executeUpdate();
            
            if (filas > 0 && !idDonador.isEmpty()) {
                // 3. Ajustar el total donado
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
                    "✅ Garantía actualizada exitosamente\n" +
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
            "Error al actualizar garantía:\n" + e.getMessage(),
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

private void eliminarGarantia() {
    int fila = tablaGarantias.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
            "Seleccione una garantía de la tabla para eliminar",
            "Selección requerida",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int idGarantia = (int) tablaGarantias.getValueAt(fila, 0);
    String donador = tablaGarantias.getValueAt(fila, 6).toString();
    
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Eliminar garantía ID: " + idGarantia + "?\n\n" +
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
        
        // 1. Obtener datos de la garantía antes de eliminar
        String sqlObtenerDatos = """
            SELECT cantidadRecibida, idDonador 
            FROM Garantia 
            WHERE idGarantia = ?
            """;
        
        BigDecimal montoEliminar = BigDecimal.ZERO;
        String idDonador = "";
        
        try (PreparedStatement ps = con.prepareStatement(sqlObtenerDatos)) {
            ps.setInt(1, idGarantia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                montoEliminar = rs.getBigDecimal("cantidadRecibida");
                idDonador = rs.getString("idDonador");
            }
        }
        
        // 2. Eliminar la garantía
        String sqlEliminar = "DELETE FROM Garantia WHERE idGarantia = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sqlEliminar)) {
            ps.setInt(1, idGarantia);
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
                    "✅ Garantía eliminada exitosamente\n" +
                    "Círculo del donador actualizado automáticamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTabla();
                cargarProximoIdGarantia();
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
            "Error al eliminar garantía:\n" + e.getMessage(),
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

private void buscarGarantia() {
    String criterio = JOptionPane.showInputDialog(this,
        "Ingrese ID de la garantía a buscar:",
        "Buscar Garantía",
        JOptionPane.QUESTION_MESSAGE);
    
    if (criterio != null && !criterio.trim().isEmpty()) {
        buscarGarantiaEnBD(criterio.trim());
    }
}

private void buscarGarantiaEnBD(String idGarantia) {
    String sql = """
        SELECT G.*, DO.nombre as nombreDonador, C.nombre as nombreCorporacion
        FROM Garantia G
        LEFT JOIN Donador DO ON G.idDonador = DO.idDonador
        LEFT JOIN Corporacion C ON G.idCorporacion = C.idCorporacion
        WHERE G.idGarantia = ?
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, Integer.parseInt(idGarantia));
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            cargarGarantiaEnFormulario(rs);
            
            JOptionPane.showMessageDialog(this,
                "Garantía encontrada\n\n" +
                "ID: " + rs.getInt("idGarantia") + "\n" +
                "Donador: " + rs.getString("nombreDonador"),
                "Búsqueda exitosa",
                JOptionPane.INFORMATION_MESSAGE);
            
            botonRegistrar.setText("Actualizar Garantía");
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

private void cargarGarantiaEnFormulario(ResultSet rs) throws SQLException {
    txtIdGarantia.setText(String.valueOf(rs.getInt("idGarantia")));
    txtIdDonador.setText(rs.getString("idDonador"));
    txtNombreDonador.setText(rs.getString("nombreDonador"));
    
    // Fecha
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
    txtCantidadEnviada.setText(rs.getBigDecimal("cantidadRecibida").toString());
    
    // Método de pago
    String metodo = rs.getString("metodoPago");
    for (int i = 0; i < comboMetodoPago.getItemCount(); i++) {
        if (comboMetodoPago.getItemAt(i).equals(metodo)) {
            comboMetodoPago.setSelectedIndex(i);
            break;
        }
    }
    
    // Número de pagos
    String numPagos = String.valueOf(rs.getInt("numeroPagos"));
    for (int i = 0; i < comboNumeroPagos.getItemCount(); i++) {
        if (comboNumeroPagos.getItemAt(i).equals(numPagos)) {
            comboNumeroPagos.setSelectedIndex(i);
            break;
        }
    }
    
    // Tarjeta
    String tarjeta = rs.getString("numeroTarjeta");
    if (tarjeta != null) {
        txtNumTDC.setText(tarjeta);
    }
    
    // Corporación y dirección
    int idCorp = rs.getInt("idCorporacion");
    for (int i = 0; i < comboCorporacion.getItemCount(); i++) {
        Object item = comboCorporacion.getItemAt(i);
        if (item instanceof CorporacionItem) {
            CorporacionItem corp = (CorporacionItem) item;
            if (corp.getId() == idCorp) {
                comboCorporacion.setSelectedIndex(i);
                // Autocompletar dirección
                txtDireccionCorporacion.setText(rs.getString("direccionCorporacion"));
                break;
            }
        }
    }
}
    
private void editarGarantiaDesdeTabla() {
    int fila = tablaGarantias.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
            "Seleccione una garantía de la tabla para editar",
            "Selección requerida",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Asumo que la primera columna (0) es idGarantia
    int idGarantia = (int) tablaGarantias.getValueAt(fila, 0);
    buscarGarantiaEnBD(String.valueOf(idGarantia));
}

private void limpiarCampos() {
    // Cargar próximo ID para nuevo registro
    cargarProximoIdGarantia();
    
    txtIdDonador.setText("");
    txtNombreDonador.setText("");
    txtFechaGarantia.setText("dd/mm/aaaa");
    txtFechaGarantia.setForeground(Color.GRAY);
    txtCantidadGarantizada.setText("");
    txtCantidadEnviada.setText("");
    txtNumTDC.setText(""); // Cambiado de txtNumTDC
    // txtObservaciones no existe en variables de BD
    
    comboMetodoPago.setSelectedIndex(0); // Cambiado de comboMetodo
    comboNumeroPagos.setSelectedIndex(0); // Cambiado de comboNumPagos
    comboCorporacion.setSelectedIndex(0);
    
    // Campo para dirección de corporación falta en UI
    // txtDireccionCorporacion debería existir
    
    txtNumTDC.setEnabled(false);
    txtNumTDC.setBackground(new Color(240, 240, 240));
    
    // Cambiar texto del botón
    botonRegistrar.setText("Registrar Garantía");
    tablaGarantias.clearSelection();
    
    // Restaurar colores de fondo
    txtIdDonador.setBackground(Color.WHITE);
    txtFechaGarantia.setBackground(Color.WHITE);
    txtCantidadGarantizada.setBackground(Color.WHITE);
    txtCantidadEnviada.setBackground(Color.WHITE);
    comboMetodoPago.setBackground(Color.WHITE);
    comboNumeroPagos.setBackground(Color.WHITE);
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
        tablaGarantias = new javax.swing.JTable();
        botonVolver = new javax.swing.JButton();
        botonEliminarDonativo = new javax.swing.JButton();
        botonBuscarDonador = new javax.swing.JButton();
        comboMetodoPago = new javax.swing.JComboBox<>();
        comboNumeroPagos = new javax.swing.JComboBox<>();
        txtNumTDC = new javax.swing.JTextField();
        botonBuscarDonativo = new javax.swing.JButton();
        botonEditarDonativo = new javax.swing.JButton();
        botonLimpiar = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtIdGarantia = new javax.swing.JTextField();
        comboCorporacion = new javax.swing.JComboBox();
        txtIdDonador = new javax.swing.JTextField();
        txtNombreDonador = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtFechaGarantia = new javax.swing.JTextField();
        txtCantidadGarantizada = new javax.swing.JTextField();
        txtCantidadEnviada = new javax.swing.JTextField();
        txtDireccionCorporacion = new javax.swing.JTextField();
        botonGarantiasPendientes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 204));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\folder-download.png")); // NOI18N
        jLabel1.setText("Registro de garantia");

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

        jLabel2.setText("Donador: ");

        jLabel3.setText("Fecha de la garantia:");

        jLabel4.setText("Cantidad garantizada:");

        jLabel5.setText("Cantidad enviada:");

        jLabel6.setText("Metodo de pago:");

        jLabel7.setText("Numero de pagos:");

        jLabel8.setText("Tarjeta de credito: ");

        jLabel9.setText("Corporacion que aporta: ");

        jLabel10.setText("Direccion de la corporacion: ");

        botonRegistrar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\check.png")); // NOI18N
        botonRegistrar.setText("Registrar Garantia");
        botonRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRegistrarActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setText("Historial de Garantias:");

        tablaGarantias.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaGarantias);

        botonVolver.setBackground(new java.awt.Color(102, 204, 0));
        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        botonEliminarDonativo.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\cross.png")); // NOI18N
        botonEliminarDonativo.setText("Eliminar Garantia");
        botonEliminarDonativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarDonativoActionPerformed(evt);
            }
        });

        botonBuscarDonador.setBackground(new java.awt.Color(204, 255, 153));
        botonBuscarDonador.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\chart-tree.png")); // NOI18N
        botonBuscarDonador.setText("Buscar Donador");
        botonBuscarDonador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarDonadorActionPerformed(evt);
            }
        });

        comboMetodoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMetodoPagoActionPerformed(evt);
            }
        });

        comboNumeroPagos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        botonBuscarDonativo.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\search-alt.png")); // NOI18N
        botonBuscarDonativo.setText("Buscar Garantia");
        botonBuscarDonativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarDonativoActionPerformed(evt);
            }
        });

        botonEditarDonativo.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\edit.png")); // NOI18N
        botonEditarDonativo.setText("Editar Garantia");
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

        jLabel12.setText("Id Garantia");

        txtIdGarantia.setEditable(false);

        txtIdDonador.setEditable(false);
        txtIdDonador.setText("id del donador");

        txtNombreDonador.setEditable(false);
        txtNombreDonador.setBackground(new java.awt.Color(255, 255, 255));
        txtNombreDonador.setText("Nombre del donador");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(153, 153, 153));
        jLabel13.setText("dd/mm/aaaa");

        txtDireccionCorporacion.setEditable(false);

        botonGarantiasPendientes.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\book-open-reader.png")); // NOI18N
        botonGarantiasPendientes.setText("Garantias Pendientes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel9)
                                    .addGap(18, 18, 18))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addGap(25, 25, 25)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel12))
                                .addGap(44, 44, 44)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtIdGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCantidadGarantizada, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboNumeroPagos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCantidadEnviada, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtFechaGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel13))
                                    .addComponent(txtNumTDC, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDireccionCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(botonEliminarDonativo, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(botonBuscarDonativo, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(botonEditarDonativo, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(botonLimpiar, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(botonGarantiasPendientes, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addComponent(botonRegistrar))
                                .addGap(28, 28, 28))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtIdDonador, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNombreDonador, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(botonBuscarDonador)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 879, Short.MAX_VALUE)))
                        .addGap(24, 24, 24)
                        .addComponent(botonVolver))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtIdGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdDonador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreDonador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonBuscarDonador))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtFechaGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(botonRegistrar))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(botonEliminarDonativo)
                            .addComponent(jLabel4)
                            .addComponent(txtCantidadGarantizada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtCantidadEnviada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(comboMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(botonEditarDonativo)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(comboNumeroPagos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(botonLimpiar)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(botonBuscarDonativo)
                                .addGap(84, 84, 84)))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(txtNumTDC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9)
                                    .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(botonGarantiasPendientes)
                                .addGap(33, 33, 33)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtDireccionCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addComponent(jLabel11)
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonVolver))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonBuscarDonadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarDonadorActionPerformed
  String idBuscado = JOptionPane.showInputDialog(
        this,
        "Ingrese el ID del donador:",
        "Buscar Donador",
        JOptionPane.QUESTION_MESSAGE
    );
    
    if (idBuscado != null) {
        idBuscado = idBuscado.trim();
        if (!idBuscado.isEmpty()) {
            buscarDonadorEnBD(idBuscado);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Debe ingresar un ID para buscar", 
                "ID vacío", 
                JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_botonBuscarDonadorActionPerformed

    
    private void botonVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolverActionPerformed
        DonativosYPagos d = new DonativosYPagos();
        d.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_botonVolverActionPerformed

    private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_botonLimpiarActionPerformed

    private void botonRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRegistrarActionPerformed
        registrarGarantia();
    }//GEN-LAST:event_botonRegistrarActionPerformed

    private void comboMetodoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMetodoPagoActionPerformed
        habilitarCampoTarjeta();
    }//GEN-LAST:event_comboMetodoPagoActionPerformed

    private void botonEliminarDonativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarDonativoActionPerformed
        eliminarGarantia();
    }//GEN-LAST:event_botonEliminarDonativoActionPerformed

    private void botonEditarDonativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarDonativoActionPerformed
       editarGarantiaDesdeTabla();
    }//GEN-LAST:event_botonEditarDonativoActionPerformed

    private void botonBuscarDonativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarDonativoActionPerformed
        buscarGarantia();
    }//GEN-LAST:event_botonBuscarDonativoActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Garantias().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonBuscarDonador;
    private javax.swing.JButton botonBuscarDonativo;
    private javax.swing.JButton botonEditarDonativo;
    private javax.swing.JButton botonEliminarDonativo;
    private javax.swing.JButton botonGarantiasPendientes;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonRegistrar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JComboBox comboCorporacion;
    private javax.swing.JComboBox<String> comboMetodoPago;
    private javax.swing.JComboBox<String> comboNumeroPagos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private java.awt.MenuBar menuBar1;
    private javax.swing.JTable tablaGarantias;
    private javax.swing.JTextField txtCantidadEnviada;
    private javax.swing.JTextField txtCantidadGarantizada;
    private javax.swing.JTextField txtDireccionCorporacion;
    private javax.swing.JTextField txtFechaGarantia;
    private javax.swing.JTextField txtIdDonador;
    private javax.swing.JTextField txtIdGarantia;
    private javax.swing.JTextField txtNombreDonador;
    private javax.swing.JTextField txtNumTDC;
    // End of variables declaration//GEN-END:variables
}
