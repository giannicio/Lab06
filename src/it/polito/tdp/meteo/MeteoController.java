package it.polito.tdp.meteo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class MeteoController {
	
	Model model;
	
	public void setModel(Model model) {
		this.model = model;
		boxMese.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
	}

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<String> boxMese;

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCalcolaSequenza(ActionEvent event) {
		int mese = Integer.parseInt(boxMese.getValue()); 
		int L = 0;
		List<Citta> parziale = new ArrayList<Citta>();
		List<Citta> best = model.calcolaSequenza(mese, L, parziale);
		for(Citta c: best) {
			txtResult.appendText(c + "\n");
			System.out.print(c);
		}
	}

	@FXML
	void doCalcolaUmidita(ActionEvent event) {
		
		int mese = Integer.parseInt(boxMese.getValue()); 
		double AvgrilevamentiMeseTorino = model.getAvgRilevamentiLocalitaMese(mese, "Torino");
		double AvgrilevamentiMeseMilano = model.getAvgRilevamentiLocalitaMese(mese, "Milano");
		double AvgrilevamentiMeseGenova = model.getAvgRilevamentiLocalitaMese(mese, "Genova");
		txtResult.appendText("Torino: " + "   " + AvgrilevamentiMeseTorino +"\n");
		txtResult.appendText("Milano: " + "   " + AvgrilevamentiMeseMilano +"\n");
		txtResult.appendText("Genova: " + "   " + AvgrilevamentiMeseGenova +"\n");
 	}

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";
	}

}
