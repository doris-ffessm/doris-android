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

import static fr.ffessm.doris.android.R.string.pref_key_filtre_groupe;

import java.util.List;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Groupe;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

//Start of user code protected additional GroupeSelection_Adapter imports
// additional imports
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.tools.Groupes_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.Textes_Outils;
import fr.ffessm.doris.android.tools.ThemeUtil;
//End of user code

public class GroupeSelection_Adapter extends BaseAdapter {

    private Context context;

    /**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
     */
    protected DorisDBHelper _contextDB = null;

    private static final String LOG_TAG = GroupeSelection_Adapter.class.getCanonicalName();

    private List<Groupe> groupeList;
    public List<Groupe> filteredGroupeList;
    SharedPreferences prefs;
    //Start of user code protected additional GroupeSelection_Adapter attributes
    boolean depuisAccueil = false;

    private Param_Outils paramOutils;
    private Textes_Outils textesOutils;

    // niveau de profondeur utilisé pour le listGroup par rapport au Groupe
    public Groupe currentRootGroupe;

    /**
     * custom constructor
     *
     * @param context
     * @param contextDB
     * @param depuisAccueil
     */
    public GroupeSelection_Adapter(Context context, DorisDBHelper contextDB, boolean depuisAccueil) {
        super();
        this.context = context;
        this._contextDB = contextDB;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        paramOutils = new Param_Outils(context);
        textesOutils = new Textes_Outils(context);
        this.depuisAccueil = depuisAccueil;

        updateList();

    }
    // End of user code

    public GroupeSelection_Adapter(Context context, DorisDBHelper contextDB) {
        super();
        this.context = context;
        this._contextDB = contextDB;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Start of user code protected GroupeSelection_Adapter constructor

        textesOutils = new Textes_Outils(context);

        // End of user code
        updateList();
    }

    protected void updateList() {
        // Start of user code protected GroupeSelection_Adapter updateList
        Log.d(LOG_TAG, "updateList() - Début");

        if (groupeList == null) {
            this.groupeList = Groupes_Outils.getAllGroupes(_contextDB);
        }

        if (currentRootGroupe == null) {
            // Si un Groupe avait déjà été sélectionné, on réaffiche son père : logique si l'on veut qu'il soit un choix possible
            int groupRootId = Groupes_Outils.getGroupeRoot(groupeList).getId();
            int filtreCourantId = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), groupRootId);

            if (filtreCourantId != groupRootId) {
                currentRootGroupe = Groupes_Outils.getGroupeFromId(groupeList, filtreCourantId).getGroupePere();
            }
        }

        if (currentRootGroupe == null) {
            currentRootGroupe = Groupes_Outils.getGroupeRoot(groupeList);
        }

        buildTreeForRoot(currentRootGroupe);

        Log.d(LOG_TAG, "updateList() - Fin");
        // End of user code
    }

    @Override
    public int getCount() {
        return filteredGroupeList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredGroupeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // Start of user code protected additional GroupeSelection_Adapter getView_assign code
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.groupeselection_listviewrow, null);
        }
        final Groupe entry = filteredGroupeList.get(position);
        if (_contextDB != null) entry.setContextDB(_contextDB);

        // set data in the row
        TextView tvLabel = (TextView) convertView.findViewById(R.id.groupeselection_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
        labelSB.append(entry.getNomGroupe());
        labelSB.append(" ");
        tvLabel.setText(labelSB.toString());

        TextView tvDetails = (TextView) convertView.findViewById(R.id.groupeselection_listviewrow_details);
        StringBuilder detailsSB = new StringBuilder();
        if(entry.getDescriptionGroupe() != null) {
            detailsSB.append(entry.getDescriptionGroupe().toString());
        }
        detailsSB.append(" ");
        tvDetails.setText(detailsSB.toString());
        // End of user code

        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout) convertView.findViewById(R.id.groupeselection_listviewrow);
        llRow.setTag(entry);

        // Start of user code protected additional GroupeSelection_Adapter getView code
        //	additional code

        ImageView ivIconGroup = (ImageView) convertView.findViewById(R.id.groupeselection_listviewrow_icon);
        int defaultIconSize = paramOutils.getParamInt(R.string.pref_key_accueil_icone_taille, Integer.parseInt(context.getString(R.string.accueil_icone_taille_defaut)));
        // Les icones ne sont pas d'une qualité suffisante pour être affichées à la même taille que les icônes des zones
        // On les diminue donc un peu
        defaultIconSize = (int) (defaultIconSize * 0.5);
        ivIconGroup.getLayoutParams().height = ScreenTools.dp2px(context, defaultIconSize);
        ivIconGroup.getLayoutParams().width = ScreenTools.dp2px(context, defaultIconSize);

        //Log.d(LOG_TAG,"currentRootGroupe : "+currentRootGroupe.getId()+" - "+currentRootGroupe.getNomGroupe());
        if (currentRootGroupe.getId() != 1) {
            ivIconGroup.setBackgroundResource(ThemeUtil.attrToResId(((GroupeSelection_ClassListViewActivity) context), R.attr.ic_action_background));
        } else {
            ivIconGroup.setBackgroundResource(0);
        }

        if (entry.getCleURLImage() != null && !entry.getCleURLImage().isEmpty()) {
            //Picasso.with(context).load(Constants.getSiteUrl()+entry.getCleURLImage()).placeholder(R.drawable.app_ic_launcher).into(ivIcon);
            int identifierIconeGroupe = context.getResources().getIdentifier(entry.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw", context.getPackageName());
            //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getView() - identifierIconeGroupe : "+identifierIconeGroupe);

            Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIconeGroupe));
            ivIconGroup.setImageBitmap(bitmap);
        } else {
            // remet image de base
            ivIconGroup.setImageResource(R.drawable.app_ic_launcher);
        }

        // Bouton Liste des Fiches
        ImageButton selectButton1 = (ImageButton) convertView
                .findViewById(R.id.groupeselection_btn1Select);
        selectButton1.setFocusable(false);
        selectButton1.setClickable(true);
        selectButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((GroupeSelection_ClassListViewActivity) context).isActivityDestroyed() || ((GroupeSelection_ClassListViewActivity) context).isFinishing())
                    return;
                Toast.makeText(context, "Filtre espèces : " + entry.getNomGroupe(), Toast.LENGTH_SHORT).show();

                prefs.edit().putInt(context.getString(pref_key_filtre_groupe), entry.getId())
                        .apply();

                if (BuildConfig.DEBUG)
                    Log.d(LOG_TAG, "onClick() - depuisAccueil : " + depuisAccueil);
                if (!depuisAccueil) {
                    ((GroupeSelection_ClassListViewActivity) context).finish();
                } else {

                    DorisApplicationContext.getInstance().setIntentPourRetour(((Activity) context).getIntent());

                    Intent toListeFiche_View = new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class);
                    toListeFiche_View.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(toListeFiche_View);
                }
            }
        });

        // Bouton Liste des Fiches par Images
        ImageButton selectButton2 = (ImageButton) convertView
                .findViewById(R.id.groupeselection_btn2Select);
        selectButton2.setFocusable(false);
        selectButton2.setClickable(true);
        selectButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((GroupeSelection_ClassListViewActivity) context).isActivityDestroyed() || ((GroupeSelection_ClassListViewActivity) context).isFinishing())
                    return;
                Toast.makeText(context, "Filtre espèces : " + entry.getNomGroupe(), Toast.LENGTH_SHORT).show();

                prefs.edit().putInt(context.getString(pref_key_filtre_groupe), entry.getId())
                        .apply();

                if (BuildConfig.DEBUG)
                    Log.d(LOG_TAG, "onClick() - depuisAccueil : " + depuisAccueil);
                if (!depuisAccueil) {
                    ((GroupeSelection_ClassListViewActivity) context).finish();
                } else {

                    DorisApplicationContext.getInstance().setIntentPourRetour(((Activity) context).getIntent());

                    Intent toListeFiche_View = new Intent(context, ListeImageFicheAvecFiltre_ClassListViewActivity.class);
                    toListeFiche_View.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(toListeFiche_View);
                }
            }
        });
        // ajout de l'image "expand" si contient des sous groupes
        ImageView ivChildGroup = (ImageView) convertView.findViewById(R.id.groupeselection_ivChildGroup);

        //TODO : test Nb groupes fils et nb fiches résultant (Attention lent)
        //Log.d(LOG_TAG,"Nb Sous-Groupes : "+entry.getGroupesFils().size());
        //int currentZoneFilterId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
        //Log.d(LOG_TAG,"Nb Fiches du Groupe : "
        //		+ Groupes_Outils.getTailleGroupeFiltre(context, _contextDB, currentZoneFilterId, currentRootGroupe.getId() ));


        if (entry.getGroupesFils().size() > 0) {
            ivChildGroup.setVisibility(View.VISIBLE);
        } else {
            ivChildGroup.setVisibility(View.INVISIBLE);
        }
        // End of user code

        return convertView;

    }


    //Start of user code protected additional GroupeSelection_Adapter methods
    // additional methods
    public Groupe getGroupeFromPosition(int position) {
        return filteredGroupeList.get(position);

    }

    protected void refreshNavigation() {

        LinearLayout navigationLayout = (LinearLayout) ((GroupeSelection_ClassListViewActivity) context).findViewById(R.id.groupselection_listview_navigation);

        navigationLayout.removeAllViews();
        addBackToParentGroupButton(navigationLayout, currentRootGroupe.getGroupePere());

        if (currentRootGroupe.getId() == 1) {

            // ajout du nouveau bouton standard
            ImageButton backToParentButton = new ImageButton(context);
            navigationLayout.addView(backToParentButton);

            backToParentButton.setImageResource(ThemeUtil.attrToResId(((GroupeSelection_ClassListViewActivity) context), R.attr.ic_action_arbre_phylogenetique));
            backToParentButton.setBackgroundResource(R.drawable.button_background);
            backToParentButton.setScaleType(ScaleType.FIT_CENTER);
            backToParentButton.setPadding(0, 8, 0, 8);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) backToParentButton.getLayoutParams();

            backToParentButton.setLayoutParams(layoutParams);


        } else {

            // ajout du nouveau bouton
            Button backToParentButton = new Button(context);
            navigationLayout.addView(backToParentButton);

            backToParentButton.setText(
                    textesOutils.raccourcir(currentRootGroupe.getNomGroupe().trim(),
                            Integer.parseInt(context.getString(R.string.groupselection_listview_groupe_nbcarmax))
                    ));


            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) backToParentButton.getLayoutParams();
            layoutParams.leftMargin = 2;
            layoutParams.rightMargin = 2;

            backToParentButton.setBackgroundResource(R.drawable.button_background);
            backToParentButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            backToParentButton.setPadding(5, 0, 5, 0);

        }

        Log.d(LOG_TAG, "refreshNavigation() - Fin");
    }

    protected void addBackToParentGroupButton(LinearLayout _navigationLayout, Groupe _parent) {
        Log.d(LOG_TAG, "addBackToParentGroupButton() - Début");

        if (_parent == null) return;

        if (groupeList == null) {
            this.groupeList = Groupes_Outils.getAllGroupes(_contextDB);
        }

        final Groupe parent = Groupes_Outils.getGroupeFromId(groupeList, _parent.getId());

        addBackToParentGroupButton(_navigationLayout, parent.getGroupePere());

        if (parent.getId() == 1) {
            // ajout du nouveau bouton standard
            ImageButton backToParentButton = new ImageButton(context);
            _navigationLayout.addView(backToParentButton);

            backToParentButton.setImageResource(ThemeUtil.attrToResId(((GroupeSelection_ClassListViewActivity) context), R.attr.ic_action_arbre_phylogenetique));
            backToParentButton.setBackgroundResource(R.drawable.button_selected_background);
            backToParentButton.setScaleType(ScaleType.FIT_CENTER);
            backToParentButton.setPadding(0, 8, 0, 8);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) backToParentButton.getLayoutParams();
            backToParentButton.setLayoutParams(layoutParams);
            backToParentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildTreeForRoot(parent);
                }
            });
        } else {
            // ajout du nouveau bouton
            Button backToParentButton = new Button(context);
            _navigationLayout.addView(backToParentButton);

            backToParentButton.setText(
                    textesOutils.raccourcir(parent.getNomGroupe().trim(),
                            Integer.parseInt(context.getString(R.string.groupselection_listview_groupe_nbcarmax))
                    ));

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) backToParentButton.getLayoutParams();
            layoutParams.leftMargin = 2;
            layoutParams.rightMargin = 2;
            //layoutParams.height = 28;

            backToParentButton.setBackgroundResource(R.drawable.button_selected_background);
            backToParentButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            backToParentButton.setPadding(5, 0, 5, 0);

            backToParentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groupeList == null) {
                        groupeList = Groupes_Outils.getAllGroupes(_contextDB);
                    }
                    buildTreeForRoot(parent);
                }
            });
        }
        Log.d(LOG_TAG, "addBackToParentGroupButton() - Fin");
    }


    public void buildTreeForRoot(Groupe rootGroupe) {
        Log.d(LOG_TAG, "buildTreeForRoot() - Début");
        Log.d(LOG_TAG, "buildTreeForRoot() - rootGroupe : " + rootGroupe.getId());

        this.currentRootGroupe = rootGroupe;

        List<Groupe> nextLevelGroupes = Groupes_Outils.getAllGroupesForNextLevel(groupeList, currentRootGroupe);

        if (nextLevelGroupes.size() > 0)
            this.filteredGroupeList = nextLevelGroupes;

        notifyDataSetChanged();
        refreshNavigation();

        Log.d(LOG_TAG, "buildTreeForRoot() - Fin");
    }

    //End of user code
}
