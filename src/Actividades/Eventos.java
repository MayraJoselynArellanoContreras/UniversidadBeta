/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Actividades;

import VentanasPrincipales.MenuPrincipal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.awt.Color;
import util.ConexionBD;


/**
 *
 * @author contr
 */
public class Eventos extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Eventos.class.getName());

    /**
     * Creates new form Eventos
     */
    public Eventos() {
       initComponents();
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(new Color(245, 235, 204));
        cargarTiposEventoDesdeBD();
        cargarTablaEventos();
        configurarFecha();
        
        // Inicialmente deshabilitar botón de editar
        botonEditar.setEnabled(false);
        
        // Configurar doble clic en tabla
        tablaEventos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble clic
                    cargarEventoDesdeTabla();
                }
            }
        });
    }
    
    private void configurarFecha() {
        // Configurar formato de fecha
        try {
            txtFecha.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.MaskFormatter("####-##-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
   }
    
    private void cargarTiposEventoDesdeBD() {
        String sql = "SELECT DISTINCT tipo FROM Evento ORDER BY tipo";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            comboTipo.removeAllItems();
            comboTipo.addItem("Seleccione tipo...");
            
            while (rs.next()) {
                comboTipo.addItem(rs.getString("tipo"));
            }
            
            // Si no hay tipos en la BD, agregar los básicos
            if (comboTipo.getItemCount() == 1) {
                comboTipo.addItem("Carnaval");
                comboTipo.addItem("Cena Baile");
                comboTipo.addItem("Torneo de Golf");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar tipos: " + e.getMessage(), 
                "Error de base de datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            
            // En caso de error, cargar tipos por defecto
            comboTipo.removeAllItems();
            comboTipo.addItem("Seleccione tipo...");
            comboTipo.addItem("Carnaval");
            comboTipo.addItem("Cena Baile");
            comboTipo.addItem("Torneo de Golf");
        }
    }
    
    private void cargarTablaEventos() {
        String sql = """
            SELECT 
                idEvento,
                nombre,
                CONVERT(VARCHAR(10), fecha, 120) as fecha_str,
                tipo,
                lugar,
                descripcion,
                metaRecaudacion
            FROM Evento
            ORDER BY fecha DESC
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            javax.swing.table.DefaultTableModel modelo = 
                new javax.swing.table.DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Fecha", "Tipo", "Lugar", "Meta", "Descripción"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("idEvento"),
                    rs.getString("nombre"),
                    rs.getString("fecha_str"),
                    rs.getString("tipo"),
                    rs.getString("lugar"),
                    "$" + String.format("%,.2f", rs.getDouble("metaRecaudacion")),
                    rs.getString("descripcion")
                });
            }
            
            tablaEventos.setModel(modelo);
            
            // Ajustar anchos de columnas
            if (tablaEventos.getColumnCount() >= 7) {
                tablaEventos.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
                tablaEventos.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
                tablaEventos.getColumnModel().getColumn(2).setPreferredWidth(100);  // Fecha
                tablaEventos.getColumnModel().getColumn(3).setPreferredWidth(100);  // Tipo
                tablaEventos.getColumnModel().getColumn(4).setPreferredWidth(120);  // Lugar
                tablaEventos.getColumnModel().getColumn(5).setPreferredWidth(100);  // Meta
                tablaEventos.getColumnModel().getColumn(6).setPreferredWidth(200);  // Descripción
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar eventos: " + e.getMessage(), 
                "Error de base de datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarEventoDesdeTabla() {
        int fila = tablaEventos.getSelectedRow();
        if (fila == -1) {
            return;
        }
        
        int idEvento = (Integer) tablaEventos.getValueAt(fila, 0);
        buscarEventoPorID(idEvento);
    }
    
    private void buscarEventoPorID(int idEvento) {
        String sql = """
            SELECT 
                idEvento,
                nombre,
                fecha,
                tipo,
                lugar,
                descripcion,
                metaRecaudacion
            FROM Evento
            WHERE idEvento = ?
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idEvento);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                cargarDatosEnFormulario(rs);
                
                JOptionPane.showMessageDialog(this, 
                    "Evento cargado para edición.\nModifique y haga clic en 'Editar'.",
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
    
private void cargarDatosEnFormulario(ResultSet rs) throws SQLException {
    txtIdEvento.setText(String.valueOf(rs.getInt("idEvento")));
    txtNombreEvento.setText(rs.getString("nombre"));
    
    // Formatear fecha
    java.sql.Date fechaSQL = rs.getDate("fecha");
    if (fechaSQL != null) {
        txtFecha.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(fechaSQL));
    }
    
    // Seleccionar tipo
    String tipo = rs.getString("tipo");
    for (int i = 0; i < comboTipo.getItemCount(); i++) {
        if (comboTipo.getItemAt(i).equals(tipo)) {
            comboTipo.setSelectedIndex(i);
            break;
        }
    }
    
    txtLugar.setText(rs.getString("lugar"));
    txtDescripcion.setText(rs.getString("descripcion"));
    
    // Cargar recaudación (formateada como número)
    double metaRecaudacion = rs.getDouble("metaRecaudacion");
    txtRecaudacion.setText(String.valueOf(metaRecaudacion));
    
    // Habilitar botón de editar y deshabilitar agregar
    botonEditar.setEnabled(true);
    botonGuardarEvento.setEnabled(false);
}
    
private boolean validarCampos() {
    StringBuilder errores = new StringBuilder();
    boolean hayErrores = false;
    
    // Resetear colores
    txtNombreEvento.setBackground(Color.WHITE);
    txtFecha.setBackground(Color.WHITE);
    comboTipo.setBackground(Color.WHITE);
    txtLugar.setBackground(Color.WHITE);
    txtRecaudacion.setBackground(Color.WHITE); // ahora es JTextField
    
    // Validar nombre
    String nombre = txtNombreEvento.getText().trim();
    if (nombre.isEmpty()) {
        errores.append("• El nombre del evento es obligatorio\n");
        txtNombreEvento.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }
    
    // Validar fecha
    String fecha = txtFecha.getText().trim();
    if (fecha.isEmpty() || fecha.equals("____-__-__")) {
        errores.append("• La fecha es obligatoria\n");
        txtFecha.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(fecha);
        } catch (Exception e) {
            errores.append("• Formato de fecha inválido (YYYY-MM-DD)\n");
            txtFecha.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
    }
    
    // Validar tipo
    if (comboTipo.getSelectedIndex() <= 0) {
        errores.append("• Debe seleccionar un tipo de evento\n");
        comboTipo.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }
    
    // Validar lugar
    String lugar = txtLugar.getText().trim();
    if (lugar.isEmpty()) {
        errores.append("• El lugar es obligatorio\n");
        txtLugar.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }
    
    // Validar recaudación (ahora con JTextField normal)
    String recaudacionTexto = txtRecaudacion.getText().trim();
    if (recaudacionTexto.isEmpty()) {
        errores.append("• La meta de recaudación es obligatoria\n");
        txtRecaudacion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else {
        try {
            double metaRecaudacion = Double.parseDouble(recaudacionTexto);
            if (metaRecaudacion <= 0) {
                errores.append("• La meta de recaudación debe ser mayor a 0\n");
                txtRecaudacion.setBackground(new Color(255, 200, 200));
                hayErrores = true;
            }
        } catch (NumberFormatException e) {
            errores.append("• Meta de recaudación debe ser un número válido\n");
            txtRecaudacion.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
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
    
private void agregarEvento() {
    if (!validarCampos()) {
        return;
    }
    
    try {
        String nombre = txtNombreEvento.getText().trim();
        String fecha = txtFecha.getText().trim();
        String tipo = comboTipo.getSelectedItem().toString();
        String lugar = txtLugar.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        double metaRecaudacion = Double.parseDouble(txtRecaudacion.getText().trim());
        
        String sql = """
            INSERT INTO Evento 
            (nombre, fecha, tipo, lugar, descripcion, metaRecaudacion)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, nombre);
            ps.setDate(2, java.sql.Date.valueOf(fecha));
            ps.setString(3, tipo);
            ps.setString(4, lugar);
            ps.setString(5, descripcion.isEmpty() ? null : descripcion);
            ps.setDouble(6, metaRecaudacion);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet generatedKeys = ps.getGeneratedKeys();
                int idGenerado = 0;
                if (generatedKeys.next()) {
                    idGenerado = generatedKeys.getInt(1);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Evento registrado exitosamente!\n\n" +
                    "ID: " + idGenerado + "\n" +
                    "Nombre: " + nombre + "\n" +
                    "Fecha: " + fecha + "\n" +
                    "Tipo: " + tipo + "\n" +
                    "Meta: $" + String.format("%,.2f", metaRecaudacion),
                    "Registro exitoso", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTablaEventos();
                limpiarCampos();
            }
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al guardar en la base de datos:\n" + e.getMessage(), 
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, 
            "Formato de fecha inválido: " + e.getMessage(), 
            "Error de formato", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    
private void editarEvento() {
    String idTexto = txtIdEvento.getText().trim();
    if (idTexto.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Primero busque un evento para editar",
            "Advertencia", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    if (!validarCampos()) {
        return;
    }
    
    try {
        int idEvento = Integer.parseInt(idTexto);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de actualizar los datos del evento?\n\n" +
            "ID: " + idEvento + "\n" +
            "Nombre: " + txtNombreEvento.getText().trim() + "\n" +
            "Nueva meta: $" + txtRecaudacion.getText().trim(),
            "Confirmar actualización", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            actualizarEventoEnBD(idEvento);
        }
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, 
            "ID del evento no válido", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    
private void actualizarEventoEnBD(int idEvento) {
    String sql = """
        UPDATE Evento SET 
            nombre = ?, 
            fecha = ?, 
            tipo = ?, 
            lugar = ?, 
            descripcion = ?, 
            metaRecaudacion = ?
        WHERE idEvento = ?
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        String nombre = txtNombreEvento.getText().trim();
        String fecha = txtFecha.getText().trim();
        String tipo = comboTipo.getSelectedItem().toString();
        String lugar = txtLugar.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        double metaRecaudacion = Double.parseDouble(txtRecaudacion.getText().trim());
        
        ps.setString(1, nombre);
        ps.setDate(2, java.sql.Date.valueOf(fecha));
        ps.setString(3, tipo);
        ps.setString(4, lugar);
        ps.setString(5, descripcion.isEmpty() ? null : descripcion);
        ps.setDouble(6, metaRecaudacion);
        ps.setInt(7, idEvento);
        
        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(this, 
                "Evento actualizado exitosamente\n\n" +
                "ID: " + idEvento + "\n" +
                "Nombre: " + nombre + "\n" +
                "Fecha: " + fecha + "\n" +
                "Tipo: " + tipo + "\n" +
                "Meta actualizada: $" + String.format("%,.2f", metaRecaudacion),
                "Actualización exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            
            cargarTablaEventos();
            limpiarCampos();
            
            botonGuardarEvento.setEnabled(true);
            botonEditar.setEnabled(false);
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se pudo actualizar el evento\n" +
                "Verifique que el evento aún exista en la base de datos",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al actualizar en la base de datos:\n" + e.getMessage(),
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, 
            "Formato de fecha inválido: " + e.getMessage(), 
            "Error de formato", 
            JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void eliminarEvento() {
        int fila = tablaEventos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un evento de la tabla para eliminar",
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idEvento = (Integer) tablaEventos.getValueAt(fila, 0);
        String nombre = tablaEventos.getValueAt(fila, 1).toString();
        
        // Confirmar eliminación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el evento?\n\n" +
            "ID: " + idEvento + "\n" +
            "Nombre: " + nombre + "\n\n" +
            "Advertencia: Esta acción no se puede deshacer.",
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Evento WHERE idEvento = ?";
            
            try (Connection con = ConexionBD.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, idEvento);
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Evento eliminado exitosamente",
                        "Eliminación exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarTablaEventos();
                    limpiarCampos();
                    
                    // Restaurar estado de botones
                    botonGuardarEvento.setEnabled(true);
                    botonEditar.setEnabled(false);
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar: " + e.getMessage(),
                    "Error de base de datos", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void buscarEvento() {
        String criterio = JOptionPane.showInputDialog(this, 
            "Ingrese nombre, ID o tipo de evento a buscar:", 
            "Buscar Evento", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            buscarEventoPorCriterio(criterio.trim());
        }
    }
    
    private void buscarEventoPorCriterio(String criterio) {
        String sql = """
            SELECT 
                idEvento,
                nombre,
                fecha,
                tipo,
                lugar,
                descripcion,
                metaRecaudacion
            FROM Evento
            WHERE nombre LIKE ? OR tipo LIKE ? OR CAST(idEvento AS VARCHAR) = ?
            ORDER BY nombre
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String parametro = "%" + criterio + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            
            // Verificar si es un número (para búsqueda por ID)
            try {
                int id = Integer.parseInt(criterio);
                ps.setString(3, criterio);
            } catch (NumberFormatException e) {
                ps.setString(3, "");
            }
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                cargarDatosEnFormulario(rs);
                
                JOptionPane.showMessageDialog(this, 
                    "Evento encontrado\n\nID: " + rs.getInt("idEvento") + 
                    "\nNombre: " + rs.getString("nombre"),
                    "Búsqueda exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                botonGuardarEvento.setEnabled(false);
                botonEditar.setEnabled(true);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se encontró ningún evento con: " + criterio,
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
    
private void limpiarCampos() {
    txtIdEvento.setText("");
    txtNombreEvento.setText("");
    txtFecha.setText("____-__-__");  // o txtFecha.setValue(null);
    comboTipo.setSelectedIndex(0);
    txtLugar.setText("");
    txtDescripcion.setText("");
    txtRecaudacion.setText("");  // Ahora es JTextField normal
    
    // Resetear colores
    txtNombreEvento.setBackground(Color.WHITE);
    txtFecha.setBackground(Color.WHITE);
    comboTipo.setBackground(Color.WHITE);
    txtLugar.setBackground(Color.WHITE);
    txtRecaudacion.setBackground(Color.WHITE);
    
    botonGuardarEvento.setEnabled(true);
    botonEditar.setEnabled(false);
    
    tablaEventos.clearSelection();
    
    txtNombreEvento.requestFocus();
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombreEvento = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtFecha = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        botonEditar = new javax.swing.JButton();
        botonGuardarEvento = new javax.swing.JButton();
        botonEliminar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaEventos = new javax.swing.JTable();
        botonVolver = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        txtIdEvento = new javax.swing.JTextField();
        txtLugar = new javax.swing.JTextField();
        botonBuscar = new javax.swing.JButton();
        botonLimpiar = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        comboTipo = new javax.swing.JComboBox<>();
        txtRecaudacion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 153, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\phone-office.png")); // NOI18N
        jLabel1.setText("Gestion de eventos");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(238, 238, 238)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel2.setText("Nombre del evento:");

        jLabel3.setText("Fecha: ");

        txtFecha.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG))));

        jLabel4.setText("Lugar: ");

        jLabel5.setText("Descripcion:");

        jLabel6.setText("Meta de recaudacion: ");

        botonEditar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\edit.png")); // NOI18N
        botonEditar.setText("Editar evento");
        botonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEditarActionPerformed(evt);
            }
        });

        botonGuardarEvento.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\check.png")); // NOI18N
        botonGuardarEvento.setText("Agregar evento");
        botonGuardarEvento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarEventoActionPerformed(evt);
            }
        });

        botonEliminar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\cross.png")); // NOI18N
        botonEliminar.setText("Eliminar evento");
        botonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setText("Lista de eventos y total recaudado:");

        tablaEventos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaEventos);

        botonVolver.setBackground(new java.awt.Color(0, 153, 0));
        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        txtDescripcion.setColumns(20);
        txtDescripcion.setRows(5);
        jScrollPane2.setViewportView(txtDescripcion);

        jLabel8.setText("id evento:");

        txtIdEvento.setEditable(false);

        botonBuscar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\search-alt.png")); // NOI18N
        botonBuscar.setText("Buscar evento");
        botonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarActionPerformed(evt);
            }
        });

        botonLimpiar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\broom.png")); // NOI18N
        botonLimpiar.setText("Limpiar casillas");
        botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarActionPerformed(evt);
            }
        });

        jLabel9.setText("Tipo de evento:");

        jLabel10.setText("AAAA/MM/DD");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(18, 18, 18)
                        .addComponent(botonVolver))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(botonGuardarEvento)
                                        .addGap(57, 57, 57)
                                        .addComponent(botonEliminar)
                                        .addGap(49, 49, 49)
                                        .addComponent(botonBuscar))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(11, 11, 11)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel5)
                                                            .addComponent(jLabel4))
                                                        .addGap(58, 58, 58))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel3)
                                                        .addGap(87, 87, 87)))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel9)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(comboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtLugar, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(31, 31, 31)
                                                .addComponent(jLabel10))
                                            .addComponent(txtRecaudacion, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                                .addComponent(botonEditar))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel8))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(botonLimpiar))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNombreEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtIdEvento, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(18, 18, 18))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtIdEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombreEvento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonLimpiar)
                    .addComponent(jLabel9)
                    .addComponent(comboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtLugar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRecaudacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonGuardarEvento)
                    .addComponent(botonEditar)
                    .addComponent(botonEliminar)
                    .addComponent(botonBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonVolver, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolverActionPerformed
       MenuPrincipal menu = new MenuPrincipal();   
    menu.setVisible(true);
    this.dispose();  
    }//GEN-LAST:event_botonVolverActionPerformed

    private void botonGuardarEventoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGuardarEventoActionPerformed
        agregarEvento();
    }//GEN-LAST:event_botonGuardarEventoActionPerformed

    private void botonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarActionPerformed
        eliminarEvento();
    }//GEN-LAST:event_botonEliminarActionPerformed

    private void botonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarActionPerformed
        buscarEvento();
    }//GEN-LAST:event_botonBuscarActionPerformed

    private void botonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarActionPerformed
        editarEvento();
    }//GEN-LAST:event_botonEditarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Eventos().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonBuscar;
    private javax.swing.JButton botonEditar;
    private javax.swing.JButton botonEliminar;
    private javax.swing.JButton botonGuardarEvento;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JComboBox<String> comboTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JTable tablaEventos;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JFormattedTextField txtFecha;
    private javax.swing.JTextField txtIdEvento;
    private javax.swing.JTextField txtLugar;
    private javax.swing.JTextField txtNombreEvento;
    private javax.swing.JTextField txtRecaudacion;
    // End of variables declaration//GEN-END:variables
}
