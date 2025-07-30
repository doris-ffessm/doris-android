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

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.ChainedLoadImageViewCallback;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import fr.ffessm.doris.android.tools.Reseau_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;

import static fr.ffessm.doris.android.tools.Photos_Outils.ImageType.HI_RES;
import static fr.ffessm.doris.android.tools.Photos_Outils.ImageType.MED_RES;

public class ImagePleinEcran_Adapter extends PagerAdapter {

    //private static final String LOG_TAG = ImagePleinEcran_Adapter.class.getCanonicalName();


    private static final String LOG_TAG = ImagePleinEcran_Adapter.class.getSimpleName();

    private ImagePleinEcran_CustomViewActivity _activity;
    private ArrayList<PhotoFiche> _PhotoFicheLists;
    private LayoutInflater inflater;

    private Param_Outils paramOutils;
    private Photos_Outils photosOutils;
    private Reseau_Outils reseauOutils;


    // constructor
    public ImagePleinEcran_Adapter(ImagePleinEcran_CustomViewActivity activity,
                                   ArrayList<PhotoFiche> photoFicheLists) {
        this._activity = activity;
        this._PhotoFicheLists = photoFicheLists;

        paramOutils = new Param_Outils(activity);
        photosOutils = new Photos_Outils(activity);
        reseauOutils = new Reseau_Outils(activity);
    }

    @Override
    public int getCount() {
        return this._PhotoFicheLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final fr.ffessm.doris.android.tools.TouchImageView imgDisplay;
        ImageView btnClose;
        ImageView btnHiResNotAvailable;
        Button imgTitre;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.imagepleinecran_image, container,
                false);

        imgDisplay = (fr.ffessm.doris.android.tools.TouchImageView) viewLayout.findViewById(R.id.imagepleinecran_image_imgDisplay);
        btnClose = (ImageView) viewLayout.findViewById(R.id.imagepleinecran_image_btnClose);
        imgTitre = (Button) viewLayout.findViewById(R.id.imagepleinecran_image_titre);
        btnHiResNotAvailable = (ImageView) viewLayout.findViewById(R.id.imagepleinecran_image_btnHiResNotAvalaible);
        btnHiResNotAvailable.setVisibility(View.GONE);
        //int hauteur = (int )Math.round((ScreenTools.getScreenHeight(_activity)*1.5));
        //int largeur = (int )Math.round((ScreenTools.getScreenWidth(_activity)*1.5));
        int hauteur = ScreenTools.getScreenHeight(_activity);
        int largeur = ScreenTools.getScreenWidth(_activity);
        final PhotoFiche photoFiche = _PhotoFicheLists.get(position);

        final ImageType bestLocallyAvailableRes;
        if (photosOutils.isAvailableInFolderPhoto(photoFiche.getCleURLNomFichier(), ImageType.HI_RES)) {
            bestLocallyAvailableRes = ImageType.HI_RES;
        } else if (photosOutils.isAvailableInFolderPhoto(photoFiche.getCleURLNomFichier(), ImageType.MED_RES)) {
            bestLocallyAvailableRes = ImageType.MED_RES;
        } else if (photosOutils.isAvailableInFolderPhoto(photoFiche.getCleURLNomFichier(), ImageType.VIGNETTE)) {
            bestLocallyAvailableRes = ImageType.VIGNETTE;
        } else {
            bestLocallyAvailableRes = null;
        }

        // calcul des postfix de nom si nécessaire
        ImageType requestedRes = Photos_Outils.ImageType.valueOf(paramOutils.getParamString(R.string.pref_key_mode_connecte_qualite_photo, ""));
        String small_suffixe_photo = Constants.GRANDE_BASE_URL_SUFFIXE;
        String med_suffixe_photo = Constants.GRANDE_BASE_URL_SUFFIXE;
        String large_suffixe_photo = Constants.GRANDE_BASE_URL_SUFFIXE;
        if (!photoFiche.getImgPostfixCodes().isEmpty() && photoFiche.getImgPostfixCodes().contains("&")) {
            // !! split -1 car https://stackoverflow.com/questions/14602062/java-string-split-removed-empty-values
            String[] imgPostfixCodes = photoFiche.getImgPostfixCodes().split("&", -1);
            if (!imgPostfixCodes[0].isEmpty()) {
                small_suffixe_photo = Constants.ImagePostFixCode.getEnumFromCode(imgPostfixCodes[0]).getPostFix();
            }
            if (!imgPostfixCodes[1].isEmpty()) {
                med_suffixe_photo = Constants.ImagePostFixCode.getEnumFromCode(imgPostfixCodes[1]).getPostFix();
            }
        }
        String requested_suffixe_photo = large_suffixe_photo;
        if (Photos_Outils.ImageType.valueOf(paramOutils.getParamString(R.string.pref_key_mode_connecte_qualite_photo, "")).equals(MED_RES)) {
            requested_suffixe_photo = med_suffixe_photo;
        }
        ChainedLoadImageViewCallback chainedLoadImageViewCallback = new ChainedLoadImageViewCallback(
                _activity,
                imgDisplay,
                Constants.IMAGE_BASE_URL + "/"
                        + photoFiche.getCleURL().replaceAll(
                        Constants.IMAGE_BASE_URL_SUFFIXE + "$", requested_suffixe_photo),
                largeur,
                hauteur,
                false,
                btnHiResNotAvailable); // vrai chargement de l'image dans le callback

        if (bestLocallyAvailableRes != null) {
            // on a une image en local, on commence par elle si pas déjà hires et on télécharge celle requise en ligne si autorisé
            if (bestLocallyAvailableRes.equals(HI_RES) || (bestLocallyAvailableRes.equals(MED_RES) && requestedRes.equals(MED_RES))) {
                // on a la bonne image en local
                try {
                    Picasso.get()
                            .load(photosOutils.getPhotoFile(photoFiche.getCleURLNomFichier(), bestLocallyAvailableRes))
                            .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                            .resize(largeur, hauteur)
                            .centerInside()
                            .into(imgDisplay);
                } catch (IOException e) {
                }
            } else {
                if (reseauOutils.isTelechargementsModeConnectePossible()) {
                    try {
                        Picasso.get()
                                .load(photosOutils.getPhotoFile(photoFiche.getCleURLNomFichier(), bestLocallyAvailableRes)) // charge d'abord la vignette depuis le disque
                                .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                                .resize(largeur, hauteur)
                                .centerInside()
                                .into(imgDisplay,
                                        chainedLoadImageViewCallback);  // on enchaine avec la vrai image requise
                    } catch (IOException e) {
                    }
                } else {
                    // téléchargement non autorisé, cherche dans le cache picasso
                    // si pas présent alors utilise la photo dispo et ajoute l'overlay
                    Picasso.get()
                            .load(Constants.IMAGE_BASE_URL + "/"
                                    + photoFiche.getCleURL().replaceAll(
                                    Constants.IMAGE_BASE_URL_SUFFIXE + "$", requested_suffixe_photo))
                            .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                            .placeholder(R.drawable.doris_icone_doris_large)
                            .into(imgDisplay, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e1) {
                                    try {
                                        Picasso.get()
                                                .load(photosOutils.getPhotoFile(photoFiche.getCleURLNomFichier(), bestLocallyAvailableRes))
                                                .resize(largeur, hauteur)
                                                .centerInside()
                                                .placeholder(R.drawable.doris_icone_doris_large_pas_connecte)
                                                .into(imgDisplay);
                                    } catch (IOException e) {
                                    }
                                    btnHiResNotAvailable.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }
        } else {
            final String  med_suffixe_photo2 = med_suffixe_photo;
            // pas de photo en local, télécharge en ligne si autorisé
            if (reseauOutils.isTelechargementsModeConnectePossible()) {
                String requestedImage = Constants.IMAGE_BASE_URL + "/"
                        + photoFiche.getCleURL().replaceAll(
                        Constants.IMAGE_BASE_URL_SUFFIXE + "$", requested_suffixe_photo);
                Picasso picasso = Picasso.get();
                picasso.setLoggingEnabled(true);
                picasso
                        .load(requestedImage)
                        .placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par défaut pour commencer
                        .resize(largeur, hauteur)
                        .centerInside()
                        .into(imgDisplay, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(LOG_TAG, "onSuccess: "+requestedImage);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(LOG_TAG, "onError: "+requestedImage);
                                btnHiResNotAvailable.setVisibility(View.VISIBLE);
                            }
                        });
                picasso.setLoggingEnabled(false);

            } else {
                // téléchargement non autorisé
                final String fallback_offline_suffixe_photo = small_suffixe_photo;
                Picasso.get()
                        .load(Constants.IMAGE_BASE_URL + "/"
                                + photoFiche.getCleURL().replaceAll(
                                Constants.IMAGE_BASE_URL_SUFFIXE + "$", requested_suffixe_photo))
                        .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                        .placeholder(R.drawable.doris_icone_doris_large)
                        .into(imgDisplay, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                // on tente de chercher le cache des vignettes avant de vraiment abandonner
                                btnHiResNotAvailable.setVisibility(View.VISIBLE);
                                Picasso.get()
                                        .load(Constants.IMAGE_BASE_URL + "/"
                                                + photoFiche.getCleURL().replaceAll(
                                                Constants.IMAGE_BASE_URL_SUFFIXE + "$", fallback_offline_suffixe_photo))
                                        .networkPolicy(NetworkPolicy.OFFLINE) // interdit l'accés web
                                        .placeholder(R.drawable.doris_icone_doris_large)
                                        .into(imgDisplay);
                            }
                        });
            }
        }

        imgDisplay.setOnClickListener(new PhotoClickListener(photoFiche));

        // gestion des bouton de control de zoom
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_activity);
        if (prefs.getBoolean(_activity.getString(R.string.pref_key_imagepleinecran_aff_zoomcontrol), false)) {
            ZoomControls zoomControls = (ZoomControls) viewLayout.findViewById(R.id.imagepleinecran_image_zoomControls);
            zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgDisplay.zoomIn();
                }
            });
            zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgDisplay.zoomOut();
                }
            });
        } else {
            ZoomControls zoomControls = (ZoomControls) viewLayout.findViewById(R.id.imagepleinecran_image_zoomControls);
            zoomControls.setVisibility(View.GONE);
        }
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });

        btnHiResNotAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_activity.isActivityDestroyed() || _activity.isFinishing()) return;
                Toast.makeText(_activity, _activity.getString(R.string.imagepleinecran_customview_btnHiResNotAvailable_message), Toast.LENGTH_LONG).show();
            }
        });


        // Affichage Titre & Description de l'image
        String titre = photoFiche.getTitre();
        int longMax = Integer.parseInt(paramOutils.getParamString(R.string.imagepleinecran_titre_longmax, "25"));
        // on termine par un espace insécable puis "..."
        if (titre.length() > longMax) titre = titre.substring(0, longMax) + "\u00A0\u2026";
        if (titre.isEmpty()) titre = "Image sans titre";
        imgTitre.setText(titre);

        imgTitre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullDescription(photoFiche);
            }
        });
        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }


    class PhotoClickListener implements View.OnClickListener {
        PhotoFiche photoFiche;
        private long lastTouchTime = -1;

        public PhotoClickListener(PhotoFiche photoFiche) {
            this.photoFiche = photoFiche;
        }

        @Override
        public void onClick(View v) {
            long thisTime = System.currentTimeMillis();
            if (thisTime - lastTouchTime < 250) {
                // Double Click on affiche la Description de l'image

                // TODO :
                //String texteAff = "Double Click - j'aurais aimé zommer x2 (comme Google Photo)";
                //Toast.makeText(_activity, texteAff, Toast.LENGTH_LONG).show();

                lastTouchTime = -1;
            } else if (lastTouchTime != -1) {
                // Double Click Lent on affiche la Description de l'image
                showFullDescription(photoFiche);

                lastTouchTime = -1;
            } else {
                lastTouchTime = thisTime;
            }
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    public void showDescription(int position) {
        //shows toast after 500ms
        final PhotoFiche photoFiche = _PhotoFicheLists.get(position);
        showDescription(photoFiche);
    }

    private void showDescription(PhotoFiche photoFiche) {
        String titre = photoFiche.getTitre();
        String description = photoFiche.getDescription();
        String texteAff = description;
        if (titre.length() > Integer.parseInt(paramOutils.getParamString(R.string.imagepleinecran_titre_longmax, "25")))
            texteAff = titre + System.getProperty("line.separator") + description;
        if (!texteAff.isEmpty()) {
            if (_activity.isActivityDestroyed() || _activity.isFinishing()) return;
            Toast.makeText(_activity, texteAff, Toast.LENGTH_LONG).show();
        }
    }

    public void showFullDescription(int position) {
        final PhotoFiche photoFiche = _PhotoFicheLists.get(position);
        showFullDescription(photoFiche);
    }
    private void showFullDescription(PhotoFiche photoFiche) {
        String titre = photoFiche.getTitre();
        String description = photoFiche.getDescription();
        String texteAff = titre + System.getProperty("line.separator") + description;
        AlertDialog alertDialog = new AlertDialog.Builder(_activity).create();
        //alertDialog.setTitle(photoFiche.getTitre());
        alertDialog.setMessage(texteAff);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
