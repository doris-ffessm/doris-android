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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.tools.Jeu;

import static java.lang.Math.random;

public class JeuxReponses_ClassListViewFragment extends Fragment
{

	private static final String LOG_TAG = JeuxReponses_ClassListViewFragment.class.getCanonicalName();
    JeuSelectionneListener jeuSelectionneCallback;
    NiveauSelectionneListener niveauSelectionneCallback;
    ReponseSelectionneeListener reponseSelectionneeCallback;

    Jeu jeu;
    Jeu jeu1;
    Jeu jeu2;

    private LinearLayout llContainerLayout;
    private TextView tvTitreLabel;

    public interface JeuSelectionneListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onJeuSelectionne(Jeu.JeuType position);
    }

    public interface NiveauSelectionneListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onNiveauSelectionne(Jeu.JeuType position, Jeu.Niveau niveau);
    }

    public interface ReponseSelectionneeListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onReponseSelectionnee(Fiche ficheQuestion, int id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            jeuSelectionneCallback = (JeuSelectionneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement JeuSelectionneListener");
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView() - Début");
        View view = inflater.inflate(R.layout.jeux_listview_fragment, container, false);

        Log.d(LOG_TAG, "onCreateView() - Fin");
        return view;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart() - Début");
        super.onStart();

        Log.d(LOG_TAG, "onStart() - containerId : "+this.getId());
        Log.d(LOG_TAG, "onStart() - containerTag : "+this.getTag());
        Log.d(LOG_TAG, "onStart() - jeuEncours : "+ DorisApplicationContext.getInstance().jeuEncours);

        llContainerLayout =  (LinearLayout) getActivity().findViewById(R.id.jeu_reponses_liste_layout);
        tvTitreLabel =  (TextView) getActivity().findViewById(R.id.jeu_reponses_titre_label);

        if (DorisApplicationContext.getInstance().jeuEncours == Jeu.JeuType.ACCUEIL) {
            createListeJeuxViews();
        }


        Log.d(LOG_TAG, "onStart() - Fin");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState() - Début");
        super.onSaveInstanceState(outState);

        //outState.putInt(ARG_PETRI_ETAT, 0);

        Log.d(LOG_TAG, "onSaveInstanceState() - Fin");
    }

    /* Création de la liste des Jeux */
    public void createListeJeuxViews(){
        Log.d(LOG_TAG, "createListeJeuxViews() - Début");

        jeu1 = new Jeu((Context) getActivity(), getActivity(), Jeu.JeuType.JEU_1);
        llContainerLayout.addView(jeu1.getJeuView(jeuSelectionneCallback));

        jeu2 = new Jeu((Context) getActivity(), getActivity(), Jeu.JeuType.JEU_2);
        llContainerLayout.addView(jeu2.getJeuView(jeuSelectionneCallback));

        Log.d(LOG_TAG, "createListeJeuxViews() - Fin");
    }

    /* Création de la liste des Niveaux */
    public void createListeNiveauxViews(Jeu.JeuType jeuId){
        Log.d(LOG_TAG, "createListeNiveauxViews() - Début");

        if (jeuId == Jeu.JeuType.JEU_1) jeu = jeu1;
        if (jeuId == Jeu.JeuType.JEU_2) jeu = jeu2;

        viderListeReponsesViews();

        llContainerLayout.addView(jeu.getNiveauView(niveauSelectionneCallback, Jeu.Niveau.FACILE));
        llContainerLayout.addView(jeu.getNiveauView(niveauSelectionneCallback, Jeu.Niveau.INTERMEDIAIRE));
        llContainerLayout.addView(jeu.getNiveauView(niveauSelectionneCallback, Jeu.Niveau.DIFFICILE));

        Log.d(LOG_TAG, "createListeNiveauxViews() - Fin");
    }

    /* Création de la liste des Réponses */
    public void createListeReponsesViews(Jeu.Niveau niveau, Fiche fiche){
        Log.d(LOG_TAG, "createListeReponsesViews() - Début");
        Log.d(LOG_TAG, "createListeReponsesViews() - niveau : "+niveau.name());
        Log.d(LOG_TAG, "createListeReponsesViews() - fiche : "+fiche.getNomCommun());

        viderListeReponsesViews();

        // bornes[0] : mini ; bornes[1] : maxi
        int[] bornes = jeu.getBornesClassification(jeu.getId(), niveau);
        Log.d(LOG_TAG, "createListeReponsesViews() - bornes : "+bornes[0]+" ; "+bornes[1]);

        boolean reponsePlacee = false;
        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(getActivity());

        ClassificationFiche classificationFicheSelonNiveau = null;
        Classification classificationSelonNiveau = null;
        try{
            QueryBuilder<ClassificationFiche, Integer> qbClassificationFiche =  ormLiteDBHelper.getClassificationFicheDao().queryBuilder();
            qbClassificationFiche.where().eq("fiche_id", fiche.getId())
                    .and().ge("numOrdre",bornes[0])
                    .and().le("numOrdre",bornes[1]);
            qbClassificationFiche.orderByRaw("RANDOM()");
            Log.d(LOG_TAG, "createListeReponsesViews() - sql : "+qbClassificationFiche.prepareStatementString());

            classificationFicheSelonNiveau = ormLiteDBHelper.getClassificationFicheDao().queryForFirst(qbClassificationFiche.prepare());

            QueryBuilder<Classification, Integer> qbClassification =  ormLiteDBHelper.getClassificationDao().queryBuilder();
            qbClassification.where().eq("_id", classificationFicheSelonNiveau.getClassification().getId());

            classificationSelonNiveau = ormLiteDBHelper.getClassificationDao().queryForFirst(qbClassification.prepare());

            } catch (SQLException error) {
            error.printStackTrace();
        }
        Log.d(LOG_TAG, "createListeReponsesViews() - classificationFicheSelonNiveau : "+classificationFicheSelonNiveau.getNumOrdre());
        Log.d(LOG_TAG, "createListeReponsesViews() - classificationFicheSelonNiveau : "+classificationFicheSelonNiveau.getClassification().toString());
        Log.d(LOG_TAG, "createListeReponsesViews() - classificationFicheSelonNiveau : "+classificationFicheSelonNiveau.getClassification().getId());
        Log.d(LOG_TAG, "createListeReponsesViews() - classificationSelonNiveau : "+classificationSelonNiveau.getTermeFrancais());
        Log.d(LOG_TAG, "createListeReponsesViews() - classificationSelonNiveau : "+classificationSelonNiveau.getNiveau());

        tvTitreLabel.setText(classificationSelonNiveau.getNiveau());

        QueryBuilder<ClassificationFiche, Integer> qbClassificationFiche =  ormLiteDBHelper.getClassificationFicheDao().queryBuilder();
        List<ClassificationFiche> classificationFicheListeAleatoire = null;

        try{

            qbClassificationFiche.where().eq("numOrdre", classificationFicheSelonNiveau.getNumOrdre())
                    .and().ne("classification_id", classificationFicheSelonNiveau.getClassification().getId());
            qbClassificationFiche.groupBy("classification_id");
            qbClassificationFiche.orderByRaw("RANDOM()");
            Log.d(LOG_TAG, "createListeReponsesViews() - sql : "+qbClassificationFiche.prepareStatementString());

            classificationFicheListeAleatoire = ormLiteDBHelper.getClassificationFicheDao().query(qbClassificationFiche.prepare());
        } catch (SQLException error) {
            error.printStackTrace();
        }

        for(int i=1  ; i<=jeu.NBREPONSESPROPOSEES  ;i++){
            if ( reponsePlacee == false && ( (jeu.NBREPONSESPROPOSEES * random()) < 1 || i == jeu.NBREPONSESPROPOSEES )) {
                reponsePlacee = true;

                Log.d(LOG_TAG, "createListeReponsesViews() - Fr : "+classificationSelonNiveau.getTermeFrancais());
                Log.d(LOG_TAG, "createListeReponsesViews() - Sc : "+classificationSelonNiveau.getTermeScientifique());

                String reponseLibelle = classificationSelonNiveau.getTermeFrancais();
                if (reponseLibelle.equals("")) reponseLibelle = classificationSelonNiveau.getTermeScientifique();

                Log.d(LOG_TAG, "createListeReponsesViews() - lib : "+reponseLibelle);

                llContainerLayout.addView(
                        jeu.getReponseView(
                                reponseSelectionneeCallback,
                                fiche,
                                classificationFicheSelonNiveau.getClassification().getId(),
                                reponseLibelle,
                                "Icone 1"));
            } else {

                try{
                    QueryBuilder<Classification, Integer> qbClassification =  ormLiteDBHelper.getClassificationDao().queryBuilder();
                    qbClassification.where().eq("_id", classificationFicheListeAleatoire.get(i).getClassification().getId());
                    Log.d(LOG_TAG, "createListeReponsesViews() - sql : "+qbClassification.prepareStatementString());

                    Classification classificationAleatoire = ormLiteDBHelper.getClassificationDao().queryForFirst(qbClassification.prepare());

                    String reponseLibelle = classificationAleatoire.getTermeFrancais();
                    if (reponseLibelle.equals("")) reponseLibelle = classificationAleatoire.getTermeScientifique();

                    llContainerLayout.addView(
                            jeu.getReponseView(
                                    reponseSelectionneeCallback,
                                    fiche,
                                    classificationAleatoire.getId(),
                                    reponseLibelle,
                                    "Icone 2"));
                } catch (SQLException error) {
                    error.printStackTrace();
                }
            }

        }

        Log.d(LOG_TAG, "createListeReponsesViews() - Fin");
    }


    /* Création des Réponses */
    public void viderListeReponsesViews() {
        llContainerLayout.removeAllViews();
    }

}
