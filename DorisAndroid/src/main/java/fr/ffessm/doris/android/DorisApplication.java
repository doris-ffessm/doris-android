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
package fr.ffessm.doris.android;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;
import org.acra.annotation.AcraToast;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.config.ToastConfigurationBuilder;
import org.acra.data.StringFormat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import fr.ffessm.doris.android.tools.SortModesTools;

public class DorisApplication extends Application {

	private static final String LOG_TAG = DorisApplication.class.getSimpleName();

	@Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - Début");

        // upgrade mode affichage if required
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String current_mode = prefs.getString(getResources().getString(
                R.string.pref_key_current_mode_affichage),
                getResources().getString(
                        R.string.current_mode_affichage_default));
        if(SortModesTools.getLabelMap(this).get(current_mode) == null) {
            prefs.edit().putString(getResources().getString(
                    R.string.pref_key_current_mode_affichage),
                    getResources().getString(
                            R.string.current_mode_affichage_default)).apply();
        }

        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "onCreate() - Fin");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
        //core configuration:
        builder
                .withBuildConfigClass(BuildConfig.class)
                .withLogcatArguments("-t", "200", "-v", "time")
                .withReportFormat(StringFormat.JSON);
        //each plugin you chose above can be configured with its builder like this:
        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class)
                .withResText(R.string.crash_toast_text)
                //make sure to enable all plugins you want to use:
                .withEnabled(true);
        builder.getPluginConfigurationBuilder(MailSenderConfigurationBuilder.class)
                .withMailTo("doris4android@gmail.com")
                .withResBody(R.string.crash_mail_body_text)
                .withEnabled(true);
        ACRA.init(this, builder);
    }
}
