package fr.ffessm.doris.android.sitedoris;


import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;


// Entête Fiche permettant d'avoir une emprunte mémoire minimum
public class FicheLight {
	public static Log log = LogFactory.getLog(FicheLight.class);
	
	private int numeroFiche;
	private int etatFiche;
	private String nomScientifique;
	private String nomCommun;
	
	public FicheLight(int numeroFiche, int etatFiche){
		this.numeroFiche = numeroFiche;
		this.etatFiche = etatFiche;
	}
	
	public FicheLight(int numeroFiche, int etatFiche, String nomScientifique, String nomCommun){
		this.numeroFiche = numeroFiche;
		this.etatFiche = etatFiche;
		this.nomScientifique = nomScientifique;
		this.nomCommun = nomCommun;
	}
	
	public int getNumeroFiche() {
		return numeroFiche;
	}
	public int getEtatFiche() {
		return etatFiche;
	}
	public String getNomScientifique() {
		return nomScientifique;
	}
	public String getNomCommun() {
		return nomCommun;
	}

	public String getCleCompareUpdate(){
		return numeroFiche+";"+etatFiche;
	}
	
}
