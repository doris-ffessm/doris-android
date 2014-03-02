package fr.ffessm.doris.android.sitedoris;

// Entête Fiche permettant d'avoir une emprunte mémoire minimum
public class FicheLight {
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
	
	@Override
	public boolean equals(Object other) {
	    return other != null && other instanceof FicheLight && this.numeroFiche == ((FicheLight)other).numeroFiche;
	}
}
