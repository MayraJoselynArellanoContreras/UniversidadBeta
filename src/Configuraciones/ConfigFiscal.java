/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Configuraciones;

import Configuraciones.Configuraciones;
import util.ConexionBD;
import java.awt.Color;
import java.sql.*;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author contr
 */
public class ConfigFiscal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ConfigFiscal.class.getName());
     private DefaultTableModel modeloTabla;
    /**
     * Creates new form ConfigFiscal
     */
    public ConfigFiscal() {
        initComponents();
        // --- Configurar navegación por tabulación ---
this.setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {
    @Override
    public java.awt.Component getComponentAfter(java.awt.Container aContainer, java.awt.Component aComponent) {

        if (aComponent.equals(txtID)) return txtNombre;
        if (aComponent.equals(txtNombre)) return txtFechaInicio;
        if (aComponent.equals(txtFechaInicio)) return txtFechaFin;
        if (aComponent.equals(txtFechaFin)) return txtEstado;

        if (aComponent.equals(txtEstado)) return botonActivar;
        if (aComponent.equals(botonActivar)) return botonEditar;
        if (aComponent.equals(botonEditar)) return botonEliminar;
        if (aComponent.equals(botonEliminar)) return tablaAnios;
        if (aComponent.equals(tablaAnios)) return botonVolver;

        return txtID; // inicio del ciclo
    }

    @Override
    public java.awt.Component getComponentBefore(java.awt.Container aContainer, java.awt.Component aComponent) {

        if (aComponent.equals(txtNombre)) return txtID;
        if (aComponent.equals(txtFechaInicio)) return txtNombre;
        if (aComponent.equals(txtFechaFin)) return txtFechaInicio;
        if (aComponent.equals(txtEstado)) return txtFechaFin;

        if (aComponent.equals(botonActivar)) return txtEstado;
        if (aComponent.equals(botonEditar)) return botonActivar;
        if (aComponent.equals(botonEliminar)) return botonEditar;
        if (aComponent.equals(tablaAnios)) return botonEliminar;
        if (aComponent.equals(botonVolver)) return tablaAnios;

        return botonVolver; // final del ciclo
    }

    @Override public java.awt.Component getDefaultComponent(java.awt.Container aContainer) { return txtID; }
    @Override public java.awt.Component getFirstComponent(java.awt.Container aContainer) { return txtID; }
    @Override public java.awt.Component getLastComponent(java.awt.Container aContainer) { return botonVolver; }
});

        setLocationRelativeTo(null);
        configurarTabla();
        cargarTabla();
        configurarFechaActual();
    }
    
    private void configurarTabla() {
        String[] columnas = {"ID", "Año Fiscal", "Inicio", "Fin", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaAnios.setModel(modeloTabla);
        
        // Doble clic para cargar datos
        tablaAnios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    cargarAnioDesdeTabla();
                }
            }
        });
    }
    
    private void configurarFechaActual() {
        Calendar cal = Calendar.getInstance();
        int añoActual = cal.get(Calendar.YEAR);
        
        if (cal.get(Calendar.MONTH) >= 6) { // Julio o después
            txtFechaInicio.setText(añoActual + "-07-01");
            txtFechaFin.setText((añoActual + 1) + "-06-30");
            txtNombre.setText(añoActual + "-" + (añoActual + 1));
        } else { // Antes de julio
            txtFechaInicio.setText((añoActual - 1) + "-07-01");
            txtFechaFin.setText(añoActual + "-06-30");
            txtNombre.setText((añoActual - 1) + "-" + añoActual);
        }
    }

    
    private void cargarTabla() {
        String sql = """
            SELECT 
                idAnioFiscal,
                nombre,
                CONVERT(VARCHAR, fechaInicio, 23) as inicio,
                CONVERT(VARCHAR, fechaFin, 23) as fin,
                CASE WHEN activo = 1 THEN 'SÍ' ELSE 'NO' END as activo
            FROM AnioFiscal
            ORDER BY fechaInicio DESC
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            modeloTabla.setRowCount(0);
            
            while (rs.next()) {
                modeloTabla.addRow(new Object[]{
                    rs.getString("idAnioFiscal"),
                    rs.getString("nombre"),
                    rs.getString("inicio"),
                    rs.getString("fin"),
                    rs.getString("activo")
                });
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar años fiscales: " + e.getMessage(),
                "Error de base de datos",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cargarAnioDesdeTabla() {
        int fila = tablaAnios.getSelectedRow();
        if (fila == -1) return;
        
        String idAnio = modeloTabla.getValueAt(fila, 0).toString();
        buscarAnioPorID(idAnio);
    }
    
    private void buscarAnioPorID(String idAnio) {
        String sql = "SELECT idAnioFiscal, nombre, fechaInicio, fechaFin, activo FROM AnioFiscal WHERE idAnioFiscal = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, idAnio);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                txtID.setText(idAnio);
                txtNombre.setText(rs.getString("nombre"));
                txtFechaInicio.setText(rs.getDate("fechaInicio").toString());
                txtFechaFin.setText(rs.getDate("fechaFin").toString());
                
                boolean activo = rs.getBoolean("activo");
                if (activo) {
                    txtEstado.setText("ACTIVO");
                    txtEstado.setForeground(Color.GREEN);
                } else {
                    txtEstado.setText("INACTIVO");
                    txtEstado.setForeground(Color.RED);
                }
                
                botonEditar.setEnabled(true);
                botonEliminar.setEnabled(true);
                botonActivar.setEnabled(!activo);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
            return false;
        }
        
        if (txtFechaInicio.getText().trim().isEmpty() || txtFechaFin.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Las fechas son obligatorias");
            return false;
        }
        
        // Validar formato de fecha
        if (!txtFechaInicio.getText().matches("\\d{4}-\\d{2}-\\d{2}") || 
            !txtFechaFin.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use: YYYY-MM-DD");
            return false;
        }
        
        // Validar que inicio sea 1 de julio
        if (!txtFechaInicio.getText().endsWith("-07-01")) {
            JOptionPane.showMessageDialog(this, "La fecha de inicio debe ser 1 de julio (YYYY-07-01)");
            return false;
        }
        
        // Validar que fin sea 30 de junio
        if (!txtFechaFin.getText().endsWith("-06-30")) {
            JOptionPane.showMessageDialog(this, "La fecha de fin debe ser 30 de junio (YYYY-06-30)");
            return false;
        }
        
        return true;
    }
    
    private void limpiarCampos() {
        txtID.setText("");
        txtNombre.setText("");
        configurarFechaActual();
        txtEstado.setText("");
        txtEstado.setForeground(Color.BLACK);
        
        botonEditar.setEnabled(false);
        botonEliminar.setEnabled(false);
        botonActivar.setEnabled(false);
        
        tablaAnios.clearSelection();
        txtNombre.requestFocus();
    }
    
     private void agregar() {
        if (!validarCampos()) return;
    
    // Verificar que no se traslape con otros años fiscales
    String verificarSQL = """
        SELECT COUNT(*) FROM AnioFiscal 
        WHERE (fechaInicio <= ? AND fechaFin >= ?)
           OR (fechaInicio <= ? AND fechaFin >= ?)
           OR (? <= fechaFin AND ? >= fechaInicio)
        """;
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(verificarSQL)) {
        
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        
        ps.setString(1, fechaFin);
        ps.setString(2, fechaInicio);
        ps.setString(3, fechaInicio);
        ps.setString(4, fechaFin);
        ps.setString(5, fechaInicio);
        ps.setString(6, fechaFin);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(this,
                "El período fiscal se traslapa con otro existente",
                "Error de traslape",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al verificar traslape: " + e.getMessage());
        return;
    }
    
    // Insertar nuevo año fiscal (inactivo por defecto)
    String insertSQL = "INSERT INTO AnioFiscal (nombre, fechaInicio, fechaFin, activo) VALUES (?, ?, ?, 0)";
    
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
        
        String nombre = txtNombre.getText().trim();
        String fechaInicio = txtFechaInicio.getText().trim();
        String fechaFin = txtFechaFin.getText().trim();
        
        ps.setString(1, nombre);
        ps.setString(2, fechaInicio);
        ps.setString(3, fechaFin);
        
        int filas = ps.executeUpdate();
        if (filas > 0) {
            // Obtener ID generado (OPCIONAL - solo para mostrar)
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int idGenerado = keys.getInt(1);
                JOptionPane.showMessageDialog(this,
                    "✅ Año fiscal creado exitosamente\n\n" +
                    "ID asignado: " + idGenerado + "\n" +
                    "Nombre: " + nombre + "\n" +
                    "Período: " + fechaInicio + " al " + fechaFin + "\n\n" +
                    "Nota: Para activarlo, selecciónelo y haga clic en 'Activar'",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "✅ Año fiscal creado exitosamente",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Limpiar y recargar
            limpiarCampos();
            cargarTabla();
            configurarFechaActual(); // Volver a poner fecha actual
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
            "❌ Error al crear año fiscal:\n" + e.getMessage(),
            "Error de base de datos",
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }
     
     private void editar() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un año fiscal para editar");
            return;
        }
        
        if (!validarCampos()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de actualizar el año fiscal?\n\n" +
            "ID: " + txtID.getText() + "\n" +
            "Nombre: " + txtNombre.getText().trim(),
            "Confirmar actualización",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        String sql = "UPDATE AnioFiscal SET nombre = ?, fechaInicio = ?, fechaFin = ? WHERE idAnioFiscal = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, txtNombre.getText().trim());
            ps.setString(2, txtFechaInicio.getText().trim());
            ps.setString(3, txtFechaFin.getText().trim());
            ps.setString(4, txtID.getText());
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "Año fiscal actualizado");
                cargarTabla();
                limpiarCampos();
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        }
    }
     
     private void eliminar() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un año fiscal para eliminar");
            return;
        }
        
        // Verificar si es el año activo
        String verificarSQL = "SELECT activo FROM AnioFiscal WHERE idAnioFiscal = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(verificarSQL)) {
            
            ps.setString(1, txtID.getText());
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getBoolean("activo")) {
                JOptionPane.showMessageDialog(this,
                    "No se puede eliminar el año fiscal activo.\n" +
                    "Primero active otro año fiscal.",
                    "No se puede eliminar",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar: " + e.getMessage());
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el año fiscal?\n\n" +
            "ID: " + txtID.getText() + "\n" +
            "Nombre: " + txtNombre.getText().trim() + "\n\n" +
            "Advertencia: Esta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        String sql = "DELETE FROM AnioFiscal WHERE idAnioFiscal = ?";
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, txtID.getText());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "Año fiscal eliminado");
                cargarTabla();
                limpiarCampos();
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }
     
     private void activar() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un año fiscal para activar");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Activar este año fiscal como el activo?\n\n" +
            "Todos los demás años se desactivarán automáticamente.",
            "Activar año fiscal",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        String sql = """
            UPDATE AnioFiscal SET activo = 0;
            UPDATE AnioFiscal SET activo = 1 WHERE idAnioFiscal = ?;
            """;
        
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, txtID.getText());
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(this,
                "✅ Año fiscal activado correctamente\n\n" +
                "Ahora todos los reportes y donativos se asociarán a este año.",
                "Activación exitosa",
                JOptionPane.INFORMATION_MESSAGE);
            
            cargarTabla();
            buscarAnioPorID(txtID.getText()); // Refrescar estado
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al activar: " + e.getMessage());
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtFechaFin = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtID = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtEstado = new javax.swing.JTextField();
        botonActivar = new javax.swing.JButton();
        botonEditar = new javax.swing.JButton();
        botonEliminar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaAnios = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txtFechaInicio = new javax.swing.JTextField();
        botonVolver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(107, 114, 128));

        jPanel2.setBackground(new java.awt.Color(30, 58, 138));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 24)); // NOI18N
        jLabel1.setText("CONFIGURACION DE AÑOS FISCALES");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel1)
                .addContainerGap(85, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Nuevo Año Fiscal");

        jLabel3.setText("Nombre:");

        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });

        jLabel4.setText("Fecha de inicio: ");

        jLabel5.setText("Fecha de fin:");

        jLabel6.setText("Id:");

        txtID.setEditable(false);

        jLabel7.setText("Estado:");

        txtEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEstadoActionPerformed(evt);
            }
        });

        botonActivar.setText("Activar año Ffscal");
        botonActivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonActivarActionPerformed(evt);
            }
        });

        botonEditar.setText("Editar año fiscal");
        botonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEditarActionPerformed(evt);
            }
        });

        botonEliminar.setText("Eliminar año fiscal");
        botonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonEliminarActionPerformed(evt);
            }
        });

        tablaAnios.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaAnios);

        jLabel8.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel8.setText(" Años Fiscales Existentes    ");

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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(50, 50, 50)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(botonActivar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtEstado, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                                    .addComponent(txtFechaFin)
                                    .addComponent(txtFechaInicio))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(botonEditar, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(botonEliminar, javax.swing.GroupLayout.Alignment.TRAILING)))))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(201, 201, 201)
                            .addComponent(jLabel2))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(botonVolver))))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonActivar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(botonEditar)
                    .addComponent(txtFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(botonEliminar)))
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(46, 46, 46)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(botonVolver))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEstadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEstadoActionPerformed

    private void botonActivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonActivarActionPerformed
       activar();
    }//GEN-LAST:event_botonActivarActionPerformed

    private void botonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEditarActionPerformed
        editar();
    }//GEN-LAST:event_botonEditarActionPerformed

    private void botonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonEliminarActionPerformed
        eliminar();
    }//GEN-LAST:event_botonEliminarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ConfigFiscal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonActivar;
    private javax.swing.JButton botonEditar;
    private javax.swing.JButton botonEliminar;
    private javax.swing.JButton botonVolver;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaAnios;
    private javax.swing.JTextField txtEstado;
    private javax.swing.JTextField txtFechaFin;
    private javax.swing.JTextField txtFechaInicio;
    private javax.swing.JTextField txtID;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
