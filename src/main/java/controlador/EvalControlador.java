package controlador;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import modelo.CuotaPago;
import modelo.SimulacionCredito;
import modelo.Transaccion;

public class EvalControlador {
	
	private ControladorGeneral controlGeneral = new ControladorGeneral();

    @FXML
    private TableView<Transaccion> tblEvaluacion;
    @FXML
    private TableColumn<Transaccion, Integer> tblColEvalCodigo;
    @FXML
    private TableColumn<Transaccion, Character> tblColEvalTipo;
    @FXML
    private TableColumn<Transaccion, String> tblColEvalCliente;
    @FXML
    private TableColumn<Transaccion, Double> tblColEvalMonto;
    @FXML
    private TableColumn<Transaccion, LocalDate> tblColEvalFecha;
    @FXML
    private TableColumn<Transaccion, String> tblColEvalEstado;
    
    ObservableList<Transaccion> listaEvaluacion;
    
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
    public void aprobarSolicitud(ActionEvent event) throws SQLException {
    	Transaccion transSelected = this.tblEvaluacion.getSelectionModel().getSelectedItem();
    	if(transSelected==null) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Transacción no seleccionada", "Transacción no seleccionada",
    				"Por favor haga click sobre alguna transacción y presione Ver Detalles.");
    		return;
    	}    	
    	    	    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Aprobar solicitud");
    	alert.setHeaderText("Aprobando solicitud...");
    	alert.setContentText("¿Está seguro?");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		transSelected.setFechaAprobacion(LocalDate.now());
        	String sentenciaSQL = "update transacciones set fechaaprobacion='"+transSelected.getFechaAprobacion()+"' where codtrans="+transSelected.getCodTrans()+";";
        	controlGeneral.ejecutarSentenciaInsert(sentenciaSQL);    
    		sentenciaSQL = "update transacciones set estadoSolicitud= 'EN CURSO' where codtrans="+transSelected.getCodTrans()+";";
        	controlGeneral.ejecutarSentenciaInsert(sentenciaSQL);        	
        	
        	double mensualidad=0;
        	double monto= transSelected.getMontoTrans();
        	double tasaMensual = (Math.pow(transSelected.getTasaTrans()+1, 1.0/12.0)) - 1.0; 
        	List<CuotaPago> listaCuotaPago = new ArrayList<>();
        	
        	if(transSelected.getTipoTrans()=='I') {
        		mensualidad = monto * ((Math.pow(1+(tasaMensual/100.0), transSelected.getNumCuotas()))-1);
        	}else {
        		mensualidad= (tasaMensual*monto)/(1.0 - (Math.pow((1+tasaMensual),-transSelected.getNumCuotas())));
        	}        	
        	
        	for(int i=0; i<transSelected.getNumCuotas();i++) {
        		int noCuota = i+1;
        		LocalDate fechaPago = transSelected.getFechaIniciacion().plusMonths((long) i+1);
        		CuotaPago cuotai = new CuotaPago(0,"",transSelected.getCodTrans(),mensualidad,fechaPago,null,"","EN MORA");
        		listaCuotaPago.add(cuotai);
        	}
        	
        	if(transSelected.getTipoTrans()=='I') {        		
        		listaCuotaPago.forEach(cuota ->{
        			String ordenSQL = "insert into cuotaspago(transcuota,menscuota,fechapagocuota,estadocuota) "
        					+ "values ("+cuota.getTransCuota()+","+Math.round(cuota.getMensCuota())+","
        							+ "'"+cuota.getFechaPagoCuota()+"','EN MORA') RETURNING codcuota;";

        			//Obtiene el codigo de la cuenta generado por SQL después de haberla insertado 
        			ResultSet codcuotaRS = controlGeneral.ejecutarSentencia(ordenSQL);        			
        			
        			//Conseguir cuenta para agregar una cuota de inversión
					String ordenSQL2 = "select cuentapagogeneral from inversiones join transacciones on codtrans=codinversion "
							+ "where codtrans="+cuota.getTransCuota()+";";
					ResultSet consultaCuenta = controlGeneral.ejecutarSentencia(ordenSQL2);	
					
					try {
						int codcuota = 0;
						while(codcuotaRS.next()) {
							codcuota = codcuotaRS.getInt("codcuota");
						}
						String cuentaInversion = "";
						while(consultaCuenta.next()) {
							cuentaInversion = consultaCuenta.getString("cuentapagogeneral");
						}						
						//Insert con los datos obtenidos
						controlGeneral.ejecutarSentenciaInsert("insert into cuotaspagoinversion values("+codcuota+",'"+cuentaInversion+"');");
					} catch (SQLException e) {						
						e.printStackTrace();
					}       			
        		});
        	}else {
        		listaCuotaPago.forEach(cuota ->{         			
        			String ordenSQL = "insert into cuotaspago(transcuota,menscuota,fechapagocuota,estadocuota) "
        					+ "values ("+cuota.getTransCuota()+","+Math.round(cuota.getMensCuota())+","
        							+ "'"+cuota.getFechaPagoCuota()+"','EN MORA') RETURNING codcuota;";
        			
        			controlGeneral.ejecutarSentenciaInsert(ordenSQL);
        		});
        		
        	}
        	
        	controlGeneral.mostrarAlerta(AlertType.INFORMATION, "Aprobar solicitud", "Solicitud aprobada", "Cronograma de pagos habilitado");
    	} else {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Operación rechazada", "Operación rechazada", "No se actualizó el estado de la solicitud");
    	    return;
    	}
    	this.initialize();
    }   

    @FXML
    public void rechazarSolicitud(ActionEvent event) throws SQLException {
    	Transaccion transSelected = this.tblEvaluacion.getSelectionModel().getSelectedItem();
    	if(transSelected==null) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Transacción no seleccionada", "Transacción no seleccionada",
    				"Por favor haga click sobre alguna transacción y presione Ver Detalles.");
    		return;
    	} 
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Rechazar solicitud");
    	alert.setHeaderText("Rechazando solicitud...");
    	alert.setContentText("¿Está seguro?");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
	    	String sentenciaSQL = "update transacciones set estadoSolicitud= 'RECHAZADA' where codtrans="+transSelected.getCodTrans()+";";
	    	controlGeneral.ejecutarSentenciaInsert(sentenciaSQL);
	    	controlGeneral.mostrarAlerta(AlertType.INFORMATION, "Rechazar solicitud", "Solicitud rechazada", "No se generó cronograma de pagos");
	    	this.initialize();
    	}else {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "Operación rechazada", "Operación rechazada", "No se actualizó el estado de la solicitud");
    	}
    }

    @FXML
    public void verDetalles(ActionEvent event) throws SQLException {
    	Transaccion transSelected = this.tblEvaluacion.getSelectionModel().getSelectedItem();
    	if(transSelected==null) {
    		controlGeneral.mostrarAlerta(AlertType.ERROR, "ERROR: Transacción no seleccionada", "Transacción no seleccionada",
    				"Por favor haga click sobre alguna transacción y presione Ver Detalles.");
    		return;
    	}
    	
    	AnchorPane verDetalles = new AnchorPane();
    	TextArea txaVerDetalles = new TextArea();
    	txaVerDetalles.setEditable(false);
    	verDetalles.getChildren().add(txaVerDetalles);
    	
    	txaVerDetalles.setText(transSelected.toString());
    	String fechaAprobacion;
    	if(transSelected.getFechaAprobacion()==null)
    		fechaAprobacion = "No definida aún";
    	else
    		fechaAprobacion = transSelected.getFechaAprobacion().toString();
    	
    	//Búsqueda de todos los datos de la transacción.
    	String datosTrans = "\nTRANSACCIÓN"+
					    	"\nCódigo:" + transSelected.getCodTrans()+
					    	"\nTipo: " +transSelected.getTipoTrans()+
					    	"\nMonto: " +transSelected.getMontoTrans()+
					    	"\nTasa efectiva anual: " + transSelected.getTasaTrans()+
					    	"\nNúmero de periodos: " + transSelected.getNumCuotas()+
					    	"\nFecha de solicitud: " + transSelected.getFechaSolicitud()+
					    	"\nFecha de aprobación: " + fechaAprobacion+
					    	"\nFecha de iniciación: " + transSelected.getFechaIniciacion()+
					    	"\nFecha de término: " + transSelected.getFechaTermino()+
					    	"\nEstado de solicitud: " + transSelected.getEstadoSolicitud();
    	
    	//Obtención de los datos del cliente.
    	String datosCliente ="";
    	ResultSet clienteTrans = controlGeneral.ejecutarSentencia("select cliente.* from cliente "
    			+ "join transacciones on cedula=clientetrans where codtrans = "+transSelected.getCodTrans()+";");
    	if(clienteTrans.next()) {
    	datosCliente = "\n\nCLIENTE"+
				    	"\nCédula: " + clienteTrans.getString("cedula")+
				    	"\nNombre: " +clienteTrans.getString("nombres")+
				    	"\nTeléfono: " + clienteTrans.getString("telefono")+
				    	"\nEmail: " + clienteTrans.getString("email")+
				    	"\nDirección: " + clienteTrans.getString("direccion");
    	}
    	
    	String datosExtra="";
    	
    	if(transSelected.getTipoTrans()=='I') {
    		//Se obtiene la cuenta bancaria.
    		ResultSet cuentaTrans = controlGeneral.ejecutarSentencia("select cuentasbancarias.* from cuentasbancarias join inversiones on cuentapagogeneral=numcuentabanc\r\n" + 
    				"join transacciones on codtrans=codinversion where codtrans ="+transSelected.getCodTrans()+";");
    		if(cuentaTrans.next()) {
    		datosExtra = "\n\nCUENTA BANCARIA"+
    					"\nNúmero de cuenta: " + cuentaTrans.getString("numcuentabanc")+
    					"\nBanco: " + cuentaTrans.getString("bancocuentabanc")+
    					"\nTipo de cuenta: " + cuentaTrans.getString("tipocuentabanc");
    		}    		 		
    	}
    	else {//Si no es una inversión, es un préstamo.
    		
    		//Se obtiene el Fiador.
    		ResultSet fiadorTrans = controlGeneral.ejecutarSentencia("select cliente.* from cliente "
    				+ "join prestamos on fiador=cedula join transacciones on codprestamo=codtrans\r\n" + 
    				"where codtrans = "+transSelected.getCodTrans()+";");
    		//Se obtiene las garantías.
    		ResultSet garantiasTrans = controlGeneral.ejecutarSentencia("select garantias.* from garantias natural join garantias_prestamo join transacciones on codprestamo=codtrans\r\n" + 
    				"where codtrans = "+transSelected.getCodTrans()+";");    		
    		
    		if(fiadorTrans.next()) {
    		datosExtra = "\n\nFIADOR"+
    				"\nCédula: " + fiadorTrans.getString("cedula")+
    		    	"\nNombre: " +fiadorTrans.getString("nombres")+
    		    	"\nTeléfono: " + fiadorTrans.getString("telefono")+
    		    	"\nEmail: " + fiadorTrans.getString("email")+
    		    	"\nDirección: " + fiadorTrans.getString("direccion");
    		}
    		StringBuilder datosGarantias = new StringBuilder();
    		if(garantiasTrans.next()) {
    			ResultSet garantiasTrans2 = controlGeneral.ejecutarSentencia("select garantias.* from garantias natural join garantias_prestamo join transacciones on codprestamo=codtrans\r\n" + 
        				"where codtrans = "+transSelected.getCodTrans()+";"); 
	    		datosGarantias.append("\n\nGARANTIAS");
	    		while(garantiasTrans2.next()) {
	    			datosGarantias.append("\n--------------------------------------"+
	    							"\nCódigo: " + garantiasTrans2.getInt("codgarantia")+
	    							"\nTipo: " + garantiasTrans2.getString("tipogarantia")+
	    							"\nValor: " + garantiasTrans2.getString("valorgarantia")+
	    							"\nUbicación: " + garantiasTrans2.getString("ubigarantia"));
	    		}
	    		
	    		datosExtra = datosExtra + datosGarantias.toString();
    		}
    	}
    	
    	String stgVerDetalles = "DETALLES\n" + datosTrans + datosCliente+datosExtra;   	
    	
    	txaVerDetalles.setText(stgVerDetalles);
    	txaVerDetalles.setPrefSize(500, 500);
    	txaVerDetalles.setStyle("-fx-font-size:16");
    	
    	Scene verDetallesScene = new Scene(verDetalles);
		Stage verDetallesStage = new Stage();
		verDetallesStage.setScene(verDetallesScene);
		verDetallesStage.setTitle("Detalles de transacción");
		verDetallesStage.show();    	
    }

    @FXML
    public void initialize() throws SQLException {

    	this.listaEvaluacion = FXCollections.observableArrayList();
    	
    	this.tblColEvalCodigo.setCellValueFactory(new PropertyValueFactory<Transaccion, Integer>("codTrans"));
    	this.tblColEvalTipo.setCellValueFactory(new PropertyValueFactory<Transaccion, Character>("tipoTrans"));
    	this.tblColEvalCliente.setCellValueFactory(new PropertyValueFactory<Transaccion, String>("clienteTrans"));
    	this.tblColEvalMonto.setCellValueFactory(new PropertyValueFactory<Transaccion, Double>("montoTrans"));
    	this.tblColEvalFecha.setCellValueFactory(new PropertyValueFactory<Transaccion, LocalDate>("fechaIniciacion"));
    	this.tblColEvalEstado.setCellValueFactory(new PropertyValueFactory<Transaccion, String>("estadoSolicitud"));
    	
    	
    	String sentenciaSQL = "select * from transacciones where estadosolicitud like 'PARA EVALUAR' or estadosolicitud like 'RECHAZADA';";
    	ResultSet transEstudio = this.controlGeneral.ejecutarSentencia(sentenciaSQL);    	
    	
    	while(transEstudio.next()) {
    		Transaccion objTrans = new Transaccion();
    		objTrans.setCodTrans(transEstudio.getInt("codTrans"));
    		objTrans.setClienteTrans(transEstudio.getString("clienteTrans"));
    		objTrans.setTipoTrans(transEstudio.getString("tipoTrans").charAt(0));
    		objTrans.setMontoTrans(transEstudio.getDouble("montoTrans"));
    		objTrans.setTasaTrans(transEstudio.getDouble("tasaTrans"));
    		objTrans.setNumCuotas(transEstudio.getInt("numCuotas"));
    		objTrans.setFechaSolicitud(transEstudio.getDate("fechaSolicitud").toLocalDate());
    		objTrans.setFechaAprobacion(null);
    		objTrans.setFechaIniciacion(transEstudio.getDate("fechaIniciacion").toLocalDate());
    		objTrans.setFechaTermino(transEstudio.getDate("fechaTermino").toLocalDate());
    		objTrans.setEstadoSolicitud(transEstudio.getString("estadoSolicitud"));
    		
    		this.listaEvaluacion.add(objTrans);
    	}
    	
    	this.tblEvaluacion.setItems(this.listaEvaluacion);
    	

    }
}
