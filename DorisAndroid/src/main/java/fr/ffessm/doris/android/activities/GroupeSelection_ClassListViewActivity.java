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


import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.Groupes_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
// Start of user code protectedGroupeSelection_ClassListViewActivity_additionalimports
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.acra.ACRA;

import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
// End of user code

public class GroupeSelection_ClassListViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> implements OnItemClickListener {

    private static final String LOG_TAG = GroupeSelection_ClassListViewActivity.class.getSimpleName();

    //Start of user code constants GroupeSelection_ClassListViewActivity
    boolean depuisAccueil = false;
    final Context context = this;

    private SharedPreferences prefs;
    private String current_mode_affichage;
    //End of user code

    GroupeSelection_Adapter adapter;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.groupeselection_listview);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView list = findViewById(R.id.groupeselection_listview);
        list.setClickable(false);
        //Start of user code onCreate GroupeSelection_ClassListViewActivity adapter creation
        Log.d(LOG_TAG, "onCreate() - Début");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Param_Outils paramOutils = new Param_Outils(context);

        depuisAccueil = getIntent().getExtras().getBoolean("GroupeSelection_depuisAccueil", false);
        ACRA.getErrorReporter().putCustomData("depuisAccueil", "" + depuisAccueil);
        current_mode_affichage = paramOutils.getParamString(R.string.pref_key_current_mode_affichage, "liste");

        adapter = new GroupeSelection_Adapter(this, getHelper().getDorisDBHelper(), depuisAccueil);
        //End of user code
        // avoid opening the keyboard on view opening
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);

        //Start of user code onCreate additions GroupeSelection_ClassListViewActivity

        // affiche ou cache le filtre espèce actuel (+ son bouton de suppression)
        RelativeLayout currentFilterInfoLayout = findViewById(R.id.groupselection_listview_filtre_espece_courant_layout);

        int groupRootId = Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId();
        int filtreCourantId = prefs.getInt(this.getString(R.string.pref_key_filtre_groupe), groupRootId);
        Log.d(LOG_TAG, "onCreate() - filtreCourantId : " + filtreCourantId);

        if (filtreCourantId == groupRootId) {
            currentFilterInfoLayout.setVisibility(View.GONE);
        } else {
            Groupe groupeFiltreCourant = getHelper().getGroupeDao().queryForId(filtreCourantId);

            TextView filtreCourantTV = findViewById(R.id.groupselection_listview_filtre_espece_courant_textView);
            currentFilterInfoLayout.setVisibility(View.VISIBLE);
            filtreCourantTV.setText(getString(R.string.groupselection_listview_filtre_espece_courant_label) + groupeFiltreCourant.getNomGroupe());

        }

        RadioButton radio = findViewById(R.id.groupeselection_main_radioSelect);
        radio.setChecked(filtreCourantId == adapter.currentRootGroupe.getId());

        actionBar.setTitle(R.string.groupselection_listview_title);

        Log.d(LOG_TAG, "onCreate() - Fin");
        //End of user code
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Start of user code onResume additions GroupeSelection_ClassListViewActivity
        Log.d(LOG_TAG, "onResume() - Début");
        Log.d(LOG_TAG, "onResume() - Fin");
        //End of user code
    }

    public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
        //Start of user code onItemClick additions GroupeSelection_ClassListViewActivity
        Log.d(LOG_TAG, "onItemClick() - Début");
        Log.d(LOG_TAG, "onItemClick() - Groupe : " + position + " - " + index);

        GroupeSelection_Adapter groupeSelection_adapter = (GroupeSelection_Adapter) arg0.getAdapter();

        Groupe clickedGroupe = groupeSelection_adapter.getGroupeFromPosition(position);
        if (clickedGroupe.getContextDB() == null) {
            //Log.w(this.getClass().getSimpleName(),"workaround clickedGroupe.getContextDB() == null "+clickedGroupe.getId());
            clickedGroupe.setContextDB(getHelper().getDorisDBHelper());
        }

        if (clickedGroupe.getGroupesFils().size() > 0) {
            for (Groupe g : clickedGroupe.getGroupesFils()) {
                if (g.getContextDB() == null) {
                    //Log.w(this.getClass().getSimpleName(),"workaround Groupe.fils.getContextDB() == null "+g.getId());
                    g.setContextDB(getHelper().getDorisDBHelper());
                }
            }
            // update radioButton status
            int groupRootId = Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId();
            int filtreCourantId = prefs.getInt(this.getString(R.string.pref_key_filtre_groupe), groupRootId);
            RadioButton radio = findViewById(R.id.groupeselection_main_radioSelect);
            radio.setChecked(filtreCourantId == clickedGroupe.getId());

            groupeSelection_adapter.currentRootGroupe = clickedGroupe;
            groupeSelection_adapter.updateList();
            groupeSelection_adapter.notifyDataSetChanged();

        } else {
            showToast("Pas de sous-groupe. Cliquez sur le bouton radio pour sélectionner ce groupe.");
        }

        //End of user code
    }

    //Start of user code additional  GroupeSelection_ClassListViewActivity methods

    public void onRemoveCurrentFilterClick(View view) {
        showToast(R.string.groupselection_filtre_supprime);

        prefs.edit().putInt(this.getString(R.string.pref_key_filtre_groupe),
                Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId()).apply();
        finish();
    }
    //End of user code

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add options in the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.groupeselection_classlistview_actions, menu);
        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu GroupeSelection_ClassListViewActivity
        Log.d(LOG_TAG, "onCreateOptionsMenu() - Début");

        //End of user code
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu
        switch (item.getItemId()) {
            case R.id.groupeselection_classlistview_action_preference:
                startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
                return true;
            //Start of user code additional menu action GroupeSelection_ClassListViewActivity

            case R.id.groupeselection_classlistview_action_aide:
                AffichageMessageHTML aide = new AffichageMessageHTML(context, (Activity) context, getHelper());
                aide.affichageMessageHTML(context.getString(R.string.aide_label), " ", "file:///android_res/raw/aide.html");
                return true;

            //End of user code
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // Retour en Arrière et Si arrivée à la Racine retour à l'appli précédente
                if (retourGroupeSuperieur()) {
                    Intent upIntent = DorisApplicationContext.getInstance().getIntentPrecedent();

                    if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                        // This activity is NOT part of this app's task, so create a new task
                        // when navigating up, with a synthesized back stack.
                        TaskStackBuilder.create(this)
                                // Add all of this activity's parents to the back stack
                                .addNextIntentWithParentStack(upIntent)
                                // Navigate up to the closest parent
                                .startActivities();
                    } else {
                        // This activity is part of this app's task, so simply
                        // navigate up to the logical parent activity.
                        NavUtils.navigateUpTo(this, upIntent);
                    }
                }


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  ------------ dealing with Up button
    @Override
    public Intent getSupportParentActivityIntent() {
        //Start of user code getSupportParentActivityIntent GroupeSelection_ClassListViewActivity
        // navigates to the parent activity
        return new Intent(this, Accueil_CustomViewActivity.class);
        //End of user code
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        //Start of user code onCreateSupportNavigateUpTaskStack GroupeSelection_ClassListViewActivity
        super.onCreateSupportNavigateUpTaskStack(builder);
        //End of user code
    }

    // Start of user code protectedGroupeSelection_ClassListViewActivity

    public void onClickCurrentGroup(View view) {
        showToast("Filtre espèces : " + adapter.currentRootGroupe.getNomGroupe());
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
        ed.putInt(getString(R.string.pref_key_filtre_groupe), adapter.currentRootGroupe.getId());
        ed.apply();

        DorisApplicationContext.getInstance().setIntentPourRetour(getIntent());

        finish();
    }


    /* *********************************************************************
     * Capture des évènements sur le Clavier Physique de l'appareil
     ********************************************************************** */
    @Override
    public boolean onKeyDown(int inKeyCode, KeyEvent inEvent) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onKeyDown() - Début");
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onKeyDown() - inKeyCode : " + inKeyCode);
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onKeyDown() - inEvent : " + inEvent);

        switch (inKeyCode) {
            case KeyEvent.KEYCODE_BACK:

                if (retourGroupeSuperieur())
                    this.finish();

                return true;
        }

        return false;
    }


    public boolean retourGroupeSuperieur() {
        // Retour true si on est à la racine et donc que l'on doit fermé et false sinon

        Groupe groupeCourant = adapter.currentRootGroupe;
        if (groupeCourant.getContextDB() == null) {
            Log.w(this.getClass().getSimpleName(), "workaround clickedGroupe.getContextDB() == null " + groupeCourant.getId());
            groupeCourant.setContextDB(getHelper().getDorisDBHelper());
        }

        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onKeyDown() - groupeCourant.getGroupePere() : " + groupeCourant.getGroupePere());

        if (groupeCourant.getNomGroupe().equals("racine")) {

            return true;
        } else {
            adapter.currentRootGroupe = groupeCourant.getGroupePere();
            adapter.updateList();
            adapter.notifyDataSetChanged();

            return false;
        }
    }


    // End of user code


}
