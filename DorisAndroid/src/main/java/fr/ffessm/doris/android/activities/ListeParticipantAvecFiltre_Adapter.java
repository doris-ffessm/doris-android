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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;


import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//Start of user code protected additional ListeParticipantAvecFiltre_Adapter imports
// additional imports

import android.view.ViewGroup.LayoutParams;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;

import java.io.IOException;
import java.util.Locale;

//End of user code

public class ListeParticipantAvecFiltre_Adapter extends BaseAdapter implements Filterable {

    private Context context;

    /**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
     */
    protected DorisDBHelper _contextDB = null;

    private static final String LOG_TAG = ListeParticipantAvecFiltre_Adapter.class.getCanonicalName();

    private List<Participant> participantList;
    public List<Participant> filteredParticipantList;
    private final Object mLock = new Object();
    private SimpleFilter mFilter;
    SharedPreferences prefs;
    //Start of user code protected additional ListeParticipantAvecFiltre_Adapter attributes
    // additional attributes

    protected Param_Outils paramOutils;
    protected Photos_Outils photosOutils;
    protected Reseau_Outils reseauOutils;

    //End of user code

    public ListeParticipantAvecFiltre_Adapter(Context context, DorisDBHelper contextDB) {
        super();
        this.context = context;
        this._contextDB = contextDB;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Start of user code protected ListeParticipantAvecFiltre_Adapter constructor

        paramOutils = new Param_Outils(context);
        reseauOutils = new Reseau_Outils(context);
        photosOutils = new Photos_Outils(context);

        // End of user code
        updateList();
    }

    protected void updateList() {
        // Start of user code protected ListeParticipantAvecFiltre_Adapter updateList
        // TODO find a way to query in a lazier way
        try {
            //this.participantList = _contextDB.participantDao.queryForAll();
            this.participantList = _contextDB.participantDao.query(_contextDB.participantDao.queryBuilder().orderBy("nom", true).prepare());
            this.filteredParticipantList = this.participantList;
        } catch (java.sql.SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        // End of user code
    }

    @Override
    public int getCount() {
        if (filteredParticipantList.size() == 0) {
            return 1;    // will create a dummy entry to invite changing the filters
        }
        return filteredParticipantList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredParticipantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // Start of user code protected additional ListeParticipantAvecFiltre_Adapter getView_assign code
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listeparticipantavecfiltre_listviewrow, null);
        }
        if (filteredParticipantList.size() == 0) {
            return getNoResultSubstitute(convertView);
        }
        final Participant entry = filteredParticipantList.get(position);
        if (_contextDB != null) entry.setContextDB(_contextDB);

        // set data in the row
        TextView tvLabel = (TextView) convertView.findViewById(R.id.listeparticipantavecfiltre_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
        labelSB.append(entry.getNom());
        labelSB.append(" ");
        tvLabel.setText(labelSB.toString());
        // End of user code

        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout) convertView.findViewById(R.id.listeparticipantavecfiltre_listviewrow);
        llRow.setTag(entry);

        // Start of user code protected additional ListeParticipantAvecFiltre_Adapter getView code
        //	additional code
        final ImageView trombineView = (ImageView) convertView.findViewById(R.id.listeparticipantavecfiltre_listviewrow_icon);
        final int defaultIconSize = paramOutils.getParamInt(R.string.pref_key_list_icone_taille, Integer.parseInt(context.getString(R.string.list_icone_taille_defaut)));

        trombineView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        trombineView.getLayoutParams().width = ScreenTools.dp2px(context, defaultIconSize);

        if (!entry.getCleURLPhotoParticipant().isEmpty()) {

            if (photosOutils.isAvailableInFolderPhoto(entry.getPhotoNom(), ImageType.PORTRAITS)) {
                try {
                    Picasso.get().load(photosOutils.getPhotoFile(entry.getPhotoNom(), ImageType.PORTRAITS))
                            .placeholder(R.drawable.app_ic_participant)  // utilisation de l'image par defaut pour commencer
                            .resize(defaultIconSize, defaultIconSize)
                            .centerInside()
                            .into(trombineView);
                } catch (IOException e) {
                }
            } else {
                // pas préchargée en local pour l'instant, cherche sur internet

                if (reseauOutils.isTelechargementsModeConnectePossible()) {

                    Log.d(LOG_TAG, "addFoldableView() - entry.getCleURLPhotoParticipant() : " + entry.getPhotoNom());
                    Log.d(LOG_TAG, "getView URL Vignette Image : " +
                            entry.getPhotoNom().replaceAll(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.VIGNETTE_BASE_URL_SUFFIXE));

                    Picasso.get()
                            .load(Constants.IMAGE_BASE_URL + "/" + entry.getPhotoNom().replaceAll(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.VIGNETTE_BASE_URL_SUFFIXE))
                            .placeholder(R.drawable.app_ic_participant)  // utilisation de l'image par defaut pour commencer
                            .resize(defaultIconSize, defaultIconSize)
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
                                                    .placeholder(R.drawable.app_ic_participant)  // utilisation de l'image par defaut pour commencer
                                                    .resize(defaultIconSize, defaultIconSize)
                                                    .centerInside()
                                                    .error(R.drawable.doris_icone_doris_large_pas_connecte)
                                                    .into(trombineView);
                                        }

                                    });

                } else {

                    Picasso.get()
                            .load(Constants.IMAGE_BASE_URL + "/" + entry.getPhotoNom().replaceAll(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.VIGNETTE_BASE_URL_SUFFIXE))
                            .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                            .placeholder(R.drawable.app_ic_participant)  // utilisation de l'image par defaut pour commencer
                            .resize(defaultIconSize, defaultIconSize)
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
                                                    .resize(defaultIconSize, defaultIconSize)
                                                    .centerInside()
                                                    .error(R.drawable.doris_icone_doris_large_pas_connecte)
                                                    .into(trombineView);
                                        }

                                    });
                }
            }
        } else {
            // remet l'image par défaut (nécessaire à cause de recyclage des widgets)
            trombineView.setImageResource(R.drawable.app_ic_participant_small);
        }
        // End of user code

        return convertView;

    }

    protected View getNoResultSubstitute(View convertView) {
        TextView tvLabel = (TextView) convertView.findViewById(R.id.listeparticipantavecfiltre_listviewrow_label);
        tvLabel.setText(R.string.listeparticipantavecfiltre_classlistview_no_result);
        // Start of user code protected additional ListeParticipantAvecFiltre_Adapter getNoResultSubstitute code

        StringBuilder sbRechercheCourante = new StringBuilder();

        // TODO ajouter le filtre textuel courant qui lui aussi peut impliquer de ne retourner aucun résultats
        TextView tvDetails = (TextView) convertView.findViewById(R.id.listeparticipantavecfiltre_listviewrow_details);
        tvDetails.setText(sbRechercheCourante.toString());

        // End of user code
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.listeparticipantavecfiltre_listviewrow_icon);
        ivIcon.setImageResource(R.drawable.app_ic_launcher);
        return convertView;
    }


    //Start of user code protected additional ListeParticipantAvecFiltre_Adapter methods
    // additional methods
    //End of user code
    protected boolean sortAfterFilter() {
        return false;
    }

    public int filter(int position, Participant entry, String pattern) {
        // Start of user code protected additional ListeParticipantAvecFiltre_Adapter filter code
        StringBuilder labelSB = new StringBuilder();
        labelSB.append(entry.getNom());
        labelSB.append(" ");
        if (labelSB.toString().toLowerCase(Locale.FRENCH).contains(pattern)) return 1;
        else return -1;
        // End of user code
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SimpleFilter();
        }
        return mFilter;
    }

    private class SimpleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

			/*if (ficheList == null) {
				synchronized (mLock) {
					ficheList = new ArrayList<Fiches>(mObjects);
				}
			}*/

            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    ArrayList<Participant> list = new ArrayList<Participant>(participantList);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                // Start of user code protected ListeParticipantAvecFiltre_Adapter filter prefix customisation
                String prefixString = prefix.toString().toLowerCase(Locale.FRENCH);
                // End of user code
                boolean sort = sortAfterFilter();
                final List<Participant> values = participantList;
                final int count = values.size();

                final ArrayList<Participant> newValues = new ArrayList<Participant>(count);
                final int[] orders = sort ? new int[count] : null;

                for (int i = 0; i < count; i++) {
                    final Participant value = values.get(i);
                    int order = ListeParticipantAvecFiltre_Adapter.this.filter(i, value, prefixString);
                    if (order >= 0) {
                        if (sort)
                            orders[newValues.size()] = order;
                        newValues.add(value);
                    }
                }
				/* TODO implement a sort
				if (sort) {
					Comparator<Participant> c = new Comparator<Participant>() {
						public int compare(Participant object1, Participant object2) {
							// Start of user code protected additional ListeParticipantAvecFiltre_Adapter compare code
							int i1 = newValues.indexOf(object1);
							int i2 = newValues.indexOf(object2);
							return orders[i1] - orders[i2];
							// End of user code
						}
					};
					Collections.sort(newValues, c);
				}
				*/
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (results.count > 0) {
                filteredParticipantList = (List<Participant>) results.values;
                notifyDataSetChanged();
            } else {
                filteredParticipantList = new ArrayList<Participant>();
                notifyDataSetInvalidated();
            }
            // update hashmap for index
            ((ActivityWithIndexBar) context).populateIndexBarHashMap();
        }
    }
}
