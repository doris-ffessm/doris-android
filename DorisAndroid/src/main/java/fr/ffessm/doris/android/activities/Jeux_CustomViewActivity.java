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
package fr.ffessm.doris.android.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.fragments.JeuxQuestion_CustomViewFragment;
import fr.ffessm.doris.android.fragments.JeuxReponses_ClassListViewFragment;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.Groupes_Outils;
import fr.ffessm.doris.android.tools.Jeu;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;

public class Jeux_CustomViewActivity extends FragmentActivity
        implements JeuxReponses_ClassListViewFragment.JeuSelectionneListener,
        JeuxReponses_ClassListViewFragment.ZoneGeographiqueSelectionneeListener,
        JeuxReponses_ClassListViewFragment.NiveauSelectionneListener,
        JeuxReponses_ClassListViewFragment.ReponseSelectionneeListener,
        JeuxQuestion_CustomViewFragment.BoutonSuivantListener {

    private static final String LOG_TAG = Jeux_CustomViewActivity.class.getCanonicalName();

    private JeuxQuestion_CustomViewFragment questionFrag;
    private JeuxReponses_ClassListViewFragment reponsesFrag;

    Param_Outils paramOutils;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate() - Début");
        super.onCreate(savedInstanceState);

        getParamOutils().setParamBoolean(R.string.pref_key_jeux_actifs, true);

        Log.d(LOG_TAG, "onCreate() - 010");
        setContentView(R.layout.jeux_view);

        // Si l'application avait déjà été lancée, on ne recrée pas (cas des rotations)
        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "onCreate() - 019");
            return;
        }

        Log.d(LOG_TAG, "onCreate() - questionFrag");
        questionFrag = (JeuxQuestion_CustomViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.jeux_activity_fragment_question);
        Log.d(LOG_TAG, "onCreate() - reponsesFrag");
        reponsesFrag = (JeuxReponses_ClassListViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.jeux_activity_fragment_reponses);

        DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.ACCUEIL;


        Log.d(LOG_TAG, "onCreate() - TEST TEST TEST TEST TEST TEST TEST TEST TEST");
        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(this);

        List<Groupe> allGroupes = Groupes_Outils.getAllGroupes(ormLiteDBHelper.getDorisDBHelper());
        Log.d(LOG_TAG, "onCreate() - allGroupessize() : " + allGroupes.size());

        List<Groupe> groupesListe = Groupes_Outils.getAllGroupesEnfantsJusquAuNiveau(allGroupes, new ArrayList<Groupe>(), 3);
        Log.d(LOG_TAG, "onCreate() - groupesListe() : " + groupesListe.size());

        Groupe groupeJeu1 = groupesListe.get((int) (Math.random() * (groupesListe.size() - 1)));
        Log.d(LOG_TAG, "onCreate() - groupe 1 au hasard() : " + groupeJeu1.getNomGroupe());
        Groupe groupeJeu2 = groupesListe.get((int) (Math.random() * (groupesListe.size() - 1)));
        Log.d(LOG_TAG, "onCreate() - groupe 2 au hasard() : " + groupeJeu2.getNomGroupe());

        Fiches_Outils fichesOutils = new Fiches_Outils(this);
        List<Integer> fichesListeId1 = fichesOutils.getListeIdFichesFiltrees(this, ormLiteDBHelper.getDorisDBHelper(), -1, groupeJeu1.getId());
        Log.d(LOG_TAG, "onCreate() - fiche du groupe 1 au hasard() : " + fichesListeId1.size()
                + " - "
                + fichesOutils.getFicheForId(fichesListeId1.get((int) (Math.random() * (fichesListeId1.size() - 1)))).getNomCommun()
        );
        List<Integer> fichesListeId2 = fichesOutils.getListeIdFichesFiltrees(this, ormLiteDBHelper.getDorisDBHelper(), -1, groupeJeu2.getId());
        Log.d(LOG_TAG, "onCreate() - fiche du groupe 2 au hasard() : " + fichesListeId2.size()
                + " - "
                + fichesOutils.getFicheForId(fichesListeId2.get((int) (Math.random() * (fichesListeId2.size() - 1)))).getNomCommun()
        );

        Log.d(LOG_TAG, "onCreate() - TEST TEST TEST TEST TEST TEST TEST TEST TEST");


        Log.d(LOG_TAG, "onCreate() - Fin");
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume() - Début");
        super.onResume();

        questionFrag = (JeuxQuestion_CustomViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.jeux_activity_fragment_question);
        reponsesFrag = (JeuxReponses_ClassListViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.jeux_activity_fragment_reponses);

        Log.d(LOG_TAG, "onResume() - jeuStatut : " + DorisApplicationContext.getInstance().jeuStatut.name());

        if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.ACCUEIL) {
            questionFrag.createListeJeuxViews();
            reponsesFrag.createListeJeuxViews();
        }

        if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.CHOIX_NIVEAU) {
            reponsesFrag.createListeNiveauxViews();
        }

        if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.JEU) {
            onNiveauSelectionne(DorisApplicationContext.getInstance().jeuSelectionne, DorisApplicationContext.getInstance().jeuNiveauSelectionne, true);
        }


        Log.d(LOG_TAG, "onResume() - Fin");
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy() - Début");
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy() - Fin");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu() - Début");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.jeux_elementview_actions, menu);


        Log.d(LOG_TAG, "onCreateOptionsMenu() - Fin");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected() - Début");

        // behavior of option menu
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d(LOG_TAG, "onOptionsItemSelected() - home");

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.ACCUEIL) {

                    ((Jeux_CustomViewActivity) this).finish();
                    return true;
                }

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.CHOIX_ZONE_GEO) {
                    questionFrag.createListeJeuxViews();
                    reponsesFrag.createListeJeuxViews();

                    DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.ACCUEIL;
                    return true;
                }

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.CHOIX_NIVEAU) {
                    questionFrag.createListeZonesGeographiquesViews();
                    reponsesFrag.createListeZonesGeographiquesViews();

                    DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_ZONE_GEO;
                    return true;
                }

                // Forcément en mode jeu => on revient au choix du Niveau
                questionFrag.createListeNiveauxViews();
                reponsesFrag.createListeNiveauxViews();

                DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_NIVEAU;

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int inKeyCode, KeyEvent inEvent) {
        Log.d(LOG_TAG, "onKeyDown() - Début");
        Log.d(LOG_TAG, "onKeyDown() - inEvent : " + inEvent);

        switch (inKeyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.d(LOG_TAG, "onKeyDown() - jeuStatut : " + DorisApplicationContext.getInstance().jeuStatut.name());

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.ACCUEIL) {

                    ((Jeux_CustomViewActivity) this).finish();
                    return true;
                }

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.CHOIX_ZONE_GEO) {
                    questionFrag.createListeJeuxViews();
                    reponsesFrag.createListeJeuxViews();

                    DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.ACCUEIL;
                    return true;
                }

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.CHOIX_NIVEAU) {
                    questionFrag.createListeZonesGeographiquesViews();
                    reponsesFrag.createListeZonesGeographiquesViews();

                    DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_ZONE_GEO;
                    return true;
                }

                // Forcément en mode jeu => on revient au choix du Niveau
                questionFrag.createListeNiveauxViews();
                reponsesFrag.createListeNiveauxViews();

                DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_NIVEAU;

                return true;
        }

        return false;
    }

    public void onJeuSelectionne(Jeu.JeuRef jeuSelectionne) {
        Log.d(LOG_TAG, "onJeuSelectionne() - Début");

        Toast.makeText(this, "Jeu : " + jeuSelectionne.name(), Toast.LENGTH_LONG).show();

        DorisApplicationContext.getInstance().jeuSelectionne = jeuSelectionne;

        if (questionFrag != null) {
            // Si le Fragment Question Existe on le met à jour
            questionFrag.createListeZonesGeographiquesViews();

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }

        if (reponsesFrag != null) {
            // Si le Fragment Liste de Réponses Existe on le met à jour
            //reponsesFrag.createListeNiveauxViews(jeuSelectionne);
            reponsesFrag.createListeZonesGeographiquesViews();

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }

        DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_ZONE_GEO;
        Log.d(LOG_TAG, "onJeuSelectionne() - Fin");
    }

    public void onZoneGeographiqueSelectionnee(ZoneGeographique zone) {
        Log.d(LOG_TAG, "onZoneGeographiqueSelectionnee() - Début");

        Toast.makeText(this, "Zone : " + zone.getNom(), Toast.LENGTH_LONG).show();

        DorisApplicationContext.getInstance().jeuZoneGeographiqueSelectionnee = zone;


        if (questionFrag != null) {
            // Si le Fragment Question Existe on le met à jour
            questionFrag.createListeNiveauxViews();

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }

        if (reponsesFrag != null) {
            // Si le Fragment Liste de Réponses Existe on le met à jour
            //reponsesFrag.createListeNiveauxViews(jeuSelectionne);
            reponsesFrag.createListeNiveauxViews();

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }

        DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_NIVEAU;

        Log.d(LOG_TAG, "onZoneGeographiqueSelectionnee() - Fin");
    }

    public void onNiveauSelectionne(Jeu.JeuRef jeuSelectionne, Jeu.Niveau niveau, boolean onResume) {
        Log.d(LOG_TAG, "onNiveauSelectionne() - Début");

        Toast.makeText(this, "Niveau : " + niveau.name(), Toast.LENGTH_LONG).show();

        DorisApplicationContext.getInstance().jeuNiveauSelectionne = niveau;

        String jeux_libelle[] = getResources().getStringArray(R.array.jeux_titre_array);
        String jeux_niveau[] = getResources().getStringArray(R.array.jeux_niveau_array);


        if (questionFrag != null) {
            // If article frag is available, we're in two-pane layout...


        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }

        if (reponsesFrag != null) {
            // If article frag is available, we're in two-pane layout...

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }

        // LE JEU ! ! !

        Fiche QstFicheAleatoire = null;
        ClassificationFiche QstClassificationFicheAleatoire = null;
        Classification QstClassificationAleatoire = null;

        if (onResume == false) {

            DorisApplicationContext.getInstance().reponseOK = false;

            OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(this);
            QstFicheAleatoire = ficheAleatoire(ormLiteDBHelper, DorisApplicationContext.getInstance().jeuZoneGeographiqueSelectionnee);

            if (jeuSelectionne == Jeu.JeuRef.JEU_CLADE) {
                QstClassificationFicheAleatoire = classificationFicheAleatoire(ormLiteDBHelper, QstFicheAleatoire);
                QstClassificationAleatoire = classification(ormLiteDBHelper, QstClassificationFicheAleatoire);
            }

        } else {

            QstFicheAleatoire = DorisApplicationContext.getInstance().jeuFicheEnCours;

            if (jeuSelectionne == Jeu.JeuRef.JEU_CLADE) {
                QstClassificationFicheAleatoire = DorisApplicationContext.getInstance().jeuClassificationFicheEnCours;
                QstClassificationAleatoire = DorisApplicationContext.getInstance().jeuClassificationEnCours;
            }
        }

        if (questionFrag != null) {
            PhotoFiche photoFiche = QstFicheAleatoire.getPhotosFiche().iterator().next();
            Log.d(LOG_TAG, "onNiveauSelectionne() - fiche.getNomCommun : " + photoFiche.getCleURL());

            questionFrag.setTvTitreIconeLabel(jeux_niveau[niveau.ordinal()].substring(0, 1));

            if (jeuSelectionne == Jeu.JeuRef.JEU_CLADE) {
                questionFrag.createListeReponsesJeuCLADEViews(niveau, QstFicheAleatoire, QstClassificationAleatoire);
                questionFrag.setQuestionLibelle(QstFicheAleatoire.getNomCommun());
                questionFrag.setQuestionImage(photoFiche.getCleURL(), ImageType.VIGNETTE);
            }
        }
        if (reponsesFrag != null) {
            if (jeuSelectionne == Jeu.JeuRef.JEU_CLADE)
                reponsesFrag.createListeReponsesJeuCLADEViews(
                        DorisApplicationContext.getInstance().jeuZoneGeographiqueSelectionnee,
                        DorisApplicationContext.getInstance().jeuNiveauSelectionne,
                        QstFicheAleatoire,
                        QstClassificationFicheAleatoire,
                        QstClassificationAleatoire,
                        onResume);
        }

        DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.JEU;
        Log.d(LOG_TAG, "onNiveauSelectionne() - Fin");
    }

    public void onReponseSelectionnee(Fiche ficheQuestion, int idReponse, ImageView ivIcone) {
        Log.d(LOG_TAG, "onReponseSelectionnee() - Début");
        Log.d(LOG_TAG, "onReponseSelectionnee() - ficheQuestion : " + ficheQuestion.getNomCommun());
        Log.d(LOG_TAG, "onReponseSelectionnee() - idReponse : " + idReponse);

        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(this);
        ClassificationFiche classificationFiche = null;
        try {
            QueryBuilder<ClassificationFiche, Integer> qbClassificationFiche = ormLiteDBHelper.getClassificationFicheDao().queryBuilder();
            qbClassificationFiche.where().eq("fiche_id", ficheQuestion.getId())
                    .and().eq("classification_id", idReponse);

            Log.d(LOG_TAG, "createListeReponsesViews() - sql : " + qbClassificationFiche.prepareStatementString());

            classificationFiche = ormLiteDBHelper.getClassificationFicheDao().queryForFirst(qbClassificationFiche.prepare());
        } catch (SQLException error) {
            error.printStackTrace();
        }


        if (classificationFiche == null) {
            Toast.makeText(this, "Mauvaise Réponse", Toast.LENGTH_LONG).show();

            ivIcone.setImageResource(R.drawable.ic_action_jeux_ko);

        } else {
            Toast.makeText(this, "Ok", Toast.LENGTH_LONG).show();

            ivIcone.setImageResource(R.drawable.ic_action_jeux_ok);

            DorisApplicationContext.getInstance().reponseOK = true;

            questionFrag.activationBoutonSuivant(DorisApplicationContext.getInstance().jeuSelectionne, DorisApplicationContext.getInstance().jeuNiveauSelectionne);

        }

        Log.d(LOG_TAG, "onReponseSelectionnee() - Fin");
    }

    public void onBoutonSuivant() {
        Log.d(LOG_TAG, "onBoutonSuivant() - Début");

        DorisApplicationContext.getInstance().reponseOK = false;

        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(this);

        Fiche QstFicheAleatoire = ficheAleatoire(ormLiteDBHelper, DorisApplicationContext.getInstance().jeuZoneGeographiqueSelectionnee);
        ClassificationFiche QstClassificationFicheAleatoire = null;
        Classification QstClassificationAleatoire = null;

        PhotoFiche photoFiche = QstFicheAleatoire.getPhotosFiche().iterator().next();
        Log.d(LOG_TAG, "onBoutonSuivant() - fiche.getNomCommun : " + photoFiche.getCleURL());

        if (DorisApplicationContext.getInstance().jeuSelectionne == Jeu.JeuRef.JEU_CLADE) {
            QstClassificationFicheAleatoire = classificationFicheAleatoire(ormLiteDBHelper, QstFicheAleatoire);
            QstClassificationAleatoire = classification(ormLiteDBHelper, QstClassificationFicheAleatoire);
        }

        if (questionFrag != null) {
            if (DorisApplicationContext.getInstance().jeuSelectionne == Jeu.JeuRef.JEU_CLADE) {
                questionFrag.createListeReponsesJeuCLADEViews(DorisApplicationContext.getInstance().jeuNiveauSelectionne, QstFicheAleatoire, QstClassificationAleatoire);
                questionFrag.setQuestionLibelle(QstFicheAleatoire.getNomCommun());
                questionFrag.setQuestionImage(photoFiche.getCleURL(), ImageType.VIGNETTE);
            }
        }
        if (reponsesFrag != null) {
            if (DorisApplicationContext.getInstance().jeuSelectionne == Jeu.JeuRef.JEU_CLADE)
                reponsesFrag.createListeReponsesJeuCLADEViews(
                        DorisApplicationContext.getInstance().jeuZoneGeographiqueSelectionnee,
                        DorisApplicationContext.getInstance().jeuNiveauSelectionne,
                        QstFicheAleatoire,
                        QstClassificationFicheAleatoire,
                        QstClassificationAleatoire,
                        false);
        }

        Log.d(LOG_TAG, "onBoutonSuivant() - Fin");
    }

    public Fiche ficheAleatoire(OrmLiteDBHelper ormLiteDBHelper, ZoneGeographique zoneGeographique) {
        Log.d(LOG_TAG, "ficheAleatoire() - zoneGeographique : " + zoneGeographique.getZoneGeoKind().name());

        Fiche fiche = null;

        if (zoneGeographique.getZoneGeoKind() == Constants.ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES) {
            try {
                QueryBuilder<Fiche, Integer> qbFiche = ormLiteDBHelper.getFicheDao().queryBuilder();
                qbFiche.where().eq("etatFiche", "4");
                qbFiche.orderByRaw("RANDOM()");
                fiche = ormLiteDBHelper.getFicheDao().queryForFirst(qbFiche.prepare());

            } catch (SQLException error) {
                error.printStackTrace();
            }
        } else {
            try {
                QueryBuilder<Fiches_ZonesGeographiques, Integer> qbFichesZonGeo = ormLiteDBHelper.getFiches_ZonesGeographiquesDao().queryBuilder();
                qbFichesZonGeo.where().eq("ZoneGeographique_id", zoneGeographique.getId());
                qbFichesZonGeo.orderByRaw("RANDOM()");

                CloseableIterator<Fiches_ZonesGeographiques> listeFichesPourLaZone = ormLiteDBHelper.getFiches_ZonesGeographiquesDao().iterator(qbFichesZonGeo.prepare());

                boolean ficheTrouvee = false;
                while (ficheTrouvee == false && listeFichesPourLaZone.hasNext()) {
                    Fiche ficheTestee = listeFichesPourLaZone.next().getFiche();

                    QueryBuilder<Fiche, Integer> qbFiche = ormLiteDBHelper.getFicheDao().queryBuilder();
                    qbFiche.where().eq("_id", ficheTestee.getId());
                    ficheTestee = ormLiteDBHelper.getFicheDao().queryForFirst(qbFiche.prepare());

                    /*
                    Log.d(LOG_TAG, "ficheAleatoire() - ficheTestee.getId : "+ficheTestee.getId());
                    Log.d(LOG_TAG, "ficheAleatoire() - ficheTestee.getNomCommun : "+ficheTestee.getNomCommun());
                    Log.d(LOG_TAG, "ficheAleatoire() - ficheTestee.getEtatFiche : "+ficheTestee.getEtatFiche());
                    */
                    if (ficheTestee.getEtatFiche() == 4) {
                        fiche = ficheTestee;
                        ficheTrouvee = true;
                    }
                }

            } catch (SQLException error) {
                error.printStackTrace();
            }

        }

        Log.d(LOG_TAG, "ficheAleatoire() - fiche.getNomCommun : " + fiche.getNomCommun());

        DorisApplicationContext.getInstance().jeuFicheEnCours = fiche;

        return fiche;
    }

    public ClassificationFiche classificationFicheAleatoire(OrmLiteDBHelper ormLiteDBHelper, Fiche fiche) {
        // bornes[0] : mini ; bornes[1] : maxi
        int[] bornes = Jeu.getBornesClassification(DorisApplicationContext.getInstance().jeuSelectionne, DorisApplicationContext.getInstance().jeuNiveauSelectionne);
        Log.d(LOG_TAG, "createListeReponsesViews() - bornes : " + bornes[0] + " ; " + bornes[1]);

        boolean reponsePlacee = false;

        ClassificationFiche classificationFicheSelonNiveau = null;
        Classification classificationSelonNiveau = null;
        try {
            QueryBuilder<ClassificationFiche, Integer> qbClassificationFiche = ormLiteDBHelper.getClassificationFicheDao().queryBuilder();
            qbClassificationFiche.where().eq("fiche_id", fiche.getId())
                    .and().ge("numOrdre", bornes[0])
                    .and().le("numOrdre", bornes[1]);
            qbClassificationFiche.orderByRaw("RANDOM()");
            Log.d(LOG_TAG, "createListeReponsesViews() - sql : " + qbClassificationFiche.prepareStatementString());

            classificationFicheSelonNiveau = ormLiteDBHelper.getClassificationFicheDao().queryForFirst(qbClassificationFiche.prepare());

            // Parfois on ne peut pas respecter les bornes, dans ce cas on pioche n'importe quel niveau
            if (classificationFicheSelonNiveau == null) {
                qbClassificationFiche = ormLiteDBHelper.getClassificationFicheDao().queryBuilder();
                qbClassificationFiche.where().eq("fiche_id", fiche.getId())
                        .and().le("numOrdre", bornes[1]);
                qbClassificationFiche.orderByRaw("RANDOM()");
                Log.d(LOG_TAG, "createListeReponsesViews() - sql : " + qbClassificationFiche.prepareStatementString());

                classificationFicheSelonNiveau = ormLiteDBHelper.getClassificationFicheDao().queryForFirst(qbClassificationFiche.prepare());
            }

        } catch (SQLException error) {
            error.printStackTrace();
        }
        Log.d(LOG_TAG, "createListeReponsesViews() - classificationFicheSelonNiveau : " + classificationFicheSelonNiveau.getNumOrdre());
        Log.d(LOG_TAG, "createListeReponsesViews() - classificationFicheSelonNiveau : " + classificationFicheSelonNiveau.getClassification().toString());
        Log.d(LOG_TAG, "createListeReponsesViews() - classificationFicheSelonNiveau : " + classificationFicheSelonNiveau.getClassification().getId());

        DorisApplicationContext.getInstance().jeuClassificationFicheEnCours = classificationFicheSelonNiveau;

        return classificationFicheSelonNiveau;
    }

    public Classification classification(OrmLiteDBHelper ormLiteDBHelper, ClassificationFiche classificationFiche) {

        Classification classification = null;

        try {
            QueryBuilder<Classification, Integer> qbClassification = ormLiteDBHelper.getClassificationDao().queryBuilder();
            qbClassification.where().eq("_id", classificationFiche.getClassification().getId());

            classification = ormLiteDBHelper.getClassificationDao().queryForFirst(qbClassification.prepare());

        } catch (SQLException error) {
            error.printStackTrace();
        }
        Log.d(LOG_TAG, "classification() - classification : " + classification.getTermeFrancais());
        Log.d(LOG_TAG, "classification() - classification : " + classification.getNiveau());

        DorisApplicationContext.getInstance().jeuClassificationEnCours = classification;

        return classification;
    }

    private Param_Outils getParamOutils() {
        if (paramOutils == null) paramOutils = new Param_Outils(this);
        return paramOutils;
    }
}
