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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
import fr.ffessm.doris.android.activities.view.FoldableClickListener;
import fr.ffessm.doris.android.activities.view.FoldableClickListener.ImageButtonKind;
import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.IntervenantFiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.Textes_Outils;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;
// End of user code

public class DetailsFiche_ElementViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
// Start of user code protectedDetailsFiche_ElementViewActivity_additional_implements
        implements DataChangedListener
// End of user code
{

    private static final String LOG_TAG = DetailsFiche_ElementViewActivity.class.getCanonicalName();
    final Context context = this;

    // Start of user code protectedDetailsFiche_ElementViewActivity_additional_attributes
    final Activity activity = this;
    final Textes_Outils textesOutils = new Textes_Outils(context);
    final Param_Outils paramOutils = new Param_Outils(context);
    final Reseau_Outils reseauOutils = new Reseau_Outils(context);
    final Photos_Outils photosOutils = new Photos_Outils(context);
    protected int ficheId;
    protected int ficheNumero;

    boolean isOnCreate = true;
    List<FoldableClickListener> allFoldableDetails = new ArrayList<>();
    List<FoldableClickListener> allFoldableClassificationDescription = new ArrayList<>();
    Handler mHandler;
    LinearLayout photoGallery;
    Collection<String> insertedPhotosFiche = new ArrayList<>();
    boolean askedBgDownload = false;

    String current_mode_affichage;

// End of user code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.detailsfiche_elementview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailsfiche_elementview_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ficheId = getIntent().getExtras().getInt("ficheId");
        // Start of user code protectedDetailsFiche_ElementViewActivity_onCreate
        ACRA.getErrorReporter().putCustomData("ficheId", "" + ficheId);

        ficheNumero = getIntent().getExtras().getInt("ficheNumero");
        ACRA.getErrorReporter().putCustomData("ficheNumero", "" + ficheNumero);
        // Defines a Handler object that's attached to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {
            /*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {
                if (DetailsFiche_ElementViewActivity.this.isFinishing() || DetailsFiche_ElementViewActivity.this.isActivityDestroyed())
                    return;
                if (inputMessage.obj != null) {
                    showToast((String) inputMessage.obj);
                }
                refreshScreenData();
            }

        };

        // Liste Fiches, Arbre ou Liste Images
        current_mode_affichage = paramOutils.getParamString(R.string.pref_key_current_mode_affichage,
                getString(R.string.current_mode_affichage_default));

        // info de debug de Picasso
        if (paramOutils.getParamBoolean(R.string.pref_key_affichage_debug, false)) {
            Picasso.get().setLoggingEnabled(BuildConfig.DEBUG);
        }

        // End of user code
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreenData();
    }


    private void refreshScreenData() {
        // get our dao
        RuntimeExceptionDao<Fiche, Integer> entriesDao = getHelper().getFicheDao();
        // Start of user code protectedDetailsFiche_ElementViewActivity.refreshScreenData
        Fiche entry = null;
        if (ficheId != 0) entry = entriesDao.queryForId(ficheId);
        else if (ficheNumero != 0) entry = entriesDao.queryForEq("numeroFiche", ficheNumero).get(0);
        entry.setContextDB(getHelper().getDorisDBHelper());

        if (ficheId != 0) ficheNumero = entry.getNumeroFiche();
        else if (ficheNumero != 0) ficheId = entry.getId();

        getSupportActionBar().setTitle(entry.getNomCommunNeverEmpty().replaceAll("\\{\\{[^\\}]*\\}\\}", ""));
        getSupportActionBar().setSubtitle(textesOutils.textToSpannableStringDoris(entry.getNomScientifique()));

        ((TextView) findViewById(R.id.detailsfiche_elementview_nomscientifique)).setText(textesOutils.textToSpannableStringDoris(entry.getNomScientifique()));
        ((TextView) findViewById(R.id.detailsfiche_elementview_nomcommun)).setText(entry.getNomCommunNeverEmpty().replaceAll("\\{\\{[^\\}]*\\}\\}", ""));
        ((TextView) findViewById(R.id.detailsfiche_elementview_numerofiche)).setText("N° " + ((Integer) entry.getNumeroFiche()));
        ((TextView) findViewById(R.id.detailsfiche_elementview_etatfiche)).setText(((Integer) entry.getEtatFiche()).toString());

        int[] colors = {entry.getGroupe().getCouleurGroupe(), Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradientDrawable.setColors(colors);
        findViewById(R.id.detailsfiche_elementview_nomcommun).setBackground(gradientDrawable);
        findViewById(R.id.detailsfiche_elementview_nomscientifique).setBackground(gradientDrawable);

        TextView btnEtatFiche = findViewById(R.id.detailsfiche_elementview_etatfiche);
        //1-Fiche en cours de rédaction;2-Fiche en cours de rédaction;3-Fiche en cours de rédaction;4-Fiche Publiée;5-Fiche proposée
        switch (entry.getEtatFiche()) {
            case 1:
            case 2:
            case 3:
                btnEtatFiche.setVisibility(View.VISIBLE);
                btnEtatFiche.setText(" R ");
                btnEtatFiche.setOnClickListener(v -> showToast(R.string.ficheredaction_explications));
                break;
            case 5:
                btnEtatFiche.setVisibility(View.VISIBLE);
                btnEtatFiche.setText(" P ");
                btnEtatFiche.setOnClickListener(v -> showToast(R.string.ficheproposee_explications));
                break;
            case 4:
                btnEtatFiche.setVisibility(View.GONE);
                break;
            default:
                btnEtatFiche.setVisibility(View.VISIBLE);
                btnEtatFiche.setText(" " + entry.getEtatFiche() + " ");
        }

        ImageView picoEspeceReglementee = findViewById(R.id.detailsfiche_elementview_picto_reglementee);
        ImageView picoEspeceDanger = findViewById(R.id.detailsfiche_elementview_picto_dangereuse);
        if (entry.getPictogrammes().contains(Constants.PictoKind.PICTO_ESPECE_REGLEMENTEE.ordinal() + ";")) {
            picoEspeceReglementee.setVisibility(View.VISIBLE);
            picoEspeceReglementee.setOnClickListener(v -> showToast(R.string.picto_espece_reglementee_label));
        }
        if (entry.getPictogrammes().contains(Constants.PictoKind.PICTO_ESPECE_DANGEREUSE.ordinal() + ";")) {
            picoEspeceDanger.setVisibility(View.VISIBLE);
            picoEspeceDanger.setOnClickListener(v -> showToast(R.string.picto_espece_en_danger_label));
        }


        StringBuffer sbDebugText = new StringBuffer();

        Collection<PhotoFiche> photosFiche = entry.getPhotosFiche();
        if (photosFiche != null && isOnCreate) {
            //sbDebugText.append("\nnbPhoto="+photosFiche.size()+"\n");

            photoGallery = findViewById(R.id.detailsfiche_elementview_photogallery);
            int pos = 0;
            for (PhotoFiche photoFiche : photosFiche) {

                // Si la fiche n'est pas publiée, on n'affiche que la 1ère image, mais il existe une option dans les préférences Debug et Autres permettant de tout afficher
                if (pos == 0 || entry.getEtatFiche() == 4 || paramOutils.getParamBoolean(R.string.pref_key_affichage_tous_contenus, false)) {
                    View photoView = insertPhoto(photoFiche);
                    photoView.setOnClickListener(new OnImageClickListener(this.ficheId, pos, this));
                    photoView.setPadding(0, 0, 2, 0);
                    photoGallery.addView(photoView);
                    pos++;
                }

            }

        }

        if (isOnCreate) {
            // do only on first creation
            LinearLayout containerLayout = findViewById(R.id.detailsfiche_sections_layout);

            // section Autres Dénominations
            if (entry.getAutresDenominations() != null) {
                Collection<AutreDenomination> autresDenominations = entry.getAutresDenominations();
                if (autresDenominations.size() > 0) {
                    StringBuilder sbAutresDenominations = new StringBuilder();
                    int i = 1;
                    for (AutreDenomination autreDenomination : autresDenominations) {
                        sbAutresDenominations.append(autreDenomination.getDenomination());
                        if (autresDenominations.size() > 1 && i < autresDenominations.size()) {
                            sbAutresDenominations.append("\n");
                        }
                        i++;
                    }
                    addFoldableTextView(containerLayout, getString(R.string.detailsfiche_elementview_autresdenominations_label), sbAutresDenominations.toString());

                }
            }
            // Section Groupe Phylogénique
            if (entry.getGroupe() != null && entry.getGroupe().getNumeroGroupe() != 0) {
                addFoldableGroupeView(containerLayout, getString(R.string.detailsfiche_elementview_groupes_label), entry.getGroupe());
            }

            // 1ère partie des sections issues de la fiche
            // Si la fiche n'est pas publiée, on masque par défaut, mais il existe une option dans les préférences Debug et Autres permettant de tout afficher
            if (entry.getContenu() != null && (entry.getEtatFiche() == 4 || paramOutils.getParamBoolean(R.string.pref_key_affichage_tous_contenus, false))) {
                for (SectionFiche sectionFiche : entry.getContenu()) {
                    if (sectionFiche.getNumOrdre() < 300) {
                        //Log.d(LOG_TAG, "refreshScreenData() - titre : "+sectionFiche.getTitre());
                        //Log.d(LOG_TAG, "refreshScreenData() - texte : "+sectionFiche.getTexte());

                        addFoldableTextView(containerLayout, sectionFiche.getTitre(), sectionFiche.getTexte());
                    }
                }
            } // Fin 1ère partie des Sections de la Fiche

            // Zones Géographiques DORIS
            List<ZoneGeographique> zonesGeographiques = entry.getZonesGeographiques();
            if (zonesGeographiques != null) {
                StringBuilder sbZonesGeographiques = new StringBuilder();
                int i = 1;
                for (ZoneGeographique zoneGeographique : zonesGeographiques) {
                    sbZonesGeographiques.append(zoneGeographique.getNom());
                    if (zonesGeographiques.size() > 1 && i < zonesGeographiques.size()) {
                        sbZonesGeographiques.append("\n");
                    }
                    i++;
                }
                if (sbZonesGeographiques.toString().length() != 0) {
                    addFoldableTextView(containerLayout, getString(R.string.detailsfiche_elementview_zonesgeo_label), sbZonesGeographiques.toString());
                } else {
                    addFoldableTextView(containerLayout, getString(R.string.detailsfiche_elementview_zonesgeo_label), getString(R.string.detailsfiche_elementview_zonesgeo_aucune_label));
                }
            }

            // section "Crédits"
            StringBuilder sbCreditText = new StringBuilder();
            final String urlString = Constants.getFicheFromIdUrl(entry.getNumeroFiche());
            sbCreditText.append("{{A:").append(urlString).append("}}");
            sbCreditText.append(urlString);
            sbCreditText.append("{{/A}}");

            sbCreditText.append("\n").append(getString(R.string.detailsfiche_elementview_datecreation_label));
            sbCreditText.append(entry.getDateCreation());

            if (!entry.getDateModification().isEmpty()) {
                sbCreditText.append("\n").append(getString(R.string.detailsfiche_elementview_datemodification_label));
                sbCreditText.append(entry.getDateModification());
            }

            for (IntervenantFiche intervenant : entry.getIntervenants()) {
                intervenant.setContextDB(getHelper().getDorisDBHelper());
                //sbCreditText.append("\n"+intervenant.getId());
                sbCreditText.append("\n").append(Constants.getTitreParticipant(intervenant.getRoleIntervenant())).append(" : ");

                Participant participant = intervenant.getParticipant();
                participant.setContextDB(getHelper().getDorisDBHelper());

                sbCreditText.append("{{P:").append(participant.getId()).append("}}");
                sbCreditText.append(participant.getNom());
                sbCreditText.append("{{/P}}");

            }

            SpannableString richtext = textesOutils.textToSpannableStringDoris(sbCreditText.toString());
            //richtext.setSpan(new RelativeSizeSpan(2f), 0, urlString.length(), 0);
            //richtext.setSpan(new URLSpan(urlString), 0, urlString.length(), 0);

            addFoldableTextView(containerLayout, getString(R.string.detailsfiche_elementview_credit_label), richtext);


            // 2ème partie des sections issues de la fiche
            // Si la fiche n'est pas publiée, on masque par défaut, mais il existe une option dans les préférences Debug et Autres permettant de tout afficher
            if (entry.getContenu() != null && (entry.getEtatFiche() == 4 || paramOutils.getParamBoolean(R.string.pref_key_affichage_tous_contenus, false))) {

                for (SectionFiche sectionFiche : entry.getContenu()) {
                    if (sectionFiche.getNumOrdre() > 400) {
                        //Log.d(LOG_TAG, "refreshScreenData() - titre : "+sectionFiche.getTitre());
                        //Log.d(LOG_TAG, "refreshScreenData() - texte : "+sectionFiche.getTexte());

                        addFoldableTextView(containerLayout, sectionFiche.getTitre(), sectionFiche.getTexte());
                    }
                }
            } // Fin de la 2ème Partie des Sections de la Fiche


            // Arbre Phylogénique
            // Si la fiche n'est pas publiée, on masque par défaut, mais il existe une option dans les préférences Debug et Autres permettant de tout afficher
            if (entry.getClassification() != null && (entry.getEtatFiche() == 4 || paramOutils.getParamBoolean(R.string.pref_key_affichage_tous_contenus, false))) {
                Collection<ClassificationFiche> classificationFicheCollect = entry.getClassification();

                if (classificationFicheCollect.size() != 0) {
                    addFoldableArbrePhylogenetiqueView(containerLayout, classificationFicheCollect);
                }
            }

            isOnCreate = false;
        }


        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // Debug
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        if (paramOutils.getParamBoolean(R.string.pref_key_affichage_debug, false)) {

            findViewById(R.id.detailsfiche_elementview_debug_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.detailsfiche_elementview_debug_text)).setText(sbDebugText.toString());

        } else {
            findViewById(R.id.detailsfiche_elementview_debug_text).setVisibility(View.GONE);
        }
        // End of user code

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add options in the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailsfiche_elementview_actions, menu);
        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu DetailsFiche_EditableElementViewActivity


        //End of user code
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu
        int itemId = item.getItemId();
        if (itemId == R.id.detailsfiche_elementview_action_preference) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
            //Start of user code additional menu action DetailsFiche_ElementViewActivity
        } else if (itemId == R.id.detailsfiche_elementview_action_fold_all_sections) {
            foldAllDetails();
            return true;
        } else if (itemId == R.id.detailsfiche_elementview_action_unfold_all_sections) {
            unfoldAllDetails();
            return true;
        } else if (itemId == R.id.detailsfiche_elementview_action_glossaire) {
            DorisApplicationContext.getInstance().setIntentPourRetour(getIntent());
            Intent toDefinitionlView = new Intent(context, Glossaire_ClassListViewActivity.class);
            context.startActivity(toDefinitionlView);
            return true;
        } else if (itemId == R.id.detailsfiche_elementview_action_aide) {
            AffichageMessageHTML aide = new AffichageMessageHTML(this, this, getHelper());
            aide.affichageMessageHTML(this.getString(R.string.aide_label), " ", "file:///android_res/raw/aide.html");
            return true;
            //End of user code
            // Respond to the action bar's Up/Home button
        } else if (itemId == android.R.id.home) {
            Intent upIntent = DorisApplicationContext.getInstance().getIntentPrecedent();
            if (upIntent == null) {
                // workaround bug https://gitlab.inria.fr/doris/doris-android/issues/134
                Intent i = new Intent(this, Accueil_CustomViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                DorisApplicationContext.getInstance().resetIntentPrecedent(i);
                upIntent = i;
            }
            Log.d(LOG_TAG, "onOptionsItemSelected() - upIntent : " + Objects.requireNonNull(upIntent.getComponent()).toString());

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
        //Start of user code getSupportParentActivityIntent DetailsFiche_ClassListViewActivity
        Log.d(LOG_TAG, "getSupportParentActivityIntent()");
        // navigates to the parent activity
        return new Intent(this, ListeFicheAvecFiltre_ClassListViewActivity.class);
        //End of user code
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        //Start of user code onCreateSupportNavigateUpTaskStack DetailsFiche_ClassListViewActivity
        Log.d(LOG_TAG, "onCreateSupportNavigateUpTaskStack()");
        super.onCreateSupportNavigateUpTaskStack(builder);
        //End of user code
    }

    // Start of user code protectedDetailsFiche_ElementViewActivity_additional_operations
    // pour le menu sur click long
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //menu.setHeaderTitle("Context Menu");  
        menu.add(Menu.NONE, R.id.detailsfiche_elementview_action_fold_all_sections, 1, R.string.fold_all_sections_menu_option).setIcon(R.drawable.app_expander_ic_minimized);
        menu.add(Menu.NONE, R.id.detailsfiche_elementview_action_unfold_all_sections, 2, R.string.unfold_all_sections_menu_option).setIcon(R.drawable.app_expander_ic_maximized);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.detailsfiche_elementview_action_fold_all_sections) {
            foldAllDetails();
        } else if (itemId == R.id.detailsfiche_elementview_action_unfold_all_sections) {
            unfoldAllDetails();
        }
        return false;
    }

    // -------------- handler (for indexBar)

    protected void addFoldableTextView(LinearLayout containerLayout, String titre, CharSequence texte) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.detailsfiche_elementview_foldablesection, null);

        // Titre de la Section
        TextView titreText = convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_titre);
        titreText.setText(titre);

        // Texte avec mise en forme avancée, par exemple si le texte contient {{999}} alors on remplace par (Fiche)
        // et on met un lien vers la fiche sur (Fiche)
        TextView contenuText = convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldabletext);
        SpannableString richtext = textesOutils.textToSpannableStringDoris(texte);
        Log.d(LOG_TAG, "addFoldableView() - titre : " + titre);
        Log.d(LOG_TAG, "addFoldableView() - text : " + texte);

        contenuText.setText(richtext, BufferType.SPANNABLE);
        // make our ClickableSpans and URLSpans work 
        contenuText.setMovementMethod(LinkMovementMethod.getInstance());

        // button allowing to fold/unfold the section
        ImageButton foldButton = convertView.findViewById(R.id.detailsfiche_elementview_fold_unfold_section_imageButton);

        FoldableClickListener foldable = new FoldableClickListener(this, contenuText, foldButton, ImageButtonKind.DETAILS_FICHE);
        allFoldableDetails.add(foldable);
        foldButton.setOnClickListener(foldable);

        LinearLayout titreLinearLayout = convertView.findViewById(R.id.detailsfiche_elementview_fold_unflod_section_linearlayout);
        titreLinearLayout.setOnClickListener(foldable);
        // enregistre pour réagir au click long
        registerForContextMenu(foldButton);
        registerForContextMenu(titreLinearLayout);

        // Gestion de l'affichage à l'ouverture de la fiche
        if (!paramOutils.getParamBoolean(R.string.pref_key_fiche_aff_details_pardefaut, false)) {
            contenuText.setVisibility(View.GONE); // par défaut invisible
        } else {
            contenuText.setVisibility(View.VISIBLE);
            foldButton.setImageResource(R.drawable.ic_menu_up_outline);
        }

        containerLayout.addView(convertView);
    }

    protected void foldAllDetails() {
        for (FoldableClickListener foldable : allFoldableDetails) {
            foldable.fold();
        }
    }

    protected void unfoldAllDetails() {
        for (FoldableClickListener foldable : allFoldableDetails) {
            foldable.unfold();
        }
    }

    protected void addFoldableGroupeView(LinearLayout containerLayout, String titre, final Groupe groupe) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.detailsfiche_elementview_foldablesection_2icones, null);

        // Titre de la Section
        TextView titreText = convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_titre);
        titreText.setText(titre);

        // Icônes de la Section
        RelativeLayout sectionIcones = convertView.findViewById(R.id.detailsfiche_elementview_fold_unflod_section_icones);

        final Groupe groupePere = getHelper().getGroupeDao().queryForId(groupe.getGroupePere().getId());

        ImageButton icone1 = convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldableicone1);
        TextView texte1 = convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldabletexte1);
        ImageButton icone2 = convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldableicone2);
        TextView texte2 = convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldabletexte2);

        if (groupe.getNumeroSousGroupe() == 0) {
            int identifierIcone1Groupe = context.getResources().getIdentifier(groupe.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw", context.getPackageName());
            Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIcone1Groupe));
            icone1.setImageBitmap(bitmap);
            icone1.setBackgroundResource(ThemeUtil.attrToResId(((DetailsFiche_ElementViewActivity) context), R.attr.ic_action_background));
            texte1.setText(groupe.getNomGroupe());
            icone1.setOnClickListener(v -> {
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
                ed.putInt(context.getString(R.string.pref_key_filtre_groupe), groupe.getId());
                ed.apply();

                if (current_mode_affichage.equals("photos")) {
                    startActivity(new Intent(context, ListeImageFicheAvecFiltre_ClassListViewActivity.class));
                } else {
                    startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
                }

            });
            icone2.setVisibility(View.GONE);
            texte2.setVisibility(View.GONE);
        } else {
            int identifierIcone1Groupe = context.getResources().getIdentifier(groupePere.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw", context.getPackageName());
            Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIcone1Groupe));
            icone1.setImageBitmap(bitmap);
            icone1.setBackgroundResource(ThemeUtil.attrToResId(((DetailsFiche_ElementViewActivity) context), R.attr.ic_action_background));
            texte1.setText(groupePere.getNomGroupe());
            icone1.setOnClickListener(v -> {
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
                ed.putInt(context.getString(R.string.pref_key_filtre_groupe), groupePere.getId());
                ed.apply();

                if (current_mode_affichage.equals("photos")) {
                    startActivity(new Intent(context, ListeImageFicheAvecFiltre_ClassListViewActivity.class));
                } else {
                    startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
                }

            });

            int identifierIcone2Groupe = context.getResources().getIdentifier(groupe.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw", context.getPackageName());
            bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIcone2Groupe));
            icone2.setImageBitmap(bitmap);
            icone2.setBackgroundResource(ThemeUtil.attrToResId(((DetailsFiche_ElementViewActivity) context), R.attr.ic_action_background));
            texte2.setText(groupe.getNomGroupe());
            icone2.setOnClickListener(v -> {
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
                ed.putInt(context.getString(R.string.pref_key_filtre_groupe), groupe.getId());
                ed.apply();

                if (current_mode_affichage.equals("photos")) {
                    startActivity(new Intent(context, ListeImageFicheAvecFiltre_ClassListViewActivity.class));
                } else {
                    startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
                }

            });
        }

        // Bouton d'Affichage et de Masque de la section
        ImageButton foldButton = convertView.findViewById(R.id.detailsfiche_elementview_fold_unfold_section_imageButton);

        FoldableClickListener foldable = new FoldableClickListener(this, sectionIcones, foldButton, ImageButtonKind.DETAILS_FICHE);
        allFoldableDetails.add(foldable);
        foldButton.setOnClickListener(foldable);

        LinearLayout titreLinearLayout = convertView.findViewById(R.id.detailsfiche_elementview_fold_unflod_section_linearlayout);
        titreLinearLayout.setOnClickListener(foldable);
        // enregistre pour réagir au click long
        registerForContextMenu(foldButton);
        registerForContextMenu(titreLinearLayout);


        // Affichage défaut à l'ouverture de la fiche
        if (!paramOutils.getParamBoolean(R.string.pref_key_fiche_aff_details_pardefaut, false)) {
            sectionIcones.setVisibility(View.GONE); // par défaut invisible
        } else {
            sectionIcones.setVisibility(View.VISIBLE);
            foldButton.setImageResource(R.drawable.ic_menu_up_outline);
        }

        containerLayout.addView(convertView);
    }

    private void onClickAllDescriptionClassification() {
        for (FoldableClickListener foldable : allFoldableClassificationDescription) {
            foldable.onClick();
        }
    }

    protected void addFoldableArbrePhylogenetiqueView(LinearLayout containerLayout, Collection<ClassificationFiche> classificationFicheCollect) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.details_tableau_phylogenetique_foldablesection, null);

        // Affichage de l'arbre
        LinearLayout sectionArbre = convertView.findViewById(R.id.details_tableau_phylogenetique_contenu_linearlayout);

        for (ClassificationFiche classificationFiche : classificationFicheCollect) {

            classificationFiche.setContextDB(getHelper().getDorisDBHelper());

            Classification classification = classificationFiche.getClassification();
            Log.d(LOG_TAG, "addFoldableArbrePhylogenetiqueView() - classification : " + classification.getNiveau() + " : " + classification.getTermeScientifique());
            classification.setContextDB(getHelper().getDorisDBHelper());

            View convertArbreView = inflater.inflate(R.layout.details_tableau_phylogenetique_detail, null);

            TextView detailsfiche_arbreview_titre = convertArbreView.findViewById(R.id.detailsfiche_arbreview_niveau);
            SpannableString richtext = textesOutils.textToSpannableStringDoris(classification.getNiveau());
            detailsfiche_arbreview_titre.setText(richtext, BufferType.SPANNABLE);
            detailsfiche_arbreview_titre.setMovementMethod(LinkMovementMethod.getInstance());

            TextView detailsfiche_arbreview_scientifique = convertArbreView.findViewById(R.id.detailsfiche_arbreview_scientifique);
            richtext = textesOutils.textToSpannableStringDoris(classification.getTermeScientifique());
            detailsfiche_arbreview_scientifique.setText(richtext, BufferType.SPANNABLE);
            detailsfiche_arbreview_scientifique.setMovementMethod(LinkMovementMethod.getInstance());

            TextView detailsfiche_arbreview_francais = convertArbreView.findViewById(R.id.detailsfiche_arbreview_francais);
            if (!classification.getTermeFrancais().isEmpty()) {
                richtext = textesOutils.textToSpannableStringDoris(classification.getTermeFrancais());
                detailsfiche_arbreview_francais.setText(richtext, BufferType.SPANNABLE);
                detailsfiche_arbreview_francais.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                detailsfiche_arbreview_francais.setVisibility(View.GONE);
            }

            TextView detailsfiche_arbreview_description = convertArbreView.findViewById(R.id.detailsfiche_arbreview_description);
            ImageButton descriptionClassificationButton = convertArbreView.findViewById(R.id.details_tableau_phylogenetique_fold_unflod_description_imageButton);

            if (!classification.getDescriptif().isEmpty()) {
                richtext = textesOutils.textToSpannableStringDoris(classification.getDescriptif());
                detailsfiche_arbreview_description.setText(richtext, BufferType.SPANNABLE);
                detailsfiche_arbreview_description.setMovementMethod(LinkMovementMethod.getInstance());

                // Ouverture fermeture des descriptions des Classifications
                LinearLayout descriptionClassificationLayout = convertArbreView.findViewById(R.id.LinearLayout_description);

                FoldableClickListener descriptionClassificationFoldable = new FoldableClickListener(this, descriptionClassificationLayout, descriptionClassificationButton, ImageButtonKind.DESCRIPTION_ARBRE_PHYLO);
                allFoldableClassificationDescription.add(descriptionClassificationFoldable);

                // Par défaut on cache la description
                descriptionClassificationLayout.setVisibility(View.GONE);
                descriptionClassificationButton.setOnClickListener(v -> onClickAllDescriptionClassification());

            } else {
                detailsfiche_arbreview_description.setVisibility(View.GONE);
                descriptionClassificationButton.setVisibility(View.GONE);
            }

            sectionArbre.addView(convertArbreView);
        }

        // Ouverture fermeture de l'arbre
        ImageButton detailsTableauButton = convertView.findViewById(R.id.details_tableau_phylogenetique_fold_unflod_section_imageButton);

        FoldableClickListener detailsTableaufoldable = new FoldableClickListener(this, sectionArbre, detailsTableauButton, ImageButtonKind.DETAILS_FICHE);
        allFoldableDetails.add(detailsTableaufoldable);
        detailsTableauButton.setOnClickListener(detailsTableaufoldable);

        LinearLayout detailsTableauLayout = convertView.findViewById(R.id.details_tableau_phylogenetique_fold_unflod_section_linearlayout);
        detailsTableauLayout.setOnClickListener(detailsTableaufoldable);

        // enregistre pour réagir au click long
        registerForContextMenu(detailsTableauButton);
        registerForContextMenu(detailsTableauLayout);

        // Affichage et Masquage de l'arbre à l'affichage de la fiche
        if (!paramOutils.getParamBoolean(R.string.pref_key_fiche_aff_details_pardefaut, false)) {
            sectionArbre.setVisibility(View.GONE); // par défaut invisible
        } else {
            sectionArbre.setVisibility(View.VISIBLE);
            detailsTableauButton.setImageResource(R.drawable.ic_menu_up_outline);
        }

        containerLayout.addView(convertView);
    }

    View insertPhoto(PhotoFiche photoFiche) {
        //Log.d(LOG_TAG, "insertPhoto() - photoFiche : "+photoFiche.getCleURL());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int iconSize = paramOutils.getParamInt(R.string.pref_key_fiche_icone_taille, Integer.parseInt(context.getString(R.string.fiche_icone_taille_defaut)));
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LayoutParams(ScreenTools.dp2px(context, iconSize), ScreenTools.dp2px(context, iconSize)));

        layout.setGravity(Gravity.CENTER);

        final ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LayoutParams(ScreenTools.dp2px(context, iconSize), ScreenTools.dp2px(context, iconSize)));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Photos_Outils photosOutils = new Photos_Outils(this);
        final ImageType bestLocallyAvailableRes;
        if (photosOutils.isAvailableInFolderPhoto(photoFiche.getCleURLNomFichier(), ImageType.VIGNETTE)) { // utilise le format vignette en priorité, fallback sur les autres
            bestLocallyAvailableRes = ImageType.VIGNETTE;
        } else if (photosOutils.isAvailableInFolderPhoto(photoFiche.getCleURLNomFichier(), ImageType.MED_RES)) {
            bestLocallyAvailableRes = ImageType.MED_RES;
        } else if (photosOutils.isAvailableInFolderPhoto(photoFiche.getCleURLNomFichier(), ImageType.HI_RES)) {
            bestLocallyAvailableRes = ImageType.HI_RES;
        } else {
            bestLocallyAvailableRes = null;
        }


        ImageType requestedRes = ImageType.VIGNETTE;
        String small_suffixe_photo = Constants.GRANDE_BASE_URL_SUFFIXE;
        if (!photoFiche.getImgPostfixCodes().isEmpty() && photoFiche.getImgPostfixCodes().contains("&")) {
            // !! split -1 car https://stackoverflow.com/questions/14602062/java-string-split-removed-empty-values
            String[] imgPostfixCodes = photoFiche.getImgPostfixCodes().split("&", -1);
            if (!imgPostfixCodes[0].isEmpty()) {
                small_suffixe_photo = Constants.ImagePostFixCode.getEnumFromCode(imgPostfixCodes[0]).getPostFix();
            }
        }
        String requested_suffixe_photo = small_suffixe_photo;

        if (bestLocallyAvailableRes != null) {
            // on a une image en local, on l'utilise
            try {
                Picasso.get().load(photosOutils.getPhotoFile(photoFiche.getCleURLNomFichier(), bestLocallyAvailableRes))
                        .fit()
                        .centerInside()
                        .into(imageView);
            } catch (IOException ignored) {
            }
        } else {
            // pas préchargée en local pour l'instant, cherche sur internet si c'est autorisé
            if (reseauOutils.isTelechargementsModeConnectePossible()) {
                Picasso.get()
                        .load(Constants.IMAGE_BASE_URL + "/"
                                + photoFiche.getCleURL().replaceAll(
                                Constants.IMAGE_BASE_URL_SUFFIXE + "$", requested_suffixe_photo))
                        .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                        .error(R.drawable.doris_icone_doris_large_pas_connecte)
                        .fit()
                        .centerInside()
                        .into(imageView);
            } else {
                Picasso.get()
                        .load(Constants.IMAGE_BASE_URL + "/"
                                + photoFiche.getCleURL().replaceAll(
                                Constants.IMAGE_BASE_URL_SUFFIXE + "$", requested_suffixe_photo))
                        .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                        .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                        .error(R.drawable.doris_icone_doris_large_pas_connecte)
                        .fit()
                        .centerInside()
                        .into(imageView);
            }
        }

        layout.addView(imageView);
        return layout;

    }

    public void dataHasChanged(String textmessage) {
        Message completeMessage = mHandler.obtainMessage(1, textmessage);
        completeMessage.sendToTarget();
    }

    class OnImageClickListener implements OnClickListener {

        int _position;
        int _ficheID;
        Activity _activity;

        // constructor
        public OnImageClickListener(int ficheID, int position, Activity activity) {
            this._position = position;
            this._activity = activity;
            this._ficheID = ficheID;
        }

        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "onClick() - v : " + v.toString());
            Log.d(LOG_TAG, "onClick() - _position : " + _position + " - _ficheID : " + _ficheID);

            DorisApplicationContext.getInstance().setIntentPourRetour(getIntent());

            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, ImagePleinEcran_CustomViewActivity.class);
            i.putExtra("position", _position);
            i.putExtra("ficheId", _ficheID);
            _activity.startActivity(i);
        }

    }

    // End of user code

}
