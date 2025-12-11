package Configuraciones;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import util.ConexionBD;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author contr
 */
public class CirculoDonador extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CirculoDonador.class.getName());
    private DefaultTableModel modeloTabla;
    /**
     * Creates new form CirculoDonador
     */
    public CirculoDonador() {
        
        initComponents();
        setLocationRelativeTo(null);
         cargarTabla();

this.setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {
    @Override
    public java.awt.Component getComponentAfter(java.awt.Container aContainer, java.awt.Component aComponent) {

        if (aComponent.equals(txtNombreCirculo)) return txtMontoMinimo;
        if (aComponent.equals(txtMontoMinimo)) return txtMontoMaximo;
        if (aComponent.equals(txtMontoMaximo)) return botonRegistrar;
        if (aComponent.equals(botonRegistrar)) return botonEditar;
        if (aComponent.equals(botonEditar)) return botonEliminar;
        if (aComponent.equals(botonEliminar)) return botonBuscar;
        if (aComponent.equals(botonBuscar)) return botonLimpiar;
        if (aComponent.equals(botonLimpiar)) return botonVolver;
        if (aComponent.equals(botonVolver)) return tablaCirculos;

        return txtNombreCirculo; // inicio del ciclo
    }

    @Override
    public java.awt.Component getComponentBefore(java.awt.Container aContainer, java.awt.Component aComponent) {

        if (aComponent.equals(txtMontoMinimo)) return txtNombreCirculo;
        if (aComponent.equals(txtMontoMaximo)) return txtMontoMinimo;
        if (aComponent.equals(botonRegistrar)) return txtMontoMaximo;
        if (aComponent.equals(botonEditar)) return botonRegistrar;
        if (aComponent.equals(botonEliminar)) return botonEditar;
        if (aComponent.equals(botonBuscar)) return botonEliminar;
        if (aComponent.equals(botonLimpiar)) return botonBuscar;
        if (aComponent.equals(botonVolver)) return botonLimpiar;
        if (aComponent.equals(tablaCirculos)) return botonVolver;

        return tablaCirculos; // final del ciclo
    }

    @Override public java.awt.Component getDefaultComponent(java.awt.Container aContainer) { return txtNombreCirculo; }
    @Override public java.awt.Component getFirstComponent(java.awt.Container aContainer) { return txtNombreCirculo; }
    @Override public java.awt.Component getLastComponent(java.awt.Container aContainer) { return tablaCirculos; }
});

        botonEditar.setEnabled(false);

        this.getContentPane().setBackground(new Color(182, 197, 179));
    
   
        tablaCirculos.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                cargarCirculoDesdeTabla();
            }
        }
    });
  }     
    private void crearModeloTabla() {
    String[] columnas = {"ID", "Nombre", "Mínimo", "Máximo", "Rango"};
    modeloTabla = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; 
        }
    };
    
    if (tablaCirculos != null) {
        tablaCirculos.setModel(modeloTabla);
        System.out.println("✓ ModeloTabla creado y asignado a JTable");
    }
}
    private void configurarTabla() {
        if (tablaCirculos.getColumnCount() >= 5) {
            tablaCirculos.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
            tablaCirculos.getColumnModel().getColumn(1).setPreferredWidth(150);  // Nombre
            tablaCirculos.getColumnModel().getColumn(2).setPreferredWidth(100);  // Mínimo
            tablaCirculos.getColumnModel().getColumn(3).setPreferredWidth(100);  // Máximo
            tablaCirculos.getColumnModel().getColumn(4).setPreferredWidth(150);  // Rango
        }
    }

private void cargarTabla() {
    String sql = """
        SELECT 
            idCirculo,
            nombre,
            minimo,
            maximo
        FROM CirculoDonador
        ORDER BY minimo DESC
        """;

    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {


        DefaultTableModel modelo = (DefaultTableModel) tablaCirculos.getModel();
        

        modelo.setRowCount(0);
        

        while (rs.next()) {
            int id = rs.getInt("idCirculo");
            String nombre = rs.getString("nombre");
            double minimo = rs.getDouble("minimo");
            double maximo = rs.getDouble("maximo");
            boolean maximoEsNull = rs.wasNull();
            
            String strMinimo = String.format("$%,.2f", minimo);
            String strMaximo = maximoEsNull ? "-" : String.format("$%,.2f", maximo);
            String rango = maximoEsNull ? 
                String.format("$%,.2f y más", minimo) : 
                String.format("$%,.2f - $%,.2f", minimo, maximo);
            

            modelo.addRow(new Object[]{
                String.valueOf(id),
                nombre,
                strMinimo,
                strMaximo,
                rango
            });
        }


        if (tablaCirculos.getColumnCount() >= 5) {
            tablaCirculos.getColumnModel().getColumn(0).setPreferredWidth(60);
            tablaCirculos.getColumnModel().getColumn(1).setPreferredWidth(150);
            tablaCirculos.getColumnModel().getColumn(2).setPreferredWidth(100);
            tablaCirculos.getColumnModel().getColumn(3).setPreferredWidth(100);
            tablaCirculos.getColumnModel().getColumn(4).setPreferredWidth(180);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error al cargar círculos: " + e.getMessage(), 
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        boolean hayErrores = false;
        
        txtNombreCirculo.setBackground(Color.WHITE);
        txtMontoMinimo.setBackground(Color.WHITE);
        txtMontoMaximo.setBackground(Color.WHITE);
        

        String nombre = txtNombreCirculo.getText().trim();
        if (nombre.isEmpty()) {
            errores.append("• El nombre es obligatorio\n");
            txtNombreCirculo.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        }
        

        String strMinimo = txtMontoMinimo.getText().trim();
        if (strMinimo.isEmpty()) {
            errores.append("• El monto mínimo es obligatorio\n");
            txtMontoMinimo.setBackground(new Color(255, 200, 200));
            hayErrores = true;
        } else {
            try {
                double minimo = Double.parseDouble(strMinimo);
                if (minimo < 0) {
                    errores.append("• El monto mínimo no puede ser negativo\n");
                    txtMontoMinimo.setBackground(new Color(255, 200, 200));
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errores.append("• El monto mínimo debe ser un número válido\n");
                txtMontoMinimo.setBackground(new Color(255, 200, 200));
                hayErrores = true;
            }
        }
        

        String strMaximo = txtMontoMaximo.getText().trim();
        if (!strMaximo.isEmpty()) {
            try {
                double maximo = Double.parseDouble(strMaximo);
                double minimo = Double.parseDouble(txtMontoMinimo.getText().trim());
                
                if (maximo < 0) {
                    errores.append("• El monto máximo no puede ser negativo\n");
                    txtMontoMaximo.setBackground(new Color(255, 200, 200));
                    hayErrores = true;
                }
                
                if (maximo <= minimo) {
                    errores.append("• El monto máximo debe ser mayor al mínimo\n");
                    txtMontoMaximo.setBackground(new Color(255, 200, 200));
                    hayErrores = true;
                }
            } catch (NumberFormatException e) {
                errores.append("• El monto máximo debe ser un número válido\n");
                txtMontoMaximo.setBackground(new Color(255, 200, 200));
                hayErrores = true;
            }
        }
        
        if (hayErrores) {
            JOptionPane.showMessageDialog(this, 
                "Por favor corrija los siguientes errores:\n\n" + errores.toString(), 
                "Errores de validación", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
 private void agregarCirculo() {
    if (!validarCampos()) {
        return;
    }
    

    String sql = "INSERT INTO CirculoDonador (nombre, minimo, maximo) VALUES (?, ?, ?)";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        String nombre = txtNombreCirculo.getText().trim();
        double minimo = Double.parseDouble(txtMontoMinimo.getText().trim());
        String strMaximo = txtMontoMaximo.getText().trim();
        
        ps.setString(1, nombre);
        ps.setDouble(2, minimo);
        
        if (strMaximo.isEmpty()) {
            ps.setNull(3, Types.DOUBLE);
        } else {
            ps.setDouble(3, Double.parseDouble(strMaximo));
        }
        

        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int idGenerado = generatedKeys.getInt(1);
                

                txtIdCirculo.setText(String.valueOf(idGenerado));
                txtIdCirculo.setForeground(Color.BLUE);
                
                System.out.println("ID generado: " + idGenerado);
                
                JOptionPane.showMessageDialog(this, 
                    "✅ Círculo creado exitosamente!\n\n" +
                    "🔹 ID asignado: " + idGenerado + "\n" +
                    "🔹 Nombre: " + nombre + "\n" +
                    "🔹 Rango: " + obtenerRango(minimo, strMaximo.isEmpty() ? null : Double.parseDouble(strMaximo)), 
                    "Registro exitoso", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {

                txtIdCirculo.setText("(Nuevo)");
                JOptionPane.showMessageDialog(this, 
                    "Círculo creado, pero no se pudo obtener el ID",
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
            

            cargarTabla();
            botonEditar.setEnabled(true);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "❌ Error al guardar en la base de datos:\n" + e.getMessage(), 
            "Error de base de datos", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}


private String obtenerRango(double minimo, Double maximo) {
    if (maximo == null) {
        return String.format("$%,.2f y más", minimo);
    } else {
        return String.format("$%,.2f - $%,.2f", minimo, maximo);
    }
}
private void cargarCirculoDesdeTabla() {
    int fila = tablaCirculos.getSelectedRow();
    if (fila == -1) {
        return;
    }
    

    DefaultTableModel modelo = (DefaultTableModel) tablaCirculos.getModel();
    String idCirculo = modelo.getValueAt(fila, 0).toString();
    buscarCirculoPorID(idCirculo);
}
    
private void buscarCirculoPorID(String idCirculo) {
    String sql = """
        SELECT 
            idCirculo,
            nombre,
            minimo,
            maximo
        FROM CirculoDonador
        WHERE idCirculo = ?
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, idCirculo);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
      
            txtIdCirculo.setText(rs.getString("idCirculo"));
            txtIdCirculo.setForeground(Color.BLUE);
            
            txtNombreCirculo.setText(rs.getString("nombre"));
            txtMontoMinimo.setText(String.valueOf(rs.getDouble("minimo"))); 
            
            Double maximo = rs.getDouble("maximo"); 
            if (rs.wasNull()) {
                txtMontoMaximo.setText("");
            } else {
                txtMontoMaximo.setText(String.valueOf(maximo));
            }
            
            botonEditar.setEnabled(true);
            
            JOptionPane.showMessageDialog(this, 
                "📋 Datos cargados para edición\n\n" +
                "ID: " + rs.getString("idCirculo") + "\n" +
                "Nombre: " + rs.getString("nombre") + "\n\n" +
                "Modifique y haga clic en 'Editar'.",
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
    
    private void editarCirculo() {
        if (txtIdCirculo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Primero seleccione un círculo para editar",
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validarCampos()) {
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de actualizar los datos del círculo?\n\n" +
            "ID: " + txtIdCirculo.getText() + "\n" +
            "Nombre: " + txtNombreCirculo.getText().trim(),
            "Confirmar actualización", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            actualizarCirculoEnBD();
        }
    }
    
    private void actualizarCirculoEnBD() {
        String sql = """
            UPDATE CirculoDonador SET 
                nombre = ?, 
                minimo = ?, 
                maximo = ?
            WHERE idCirculo = ?
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String nombre = txtNombreCirculo.getText().trim();
            double minimo = Double.parseDouble(txtMontoMinimo.getText().trim());
            String strMaximo = txtMontoMaximo.getText().trim();
            int idCirculo = Integer.parseInt(txtIdCirculo.getText());
            
            ps.setString(1, nombre);
            ps.setDouble(2, minimo);
            
            if (strMaximo.isEmpty()) {
                ps.setNull(3, Types.DOUBLE);
            } else {
                ps.setDouble(3, Double.parseDouble(strMaximo));
            }
            
            ps.setInt(4, idCirculo);
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Círculo actualizado exitosamente",
                    "Actualización exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                cargarTabla();
                limpiarCampos();
                botonEditar.setEnabled(false);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar: " + e.getMessage(),
                "Error de base de datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void eliminarCirculo() {
        int fila = tablaCirculos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un círculo de la tabla para eliminar",
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultTableModel modelo = (DefaultTableModel) tablaCirculos.getModel();
        String idCirculo = modelo.getValueAt(fila, 0).toString();
        String nombre = modelo.getValueAt(fila, 1).toString();
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el círculo?\n\n" +
            "ID: " + idCirculo + "\n" +
            "Nombre: " + nombre + "\n\n" +
            "Advertencia: Esta acción no se puede deshacer.",
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM CirculoDonador WHERE idCirculo = ?";
            
            try (Connection con = ConexionBD.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                
                ps.setString(1, idCirculo);
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Círculo eliminado exitosamente",
                        "Eliminación exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    cargarTabla();
                    limpiarCampos();
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
    
    private void buscarCirculo() {
        String criterio = JOptionPane.showInputDialog(this, 
            "Ingrese nombre o ID del círculo a buscar:", 
            "Buscar Círculo", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (criterio == null || criterio.trim().isEmpty()) {
            return;
        }
        
        String sql = """
            SELECT 
                idCirculo,
                nombre,
                minimo,
                maximo
            FROM CirculoDonador
            WHERE nombre LIKE ? OR idCirculo LIKE ?
            ORDER BY nombre
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String parametro = "%" + criterio + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                txtIdCirculo.setText(rs.getString("idCirculo"));
                txtNombreCirculo.setText(rs.getString("nombre"));
                txtMontoMinimo.setText(String.valueOf(rs.getDouble("minimo")));
                
                Double maximo = rs.getDouble("maximo");
                if (rs.wasNull()) {
                    txtMontoMaximo.setText("");
                } else {
                    txtMontoMaximo.setText(String.valueOf(maximo));
                }
                
                botonEditar.setEnabled(true);
                
                JOptionPane.showMessageDialog(this, 
                    "Círculo encontrado\n\nID: " + rs.getString("idCirculo") + 
                    "\nNombre: " + rs.getString("nombre"),
                    "Búsqueda exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No se encontró ningún círculo con: " + criterio,
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

    txtIdCirculo.setText("");
    txtIdCirculo.setForeground(Color.BLACK);
    
    txtNombreCirculo.setText("");
    txtMontoMinimo.setText("");
    txtMontoMaximo.setText("");
    

    txtNombreCirculo.setBackground(Color.WHITE);
    txtMontoMinimo.setBackground(Color.WHITE);
    txtMontoMaximo.setBackground(Color.WHITE);

    botonEditar.setEnabled(false);
    

    tablaCirculos.clearSelection();
    

    txtNombreCirculo.requestFocus();
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtIdCirculo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNombreCirculo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtMontoMinimo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtMontoMaximo = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaCirculos = new javax.swing.JTable();
        botonRegistrar = new javax.swing.JButton();
        botonEliminar = new javax.swing.JButton();
        botonBuscar = new javax.swing.JButton();
        botonEditar = new javax.swing.JButton();
        botonLimpiar = new javax.swing.JButton();
        botonVolver = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

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
        setBackground(new java.awt.Color(255, 153, 153));

        jPanel1.setBackground(new java.awt.Color(204, 0, 51));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel1.setText("CIRCULO DE DONADORES");

        jLabel2.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\users.png")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(190, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(178, 178, 178))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("id del Circulo: ");

        txtIdCirculo.setEditable(false);

        jLabel4.setText("Nombre: ");

        jLabel5.setText("Monto minimo:");

        jLabel6.setText("Monto maximo:");

        jScrollPane2.setOpaque(false);

        tablaCirculos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Nombre", "Monto Maximo", "Monto Minimo", "Rango"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tablaCirculos);

        botonRegistrar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\check.png")); // NOI18N
        botonRegistrar.setText("Registrar circulo");
        botonRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRegistrarActionPerformed(evt);
            }
        });

        botonEliminar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\cross.png")); // NOI18N
        botonEliminar.setText("Eliminar circulo");
        botonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarActionPerformed(evt);
            }
        });

        botonBuscar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\search-alt.png")); // NOI18N
        botonBuscar.setText("Buscar circulo");
        botonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonBuscarActionPerformed(evt);
            }
        });

        botonEditar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\edit.png")); // NOI18N
        botonEditar.setText("Editar Circulo");
        botonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEditarActionPerformed(evt);
            }
        });

        botonLimpiar.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\broom.png")); // NOI18N
        botonLimpiar.setText("Limpiar Casillas");
        botonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLimpiarActionPerformed(evt);
            }
        });

        botonVolver.setIcon(new javax.swing.ImageIcon("C:\\Users\\contr\\Downloads\\arrow-small-left.png")); // NOI18N
        botonVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVolverActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Lista de circulos existentes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(495, 495, 495)
                .addComponent(botonLimpiar)
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botonVolver))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(botonRegistrar)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(jLabel5)
                        .addComponent(jLabel6)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIdCirculo, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtMontoMaximo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                                .addComponent(txtMontoMinimo)
                                .addComponent(txtNombreCirculo))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(botonEliminar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonBuscar)
                        .addGap(18, 18, 18)
                        .addComponent(botonEditar))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtIdCirculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonLimpiar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNombreCirculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtMontoMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtMontoMaximo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonRegistrar)
                    .addComponent(botonEliminar)
                    .addComponent(botonBuscar)
                    .addComponent(botonEditar))
                .addGap(73, 73, 73)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(botonVolver)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRegistrarActionPerformed
        agregarCirculo();
    }//GEN-LAST:event_botonRegistrarActionPerformed

    private void botonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarActionPerformed
        eliminarCirculo();
    }//GEN-LAST:event_botonEliminarActionPerformed

    private void botonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonBuscarActionPerformed
       buscarCirculo();
    }//GEN-LAST:event_botonBuscarActionPerformed

    private void botonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarActionPerformed
       editarCirculo();
    }//GEN-LAST:event_botonEditarActionPerformed

    private void botonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_botonLimpiarActionPerformed

    private void botonVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVolverActionPerformed
       Configuraciones c = new Configuraciones();
        c.setVisible(true);
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
        java.awt.EventQueue.invokeLater(() -> new CirculoDonador().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonBuscar;
    private javax.swing.JButton botonEditar;
    private javax.swing.JButton botonEliminar;
    private javax.swing.JButton botonLimpiar;
    private javax.swing.JButton botonRegistrar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tablaCirculos;
    private javax.swing.JTextField txtIdCirculo;
    private javax.swing.JTextField txtMontoMaximo;
    private javax.swing.JTextField txtMontoMinimo;
    private javax.swing.JTextField txtNombreCirculo;
    // End of variables declaration//GEN-END:variables
}
