package ni.edu.uam.practicahilos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProcesadorController {

    // ---------- Clientes ----------
    @FXML
    private Label lblEstado;

    @FXML
    private Label lblPorcentaje;

    @FXML
    private ProgressBar barraProgreso;

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnDetener;

    // ---------- Productos ----------
    @FXML
    private Label lblEstadoProducto;

    @FXML
    private Label lblPorcentajeProducto;

    @FXML
    private ProgressBar barraProgresoProducto;

    @FXML
    private Button btnIniciarProducto;

    @FXML
    private Button btnDetenerProducto;

    // ---------- General ----------
    @FXML
    private Button btnReiniciar;

    private Thread hiloProcesoCliente;
    private Thread hiloProcesoProducto;

    // Cantidad de pasos de cada proceso
    private static final int TOTAL_CLIENTES = 100;
    private static final int TOTAL_PRODUCTOS = 50;

    // Último paso completado de cada proceso.
    // "volatile" hace que el hilo principal siempre vea
    // el valor más reciente que escribió el otro hilo.
    private volatile int pasoCliente = 0;
    private volatile int pasoProducto = 0;

    // =====================================================
    //                      CLIENTES
    // =====================================================

    @FXML
    private void iniciarProcesoCliente() {

        if (estaActivo(hiloProcesoCliente)) {
            return;
        }

        // Si ya había terminado, empieza otra vez desde cero
        if (pasoCliente >= TOTAL_CLIENTES) {
            pasoCliente = 0;
            barraProgreso.setProgress(0);
            lblPorcentaje.setText("0%");
        }

        lblEstado.setText(pasoCliente == 0
                ? "Procesando clientes..."
                : "Continuando clientes...");

        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        btnReiniciar.setDisable(true);

        // Se continúa desde el paso siguiente al último completado
        final int inicio = pasoCliente + 1;

        Runnable tarea = () -> {

            try {
                for (int i = inicio; i <= TOTAL_CLIENTES; i++) {

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(100);

                    pasoCliente = i; // se guarda el avance

                    final int porcentaje = i * 100 / TOTAL_CLIENTES;

                    Platform.runLater(() -> {
                        barraProgreso.setProgress(porcentaje / 100.0);
                        lblPorcentaje.setText(porcentaje + "%");
                    });
                }

                Platform.runLater(() -> {
                    lblEstado.setText("Clientes: proceso finalizado");
                    btnIniciar.setText("Iniciar");
                    habilitarBotonesCliente();
                });

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                Platform.runLater(() -> {
                    lblEstado.setText("Clientes: proceso detenido");
                    btnIniciar.setText("Continuar");
                    habilitarBotonesCliente();
                });
            }
        };

        hiloProcesoCliente = new Thread(tarea);
        hiloProcesoCliente.setName("Hilo-Clientes");
        hiloProcesoCliente.setDaemon(true);
        hiloProcesoCliente.start();
    }

    @FXML
    private void detenerProcesoCliente() {

        if (estaActivo(hiloProcesoCliente)) {
            btnDetener.setDisable(true);
            lblEstado.setText("Solicitando detención...");
            hiloProcesoCliente.interrupt();
        }
    }

    private void habilitarBotonesCliente() {
        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        actualizarBotonReiniciar();
    }

    // =====================================================
    //                      PRODUCTOS
    // =====================================================

    @FXML
    private void iniciarProcesoProductos() {

        if (estaActivo(hiloProcesoProducto)) {
            return;
        }

        // Si ya había terminado, empieza otra vez desde cero
        if (pasoProducto >= TOTAL_PRODUCTOS) {
            pasoProducto = 0;
            barraProgresoProducto.setProgress(0);
            lblPorcentajeProducto.setText("0%");
        }

        lblEstadoProducto.setText(pasoProducto == 0
                ? "Procesando productos..."
                : "Continuando productos...");

        btnIniciarProducto.setDisable(true);
        btnDetenerProducto.setDisable(false);
        btnReiniciar.setDisable(true);

        // Se continúa desde el paso siguiente al último completado
        final int inicio = pasoProducto + 1;

        Runnable tarea = () -> {

            try {
                for (int i = inicio; i <= TOTAL_PRODUCTOS; i++) {

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(200);

                    pasoProducto = i; // se guarda el avance

                    final int porcentaje = i * 100 / TOTAL_PRODUCTOS;

                    Platform.runLater(() -> {
                        barraProgresoProducto.setProgress(porcentaje / 100.0);
                        lblPorcentajeProducto.setText(porcentaje + "%");
                    });
                }

                Platform.runLater(() -> {
                    lblEstadoProducto.setText("Productos: proceso finalizado");
                    btnIniciarProducto.setText("Iniciar");
                    habilitarBotonesProducto();
                });

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                Platform.runLater(() -> {
                    lblEstadoProducto.setText("Productos: proceso detenido");
                    btnIniciarProducto.setText("Continuar");
                    habilitarBotonesProducto();
                });
            }
        };

        hiloProcesoProducto = new Thread(tarea);
        hiloProcesoProducto.setName("Hilo-Productos");
        hiloProcesoProducto.setDaemon(true);
        hiloProcesoProducto.start();
    }

    @FXML
    private void detenerProcesoProductos() {

        if (estaActivo(hiloProcesoProducto)) {
            btnDetenerProducto.setDisable(true);
            lblEstadoProducto.setText("Solicitando detención...");
            hiloProcesoProducto.interrupt();
        }
    }

    private void habilitarBotonesProducto() {
        btnIniciarProducto.setDisable(false);
        btnDetenerProducto.setDisable(true);
        actualizarBotonReiniciar();
    }

    // =====================================================
    //                      GENERAL
    // =====================================================

    @FXML
    private void reiniciarProceso() {

        // No se reinicia si algún proceso sigue trabajando
        if (estaActivo(hiloProcesoCliente) || estaActivo(hiloProcesoProducto)) {
            return;
        }

        // Se borra el avance guardado: la próxima vez empieza desde cero
        pasoCliente = 0;
        pasoProducto = 0;

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");
        btnIniciar.setText("Iniciar");

        barraProgresoProducto.setProgress(0);
        lblPorcentajeProducto.setText("0%");
        lblEstadoProducto.setText("Esperando inicio");
        btnIniciarProducto.setText("Iniciar");

        habilitarBotonesCliente();
        habilitarBotonesProducto();
    }

    // "Reiniciar" solo se activa cuando ningún proceso está trabajando
    private void actualizarBotonReiniciar() {
        boolean algunoActivo =
                estaActivo(hiloProcesoCliente) || estaActivo(hiloProcesoProducto);
        btnReiniciar.setDisable(algunoActivo);
    }

    private boolean estaActivo(Thread hilo) {
        return hilo != null && hilo.isAlive();
    }

    public void detenerAlCerrar() {

        if (estaActivo(hiloProcesoCliente)) {
            hiloProcesoCliente.interrupt();
        }

        if (estaActivo(hiloProcesoProducto)) {
            hiloProcesoProducto.interrupt();
        }
    }
}