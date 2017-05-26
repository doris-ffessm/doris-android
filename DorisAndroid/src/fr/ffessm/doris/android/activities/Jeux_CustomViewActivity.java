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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.fragments.JeuxQuestion_CustomViewFragment;
import fr.ffessm.doris.android.fragments.JeuxReponses_ClassListViewFragment;
import fr.ffessm.doris.android.tools.Jeu;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;

import static android.R.id.message;

public class Jeux_CustomViewActivity extends FragmentActivity
        implements JeuxReponses_ClassListViewFragment.JeuSelectionneListener,
                    JeuxReponses_ClassListViewFragment.NiveauSelectionneListener,
                    JeuxReponses_ClassListViewFragment.ReponseSelectionneeListener {

	private static final String LOG_TAG = Jeux_CustomViewActivity.class.getCanonicalName();

    private JeuxQuestion_CustomViewFragment questionFrag;
    private JeuxReponses_ClassListViewFragment reponsesFrag;

    Param_Outils paramOutils;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate() - Début");
        super.onCreate(savedInstanceState);

        getParamOutils().setParamBoolean(R.string.pref_key_jeux_actifs, true);

        Log.d(LOG_TAG, "onCreate() - 010");
        setContentView(R.layout.jeux_customview);

        // Si l'application avait déjà été lancée, on ne recrée pas (cas des rotations)
        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "onCreate() - 019");
            return;
        }

        questionFrag = (JeuxQuestion_CustomViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.jeux_activity_fragment_principal);
        reponsesFrag = (JeuxReponses_ClassListViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.jeux_activity_fragment_reponses);

        DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.ACCUEIL;

        Log.d(LOG_TAG, "onCreate() - Fin");
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "onResume() - Début");
        super.onResume();

        questionFrag = (JeuxQuestion_CustomViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.jeux_activity_fragment_principal);
        reponsesFrag = (JeuxReponses_ClassListViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.jeux_activity_fragment_reponses);

        Log.d(LOG_TAG, "onResume() - jeuStatut : " + DorisApplicationContext.getInstance().jeuStatut.name());

        if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.ACCUEIL) {
            reponsesFrag.createListeJeuxViews();
        }

        if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.CHOIX_NIVEAU) {
            reponsesFrag.createListeNiveauxViews(DorisApplicationContext.getInstance().jeuSelectionne);
        }

        if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.JEU) {
            onNiveauSelectionne(DorisApplicationContext.getInstance().jeuNiveauSelectionne);
        }


        Log.d(LOG_TAG, "onResume() - Fin");
    }

    @Override
    protected void onDestroy(){
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
                    ((Jeux_CustomViewActivity)this).finish();
                    return true;
                }

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.CHOIX_NIVEAU) {
                    reponsesFrag.createListeJeuxViews();
                    DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.ACCUEIL;
                    return true;
                }

                // Forcément en mode jeu => on revient au choix du Niveau
                reponsesFrag.createListeNiveauxViews(DorisApplicationContext.getInstance().jeuSelectionne);
                DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_NIVEAU;

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int inKeyCode, KeyEvent inEvent)
    {
        Log.d(LOG_TAG, "onKeyDown() - Début");
        Log.d(LOG_TAG, "onKeyDown() - inEvent : " + inEvent);

        switch(inKeyCode){
            case KeyEvent.KEYCODE_BACK :
                Log.d(LOG_TAG, "onKeyDown() - jeuStatut : " + DorisApplicationContext.getInstance().jeuStatut.name());

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.ACCUEIL) {
                    ((Jeux_CustomViewActivity)this).finish();
                    return true;
                }

                if (DorisApplicationContext.getInstance().jeuStatut == Jeu.Statut.CHOIX_NIVEAU) {
                    reponsesFrag.createListeJeuxViews();
                    DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.ACCUEIL;
                    return true;
                }

                // Forcément en mode jeu => on revient au choix du Niveau
                reponsesFrag.createListeNiveauxViews(DorisApplicationContext.getInstance().jeuSelectionne);
                DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_NIVEAU;

                return true;
        }

        return false;
    }

    public void onJeuSelectionne(Jeu.JeuRef jeuSelectionne) {
        Log.d(LOG_TAG, "onJeuSelectionne() - Début");

        Toast.makeText(this, "Jeu : "+jeuSelectionne.name(), Toast.LENGTH_LONG).show();

        DorisApplicationContext.getInstance().jeuSelectionne = jeuSelectionne;

        if (questionFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            String jeux_libelle[] = getResources().getStringArray(R.array.jeux_titre_array);
            questionFrag.setTitre(jeux_libelle[jeuSelectionne.ordinal()]);

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }

        if (reponsesFrag != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
            reponsesFrag.createListeNiveauxViews(jeuSelectionne);

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }

        DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.CHOIX_NIVEAU;
        Log.d(LOG_TAG, "onJeuSelectionne() - Fin");
    }

    public void onNiveauSelectionne(Jeu.Niveau niveau) {
        Log.d(LOG_TAG, "onNiveauSelectionne() - Début");

        Toast.makeText(this, "Niveau : "+niveau.name(), Toast.LENGTH_LONG).show();

        DorisApplicationContext.getInstance().jeuNiveauSelectionne = niveau;

        if (questionFrag != null) {
            // If article frag is available, we're in two-pane layout...

            questionFrag.setTitre(questionFrag.getTitre()+" ("+niveau.name()+")");

        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
        }


        // LE JEU ! ! !

        // Pour commencer Toujours le même jeu et crado
        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(this);
        Fiche fiche = null;
        try{
            QueryBuilder<Fiche, Integer> qb =  ormLiteDBHelper.getFicheDao().queryBuilder();
            qb.where().eq("etatFiche","4");
            qb.orderByRaw("RANDOM()");
            fiche = ormLiteDBHelper.getFicheDao().queryForFirst(qb.prepare());
        } catch (SQLException error) {
            error.printStackTrace();
        }

        Log.d(LOG_TAG, "onNiveauSelectionne() - fiche.getNomCommun : "+fiche.getNomCommun());
        PhotoFiche photoFiche = fiche.getPhotosFiche().iterator().next();
        Log.d(LOG_TAG, "onNiveauSelectionne() - fiche.getNomCommun : "+photoFiche.getCleURL());
        if (questionFrag != null) {
            questionFrag.setSousTitre(fiche.getNomCommun());
            questionFrag.setIcone(photoFiche.getCleURL(), ImageType.VIGNETTE);
        }
        if (reponsesFrag != null) {
            reponsesFrag.createListeReponsesViews(DorisApplicationContext.getInstance().jeuNiveauSelectionne, fiche);
        }

        DorisApplicationContext.getInstance().jeuStatut = Jeu.Statut.JEU;
        Log.d(LOG_TAG, "onNiveauSelectionne() - Fin");
    }

    public void onReponseSelectionnee(Fiche ficheQuestion, int idReponse) {
        Log.d(LOG_TAG, "onReponseSelectionnee() - Début");
        Log.d(LOG_TAG, "onReponseSelectionnee() - ficheQuestion : "+ficheQuestion.getNomCommun());
        Log.d(LOG_TAG, "onReponseSelectionnee() - idReponse : " + idReponse);

        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(this);
        ClassificationFiche classificationFiche = null;
        try{
            QueryBuilder<ClassificationFiche, Integer> qbClassificationFiche =  ormLiteDBHelper.getClassificationFicheDao().queryBuilder();
            qbClassificationFiche.where().eq("fiche_id", ficheQuestion.getId())
                                    .and().eq("classification_id", idReponse);

            Log.d(LOG_TAG, "createListeReponsesViews() - sql : "+qbClassificationFiche.prepareStatementString());

            classificationFiche = ormLiteDBHelper.getClassificationFicheDao().queryForFirst(qbClassificationFiche.prepare());
        } catch (SQLException error) {
            error.printStackTrace();
        }


        if (classificationFiche == null) {
            Toast.makeText(this, "Mauvaise Réponse", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Ok", Toast.LENGTH_LONG).show();

            Fiche fiche = null;
            try{
                QueryBuilder<Fiche, Integer> qb =  ormLiteDBHelper.getFicheDao().queryBuilder();
                qb.where().eq("etatFiche","4");
                qb.orderByRaw("RANDOM()");
                fiche = ormLiteDBHelper.getFicheDao().queryForFirst(qb.prepare());
            } catch (SQLException error) {
                error.printStackTrace();
            }

            Log.d(LOG_TAG, "onNiveauSelectionne() - fiche.getNomCommun : "+fiche.getNomCommun());
            PhotoFiche photoFiche = fiche.getPhotosFiche().iterator().next();
            Log.d(LOG_TAG, "onNiveauSelectionne() - fiche.getNomCommun : "+photoFiche.getCleURL());
            if (questionFrag != null) {
                questionFrag.setSousTitre(fiche.getNomCommun());
                questionFrag.setIcone(photoFiche.getCleURL(), ImageType.VIGNETTE);
            }
            if (reponsesFrag != null) {
                reponsesFrag.createListeReponsesViews(DorisApplicationContext.getInstance().jeuNiveauSelectionne, fiche);
            }

        }

        Log.d(LOG_TAG, "onReponseSelectionnee() - Fin");
    }

    private Param_Outils getParamOutils(){
        if(paramOutils == null) paramOutils = new Param_Outils(this);
        return paramOutils;
    }
}
