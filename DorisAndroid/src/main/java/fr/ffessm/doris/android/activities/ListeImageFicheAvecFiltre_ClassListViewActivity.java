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


import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.activities.view.indexbar.FicheAlphabeticalIndexManager;
import fr.ffessm.doris.android.activities.view.indexbar.AlphabetIndexBarHandler;
import fr.ffessm.doris.android.activities.view.indexbar.FicheGroupeIndexManager;
import fr.ffessm.doris.android.activities.view.indexbar.GroupIndexBarHandler;
import fr.ffessm.doris.android.activities.view.indexbar.GroupeListProvider;
import fr.ffessm.doris.android.activities.view.indexbar.IndxBarHandlerMessages;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.Groupes_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.appcompat.app.ActionBar;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
// Start of user code protectedListeFicheAvecFiltre_ClassListViewActivity_additionalimports
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import androidx.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import fr.ffessm.doris.android.activities.view.AffichageMessageHTML;
// End of user code

public class ListeImageFicheAvecFiltre_ClassListViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> implements OnItemClickListener, ActivityWithIndexBar {

    private static final String LOG_TAG = ListeImageFicheAvecFiltre_ClassListViewActivity.class.getSimpleName();

    //Start of user code constants ListeImageFicheAvecFiltre_ClassListViewActivity

    MenuItem searchButtonMenuItem;

    int iconSize = R.string.list_icone_taille_defaut;

    final Context context = this;
    final Param_Outils paramOutils = new Param_Outils(context);
    //End of user code

    ListeImageFicheAvecFiltre_Adapter adapter;

    Handler mHandler;
    HashMap<Character, Integer> alphabetToIndex;
    HashMap<Integer, Integer> groupeIdToIndex;

    public void onCreate(Bundle bundle) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(bundle);
        ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.listeimageficheavecfiltre_listview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listeimageficheavecfiltre_listview_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView list = findViewById(R.id.listeimageficheavecfiltre_listview);
        list.setClickable(true);
        //Start of user code onCreate ListeImageFicheAvecFiltre_ClassListViewActivity adapter creation
        Log.d(LOG_TAG, "ListeImageFicheAvecFiltre_ClassListViewActivity - onCreate");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        adapter = new ListeImageFicheAvecFiltre_Adapter(this, getHelper().getDorisDBHelper(),
                prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1),
                prefs.getInt(getString(R.string.pref_key_filtre_groupe), Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId())
        );

        //End of user code
        // avoid opening the keyboard on view opening
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);

        // Get the intent, verify the action and get the query
        handleIntent(getIntent());

        // add handler for indexBar
        if(isGroupeMode()) {
            ListView listview = findViewById(R.id.listeficheavecfiltre_listview);
            int filtreGroupe = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe),
                    Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId());
            mHandler = new GroupIndexBarHandler(this, listview, filtreGroupe);
        } else {
            mHandler = new AlphabetIndexBarHandler(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "ListeImageFicheAvecFiltre_ClassListViewActivity - onResume");
        // refresh on resume, the preferences and filter may have changed
        // TODO peut être qu'il y a moyen de s'abonner aux changements de préférence et de ne le faire que dans ce cas ?
        ListeImageFicheAvecFiltre_ClassListViewActivity.this.adapter.refreshFilter();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (paramOutils.getParamInt(R.string.pref_key_list_icone_taille, Integer.parseInt(context.getString(R.string.list_icone_taille_defaut))) != iconSize) {
            iconSize = paramOutils.getParamInt(R.string.pref_key_list_icone_taille, Integer.parseInt(context.getString(R.string.list_icone_taille_defaut)));
            ListView list = findViewById(R.id.listeimageficheavecfiltre_listview);
            list.invalidateViews();
        }



        // send an update of the filtre on Groupe
        Integer filtreGroupe = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId());
        Message msg = this.getHandler().obtainMessage();
        msg.what = IndxBarHandlerMessages.ON_RESUME_GROUP_EVT;
        msg.obj=filtreGroupe;
        mHandler.sendMessage(msg);

        updateFilterInActionBar();

        populateIndexBarHashMap();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        //Log.d(LOG_TAG,"Intent received");
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show word
            //  Intent wordIntent = new Intent(this, WordActivity.class);
            // wordIntent.setData(intent.getData());
            // startActivity(wordIntent);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(LOG_TAG, "ACTION_SEARCH Intent received for " + query);
            ListeImageFicheAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(query);
        }
    }

    @Override
    public boolean onSearchRequested() {
        Log.d(LOG_TAG, "onSearchRequested received");
        return super.onSearchRequested();
    }

    public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
        Log.d(LOG_TAG, "onItemClick " + view);
        if (view instanceof LinearLayout && view.getId() == R.id.listeimageficheavecfiltre_listviewrow) {
            //Start of user code onItemClick additions ListeImageFicheAvecFiltre_ClassListViewActivity
            DorisApplicationContext.getInstance().setIntentPourRetour(getIntent());
            //End of user code

            // normal case on main item

            Intent toDetailView = new Intent(this, DetailsFiche_ElementViewActivity.class);
            Bundle b = new Bundle();
            b.putInt("ficheId", ((Fiche) view.getTag()).getId());
            toDetailView.putExtras(b);
            startActivity(toDetailView);
        } else if (view instanceof TextView && view.getId() == R.id.indexbar_alphabet_row_textview) {
            // click on indexBar
            if(!isGroupeMode()) {
                TextView rowview = (TextView) view;
                CharSequence alphabet = rowview.getText();

                if (alphabet == null || alphabet.equals(""))
                    return;

                String selected_alphabet = alphabet.toString().trim();
                Integer newPosition = alphabetToIndex.get(selected_alphabet.charAt(0));
                Log.d(LOG_TAG, "Selected Alphabet is:" + selected_alphabet + "   position is:" + newPosition);
                if (newPosition != null) {
                    showShortToast(selected_alphabet);
                    ListView listview = findViewById(R.id.listeimageficheavecfiltre_listview);
                    listview.setSelection(newPosition);
                }
            }
        } else if (view instanceof ImageView && view.getId() == R.id.indexbar_alphabet_row_imageview) {
            // click on indexBar
            if(isGroupeMode()) {
                ImageView rowview = (ImageView) view;
                if(rowview.getTag() !=  null && rowview.getTag() instanceof Groupe) {
                    Groupe groupe = (Groupe) rowview.getTag();
                    Integer newPosition = groupeIdToIndex.get(groupe.getId());
                    if (newPosition != null) {
                        showShortToast(groupe.getNomGroupe());
                        ListView listview = findViewById(R.id.listeimageficheavecfiltre_listview);
                        listview.setSelection(newPosition);
                    }
                }
            }
        }
    }

    //Start of user code additional  ListeImageFicheAvecFiltre_ClassListViewActivity methods
    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");

        //On vide le cache des infos liées aux fiches
        getHelper().getFicheDao().clearObjectCache();
        getHelper().getFiches_ZonesGeographiquesDao().clearObjectCache();
        getHelper().getIntervenantFicheDao().clearObjectCache();
        getHelper().getPhotoFicheDao().clearObjectCache();

        super.onDestroy();
    }
    //End of user code

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add options in the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listeficheavecfiltre_classlistview_actions, menu);
        // Associate searchable configuration with the SearchView
        // deal with compat
        MenuItem menuItem = menu.findItem(R.id.listeficheavecfiltre_classlistview_action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO must be careful if the request might be long
                // action on text change
                ListeImageFicheAvecFiltre_ClassListViewActivity.this.adapter.getFilter().filter(arg0);
                return false;
            }
        });

        // add additional programmatic options in the menu
        //Start of user code additional onCreateOptionsMenu ListeFicheAvecFiltre_ClassListViewActivity
        // changement du titre (on aurai aussi pu simplement changer le menu ?)
        MenuItem switchListMode = menu.findItem(R.id.listeficheavecfiltre_classlistview_action_textlist2imagelist);
        switchListMode.setTitle(R.string.listeficheavecfiltre_classlistview_action_imagelist2textlist_title);
        switchListMode.setIcon(R.drawable.ic_action_liste_fiches);

        searchButtonMenuItem = menu.findItem(R.id.listeficheavecfiltre_classlistview_action_filterpopup);
        updateFilterInActionBar();
        //searchPopupButtonManager = new SearchPopupButtonManager(this);
        //End of user code
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // behavior of option menu
        int itemId = item.getItemId();
        if (itemId == R.id.listeficheavecfiltre_classlistview_action_preference) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
            //Start of user code additional menu action ListeFicheAvecFiltre_ClassListViewActivity
        } else if (itemId == R.id.listeficheavecfiltre_classlistview_action_filterpopup) {//showToast("searchPopupButtonManager.onClickFilterBtn(MenuItemCompat.getActionView(item))");
            //	searchPopupButtonManager.onClickFilterBtn(MenuItemCompat.getActionView(item));
            View menuItemView = findViewById(R.id.listeficheavecfiltre_classlistview_action_filterpopup); // SAME ID AS MENU ID
            // crée le manager de popup
            //searchPopupButtonManager = new SearchPopupButtonManager(this);
            //showFilterPopupMenu(menuItemView);
            //searchPopupButtonManager.onClickFilterBtn(menuItemView);
            showPopup();
            return true;
        } else if (itemId == R.id.listeficheavecfiltre_classlistview_action_textlist2imagelist) {
            Intent i = new Intent(this, ListeFicheAvecFiltre_ClassListViewActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return true;
        } else if (itemId == R.id.listeficheavecfiltre_action_aide) {
            AffichageMessageHTML aide = new AffichageMessageHTML(context, (Activity) context, getHelper());
            aide.affichageMessageHTML(context.getString(R.string.aide_label), " ", "file:///android_res/raw/aide.html");
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
        //Start of user code getSupportParentActivityIntent ListeFicheAvecFiltre_ClassListViewActivity
        // navigates to the parent activity
        return new Intent(this, Accueil_CustomViewActivity.class);
        //End of user code
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        //Start of user code onCreateSupportNavigateUpTaskStack ListeFicheAvecFiltre_ClassListViewActivity
        super.onCreateSupportNavigateUpTaskStack(builder);
        //End of user code
    }

    // -------------- handler (for indexBar)
    @Override
    public Handler getHandler() {
        return mHandler;
    }

    public void populateIndexBarHashMap() {

        if(isGroupeMode()) {
            populateIndexBarHashMapGroupe();
        } else {
            populateIndexBarHashMapAlphabet();
        }
    }
    public void populateIndexBarHashMapAlphabet() {
        FicheAlphabeticalIndexManager indexHelper = new FicheAlphabeticalIndexManager(context, getHelper().getDorisDBHelper() );
        alphabetToIndex = indexHelper.getUsedIndexHashMapItemIds(adapter.filteredFicheIdList);

        /*Now I am making an entry of those alphabets which are not there in the Map*/
        String[] alphabets = getResources().getStringArray(R.array.alphabet_array);
        int index = -1;

        for (String alpha1 : alphabets) {
            char alpha = alpha1.charAt(0);
            index++;

            if (alphabetToIndex.containsKey(alpha))
                continue;

            /*Start searching the next character position. Example, here alpha is E. Since there is no entry for E, we need to find the position of next Character, F.*/
            for (int i = index + 1; i < 27; i++) {        //start from next character to last character
                char searchAlphabet = alphabets[i].charAt(0);

                /*If we find the position of F character, then on click event on E should take the user to F*/
                if (alphabetToIndex.containsKey(searchAlphabet)) {
                    alphabetToIndex.put(alpha, alphabetToIndex.get(searchAlphabet));
                    break;
                } else if (i == 26) /*If there are no entries after E, then on click event on E should take the user to end of the list*/
                    alphabetToIndex.put(alpha, adapter.filteredFicheIdList.size() - 1);
                else
                    continue;

            }
        }
    }
    public void populateIndexBarHashMapGroupe() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int filtreGroupe = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId());
        FicheGroupeIndexManager indexHelper = new FicheGroupeIndexManager(context, getHelper().getDorisDBHelper(), filtreGroupe );
        groupeIdToIndex = indexHelper.getUsedIndexHashMapItemIds(adapter.filteredFicheIdList);

        /*Now I am making an entry of those GroupeId which are not present in the Map*/
        List<Integer> allGroupIDs = GroupeListProvider.getAllGroupeIdList(getHelper().getDorisDBHelper());
        String[] alphabets = getResources().getStringArray(R.array.alphabet_array);
        int index = -1;

        for (Integer groupID : allGroupIDs) {

            index++;

            if (groupeIdToIndex.containsKey(groupID))
                continue;

            /*Start searching the next character position. Example, here alpha is E. Since there is no entry for E, we need to find the position of next Character, F.*/
            for (int i = index + 1; i < allGroupIDs.size(); i++) {        //start from next character to last character
                Integer searchAlphabet = allGroupIDs.get(i);

                /*If we find the position of F character, then on click event on E should take the user to F*/
                if (groupeIdToIndex.containsKey(searchAlphabet)) {
                    groupeIdToIndex.put(groupID, groupeIdToIndex.get(searchAlphabet));
                    break;
                } else if (i == allGroupIDs.size()) /*If there are no entries after E, then on click event on E should take the user to end of the list*/
                    groupeIdToIndex.put(groupID, adapter.filteredFicheIdList.size() - 1);
                else
                    continue;

            }
        }
    }

    @Override
    public DorisDBHelper getDorisDBHelper() {
        return getHelper().getDorisDBHelper();
    }


    protected boolean isGroupeMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currentMode =  prefs.getString(
                this.getResources().getString(
                        R.string.pref_key_current_mode_affichage),
                this.getResources().getString(R.string.current_mode_affichage_default));
        return currentMode.equals("liste_par_groupe") || currentMode.equals("photos_par_groupe");
    }

    @Override
    public ListView getAlphabetListView() {
        return findViewById(R.id.listeimageficheavecfiltre_listView_alphabets);
    }

    public View getAlphabetRowView() {
        return findViewById(R.id.alphabet_row_layout);
    }

    // Start of user code protectedListeFicheAvecFiltre_ClassListViewActivity

    public void updateFilterInActionBar() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ActionBar actionBar = getSupportActionBar();
        // mise à jour des titres
        // Titre = zone
        int currentZoneFilterId = prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1);
        if (currentZoneFilterId == -1 || currentZoneFilterId == 0) { // test sur 0, juste pour assurer la migration depuis alpha3 , a supprimer plus tard
            //actionBar.setTitle(R.string.accueil_recherche_precedente_filtreGeographique_sans);
            String[] zonegeo_shortnames = getResources().getStringArray(R.array.zonegeo_shortname_array);
            actionBar.setTitle(zonegeo_shortnames[0]);
        } else {
            //ZoneGeographique currentZoneFilter= getHelper().getZoneGeographiqueDao().queryForId(currentFilterId);
            //actionBar.setTitle(currentZoneFilter.getNom().trim());
            String[] zonegeo_shortnames = getResources().getStringArray(R.array.zonegeo_shortname_array);
            actionBar.setTitle(zonegeo_shortnames[currentZoneFilterId]);
        }

        // sous titre = espèce
        int groupRootId = Groupes_Outils.getGroupeRoot(getHelper().getDorisDBHelper()).getId();
        int filtreCourantId = prefs.getInt(getString(R.string.pref_key_filtre_groupe), groupRootId);
        if (filtreCourantId == groupRootId) {
            actionBar.setSubtitle(R.string.accueil_recherche_precedente_filtreEspece_sans);
        } else {
            Groupe groupeFiltreCourant = getHelper().getGroupeDao().queryForId(filtreCourantId);
            actionBar.setSubtitle(groupeFiltreCourant.getNomGroupe().trim());

        }
        // mise à jour des actions
        if ((prefs.getInt(getString(R.string.pref_key_filtre_groupe), groupRootId) != groupRootId) ||
                (prefs.getInt(getString(R.string.pref_key_filtre_zonegeo), -1) != -1)) {
            // mise à jour de l'image du bouton de filtre
            if (searchButtonMenuItem != null)
                searchButtonMenuItem.setIcon(ThemeUtil.attrToResId(((ListeImageFicheAvecFiltre_ClassListViewActivity) context), R.attr.ic_app_filter_settings_actif));
        } else {
            // pas de filtre actif
            // remet l'imaged efiltre inactif
            //searchButton.setImageResource(R.drawable.filter_settings_32);
            if (searchButtonMenuItem != null)
                searchButtonMenuItem.setIcon(ThemeUtil.attrToResId(((ListeImageFicheAvecFiltre_ClassListViewActivity) context), R.attr.ic_app_filter_settings));

        }
    }

    public void showPopup() {

        View menuItemView = findViewById(R.id.listeficheavecfiltre_classlistview_action_filterpopup);
        // peut être null si pas visible, ex: dans actionbar overflow si pas assez de place dans l'action bar
        RelativeLayout viewGroup = findViewById(R.id.listeavecfiltre_filtrespopup);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.listeficheavecfiltre_filtrespopup, viewGroup);

        int popupWidth = getResources().getDimensionPixelSize(R.dimen.listeficheavecfiltre_popup_width);
        int popupHeight = getResources().getDimensionPixelSize(R.dimen.listeficheavecfiltre_popup_height);
        //Log.d(LOG_TAG,"showPopup() - width="+popupWidth+", height="+popupHeight);

        final PopupWindow popup = new PopupWindow(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);

        //popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        if (menuItemView != null) {
            menuItemView.getLocationOnScreen(location);
            Log.d(LOG_TAG, "menuitem pos =" + location[0] + " " + location[1]);

            popup.showAsDropDown(menuItemView, 0, 0);
        } else {
            Log.d(LOG_TAG, "menuitem pos not available, anchor to top of the listview");
            //popup.showAsDropDown(findViewById(R.id.listeficheavecfiltre_listview),0,0);
            View containerView = findViewById(R.id.listeimageficheavecfiltre_listview);
            containerView.getLocationOnScreen(location);
            Log.d(LOG_TAG, "menuitem pos =" + location[0] + " " + location[1] + " ");
            popup.showAtLocation(layout, Gravity.TOP | Gravity.RIGHT, 0, location[1]);
        }
        // bouton filtre espèce
        Button btnFiltreEspece = layout.findViewById(R.id.listeavecfiltre_filtrespopup_GroupeButton);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int filtreCourantId = prefs.getInt(this.getString(R.string.pref_key_filtre_groupe), 1);
        if (filtreCourantId == 1) {
            btnFiltreEspece.setText(getString(R.string.listeficheavecfiltre_popup_filtreEspece_sans));
        } else {
            Groupe groupeFiltreCourant = getHelper().getGroupeDao().queryForId(filtreCourantId);
            btnFiltreEspece.setText(getString(R.string.listeficheavecfiltre_popup_filtreEspece_avec) + " " + groupeFiltreCourant.getNomGroupe().trim());
        }

        btnFiltreEspece.setOnClickListener(v -> {
            popup.setFocusable(true);
            popup.dismiss();

            //Permet de revenir à cette liste après choix du groupe, True on retournerait à l'accueil
            Intent toGroupeSelectionView = new Intent(ListeImageFicheAvecFiltre_ClassListViewActivity.this, GroupeSelection_ClassListViewActivity.class);
            Bundle b = new Bundle();
            b.putBoolean("GroupeSelection_depuisAccueil", false);
            toGroupeSelectionView.putExtras(b);
            startActivity(toGroupeSelectionView);
        });

        // bouton filtre zone géographique
        Button btnZoneGeo = layout.findViewById(R.id.listeavecfiltre_filtrespopup_ZoneGeoButton);
        int currentFilterId = prefs.getInt(ListeImageFicheAvecFiltre_ClassListViewActivity.this.getString(R.string.pref_key_filtre_zonegeo), -1);
        if (currentFilterId == -1) {
            btnZoneGeo.setText(getString(R.string.listeficheavecfiltre_popup_filtreGeographique_sans));
        } else {
            ZoneGeographique currentZoneFilter = getHelper().getZoneGeographiqueDao().queryForId(currentFilterId);
            btnZoneGeo.setText(getString(R.string.listeficheavecfiltre_popup_filtreGeographique_avec) + " " + currentZoneFilter.getNom().trim());
        }

        btnZoneGeo.setOnClickListener(v -> {
            popup.setFocusable(true);
            popup.dismiss();

            //Toast.makeText(getApplicationContext(), "Zone géographique", Toast.LENGTH_LONG).show();
            startActivity(new Intent(ListeImageFicheAvecFiltre_ClassListViewActivity.this, ZoneGeoSelection_ClassListViewActivity.class));
        });

    }


    // End of user code


}
