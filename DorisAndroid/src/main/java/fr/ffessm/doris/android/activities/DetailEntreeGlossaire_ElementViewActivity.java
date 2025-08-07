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


import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Reseau_Outils;
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

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;


// Start of user code protectedDetailEntreeGlossaire_ElementViewActivity_additional_import
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;

import org.acra.ACRA;

import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Textes_Outils;

import java.io.IOException;
// End of user code

public class DetailEntreeGlossaire_ElementViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
// Start of user code protectedDetailEntreeGlossaire_ElementViewActivity_additional_implements
// End of user code
{

    protected int definitionGlossaireId;

    private static final String LOG_TAG = DetailEntreeGlossaire_ElementViewActivity.class.getCanonicalName();

    // Start of user code protectedDetailEntreeGlossaire_ElementViewActivity_additional_attributes
    final Context context = this;
    protected Reseau_Outils reseauOutils;

// End of user code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.detailentreeglossaire_elementview);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailentreeglossaire_elementview_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        definitionGlossaireId = getIntent().getExtras().getInt("definitionGlossaireId");

        // Start of user code protectedDetailEntreeGlossaire_ElementViewActivity_onCreate

        ACRA.getErrorReporter().putCustomData("definitionGlossaireId", "" + definitionGlossaireId);
        reseauOutils = new Reseau_Outils(context);
        // End of user code
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreenData();
    }


    private void refreshScreenData() {
        // get our dao
        RuntimeExceptionDao<DefinitionGlossaire, Integer> entriesDao = getHelper().getDefinitionGlossaireDao();
        // Start of user code protectedDetailEntreeGlossaire_ElementViewActivity.refreshScreenData
        DefinitionGlossaire entry = entriesDao.queryForId(definitionGlossaireId);

        //entry.setContextDB(getHelper().getDorisDBHelper());
/*
    	Log.d(LOG_TAG, "refreshScreenData() - id : " + ((Integer)entry.getNumeroDoris()).toString());
    	Log.d(LOG_TAG, "refreshScreenData() - terme : " + entry.getTerme());
  */
        ((TextView) findViewById(R.id.detailentreeglossaire_elementview_numerodoris)).setText(((Integer) entry.getNumeroDoris()).toString());
        ((TextView) findViewById(R.id.detailentreeglossaire_elementview_terme)).setText(entry.getTerme());

        TextView definition = (TextView) findViewById(R.id.detailentreeglossaire_elementview_definition);
        Textes_Outils textesOutils = new Textes_Outils(context);

        CharSequence definitionTexte = entry.getDefinition();
        definition.setText(textesOutils.textToSpannableStringDoris(definitionTexte));
        definition.setMovementMethod(LinkMovementMethod.getInstance());

        // Lien vers la Définition sur le site
        String urlString = Constants.getDefinitionUrl("" + entry.getNumeroDoris());
        SpannableString richtext = new SpannableString(urlString);
        richtext.setSpan(new URLSpan(urlString), 0, urlString.length(), 0);
        TextView contenuUrl = (TextView) findViewById(R.id.detailentreeglossaire_elementview_liensite);
        contenuUrl.setText(richtext);
        contenuUrl.setMovementMethod(LinkMovementMethod.getInstance());

        Log.d(LOG_TAG, "refreshScreenData() - definition.getParent() : " + definition.getParent());
        if (definition.getParent() instanceof LinearLayout) {
            LinearLayout parentLayout = (LinearLayout) definition.getParent();

            // ne fait rien s'il y a déjà des images car cela suppose un reload (par exemple navigue sur le lien puis reviens sur la page
            if (parentLayout.getChildCount() <= 2) {


                Photos_Outils photosOutils = new Photos_Outils(context);
                for (String cleURLIllustration : entry.getCleURLIllustration().split(";")) {
                    if (cleURLIllustration != null && cleURLIllustration != "") {
                        String[] illustration = cleURLIllustration.split("\\|");

                        //Log.d(LOG_TAG, "refreshScreenData() - illustration : " + illustration.length);
                        //Log.d(LOG_TAG, "refreshScreenData() - illustration : " + illustration);
                        //Log.d(LOG_TAG, "refreshScreenData() - illustration[0] : " + illustration[0]);
                        if (illustration.length > 1)
                            Log.d(LOG_TAG, "refreshScreenData() - illustration[1] : " + illustration[1]);

                        definitionTexte = definitionTexte + "{{n/}}" + "{{E:" + illustration[0] + "/}}";
                        if (illustration.length > 1 && !illustration[1].equals("")) {
                            definitionTexte = definitionTexte + "{{n/}}" + illustration[1];
                        }
                        String nomPhotoLocal = Constants.PREFIX_IMGDSK_DEFINITION + illustration[0];
                        ImageView imageView = new ImageView(this);
                        parentLayout.addView(imageView);
                        if (photosOutils.isAvailableInFolderPhoto(nomPhotoLocal, Photos_Outils.ImageType.ILLUSTRATION_DEFINITION)) {
                            try {
                                String path = photosOutils.getPhotoFile(nomPhotoLocal, Photos_Outils.ImageType.ILLUSTRATION_DEFINITION).getAbsolutePath();
                                // chargement avec picasso
                                Picasso.get().load(path)
                                        .placeholder(R.drawable.app_glossaire_indisponible)
                                        //.centerInside()
                                        .into(imageView);
                            } catch (IOException e) {
                                Log.e(LOG_TAG, "refreshScreenData() - cannot find file : " + nomPhotoLocal, e);
                            }
                        } else {
                            // récupère image sur internet si possible
                            // utilise la version en ligne si possible
                            if (reseauOutils.isTelechargementsModeConnectePossible()) {
                                //Log.i(LOG_TAG, "refreshScreenData() - tentative téléchargement : " +Constants.IMAGE_BASE_URL +"/"+ illustration[0]);
                                Picasso.get().load(Constants.IMAGE_BASE_URL + "/" + illustration[0])
                                        .placeholder(R.drawable.app_glossaire_indisponible)
                                        //.centerInside()
                                        .into(imageView);
                            } else {
                                Picasso.get().load(Constants.IMAGE_BASE_URL + "/" + illustration[0])
                                        .placeholder(R.drawable.app_glossaire_indisponible)
                                        .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                                        //.centerInside()
                                        .into(imageView);
                            }
                        }

                        // ajout du texte de description de l'image
                        if (illustration.length > 1 && !illustration[1].equals("")) {
                            TextView imgDescription = new TextView(this);
                            imgDescription.setText(textesOutils.textToSpannableStringDoris(illustration[1]));
                            parentLayout.addView(imgDescription);
                        }
                    }
                }
            }
        }
        // End of user code

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add options in the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailentreeglossaire_elementview_actions, menu);
        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu DetailEntreeGlossaire_EditableElementViewActivity

        //End of user code
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu
        int itemId = item.getItemId();
        if (itemId == R.id.detailentreeglossaire_elementview_action_preference) {
            startActivity(new Intent(this, UserPreferences_Activity.class));
            return true;
            //Start of user code additional menu action DetailEntreeGlossaire_ElementViewActivity
        } else if (itemId == R.id.detailentreeglossaire_elementview_action_aide) {
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
        //Start of user code getSupportParentActivityIntent DetailEntreeGlossaire_ClassListViewActivity
        // navigates to the parent activity
        return new Intent(this, Glossaire_ClassListViewActivity.class);
        //End of user code
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        //Start of user code onCreateSupportNavigateUpTaskStack DetailEntreeGlossaire_ClassListViewActivity
        super.onCreateSupportNavigateUpTaskStack(builder);
        //End of user code
    }

    // Start of user code protectedDetailEntreeGlossaire_ElementViewActivity_additional_operations


    // End of user code

}
