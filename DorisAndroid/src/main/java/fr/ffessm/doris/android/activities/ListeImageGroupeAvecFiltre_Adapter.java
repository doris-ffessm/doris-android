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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.indexbar.ActivityWithIndexBar;
import fr.ffessm.doris.android.activities.view.indexbar.FicheAlphabeticalIndexManager;
import fr.ffessm.doris.android.activities.view.indexbar.GroupeIndexManager;
import fr.ffessm.doris.android.activities.view.indexbar.GroupeListProvider;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Fiches_Outils;
import fr.ffessm.doris.android.tools.Groupes_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.Textes_Outils;

//End of user code

public class ListeImageGroupeAvecFiltre_Adapter extends BaseAdapter implements Filterable {

    private Context context;

    /**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
     */
    protected DorisDBHelper _contextDB;

    private static final String LOG_TAG = ListeImageGroupeAvecFiltre_Adapter.class.getCanonicalName();

    private List<Integer> groupeIdList;
    public List<Integer> filteredGroupeIdList;
    private final Object mLock = new Object();
    private SimpleFilter mFilter;
    SharedPreferences prefs;
    //Start of user code protected additional ListeImageFicheAvecFiltre_Adapter attributes
    // additional attributes

    protected Param_Outils paramOutils;
    protected Reseau_Outils reseauOutils;
    protected Photos_Outils photosOutils;
    protected Fiches_Outils fichesOutils;
    protected Textes_Outils textesOutils;
    protected Common_Outils commonOutils = new Common_Outils();

    // vide signifie que l'on accepte tout
    protected ArrayList<Integer> acceptedGroupeId = new ArrayList<>();
    int filteredZoneGeoId = -1;
    int filteredGroupeId = 1;

    protected Fiches_Outils.OrdreTriAlphabetique ordreTriAlphabetique = Fiches_Outils.OrdreTriAlphabetique.NOMCOMMUN;

    public ListeImageGroupeAvecFiltre_Adapter(Context context, DorisDBHelper contextDB, int filteredZoneGeoId, int filteredGroupeId) {
        super();
        this.context = context;
        this._contextDB = contextDB;
        this.filteredZoneGeoId = filteredZoneGeoId;
        this.filteredGroupeId = filteredGroupeId;

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        paramOutils = new Param_Outils(context);
        reseauOutils = new Reseau_Outils(context);
        photosOutils = new Photos_Outils(context);
        fichesOutils = new Fiches_Outils(context);
        textesOutils = new Textes_Outils(context);
        ordreTriAlphabetique = fichesOutils.getOrdreTriAlphabetique(context);

        updateList();
    }

    protected void updateList() {
        // Start of user code protected ListeImageFicheAvecFiltre_Adapter updateList

        // TODO : Bizarre que ce soit passé ainsi ....
        int filtreGroupe = prefs.getInt(context.getString(R.string.pref_key_filtre_groupe), Groupes_Outils.getGroupeRoot(_contextDB).getId());


        this.filteredGroupeIdList = GroupeListProvider.getFilteredGroupeIdList(_contextDB, filtreGroupe);
        this.filteredGroupeIdList.remove(0); // remove the "racine" groupe
        this.groupeIdList = GroupeListProvider.getAllGroupeIdList(_contextDB);
        this.groupeIdList.remove(0); // remove the "racine" groupe
        // End of user code
    }

    @Override
    public int getCount() {
        if (filteredGroupeIdList.size() == 0) {
            return 1;    // will create a dummy entry to invite changing the filters
        }
        return filteredGroupeIdList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredGroupeIdList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // Start of user code protected additional ListeFicheAvecFiltre_Adapter getView_assign code
        //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listeimagegroupeavecfiltre_listviewrow, null);
        //}
        if (filteredGroupeIdList.size() == 0) {
            return getNoResultSubstitute(convertView);
        }
        GroupeIndexManager indexHelper = new GroupeIndexManager(context, _contextDB, filteredGroupeId );
        final Groupe groupeEntry = indexHelper.getItemForId(filteredGroupeIdList.get(position));
        if (groupeEntry == null) return convertView;

        TextView tvDetails = convertView.findViewById(R.id.listeimageavecfiltre_listviewrow_details);
        tvDetails.setVisibility(View.GONE);

        TextView tvLabel = convertView.findViewById(R.id.listeimagegroupeavecfiltre_listviewrow_label);
        tvLabel.setText(groupeEntry.getNomGroupe());

        // assign group color in background
        int[] colors = {groupeEntry.getCouleurGroupe(), Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradientDrawable.setColors(colors);
        convertView.setBackground(gradientDrawable);
        // End of user code

        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = convertView.findViewById(R.id.listeimagegroupeavecfiltre_listviewrow);
        llRow.setTag(groupeEntry);

        // Start of user code protected additional ListeFicheAvecFiltre_Adapter getView code
        //	additional code
        HorizontalScrollView hsGallery = convertView.findViewById(R.id.listeimagegroupeavecfiltre_elementview_gallery_horizontalScrollView);
        hsGallery.setVisibility(View.VISIBLE);


        // get Fiche and associated photo relevant for the given groupe

        List<Integer> groupeficheIdList = fichesOutils.getListeIdFichesFiltrees(context, _contextDB, filteredZoneGeoId, groupeEntry.getId(), false);

        //Collection<PhotoFiche> photosFiche = entry.getPhotosFiche();
        //if (photosFiche != null) {
        if(!groupeficheIdList.isEmpty()) {
            //sbDebugText.append("\nnbPhoto="+photosFiche.size()+"\n");

            LinearLayout photoGallery = convertView.findViewById(R.id.listeimagegroupeavecfiltre_elementview_photogallery);
            photoGallery.removeAllViews();

            int pos = 0;
            for (Integer ficheID : groupeficheIdList) {

                RuntimeExceptionDao<Fiche, Integer> ficheDao = ((ListeImageGroupeAvecFiltre_ClassListViewActivity) context).getHelper().getFicheDao();

                Fiche fiche = ficheDao.queryForId(ficheID);
                fiche.setContextDB(_contextDB);
                ficheDao.refresh(fiche);
                PhotoFiche photoPrincipale = fiche.getPhotoPrincipale();
                if(photoPrincipale != null && photoPrincipale.getCleURL() != null) {
                    View photoView = insertPhoto(photoPrincipale);

                    photoView.setPadding(0, 0, 2, 0);
                    photoGallery.addView(photoView);

                    photoGallery.setClickable(true);
                    View.OnClickListener ficheLauncher = v -> {
                        Log.d(LOG_TAG, "ListeImageFicheAvecFiltre_Adapter - onClick");

                        DorisApplicationContext.getInstance().setIntentPourRetour(((Activity) context).getIntent());

                        Intent toDetailView = new Intent(context, DetailsFiche_ElementViewActivity.class);
                        toDetailView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle b = new Bundle();
                        b.putInt("ficheId", ficheID);
                        toDetailView.putExtras(b);
                        context.getApplicationContext().startActivity(toDetailView);
                    };

                    photoView.setOnClickListener(ficheLauncher);


                    final int posImageCourante = pos;
                    /*View.OnLongClickListener photoLauncher = v -> {
                        Log.d(LOG_TAG, "ListeImageFicheAvecFiltre_Adapter - onLongClick");

                        DorisApplicationContext.getInstance().setIntentPourRetour(((Activity) context).getIntent());

                        Intent toImageView = new Intent(context, ImagePleinEcran_CustomViewActivity.class);
                        toImageView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        toImageView.putExtra("position", posImageCourante);
                        toImageView.putExtra("ficheId", entry.getId());
                        context.startActivity(toImageView);

                        return true;
                    };

                    photoView.setOnLongClickListener(photoLauncher);

                     */

                    pos++;
                }

            }

        }


        // End of user code

        return convertView;

    }

    protected View getNoResultSubstitute(View convertView) {
        TextView tvLabel = convertView.findViewById(R.id.listeimagegroupeavecfiltre_listviewrow_label);
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
            TextView tvDetails = convertView.findViewById(R.id.listeimageavecfiltre_listviewrow_details);
            tvDetails.setText(sbRechercheCourante.toString());
            tvDetails.setVisibility(View.VISIBLE);

            HorizontalScrollView hsGallery = convertView.findViewById(R.id.listeimagegroupeavecfiltre_elementview_gallery_horizontalScrollView);
            hsGallery.setVisibility(View.GONE);
        } catch (SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        // End of user code

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
            if (filteredGroupeIdList.size() > 0) {
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

    public int filter(int position, Groupe entry, String pattern) {
        // always filter groupe rcine
        if(entry.getNomGroupe().equals("racine")) {
            return -1;
        }
        // Start of user code protected additional ListeFicheAvecFiltre_Adapter filter code
        // chercher séparement les mots (séparés par un blanc) et faire un "ET"
        String[] patterns = pattern.split(" ");
        boolean isValid = true;
        for (String patt : patterns) {
            if (patt.isEmpty()) continue; // en cas de blanc multiples
            if (patt.equals("*"))
                break;  // accepte tout; aussi utilisé pour le filtre en retour de sélection de filtre
            // DescriptionGroupe already contains NomGroupe
            String lookuptxt = (commonOutils.formatStringNormalizer(entry.getDescriptionGroupe() )).toLowerCase(Locale.FRENCH).trim();
            if (lookuptxt.contains(patt) )
                continue;
            //if (entry.getTextePourRechercheRapide().contains(patt))
            //    continue;
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
                    ArrayList<Integer> list = new ArrayList<>(groupeIdList);
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
                final List<Integer> values = groupeIdList;
                final int count = values.size();

                final ArrayList<Integer> newValues = new ArrayList<>(count);
                final int[] orders = sort ? new int[count] : null;

                GroupeIndexManager indexHelper = new GroupeIndexManager(context, _contextDB, filteredGroupeId );
                for (int i = 0; i < count; i++) {
                    final Integer valueId = values.get(i);
                    Groupe value = indexHelper.getItemForId(valueId);
                    if (value != null) {
                        int order = ListeImageGroupeAvecFiltre_Adapter.this.filter(i, value, prefixString);
                        if (order >= 0) {
                            if (sort)
                                orders[newValues.size()] = order;
                            newValues.add(valueId);
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                filteredGroupeIdList = (List<Integer>) results.values;
                notifyDataSetChanged();
            } else {
                filteredGroupeIdList = new ArrayList<>();
                notifyDataSetInvalidated();
            }
            // update hashmap for index
            ((ActivityWithIndexBar) context).populateIndexBarHashMap();
        }
    }

    View insertPhoto(PhotoFiche photoFiche) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int iconSize = ScreenTools.dp2px(context, paramOutils.getParamInt(R.string.pref_key_list_icone_taille, Integer.parseInt(context.getString(R.string.list_icone_taille_defaut))));

        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new LayoutParams(iconSize, iconSize));
        layout.setGravity(Gravity.CENTER);

        final ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LayoutParams(iconSize, iconSize));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);


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
                        .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                        .error(R.drawable.doris_icone_doris_large_pas_connecte)
                        .fit()
                        .centerInside()
                        .into(imageView);
            } catch (IOException e) {
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

}
