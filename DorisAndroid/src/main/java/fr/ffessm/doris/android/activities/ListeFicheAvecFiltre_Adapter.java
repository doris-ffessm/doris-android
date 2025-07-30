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
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.activities.view.indexbar.FicheAlphabeticalIndexManager;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

//Start of user code protected additional ListeFicheAvecFiltre_Adapter imports
// additional imports

import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.Groupes_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.Textes_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;

//End of user code

public class ListeFicheAvecFiltre_Adapter extends BaseAdapter implements Filterable {

    private Context context;

    /**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
     */
    protected DorisDBHelper _contextDB;

    private static final String LOG_TAG = ListeFicheAvecFiltre_Adapter.class.getCanonicalName();

    private List<Integer> ficheIdList;
    public List<Integer> filteredFicheIdList;
    private final Object mLock = new Object();
    private SimpleFilter mFilter;
    SharedPreferences prefs;
    //Start of user code protected additional ListeFicheAvecFiltre_Adapter attributes
    // additional attributes

    protected Groupe filtreGroupe;
    protected Param_Outils paramOutils;
    protected Photos_Outils photosOutils;
    protected Reseau_Outils reseauOutils;
    protected Fiches_Outils fichesOutils;
    protected Textes_Outils textesOutils;

    // vide signifie que l'on accepte tout
    protected ArrayList<Integer> acceptedGroupeId = new ArrayList<>();
    int filteredZoneGeoId = -1;
    int filteredGroupeId = 1;


    protected Fiches_Outils.OrdreTriAlphabetique ordreTriAlphabetique = Fiches_Outils.OrdreTriAlphabetique.NOMCOMMUN;

    public ListeFicheAvecFiltre_Adapter(Context context, DorisDBHelper contextDB, int filteredZoneGeoId) {
        super();
        this.context = context;
        this._contextDB = contextDB;
        this.filteredZoneGeoId = filteredZoneGeoId;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        paramOutils = new Param_Outils(context);
        reseauOutils = new Reseau_Outils(context);
        photosOutils = new Photos_Outils(context);
        fichesOutils = new Fiches_Outils(context);
        textesOutils = new Textes_Outils(context);
        ordreTriAlphabetique = fichesOutils.getOrdreTriAlphabetique(context);
        updateList();
    }
    //End of user code

    public ListeFicheAvecFiltre_Adapter(Context context, DorisDBHelper contextDB) {
        super();
        this.context = context;
        this._contextDB = contextDB;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Start of user code protected ListeFicheAvecFiltre_Adapter constructor

        paramOutils = new Param_Outils(context);
        reseauOutils = new Reseau_Outils(context);
        photosOutils = new Photos_Outils(context);
        fichesOutils = new Fiches_Outils(context);
        textesOutils = new Textes_Outils(context);
        ordreTriAlphabetique = fichesOutils.getOrdreTriAlphabetique(context);
        // End of user code
        updateList();
    }

    protected void updateList() {
        // Start of user code protected ListeFicheAvecFiltre_Adapter updateList

        // TODO : Bizarre que ce soit passé ainsi ....
        int filtreGroupe = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), Groupes_Outils.getGroupeRoot(_contextDB).getId());

        this.filteredFicheIdList = fichesOutils.getListeIdFichesFiltrees(context, _contextDB, filteredZoneGeoId, filtreGroupe);
        this.ficheIdList = fichesOutils.getListeIdFiches();
        // End of user code
    }

    @Override
    public int getCount() {
        if (filteredFicheIdList.size() == 0) {
            return 1;    // will create a dummy entry to invite changing the filters
        }
        return filteredFicheIdList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredFicheIdList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // Start of user code protected additional ListeFicheAvecFiltre_Adapter getView_assign code
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listeficheavecfiltre_listviewrow, null);
        }
        if (filteredFicheIdList.size() == 0) {
            return getNoResultSubstitute(convertView);
        }

        FicheAlphabeticalIndexManager indexHelper = new FicheAlphabeticalIndexManager(context, _contextDB );
        final Fiche entry = indexHelper.getItemForId(filteredFicheIdList.get(position));
        if (entry == null) return convertView;

        // set data in the row
        TextView tvLabel = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_label);
        switch (ordreTriAlphabetique) {
            case NOMSCIENTIFIQUE:
                tvLabel.setText(textesOutils.textToSpannableStringDoris(entry.getNomScientifique()));
                break;
            case NOMCOMMUN:
            default:
                tvLabel.setText(entry.getNomCommunNeverEmpty() + " ");
                break;
        }

        // assign group color in background
        int[] colors = {entry.getGroupe().getCouleurGroupe(), Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradientDrawable.setColors(colors);
        convertView.setBackground(gradientDrawable);
        // End of user code

        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow);
        llRow.setTag(entry);

        // Start of user code protected additional ListeFicheAvecFiltre_Adapter getView code
        //	additional code
        TextView tvDetails = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_details);
        switch (ordreTriAlphabetique) {
            case NOMSCIENTIFIQUE:
                tvDetails.setText(entry.getNomCommunNeverEmpty());
                break;
            case NOMCOMMUN:
            default:
                tvDetails.setText(textesOutils.textToSpannableStringDoris(entry.getNomScientifique()));
                break;
        }

        int defaultIconSize = paramOutils.getParamInt(R.string.pref_key_list_icone_taille, Integer.parseInt(context.getString(R.string.list_icone_taille_defaut)));
        final ImageView ivIcon = (ImageView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_icon);
        ivIcon.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        ivIcon.getLayoutParams().width = ScreenTools.dp2px(context, defaultIconSize);

        PhotoFiche photoPrincipale = entry.getPhotoPrincipale();
        if (photoPrincipale == null) {
            //try {
            Log.w(LOG_TAG, "bizarre photoprincipale=" + photoPrincipale + " application d'un workaround temporaire");
            //((ListeFicheAvecFiltre_ClassListViewActivity)context).getHelper().getFicheDao()
            Fiche fiche = ((ListeFicheAvecFiltre_ClassListViewActivity) context).getHelper().getFicheDao().queryForId(entry.getId());
            fiche.setContextDB(_contextDB);
            ((ListeFicheAvecFiltre_ClassListViewActivity) context).getHelper().getFicheDao().refresh(fiche);
            //fiche.
            photoPrincipale = fiche.getPhotoPrincipale();
            //_contextDB.ficheDao.refresh(entry);
			/*} catch (SQLException e1) {
				Log.e(LOG_TAG, e1.getMessage(),e1);
			}*/
        }

        //Log.d(LOG_TAG, "getView photoPrincipale.getCleURL() = " + photoPrincipale.getCleURL());

        if (photoPrincipale != null && photoPrincipale.getCleURL() != null) {

            photoPrincipale.setContextDB(_contextDB);

            //Log.d(LOG_TAG, "getView photoprincipale="+photoPrincipale.getCleURL());


            final ImageType bestLocallyAvailableRes;
            if (photosOutils.isAvailableInFolderPhoto(photoPrincipale.getCleURLNomFichier(), ImageType.VIGNETTE)) { // utilise le format vignette en priorité, fallback sur les autres
                bestLocallyAvailableRes = ImageType.VIGNETTE;
            } else if (photosOutils.isAvailableInFolderPhoto(photoPrincipale.getCleURLNomFichier(), ImageType.MED_RES)) {
                bestLocallyAvailableRes = ImageType.MED_RES;
            } else if (photosOutils.isAvailableInFolderPhoto(photoPrincipale.getCleURLNomFichier(), ImageType.HI_RES)) {
                bestLocallyAvailableRes = ImageType.HI_RES;
            } else {
                bestLocallyAvailableRes = null;
            }
            ImageType requestedRes = ImageType.VIGNETTE;
            String small_suffixe_photo = Constants.GRANDE_BASE_URL_SUFFIXE;
            if (!photoPrincipale.getImgPostfixCodes().isEmpty() && photoPrincipale.getImgPostfixCodes().contains("&")) {
                // !! split -1 car https://stackoverflow.com/questions/14602062/java-string-split-removed-empty-values
                String[] imgPostfixCodes = photoPrincipale.getImgPostfixCodes().split("&", -1);
                if (!imgPostfixCodes[0].isEmpty()) {
                    small_suffixe_photo = Constants.ImagePostFixCode.getEnumFromCode(imgPostfixCodes[0]).getPostFix();
                }
            }
            String requested_suffixe_photo = small_suffixe_photo;
            if (bestLocallyAvailableRes != null) {
                // on a une image en local, on l'utilise
                try {
                    Picasso.get().load(photosOutils.getPhotoFile(photoPrincipale.getCleURLNomFichier(), bestLocallyAvailableRes))
                            .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                            .error(R.drawable.doris_icone_doris_large_pas_connecte)
                            .fit()
                            .centerInside()
                            .into(ivIcon);
                } catch (IOException e) {
                    Log.w(LOG_TAG, "pb loading " + photoPrincipale.getCleURLNomFichier() + " in quality " + bestLocallyAvailableRes.name());
                }
            } else {
                // pas préchargée en local pour l'instant, cherche sur internet si c'est autorisé
                if (reseauOutils.isTelechargementsModeConnectePossible()) {
                    Picasso.get()
                            .load(Constants.IMAGE_BASE_URL + "/"
                                    + photoPrincipale.getCleURL().replaceAll(
                                    Constants.IMAGE_BASE_URL_SUFFIXE + "$", requested_suffixe_photo))
                            .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                            .error(R.drawable.doris_icone_doris_large_pas_connecte)
                            .fit()
                            .centerInside()
                            .into(ivIcon);
                } else {
                    Picasso.get()
                            .load(Constants.IMAGE_BASE_URL + "/"
                                    + photoPrincipale.getCleURL().replaceAll(
                                    Constants.IMAGE_BASE_URL_SUFFIXE + "$", requested_suffixe_photo))
                            .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                            .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                            .error(R.drawable.doris_icone_doris_large_pas_connecte)
                            .fit()
                            .centerInside()
                            .into(ivIcon);
                }
            }
        } else {
            // remet l'icone de base
            ivIcon.setImageResource(R.drawable.app_ic_launcher);
        }

        TextView btnEtatFiche = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow__btnEtatFiche);
        switch (entry.getEtatFiche()) {
            case 1:
            case 2:
            case 3:
                btnEtatFiche.setVisibility(View.VISIBLE);
                btnEtatFiche.setText(" R ");
                btnEtatFiche.setOnClickListener(v -> Toast.makeText(context, R.string.ficheredaction_explications, Toast.LENGTH_LONG).show());
                break;
            case 5:
                btnEtatFiche.setVisibility(View.VISIBLE);
                btnEtatFiche.setText(" P ");
                btnEtatFiche.setOnClickListener(v -> Toast.makeText(context, R.string.ficheproposee_explications, Toast.LENGTH_LONG).show());
                break;
            case 4:
                btnEtatFiche.setVisibility(View.GONE);

                break;
            default:
                btnEtatFiche.setVisibility(View.VISIBLE);
                btnEtatFiche.setText(" " + entry.getEtatFiche() + " ");
        }

        // End of user code

        return convertView;

    }

    protected View getNoResultSubstitute(View convertView) {
        TextView tvLabel = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_label);
        tvLabel.setText(R.string.listeficheavecfiltre_classlistview_no_result);
        // Start of user code protected additional ListeFicheAvecFiltre_Adapter getNoResultSubstitute code
        try {
            StringBuilder sbRechercheCourante = new StringBuilder();
            int groupRootId = Groupes_Outils.getGroupeRoot(_contextDB).getId();
            int filtreCourantId = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), groupRootId);
            if (filtreCourantId == groupRootId) {
                sbRechercheCourante.append(context.getString(R.string.accueil_recherche_precedente_filtreEspece_sans));
            } else {
                Groupe groupeFiltreCourant = _contextDB.groupeDao.queryForId(filtreCourantId);
                sbRechercheCourante.append(context.getString(R.string.listeficheavecfiltre_popup_filtreEspece_avec) + " " + groupeFiltreCourant.getNomGroupe().trim());
            }
            sbRechercheCourante.append(" ; ");
            int currentFilterId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
            if (currentFilterId == -1 || currentFilterId == 0) { // test sur 0, juste pour assurer la migration depuis alpha3 , a supprimer plus tard
                sbRechercheCourante.append(context.getString(R.string.accueil_recherche_precedente_filtreGeographique_sans));
            } else {
                ZoneGeographique currentZoneFilter = _contextDB.zoneGeographiqueDao.queryForId(currentFilterId);
                sbRechercheCourante.append(context.getString(R.string.listeficheavecfiltre_popup_filtreGeographique_avec) + " " + currentZoneFilter.getNom().trim());
            }
            // TODO ajouter le filtre textuel courant qui lui aussi peut impliquer de ne retourner aucun résultats
            TextView tvDetails = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_details);
            tvDetails.setText(sbRechercheCourante.toString());
        } catch (SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        // End of user code
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_icon);
        ivIcon.setImageResource(R.drawable.app_ic_launcher);
        return convertView;
    }




    //Start of user code protected additional ListeFicheAvecFiltre_Adapter methods
    // additional methods
    public void refreshFilter() {

        int oldFilteredZoneGeoId = filteredZoneGeoId;
        filteredZoneGeoId = prefs.getInt(context.getString(R.string.pref_key_filtre_zonegeo), -1);
        int oldFilteredGroupeId = filteredGroupeId;
        filteredGroupeId = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), Groupes_Outils.getGroupeRoot(_contextDB).getId());
        if ((oldFilteredZoneGeoId != filteredZoneGeoId) | (oldFilteredGroupeId != filteredGroupeId)) {
            //need full query
            updateList();
            if (filteredFicheIdList.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    //End of user code
    protected boolean sortAfterFilter() {
        return false;
    }

    public int filter(int position, Fiche entry, String pattern) {
        // Start of user code protected additional ListeFicheAvecFiltre_Adapter filter code
        // chercher séparement les mots (séparés par un blanc) et faire un "ET"
        String[] patterns = pattern.split(" ");
        boolean isValid = true;
        for (String patt : patterns) {
            if (patt.isEmpty()) continue; // en cas de blanc multiples
            if (patt.equals("*"))
                break;  // accepte tout; aussi utilisé pour le filtre en retour de sélection de filtre
            if (entry.getTextePourRechercheRapide().contains(patt))
                continue;
            else isValid = false;
        }
        if (isValid) return 1;
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
                    ArrayList<Integer> list = new ArrayList<>(ficheIdList);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                // Start of user code protected ListeFicheAvecFiltre_Adapter filter prefix customisation
                Common_Outils commonOutils = new Common_Outils();
                final String prefixString = commonOutils.formatStringNormalizer(prefix.toString().toLowerCase(Locale.FRENCH));
                //
                // End of user code
                boolean sort = sortAfterFilter();
                final List<Integer> values = ficheIdList;
                final int count = values.size();

                final ArrayList<Integer> newValues = new ArrayList<>(count);
                final int[] orders = sort ? new int[count] : null;
                FicheAlphabeticalIndexManager indexHelper = new FicheAlphabeticalIndexManager(context, _contextDB );
                for (int i = 0; i < count; i++) {
                    final Integer valueId = values.get(i);

                    Fiche value = indexHelper.getItemForId(valueId);
                    if (value != null) {
                        int order = ListeFicheAvecFiltre_Adapter.this.filter(i, value, prefixString);
                        if (order >= 0) {
                            if (sort)
                                orders[newValues.size()] = order;
                            newValues.add(valueId);
                        }
                    }
                }
				/* TODO implement a sort
				if (sort) {
					Comparator<Fiche> c = new Comparator<Fiche>() {
						public int compare(Fiche object1, Fiche object2) {
							// Start of user code protected additional ListeFicheAvecFiltre_Adapter compare code
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
                filteredFicheIdList = (List<Integer>) results.values;
                notifyDataSetChanged();
            } else {
                filteredFicheIdList = new ArrayList<Integer>();
                notifyDataSetInvalidated();
            }
            // update hashmap for index
            ((ActivityWithIndexBar) context).populateIndexBarHashMap();
        }
    }
}
