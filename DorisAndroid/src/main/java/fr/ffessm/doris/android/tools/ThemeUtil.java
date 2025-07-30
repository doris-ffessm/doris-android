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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.preference.PreferenceManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import fr.ffessm.doris.android.R;

public class ThemeUtil {

    private static final String LOG_TAG = ThemeUtil.class.getSimpleName();

    /**
     * Changes the theme of the activity, the activity is restarted (must not be called in onCreate())
     *
     * @param activity activity to update theme
     */
    public static void updateActivityTheme(Activity activity) {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void onActivityCreateSetTheme(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        String theme = prefs.getString(activity.getString(R.string.pref_key_theme), "DorisAndroid");
        Log.d(LOG_TAG, "theme = " + theme);
        switch (theme) {
            case "DorisAndroidLight":
                activity.setTheme(R.style.Theme_AppDorisAndroidLight);
                break;
            case "PureBlack":
                activity.setTheme(R.style.Theme_AppPureBlack);
                break;
            case "Holo":
                activity.setTheme(R.style.Theme_AppHolo);
                break;
            case "HoloLight":
                activity.setTheme(R.style.Theme_AppHoloLight);
                break;
            case "DorisAndroid":
            case "DORISAndroid":
            default:
                activity.setTheme(R.style.Theme_AppDorisAndroid);
        }
        enforceWindowBackground(activity);
    }

    /**
     * Workaround: Useful for older devices where Windowbackground color is overridden by the system
     */
    public static void enforceWindowBackground(Activity activity) {
        // Explicitly set the window background AFTER super.onCreate()
        // This attempts to override any system default or theme misapplication.
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activity.getTheme(); // Get the activity's theme

        int windowBackgroundColor = 0; // To store the resolved color

        // Try to resolve theme's intended windowBackground
        if (theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)) {
            if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // It's a color
                windowBackgroundColor = typedValue.data;

                activity.getWindow().setBackgroundDrawable(new ColorDrawable(typedValue.data));
                Log.d("ThemeFix", "Set windowBackground to theme color: #" + Integer.toHexString(typedValue.data));
            }
        }

        View decorView = activity.getWindow().getDecorView(); // Or a specific root view from your layout
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(activity.getWindow(), decorView);

        // Check if the window background color is "light"
        // A simple heuristic: if a color is more than half luminance, consider it light.
        // (Luminance calculation can be more precise, but this is a common approximation)
        boolean isLightBackground = (windowBackgroundColor != 0) && isColorLight(windowBackgroundColor);

        // For Navigation Bar buttons:
        // If the background is light, we need dark navigation buttons.
        // If the background is dark, we need light navigation buttons.
        insetsController.setAppearanceLightNavigationBars(isLightBackground);
        Log.d("ThemeFix", "Nav bar buttons set to " + (isLightBackground ? "DARK" : "LIGHT") + " based on window background.");



    }

    public static boolean isColorLight(int color) {
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return a < 0.5; // Threshold, can be adjusted
    }

    // Permet d'obtenir id de l'image pour setImageResource (différente selon les thèmes)
    public static int attrToResId(Activity activity, int attr) {

        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{attr});
        //Log.d(LOG_TAG, "attrToResId() - a.getResourceId(0, 0) ="+ a.getResourceId(0, 0));
        return a.getResourceId(0, 0);
    }
}
