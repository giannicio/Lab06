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

		for(String c : meteoD.localita())
			listaC.add(new Citta(c,meteoD.getAllRilevamentiLocalitaMese(mese, c)));
		bestSeq = null;
		costoB=Double.MAX_VALUE;
		
		cercaSeq(parziale,0);
		
		return bestSeq;
	}

	private void cercaSeq(List<Citta> parziale, int L) {
		
		if(L==15) {
			if(controlloPresenza(parziale)) {
			double costo = this.punteggioSoluzione(parziale);
			if(costo<costoB) {
				bestSeq= parziale;
				costoB=costo;
				System.out.println("Scarto"+bestSeq);
				return;
				}
			}
		}
		
		if(this.controllaParziale(parziale)) {
			return;
		}
		
		for(Citta c: listaC) {
			parziale.add(c);
			cercaSeq(parziale,L+1);
			parziale.remove(c);
		}
		
	}

	private boolean controlloPresenza(List<Citta> parziale) {
		boolean result = true;
		for(Citta c : listaC) {
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
		
		for(int i = 0; i<soluzioneCandidata.size();i++) {
			score += soluzioneCandidata.get(i).getRilevamenti().get(i).getUmidita();
			if(i<14 && !(soluzioneCandidata.get(i).equals(soluzioneCandidata.get(i+1)))) {
				score +=100;
			}
		}
		return score;
	}

	private boolean controllaParziale(List<Citta> parziale) {
		boolean result = true; // di default è true quindi entra e fa return, cioè scarta la soluzione
		for(Citta c : listaC) {
			for(Citta ct : parziale) {
				if(ct.getNome().equals(c.getNome()))
					c.increaseCounter();
			}
		}
		
		for(Citta c : listaC) {
			if(c.getCounter()>6)
				return result;
		}
		
	/*	for(int i =0; i<parziale.size();i++) {
			if(parziale.size()-1==0)
				return false;
			if(i>0 && (parziale.size()==0||parziale.size()==1)) {
				return !(parziale.get(i).equals(parziale.get(i-1)));
			}
				
			if(i>=2 && i<(parziale.size()-1) && (!parziale.get(i).equals(parziale.get(i+1)) && !(parziale.get(i).equals(parziale.get(i-1)) && parziale.get(i-1).equals(parziale.get(i-2))))){
				return result;
			}
		}*/
			
		result = false;
		return result;
	}
	
	

}
