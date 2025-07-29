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
package fr.ffessm.doris.android.tools;


import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.util.Log;

import fr.ffessm.doris.android.R;

public class Param_Outils {
    private final String LOG_TAG = Param_Outils.class.getCanonicalName();

    private Context context;

    public Param_Outils(Context context) {
        this.context = context;
    }


    /* Lecture Paramètres */

    public String getStringKeyParam(int inParam) {
        return context.getResources().getResourceEntryName(inParam);
    }

    public String getStringNameParam(int inParam) {
        return context.getString(inParam);
    }

    public boolean getParamBoolean(int inParam, boolean inValDef) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(inParam), inValDef);
    }

    public String getParamString(int inParam, String inValDef) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamString() - param : " + inParam + "-" + context.getString(inParam) );
        return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(inParam), inValDef);
    }

    public long getParamLong(int inParam, Long inValDef) {
        if (PreferenceManager.getDefaultSharedPreferences(context).contains(context.getString(inParam))) {
            return PreferenceManager.getDefaultSharedPreferences(context).getLong(context.getString(inParam), inValDef);
        } else {
            return inValDef;
        }
    }

    public int getParamInt(int inParam, int inValDef) {
        //Log.d(LOG_TAG, "getParamInt() - param : " + context.getString(inParam) + " ; valDef : " + inValDef );
        try {
            //		Log.d(LOG_TAG, "getParamInt() - context.getString(inParam) : " + context.getString(inParam) );
            //		Log.d(LOG_TAG, "getParamInt() - valeur Int : " + PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(inParam), inValDef) );
            return PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(inParam), inValDef);

        } catch (Exception eInt) {
            //		Log.e(LOG_TAG, eInt.getMessage(), eInt);

            try {
                //			Log.d(LOG_TAG, "getParamInt() - valeur String: " + PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(inParam), "-") );
                return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(inParam), "" + inValDef));

            } catch (Exception eStr) {
                Log.e(LOG_TAG, eStr.getMessage(), eStr);
                return inValDef;
            }

        }
    }

    /* Enregistrement paramètres */

    public void setParamString(int inParam, String inVal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEdit = preferences.edit();
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setParamString() - param : " + context.getString(inParam) );
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setParamString() - getStringKeyParam : " + Outils.getStringKeyParam(context,inParam) );
        prefEdit.putString(context.getString(inParam), inVal);
        prefEdit.apply();
    }

    public void setParamInt(int inParam, int inVal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEdit = preferences.edit();
        prefEdit.putInt(context.getString(inParam), inVal);
        prefEdit.apply();
    }

    public void setParamBoolean(int inParam, Boolean inVal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEdit = preferences.edit();
        prefEdit.putBoolean(context.getString(inParam), inVal);
        prefEdit.apply();
    }

    public void setParamLong(int inParam, long inVal) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEdit = preferences.edit();
        prefEdit.putLong(context.getString(inParam), inVal);
        prefEdit.apply();
    }


}
