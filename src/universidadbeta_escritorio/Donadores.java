/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package universidadbeta_escritorio;

import java.awt.Color;
import util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author contr
 */
public class Donadores extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Donadores.class.getName());

    /**
     * Creates new form Donadores
     */
    public Donadores() {
        initComponents();
        cargarCategorias();
        cargarCorporaciones();
        cargarTabla();
        configurarspinnerAñoGraduacion();
        
        spinnerAñoGraduacion.setEnabled(false);
    spinnerAñoGraduacion.setBackground(Color.LIGHT_GRAY);
     botonEditar.setEnabled(false);
    
    tablaListaDonadores.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() == 2) { // Doble clic
            cargarDonadorDesdeTabla();
        }
    }
});
    }
    
private void configurarspinnerAñoGraduacion() {
     int añoActual = java.time.Year.now().getValue();
    
    javax.swing.SpinnerNumberModel model = new javax.swing.SpinnerNumberModel(
        añoActual,          // Valor inicial
        1990,               // Mínimo
        añoActual,          // Máximo
        1                   // Incremento
    );
    
    model.setValue(Integer.valueOf(añoActual));
    
    spinnerAñoGraduacion.setModel(model);
    

    javax.swing.JSpinner.NumberEditor editor = new javax.swing.JSpinner.NumberEditor(
        spinnerAñoGraduacion, 
        "####"  
    );
    
    spinnerAñoGraduacion.setEditor(editor);
    editor.getTextField().setHorizontalAlignment(javax.swing.JTextField.CENTER);
}
    
private void cargarDonadorDesdeTabla() {
    int fila = tablaListaDonadores.getSelectedRow();
    if (fila == -1) {
        return;
    }
    
    String idDonador = tablaListaDonadores.getValueAt(fila, 0).toString();
    buscarDonadorPorID(idDonador);
}

private void buscarDonadorPorID(String idDonador) {
    String sql = """
        SELECT 
            D.idDonador,
            D.nombre,
            D.telefono,
            D.email,
            D.direccion,
            D.anioGraduacion,
            D.conyuge,
            D.idCategoria,
            D.idCorporacion
        FROM Donador D
        WHERE D.idDonador = ?
        """;

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, idDonador);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            cargarDatosEnFormulario(rs);
            
            JOptionPane.showMessageDialog(this, 
                "Datos cargados para edición.\nModifique y haga clic en 'Editar'.",
                "Modo edición", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar datos: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    
private void cargarCategorias() {
    String sql = "SELECT idCategoria, nombre FROM CategoriaDonador ORDER BY nombre";

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        combCategoria.removeAllItems();
        combCategoria.addItem(new CategoriaItem(0, "Seleccione categoría..."));

        while (rs.next()) {
            int id = rs.getInt("idCategoria");
            String nombre = rs.getString("nombre");
            combCategoria.addItem(new CategoriaItem(id, nombre));
        }

        // Agregar listener para controlar el spinner de año
        combCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controlarSpinnerGraduacion();
            }
        });

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + e.getMessage());
        e.printStackTrace();
    }
}

private void controlarSpinnerGraduacion() {
    CategoriaItem categoria = (CategoriaItem) combCategoria.getSelectedItem();
    if (categoria != null && categoria.getNombre().toLowerCase().contains("graduado")) {
        spinnerAñoGraduacion.setEnabled(true);
        spinnerAñoGraduacion.setBackground(Color.WHITE);
    } else {
        spinnerAñoGraduacion.setEnabled(false);
        spinnerAñoGraduacion.setBackground(Color.LIGHT_GRAY);
        spinnerAñoGraduacion.setValue(0); 
    }
}
 
private boolean validarCamposObligatorios() {
    StringBuilder errores = new StringBuilder();
    boolean hayErrores = false;
    
    txtNombre.setBackground(Color.WHITE);
    txtDireccion.setBackground(Color.WHITE);
    combCategoria.setBackground(Color.WHITE);
    txtTelefono.setBackground(Color.WHITE);
    txtCorreo.setBackground(Color.WHITE);
    comboCorporacion.setBackground(Color.WHITE);
    txtIdDonador.setBackground(Color.WHITE);

    String idDonador = txtIdDonador.getText().trim();
    if (idDonador.isEmpty() || idDonador.contains("Presiona") || idDonador.contains("DON")) {
        errores.append("• Debe generar el ID del donador primero\n");
        txtIdDonador.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }


    String nombre = txtNombre.getText().trim();
    if (nombre.isEmpty()) {
        errores.append("• El nombre es obligatorio\n");
        txtNombre.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }


    String direccion = txtDireccion.getText().trim();
    if (direccion.isEmpty()) {
        errores.append("• La dirección es obligatoria\n");
        txtDireccion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }


    if (combCategoria.getSelectedIndex() <= 0) {
        errores.append("• Debe seleccionar una categoría\n");
        combCategoria.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else {

        CategoriaItem categoria = (CategoriaItem) combCategoria.getSelectedItem();
        if (categoria.getNombre().toLowerCase().contains("alumno")) {
            int año = (Integer) spinnerAñoGraduacion.getValue();
            if (año <= 1900 || año > java.time.Year.now().getValue()) {
                errores.append("• Año de graduación inválido para alumno\n");
                spinnerAñoGraduacion.setBackground(new Color(255, 200, 200));
                hayErrores = true;
            }
        }
    }


    String telefono = txtTelefono.getText().trim();
    if (telefono.isEmpty() || telefono.equals("(  )    -")) {
        errores.append("• El teléfono es obligatorio\n");
        txtTelefono.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else {

        String soloNumeros = telefono.replaceAll("[^0-9]", "");
        if (soloNumeros.length() < 8) {
            errores.append("• Teléfono debe tener al menos 8 dígitos\n");
            txtTelefono.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
    }


    String email = txtCorreo.getText().trim();
    if (!email.isEmpty()) {
        if (!email.contains("@") || !email.contains(".") || email.length() < 5) {
            errores.append("• Email no tiene formato válido\n");
            txtCorreo.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
    }


    if (comboCorporacion.getSelectedIndex() <= 0) {
        errores.append("• Debe seleccionar una corporación\n");
        comboCorporacion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }


    if (hayErrores) {
        JOptionPane.showMessageDialog(this, 
            "Por favor complete los siguientes campos:\n\n" + errores.toString(), 
            "Campos incompletos", 
            JOptionPane.WARNING_MESSAGE);
        return false;
    }
    
    return true;
}

 
    private void cargarCorporaciones() {
    String sql = "SELECT idCorporacion, nombre FROM Corporacion ORDER BY nombre";

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        comboCorporacion.removeAllItems();
        comboCorporacion.addItem(new CorporacionItem(0, "Seleccione corporación...")); // Opción por defecto

        while(rs.next()) {
            comboCorporacion.addItem(
                new CorporacionItem(
                    rs.getInt("idCorporacion"),
                    rs.getString("nombre")
                )
            );
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar corporaciones: " + e.getMessage());
        e.printStackTrace();
    }
}

    // Carga la tabla de donadores
private void cargarTabla() {

    String sql = """
        SELECT 
            D.idDonador,
            D.nombre,
            D.telefono,
            D.email,
            D.direccion,
            ISNULL(C.nombre, 'Sin categoría') as categoria,
            ISNULL(CORP.nombre, 'Sin corporación') as corporacion,
            D.idCategoria,
            D.idCorporacion
        FROM Donador D
        LEFT JOIN CategoriaDonador C ON D.idCategoria = C.idCategoria
        LEFT JOIN Corporacion CORP ON D.idCorporacion = CORP.idCorporacion
        ORDER BY D.nombre
        """;

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {


        javax.swing.table.DefaultTableModel modelo = 
            new javax.swing.table.DefaultTableModel(
                new Object[]{"ID", "Nombre", "Teléfono", "Email", "Dirección", "Categoría", "Corporación"}, 0) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getString("idDonador"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("direccion"),
                rs.getString("categoria"),
                rs.getString("corporacion")
            });
        }

        tablaListaDonadores.setModel(modelo);
        

        if (tablaListaDonadores.getColumnCount() >= 7) {
            tablaListaDonadores.getColumnModel().getColumn(0).setPreferredWidth(80);   // ID
            tablaListaDonadores.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
            tablaListaDonadores.getColumnModel().getColumn(2).setPreferredWidth(100);  // Teléfono
            tablaListaDonadores.getColumnModel().getColumn(3).setPreferredWidth(150);  // Email
            tablaListaDonadores.getColumnModel().getColumn(4).setPreferredWidth(200);  // Dirección
            tablaListaDonadores.getColumnModel().getColumn(5).setPreferredWidth(100);  // Categoría
            tablaListaDonadores.getColumnModel().getColumn(6).setPreferredWidth(150);  // Corporación
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar donadores: " + e.getMessage(), 
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
     private String generarIdDonador(String nombreCompleto) {
        String limpio = nombreCompleto.trim().toUpperCase()
            .replaceAll("[ÁÀÄÂ]", "A")
            .replaceAll("[ÉÈËÊ]", "E")
            .replaceAll("[ÍÌÏÎ]", "I")
            .replaceAll("[ÓÒÖÔ]", "O")
            .replaceAll("[ÚÙÜÛ]", "U")
            .replaceAll("[^A-Z\\s]", " ");
        String[] partes = limpio.split("\\s+");
        String iniciales = "DON";

        if (partes.length >= 3) {
            iniciales = "" + partes[0].charAt(0) + partes[1].charAt(0) + partes[2].charAt(0);
        } else if (partes.length == 2) {
            iniciales = "" + partes[0].charAt(0) + partes[1].charAt(0);
        } else if (partes.length == 1 && partes[0].length() >= 3) {
            iniciales = partes[0].substring(0, 3);
        }

        String año = String.format("%02d", java.time.Year.now().getValue() % 100);
        String prefijo = iniciales + año;

    
        String sql = """
            SELECT ISNULL(MAX(CAST(RIGHT(idDonador, 3) AS INT)), 0) + 1 AS siguiente
            FROM Donador
            WHERE idDonador LIKE ?
            """;

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, prefijo + "-%");
            ResultSet rs = ps.executeQuery();

            int siguiente = 1;
            if (rs.next()) {
                siguiente = rs.getInt("siguiente");
            }

            return prefijo + "-" + String.format("%03d", siguiente);

        } catch (SQLException e) {
            e.printStackTrace();
            return prefijo + "-001"; // por si falla la conexión
        }
    }
     

  private void agregarDonador() {

    if (!validarCamposObligatorios()) {
        return;
    }

    try {

        String idDonador = txtIdDonador.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        CategoriaItem categoria = (CategoriaItem) combCategoria.getSelectedItem();

        int añoGraduacion;
        if (categoria.getNombre().toLowerCase().contains("alumno")) {
            añoGraduacion = (Integer) spinnerAñoGraduacion.getValue();
        } else {
            añoGraduacion = 0;
        }
        

        String telefono = txtTelefono.getText().trim().replaceAll("[^0-9]", "");
        String email = txtCorreo.getText().trim();
        String conyuge = txtConyuge.getText().trim();
        

        CorporacionItem corporacion = (CorporacionItem) comboCorporacion.getSelectedItem();
        

        String sql = """
            INSERT INTO Donador 
            (idDonador, nombre, direccion, idCategoria, anioGraduacion, 
             telefono, email, idCorporacion, conyuge)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, idDonador);
            ps.setString(2, nombre);
            ps.setString(3, direccion);
            ps.setInt(4, categoria.getId());
            

            if (añoGraduacion > 0) {
                ps.setInt(5, añoGraduacion);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            
            ps.setString(6, telefono);
            ps.setString(7, email.isEmpty() ? null : email);
            ps.setInt(8, corporacion.getId());
            ps.setString(9, conyuge.isEmpty() ? null : conyuge);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Donador registrado exitosamente!\n\n" +
                    "ID: " + idDonador + "\n" +
                    "Nombre: " + nombre + "\n" +
                    "Categoría: " + categoria.getNombre(), 
                    "Registro exitoso", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTabla();
                limpiarCampos();
                txtIdDonador.setText("Presiona Generar ID");
                txtIdDonador.setForeground(Color.GRAY);
            }
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "❌ Error al guardar en la base de datos:\n" + e.getMessage(), 
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void eliminarDonador() {
    int fila = tablaListaDonadores.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, 
            "Seleccione un donador de la tabla para eliminar",
            "Selección requerida", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    String idDonador = tablaListaDonadores.getValueAt(fila, 0).toString();
    String nombre = tablaListaDonadores.getValueAt(fila, 1).toString();
    
    // Confirmar eliminación
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Está seguro de eliminar al donador?\n\n" +
        "ID: " + idDonador + "\n" +
        "Nombre: " + nombre + "\n\n" +
        "⚠️ Advertencia: Esta acción no se puede deshacer.",
        "Confirmar eliminación", 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.WARNING_MESSAGE);
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        String sql = "DELETE FROM Donador WHERE idDonador = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, idDonador);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Donador eliminado exitosamente",
                    "Eliminación exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                

                cargarTabla();
                limpiarCampos();
                
                // Restaurar estado de botones
                botonAgregar.setEnabled(true);
                botonEditar.setEnabled(false);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "❌ Error al eliminar: " + e.getMessage(),
                "Error de base de datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}


private boolean tieneDonativosAsociados(String idDonador) {
    String sql = "SELECT COUNT(*) FROM Donativo WHERE idDonador = ?";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, idDonador);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return false;
}


private boolean tienePagosAsociados(String idDonador) {
    String sql = "SELECT COUNT(*) FROM Pago WHERE idDonador = ?";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, idDonador);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return false;
}

private void buscarDonador(String criterio) {
    String sql = """
        SELECT 
            D.idDonador,
            D.nombre,
            D.telefono,
            D.email,
            D.direccion,
            D.anioGraduacion,
            D.conyuge,
            D.idCategoria,
            D.idCorporacion,
            C.nombre as nombreCategoria,
            CORP.nombre as nombreCorporacion
        FROM Donador D
        LEFT JOIN CategoriaDonador C ON D.idCategoria = C.idCategoria
        LEFT JOIN Corporacion CORP ON D.idCorporacion = CORP.idCorporacion
        WHERE D.nombre LIKE ? OR D.idDonador LIKE ? OR D.telefono LIKE ?
        ORDER BY D.nombre
        """;

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        String parametro = "%" + criterio + "%";
        ps.setString(1, parametro);
        ps.setString(2, parametro);
        ps.setString(3, parametro);
        
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {

            cargarDatosEnFormulario(rs);
            
            JOptionPane.showMessageDialog(this, 
                "✅ Donador encontrado\n\nID: " + rs.getString("idDonador") + 
                "\nNombre: " + rs.getString("nombre"),
                "Búsqueda exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            

            botonAgregar.setEnabled(false);
            botonEditar.setEnabled(true);
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se encontró ningún donador con: " + criterio,
                "Sin resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error en búsqueda: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private void cargarDatosEnFormulario(ResultSet rs) throws SQLException {

    txtIdDonador.setText(rs.getString("idDonador"));
    txtNombre.setText(rs.getString("nombre"));
    txtDireccion.setText(rs.getString("direccion"));
    txtTelefono.setText(rs.getString("telefono"));
    txtCorreo.setText(rs.getString("email"));
    txtConyuge.setText(rs.getString("conyuge"));
    

    int año = rs.getInt("anioGraduacion");
    if (!rs.wasNull()) {
        spinnerAñoGraduacion.setValue(año);
    }
    

    int idCategoria = rs.getInt("idCategoria");
    seleccionarItemEnComboBox(combCategoria, idCategoria);
    
    // Seleccionar corporación en ComboBox
    int idCorporacion = rs.getInt("idCorporacion");
    seleccionarItemEnComboBox(comboCorporacion, idCorporacion);

    txtIdDonador.setForeground(new Color(0, 100, 0)); 
    txtIdDonador.setBackground(new Color(220, 255, 220));
}

private void seleccionarItemEnComboBox(javax.swing.JComboBox comboBox, int id) {
    for (int i = 0; i < comboBox.getItemCount(); i++) {
        Object item = comboBox.getItemAt(i);
        if (item instanceof CategoriaItem) {
            CategoriaItem catItem = (CategoriaItem) item;
            if (catItem.getId() == id) {
                comboBox.setSelectedIndex(i);
                break;
            }
        } else if (item instanceof CorporacionItem) {
            CorporacionItem corpItem = (CorporacionItem) item;
            if (corpItem.getId() == id) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }
}

private void editarDonador() {

    String idDonador = txtIdDonador.getText().trim();
    if (idDonador.isEmpty() || idDonador.contains("Presiona") || idDonador.contains("DON")) {
        JOptionPane.showMessageDialog(this, 
            "Primero busque un donador para editar",
            "Advertencia", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Validar campos obligatorios
    if (!validarCamposObligatorios()) {
        return;
    }
    

    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Está seguro de actualizar los datos del donador?\n\n" +
        "ID: " + idDonador + "\n" +
        "Nombre: " + txtNombre.getText().trim(),
        "Confirmar actualización", 
        JOptionPane.YES_NO_OPTION);
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        actualizarDonadorEnBD(idDonador);
    }
}

private void actualizarDonadorEnBD(String idDonador) {
    String sql = """
        UPDATE Donador SET 
            nombre = ?, 
            direccion = ?, 
            idCategoria = ?, 
            anioGraduacion = ?, 
            telefono = ?, 
            email = ?, 
            idCorporacion = ?, 
            conyuge = ?
        WHERE idDonador = ?
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        

        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        CategoriaItem categoria = (CategoriaItem) combCategoria.getSelectedItem();
        int añoGraduacion = (Integer) spinnerAñoGraduacion.getValue();
        String telefono = txtTelefono.getText().trim().replaceAll("[^0-9]", "");
        String email = txtCorreo.getText().trim();
        String conyuge = txtConyuge.getText().trim();
        CorporacionItem corporacion = (CorporacionItem) comboCorporacion.getSelectedItem();
        

        ps.setString(1, nombre);
        ps.setString(2, direccion);
        ps.setInt(3, categoria.getId());
        

        if (añoGraduacion > 0) {
            ps.setInt(4, añoGraduacion);
        } else {
            ps.setNull(4, java.sql.Types.INTEGER);
        }
        
        ps.setString(5, telefono);
        ps.setString(6, email.isEmpty() ? null : email);
        

        if (corporacion != null && corporacion.getId() > 0) {
            ps.setInt(7, corporacion.getId());
        } else {
            ps.setNull(7, java.sql.Types.INTEGER);
        }
        
        ps.setString(8, conyuge.isEmpty() ? null : conyuge);
        ps.setString(9, idDonador);
        
        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(this, 
                "✅ Donador actualizado exitosamente",
                "Actualización exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Actualizar tabla y limpiar
            cargarTabla();
            limpiarCampos();
            

            botonAgregar.setEnabled(true);
            botonEditar.setEnabled(false);
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se pudo actualizar el donador",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "❌ Error al actualizar: " + e.getMessage(),
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void limpiarCampos() {
    txtIdDonador.setText("Presiona Generar ID");
    txtIdDonador.setForeground(Color.GRAY);
    txtIdDonador.setBackground(Color.WHITE);
    
    txtNombre.setText("");
    txtNombre.setBackground(Color.WHITE);
    
    txtDireccion.setText("");
    txtDireccion.setBackground(Color.WHITE);
    
    combCategoria.setSelectedIndex(0);
    combCategoria.setBackground(Color.WHITE);
    
    spinnerAñoGraduacion.setValue(2025);
    if (spinnerAñoGraduacion.isEnabled()) {
        spinnerAñoGraduacion.setBackground(Color.WHITE);
    } else {
        spinnerAñoGraduacion.setBackground(Color.LIGHT_GRAY);
    }
    
    txtTelefono.setText("");
    txtTelefono.setBackground(Color.WHITE);
    
    txtCorreo.setText("");
    txtCorreo.setBackground(Color.WHITE);
    
    if (comboCorporacion != null) {
        comboCorporacion.setSelectedIndex(0);
        comboCorporacion.setBackground(Color.WHITE);
    }
    
    txtConyuge.setText("");
    

    botonAgregar.setEnabled(true);
    botonEditar.setEnabled(false);
    

    tablaListaDonadores.clearSelection();
    

    txtNombre.requestFocus();
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        botonBuscar = new javax.swing.JButton();
        botonEditar = new javax.swing.JButton();
        botonEliminar = new javax.swing.JButton();
        botonLimpiar = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        combCategoria = new javax.swing.JComboBox();
        spinnerAñoGraduacion = new javax.swing.JSpinner();
        txtTelefono = new javax.swing.JFormattedTextField();
        txtCorreo = new javax.swing.JTextField();
        txtConyuge = new javax.swing.JTextField();
        botonAgregar = new javax.swing.JButton();
        botonVolver = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtIdDonador = new javax.swing.JTextField();
        botonGenerarId = new javax.swing.JButton();
        comboCorporacion = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaListaDonadores = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 102));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel1.setBackground(new java.awt.Color(204, 204, 0));

        jLabel2.setFont(new java.awt.Font("Yu Gothic", 1, 24)); // NOI18N
        jLabel2.setText("Datos del Donador");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(199, 199, 199)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("Nombre: ");

        jLabel3.setText("Direccion: ");

        jLabel6.setText("Categoria: ");

        jLabel7.setText("Año graduacion: ");

        jLabel8.setText("Telefono:");

        jLabel9.setText("E-mail: ");

        jLabel10.setText("Corporacion: ");

        jLabel11.setText("Conyuge: ");

        botonBuscar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\search-alt.png")); // NOI18N
        botonBuscar.setText("Buscar");
        botonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarActionPerformed(evt);
            }
        });

        botonEditar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\edit.png")); // NOI18N
        botonEditar.setText("Editar");
        botonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEditarActionPerformed(evt);
            }
        });

        botonEliminar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\cross.png")); // NOI18N
        botonEliminar.setText("Eliminar");
        botonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarActionPerformed(evt);
            }
        });

        botonLimpiar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\broom.png")); // NOI18N
        botonLimpiar.setText("Limpiar Casillas");
        botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Yu Gothic Medium", 1, 18)); // NOI18N
        jLabel12.setText("Lista de donadores: ");

        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        combCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combCategoriaActionPerformed(evt);
            }
        });

        try {
            txtTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##) ###-###-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        botonAgregar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\check.png")); // NOI18N
        botonAgregar.setText("Agregar");
        botonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAgregarActionPerformed(evt);
            }
        });

        botonVolver.setBackground(new java.awt.Color(51, 204, 0));
        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.setText("Volver");
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        jLabel14.setText("Id del donador: ");

        txtIdDonador.setEditable(false);

        botonGenerarId.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\rotate-right.png")); // NOI18N
        botonGenerarId.setText("Generar Id");
        botonGenerarId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGenerarIdActionPerformed(evt);
            }
        });

        tablaListaDonadores.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaListaDonadores);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonVolver))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel14)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(50, 50, 50)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(combCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtIdDonador, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(38, 38, 38)
                                                .addComponent(botonGenerarId))
                                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(spinnerAñoGraduacion, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(70, 70, 70)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtConyuge, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(botonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(botonAgregar)))
                                .addComponent(botonEliminar, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(botonEditar)
                                .addComponent(botonBuscar)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(botonAgregar)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtIdDonador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(botonGenerarId))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(combCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(botonEliminar)
                        .addGap(18, 18, 18)
                        .addComponent(botonBuscar)))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(spinnerAñoGraduacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonEditar))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGap(35, 35, 35))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(botonLimpiar)
                        .addGap(16, 16, 16)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtConyuge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(botonVolver))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void combCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combCategoriaActionPerformed

    private void botonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarActionPerformed
          String criterio = JOptionPane.showInputDialog(this, 
        "Ingrese nombre, ID o teléfono a buscar:", 
        "Buscar Donador", 
        JOptionPane.QUESTION_MESSAGE);
    
    if (criterio != null && !criterio.trim().isEmpty()) {
        buscarDonador(criterio.trim());
    }
    }//GEN-LAST:event_botonBuscarActionPerformed
          
    private void botonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarActionPerformed
        editarDonador();
    }//GEN-LAST:event_botonEditarActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void botonVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolverActionPerformed
       MenuPrincipal menu = new MenuPrincipal();   
    menu.setVisible(true);
    this.dispose();      
    }//GEN-LAST:event_botonVolverActionPerformed

    private void botonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarActionPerformed
        eliminarDonador();
    }//GEN-LAST:event_botonEliminarActionPerformed

    private void botonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAgregarActionPerformed
        agregarDonador();
    }//GEN-LAST:event_botonAgregarActionPerformed

    private void botonGenerarIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGenerarIdActionPerformed
       String nombre = txtNombre.getText().trim();

    if (nombre.isEmpty() || nombre.length() < 4) {
        JOptionPane.showMessageDialog(this, 
            "Escribe el nombre completo del donador (mínimo 4 letras)\n" +
            "antes de generar el ID", 
            "Nombre requerido", 
            JOptionPane.WARNING_MESSAGE);
        txtNombre.requestFocus();
        txtNombre.setBackground(new Color(255, 200, 200));
        return;
    }

    String idGenerado = generarIdDonador(nombre);
    txtIdDonador.setText(idGenerado);
    txtIdDonador.setForeground(new Color(0, 100, 0)); // Verde oscuro
    txtIdDonador.setFont(new java.awt.Font("Consolas", java.awt.Font.BOLD, 14));
    txtIdDonador.setBackground(new Color(220, 255, 220)); // Fondo verde claro

    JOptionPane.showMessageDialog(this, 
        "✅ ID generado correctamente!\n\n" + idGenerado, 
        "ID Generado", 
        JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_botonGenerarIdActionPerformed

    private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLimpiarActionPerformed
       limpiarCampos();
    }//GEN-LAST:event_botonLimpiarActionPerformed

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
    java.awt.EventQueue.invokeLater(() -> new Donadores().setVisible(true));
        

    }
    
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAgregar;
    private javax.swing.JButton botonBuscar;
    private javax.swing.JButton botonEditar;
    private javax.swing.JButton botonEliminar;
    private javax.swing.JButton botonGenerarId;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JComboBox combCategoria;
    private javax.swing.JComboBox comboCorporacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spinnerAñoGraduacion;
    private javax.swing.JTable tablaListaDonadores;
    private javax.swing.JTextField txtConyuge;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtIdDonador;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JFormattedTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
class CategoriaItem {
    private int id;
    private String nombre;

    public CategoriaItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

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
