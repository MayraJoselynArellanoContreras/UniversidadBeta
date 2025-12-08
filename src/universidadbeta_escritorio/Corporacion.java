/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package universidadbeta_escritorio;

import util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.sql.Statement;

/**
 *
 * @author contr
 */
public class Corporacion extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Corporacion.class.getName());

    /**
     * Creates new form Corporacion
     */
    public Corporacion(){
    initComponents();
    
    if (tablaCorporaciones != null) {
        tablaCorporaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                }
            }
        });
    }
    
    cargarTabla();
}    
    
private void agregarCorporacion() {
    if (!validarCampos()) {
        return;
    }
  
    String nombre = txtNombreCorporacion.getText().trim();
    String direccion = txtDireccion.getText().trim();
    
 
    String sql = "INSERT INTO Corporacion (nombre, direccion) VALUES (?, ?)";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        
        ps.setString(1, nombre);
        ps.setString(2, direccion);
        
        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1); 
                    
                    txtIdCorporacion.setText(String.valueOf(idGenerado));
                    
                    JOptionPane.showMessageDialog(this,
                        "✅ Corporación creada exitosamente!\n\n" +
                        "ID: " + idGenerado + "\n" +
                        "Nombre: " + nombre + "\n" +
                        "Dirección: " + direccion,
                        "Alta exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            // Actualizar tabla y limpiar campos
            cargarTabla();
            limpiarCampos();
            
        } else {
            JOptionPane.showMessageDialog(this,
                "No se pudo crear la corporación",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (SQLException e) {

        String mensajeError = "Error al crear corporación: " + e.getMessage();
        

        if (e.getMessage().contains("PRIMARY KEY") || e.getMessage().contains("duplicate")) {
            mensajeError = "Error: Ya existe una corporación con ese nombre";
        }
        
        JOptionPane.showMessageDialog(this,
            mensajeError,
            "Error de base de datos",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void cargarTabla() {
    String sql = "SELECT idCorporacion, nombre, direccion FROM Corporacion ORDER BY nombre";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        

        javax.swing.table.DefaultTableModel modelo = 
            new javax.swing.table.DefaultTableModel(
                new Object[]{"ID", "Nombre", "Dirección"}, 0) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Llenar la tabla con los datos
        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getInt("idCorporacion"),  
                rs.getString("nombre"),
                rs.getString("direccion")
            });
        }
        

        tablaCorporaciones.setModel(modelo);
        

        if (tablaCorporaciones.getColumnCount() >= 3) {
            tablaCorporaciones.getColumnModel().getColumn(0).setPreferredWidth(60);  
            tablaCorporaciones.getColumnModel().getColumn(1).setPreferredWidth(150); 
            tablaCorporaciones.getColumnModel().getColumn(2).setPreferredWidth(200); 
        }
        

        tablaCorporaciones.setDefaultEditor(Object.class, null);
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "❌ Error al cargar corporaciones: " + e.getMessage(),
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

private void buscarCorporacion() {
    String criterio = JOptionPane.showInputDialog(this,
        "Ingrese nombre o ID a buscar:",
        "Buscar Corporación",
        JOptionPane.QUESTION_MESSAGE);
    
    if (criterio != null && !criterio.trim().isEmpty()) {
        String sql = "SELECT idCorporacion, nombre, direccion FROM Corporacion " +
                    "WHERE nombre LIKE ? OR CAST(idCorporacion AS VARCHAR) LIKE ? " +
                    "ORDER BY nombre";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String parametro = "%" + criterio + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            
            ResultSet rs = ps.executeQuery();
            
            javax.swing.table.DefaultTableModel modelo = 
                new javax.swing.table.DefaultTableModel(
                    new Object[]{"ID", "Nombre", "Dirección"}, 0) {
                
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("idCorporacion"),
                    rs.getString("nombre"),
                    rs.getString("direccion")
                });
            }
            
            if (modelo.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "No se encontraron corporaciones con: " + criterio,
                    "Sin resultados",
                    JOptionPane.INFORMATION_MESSAGE);
                cargarTabla(); // Recargar tabla completa
            } else {
                tablaCorporaciones.setModel(modelo);
                JOptionPane.showMessageDialog(this,
                    "Se encontraron " + modelo.getRowCount() + " resultado(s)",
                    "Búsqueda completada",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error en búsqueda: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

private boolean tieneDonadoresAsociados(int idCorporacion) {
    String sql = "SELECT COUNT(*) FROM Donador WHERE idCorporacion = ?";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, idCorporacion);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return false;
}

private boolean validarCampos() {

    txtNombreCorporacion.setBackground(Color.WHITE);
    txtDireccion.setBackground(Color.WHITE);
    

    StringBuilder errores = new StringBuilder();
    boolean hayErrores = false;
    

    String nombre = txtNombreCorporacion.getText().trim();
    if (nombre.isEmpty()) {
        errores.append("• El nombre de la corporación es obligatorio\n");
        txtNombreCorporacion.setBackground(new Color(255, 200, 200)); 
        hayErrores = true;
    } else if (nombre.length() < 3) {
        errores.append("• El nombre debe tener al menos 3 caracteres\n");
        txtNombreCorporacion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else if (nombre.length() > 100) {
        errores.append("• El nombre no puede exceder 100 caracteres\n");
        txtNombreCorporacion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }
    

    String direccion = txtDireccion.getText().trim();
    if (direccion.isEmpty()) {
        errores.append("• La dirección es obligatoria\n");
        txtDireccion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else if (direccion.length() < 10) {
        errores.append("• La dirección debe tener al menos 10 caracteres\n");
        txtDireccion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    } else if (direccion.length() > 200) {
        errores.append("• La dirección no puede exceder 200 caracteres\n");
        txtDireccion.setBackground(new Color(255, 200, 200));
        hayErrores = true;
    }
    

    if (hayErrores) {
        String titulo = "Errores de validación";
        String mensaje = "Por favor corrija los siguientes errores:\n\n" + errores.toString();
        
        JOptionPane.showMessageDialog(this, 
            mensaje, 
            titulo, 
            JOptionPane.WARNING_MESSAGE);
        

        if (txtNombreCorporacion.getBackground().equals(new Color(255, 200, 200))) {
            txtNombreCorporacion.requestFocus();
        } else if (txtDireccion.getBackground().equals(new Color(255, 200, 200))) {
            txtDireccion.requestFocus();
        }
        
        return false;
    }
    
    return true;
}

private void limpiarCampos() {
    txtIdCorporacion.setText("");
    txtNombreCorporacion.setText("");
    txtDireccion.setText("");
    
    txtNombreCorporacion.setBackground(Color.WHITE);
    txtDireccion.setBackground(Color.WHITE);
    
    // Restaurar estado de botones
    botonAgregar.setEnabled(true);
    
    // Deseleccionar tabla
    if (tablaCorporaciones != null) {
        tablaCorporaciones.clearSelection();
    }
    
    // Enfocar campo nombre
    txtNombreCorporacion.requestFocus();
}


private void eliminarCorporacion() {
    int fila = tablaCorporaciones.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this,
            "Seleccione una corporación de la tabla para eliminar",
            "Selección requerida",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int id = (Integer) tablaCorporaciones.getValueAt(fila, 0);
    String nombre = (String) tablaCorporaciones.getValueAt(fila, 1);
    

    int confirmacion = JOptionPane.showConfirmDialog(this,
        "¿Está seguro de eliminar la corporación?\n\n" +
        "ID: " + id + "\n" +
        "Nombre: " + nombre + "\n\n" +
        "⚠️ Advertencia: Esta acción no se puede deshacer.",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirmacion == JOptionPane.YES_OPTION) {

        if (tieneDonadoresAsociados(id)) {
            JOptionPane.showMessageDialog(this,
                "No se puede eliminar. Esta corporación tiene donadores asociados.\n" +
                "Primero elimine o cambie los donadores relacionados.",
                "Error de integridad referencial",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String sql = "DELETE FROM Corporacion WHERE idCorporacion = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this,
                    "✅ Corporación eliminada exitosamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTabla();
                limpiarCampos();
                botonAgregar.setEnabled(true);
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
        txtNombreCorporacion = new javax.swing.JTextField();
        txtIdCorporacion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        botonAgregar = new javax.swing.JButton();
        botonEliminar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCorporaciones = new javax.swing.JTable();
        botonVolver = new javax.swing.JButton();
        txtDireccion = new javax.swing.JTextField();
        botonLimpiar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 24)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\money-check.png")); // NOI18N
        jLabel1.setText("Corporaciones");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(173, 173, 173)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabel2.setText("Nombre: ");

        txtIdCorporacion.setEditable(false);

        jLabel4.setText("Direccion: ");

        botonAgregar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\check.png")); // NOI18N
        botonAgregar.setText("Agregar");
        botonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAgregarActionPerformed(evt);
            }
        });

        botonEliminar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\cross.png")); // NOI18N
        botonEliminar.setText("Eliminar");
        botonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Yu Gothic", 1, 24)); // NOI18N
        jLabel5.setText("Lista de corporaciones");

        tablaCorporaciones.setBackground(new java.awt.Color(204, 204, 204));
        tablaCorporaciones.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaCorporaciones);

        botonVolver.setBackground(new java.awt.Color(51, 204, 0));
        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.setText("Volver");
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        botonLimpiar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\broom.png")); // NOI18N
        botonLimpiar.setText("Limpiar Casillas");
        botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarActionPerformed(evt);
            }
        });

        jLabel3.setText("Id de la corporacion (generado automaticamente):");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(30, 30, 30)
                                .addComponent(txtIdCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel2))
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNombreCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addComponent(botonLimpiar)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(botonAgregar)
                    .addComponent(botonEliminar))
                .addGap(15, 15, 15))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(24, 24, 24)
                        .addComponent(botonVolver))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtIdCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonAgregar))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombreCorporacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonEliminar))
                .addGap(33, 33, 33)
                .addComponent(botonLimpiar)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(botonVolver)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAgregarActionPerformed
        agregarCorporacion();
    }//GEN-LAST:event_botonAgregarActionPerformed

    private void botonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarActionPerformed
          eliminarCorporacion();
    }//GEN-LAST:event_botonEliminarActionPerformed

    private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_botonLimpiarActionPerformed

    private void botonVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolverActionPerformed
        GestionPrincipal gestion = new GestionPrincipal();
    gestion.setVisible(true);
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
        java.awt.EventQueue.invokeLater(() -> new Corporacion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAgregar;
    private javax.swing.JButton botonEliminar;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaCorporaciones;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtIdCorporacion;
    private javax.swing.JTextField txtNombreCorporacion;
    // End of variables declaration//GEN-END:variables
}
// En Corporaciones.java (al final del archivo)
class CorporacionItem {
    private int id;
    private String nombre;
    
    public CorporacionItem(int id, String nombre) {
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

