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
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


//Start of user code additional imports ImagePleinEcran_CustomViewActivity

import fr.ffessm.doris.android.BuildConfig;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.viewpager.widget.ViewPager;
import android.view.MotionEvent;
import android.view.View;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;

import java.util.ArrayList;

import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Textes_Outils;

//End of user code
public class ImagePleinEcran_CustomViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
//Start of user code additional implements ImagePleinEcran_CustomViewActivity
//End of user code
{

    //Start of user code constants ImagePleinEcran_CustomViewActivity
    private static final String LOG_TAG = ImagePleinEcran_CustomViewActivity.class.getSimpleName();

    protected ImagePleinEcran_Adapter adapter;
    protected ViewPager viewPager;
    int ficheId;
    private final Handler handler = new Handler();
    private Runnable showDescriptionRunner;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            android.app.ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    //End of user code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.imagepleinecran_customview);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Start of user code onCreate ImagePleinEcran_CustomViewActivity

        viewPager = (ViewPager) findViewById(R.id.imagepleinecran_pager);

        Intent i = getIntent();
        // récupération de la position dans du click dans la vue précédente
        int position = i.getIntExtra("position", 0);
        ACRA.getErrorReporter().putCustomData("position", "" + position);
        // récupération info qui permettra de retrouver la liste des images à afficher
        ficheId = i.getIntExtra("ficheId", 0);
        ACRA.getErrorReporter().putCustomData("ficheId", "" + ficheId);


        //TODO calcul de la liste d'images à partir de l'Id de la fiche  (pour plus tard, la même chose mais pour les photos principales d'un groupe ?)
        //ArrayList<String> imagePaths;

        RuntimeExceptionDao<Fiche, Integer> entriesDao = getHelper().getFicheDao();
        Fiche entry = entriesDao.queryForId(ficheId);
        entry.setContextDB(getHelper().getDorisDBHelper());
        //Collection<PhotoFiche> photosFiche = entry.getPhotosFiche();
        ArrayList<PhotoFiche> photosFicheArrayList = new ArrayList<PhotoFiche>(entry.getPhotosFiche());

        actionBar.setTitle(entry.getNomCommunNeverEmpty().replaceAll("\\{\\{[^\\}]*\\}\\}", ""));
        Textes_Outils textesOutils = new Textes_Outils(this);
        actionBar.setSubtitle(textesOutils.textToSpannableStringDoris(entry.getNomScientifique()));

        // Image adapter
        adapter = new ImagePleinEcran_Adapter(ImagePleinEcran_CustomViewActivity.this, photosFicheArrayList);

        viewPager.setAdapter(adapter);

        // affiche l'image sélectionnée en premier
        viewPager.setCurrentItem(position);
        triggerDescription(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int arg0) {
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageSelected(int currentPage) {
                //currentPage is the position that is currently displayed.
                triggerDescription(currentPage);
            }
        });

        // info de debug de Picasso
        Param_Outils paramOutils = new Param_Outils(this.getApplicationContext());
        if (paramOutils.getParamBoolean(R.string.pref_key_affichage_debug, false)) {
            Picasso.get().setLoggingEnabled(BuildConfig.DEBUG);
        }

        // gestion du full screen
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.imagepleinecran_pager);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_control_button).setOnTouchListener(mDelayHideTouchListener);

        //End of user code
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshScreenData();
        //Start of user code onResume ImagePleinEcran_CustomViewActivity
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(getString(R.string.pref_key_imagepleinecran_aff_zoomcontrol), false) != affZoomControl) {
            affZoomControl = prefs.getBoolean(getString(R.string.pref_key_imagepleinecran_aff_zoomcontrol), false);
            ViewPager view = (ViewPager) findViewById(R.id.imagepleinecran_pager);
            view.invalidate();
            // ne fonctionne pas :-(
        }
        //End of user code
    }
    //Start of user code additional code ImagePleinEcran_CustomViewActivity

    boolean affZoomControl = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "onDestroy()");
        Log.d(LOG_TAG, "onDestroy() - isFinishing() : " + isFinishing());


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void triggerDescription(int position) {
        if (showDescriptionRunner != null) {
            // annule les précédents post
            handler.removeCallbacks(showDescriptionRunner);
            showDescriptionRunner = null;
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            showDescriptionRunner = new Runnable() {
                @Override
                public void run() {
                    //shows toast after 2000ms
                    adapter.showDescription(position);
                }
            };

            handler.postDelayed(showDescriptionRunner, 2000);
        }
    }

    private void hide() {
        // Hide UI first
        android.app.ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //End of user code

    /**
     * refresh screen from data
     */
    public void refreshScreenData() {
        //Start of user code action when refreshing the screen ImagePleinEcran_CustomViewActivity
        //End of user code
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.imagepleinecran_customview_actions, menu);
        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu ImagePleinEcran_CustomViewActivity

        //End of user code
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu

        int itemId = item.getItemId();
        if (itemId == R.id.imagepleinecran_customview_action_preference) {
            startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
            return true;
            //Start of user code additional menu action ImagePleinEcran_CustomViewActivity

            //End of user code
            // Respond to the action bar's Up/Home button
        } else if (itemId == android.R.id.home) {
            Log.d(LOG_TAG, "onOptionsItemSelected() - home");

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
                Bundle b = new Bundle();
                b.putInt("ficheId", ficheId);
                upIntent.putExtras(b);
                NavUtils.navigateUpTo(this, upIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //  ------------ dealing with Up button
    @Override
    public Intent getSupportParentActivityIntent() {
        //Start of user code getSupportParentActivityIntent ImagePleinEcran_CustomViewActivity
        // navigates to the parent activity

        Intent toDetailView = new Intent(this, DetailsFiche_ElementViewActivity.class);
        Bundle b = new Bundle();
        b.putInt("ficheId", ficheId);
        toDetailView.putExtras(b);
        return toDetailView;
        //End of user code
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        //Start of user code onCreateSupportNavigateUpTaskStack ImagePleinEcran_CustomViewActivity
        super.onCreateSupportNavigateUpTaskStack(builder);
        //End of user code
    }

    // Start of user code protected ImagePleinEcran_CustomViewActivity_additional_operations


    // End of user code

}
