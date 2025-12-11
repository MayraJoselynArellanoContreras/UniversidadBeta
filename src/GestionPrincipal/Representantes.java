/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GestionPrincipal;

import GestionPrincipal.GestionPrincipal;
import java.awt.Color;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import util.ConexionBD;

/**
 *
 * @author contr
 */
public class Representantes extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Representantes.class.getName());

    /**
     * Creates new form Representantes
     */
public Representantes() {
    initComponents();
    setLocationRelativeTo(null);
    this.getContentPane().setBackground(new Color(182, 197, 179));
    cargarGeneraciones();
    cargarTabla();

java.util.List<java.awt.Component> ordenTab = java.util.Arrays.asList(
        txtIdRepresentante,
        txtNombre,
        comboGeneracion,
        txtTelefono,
        txtEmail,
        botonAgregar,
        botonEditar,
        botonEliminar,
        botonBuscar,
        botonLimpiar,
        botonVolver,     
        tablaRepresentantes
);

this.setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {

    @Override
    public java.awt.Component getComponentAfter(java.awt.Container cont, java.awt.Component comp) {
        int i = ordenTab.indexOf(comp);
        if (i == -1) return ordenTab.get(0);
        return ordenTab.get((i + 1) % ordenTab.size());
    }

    @Override
    public java.awt.Component getComponentBefore(java.awt.Container cont, java.awt.Component comp) {
        int i = ordenTab.indexOf(comp);
        if (i == -1) return ordenTab.get(0);
        return ordenTab.get((i - 1 + ordenTab.size()) % ordenTab.size());
    }

    @Override
    public java.awt.Component getDefaultComponent(java.awt.Container cont) {
        return ordenTab.get(0);
    }

    @Override
    public java.awt.Component getFirstComponent(java.awt.Container cont) {
        return ordenTab.get(0);
    }

    @Override
    public java.awt.Component getLastComponent(java.awt.Container cont) {
        return ordenTab.get(ordenTab.size() - 1);
    }
});

    

    tablaRepresentantes.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                cargarRepresentanteDesdeTabla();
            }
        }
    });
}


class GeneracionItem {
    private int anio;
    private String display;
    
    public GeneracionItem(int anio) {
        this.anio = anio;
        if (anio == 0) {
            this.display = "Seleccione generación...";
        } else {
            this.display = "Generación " + anio;
        }
    }
    
    public int getAnio() { return anio; }
    
    @Override
    public String toString() {
        return display;
    }
}

private void cargarGeneraciones() {
    String sql = """
        SELECT DISTINCT anioGraduacion 
        FROM Donador 
        WHERE anioGraduacion IS NOT NULL AND anioGraduacion > 0
        ORDER BY anioGraduacion DESC
        """;

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        comboGeneracion.removeAllItems();
        comboGeneracion.addItem(new GeneracionItem(0));

        while (rs.next()) {
            int anio = rs.getInt("anioGraduacion");
            comboGeneracion.addItem(new GeneracionItem(anio));
        }
        

        int añoActual = java.time.Year.now().getValue();
        for (int i = añoActual; i >= 1990; i--) {
            boolean existe = false;
            for (int j = 0; j < comboGeneracion.getItemCount(); j++) {
                GeneracionItem item = (GeneracionItem) comboGeneracion.getItemAt(j);
                if (item.getAnio() == i) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                comboGeneracion.addItem(new GeneracionItem(i));
            }
        }
 
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar generaciones: " + e.getMessage());
        e.printStackTrace();
    }
}


private boolean validarCamposObligatorios() {
    StringBuilder errores = new StringBuilder();
    boolean hayErrores = false;
    

    txtNombre.setBackground(Color.WHITE);
    comboGeneracion.setBackground(Color.WHITE);
    txtTelefono.setBackground(Color.WHITE);
    txtEmail.setBackground(Color.WHITE);
    

    String nombre = txtNombre.getText().trim();
    if (nombre.isEmpty()) {
        errores.append("• El nombre es obligatorio\n");
        txtNombre.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }


    GeneracionItem generacion = (GeneracionItem) comboGeneracion.getSelectedItem();
    if (generacion.getAnio() == 0) {
        errores.append("• Debe seleccionar una generación\n");
        comboGeneracion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }


    String telefono = txtTelefono.getText().trim();
    if (telefono.isEmpty()) {
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
    

    if (hayErrores) {
        JOptionPane.showMessageDialog(this, 
            "Por favor complete los siguientes campos:\n\n" + errores.toString(), 
            "Campos incompletos", 
            JOptionPane.WARNING_MESSAGE);
        return false;
    }
    
    return true;
}


private void cargarTabla() {
    String sql = """
        SELECT 
            idRepresentante,
            nombre,
            clase,
            telefono,
            email
        FROM RepresentanteClase
        ORDER BY clase DESC, nombre
        """;

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        javax.swing.table.DefaultTableModel modelo = 
            new javax.swing.table.DefaultTableModel(
                new Object[]{"ID", "Nombre", "Generación", "Teléfono", "Email"}, 0) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getInt("idRepresentante"),
                rs.getString("nombre"),
                rs.getInt("clase"),
                rs.getString("telefono"),
                rs.getString("email")
            });
        }

        tablaRepresentantes.setModel(modelo);
        
        // Ajustar anchos de columnas
        if (tablaRepresentantes.getColumnCount() >= 5) {
            tablaRepresentantes.getColumnModel().getColumn(0).setPreferredWidth(50);
            tablaRepresentantes.getColumnModel().getColumn(1).setPreferredWidth(150);
            tablaRepresentantes.getColumnModel().getColumn(2).setPreferredWidth(80);
            tablaRepresentantes.getColumnModel().getColumn(3).setPreferredWidth(100);
            tablaRepresentantes.getColumnModel().getColumn(4).setPreferredWidth(150);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar representantes: " + e.getMessage(), 
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


private void agregarRepresentante() {
    if (!validarCamposObligatorios()) {
        return;
    }

    try {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim().replaceAll("[^0-9]", "");
        String email = txtEmail.getText().trim();
        GeneracionItem generacion = (GeneracionItem) comboGeneracion.getSelectedItem();

        String sql = """
            INSERT INTO RepresentanteClase 
            (nombre, clase, telefono, email)
            OUTPUT INSERTED.idRepresentante
            VALUES (?, ?, ?, ?)
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ps.setInt(2, generacion.getAnio());
            ps.setString(3, telefono);
            ps.setString(4, email.isEmpty() ? null : email);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Representante registrado exitosamente!\n\n" +
                    "ID: " + idGenerado + "\n" +
                    "Nombre: " + nombre + "\n" +
                    "Generación: " + generacion.getAnio(),
                    "Registro exitoso", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTabla();
                limpiarCampos();
            }
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al guardar: " + e.getMessage(), 
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {
    String idStr = JOptionPane.showInputDialog(this,
        "Ingrese ID del representante:",
        "Buscar por ID",
        JOptionPane.QUESTION_MESSAGE);
    
    if (idStr == null || idStr.trim().isEmpty()) {
        return;
    }
    
    try {
        int id = Integer.parseInt(idStr.trim());
        buscarRepresentantePorID(id);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "ID inválido. Debe ser un número.",
            "Error de formato",
            JOptionPane.ERROR_MESSAGE);
    }
}


private void buscarRepresentantePorID(int idRepresentante) {
    String sql = """
        SELECT 
            idRepresentante,
            nombre,
            telefono,
            email,
            clase
        FROM RepresentanteClase
        WHERE idRepresentante = ?
        """;

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idRepresentante);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            cargarDatosEnFormulario(rs);
            
            JOptionPane.showMessageDialog(this, 
                "✅ Representante encontrado\n\n" +
                "ID: " + rs.getInt("idRepresentante") + "\n" +
                "Nombre: " + rs.getString("nombre"),
                "Búsqueda exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            
            botonAgregar.setEnabled(false);
            botonEditar.setEnabled(true);
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "❌ No se encontró representante con ID: " + idRepresentante,
                "Sin resultados", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al buscar: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}


private void cargarRepresentanteDesdeTabla() {
    int fila = tablaRepresentantes.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
            "Seleccione un representante de la tabla",
            "Selección requerida",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int idRepresentante = (int) tablaRepresentantes.getValueAt(fila, 0);
    buscarRepresentantePorID(idRepresentante);
}


private void cargarDatosEnFormulario(ResultSet rs) throws SQLException {
    txtIdRepresentante.setText(String.valueOf(rs.getInt("idRepresentante")));
    txtNombre.setText(rs.getString("nombre"));
    txtTelefono.setText(rs.getString("telefono"));
    txtEmail.setText(rs.getString("email"));
    
    int clase = rs.getInt("clase");
    seleccionarGeneracion(clase);
    
    botonAgregar.setText("Actualizar Representante");
}


private void seleccionarGeneracion(int anio) {
    for (int i = 0; i < comboGeneracion.getItemCount(); i++) {
        GeneracionItem item = (GeneracionItem) comboGeneracion.getItemAt(i);
        if (item.getAnio() == anio) {
            comboGeneracion.setSelectedIndex(i);
            return;
        }
    }
    comboGeneracion.setSelectedIndex(0);
}


private void editarRepresentante() {
    String idStr = txtIdRepresentante.getText().trim();
    if (idStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Primero busque un representante para editar",
            "Advertencia", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    if (!validarCamposObligatorios()) {
        return;
    }
    
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Actualizar los datos del representante?\n\n" +
        "ID: " + idStr + "\n" +
        "Nombre: " + txtNombre.getText().trim(),
        "Confirmar actualización", 
        JOptionPane.YES_NO_OPTION);
    
    if (confirmacion == JOptionPane.YES_OPTION) {
        actualizarRepresentanteEnBD(Integer.parseInt(idStr));
    }
}


private void actualizarRepresentanteEnBD(int idRepresentante) {
    String sql = """
        UPDATE RepresentanteClase SET 
            nombre = ?, 
            clase = ?, 
            telefono = ?, 
            email = ?
        WHERE idRepresentante = ?
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        String nombre = txtNombre.getText().trim();
        GeneracionItem generacion = (GeneracionItem) comboGeneracion.getSelectedItem();
        String telefono = txtTelefono.getText().trim().replaceAll("[^0-9]", "");
        String email = txtEmail.getText().trim();
        
        ps.setString(1, nombre);
        ps.setInt(2, generacion.getAnio());
        ps.setString(3, telefono);
        ps.setString(4, email.isEmpty() ? null : email);
        ps.setInt(5, idRepresentante);
        
        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(this, 
                "✅ Representante actualizado exitosamente",
                "Actualización exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            
            cargarTabla();
            limpiarCampos();
            
            botonAgregar.setEnabled(true);
            botonEditar.setEnabled(false);
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se pudo actualizar el representante",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al actualizar: " + e.getMessage(),
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


private void eliminarRepresentante() {
    int fila = tablaRepresentantes.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, 
            "Seleccione un representante de la tabla para eliminar",
            "Selección requerida", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int idRepresentante = (int) tablaRepresentantes.getValueAt(fila, 0);
    String nombre = tablaRepresentantes.getValueAt(fila, 1).toString();
    
    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Eliminar representante?\n\n" +
        "ID: " + idRepresentante + "\n" +
        "Nombre: " + nombre + "\n\n" +
        "Esta acción no se puede deshacer.",
        "Confirmar eliminación", 
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirmacion != JOptionPane.YES_OPTION) {
        return;
    }
    
    String sql = "DELETE FROM RepresentanteClase WHERE idRepresentante = ?";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idRepresentante);
        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(this, 
                "✅ Representante eliminado exitosamente",
                "Eliminación exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            
            cargarTabla();
            limpiarCampos();
            
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


private void limpiarCampos() {
    txtIdRepresentante.setText("");
    txtNombre.setText("");
    txtTelefono.setText("");
    txtEmail.setText("");
    
    comboGeneracion.setSelectedIndex(0);
    

    txtNombre.setBackground(Color.WHITE);
    comboGeneracion.setBackground(Color.WHITE);
    txtTelefono.setBackground(Color.WHITE);
    txtEmail.setBackground(Color.WHITE);
    

    botonAgregar.setText("Agregar Representante");
    botonAgregar.setEnabled(true);
    botonEditar.setEnabled(false);
    

    tablaRepresentantes.clearSelection();
    

    txtNombre.requestFocus();
}


private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {
    limpiarCampos();
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
        txtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        comboGeneracion = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        botonAgregar = new javax.swing.JButton();
        botonEditar = new javax.swing.JButton();
        botonEliminar = new javax.swing.JButton();
        botonBuscar = new javax.swing.JButton();
        botonVolver = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaRepresentantes = new javax.swing.JTable();
        botonLimpiar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtIdRepresentante = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\book-open-reader.png")); // NOI18N
        jLabel1.setText("Representante de clase");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(184, 184, 184))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 18, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        jLabel2.setText("Nombre: ");

        jLabel3.setText("Generacion:");

        jLabel4.setText("Telefono: ");

        jLabel5.setText("E-mail: ");

        botonAgregar.setText("Agregar");
        botonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAgregarActionPerformed(evt);
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

        botonBuscar.setText("Buscar");
        botonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarActionPerformed(evt);
            }
        });

        botonVolver.setBackground(new java.awt.Color(51, 204, 0));
        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel6.setText("Tabla Representantes");

        tablaRepresentantes.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaRepresentantes);

        botonLimpiar.setText("Limpiar Casillas");
        botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarActionPerformed(evt);
            }
        });

        jLabel7.setText("id Representante: ");

        txtIdRepresentante.setEditable(false);

        try {
            txtTelefono.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##) ###-###-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonVolver))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4)
                                .addComponent(jLabel5)
                                .addComponent(jLabel2)
                                .addComponent(jLabel7))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(botonAgregar)
                                .addGap(34, 34, 34)))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboGeneracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIdRepresentante, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(botonEliminar)
                                .addGap(85, 85, 85)
                                .addComponent(botonBuscar)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(botonEditar)
                    .addComponent(botonLimpiar))
                .addGap(65, 65, 65))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonLimpiar)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtIdRepresentante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboGeneracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonAgregar)
                    .addComponent(botonEliminar)
                    .addComponent(botonBuscar)
                    .addComponent(botonEditar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonVolver, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAgregarActionPerformed
       agregarRepresentante();
    }//GEN-LAST:event_botonAgregarActionPerformed

    private void botonVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolverActionPerformed
        GestionPrincipal gestion = new GestionPrincipal();
    gestion.setVisible(true);
    this.dispose(); 
    }//GEN-LAST:event_botonVolverActionPerformed

    private void botonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarActionPerformed
        editarRepresentante();
    }//GEN-LAST:event_botonEditarActionPerformed

    private void botonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarActionPerformed
        eliminarRepresentante();
    }//GEN-LAST:event_botonEliminarActionPerformed

    private void botonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarActionPerformed
       String idStr = JOptionPane.showInputDialog(this,
        "Ingrese ID del representante:",
        "Buscar Representante",
        JOptionPane.QUESTION_MESSAGE);
    if (idStr == null || idStr.trim().isEmpty()) {
        return;
    }
    try {
        int id = Integer.parseInt(idStr.trim());
        buscarRepresentantePorID(id);
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "ID inválido. Debe ser un número entero.",
            "Error de formato",
            JOptionPane.ERROR_MESSAGE);
    }
    
    }//GEN-LAST:event_botonBuscarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Representantes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAgregar;
    private javax.swing.JButton botonBuscar;
    private javax.swing.JButton botonEditar;
    private javax.swing.JButton botonEliminar;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JComboBox comboGeneracion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tablaRepresentantes;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtIdRepresentante;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JFormattedTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
// Clase para los items del ComboBox de Círculos
class CirculoItem {
    private int id;
    private String nombre;
    
    public CirculoItem(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    
    @Override
    public String toString() {
        return nombre;
    }
}