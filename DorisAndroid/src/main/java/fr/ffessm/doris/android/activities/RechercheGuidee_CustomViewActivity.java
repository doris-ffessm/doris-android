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


import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.ActionBar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


//Start of user code additional imports RechercheGuidee_CustomViewActivity
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.BuildConfig;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

//End of user code
public class RechercheGuidee_CustomViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
//Start of user code additional implements RechercheGuidee_CustomViewActivity
//End of user code
{

    //Start of user code constants RechercheGuidee_CustomViewActivity
    private static final String LOG_TAG = RechercheGuidee_CustomViewActivity.class.getSimpleName();

    final Context context = this;

    //End of user code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.rechercheguidee_customview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rechercheguidee_customview_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Start of user code onCreate RechercheGuidee_CustomViewActivity
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() ");
        //End of user code
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreenData();
        //Start of user code onResume RechercheGuidee_CustomViewActivity
        //End of user code
    }

    //Start of user code additional code RechercheGuidee_CustomViewActivity
    public void onClickBtnSample(View view) {
        showToast("sample button pressed. \nPlease customize ;-)");
    }
    //End of user code

    /**
     * refresh screen from data
     */
    public void refreshScreenData() {
        //Start of user code action when refreshing the screen RechercheGuidee_CustomViewActivity
        //End of user code
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rechercheguidee_customview_actions, menu);
        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu RechercheGuidee_CustomViewActivity

        //End of user code
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu
        int itemId = item.getItemId();
        if (itemId == R.id.rechercheguidee_customview_action_preference) {
            startActivity(new Intent(this, UserPreferences_Activity.class));
            return true;
            //Start of user code additional menu action RechercheGuidee_CustomViewActivity
        } else if (itemId == R.id.rechercheguidee_customview_action_aide) {
            AffichageMessageHTML aide = new AffichageMessageHTML(context, (Activity) context, getHelper());
            aide.affichageMessageHTML(context.getString(R.string.aide_label), " ", "file:///android_res/raw/aide.html");
            return true;


            //End of user code
            // Respond to the action bar's Up/Home button
        } else if (itemId == android.R.id.home) {/* finish(); */
				/*
	        	TaskStackBuilder.create(this)
	                // Add all of this activity's parents to the back stack
	                .addNextIntentWithParentStack(getSupportParentActivityIntent())
	                // Navigate up to the closest parent
	                .startActivities();
	            */
            Intent upIntent = NavUtils.getParentActivityIntent(this);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //  ------------ dealing with Up button
    @Override
    public Intent getSupportParentActivityIntent() {
        //Start of user code getSupportParentActivityIntent RechercheGuidee_CustomViewActivity
        // navigates to the parent activity
        return new Intent(this, Accueil_CustomViewActivity.class);
        //End of user code
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        //Start of user code onCreateSupportNavigateUpTaskStack RechercheGuidee_CustomViewActivity
        super.onCreateSupportNavigateUpTaskStack(builder);
        //End of user code
    }
}
