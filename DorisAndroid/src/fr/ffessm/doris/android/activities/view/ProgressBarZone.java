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

public class ProgressBarZone {
	
	public enum NbBar {OneBar, TwoBar}
	
	private static final String LOG_TAG = ProgressBarZone.class.getCanonicalName();
	
	private Context inContext;
	
	TextView tvTitleText;
	ImageView ivIcone;
	TextView tvSummaryText;
	ProgressBar pbProgressBar;
	ProgressBar pbProgressBarPhotoPrinc;
	ProgressBar pbProgressBarPhoto;
	NbBar nbbars;
	 
	 
	public ProgressBarZone(Context inContext, LinearLayout inContainerLayout, NbBar nbbars) {
		this.inContext = inContext;
		this.nbbars = nbbars;
		LayoutInflater inflater = (LayoutInflater) inContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View convertView = inflater.inflate(R.layout.progressbar_zone, null);
		tvTitleText = (TextView) convertView.findViewById(R.id.title);
		ivIcone = (ImageView) convertView.findViewById(R.id.icon);
		tvSummaryText = (TextView) convertView.findViewById(R.id.summary);
		pbProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
		
		   
		pbProgressBarPhotoPrinc = (ProgressBar) convertView.findViewById(R.id.progressBarPhotoPrinc);
		pbProgressBarPhoto = (ProgressBar) convertView.findViewById(R.id.progressBarPhoto);
		   
		
		inContainerLayout.addView(convertView);
		
	}
	
	public ProgressBarZone(Context inContext, LinearLayout inContainerLayout, NbBar nbbars, String inTitre, String inSummary, int inIcone, boolean inAffBarrePhotoPrinc, int inAvancPhotoPrinc, boolean inAffBarrePhoto, int inAvancPhoto) {
		this(inContext, inContainerLayout, nbbars);
		update(inSummary, inSummary, inAvancPhoto, inAffBarrePhoto, inAvancPhoto, inAffBarrePhoto, inAvancPhoto);
	}

	public void update(String inTitre, String inSummary, int inIcone, boolean inAffBarrePhotoPrinc, int inAvancPhotoPrinc, boolean inAffBarrePhoto, int inAvancPhoto){
		
		   tvTitleText.setText(inTitre);
		   
		   
		   ivIcone.setImageResource(inIcone);

		   int iconeZine = Integer.valueOf(Outils.getParamString(inContext.getApplicationContext(), R.string.pref_key_accueil_icon_size, "64"));
	       ivIcone.setMaxHeight(iconeZine);
		   
		   tvSummaryText.setText(inSummary);
		   
		   
		   pbProgressBar.setVisibility(View.GONE);
		   
		   
		   if ( inAffBarrePhotoPrinc ) {
			   pbProgressBarPhotoPrinc.setVisibility(View.VISIBLE);
			   pbProgressBarPhotoPrinc.setProgress(inAvancPhotoPrinc);
		   }
		   if ( inAffBarrePhoto ) {
			   pbProgressBarPhoto.setVisibility(View.VISIBLE);
			   pbProgressBarPhoto.setProgress(inAvancPhoto);
		   }
		   
		   // Changement couleur de la barre en fonction de l'avancement
		   int limite1 = Integer.parseInt(inContext.getString(R.string.avancement_progressbar_limite1) );
		   int limite2 = Integer.parseInt(inContext.getString(R.string.avancement_progressbar_limite2) );
		   int limite3 = Integer.parseInt(inContext.getString(R.string.avancement_progressbar_limite3) );
		   //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - limites : "+limite1+" - "+limite2);
		   
		   //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "addProgressBarView() - Couleur1 :"+getContext().getString(R.string.avancement_progressbar_couleur1));
		   
		   int couleurPhotoPrinc;
		   if (inAvancPhotoPrinc <= limite1) {
			   couleurPhotoPrinc = Color.parseColor( inContext.getString(R.string.avancement_progressbar_couleur1) );   
		   } else if (inAvancPhotoPrinc <= limite2) {
			   couleurPhotoPrinc = Color.parseColor( inContext.getString(R.string.avancement_progressbar_couleur2) );
		   } else if (inAvancPhotoPrinc <= limite3) {
			   couleurPhotoPrinc = Color.parseColor( inContext.getString(R.string.avancement_progressbar_couleur3) );
		   } else {
			   couleurPhotoPrinc = Color.parseColor( inContext.getString(R.string.avancement_progressbar_couleur4) );
		   }
		   
		   int couleurPhoto;
		   if (inAvancPhoto <= limite1) {
			   couleurPhoto = Color.parseColor( inContext.getString(R.string.avancement_progressbar_couleur1) );   
		   } else if (inAvancPhoto <= limite2) {
			   couleurPhoto = Color.parseColor( inContext.getString(R.string.avancement_progressbar_couleur2) );
		   } else if (inAvancPhoto <= limite3) {
			   couleurPhoto = Color.parseColor( inContext.getString(R.string.avancement_progressbar_couleur3) );
		   } else {
			   couleurPhoto = Color.parseColor( inContext.getString(R.string.avancement_progressbar_couleur4) );
		   }
		   
		   // API 10 : pbProgressBar.getProgressDrawable().setColorFilter(couleur, Mode.MULTIPLY);
		   // TODO : API 14 (Vérifier la version qui nécessite effectivement le changement) : pbProgressBar.getProgressDrawable().setColorFilter(couleur, Mode.SRC_IN);
		   pbProgressBarPhotoPrinc.getProgressDrawable().setColorFilter(couleurPhotoPrinc, 
				   Mode.valueOf(inContext.getString(R.string.avancement_progressbar_mode) ) );
		   pbProgressBarPhoto.getProgressDrawable().setColorFilter(couleurPhoto, 
				   Mode.valueOf(inContext.getString(R.string.avancement_progressbar_mode) ) );
		   
		   
	}
	
}
