/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */
package fr.ffessm.doris.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.collection.LruCache;

import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Jeu;


/** Classe globale pour accéder aux informations générales de l'application */
public class DorisApplicationContext {

	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getSimpleName();
	
	/** singleton intance */
	private static DorisApplicationContext instance = null;
	
	/** singleton contructor */
	private DorisApplicationContext(){
		if (BuildConfig.DEBUG) Log.v(LOG_TAG, "DorisApplicationContext - Début");
		if (BuildConfig.DEBUG) Log.v(LOG_TAG, "DorisApplicationContext - Fin");
	}
	
	public static DorisApplicationContext getInstance(){
		//if (BuildConfig.DEBUG) Log.v(LOG_TAG, "DorisApplicationContext - getInstance() - Début");
		if(instance == null) instance = new DorisApplicationContext();
		//if (BuildConfig.DEBUG) Log.v(LOG_TAG, "DorisApplicationContext - getInstance() - Fin");
		return instance;
	}
	
	// used to get a pointer on running background activities, usefull when on onCreate onDestroy in case of configuration changes like rotation
	public TelechargePhotosAsync_BgActivity telechargePhotosFiches_BgActivity = null;

	public boolean isTelechPhotos = false;
	public boolean isMovingPhotos = false;
	public ZoneGeographiqueKind zoneTraitee = null;


	/* global cache to help indexation  used by IndexHelper */
	public LruCache<Integer, Fiche> ficheCache = new LruCache<>(100);
	public LruCache<Integer, DefinitionGlossaire> glossaireCache = new LruCache<>(100);
	public LruCache<Integer, Participant> participantCache = new LruCache<>(100);
	public LruCache<Integer, EntreeBibliographie> bibliographieCache = new LruCache<>(100);

	
	// Permet de piloter finement l'interface de retour (depuis le bouton HOME)
	// (i.e. sans respecter forcément le manifest.xml)
	// Accueil <-> Liste Fiches <-> Fiche
	// Accueil <-> Liste Images Fiches <-> Fiche
	// Accueil <-> Groupes <-> Liste Fiches <-> Fiche
	// Accueil <-> Groupes <-> Liste Images Fiches <-> Fiche
	// ... <-> ... <-> Fiche <-> Définitions, Intervenants ...
	protected Stack<Intent>  retourIntentStack = new Stack<>();
	protected Intent rootIntent;
    public void setIntentPourRetour(Intent currentIntent){
    	Log.d(LOG_TAG, "setIntentPourRetour() - currentIntent.getComponent() : "+currentIntent.getComponent());
	    if(retourIntentStack.isEmpty()){
		    rootIntent = currentIntent;
	    }
	    if(retourIntentStack.isEmpty() || retourIntentStack.peek().getComponent() != currentIntent.getComponent()){
		    retourIntentStack.push(currentIntent);
		    Log.d(LOG_TAG, "setIntentPourRetour() - retourIntentNiveau : "+retourIntentStack.size());
	    }

    }
    public Intent getIntentPrecedent(){
    	Log.d(LOG_TAG, "getIntentPrecedent() - retourIntentNiveau : "+retourIntentStack.size());

	    if(retourIntentStack.isEmpty()){
		    // oups !? retour à l'acceuil principal
		    Log.w(LOG_TAG, "getIntentPrecedent() - empty stack -> return to root ");
		    return rootIntent;
	    } else {
	    	return retourIntentStack.pop();
	    }
    }
	public void resetIntentPrecedent(Intent currentIntent){
		rootIntent = currentIntent;
		retourIntentStack.clear();
		retourIntentStack.push(currentIntent);
	}
    
	/** listener that have registered for being notified of data changes */
	private ArrayList<DataChangedListener>  dataChangeListeners = new ArrayList<>();
	
	public List<DataChangedListener> getDataChangeListeners(){
		synchronized(dataChangeListeners){
			return new ArrayList<>(dataChangeListeners);
		}
	}
	public void addDataChangeListeners(DataChangedListener listener){
		synchronized(dataChangeListeners){
			dataChangeListeners.add(listener);
		}
	}
	public void removeDataChangeListeners(DataChangedListener listener){
		synchronized(dataChangeListeners){
			dataChangeListeners.remove(listener);
		}
	}
	/**
	 * Averti les listener que les données ont changées
	 */
	public void notifyDataHasChanged(String message){
		for (DataChangedListener listener :  getDataChangeListeners()) {
			listener.dataHasChanged(message);
		}
	}

	// Gestion de l'interface des Jeux
    public Jeu.Statut jeuStatut;
    public Jeu.JeuRef jeuSelectionne;
    public ZoneGeographique jeuZoneGeographiqueSelectionnee;
    public Jeu.Niveau jeuNiveauSelectionne;
	// Enregistrées pour les resume()
    public Fiche jeuFicheEnCours;
    public ClassificationFiche jeuClassificationFicheEnCours;
    public Classification jeuClassificationEnCours;
	public boolean reponseOK;
}
