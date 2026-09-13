package com.mycompany.minipc.gui;

import com.mycompany.minipc.core.BCP;
import com.mycompany.minipc.isa.Instruccion;
import com.mycompany.minipc.isa.RegistroID;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Ventana principal del simulador Mini PC.
 *
 * Implementa VistaPrincipal: el controlador le pide que muestre cosas y
 * ella solo dibuja. No contiene logica de simulacion; cada boton delega
 * en una sola llamada al controlador.
 */
public class VentanaPrincipal extends javax.swing.JFrame implements VistaPrincipal {

    private static final long serialVersionUID = 1L;

    /** Formato de la hora que precede cada linea de la consola. */
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Carpetas donde buscar los programas de ejemplo al abrir el dialogo. */
    private static final String[] CARPETAS_EJEMPLO = {"../../Ejemplo", "../Ejemplo", "Ejemplo"};

    private final ControladorPrincipal controlador;

    /**
     * Crea la ventana y la deja lista para usarse.
     */
    public VentanaPrincipal() {
        initComponents();
        this.controlador = new ControladorPrincipal(this);

        tblInstrucciones.setModel(controlador.getModeloInstrucciones());
        tblInstrucciones.setDefaultRenderer(Object.class, controlador.getRenderInstrucciones());
        ajustarAnchos(tblInstrucciones.getColumnModel(), new int[]{40, 160, 190});

        tblMemoria.setModel(controlador.getModeloMemoria());
        tblMemoria.setDefaultRenderer(Object.class, controlador.getRenderMemoria());
        ajustarAnchos(tblMemoria.getColumnModel(), new int[]{50, 70, 130, 150});

        controlador.inicializarVista();
    }

    /**
     * Fija el ancho preferido de cada columna.
     *
     * @param columnas modelo de columnas de la tabla
     * @param anchos   ancho deseado para cada una, en pixeles
     */
    private void ajustarAnchos(TableColumnModel columnas, int[] anchos) {
        for (int i = 0; i < anchos.length && i < columnas.getColumnCount(); i++) {
            columnas.getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    // ------------------------------------------------------------------
    // Implementacion de VistaPrincipal
    // ------------------------------------------------------------------

    /**
     * El modelo de la tabla lo mantiene el controlador, asi que aqui solo
     * hay que asegurarse de que la vista vuelva al principio de la lista.
     */
    @Override
    public void mostrarInstrucciones(List<Instruccion> programa) {
        tblInstrucciones.clearSelection();
        tblInstrucciones.scrollRectToVisible(tblInstrucciones.getCellRect(0, 0, true));
        tblInstrucciones.repaint();
    }

    @Override
    public void resaltarInstruccion(int indiceFila) {
        tblInstrucciones.repaint();
        if (indiceFila >= 0 && indiceFila < tblInstrucciones.getRowCount()) {
            tblInstrucciones.scrollRectToVisible(
                    tblInstrucciones.getCellRect(indiceFila, 0, true));
        }
    }

    @Override
    public void refrescarMemoria() {
        ((AbstractTableModel) tblMemoria.getModel()).fireTableDataChanged();
        int direccion = controlador.getProcesador().getPc();
        if (controlador.getProcesador().hayPrograma()
                && direccion < tblMemoria.getRowCount()) {
            tblMemoria.scrollRectToVisible(tblMemoria.getCellRect(direccion, 0, true));
        }
    }

    @Override
    public void mostrarBCP(BCP bcp) {
        if (bcp == null) {
            limpiarBCP();
            return;
        }
        lblPidValor.setText(String.valueOf(bcp.getPid()));
        lblProgramaValor.setText(bcp.getNombrePrograma());
        lblEstadoBcpValor.setText(bcp.getEstado().name());
        lblPcValor.setText(String.valueOf(bcp.getPc()));
        lblIrBinValor.setText(bcp.getIrBinario());
        lblIrTextoValor.setText(bcp.getIrTexto().isEmpty() ? "-" : bcp.getIrTexto());
        lblAcValor.setText(String.valueOf(bcp.getAc()));
        lblAxValor.setText(String.valueOf(bcp.getRegistro(RegistroID.AX)));
        lblBxValor.setText(String.valueOf(bcp.getRegistro(RegistroID.BX)));
        lblCxValor.setText(String.valueOf(bcp.getRegistro(RegistroID.CX)));
        lblDxValor.setText(String.valueOf(bcp.getRegistro(RegistroID.DX)));
        lblBaseValor.setText(String.valueOf(bcp.getDireccionBase()));
        lblLimiteValor.setText(bcp.getLimite() + " posiciones");
        lblEjecutadasValor.setText(String.valueOf(bcp.getInstruccionesEjecutadas()));
    }

    /**
     * Deja los catorce campos del BCP en blanco, cuando no hay proceso.
     */
    private void limpiarBCP() {
        lblPidValor.setText("-");
        lblProgramaValor.setText("-");
        lblEstadoBcpValor.setText("-");
        lblPcValor.setText("-");
        lblIrBinValor.setText("-");
        lblIrTextoValor.setText("-");
        lblAcValor.setText("0");
        lblAxValor.setText("0");
        lblBxValor.setText("0");
        lblCxValor.setText("0");
        lblDxValor.setText("0");
        lblBaseValor.setText("-");
        lblLimiteValor.setText("-");
        lblEjecutadasValor.setText("0");
    }

    @Override
    public void escribirEnConsola(String mensaje) {
        txtConsola.append("[" + LocalTime.now().format(HORA) + "] " + mensaje + "\n");
        txtConsola.setCaretPosition(txtConsola.getDocument().getLength());
    }

    @Override
    public void limpiarConsola() {
        txtConsola.setText("");
    }

    @Override
    public void mostrarErrores(String titulo, List<String> mensajes) {
        JOptionPane.showMessageDialog(this, String.join("\n", mensajes),
                titulo, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actualizarBotones(boolean hayPrograma, boolean enEjecucion, boolean termino) {
        btnCargar.setEnabled(!enEjecucion);
        btnConfig.setEnabled(!enEjecucion);
        btnEjecutar.setEnabled(hayPrograma && !enEjecucion && !termino);
        btnPaso.setEnabled(hayPrograma && !enEjecucion && !termino);
        btnReiniciar.setEnabled(hayPrograma && !enEjecucion);
        btnLimpiar.setEnabled(hayPrograma && !enEjecucion);
        btnEstadisticas.setEnabled(hayPrograma && !enEjecucion);
    }

    @Override
    public void actualizarBarraContexto(String nombreArchivo, String estado) {
        lblArchivoValor.setText(nombreArchivo);
        lblEstadoValor.setText(estado);
    }

    @Override
    public void actualizarUsoMemoria(int porcentaje) {
        pbUsoMemoria.setValue(porcentaje);
        pbUsoMemoria.setString(porcentaje + " %");
    }

    @Override
    public File seleccionarArchivoAsm() {
        JFileChooser selector = new JFileChooser(carpetaInicial());
        selector.setDialogTitle("Seleccionar programa en ensamblador");
        selector.setAcceptAllFileFilterUsed(false);
        selector.setFileFilter(new FileNameExtensionFilter(
                "Archivos de ensamblador (*.asm)", "asm"));
        return selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
                ? selector.getSelectedFile() : null;
    }

    /**
     * Busca la carpeta de programas de ejemplo para abrir el dialogo ahi.
     *
     * @return la primera carpeta que exista, o null para usar la del usuario
     */
    private File carpetaInicial() {
        for (String ruta : CARPETAS_EJEMPLO) {
            File carpeta = new File(ruta);
            if (carpeta.isDirectory()) {
                return carpeta;
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlSuperior = new javax.swing.JPanel();
        tbBarra = new javax.swing.JToolBar();
        btnCargar = new javax.swing.JButton();
        sepEjecucion = new javax.swing.JToolBar.Separator();
        btnEjecutar = new javax.swing.JButton();
        btnPaso = new javax.swing.JButton();
        btnReiniciar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        sepConfiguracion = new javax.swing.JToolBar.Separator();
        btnConfig = new javax.swing.JButton();
        btnEstadisticas = new javax.swing.JButton();
        pnlContexto = new javax.swing.JPanel();
        lblArchivo = new javax.swing.JLabel();
        lblArchivoValor = new javax.swing.JLabel();
        lblSepA = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        lblEstadoValor = new javax.swing.JLabel();
        lblSepB = new javax.swing.JLabel();
        lblUsoMemoria = new javax.swing.JLabel();
        pbUsoMemoria = new javax.swing.JProgressBar();
        spPrincipal = new javax.swing.JSplitPane();
        pnlInstrucciones = new javax.swing.JPanel();
        scrInstrucciones = new javax.swing.JScrollPane();
        tblInstrucciones = new javax.swing.JTable();
        spSecundario = new javax.swing.JSplitPane();
        pnlMemoria = new javax.swing.JPanel();
        scrMemoria = new javax.swing.JScrollPane();
        tblMemoria = new javax.swing.JTable();
        pnlBCP = new javax.swing.JPanel();
        scrBcp = new javax.swing.JScrollPane();
        pnlBcpInterior = new javax.swing.JPanel();
        pnlBcpCampos = new javax.swing.JPanel();
        pnlProceso = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblPidValor = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblProgramaValor = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblEstadoBcpValor = new javax.swing.JLabel();
        pnlContextoCpu = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblPcValor = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblIrBinValor = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblIrTextoValor = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblAcValor = new javax.swing.JLabel();
        pnlRegistros = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblAxValor = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblBxValor = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblCxValor = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblDxValor = new javax.swing.JLabel();
        pnlMemoriaProceso = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        lblBaseValor = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblLimiteValor = new javax.swing.JLabel();
        pnlContabilidad = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        lblEjecutadasValor = new javax.swing.JLabel();
        pnlConsola = new javax.swing.JPanel();
        scrConsola = new javax.swing.JScrollPane();
        txtConsola = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mini PC - Simulador del ciclo de instruccion");
        getContentPane().setLayout(new java.awt.BorderLayout());

        pnlSuperior.setLayout(new javax.swing.BoxLayout(pnlSuperior, javax.swing.BoxLayout.Y_AXIS));

        tbBarra.setFloatable(false);
        tbBarra.setRollover(true);

        btnCargar.setText("Cargar .asm");
        btnCargar.setToolTipText("Selecciona un archivo de codigo ensamblador y lo carga en memoria");
        btnCargar.setFocusable(false);
        btnCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarActionPerformed(evt);
            }
        });
        tbBarra.add(btnCargar);
        tbBarra.add(sepEjecucion);

        btnEjecutar.setText("Ejecutar");
        btnEjecutar.setToolTipText("Ejecuta el programa completo de forma automatica");
        btnEjecutar.setEnabled(false);
        btnEjecutar.setFocusable(false);
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarActionPerformed(evt);
            }
        });
        tbBarra.add(btnEjecutar);

        btnPaso.setText("Paso a paso");
        btnPaso.setToolTipText("Ejecuta una sola instruccion");
        btnPaso.setEnabled(false);
        btnPaso.setFocusable(false);
        btnPaso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasoActionPerformed(evt);
            }
        });
        tbBarra.add(btnPaso);

        btnReiniciar.setText("Reiniciar");
        btnReiniciar.setToolTipText("Vuelve el procesador al inicio del programa sin descargarlo");
        btnReiniciar.setEnabled(false);
        btnReiniciar.setFocusable(false);
        btnReiniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReiniciarActionPerformed(evt);
            }
        });
        tbBarra.add(btnReiniciar);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.setToolTipText("Vacia la memoria de usuario, los registros, las tablas y la consola");
        btnLimpiar.setEnabled(false);
        btnLimpiar.setFocusable(false);
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });
        tbBarra.add(btnLimpiar);
        tbBarra.add(sepConfiguracion);

        btnConfig.setText("Configurar");
        btnConfig.setToolTipText("Tamano de memoria, limite de kernel y velocidad de ejecucion");
        btnConfig.setFocusable(false);
        btnConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigActionPerformed(evt);
            }
        });
        tbBarra.add(btnConfig);

        btnEstadisticas.setText("Estadisticas");
        btnEstadisticas.setToolTipText("Resumen de la ejecucion");
        btnEstadisticas.setEnabled(false);
        btnEstadisticas.setFocusable(false);
        btnEstadisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEstadisticasActionPerformed(evt);
            }
        });
        tbBarra.add(btnEstadisticas);

        pnlSuperior.add(tbBarra);

        pnlContexto.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblArchivo.setText("Archivo:");
        pnlContexto.add(lblArchivo);

        lblArchivoValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblArchivoValor.setText("(ninguno)");
        pnlContexto.add(lblArchivoValor);

        lblSepA.setText("     |     ");
        pnlContexto.add(lblSepA);

        lblEstado.setText("Estado:");
        pnlContexto.add(lblEstado);

        lblEstadoValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEstadoValor.setText("SIN PROGRAMA");
        pnlContexto.add(lblEstadoValor);

        lblSepB.setText("     |     ");
        pnlContexto.add(lblSepB);

        lblUsoMemoria.setText("Uso de memoria:");
        pnlContexto.add(lblUsoMemoria);

        pbUsoMemoria.setToolTipText("Porcentaje ocupado de la zona de usuario");
        pbUsoMemoria.setPreferredSize(new java.awt.Dimension(170, 18));
        pbUsoMemoria.setStringPainted(true);
        pnlContexto.add(pbUsoMemoria);

        pnlSuperior.add(pnlContexto);

        getContentPane().add(pnlSuperior, java.awt.BorderLayout.NORTH);

        spPrincipal.setDividerLocation(400);
        spPrincipal.setOneTouchExpandable(true);

        pnlInstrucciones.setBorder(javax.swing.BorderFactory.createTitledBorder("Instrucciones"));
        pnlInstrucciones.setLayout(new java.awt.BorderLayout());

        scrInstrucciones.setPreferredSize(new java.awt.Dimension(390, 430));

        tblInstrucciones.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblInstrucciones.setRowHeight(22);
        tblInstrucciones.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblInstrucciones.setShowVerticalLines(false);
        scrInstrucciones.setViewportView(tblInstrucciones);

        pnlInstrucciones.add(scrInstrucciones, java.awt.BorderLayout.CENTER);

        spPrincipal.setLeftComponent(pnlInstrucciones);

        spSecundario.setDividerLocation(340);
        spSecundario.setOneTouchExpandable(true);

        pnlMemoria.setBorder(javax.swing.BorderFactory.createTitledBorder("Memoria"));
        pnlMemoria.setLayout(new java.awt.BorderLayout());

        scrMemoria.setPreferredSize(new java.awt.Dimension(330, 430));

        tblMemoria.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblMemoria.setRowHeight(22);
        tblMemoria.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblMemoria.setShowVerticalLines(false);
        scrMemoria.setViewportView(tblMemoria);

        pnlMemoria.add(scrMemoria, java.awt.BorderLayout.CENTER);

        spSecundario.setLeftComponent(pnlMemoria);

        pnlBCP.setBorder(javax.swing.BorderFactory.createTitledBorder("BCP actual - CPU 1"));
        pnlBCP.setLayout(new java.awt.BorderLayout());

        scrBcp.setPreferredSize(new java.awt.Dimension(320, 430));

        pnlBcpInterior.setLayout(new java.awt.BorderLayout());

        pnlBcpCampos.setLayout(new javax.swing.BoxLayout(pnlBcpCampos, javax.swing.BoxLayout.Y_AXIS));

        pnlProceso.setBorder(javax.swing.BorderFactory.createTitledBorder("Proceso"));
        pnlProceso.setLayout(new java.awt.GridLayout(0, 2, 8, 4));

        jLabel1.setText("PID:");
        pnlProceso.add(jLabel1);

        lblPidValor.setText("-");
        pnlProceso.add(lblPidValor);

        jLabel2.setText("Programa:");
        pnlProceso.add(jLabel2);

        lblProgramaValor.setText("-");
        pnlProceso.add(lblProgramaValor);

        jLabel3.setText("Estado:");
        pnlProceso.add(jLabel3);

        lblEstadoBcpValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEstadoBcpValor.setText("-");
        pnlProceso.add(lblEstadoBcpValor);

        pnlBcpCampos.add(pnlProceso);

        pnlContextoCpu.setBorder(javax.swing.BorderFactory.createTitledBorder("Contexto del CPU"));
        pnlContextoCpu.setLayout(new java.awt.GridLayout(0, 2, 8, 4));

        jLabel4.setText("PC:");
        pnlContextoCpu.add(jLabel4);

        lblPcValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPcValor.setText("-");
        pnlContextoCpu.add(lblPcValor);

        jLabel5.setText("IR (binario):");
        pnlContextoCpu.add(jLabel5);

        lblIrBinValor.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        lblIrBinValor.setText("-");
        pnlContextoCpu.add(lblIrBinValor);

        jLabel6.setText("IR (texto):");
        pnlContextoCpu.add(jLabel6);

        lblIrTextoValor.setText("-");
        pnlContextoCpu.add(lblIrTextoValor);

        jLabel7.setText("AC:");
        pnlContextoCpu.add(jLabel7);

        lblAcValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblAcValor.setText("0");
        pnlContextoCpu.add(lblAcValor);

        pnlBcpCampos.add(pnlContextoCpu);

        pnlRegistros.setBorder(javax.swing.BorderFactory.createTitledBorder("Registros de proposito general"));
        pnlRegistros.setLayout(new java.awt.GridLayout(0, 2, 8, 4));

        jLabel8.setText("AX:");
        pnlRegistros.add(jLabel8);

        lblAxValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblAxValor.setText("0");
        pnlRegistros.add(lblAxValor);

        jLabel9.setText("BX:");
        pnlRegistros.add(jLabel9);

        lblBxValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblBxValor.setText("0");
        pnlRegistros.add(lblBxValor);

        jLabel10.setText("CX:");
        pnlRegistros.add(jLabel10);

        lblCxValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCxValor.setText("0");
        pnlRegistros.add(lblCxValor);

        jLabel11.setText("DX:");
        pnlRegistros.add(jLabel11);

        lblDxValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDxValor.setText("0");
        pnlRegistros.add(lblDxValor);

        pnlBcpCampos.add(pnlRegistros);

        pnlMemoriaProceso.setBorder(javax.swing.BorderFactory.createTitledBorder("Memoria del proceso"));
        pnlMemoriaProceso.setLayout(new java.awt.GridLayout(0, 2, 8, 4));

        jLabel12.setText("Direccion base:");
        pnlMemoriaProceso.add(jLabel12);

        lblBaseValor.setText("-");
        pnlMemoriaProceso.add(lblBaseValor);

        jLabel13.setText("Limite:");
        pnlMemoriaProceso.add(jLabel13);

        lblLimiteValor.setText("-");
        pnlMemoriaProceso.add(lblLimiteValor);

        pnlBcpCampos.add(pnlMemoriaProceso);

        pnlContabilidad.setBorder(javax.swing.BorderFactory.createTitledBorder("Contabilidad"));
        pnlContabilidad.setLayout(new java.awt.GridLayout(0, 2, 8, 4));

        jLabel14.setText("Instrucciones ejec.:");
        pnlContabilidad.add(jLabel14);

        lblEjecutadasValor.setText("0");
        pnlContabilidad.add(lblEjecutadasValor);

        pnlBcpCampos.add(pnlContabilidad);

        pnlBcpInterior.add(pnlBcpCampos, java.awt.BorderLayout.NORTH);

        scrBcp.setViewportView(pnlBcpInterior);

        pnlBCP.add(scrBcp, java.awt.BorderLayout.CENTER);

        spSecundario.setRightComponent(pnlBCP);

        spPrincipal.setRightComponent(spSecundario);

        getContentPane().add(spPrincipal, java.awt.BorderLayout.CENTER);

        pnlConsola.setBorder(javax.swing.BorderFactory.createTitledBorder("Consola"));
        pnlConsola.setLayout(new java.awt.BorderLayout());

        txtConsola.setEditable(false);
        txtConsola.setColumns(20);
        txtConsola.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtConsola.setLineWrap(true);
        txtConsola.setRows(6);
        txtConsola.setWrapStyleWord(true);
        scrConsola.setViewportView(txtConsola);

        pnlConsola.add(scrConsola, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlConsola, java.awt.BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
        controlador.alCargarArchivo();
    }//GEN-LAST:event_btnCargarActionPerformed

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        controlador.alEjecutar();
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void btnPasoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasoActionPerformed
        controlador.alPasoAPaso();
    }//GEN-LAST:event_btnPasoActionPerformed

    private void btnReiniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReiniciarActionPerformed
        controlador.alReiniciar();
    }//GEN-LAST:event_btnReiniciarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        controlador.alLimpiar();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigActionPerformed
        new DialogoConfiguracion(this, true, controlador).setVisible(true);
    }//GEN-LAST:event_btnConfigActionPerformed

    private void btnEstadisticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstadisticasActionPerformed
        new DialogoEstadisticas(this, true, controlador).setVisible(true);
    }//GEN-LAST:event_btnEstadisticasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCargar;
    private javax.swing.JButton btnConfig;
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnEstadisticas;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnPaso;
    private javax.swing.JButton btnReiniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblAcValor;
    private javax.swing.JLabel lblArchivo;
    private javax.swing.JLabel lblArchivoValor;
    private javax.swing.JLabel lblAxValor;
    private javax.swing.JLabel lblBaseValor;
    private javax.swing.JLabel lblBxValor;
    private javax.swing.JLabel lblCxValor;
    private javax.swing.JLabel lblDxValor;
    private javax.swing.JLabel lblEjecutadasValor;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblEstadoBcpValor;
    private javax.swing.JLabel lblEstadoValor;
    private javax.swing.JLabel lblIrBinValor;
    private javax.swing.JLabel lblIrTextoValor;
    private javax.swing.JLabel lblLimiteValor;
    private javax.swing.JLabel lblPcValor;
    private javax.swing.JLabel lblPidValor;
    private javax.swing.JLabel lblProgramaValor;
    private javax.swing.JLabel lblSepA;
    private javax.swing.JLabel lblSepB;
    private javax.swing.JLabel lblUsoMemoria;
    private javax.swing.JProgressBar pbUsoMemoria;
    private javax.swing.JPanel pnlBCP;
    private javax.swing.JPanel pnlBcpCampos;
    private javax.swing.JPanel pnlBcpInterior;
    private javax.swing.JPanel pnlConsola;
    private javax.swing.JPanel pnlContabilidad;
    private javax.swing.JPanel pnlContexto;
    private javax.swing.JPanel pnlContextoCpu;
    private javax.swing.JPanel pnlInstrucciones;
    private javax.swing.JPanel pnlMemoria;
    private javax.swing.JPanel pnlMemoriaProceso;
    private javax.swing.JPanel pnlProceso;
    private javax.swing.JPanel pnlRegistros;
    private javax.swing.JPanel pnlSuperior;
    private javax.swing.JScrollPane scrBcp;
    private javax.swing.JScrollPane scrConsola;
    private javax.swing.JScrollPane scrInstrucciones;
    private javax.swing.JScrollPane scrMemoria;
    private javax.swing.JToolBar.Separator sepConfiguracion;
    private javax.swing.JToolBar.Separator sepEjecucion;
    private javax.swing.JSplitPane spPrincipal;
    private javax.swing.JSplitPane spSecundario;
    private javax.swing.JTable tblInstrucciones;
    private javax.swing.JTable tblMemoria;
    private javax.swing.JToolBar tbBarra;
    private javax.swing.JTextArea txtConsola;
    // End of variables declaration//GEN-END:variables
}
