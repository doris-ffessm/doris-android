/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
 * Auteurs : Guillaume Mo <gmo7942@gmail.com>
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

package fr.ffessm.doris.android.activities.view;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.tools.Outils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarZone extends LinearLayout{
	
	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getCanonicalName();
	
	private ZoneGeographique zoneGeo;
	private LayoutInflater inflater;
	 
	public ProgressBarZone(Context inContext, AttributeSet inAttrs) {
		super(inContext, inAttrs);
	}
	 
	public ProgressBarZone(Context inContext) {
		super(inContext);
	}
	
	public ProgressBarZone(Context inContext, ZoneGeographique inZoneGeo) {
		super(inContext);
		this.zoneGeo = inZoneGeo;
	}
	/*
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	fr.ffessm.doris.android.tools.TouchImageView imgDisplay;
        Button btnClose;
        Button imgTitre;
        
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.imagepleinecran_image, container,
                false);
  
        imgDisplay = (fr.ffessm.doris.android.tools.TouchImageView) viewLayout.findViewById(R.id.imagepleinecran_image_imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.imagepleinecran_image_btnClose);
        imgTitre = (Button) viewLayout.findViewById(R.id.imagepleinecran_image_titre);
        
		String uri = Outils.getZoneIcone(getContext(), zoneGeo.getId());
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarZone() - uri icone : "+uri);  
		int imageZone = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());
	   
		boolean affichageBarre;
		String summaryTexte = "";
		int avancement1 =0;
		int avancement2 =0;
	   
		Outils.PrecharMode precharModeZoneGeo = Outils.getPrecharModeZoneGeo(getContext(), zoneGeo.getId());
	   
		if ( precharModeZoneGeo == Outils.PrecharMode.P0 ) {
			affichageBarre = false;
			summaryTexte = getContext().getString(R.string.avancement_progressbar_aucune_summary);
		} else {
			int nbPhotosPrincATelecharger = Outils.getAPrecharQteZoneGeo(getContext(), zoneGeo.getId(), true);
			int nbPhotosATelecharger = Outils.getAPrecharQteZoneGeo(getContext(), zoneGeo.getId(), false);
			int nbPhotosPrincDejaLa = Outils.getDejaLaQteZoneGeo(getContext(), zoneGeo.getId(), true);
			int nbPhotosDejaLa = Outils.getDejaLaQteZoneGeo(getContext(), zoneGeo.getId(), false);
		   
			affichageBarre = true;

			if ( nbPhotosPrincATelecharger== 0){
				summaryTexte = getContext().getString(R.string.avancement_progressbar_jamais_summary);
			} else {
			   
				if ( precharModeZoneGeo == Outils.PrecharMode.P1 ) {
					summaryTexte = getContext().getString(R.string.avancement_progressbar_P1_summary);
					summaryTexte = summaryTexte.replace("@total", ""+nbPhotosPrincATelecharger ) ;
					summaryTexte = summaryTexte.replace("@nb", ""+nbPhotosPrincDejaLa );
				   
					avancement1 = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
					avancement2 = 0;
				   
				} else {
					summaryTexte = getContext().getString(R.string.avancement_progressbar_PX_summary1);
					summaryTexte = summaryTexte.replace("@total", ""+nbPhotosPrincATelecharger ) ;
					summaryTexte = summaryTexte.replace("@nb", ""+nbPhotosPrincDejaLa );
				   
					if (nbPhotosATelecharger == 0) {
						summaryTexte += getContext().getString(R.string.avancement_progressbar_PX_jamais_summary2);
					   
						avancement1 = 0;
						avancement2 = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
					} else {
						summaryTexte += getContext().getString(R.string.avancement_progressbar_PX_summary2);
						summaryTexte = summaryTexte.replace("@total", ""+nbPhotosATelecharger ) ;
						summaryTexte = summaryTexte.replace("@nb", ""+nbPhotosDejaLa );
					   
						avancement1 = 100 * nbPhotosDejaLa / nbPhotosATelecharger;
						avancement2 = 100 * nbPhotosPrincDejaLa / nbPhotosPrincATelecharger;
					}
				}
			}
		}
	   
		if (BuildConfig.DEBUG) Log.d(LOG_TAG, "refreshScreenData() - summaryTexte"+summaryTexte);
		
	   
	   TextView tvTitleText = (TextView) convertView.findViewById(R.id.title);
   		tvTitleText.setText(inTitre);
		   
		   ImageView ivIcone = (ImageView) convertView.findViewById(R.id.icon);
		   ivIcone.setImageResource(inIcone);
			
		   TextView tvSummaryText = (TextView) convertView.findViewById(R.id.summary);
		   tvSummaryText.setText(inSummary);
		   
		   ProgressBar pbProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
		   if ( inAffBarre ) {
			   pbProgressBar.setProgress(inAvancPrinc);
			   pbProgressBar.setSecondaryProgress(inAvancSecond);
		   } else {
			   pbProgressBar.setVisibility(View.GONE);
		   }
		   
		   // Changement couleur de la barre en fonction de l'avancement
		   int limite1 = Integer.parseInt(getContext().getString(R.string.avancement_progressbar_limite1) );
		   int limite2 = Integer.parseInt(getContext().getString(R.string.avancement_progressbar_limite2) );
		   if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - limites : "+limite1+" - "+limite2);
		   
		   if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - Couleur1 :"+getContext().getString(R.string.avancement_progressbar_couleur1));
		   
		   int couleur;
		   if (inAvancPrinc <= limite1) {
			   	couleur = Color.parseColor( getContext().getString(R.string.avancement_progressbar_couleur1) );   
		   } else {
			   if (inAvancPrinc <= limite2) {
				   couleur = Color.parseColor( getContext().getString(R.string.avancement_progressbar_couleur2) );
			   } else {
				   couleur = Color.parseColor( getContext().getString(R.string.avancement_progressbar_couleur3) );
			   }
		   }
		   if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - couleur : "+couleur);
		   // API 10 : pbProgressBar.getProgressDrawable().setColorFilter(couleur, Mode.MULTIPLY);
		   // TODO : API 14 (Vérifier la version qui nécessite effectivement le changement) : pbProgressBar.getProgressDrawable().setColorFilter(couleur, Mode.SRC_IN);
	   
		   pbProgressBar.getProgressDrawable().setColorFilter(couleur, 
				   Mode.valueOf(getContext().getString(R.string.avancement_progressbar_mode) ) );
		   
		   inContainerLayout.addView(convertView);
	}
	
	*/
}
