package controlador;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import modelo.CuotaPago;
import modelo.CuotasPagoInversion;
import modelo.Transaccion;

public class PagosControlador {
	
	//Objectos de clases modelo y variables extra	
	private ControladorGeneral controlGeneral;
	private Transaccion objTransaccion;
	private CuotaPago objCuotaPago;
	private CuotasPagoInversion cuotaInversionPago;
	
	private ObservableList<Transaccion> listaTransPagos;
	private ObservableList<CuotaPago> listaCuotasPago;
	

    @FXML
    private BorderPane bpnPagosMain;
    @FXML
    private ImageView inicio_logo;
    @FXML
    private ImageView reg_logo;
    @FXML
    private StackPane stpCenter;
    @FXML
    private AnchorPane anpPagosInicio;
    @FXML
    private TextField txfClientePagos;
    @FXML
    private AnchorPane anpTransPagos;  
    
    @FXML
    private TableView<Transaccion> tblTransPagos;
    @FXML
    private TableColumn<Transaccion, Integer> tblColTPCod;
    @FXML
    private TableColumn<Transaccion, Character> tblColTPTipo;
    @FXML
    private TableColumn<Transaccion, Double> tblColTPMonto;
    @FXML
    private TableColumn<Transaccion, LocalDate> tblColTPFechaIni;
    @FXML
    private TableColumn<Transaccion, String> tblColTPEstado; 
    

    @FXML
    private AnchorPane anpListaCuotas;
    @FXML
    private TableView<CuotaPago> tblCuotasPago;
    @FXML
    private TableColumn<CuotaPago, Integer> tblColCPCod;
    @FXML
    private TableColumn<CuotaPago, Double> tblColCPMens;
    @FXML
    private TableColumn<CuotaPago, LocalDate> tblColCPFechaPago;
    @FXML
    private TableColumn<CuotaPago, String> tblColCPEstado;
    

    @FXML
    private TextField txfCodComprobante;
    @FXML
    private ComboBox<String> cbxModalidadPago;
    @FXML
    private Label lblCuentaAsociada;
    @FXML
    private TextField txfCuentaAsociada;
    @FXML
    private Button btnRegistrarCuenta;
    @FXML
    private AnchorPane anpPagoCuota;
    



    @FXML
    public void realizarPagos(ActionEvent event) {   
    	txfClientePagos.clear();
    	esconderPanesMenosIndicado(anpPagosInicio);
    }

    @FXML
    public void continuarTransaccionesPagos(ActionEvent event) throws SQLException {    	
    	
    	ResultSet clienteConsulta = controlGeneral.ejecutarSentencia("SELECT * from cliente WHERE cedula like '" + txfClientePagos.getText() +"';");
    	if(clienteConsulta.next()) {
    		String cedulaConsulta = clienteConsulta.getString("cedula");    		
    		try {
    			
    			listaTransPagos=buscarTransacciones(cedulaConsulta);
    		
	    		if(!listaTransPagos.isEmpty()) {
		    		tblColTPCod.setCellValueFactory(new PropertyValueFactory<>("codTrans"));
		        	tblColTPTipo.setCellValueFactory(new PropertyValueFactory<>("tipoTrans"));
		        	tblColTPMonto.setCellValueFactory(new PropertyValueFactory<>("montoTrans"));
		        	tblColTPFechaIni.setCellValueFactory(new PropertyValueFactory<>("fechaIniciacion"));
		        	tblColTPEstado.setCellValueFactory(new PropertyValueFactory<>("estadoSolicitud"));  
		        	
		        	tblTransPagos.setItems(listaTransPagos);
	    		}else {
	    			tblTransPagos.setItems(null);
	    		}
    		}catch(Exception e) {
    			System.out.println(e.toString());
    		}
        	
        	esconderPanesMenosIndicado(anpTransPagos);
    	}
    	else {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Cédula incorrecta", "Cliente no encontrado", "No se encontraron registros del cliente en el sistema.");
    	}
    }
    
    public ObservableList<Transaccion> buscarTransacciones(String clienteTrans) throws SQLException{    	
        
    	ObservableList<Transaccion> listaTrans = FXCollections.observableArrayList();    
    	String buscarTrans = "SELECT transacciones.* from transacciones where clientetrans like '"+clienteTrans+"' and estadosolicitud like 'EN CURSO';";
    	ResultSet trans = controlGeneral.ejecutarSentencia(buscarTrans);
    	
    	while(trans.next()) {
    		Transaccion objTrans = new Transaccion();
    		objTrans.setCodTrans(trans.getInt("codTrans"));
    		objTrans.setClienteTrans(trans.getString("clienteTrans"));
    		objTrans.setTipoTrans(trans.getString("tipoTrans").charAt(0));
    		objTrans.setMontoTrans(trans.getDouble("montoTrans"));
    		objTrans.setTasaTrans(trans.getDouble("tasaTrans"));
    		objTrans.setNumCuotas(trans.getInt("numCuotas"));
    		objTrans.setFechaSolicitud(trans.getDate("fechaSolicitud").toLocalDate());
    		objTrans.setFechaAprobacion(null);
    		objTrans.setFechaIniciacion(trans.getDate("fechaIniciacion").toLocalDate());
    		objTrans.setFechaTermino(trans.getDate("fechaTermino").toLocalDate());
    		objTrans.setEstadoSolicitud(trans.getString("estadoSolicitud"));    	
    		
    		listaTrans.add(objTrans);
    	}
    	return listaTrans;
    }
    
    @FXML
    public void continuarCuotasPago(ActionEvent event) throws SQLException {
    	
    	Transaccion transSelected = tblTransPagos.getSelectionModel().getSelectedItem();
    	if(transSelected==null) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Transacción no seleccionada", "Transacción no seleccionada",
    				"Por favor haga click sobre alguna transacción y presione Ver Detalles.");
    		return;
    	}
    	
    	//Se asigna la transacción a pagar como variable global para despúes hacer uso de ella.
    	this.objTransaccion=transSelected;
    	
    	try {    			
    		this.listaCuotasPago = buscarCuotasPagoTrans(transSelected.getCodTrans());
//    		this.listaCuotasPago.forEach(cuota-> System.out.println(cuota.toString()));
		
    		if(!this.listaCuotasPago.isEmpty()) {
    			  
    			tblColCPCod.setCellValueFactory(new PropertyValueFactory<>("codCuota"));
    			tblColCPMens.setCellValueFactory(new PropertyValueFactory<>("mensCuota"));
    			tblColCPFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPagoCuota"));
    			tblColCPEstado.setCellValueFactory(new PropertyValueFactory<>("estadoCuota"));
	        	
    			tblCuotasPago.setItems(this.listaCuotasPago);
    		}else {
    			tblCuotasPago.setItems(null);    			
    		}
    		
		}catch(Exception e) {
			System.out.println(e.toString());
		}
    	
    	esconderPanesMenosIndicado(anpListaCuotas);
    }
    
    public ObservableList<CuotaPago> buscarCuotasPagoTrans(int codTrans) throws SQLException {
    	
    	ObservableList<CuotaPago> listaCuotasPago = FXCollections.observableArrayList();
    	
    	String buscarCuotasPagoSQL = "select cuotaspago.* from cuotaspago join transacciones on transcuota=codtrans where codtrans="+codTrans+"; ";
    	ResultSet cuotasPago = controlGeneral.ejecutarSentencia(buscarCuotasPagoSQL);
    	
    	while(cuotasPago.next()) {
    		CuotaPago cuota = new CuotaPago();
    		cuota.setCodCuota(cuotasPago.getInt("codcuota"));
    		cuota.setCodComprobante(cuotasPago.getString("codcomprobante"));
    		cuota.setTransCuota(cuotasPago.getInt("transcuota"));
    		cuota.setMensCuota(cuotasPago.getDouble("menscuota"));
    		cuota.setFechaPagoCuota(cuotasPago.getDate("fechapagocuota").toLocalDate());
    		
    		//Si la cuota no está pagada, su fecha efectiva sería null, por lo que no se puede convertir a LocalDate.
    		try {
    			cuota.setFechaEfectivaCuota(cuotasPago.getDate("fechaefectivacuota").toLocalDate());
    		}catch(Exception e) {
    			cuota.setFechaEfectivaCuota(null);
    		}
    		
    		
    		
    		cuota.setModalPagoCuota(cuotasPago.getString("modalpagocuota"));
    		cuota.setEstadoCuota(cuotasPago.getString("estadocuota"));
    		
    		listaCuotasPago.add(cuota);
    	}
    	return listaCuotasPago;
    }
    
    @FXML
    public void verDetallesCuotaPago(ActionEvent event) throws SQLException {
    	CuotaPago cuotaSelected = tblCuotasPago.getSelectionModel().getSelectedItem();
    	if(cuotaSelected==null) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Cuota de pago no seleccionada", "Cuota de pago no seleccionada",
    				"Por favor haga click sobre alguna cuota de pago y presione el botón Detalles.");
    		return;
    	}
    	
    	AnchorPane verDetalles = new AnchorPane();
    	TextArea txaVerDetalles = new TextArea();
    	txaVerDetalles.setEditable(false);
    	verDetalles.getChildren().add(txaVerDetalles);
    	
    	StringBuffer datosCuota = new StringBuffer();
    	
    	this.listaCuotasPago.forEach(cuota->{
    		String codcomprobante ="";
    		String fechaefectiva="";
    		String modalidad= "";
    		
    		if(cuota.getCodComprobante()==null) {
    			codcomprobante="No aplica";
    		}else {
    			codcomprobante = cuota.getCodComprobante();
    		}
    		
    		if(cuota.getFechaEfectivaCuota()==null) {
    			fechaefectiva = "No aplica";
    		}else {
    			fechaefectiva = cuota.getFechaEfectivaCuota().toString();
    		}
    		
    		if(cuota.getModalPagoCuota()==null) {
    			modalidad = "No aplica";
    		}else{
    			modalidad = cuota.getModalPagoCuota();
    		}
    			
    		
    		if(cuotaSelected.equals(cuota)) {
    			datosCuota.append("\nDATOS DE LA CUOTA:" +
    					"\nCódigo: " + cuota.getCodCuota() +    					
    					"\nCódigo comprobante: " + codcomprobante + 
    					"\nCódigo transacción: " + cuota.getTransCuota() + 
    					"\nMensualidad: " + cuota.getMensCuota() + 
    					"\nFecha de pago: " + cuota.getFechaPagoCuota() + 
    					"\nFecha efectiva de pago: " + fechaefectiva +
    					"\nModalidad de pago: " + modalidad + 
    					"\nEstado de la cuota: " + cuota.getEstadoCuota()    					
    					);
    		}
    	});
    	
    	//Si es una cuota de inversión, tiene que tener registros en dicha tabla
    	ResultSet cuotaInversion = controlGeneral.ejecutarSentencia("select * from cuotaspagoinversion where codcuotainversion="+cuotaSelected.getCodCuota()+";");
    	while(cuotaInversion.next()) {
    		datosCuota.append("\n\nINFORMACIÓN DE PAGO:"
    				+ "\nCuenta asociada: " + cuotaInversion.getString("cuentapagoactual"));
    	}
    	
    	txaVerDetalles.setText(datosCuota.toString());
    	txaVerDetalles.setPrefSize(500, 500);
    	txaVerDetalles.setStyle("-fx-font-size:16");
    	
    	Scene verDetallesScene = new Scene(verDetalles);
		Stage verDetallesStage = new Stage();
		verDetallesStage.setScene(verDetallesScene);
		verDetallesStage.setTitle("Detalles de transacción");
		verDetallesStage.show();
    }
    
    @FXML
    public void continuarPagoCuota(ActionEvent event) throws SQLException {
    	CuotaPago cuotaSelected = tblCuotasPago.getSelectionModel().getSelectedItem();
    	if(cuotaSelected==null) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Cuota de pago no seleccionada", "Cuota de pago no seleccionada",
    				"Por favor haga click sobre alguna cuota de pago y presione el botón Detalles.");
    		return;
    	}
    	
    	if(cuotaSelected.getEstadoCuota().equals("PAGADA")) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Cuota pagada", "Cuota pagada", "No se puede ejecutar el pago de una cuota pagada");
    		return;
    	}
    	
    	this.objCuotaPago = cuotaSelected;
    	
    	limpiarCampos();    	
    	    	
    	if(this.objTransaccion.getTipoTrans()=='I') {
    		this.lblCuentaAsociada.setVisible(true);
    		this.txfCuentaAsociada.setVisible(true);
    		this.btnRegistrarCuenta.setVisible(true);			
    		
			ResultSet cuentaPago = controlGeneral.ejecutarSentencia("select cuotaspagoinversion.* from cuotaspagoinversion join"
					+ " cuotaspago on codcuotainversion=codcuota where codcuota="+cuotaSelected.getCodCuota()+";");
			
			while(cuentaPago.next()) {
				this.cuotaInversionPago.setCuentaPagoActual(cuentaPago.getString("cuentapagoactual"));
				this.cuotaInversionPago.setCodCuotaInversion(cuentaPago.getInt("codCuotaInversion"));
				this.txfCuentaAsociada.setText(this.cuotaInversionPago.getCuentaPagoActual());
			}
			
    	}else {   		
    		this.lblCuentaAsociada.setVisible(false);
    		this.txfCuentaAsociada.setVisible(false);
    		this.btnRegistrarCuenta.setVisible(false);
    	}    	
    	esconderPanesMenosIndicado(anpPagoCuota);
    }
    
    @FXML
    public void guardarPago(ActionEvent event) throws SQLException {    	
    	
    	String modalidadPago = this.cbxModalidadPago.getSelectionModel().getSelectedItem();
    	String codComprobante = this.txfCodComprobante.getText();    	
    	if(modalidadPago==null || codComprobante==null || (this.objTransaccion.getTipoTrans()== 'I' && txfCuentaAsociada.getText().isEmpty())) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Campos vacíos", "Campos vacíos", "Por favor rellene todos los campos");
    		return;
    	}   	
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Realizar pago");
    	alert.setHeaderText("Realizando pago...");
    	alert.setContentText("¿Está seguro de continuar con el pago?");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		
    		String updateCuota ="";
    		
    		if(this.objTransaccion.getTipoTrans() == 'I') {
    			String cuentaAsociada = txfCuentaAsociada.getText();
    			//Actualizar cuenta asociada. Si se dejó la cuenta anterior, igual se actualizará.
    			if(controlGeneral.validarCuentaBancaria(cuentaAsociada)) {
    				updateCuota = "update cuotaspagoinversion set cuentapagoactual = '"+cuentaAsociada+"' where codcuotainversion="+this.objCuotaPago.getCodCuota()+";";
            		controlGeneral.ejecutarSentenciaInsert(updateCuota);
    			}else {
    				controlGeneral.mostrarAlerta(AlertType.ERROR, "Cuenta bancaria inválida", "Cuenta bancaria inexistente",
    						"La cuenta no está registrada en el sistema. Para registrarla presione en el botón Registrar cuenta");
    				return;
    			}    			
    		}
    	
    		LocalDate fechaEfectivaPago = LocalDate.now();
    		
    		//Actualizar comprobante de pago
    		updateCuota = "update cuotaspago set codcomprobante = '"+codComprobante+"' where codcuota="+this.objCuotaPago.getCodCuota()+";";
    		controlGeneral.ejecutarSentenciaInsert(updateCuota);
    		
    		//Actualizar modalidad de pago
    		updateCuota = "update cuotaspago set modalpagocuota = '"+modalidadPago+"' where codcuota="+this.objCuotaPago.getCodCuota()+";";
    		controlGeneral.ejecutarSentenciaInsert(updateCuota);
    		
    		//Actualizar fecha efectiva de pago
    		updateCuota = "update cuotaspago set fechaefectivacuota = '"+fechaEfectivaPago+"' where codcuota="+this.objCuotaPago.getCodCuota()+";";
    		controlGeneral.ejecutarSentenciaInsert(updateCuota);
    		
    		//Actualizar estado 
    		updateCuota = "update cuotaspago set estadocuota = 'PAGADA' where codcuota="+this.objCuotaPago.getCodCuota()+";";
    		controlGeneral.ejecutarSentenciaInsert(updateCuota);
    		
    		controlGeneral.mostrarAlerta(AlertType.INFORMATION, "Pago realizado", "Pago realizado", "Información guardada en el sistema");
    		
    	}else {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Operación rechazada", "Operación rechazada", "No se realizó el pago de la cuota");
    	    return;
    	}
    }

   @FXML
   public  void initialize() {
    	//Se cargan los iconos
    	 this.inicio_logo = new ImageView (new Image (getClass().getResourceAsStream("/vista/icon/inicio_logo.png")));
    	 this.reg_logo = new ImageView (new Image (getClass().getResourceAsStream("/vista/icon/reg_logo.png")));
    	 //Panel de inicio
    	 esconderPanesMenosIndicado(anpPagosInicio);
    	 
    	 //Inicialización de los obj del modelo    	
    	 this.controlGeneral = new ControladorGeneral();
    	 this.objTransaccion = new Transaccion();
    	 this.cuotaInversionPago = new CuotasPagoInversion();
    	 
    	 this.cbxModalidadPago.setItems(FXCollections.observableArrayList("CHEQUE","EFECTIVO","OTRO")); 
    }
    
    @FXML    
    public void entrarHome(ActionEvent event) throws IOException {
		Parent home = FXMLLoader.load(getClass().getResource("/vista/home.fxml"));
		Scene homeScene = new Scene(home);
		Window nodo = ((Node) event.getSource()).getScene().getWindow();
		Stage ventana = (Stage)(nodo);
		ventana.setScene(homeScene);
		ventana.show();
    }
    
    @FXML
	public void botonAtras(ActionEvent event) throws SQLException {
    	Node nodo = (Node) event.getSource();
		Parent actual = nodo.getParent();
		
		if(actual.equals(anpPagoCuota)) {
			this.continuarCuotasPago(event);			
		}else {
			ObservableList<Node> hijos = stpCenter.getChildren();
			for(int i=0; i<hijos.size();i++) {
				if(hijos.get(i).equals(actual))
					esconderPanesMenosIndicado(hijos.get(i-1));
			}
		}	
	}
    
    @FXML
    public void nuevaCuentaBancariaRegTrans(ActionEvent event) throws IOException {
    	
    	//Se carga el FXML Nueva Garantía.
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/nuevaCuentaBanc.fxml"));
    	Parent interfazNuevaCuenta = loader.load();
		//Se crea un objeto del constructor NuevaGarantiaControlador y se le carga el controlador del fxml cargado anteriormente.
    	NuevaCuentaBancControlador nuevaCuentaBancControlador = loader.getController();
    	//Se ejecuta el método de cambio de cédula del controlador NuevaGarantía.
    	nuevaCuentaBancControlador.setClienteNuevaCuentabanc(objTransaccion.getClienteTrans());
    	
    	Scene escenaNuevaCuenta = new Scene(interfazNuevaCuenta);	
		Stage ventanaNuevaCuenta = new Stage();
		ventanaNuevaCuenta.setScene(escenaNuevaCuenta);
		ventanaNuevaCuenta.setTitle("Nueva cuenta");
		ventanaNuevaCuenta.show();
    }
    
    public void esconderPanesMenosIndicado(Node nodo) { 
    	
		ObservableList<Node> hijos = stpCenter.getChildren();
		for(Node hijo : hijos) {
			hijo.setVisible(false);
		}
		nodo.setVisible(true);
	}
    
    public void limpiarCampos() {
    	this.txfClientePagos.clear();
    	this.txfCodComprobante.clear();
    	this.txfCuentaAsociada.clear();
    	this.cbxModalidadPago.getSelectionModel().clearSelection();
    }  
    
}
