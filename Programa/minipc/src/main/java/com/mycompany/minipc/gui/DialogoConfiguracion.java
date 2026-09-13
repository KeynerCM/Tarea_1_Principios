package com.mycompany.minipc.gui;

import com.mycompany.minipc.core.Memoria;

import javax.swing.SpinnerNumberModel;

/**
 * Nombre: DialogoConfiguracion
 * Entradas: la ventana padre y el controlador al que aplicar los cambios
 * Salidas: la nueva configuracion, entregada al controlador al aceptar
 * Restricciones: aplicar la configuracion descarga el programa cargado,
 *                porque redimensionar la memoria invalida las direcciones ya
 *                asignadas
 * Descripcion: dialogo de configuracion de la maquina. Permite cambiar el
 *              tamano total de la memoria, el limite entre la zona de kernel
 *              y la de usuario, y la velocidad de la ejecucion automatica.
 *              Muestra en vivo como queda repartida la memoria segun lo que se
 *              elija, y deshabilita el boton Aceptar mientras la combinacion
 *              no sea valida, en lugar de dejar equivocarse y reclamar despues.
 */
public class DialogoConfiguracion extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private final ControladorPrincipal controlador;

    /**
     * Nombre: DialogoConfiguracion
     * Entradas: padre, ventana sobre la que se muestra; modal, true para
     *           bloquear la ventana de atras; controlador, al que se le
     *           aplicara la configuracion
     * Salidas: el dialogo construido, con los valores actuales ya cargados
     * Restricciones: el controlador no debe ser nulo, porque de el se leen los
     *                valores de partida
     * Descripcion: arma los componentes y les asigna los modelos de los
     *              spinners. Esos modelos se crean aqui y no en el disenador
     *              para que los limites queden junto a las constantes de
     *              Memoria que los definen, en lugar de duplicados en el XML.
     */
    public DialogoConfiguracion(java.awt.Frame padre, boolean modal,
            ControladorPrincipal controlador) {
        super(padre, modal);
        this.controlador = controlador;
        initComponents();

        // Los modelos se arman aqui y no en el disenador para que los
        // limites queden junto a las constantes que los definen.
        Memoria memoria = controlador.getProcesador().getMemoria();
        spnTamano.setModel(new SpinnerNumberModel(
                memoria.getTamano(), Memoria.TAMANO_MINIMO, 1024, 32));
        spnKernel.setModel(new SpinnerNumberModel(
                memoria.getLimiteKernel(), Memoria.LIMITE_KERNEL_MINIMO, 512, 8));
        spnVelocidad.setModel(new SpinnerNumberModel(
                controlador.getVelocidadMs(), 50, 2000, 50));

        actualizarResumen();
        getRootPane().setDefaultButton(btnAceptar);
        pack();
        setLocationRelativeTo(padre);
    }

    /**
     * Nombre: tamanoElegido
     * Entradas: ninguna
     * Salidas: el tamano de memoria seleccionado
     * Restricciones: el spinner garantiza que el valor es un entero dentro de
     *                su rango
     * Descripcion: concentra la conversion del valor del spinner, que llega
     *              como Object, para no repetir el casteo en cada uso.
     */
    private int tamanoElegido() {
        return (Integer) spnTamano.getValue();
    }

    /**
     * Nombre: kernelElegido
     * Entradas: ninguna
     * Salidas: el limite de kernel seleccionado
     * Restricciones: el spinner garantiza que el valor es un entero dentro de
     *                su rango
     * Descripcion: analogo a tamanoElegido, para el limite entre zonas.
     */
    private int kernelElegido() {
        return (Integer) spnKernel.getValue();
    }

    /**
     * Nombre: velocidadElegida
     * Entradas: ninguna
     * Salidas: los milisegundos entre instrucciones seleccionados
     * Restricciones: el spinner garantiza que el valor es un entero dentro de
     *                su rango
     * Descripcion: analogo a tamanoElegido, para la velocidad de ejecucion.
     */
    private int velocidadElegida() {
        return (Integer) spnVelocidad.getValue();
    }

    /**
     * Nombre: actualizarResumen
     * Entradas: ninguna; lee los valores actuales de los spinners
     * Salidas: ninguna; actualiza las etiquetas y el estado del boton Aceptar
     * Restricciones: ninguna
     * Descripcion: recalcula como queda repartida la memoria y avisa si la
     *              combinacion elegida no sirve. Cuando el limite de kernel
     *              alcanza o supera el tamano total, borra el resumen, muestra
     *              el aviso y deshabilita Aceptar, de modo que el error se
     *              previene en vez de reclamarse despues.
     */
    private void actualizarResumen() {
        int tamano = tamanoElegido();
        int kernel = kernelElegido();

        if (kernel >= tamano) {
            lblZonaKernelValor.setText("-");
            lblZonaUsuarioValor.setText("-");
            lblEspacioValor.setText("-");
            lblAviso.setText("El limite de kernel debe ser menor que el tamano total.");
            btnAceptar.setEnabled(false);
            return;
        }

        lblZonaKernelValor.setText("0 a " + (kernel - 1));
        lblZonaUsuarioValor.setText(kernel + " a " + (tamano - 1));
        lblEspacioValor.setText((tamano - kernel) + " posiciones");
        lblAviso.setText(" ");
        btnAceptar.setEnabled(true);
    }

    /**
     * Nombre: initComponents
     * Entradas: ninguna
     * Salidas: ninguna
     * Restricciones: NO editar a mano. El disenador visual de NetBeans
     *                regenera este metodo completo a partir del archivo .form
     *                cada vez que se modifica el dialogo
     * Descripcion: crea los componentes del dialogo, les fija sus propiedades,
     *              los ubica en sus contenedores y conecta los eventos de los
     *              spinners y de los tres botones.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlParametros = new javax.swing.JPanel();
        lblTamano = new javax.swing.JLabel();
        spnTamano = new javax.swing.JSpinner();
        lblKernel = new javax.swing.JLabel();
        spnKernel = new javax.swing.JSpinner();
        lblVelocidad = new javax.swing.JLabel();
        spnVelocidad = new javax.swing.JSpinner();
        pnlResumen = new javax.swing.JPanel();
        lblZonaKernel = new javax.swing.JLabel();
        lblZonaKernelValor = new javax.swing.JLabel();
        lblZonaUsuario = new javax.swing.JLabel();
        lblZonaUsuarioValor = new javax.swing.JLabel();
        lblEspacio = new javax.swing.JLabel();
        lblEspacioValor = new javax.swing.JLabel();
        pnlInferior = new javax.swing.JPanel();
        lblAviso = new javax.swing.JLabel();
        pnlBotones = new javax.swing.JPanel();
        btnRestaurar = new javax.swing.JButton();
        btnAceptar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configuracion del Mini PC");
        setResizable(false);
        getContentPane().setLayout(new java.awt.BorderLayout());

        pnlParametros.setBorder(javax.swing.BorderFactory.createTitledBorder("Parametros"));
        pnlParametros.setLayout(new java.awt.GridLayout(0, 2, 10, 8));

        lblTamano.setText("Tamano de memoria:");
        pnlParametros.add(lblTamano);

        spnTamano.setToolTipText("Cantidad total de posiciones. El enunciado exige un minimo de 128");
        spnTamano.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnTamanoStateChanged(evt);
            }
        });
        pnlParametros.add(spnTamano);

        lblKernel.setText("Limite de kernel:");
        pnlParametros.add(lblKernel);

        spnKernel.setToolTipText("Primera direccion de la zona de usuario");
        spnKernel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnKernelStateChanged(evt);
            }
        });
        pnlParametros.add(spnKernel);

        lblVelocidad.setText("Velocidad (ms):");
        pnlParametros.add(lblVelocidad);

        spnVelocidad.setToolTipText("Milisegundos entre instrucciones en la ejecucion automatica");
        pnlParametros.add(spnVelocidad);

        getContentPane().add(pnlParametros, java.awt.BorderLayout.NORTH);

        pnlResumen.setBorder(javax.swing.BorderFactory.createTitledBorder("Distribucion resultante"));
        pnlResumen.setLayout(new java.awt.GridLayout(0, 2, 10, 4));

        lblZonaKernel.setText("Zona de kernel:");
        pnlResumen.add(lblZonaKernel);

        lblZonaKernelValor.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        lblZonaKernelValor.setText("-");
        pnlResumen.add(lblZonaKernelValor);

        lblZonaUsuario.setText("Zona de usuario:");
        pnlResumen.add(lblZonaUsuario);

        lblZonaUsuarioValor.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        lblZonaUsuarioValor.setText("-");
        pnlResumen.add(lblZonaUsuarioValor);

        lblEspacio.setText("Espacio para programas:");
        pnlResumen.add(lblEspacio);

        lblEspacioValor.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblEspacioValor.setText("-");
        pnlResumen.add(lblEspacioValor);

        getContentPane().add(pnlResumen, java.awt.BorderLayout.CENTER);

        pnlInferior.setLayout(new java.awt.BorderLayout());

        lblAviso.setForeground(new java.awt.Color(192, 51, 51));
        lblAviso.setText(" ");
        pnlInferior.add(lblAviso, java.awt.BorderLayout.NORTH);

        pnlBotones.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        btnRestaurar.setText("Valores por defecto");
        btnRestaurar.setToolTipText("Memoria de 256 posiciones, kernel de 0 a 63, velocidad de 500 ms");
        btnRestaurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRestaurarActionPerformed(evt);
            }
        });
        pnlBotones.add(btnRestaurar);

        btnAceptar.setText("Aceptar");
        btnAceptar.setToolTipText("Aplica la configuracion y descarga el programa actual");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });
        pnlBotones.add(btnAceptar);

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });
        pnlBotones.add(btnCancelar);

        pnlInferior.add(pnlBotones, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlInferior, java.awt.BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Nombre: spnTamanoStateChanged
     * Entradas: evt, evento de cambio del spinner
     * Salidas: ninguna
     * Restricciones: ninguna
     * Descripcion: recalcula el resumen cada vez que cambia el tamano de
     *              memoria, para que la vista previa siga al usuario.
     */
    private void spnTamanoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnTamanoStateChanged
        actualizarResumen();
    }//GEN-LAST:event_spnTamanoStateChanged

    /**
     * Nombre: spnKernelStateChanged
     * Entradas: evt, evento de cambio del spinner
     * Salidas: ninguna
     * Restricciones: ninguna
     * Descripcion: recalcula el resumen cada vez que cambia el limite de
     *              kernel.
     */
    private void spnKernelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnKernelStateChanged
        actualizarResumen();
    }//GEN-LAST:event_spnKernelStateChanged

    /**
     * Nombre: btnRestaurarActionPerformed
     * Entradas: evt, evento de accion que genero el clic
     * Salidas: ninguna
     * Restricciones: no aplica nada todavia; solo cambia lo que muestran los
     *                spinners
     * Descripcion: devuelve los tres valores a los de arranque, tomandolos de
     *              las constantes de Memoria y del controlador en lugar de
     *              escribirlos aqui.
     */
    private void btnRestaurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRestaurarActionPerformed
        spnTamano.setValue(Memoria.TAMANO_POR_DEFECTO);
        spnKernel.setValue(Memoria.LIMITE_KERNEL_POR_DEFECTO);
        spnVelocidad.setValue(ControladorPrincipal.VELOCIDAD_POR_DEFECTO);
        actualizarResumen();
    }//GEN-LAST:event_btnRestaurarActionPerformed

    /**
     * Nombre: btnAceptarActionPerformed
     * Entradas: evt, evento de accion que genero el clic
     * Salidas: ninguna
     * Restricciones: el boton solo esta habilitado si la combinacion es
     *                valida, de modo que aqui no hace falta volver a validar
     * Descripcion: aplica la configuracion a traves del controlador y cierra
     *              el dialogo.
     */
    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        controlador.alConfigurar(tamanoElegido(), kernelElegido(), velocidadElegida());
        dispose();
    }//GEN-LAST:event_btnAceptarActionPerformed

    /**
     * Nombre: btnCancelarActionPerformed
     * Entradas: evt, evento de accion que genero el clic
     * Salidas: ninguna
     * Restricciones: ninguna
     * Descripcion: cierra el dialogo sin aplicar ningun cambio.
     */
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnRestaurar;
    private javax.swing.JLabel lblAviso;
    private javax.swing.JLabel lblEspacio;
    private javax.swing.JLabel lblEspacioValor;
    private javax.swing.JLabel lblKernel;
    private javax.swing.JLabel lblTamano;
    private javax.swing.JLabel lblVelocidad;
    private javax.swing.JLabel lblZonaKernel;
    private javax.swing.JLabel lblZonaKernelValor;
    private javax.swing.JLabel lblZonaUsuario;
    private javax.swing.JLabel lblZonaUsuarioValor;
    private javax.swing.JPanel pnlBotones;
    private javax.swing.JPanel pnlInferior;
    private javax.swing.JPanel pnlParametros;
    private javax.swing.JPanel pnlResumen;
    private javax.swing.JSpinner spnKernel;
    private javax.swing.JSpinner spnTamano;
    private javax.swing.JSpinner spnVelocidad;
    // End of variables declaration//GEN-END:variables
}
