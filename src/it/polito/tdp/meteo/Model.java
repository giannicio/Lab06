package it.polito.tdp.meteo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	private Double bestPunteggio = 10000000.0;
	private List<Citta> listaCitta = new ArrayList<Citta>();
	private List<Citta> bestSequenza = new ArrayList<Citta>();
	
	private MeteoDAO rilevamentiMeseDAO = new MeteoDAO();
	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {
		return rilevamentiMeseDAO.getAllRilevamentiLocalitaMese(mese, localita);
	}
	
	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {
		return rilevamentiMeseDAO.getAvgRilevamentiLocalitaMese(mese, localita);
	}
	
	private MeteoDAO getAllCittaDAO = new MeteoDAO();
	public List<Citta> ottieniCitta() {
		return getAllCittaDAO.getAllCitta();
	}


	public Model() {
		this.listaCitta = ottieniCitta();
	}

	public String getUmiditaMedia(int mese) {
		
		return "TODO!";
	}

	public List<Citta> calcolaSequenza(int mese, int L, List<Citta> sequenzaParziale) {
		trovaSequenza(mese, L, sequenzaParziale);
		return bestSequenza; 
	}
	
	public void trovaSequenza(int mese, int L, List<Citta> sequenzaParziale) {
		
		// caso terminale
		if(L == NUMERO_GIORNI_TOTALI) {
			
			if (controllaParziale(sequenzaParziale) == true) { //  && sequenzaParziale != bestSequenza
				// ho passato i controlli devo calcolare il costo totale della soluzione
				Double punteggioParziale = punteggioSoluzione(sequenzaParziale, mese);
				// aggiorno il best
				if (punteggioParziale < bestPunteggio) {
					bestPunteggio = punteggioParziale;
					bestSequenza = new ArrayList<>(sequenzaParziale);
					System.out.println(bestSequenza);
					System.out.println(bestPunteggio);
					
					
				}
			}
			return;
		}

			for(int i = 0; i < listaCitta.size(); i++) {
				if (possoAggiungereCitta(listaCitta.get(i), sequenzaParziale)) { // controllo che la citta non ci sia gia 6 volte
					sequenzaParziale.add(listaCitta.get(i)); 
					trovaSequenza(mese, L+1, sequenzaParziale);
					sequenzaParziale.remove(sequenzaParziale.size()-1);
				}
			}
		}


	private boolean possoAggiungereCitta(Citta citta, List<Citta> listaCitta) { // controllo durante la ricorsione
		boolean result = false;
		int cont = 0;
		for(Citta c: listaCitta) {
			if(c.equals(citta)) {
				cont ++;
			}
		}
		if (cont <= 5) {
			result = true;
		}
		return result;
	}
	private Double punteggioSoluzione(List<Citta> soluzioneCandidata, int mese) {
		// devo ottenere l'umidita per la citta nel mese selezionato
		double score = 0.0;
		for(int i = 0; i < soluzioneCandidata.size(); i++) {
			List<Rilevamento> rilevamenti = getAllRilevamentiLocalitaMese(mese, soluzioneCandidata.get(i).getNome());
			score += rilevamenti.get(i).getUmidita(); // somma di tutti i volori di umidita delle 15 citta
			if(i <= 13) {
				if(!soluzioneCandidata.get(i).equals(soluzioneCandidata.get(i+1))) { // aggiungo 100 se citta i diversa da citta i+1
					score += COST;
				}
			}
		}
		
		return score;
	}

	private boolean controllaParziale(List<Citta> parziale) { // controllo alla fine della ricorsione una volta ottenuta una soluzione
		// controllare che in una citta devo stare almeno tre giorni consecutivi e che nella lista finale ci siano tutte e tre le citta almeno una volta
		boolean controlloPresenza = false;
		boolean result = false;
		int contTorino = 0;
		int contMilano = 0;
		int contGenova = 0;
		int contSuccessivi_1 = 0;
		int contSuccessivi_2 = 0;
		for(Citta c: parziale) {
		
			if(c.getNome().contains("Torino"))
				contTorino ++;
			
			if(c.getNome().contains("Milano"))
				contMilano ++;
			
			if(c.getNome().contains("Genova"))
				contGenova ++;
		}
		
		if(contTorino > 0 && contMilano > 0 && contGenova > 0)
			controlloPresenza = true;
		
		for(int i = 0; i < parziale.size(); i++) {
			if(i >= 2 && i < 14) {
				if(!parziale.get(i).equals(parziale.get(i+1))) {
					contSuccessivi_1 ++;// se due successivi sono diversi
					if (parziale.get(i).equals(parziale.get(i-1)) && parziale.get(i-1).equals(parziale.get(i-2))) { 
						contSuccessivi_2 ++;
					}
				}
		
		}	
		}
		if(controlloPresenza == true && contSuccessivi_1 == contSuccessivi_2) 
			result = true;
		return result;
	}
}
