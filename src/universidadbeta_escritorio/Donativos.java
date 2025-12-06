/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package universidadbeta_escritorio;

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
public class Donativos extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Donativos.class.getName());

    /**
     * Creates new form Donativos
     */
    public Donativos() {
           initComponents();
        cargarMetodosPago();
        cargarNumeroPagos();
        cargarCorporaciones();
        cargarTabla();
        cargarProximoIdDonativo();
        configurarPlaceholderFecha();
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
    
    private void configurarPlaceholderFecha() {
        txtFechaRegistro.setText("dd/mm/aaaa");
        txtFechaRegistro.setForeground(Color.GRAY);
        txtFechaRegistro.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {
                if (txtFechaRegistro.getText().equals("dd/mm/aaaa")) {
                    txtFechaRegistro.setText("");
                    txtFechaRegistro.setForeground(Color.BLACK);
                }
            }
            

            public void focusLost(FocusEvent e) {
                if (txtFechaRegistro.getText().isEmpty()) {
                    txtFechaRegistro.setText("dd/mm/aaaa");
                    txtFechaRegistro.setForeground(Color.GRAY);
                }
            }
        });
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
                D.Observaciones
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
                    new Object[]{"ID", "Fecha", "Garantía", "Recibido", "Método", "Pagos", "Donador", "Corporación", "Observaciones"}, 0) {
                
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
                    rs.getString("Observaciones")
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
        String idDonador = txtIdDonador.getText().trim();
        if (idDonador.isEmpty() || txtNombreDonador.getText().trim().isEmpty()) {
            errores.append("• Debe buscar y seleccionar un donador\n");
            txtIdDonador.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
        
        // Validar fecha
        String fecha = txtFechaRegistro.getText().trim();
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
    
    private void registrarDonativo() {
        if (!validarCampos()) {
            return;
        }
        
        String idStr = txtIdDonativo.getText().trim();
        boolean esEdicion = !idStr.isEmpty() && idStr.matches("\\d+");
        
        if (esEdicion) {
            actualizarDonativo();
        } else {
            guardarNuevoDonativo();
        }
    }
    
    private void guardarNuevoDonativo() {
        try {
            String sql = """
                INSERT INTO Donativo (
                    idDonador, fechaGarantia, cantidadGarantizada,
                    cantidadRecibida, metodoPago, numeroPagos,
                    numeroTarjeta, idCorporacion, Observaciones
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            
            try (Connection con = ConexionBD.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                // Parámetros
                ps.setString(1, txtIdDonador.getText().trim());
                ps.setString(2, convertirFechaSQL(txtFechaRegistro.getText().trim()));
                ps.setBigDecimal(3, new BigDecimal(txtCantidadGarantizada.getText().trim()));
                ps.setBigDecimal(4, new BigDecimal(txtCantidadRecibida.getText().trim()));
                ps.setString(5, comboMetodo.getSelectedItem().toString());
                ps.setInt(6, Integer.parseInt(comboNumPagos.getSelectedItem().toString()));
                
                // Número de tarjeta
                if (comboMetodo.getSelectedItem().toString().equals("Tarjeta de Crédito")) {
                    ps.setString(7, txtNumTDC.getText().trim());
                } else {
                    ps.setString(7, null);
                }
                
                // Corporación
                CorporacionItem corp = (CorporacionItem) comboCorporacion.getSelectedItem();
                ps.setInt(8, corp.getId());
                
                // Observaciones
                String observaciones = txtObservaciones.getText().trim();
                ps.setString(9, observaciones.isEmpty() ? null : observaciones);
                
                int filas = ps.executeUpdate();
                
                if (filas > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        
                        JOptionPane.showMessageDialog(this,
                            "Donativo registrado exitosamente\n\n" +
                            "ID: " + idGenerado + "\n" +
                            "Donador: " + txtNombreDonador.getText() + "\n" +
                            "Monto: $" + txtCantidadRecibida.getText(),
                            "Registro exitoso",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        limpiarCampos();
                        cargarTabla();
                    }
                }
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar donativo:\n" + e.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarDonativo() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Actualizar donativo ID: " + txtIdDonativo.getText() + "?",
            "Confirmar actualización",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            String sql = """
                UPDATE Donativo SET
                    idDonador = ?, fechaGarantia = ?, cantidadGarantizada = ?,
                    cantidadRecibida = ?, metodoPago = ?, numeroPagos = ?,
                    numeroTarjeta = ?, idCorporacion = ?, Observaciones = ?
                WHERE idDonativo = ?
                """;
            
            try (Connection con = ConexionBD.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                // Parámetros
                ps.setString(1, txtIdDonador.getText().trim());
                ps.setString(2, convertirFechaSQL(txtFechaRegistro.getText().trim()));
                ps.setBigDecimal(3, new BigDecimal(txtCantidadGarantizada.getText().trim()));
                ps.setBigDecimal(4, new BigDecimal(txtCantidadRecibida.getText().trim()));
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
                
                if (filas > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Donativo actualizado exitosamente",
                        "Actualización exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    limpiarCampos();
                    cargarTabla();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al actualizar donativo:\n" + e.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
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
            "Esta acción no se puede deshacer",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        String sql = "DELETE FROM Donativo WHERE idDonativo = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idDonativo);
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                JOptionPane.showMessageDialog(this,
                    "Donativo eliminado exitosamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTabla();
                cargarProximoIdDonativo();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al eliminar donativo:\n" + e.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarDonativo() {
        String criterio = JOptionPane.showInputDialog(this,
            "Ingrese ID del donativo a buscar:",
            "Buscar Donativo",
            JOptionPane.QUESTION_MESSAGE);
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            buscarDonativoEnBD(criterio.trim());
        }
    }
    
    private void buscarDonativoEnBD(String idDonativo) {
        String sql = """
            SELECT D.*, DO.nombre as nombreDonador, C.nombre as nombreCorporacion
            FROM Donativo D
            LEFT JOIN Donador DO ON D.idDonador = DO.idDonador
            LEFT JOIN Corporacion C ON D.idCorporacion = C.idCorporacion
            WHERE D.idDonativo = ?
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(idDonativo));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                cargarDonativoEnFormulario(rs);
                
                JOptionPane.showMessageDialog(this,
                    "Donativo encontrado\n\n" +
                    "ID: " + rs.getInt("idDonativo") + "\n" +
                    "Donador: " + rs.getString("nombreDonador"),
                    "Búsqueda exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                botonRegistrar.setText("Actualizar Donativo");
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se encontró donativo con ID: " + idDonativo,
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
        txtIdDonativo.setText(String.valueOf(rs.getInt("idDonativo")));
        txtIdDonador.setText(rs.getString("idDonador"));
        txtNombreDonador.setText(rs.getString("nombreDonador"));
        
        // Fecha
        String fechaBD = rs.getString("fechaGarantia");
        if (fechaBD != null) {
            try {
                SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat salida = new SimpleDateFormat("dd/MM/yyyy");
                txtFechaRegistro.setText(salida.format(entrada.parse(fechaBD)));
                txtFechaRegistro.setForeground(Color.BLACK);
            } catch (Exception e) {
                txtFechaRegistro.setText(fechaBD);
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
        buscarDonativoEnBD(String.valueOf(idDonativo));
    }
    
    private void limpiarCampos() {
        cargarProximoIdDonativo();
        txtIdDonador.setText("");
        txtNombreDonador.setText("");
        txtFechaRegistro.setText("dd/mm/aaaa");
        txtFechaRegistro.setForeground(Color.GRAY);
        txtCantidadGarantizada.setText("");
        txtCantidadRecibida.setText("");
        txtNumTDC.setText("");
        txtObservaciones.setText("");
        
        comboMetodo.setSelectedIndex(0);
        comboNumPagos.setSelectedIndex(0);
        comboCorporacion.setSelectedIndex(0);
        
        txtNumTDC.setEnabled(false);
        txtNumTDC.setBackground(new Color(240, 240, 240));
        
        botonRegistrar.setText("Registrar Donativo");
        tablaHistorial.clearSelection();
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
        botonVolver = new javax.swing.JButton();
        botonEliminarDonativo = new javax.swing.JButton();
        botonBuscarDonador = new javax.swing.JButton();
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
        txtIdDonador = new javax.swing.JTextField();
        txtNombreDonador = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtFechaRegistro = new javax.swing.JTextField();
        txtCantidadGarantizada = new javax.swing.JTextField();
        txtCantidadRecibida = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 204));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\folder-download.png")); // NOI18N
        jLabel1.setText("Registro de donativo/garantia");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(167, 167, 167))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("Donador: ");

        jLabel3.setText("Fecha de registro:");

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

        botonVolver.setBackground(new java.awt.Color(102, 204, 0));
        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.setText("Volver");
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        botonEliminarDonativo.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\cross.png")); // NOI18N
        botonEliminarDonativo.setText("Eliminar Donativo");
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

        txtIdDonador.setEditable(false);

        txtNombreDonador.setEditable(false);

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(153, 153, 153));
        jLabel13.setText("dd/mm/aaaa");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(58, 58, 58)
                                        .addComponent(txtFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(12, 12, 12)
                        .addComponent(botonVolver))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addComponent(botonRegistrar))
                                    .addComponent(jLabel10))
                                .addGap(18, 18, 18)
                                .addComponent(botonEliminarDonativo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                .addComponent(botonBuscarDonativo)
                                .addGap(18, 18, 18)
                                .addComponent(botonEditarDonativo))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(botonLimpiar))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(98, 98, 98)
                                        .addComponent(txtIdDonador, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtNombreDonador))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel9)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel5)
                                                    .addComponent(jLabel4)
                                                    .addComponent(jLabel8))
                                                .addGap(36, 36, 36)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtNumTDC, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(comboMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(comboNumPagos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(txtIdDonativo, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(35, 35, 35)
                                                        .addComponent(jLabel13))
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(txtCantidadGarantizada, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtCantidadRecibida, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)))))
                                        .addGap(0, 50, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)
                                .addComponent(botonBuscarDonador)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtIdDonativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(botonBuscarDonador)
                    .addComponent(txtIdDonador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreDonador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel13)
                    .addComponent(txtFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCantidadGarantizada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCantidadRecibida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonLimpiar)
                    .addComponent(jLabel6)
                    .addComponent(comboMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(comboNumPagos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtNumTDC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonRegistrar)
                    .addComponent(botonEliminarDonativo)
                    .addComponent(botonBuscarDonativo)
                    .addComponent(botonEditarDonativo))
                .addGap(16, 16, 16)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonVolver)))
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
        MenuPrincipal menu = new MenuPrincipal();   
    menu.setVisible(true);
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
    private javax.swing.JButton botonBuscarDonador;
    private javax.swing.JButton botonBuscarDonativo;
    private javax.swing.JButton botonEditarDonativo;
    private javax.swing.JButton botonEliminarDonativo;
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
    private javax.swing.JScrollPane jScrollPane2;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JTable tablaHistorial;
    private javax.swing.JTextField txtCantidadGarantizada;
    private javax.swing.JTextField txtCantidadRecibida;
    private javax.swing.JTextField txtFechaRegistro;
    private javax.swing.JTextField txtIdDonador;
    private javax.swing.JTextField txtIdDonativo;
    private javax.swing.JTextField txtNombreDonador;
    private javax.swing.JTextField txtNumTDC;
    private javax.swing.JTextArea txtObservaciones;
    // End of variables declaration//GEN-END:variables
}
