/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Actividades;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.text.SimpleDateFormat;
import util.ConexionBD;
/**
 *
 * @author contr
 */
public class Voluntarios extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Voluntarios.class.getName());

    /**
     * Creates new form Voluntarios
     */
    public Voluntarios() {
  initComponents();
        setLocationRelativeTo(null);
        cargarTablaVoluntarios();
        configurarFechaRegistro();
        
java.util.List<java.awt.Component> ordenTab = java.util.Arrays.asList(
    txtNombre,
    txtTelefono,
    txtEmail,
    txtDireccion,
    txtFechaRegistro,
    chkEstudiante,
    chkActivo,
    txtObservaciones,
    botonAgregar,
    botonEditar,
    botonEliminar,
    botonBuscar,
    botonLimpiar,
    botonVolver,
    tablaVoluntarios
);

setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {

    @Override
    public java.awt.Component getComponentAfter(java.awt.Container container, java.awt.Component comp) {
        int idx = ordenTab.indexOf(comp);
        return ordenTab.get((idx + 1) % ordenTab.size());
    }

    @Override
    public java.awt.Component getComponentBefore(java.awt.Container container, java.awt.Component comp) {
        int idx = ordenTab.indexOf(comp);
        return ordenTab.get((idx - 1 + ordenTab.size()) % ordenTab.size());
    }

    @Override
    public java.awt.Component getFirstComponent(java.awt.Container container) {
        return ordenTab.get(0);
    }

    @Override
    public java.awt.Component getLastComponent(java.awt.Container container) {
        return ordenTab.get(ordenTab.size() - 1);
    }

    @Override
    public java.awt.Component getDefaultComponent(java.awt.Container container) {
        return ordenTab.get(0);
    }
});

        botonEditar.setEnabled(false);

        tablaVoluntarios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble clic
                    cargarVoluntarioDesdeTabla();
                }
            }
        });
    }
    
    private void configurarFechaRegistro() {

        try {
            txtFechaRegistro.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.MaskFormatter("####-##-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
    }
    
    private void cargarTablaVoluntarios() {
        String sql = """
            SELECT 
                idVoluntario,
                nombre,
                telefono,
                email,
                direccion,
                fechaRegistro,
                CASE WHEN estudiante = 1 THEN 'Sí' ELSE 'No' END as esEstudiante,
                CASE WHEN activo = 1 THEN 'Activo' ELSE 'Inactivo' END as estado,
                observaciones
            FROM Voluntario
            ORDER BY fechaRegistro DESC, nombre
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            javax.swing.table.DefaultTableModel modelo = 
                new javax.swing.table.DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Teléfono", "Email", "Dirección", "Fecha Registro", "Estudiante", "Estado", "Observaciones"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("idVoluntario"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("email"),
                    rs.getString("direccion"),
                    rs.getDate("fechaRegistro"),
                    rs.getString("esEstudiante"),
                    rs.getString("estado"),
                    rs.getString("observaciones")
                });
            }
            
            tablaVoluntarios.setModel(modelo);
            

            if (tablaVoluntarios.getColumnCount() >= 9) {
                tablaVoluntarios.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
                tablaVoluntarios.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
                tablaVoluntarios.getColumnModel().getColumn(2).setPreferredWidth(100);  // Teléfono
                tablaVoluntarios.getColumnModel().getColumn(3).setPreferredWidth(150);  // Email
                tablaVoluntarios.getColumnModel().getColumn(4).setPreferredWidth(180);  // Dirección
                tablaVoluntarios.getColumnModel().getColumn(5).setPreferredWidth(100);  // Fecha
                tablaVoluntarios.getColumnModel().getColumn(6).setPreferredWidth(70);   // Estudiante
                tablaVoluntarios.getColumnModel().getColumn(7).setPreferredWidth(70);   // Estado
                tablaVoluntarios.getColumnModel().getColumn(8).setPreferredWidth(200);  // Observaciones
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar voluntarios: " + e.getMessage(), 
                "Error de base de datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarVoluntarioDesdeTabla() {
        int fila = tablaVoluntarios.getSelectedRow();
        if (fila == -1) {
            return;
        }
        
        int idVoluntario = (Integer) tablaVoluntarios.getValueAt(fila, 0);
        buscarVoluntarioPorID(idVoluntario);
    }
    
    private void buscarVoluntarioPorID(int idVoluntario) {
        String sql = """
            SELECT 
                idVoluntario,
                nombre,
                telefono,
                email,
                direccion,
                fechaRegistro,
                estudiante,
                activo,
                observaciones
            FROM Voluntario
            WHERE idVoluntario = ?
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idVoluntario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                cargarDatosEnFormulario(rs);
                
                JOptionPane.showMessageDialog(this, 
                    "Voluntario cargado para edición.\nModifique y haga clic en 'Editar'.",
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
        txtIdVoluntario.setText(String.valueOf(rs.getInt("idVoluntario")));
        txtNombre.setText(rs.getString("nombre"));
        txtTelefono.setText(rs.getString("telefono"));
        txtEmail.setText(rs.getString("email"));
        txtDireccion.setText(rs.getString("direccion"));
        
        // Formatear fecha
        java.sql.Date fechaSQL = rs.getDate("fechaRegistro");
        if (fechaSQL != null) {
            txtFechaRegistro.setText(new SimpleDateFormat("yyyy-MM-dd").format(fechaSQL));
        }
        
        // Establecer checkbox de estudiante
        chkEstudiante.setSelected(rs.getBoolean("estudiante"));
        
        // Establecer checkbox de activo
        chkActivo.setSelected(rs.getBoolean("activo"));
        
        txtObservaciones.setText(rs.getString("observaciones"));
        
        // Habilitar botón de editar y deshabilitar agregar
        botonEditar.setEnabled(true);
        botonAgregar.setEnabled(false);
    }
    
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        boolean hayErrores = false;
        
        txtNombre.setBackground(Color.WHITE);
        txtTelefono.setBackground(Color.WHITE);
        txtEmail.setBackground(Color.WHITE);
        txtFechaRegistro.setBackground(Color.WHITE);
        
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            errores.append("• El nombre es obligatorio\n");
            txtNombre.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
        
        // Validar teléfono
        String telefono = txtTelefono.getText().trim();
        if (telefono.isEmpty() || telefono.equals("(   )    -")) {
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
        
        String email = txtEmail.getText().trim();
        if (!email.isEmpty()) {
            if (!email.contains("@") || !email.contains(".") || email.length() < 5) {
                errores.append("• Email no tiene formato válido\n");
                txtEmail.setBackground(new Color(255, 200, 200));
                hayErrores = true;
            }
        }
        
        String direccion = txtDireccion.getText().trim();
        if (direccion.isEmpty()) {
            errores.append("• La direccion es obligatoria\n");
            txtDireccion.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        } 
        
        String fecha = txtFechaRegistro.getText().trim();
        if (fecha.isEmpty() || fecha.equals("____-__-__")) {
            errores.append("• La fecha de registro es obligatoria\n");
            txtFechaRegistro.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                sdf.parse(fecha);
            } catch (Exception e) {
                errores.append("• Formato de fecha inválido (YYYY-MM-DD)\n");
                txtFechaRegistro.setBackground(new Color(255, 200, 200));
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
    
    private void agregarVoluntario() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim().replaceAll("[^0-9]", "");
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String fechaRegistro = txtFechaRegistro.getText().trim();
            boolean estudiante = chkEstudiante.isSelected();
            boolean activo = chkActivo.isSelected();
            String observaciones = txtObservaciones.getText().trim();
            
            String sql = """
                INSERT INTO Voluntario 
                (nombre, telefono, email, direccion, fechaRegistro, estudiante, activo, observaciones)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
            
            try (Connection con = ConexionBD.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                ps.setString(1, nombre);
                ps.setString(2, telefono);
                ps.setString(3, email.isEmpty() ? null : email);
                ps.setString(4, direccion.isEmpty() ? null : direccion);
                ps.setDate(5, java.sql.Date.valueOf(fechaRegistro));
                ps.setBoolean(6, estudiante);
                ps.setBoolean(7, activo);
                ps.setString(8, observaciones.isEmpty() ? null : observaciones);
                
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Obtener el ID generado
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    int idGenerado = 0;
                    if (generatedKeys.next()) {
                        idGenerado = generatedKeys.getInt(1);
                    }
                    
                    JOptionPane.showMessageDialog(this, 
                        "Voluntario registrado exitosamente!\n\n" +
                        "ID: " + idGenerado + "\n" +
                        "Nombre: " + nombre + "\n" +
                        "Teléfono: " + telefono + "\n" +
                        "Tipo: " + (estudiante ? "Estudiante" : "Voluntario General"),
                        "Registro exitoso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarTablaVoluntarios();
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
    
    private void editarVoluntario() {
        String idTexto = txtIdVoluntario.getText().trim();
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Primero busque un voluntario para editar",
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validarCampos()) {
            return;
        }
        
        try {
            int idVoluntario = Integer.parseInt(idTexto);
            
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de actualizar los datos del voluntario?\n\n" +
                "ID: " + idVoluntario + "\n" +
                "Nombre: " + txtNombre.getText().trim(),
                "Confirmar actualización", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                actualizarVoluntarioEnBD(idVoluntario);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "ID del voluntario no válido", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarVoluntarioEnBD(int idVoluntario) {
        String sql = """
            UPDATE Voluntario SET 
                nombre = ?, 
                telefono = ?, 
                email = ?, 
                direccion = ?, 
                fechaRegistro = ?, 
                estudiante = ?, 
                activo = ?, 
                observaciones = ?
            WHERE idVoluntario = ?
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String nombre = txtNombre.getText().trim();
            String telefono = txtTelefono.getText().trim().replaceAll("[^0-9]", "");
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String fechaRegistro = txtFechaRegistro.getText().trim();
            boolean estudiante = chkEstudiante.isSelected();
            boolean activo = chkActivo.isSelected();
            String observaciones = txtObservaciones.getText().trim();
            
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, direccion.isEmpty() ? null : direccion);
            ps.setDate(5, java.sql.Date.valueOf(fechaRegistro));
            ps.setBoolean(6, estudiante);
            ps.setBoolean(7, activo);
            ps.setString(8, observaciones.isEmpty() ? null : observaciones);
            ps.setInt(9, idVoluntario);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Voluntario actualizado exitosamente\n\n" +
                    "ID: " + idVoluntario + "\n" +
                    "Nombre: " + nombre + "\n" +
                    "Estado: " + (activo ? "Activo" : "Inactivo"),
                    "Actualización exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTablaVoluntarios();
                limpiarCampos();
                
                botonAgregar.setEnabled(true);
                botonEditar.setEnabled(false);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo actualizar el voluntario\n" +
                    "Verifique que el voluntario aún exista en la base de datos",
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
    
    private void eliminarVoluntario() {
        int fila = tablaVoluntarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un voluntario de la tabla para eliminar",
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idVoluntario = (Integer) tablaVoluntarios.getValueAt(fila, 0);
        String nombre = tablaVoluntarios.getValueAt(fila, 1).toString();
        
        // Confirmar eliminación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al voluntario?\n\n" +
            "ID: " + idVoluntario + "\n" +
            "Nombre: " + nombre + "\n\n" +
            "Advertencia: Esta acción no se puede deshacer.",
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Voluntario WHERE idVoluntario = ?";
            
            try (Connection con = ConexionBD.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setInt(1, idVoluntario);
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Voluntario eliminado exitosamente",
                        "Eliminación exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarTablaVoluntarios();
                    limpiarCampos();
                    
                    // Restaurar estado de botones
                    botonAgregar.setEnabled(true);
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
    
    private void buscarVoluntario() {
        String criterio = JOptionPane.showInputDialog(this, 
            "Ingrese nombre, ID o teléfono del voluntario a buscar:", 
            "Buscar Voluntario", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (criterio != null && !criterio.trim().isEmpty()) {
            buscarVoluntarioPorCriterio(criterio.trim());
        }
    }
    
    private void buscarVoluntarioPorCriterio(String criterio) {
        String sql = """
            SELECT 
                idVoluntario,
                nombre,
                telefono,
                email,
                direccion,
                fechaRegistro,
                estudiante,
                activo,
                observaciones
            FROM Voluntario
            WHERE nombre LIKE ? OR telefono LIKE ? OR CAST(idVoluntario AS VARCHAR) = ?
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
                    "Voluntario encontrado\n\nID: " + rs.getInt("idVoluntario") + 
                    "\nNombre: " + rs.getString("nombre"),
                    "Búsqueda exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                botonAgregar.setEnabled(false);
                botonEditar.setEnabled(true);
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se encontró ningún voluntario con: " + criterio,
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
        txtIdVoluntario.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        txtFechaRegistro.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        chkEstudiante.setSelected(false);
        chkActivo.setSelected(true);
        txtObservaciones.setText("");
        
        // Resetear colores
        txtNombre.setBackground(Color.WHITE);
        txtTelefono.setBackground(Color.WHITE);
        txtEmail.setBackground(Color.WHITE);
        txtFechaRegistro.setBackground(Color.WHITE);
        
        botonAgregar.setEnabled(true);
        botonEditar.setEnabled(false);
        
        tablaVoluntarios.clearSelection();
        
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtIdVoluntario = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtFechaRegistro = new javax.swing.JFormattedTextField();
        chkEstudiante = new javax.swing.JCheckBox();
        chkActivo = new javax.swing.JCheckBox();
        botonAgregar = new javax.swing.JButton();
        botonEliminar = new javax.swing.JButton();
        botonBuscar = new javax.swing.JButton();
        botonEditar = new javax.swing.JButton();
        botonLimpiar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaVoluntarios = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtObservaciones = new javax.swing.JTextArea();
        botonVolver = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 204));
        jPanel1.setForeground(new java.awt.Color(102, 178, 255));

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 24)); // NOI18N
        jLabel1.setText("Gestión de Voluntarios");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(147, 147, 147))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("id Voluntario:");

        txtIdVoluntario.setEditable(false);

        jLabel3.setText("Nombre:");

        jLabel4.setText("Telefono:");

        try {
            txtTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##) ###-###-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel5.setText("E-mail");

        jLabel6.setText("Direccion:");

        txtDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDireccionActionPerformed(evt);
            }
        });

        jLabel7.setText("Fecha de registro:");

        try {
            txtFechaRegistro.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("YYYY-MM-DD")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        chkEstudiante.setText("Es estudiante?");
        chkEstudiante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEstudianteActionPerformed(evt);
            }
        });

        chkActivo.setText("Activo");

        botonAgregar.setText("Agregar Voluntario");
        botonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAgregarActionPerformed(evt);
            }
        });

        botonEliminar.setText("Eliminar Voluntario");
        botonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarActionPerformed(evt);
            }
        });

        botonBuscar.setText("Buscar Voluntario");
        botonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarActionPerformed(evt);
            }
        });

        botonEditar.setText("Editar Voluntario");
        botonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEditarActionPerformed(evt);
            }
        });

        botonLimpiar.setText("Limpiar Casillas");

        tablaVoluntarios.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tablaVoluntarios);

        jLabel8.setFont(new java.awt.Font("Yu Gothic", 1, 24)); // NOI18N
        jLabel8.setText("Tabla de Voluntarios:");

        jLabel9.setText("Observaciones/Notas:");

        txtObservaciones.setColumns(20);
        txtObservaciones.setRows(5);
        jScrollPane3.setViewportView(txtObservaciones);

        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(botonEditar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(botonEliminar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
                                .addComponent(botonBuscar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtIdVoluntario, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(botonAgregar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chkEstudiante, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(chkActivo, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonLimpiar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botonVolver))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdVoluntario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonAgregar))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonEliminar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(botonBuscar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(botonEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(botonLimpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonVolver))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkActivo)
                            .addComponent(chkEstudiante))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkEstudianteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEstudianteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkEstudianteActionPerformed

    private void botonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAgregarActionPerformed
        agregarVoluntario();
    }//GEN-LAST:event_botonAgregarActionPerformed

    private void botonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarActionPerformed
        editarVoluntario();
    }//GEN-LAST:event_botonEditarActionPerformed

    private void botonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarActionPerformed
         eliminarVoluntario();
    }//GEN-LAST:event_botonEliminarActionPerformed

    private void botonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarActionPerformed
      buscarVoluntario();
    }//GEN-LAST:event_botonBuscarActionPerformed

    private void txtDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDireccionActionPerformed

    private void botonVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolverActionPerformed
       actividades a = new actividades();
       a.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_botonVolverActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Voluntarios().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAgregar;
    private javax.swing.JButton botonBuscar;
    private javax.swing.JButton botonEditar;
    private javax.swing.JButton botonEliminar;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JCheckBox chkActivo;
    private javax.swing.JCheckBox chkEstudiante;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tablaVoluntarios;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JFormattedTextField txtFechaRegistro;
    private javax.swing.JTextField txtIdVoluntario;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextArea txtObservaciones;
    private javax.swing.JFormattedTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
