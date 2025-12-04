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
        
        spinnerAñoGraduacion.setEnabled(false);
    spinnerAñoGraduacion.setBackground(Color.LIGHT_GRAY);
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

// Método para controlar si el spinner está habilitado
private void controlarSpinnerGraduacion() {
    CategoriaItem categoria = (CategoriaItem) combCategoria.getSelectedItem();
    if (categoria != null && categoria.getNombre().toLowerCase().contains("alumno")) {
        spinnerAñoGraduacion.setEnabled(true);
        spinnerAñoGraduacion.setBackground(Color.WHITE);
    } else {
        spinnerAñoGraduacion.setEnabled(false);
        spinnerAñoGraduacion.setBackground(Color.LIGHT_GRAY);
        spinnerAñoGraduacion.setValue(0); // O un valor por defecto
    }
}
 
private boolean validarCamposObligatorios() {
    StringBuilder errores = new StringBuilder();
    boolean hayErrores = false;
    
    // Resetear colores de fondo
    txtNombre.setBackground(Color.WHITE);
    txtDireccion.setBackground(Color.WHITE);
    combCategoria.setBackground(Color.WHITE);
    txtTelefono.setBackground(Color.WHITE);
    txtCorreo.setBackground(Color.WHITE);
    comboCorporacion.setBackground(Color.WHITE);
    txtIdDonador.setBackground(Color.WHITE);

    // 1. Validar ID generado
    String idDonador = txtIdDonador.getText().trim();
    if (idDonador.isEmpty() || idDonador.contains("Presiona") || idDonador.contains("DON")) {
        errores.append("• Debe generar el ID del donador primero\n");
        txtIdDonador.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }

    // 2. Validar nombre (obligatorio)
    String nombre = txtNombre.getText().trim();
    if (nombre.isEmpty()) {
        errores.append("• El nombre es obligatorio\n");
        txtNombre.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }

    // 3. Validar dirección (obligatorio)
    String direccion = txtDireccion.getText().trim();
    if (direccion.isEmpty()) {
        errores.append("• La dirección es obligatoria\n");
        txtDireccion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }

    // 4. Validar categoría (obligatorio)
    if (combCategoria.getSelectedIndex() <= 0) {
        errores.append("• Debe seleccionar una categoría\n");
        combCategoria.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else {
        // Validar año de graduación si es Alumno
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

    // 5. Validar teléfono (obligatorio)
    String telefono = txtTelefono.getText().trim();
    if (telefono.isEmpty() || telefono.equals("(  )    -")) {
        errores.append("• El teléfono es obligatorio\n");
        txtTelefono.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else {
        // Validar formato mínimo de teléfono (al menos 8 dígitos)
        String soloNumeros = telefono.replaceAll("[^0-9]", "");
        if (soloNumeros.length() < 8) {
            errores.append("• Teléfono debe tener al menos 8 dígitos\n");
            txtTelefono.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
    }

    // 6. Validar email (opcional pero con formato si se ingresa)
    String email = txtCorreo.getText().trim();
    if (!email.isEmpty()) {
        if (!email.contains("@") || !email.contains(".") || email.length() < 5) {
            errores.append("• Email no tiene formato válido\n");
            txtCorreo.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
    }

    // 7. Validar corporación (obligatoria según tu requerimiento)
    if (comboCorporacion.getSelectedIndex() <= 0) {
        errores.append("• Debe seleccionar una corporación\n");
        comboCorporacion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }

    // Mostrar errores si los hay
    if (hayErrores) {
        JOptionPane.showMessageDialog(this, 
            "Por favor complete los siguientes campos:\n\n" + errores.toString(), 
            "Campos incompletos", 
            JOptionPane.WARNING_MESSAGE);
        return false;
    }
    
    return true;
}

    // Carga las corporaciones (aquí solo limpia el TextField, podrías cambiar a combo)
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
    // CONSULTA CORREGIDA - usa los nombres EXACTOS de tus columnas
    String sql = """
        SELECT D.idDonador, D.nombre, D.telefono, D.email, 
               C.nombre as categoria, CORP.nombre as corporacion
        FROM Donador D
        INNER JOIN CategoriaDonador C ON D.idCategoria = C.idCategoria
        INNER JOIN Corporacion CORP ON D.idCorporacion = CORP.idCorporacion
        ORDER BY D.nombre
        """;

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        // Modelo con columnas CORRECTAS
        javax.swing.table.DefaultTableModel modelo = 
            new javax.swing.table.DefaultTableModel(
                new Object[]{"ID Donador", "Nombre", "Teléfono", "Email", "Categoría", "Corporación"}, 0) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        while (rs.next()) {
            // Asegúrate de usar los nombres EXACTOS del SELECT
            modelo.addRow(new Object[]{
                rs.getString("idDonador"),      // ← Este debe coincidir con el SELECT
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("categoria"),
                rs.getString("corporacion")
            });
        }

        tablaListaDonadores.setModel(modelo);
        
        // Ajustar anchos de columnas
        if (tablaListaDonadores.getColumnCount() >= 6) {
            tablaListaDonadores.getColumnModel().getColumn(0).setPreferredWidth(80);
            tablaListaDonadores.getColumnModel().getColumn(1).setPreferredWidth(150);
            tablaListaDonadores.getColumnModel().getColumn(2).setPreferredWidth(100);
            tablaListaDonadores.getColumnModel().getColumn(3).setPreferredWidth(150);
            tablaListaDonadores.getColumnModel().getColumn(4).setPreferredWidth(100);
            tablaListaDonadores.getColumnModel().getColumn(5).setPreferredWidth(120);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar donadores: " + e.getMessage() + 
            "\n\nSQL: " + sql,  // Muestra el SQL para debug
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
            .replaceAll("[^A-Z\\s]", " "); // quita números y símbolos

        String[] partes = limpio.split("\\s+");
        String iniciales = "DON";

        if (partes.length >= 3) {
            iniciales = "" + partes[0].charAt(0) + partes[1].charAt(0) + partes[2].charAt(0);
        } else if (partes.length == 2) {
            iniciales = "" + partes[0].charAt(0) + partes[1].charAt(0);
        } else if (partes.length == 1 && partes[0].length() >= 3) {
            iniciales = partes[0].substring(0, 3);
        }

        String año = String.format("%02d", java.time.Year.now().getValue() % 100); // 25
        String prefijo = iniciales + año; // ej: JCP25

        // QUERY 100% COMPATIBLE CON SQL SERVER
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
     
    // Agregar un donador a la base de datos
  private void agregarDonador() {
    // Validar antes de insertar
    if (!validarCamposObligatorios()) {
        return;
    }

    try {
        // Obtener valores de los campos
        String idDonador = txtIdDonador.getText().trim();
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        CategoriaItem categoria = (CategoriaItem) combCategoria.getSelectedItem();
        
        // Manejar año de graduación
        int añoGraduacion;
        if (categoria.getNombre().toLowerCase().contains("alumno")) {
            añoGraduacion = (Integer) spinnerAñoGraduacion.getValue();
        } else {
            añoGraduacion = 0; // O NULL según tu base de datos
        }
        
        // Limpiar formato del teléfono
        String telefono = txtTelefono.getText().trim().replaceAll("[^0-9]", "");
        String email = txtCorreo.getText().trim();
        String conyuge = txtConyuge.getText().trim();
        
        // Obtener ID de corporación
        CorporacionItem corporacion = (CorporacionItem) comboCorporacion.getSelectedItem();
        
        // Preparar SQL
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
            
            // Manejar año de graduación (puede ser NULL si no es alumno)
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
                
                // Actualizar y limpiar
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


  private void limpiarCampos() {
    txtNombre.setText("");
    txtNombre.setBackground(Color.WHITE);
    
    txtDireccion.setText("");
    txtDireccion.setBackground(Color.WHITE);
    
    combCategoria.setSelectedIndex(0);
    combCategoria.setBackground(Color.WHITE);
    
    // Spiner deshabilitado por defecto
    spinnerAñoGraduacion.setEnabled(false);
    spinnerAñoGraduacion.setValue(2025);
    spinnerAñoGraduacion.setBackground(Color.LIGHT_GRAY);
    
    txtTelefono.setText("");
    txtTelefono.setBackground(Color.WHITE);
    
    txtCorreo.setText("");
    txtCorreo.setBackground(Color.WHITE);
    
    if (comboCorporacion != null) {
        comboCorporacion.setSelectedIndex(0);
        comboCorporacion.setBackground(Color.WHITE);
    }
    
    txtConyuge.setText("");
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
        jLabel5 = new javax.swing.JLabel();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaListaDonadores = new javax.swing.JTable();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel2.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel2.setText("Datos del Donador");

        jLabel5.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\handshake.png")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel1.setText("Nombre: ");

        jLabel3.setText("Direccion: ");

        jLabel6.setText("Categoria: ");

        jLabel7.setText("Año graduacion: ");

        jLabel8.setText("Telefono:");

        jLabel9.setText("E-mail: ");

        jLabel10.setText("Corporacion: ");

        jLabel11.setText("Conyuge: ");

        botonBuscar.setText("Buscar");
        botonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarActionPerformed(evt);
            }
        });

        botonEditar.setText("Editar");
        botonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEditarActionPerformed(evt);
            }
        });

        botonEliminar.setText("Eliminar");
        botonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarActionPerformed(evt);
            }
        });

        botonLimpiar.setText("Limpiar Casillas");

        jLabel12.setFont(new java.awt.Font("Yu Gothic Medium", 1, 18)); // NOI18N
        jLabel12.setText("Lista de donadores: ");

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
            txtTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##) ####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        botonAgregar.setText("Agregar");
        botonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAgregarActionPerformed(evt);
            }
        });

        botonVolver.setBackground(new java.awt.Color(51, 204, 0));
        botonVolver.setText("Volver");
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        jLabel14.setText("Id del donador: ");

        txtIdDonador.setEditable(false);

        botonGenerarId.setText("Generar Id");
        botonGenerarId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGenerarIdActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(botonVolver))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(txtIdDonador, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(botonGenerarId))
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(combCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(spinnerAñoGraduacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3))
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtConyuge, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(botonEliminar)
                                    .addComponent(botonAgregar)
                                    .addComponent(botonBuscar)
                                    .addComponent(botonEditar)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(botonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)))
                        .addGap(30, 30, 30))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel6)
                            .addComponent(jLabel12))
                        .addContainerGap(401, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonAgregar))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtIdDonador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonGenerarId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonEliminar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonBuscar)
                    .addComponent(jLabel6)
                    .addComponent(combCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(spinnerAñoGraduacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(botonEditar)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(botonLimpiar)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(comboCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtConyuge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(botonVolver))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void combCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combCategoriaActionPerformed

    private void botonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_botonBuscarActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // TODO add your handling code here:
    }                  
    private void botonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarActionPerformed
        // TODO add your handling code here:
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
        // TODO add your handling code here:
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
    private javax.swing.JLabel jLabel5;
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
