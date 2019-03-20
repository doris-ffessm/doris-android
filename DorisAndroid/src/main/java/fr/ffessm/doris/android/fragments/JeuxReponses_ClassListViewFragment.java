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
package fr.ffessm.doris.android.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.tools.Jeu;

import static java.lang.Math.random;

public class JeuxReponses_ClassListViewFragment extends Fragment
{

	private static final String LOG_TAG = JeuxReponses_ClassListViewFragment.class.getCanonicalName();

    JeuSelectionneListener jeuSelectionneCallback;
    ZoneGeographiqueSelectionneeListener zoneGeographiqueSelectionneeCallback;
    NiveauSelectionneListener niveauSelectionneCallback;
    ReponseSelectionneeListener reponseSelectionneeCallback;

    Jeu jeuEncours;

    Jeu jeu1;
    Jeu jeu2;
    Jeu jeuClade;

    private LinearLayout llContainerLayout;

    public interface JeuSelectionneListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onJeuSelectionne(Jeu.JeuRef jeuSelectionne);
    }

    public interface ZoneGeographiqueSelectionneeListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onZoneGeographiqueSelectionnee(ZoneGeographique zone);
    }

    public interface NiveauSelectionneListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onNiveauSelectionne(Jeu.JeuRef jeuSelectionne, Jeu.Niveau niveau, boolean onResume);
    }

    public interface ReponseSelectionneeListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onReponseSelectionnee(Fiche ficheQuestion, int id, ImageView ivIcone);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "onAttach() - Début");

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            jeuSelectionneCallback = (JeuSelectionneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement JeuSelectionneListener");
        }
        try {
            zoneGeographiqueSelectionneeCallback = (ZoneGeographiqueSelectionneeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ZoneGeographiqueSelectionneeListener");
        }
        try {
            niveauSelectionneCallback = (NiveauSelectionneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NiveauSelectionneListener");
        }
        try {
            reponseSelectionneeCallback = (ReponseSelectionneeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ReponseSelectionneeListener");
        }

        Log.d(LOG_TAG, "onAttach() - Fin");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView() - Début");
        View view = inflater.inflate(R.layout.jeux_reponsesview_fragment, container, false);

        Log.d(LOG_TAG, "onCreateView() - Fin");
        return view;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart() - Début");
        super.onStart();

        Log.d(LOG_TAG, "onStart() - containerId : "+this.getId());
        Log.d(LOG_TAG, "onStart() - containerTag : "+this.getTag());
        Log.d(LOG_TAG, "onStart() - jeuEncours : "+ DorisApplicationContext.getInstance().jeuStatut);

        llContainerLayout =  (LinearLayout) getActivity().findViewById(R.id.jeu_reponses_liste_layout);

        jeu1 = new Jeu((Context) getActivity(), getActivity(), Jeu.JeuRef.JEU_1);
        jeu2 = new Jeu((Context) getActivity(), getActivity(), Jeu.JeuRef.JEU_2);
        jeuClade = new Jeu((Context) getActivity(), getActivity(), Jeu.JeuRef.JEU_CLADE);

        if (DorisApplicationContext.getInstance().jeuSelectionne == Jeu.JeuRef.JEU_1) jeuEncours = jeu1;
        if (DorisApplicationContext.getInstance().jeuSelectionne == Jeu.JeuRef.JEU_2) jeuEncours = jeu2;
        if (DorisApplicationContext.getInstance().jeuSelectionne == Jeu.JeuRef.JEU_CLADE) jeuEncours = jeuClade;

        if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.ACCUEIL) {
            createListeJeuxViews();
        }

        Log.d(LOG_TAG, "onStart() - Fin");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState() - Début");
        super.onSaveInstanceState(outState);

        Log.d(LOG_TAG, "onSaveInstanceState() - Fin");
    }

    /* Création de la liste des Jeux */
    public void createListeJeuxViews(){
        Log.d(LOG_TAG, "createListeJeuxViews() - Début");

        viderListeReponsesViews();

        llContainerLayout.addView(Jeu.getJeuView(jeuSelectionneCallback, (Context) getActivity(), Jeu.JeuRef.JEU_1));
        llContainerLayout.addView(Jeu.getJeuView(jeuSelectionneCallback, (Context) getActivity(), Jeu.JeuRef.JEU_2));
        llContainerLayout.addView(Jeu.getJeuView(jeuSelectionneCallback, (Context) getActivity(), Jeu.JeuRef.JEU_CLADE));

        Log.d(LOG_TAG, "createListeJeuxViews() - Fin");
    }

    /* Création de la liste des Zones Géographiques */
    public void createListeZonesGeographiquesViews(){
        Log.d(LOG_TAG, "createListeZonesGeographiquesViews() - Début");

        viderListeReponsesViews();

        // Affichage lien vers "toutes Zones"
        ZoneGeographique zoneToutesZones = new ZoneGeographique();
        zoneToutesZones.setToutesZones();
        Log.d(LOG_TAG, "createListeZonesGeographiquesViews() - zoneToutesZones : "+zoneToutesZones.getNom());

        llContainerLayout.addView( Jeu.getZoneGeographiqueView(zoneGeographiqueSelectionneeCallback, (Context) getActivity(), zoneToutesZones) );

        // Affichage lien vers les zones
        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(getActivity());

        List<ZoneGeographique> listeZoneGeo = ormLiteDBHelper.getZoneGeographiqueDao().queryForAll();
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "listeZoneGeo : "+listeZoneGeo.size());

        for (ZoneGeographique zoneGeo : listeZoneGeo) {
            llContainerLayout.addView( Jeu.getZoneGeographiqueView(zoneGeographiqueSelectionneeCallback, (Context) getActivity(), zoneGeo) );
        }

        Log.d(LOG_TAG, "createListeZonesGeographiquesViews() - Fin");
    }

    /* Création de la liste des Niveaux */
    public void createListeNiveauxViews(){
        Log.d(LOG_TAG, "createListeNiveauxViews() - Début");

        Jeu.JeuRef jeuId = DorisApplicationContext.getInstance().jeuSelectionne;
        if (jeuId == Jeu.JeuRef.JEU_CLADE) jeuEncours = jeuClade;
        if (jeuId == Jeu.JeuRef.JEU_1) jeuEncours = jeu1;
        if (jeuId == Jeu.JeuRef.JEU_2) jeuEncours = jeu2;


        viderListeReponsesViews();

        llContainerLayout.addView(jeuEncours.getNiveauView(niveauSelectionneCallback, (Context) getActivity(), jeuId, Jeu.Niveau.FACILE));
        llContainerLayout.addView(jeuEncours.getNiveauView(niveauSelectionneCallback, (Context) getActivity(), jeuId, Jeu.Niveau.INTERMEDIAIRE));
        llContainerLayout.addView(jeuEncours.getNiveauView(niveauSelectionneCallback, (Context) getActivity(), jeuId, Jeu.Niveau.DIFFICILE));

        Log.d(LOG_TAG, "createListeNiveauxViews() - Fin");
    }

    /* Création de la liste des Réponses */
    public void createListeReponsesJeuCLADEViews(ZoneGeographique zoneGeographique, Jeu.Niveau niveau, Fiche fiche, ClassificationFiche classificationFiche, Classification classification, boolean onResume){
        Log.d(LOG_TAG, "createListeReponsesViews() - Début");
        Log.d(LOG_TAG, "createListeReponsesViews() - zoneGeographique : "+zoneGeographique.getNom());
        Log.d(LOG_TAG, "createListeReponsesViews() - niveau : "+niveau.name());
        Log.d(LOG_TAG, "createListeReponsesViews() - fiche : "+fiche.getNomCommun());
        Log.d(LOG_TAG, "createListeReponsesViews() - onResume : "+onResume);
        Log.d(LOG_TAG, "createListeReponsesViews() - classification : "+classification.getTermeScientifique());

        if (onResume == false) {
            viderListeReponsesViews();

            boolean reponsePlacee = false;
            OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(getActivity());

            QueryBuilder<ClassificationFiche, Integer> qbClassificationFiche;
            List<ClassificationFiche> classificationFicheListeAleatoire = null;

            try {

                // On ne prend que les niveaux inférieurs (pour être certain d'en avoir au moins 3) ou égaux, en privilégiant les égaux
                qbClassificationFiche = ormLiteDBHelper.getClassificationFicheDao().queryBuilder();
                qbClassificationFiche.where().le("numOrdre", classificationFiche.getNumOrdre())
                        .and().ne("classification_id", classificationFiche.getClassification().getId());
                qbClassificationFiche.groupBy("classification_id");
                qbClassificationFiche.orderByRaw("numOrdre DESC, RANDOM()");
                Log.d(LOG_TAG, "createListeReponsesViews() - sql2 : " + qbClassificationFiche.prepareStatementString());
                classificationFicheListeAleatoire = ormLiteDBHelper.getClassificationFicheDao().query(qbClassificationFiche.prepare());

            } catch (SQLException error) {
                error.printStackTrace();
            }

            for (int i = 1; i <= jeuEncours.NBREPONSESPROPOSEES; i++) {

                // LA BONNE REPONSE
                if (reponsePlacee == false && ((jeuEncours.NBREPONSESPROPOSEES * random()) < 1 || i == jeuEncours.NBREPONSESPROPOSEES)) {
                    reponsePlacee = true;

                    Log.d(LOG_TAG, "createListeReponsesViews() - Fr : " + classification.getTermeFrancais());
                    Log.d(LOG_TAG, "createListeReponsesViews() - Sc : " + classification.getTermeScientifique());

                    String reponseLibelle = classification.getTermeFrancais();
                    if (reponseLibelle.equals(""))
                        reponseLibelle = classification.getTermeScientifique();

                    Log.d(LOG_TAG, "createListeReponsesViews() - lib : " + reponseLibelle);

                    llContainerLayout.addView(
                            jeuEncours.getReponseView(
                                    reponseSelectionneeCallback,
                                    fiche,
                                    classificationFiche.getClassification().getId(),
                                    reponseLibelle.replaceAll("\\{\\{[^\\}]*\\}\\}", ""),
                                    "Icone 1",
                                    classification.getDescriptif()));
                } else {

                    try {
                        QueryBuilder<Classification, Integer> qbClassification = ormLiteDBHelper.getClassificationDao().queryBuilder();
                        qbClassification.where().eq("_id", classificationFicheListeAleatoire.get(i).getClassification().getId());
                        Log.d(LOG_TAG, "createListeReponsesViews() - sql3 : " + qbClassification.prepareStatementString());

                        Classification classificationAleatoire = ormLiteDBHelper.getClassificationDao().queryForFirst(qbClassification.prepare());

                        String reponseLibelle = classificationAleatoire.getTermeFrancais();
                        if (reponseLibelle.equals(""))
                            reponseLibelle = classificationAleatoire.getTermeScientifique();

                        llContainerLayout.addView(
                                jeuEncours.getReponseView(
                                        reponseSelectionneeCallback,
                                        fiche,
                                        classificationAleatoire.getId(),
                                        reponseLibelle.replaceAll("\\{\\{[^\\}]*\\}\\}", ""),
                                        "Icone 2",
                                        classificationAleatoire.getDescriptif()));
                    } catch (SQLException error) {
                        error.printStackTrace();
                    }
                }

            }
        }

        Log.d(LOG_TAG, "createListeReponsesViews() - Fin");
    }

    /* Vidage de la Liste */
    public void viderListeReponsesViews() {
        llContainerLayout.removeAllViews();
    }

}
