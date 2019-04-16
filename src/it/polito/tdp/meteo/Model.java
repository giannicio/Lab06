package it.polito.tdp.meteo;

import java.time.Month;
import java.util.*;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {
	
	List<Citta> bestSeq;
	double costoB;
	List<Citta> parziale = new ArrayList<Citta>();

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	MeteoDAO meteoD = new MeteoDAO();
	List<Citta> listaC = new ArrayList<Citta>();

	public Model() {

	}

	public String getUmiditaMedia(int mese) {

		return "TODO!";
	}

	public List<Citta> trovaSequenza(int mese) {

		for(String c : meteoD.localita()) {
			listaC.add(new Citta(c,meteoD.getAllRilevamentiLocalitaMese(mese, c)));
		}
		costoB=Double.MAX_VALUE;
		cercaSeq(parziale,0);
		return bestSeq;
	}

	private void cercaSeq(List<Citta> parziale, int L) {
		if(parziale.size() > 15) {
			return;
		}
		
		if(this.parzialeSbagliato(parziale)) {
			return;
		}
		
		if(L==15) {
			if(controlloPresenza(parziale)) {
				double costo = this.punteggioSoluzione(parziale);
					if(costo < costoB) {
						bestSeq = new ArrayList<Citta>(parziale);
						costoB = costo;
						System.out.println(bestSeq +" "+ costoB);
						return;
					}
			}
		}
		
		
		for(Citta c: listaC) {
			
			parziale.add(c);
			cercaSeq(parziale,L+1);
			parziale.remove(parziale.size()-1);
		}
		
	}

	private boolean controlloPresenza(List<Citta> parziale) {
		boolean result = true;
		int cityCounter = 0;
		for(Citta c : listaC) {
			c.setCounter(cityCounter);
			for(Citta ct : parziale) {
				if(ct.getNome().equals(c.getNome()))
					c.increaseCounter();
			}
		}
		
		for(Citta c : listaC) {
			if(c.getCounter()<1) {
				result =false;
			}
		}
		return result;
	}

	private Double punteggioSoluzione(List<Citta> soluzioneCandidata) {

		double score = 0.0;
		
		for(int i = 0; i<soluzioneCandidata.size(); i++) {
			score += soluzioneCandidata.get(i).getRilevamenti().get(i).getUmidita();
			if(i<14 && !(soluzioneCandidata.get(i).equals(soluzioneCandidata.get(i+1)))) {
				score +=100;
			}
		}
		return score;
	}

	private boolean parzialeSbagliato(List<Citta> parziale) {
		// ritorno true --> soluzione ERRATA
		// ritorno false --> soluzione OK
		boolean result = true;
		// di default è true quindi entra e fa return, cioè scarta la soluzione
		// il boolean diventera false se la soluzione è buona
		int cityCounter = 0;
		for(Citta c : listaC) {
			c.setCounter(cityCounter);
			for(Citta ct : parziale) {
				if(ct.getNome().equals(c.getNome()))
					c.increaseCounter();
			}
		}
		
		for(Citta c : listaC) {
			if(c.getCounter()>6)
				return result; // result = true soluzione non buona 
		}
		// CASO GIORNO 1
			if(parziale.size() == 1) // se è la prima citta del primo giorno, esci dal controllo
				return false;
		// CASO GIORNO 2
			if(parziale.size() == 2) { // se sono al secondo giorno allora la seconda citta è uguale alla prima
				return !(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)));
			}
		// CASO GIORNO 3
			if(parziale.size() == 3) {
				return !(parziale.get(parziale.size()-1).equals(parziale.get(parziale.size()-2)) && parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)));
			}
		// CASO GIORNO > 3	
			if(parziale.size()>3 && parziale.size()<16){
				if(!parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-1))) { // se due successivi sono diversi
					return !(parziale.get(parziale.size()-2).equals(parziale.get(parziale.size()-3)) && parziale.get(parziale.size()-3).equals(parziale.get(parziale.size()-4))); 
		
					
				}
			}

			
		result = false;
		return result;
	}
	
	

}
