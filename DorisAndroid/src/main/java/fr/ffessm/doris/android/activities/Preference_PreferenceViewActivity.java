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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.ffessm.doris.android.R;

//Start of user code Preference preference activity additional imports

import android.content.Context;
import android.os.AsyncTask.Status;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import android.util.Log;

import androidx.core.view.WindowCompat;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.List;

import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.App_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.android.tools.ThemeUtil;

//End of user code

public class Preference_PreferenceViewActivity extends android.preference.PreferenceActivity {


    //Start of user code Preference preference activity additional attributes
    private static final String LOG_TAG = Preference_PreferenceViewActivity.class.getCanonicalName();
    final Context context = this;

    private Param_Outils paramOutils;
    private Photos_Outils photosOutils;
    private Disque_Outils disqueOutils;

    long volumeTotalNecessaire = 0;
    //End of user code

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);
        addPreferencesFromResource(R.xml.preference);
        //Start of user code Preference preference activity additional onCreate

        // Si téléchargements en tâche de fond, il est arrêté
        TelechargePhotosAsync_BgActivity telechargePhotosFiches_BgActivity = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;
        if (telechargePhotosFiches_BgActivity != null && telechargePhotosFiches_BgActivity.getStatus() == Status.RUNNING) {
            Toast.makeText(this, R.string.bg_notifToast_arretTelecharg, Toast.LENGTH_LONG).show();
            DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);
        }

        /* Permet d'afficher directement une sous-partie des préférences
         *  Utile depuis Aide ou EtatHorsLigne, etc.
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String typeParam = bundle.getString("type_parametre");
            String param = bundle.getString("parametre");

            if (typeParam != null) {
                //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - typeParam : "+typeParam);
                //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - param : "+param);
                PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference(typeParam);
                if (param != null) {
                    int pos = findPreference(param).getOrder();
                    //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - pos "+param+" : "+pos);
                    preferenceScreen.onItemClick(null, null, pos, 0);
                }
            }
        }

        // Affichage Estimation des Volumes pris par Choix des qualités de photos
        getPhotosOutils().initNbPhotosParFiche();

        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_france,
                ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_atlantne,
                ZoneGeographiqueKind.FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_mediter,
                ZoneGeographiqueKind.FAUNE_FLORE_MEDITERRANEE_FRANCAISE);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_eaudouce,
                ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_atlantno,
                ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_indopac,
                ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_antarctique,
                ZoneGeographiqueKind.FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_merrouge,
                ZoneGeographiqueKind.FAUNE_FLORE_MER_ROUGE);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_caraibes,
                ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES);
        setLibelleModePrechargPhotoZone(R.string.pref_key_mode_precharg_photo_region_guyanne,
                ZoneGeographiqueKind.FAUNE_FLORE_GUYANNE);

        final Preference btnQualiteImagesZonesKey = (Preference) getPreferenceManager().findPreference("button_qualite_images_zones_key");
        btnQualiteImagesZonesKey.setSummary(
                context.getString(R.string.mode_precharg_photo_region_summary) + getDisqueOutils().getHumanDiskUsage(volumeTotalNecessaire)
        );

        final Preference btnAutresImagesKey = (Preference) getPreferenceManager().findPreference(
                context.getString(R.string.pref_key_mode_precharg_photo_autres));
        btnAutresImagesKey.setSummary(
                context.getString(R.string.mode_precharg_photo_autres_summary)
                        .replace("@size", getDisqueOutils().getHumanDiskUsage(getPhotosOutils().getEstimVolPhotosAutres()))
        );

        // link to EtatModeHorsLigne and compute summary
        Preference button = findPreference(getString(R.string.pref_key_precharg_status));
        ZoneGeographique zoneToutesZones = new ZoneGeographique();
        zoneToutesZones.setId(-1);
        zoneToutesZones.setNom(this.getString(R.string.avancement_touteszones_titre));

        StringBuilder sb = new StringBuilder();
        sb.append(getPhotosOutils().getCurrentPhotosDiskUsageShortSummary(this));
        sb.append("; ");

        button.setSummary(EtatModeHorsLigne_CustomViewActivity.updateProgressBarZone(this, zoneToutesZones, null, sb.toString()));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(context, EtatModeHorsLigne_CustomViewActivity.class));
                return true;
            }
        });

        //End of user code
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Start of user code Preference preference activity additional onResume
        //End of user code
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Start of user code preference specific menu definition
        // menu.add(Menu.NONE, 0, 0, "Back to main menu");


        //End of user code
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Start of user code preference specific menu action
    	
    	/*
    	String message = ""+item.getItemId()+" - "+item.getGroupId()+" - "+item.toString();
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onOptionsItemSelected() - menu : "+message);  
    	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    	*/
    	
        /* switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, AndroidDiveManagerMainActivity.class));
                return true;
        } */
        //End of user code
        return false;
    }


    //Start of user code Preference preference activity additional operations

    @Override
    protected void onPause() {
        super.onPause();
        //Log.d(LOG_TAG, "onPause()");

        // Maj Nb Photos à télécharger
        OrmLiteDBHelper ormLiteDBHelper = new OrmLiteDBHelper(this);
        List<ZoneGeographique> listeZoneGeo = ormLiteDBHelper.getZoneGeographiqueDao().queryForAll();
        for (ZoneGeographique zoneGeo : listeZoneGeo) {
            getPhotosOutils().setAPrecharQteParZoneGeo(zoneGeo, true);
            getPhotosOutils().setAPrecharQteParZoneGeo(zoneGeo, false);
        }
    }

    private void setLibelleModePrechargPhotoZone(int refPreference, ZoneGeographiqueKind zoneGeoKind) {
        CharSequence summary = "";

        ListPreference lp = (ListPreference) findPreference(context.getString(refPreference));

        CharSequence[] entries = lp.getEntries();
        CharSequence[] entryValues = lp.getEntryValues();

        for (int i = 0; i < entries.length; i++) {
            long volumeNecessaire = getPhotosOutils().getEstimVolPhotosParZone(
                    Photos_Outils.PrecharMode.valueOf(entryValues[i].toString()),
                    zoneGeoKind
            );
            entries[i] = entries[i].toString().replace("@size", getDisqueOutils().getHumanDiskUsage(volumeNecessaire));

            if (entryValues[i].toString().equals(lp.getValue())) {
                summary = entries[i];
                volumeTotalNecessaire += volumeNecessaire;
            }

        }

        lp.setEntries(entries);
        lp.setEntryValues(entryValues);
        lp.setSummary(summary);
    }

    private Photos_Outils getPhotosOutils() {
        if (photosOutils == null) photosOutils = new Photos_Outils(context);
        return photosOutils;
    }

    private Param_Outils getParamOutils() {
        if (paramOutils == null) paramOutils = new Param_Outils(context);
        return paramOutils;
    }

    private Disque_Outils getDisqueOutils() {
        if (disqueOutils == null) disqueOutils = new Disque_Outils(context);
        return disqueOutils;
    }

    //End of user code
}
