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


import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;


// Start of user code protectedDetailsParticipant_ElementViewActivity_additional_import
import java.io.IOException;

import android.net.Uri;

import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.sitedoris.Constants;

import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView.BufferType;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;

import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.Textes_Outils;

// End of user code

public class DetailsParticipant_ElementViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
// Start of user code protectedDetailsParticipant_ElementViewActivity_additional_implements
// End of user code
{

    protected int participantId;

    private static final String LOG_TAG = DetailsParticipant_ElementViewActivity.class.getCanonicalName();

// Start of user code protectedDetailsParticipant_ElementViewActivity_additional_attributes

    final Context context = this;

    Photos_Outils photosOutils;
    Reseau_Outils reseauOutils = new Reseau_Outils(context);

    protected int participantNumeroDoris;
// End of user code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.detailsparticipant_elementview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailsparticipant_elementview_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        participantId = getIntent().getExtras().getInt("participantId");

        // Start of user code protectedDetailsParticipant_ElementViewActivity_onCreate
        ACRA.getErrorReporter().putCustomData("participantId", "" + participantId);
        Log.d(LOG_TAG, "onCreate() - participantId : " + participantId);
        // End of user code
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreenData();
    }


    private void refreshScreenData() {
        // get our dao
        RuntimeExceptionDao<Participant, Integer> entriesDao = getHelper().getParticipantDao();
        // Start of user code protectedDetailsParticipant_ElementViewActivity.refreshScreenData
        Participant entry = entriesDao.queryForId(participantId);
        entry.setContextDB(getHelper().getDorisDBHelper());

        ((TextView) findViewById(R.id.detailsparticipant_elementview_nom)).setText(entry.getNom());
        participantNumeroDoris = entry.getNumeroParticipant();
        //((TextView) findViewById(R.id.detailsparticipant_elementview_numeroparticipant)).setText(((Integer)participantNumeroDoris).toString());

        if (entry.getFonctions().contains(Constants.ParticipantKind.PHOTOGRAPHE.ordinal() + ";")) {
            ((ImageView) findViewById(R.id.detailsparticipant_picto_photographe)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detailsparticipant_texte_photographe)).setVisibility(View.VISIBLE);
        }
        if (entry.getFonctions().contains(Constants.ParticipantKind.REDACTEUR.ordinal() + ";")) {
            ((ImageView) findViewById(R.id.detailsparticipant_picto_redacteur)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detailsparticipant_texte_redacteur)).setVisibility(View.VISIBLE);
        }
        if (entry.getFonctions().contains(Constants.ParticipantKind.CORRECTEUR.ordinal() + ";")) {
            ((ImageView) findViewById(R.id.detailsparticipant_picto_correcteur)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detailsparticipant_texte_correcteur)).setVisibility(View.VISIBLE);
        }
        if (entry.getFonctions().contains(Constants.ParticipantKind.VERIFICATEUR.ordinal() + ";")) {
            ((ImageView) findViewById(R.id.detailsparticipant_picto_verificateur)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detailsparticipant_texte_verificateur)).setVisibility(View.VISIBLE);
        }

        TextView texte_description = (TextView) findViewById(R.id.detailsparticipant_texte_description);
        Textes_Outils textesOutils = new Textes_Outils(context);
        SpannableString richtext = textesOutils.textToSpannableStringDoris(entry.getDescription());
        texte_description.setText(richtext, BufferType.SPANNABLE);
        // make our ClickableSpans and URLSpans work 
        texte_description.setMovementMethod(LinkMovementMethod.getInstance());


        ImageView trombineView = (ImageView) findViewById(R.id.detailsparticipant_elementview_icon);
        if (!entry.getCleURLPhotoParticipant().isEmpty()) {

            if (getPhotosOutils().isAvailableInFolderPhoto(entry.getPhotoNom(), ImageType.PORTRAITS)) {
                try {
                    Picasso.get().load(getPhotosOutils().getPhotoFile(entry.getPhotoNom(), ImageType.PORTRAITS))
                            .fit()
                            .centerInside()
                            .into(trombineView);
                } catch (IOException e) {
                }
            } else {
                // pas préchargée en local pour l'instant, cherche sur internet
                String urlPhoto = Constants.PORTRAIT_BASE_URL + "/" + entry.getPhotoNom();

                com.squareup.picasso.Callback fallbackVignetteOffline = new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //Success image already loaded into the view
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(Constants.IMAGE_BASE_URL + "/" + entry.getPhotoNom().replaceAll(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.VIGNETTE_BASE_URL_SUFFIXE))
                                .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                                .placeholder(R.drawable.app_ic_participant)  // utilisation de l'image par defaut pour commencer
                                .fit()
                                .centerInside()
                                .into(trombineView,
                                        new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                //Success image already loaded into the view
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.d(LOG_TAG, "getView URL Petite Image : " +
                                                        entry.getPhotoNom().replaceAll(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.PETITE_BASE_URL_SUFFIXE));

                                                Picasso.get()
                                                        .load(Constants.IMAGE_BASE_URL + "/" + entry.getPhotoNom().replaceAll(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.PETITE_BASE_URL_SUFFIXE))
                                                        .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                                                        .placeholder(R.drawable.app_ic_participant)  // utilisation de l'image par defaut pour commencer
                                                        .fit()
                                                        .centerInside()
                                                        .error(R.drawable.app_ic_participant_pas_connecte)
                                                        .into(trombineView);
                                            }

                                        });
                    }
                };
                if (reseauOutils.isTelechargementsModeConnectePossible()) {
                    Log.d(LOG_TAG, "addFoldableView() - entry.getCleURLPhotoParticipant() : " + Constants.PORTRAIT_BASE_URL + "/" + entry.getPhotoNom());
                    Picasso.get()
                            .load(urlPhoto.replace(" ", "%20"))
                            .placeholder(R.drawable.app_ic_participant)  // utilisation de l'image par defaut pour commencer
                            .fit()
                            .centerInside()
                            .into(trombineView, fallbackVignetteOffline);
                } else {
                    Picasso.get()
                            .load(urlPhoto.replace(" ", "%20"))
                            .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                            .placeholder(R.drawable.app_ic_participant)  // utilisation de l'image par defaut pour commencer
                            .fit()
                            .centerInside()
                            .into(trombineView, fallbackVignetteOffline);
                }
            }
        }
        // End of user code

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add options in the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailsparticipant_elementview_actions, menu);
        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu DetailsParticipant_EditableElementViewActivity

        //End of user code
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu
        int itemId = item.getItemId();
        if (itemId == R.id.detailsparticipant_elementview_action_preference) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
            //Start of user code additional menu action DetailsParticipant_ElementViewActivity
        } else if (itemId == R.id.detailsparticipant_elementview_action_aide) {
            AffichageMessageHTML aide = new AffichageMessageHTML(this, (Activity) this, getHelper());
            aide.affichageMessageHTML(this.getString(R.string.aide_label), " ", "file:///android_res/raw/aide.html");
            return true;
            //End of user code
            // Respond to the action bar's Up/Home button
        } else if (itemId == android.R.id.home) {
            Intent upIntent = DorisApplicationContext.getInstance().getIntentPrecedent();
            Log.d(LOG_TAG, "onOptionsItemSelected() - upIntent : " + upIntent.getComponent().toString());

            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                Log.d(LOG_TAG, "onOptionsItemSelected() - shouldUpRecreateTask == true");
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                Log.d(LOG_TAG, "onOptionsItemSelected() - shouldUpRecreateTask == false");
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
        //Start of user code getSupportParentActivityIntent DetailsParticipant_ClassListViewActivity
        // navigates to the parent activity
        return new Intent(this, ListeParticipantAvecFiltre_ClassListViewActivity.class);
        //End of user code
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        //Start of user code onCreateSupportNavigateUpTaskStack DetailsParticipant_ClassListViewActivity
        super.onCreateSupportNavigateUpTaskStack(builder);
        //End of user code
    }

    // Start of user code protectedDetailsParticipant_ElementViewActivity_additional_operations

    public void onClickBioComplete(View view) {
        String url = Constants.getParticipantUrl(participantNumeroDoris);
        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    private Photos_Outils getPhotosOutils() {
        if (photosOutils == null) photosOutils = new Photos_Outils(this);
        return photosOutils;
    }

    // End of user code

}
