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

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.Outils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MultiProgressBar extends LinearLayout {

	Context context;

	/** title */
	public TextView tvTitleText;
	/** icon */
	public ImageView ivIcon;
	/** summary */
	public TextView tvSummaryText;
	/** 1rst progress bar */
	public ProgressBar pbProgressBar1;
	/** 2nd progress bar */
	public ProgressBar pbProgressBar2;
	/** global running progress bar */
	public ProgressBar pbProgressBar_running;

	public MultiProgressBar(Context context) {
		super(context);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		addView(inflater.inflate(R.layout.multiprogressbar, null));

		tvTitleText = (TextView) findViewById(R.id.multiprogressbar_title);
		tvSummaryText = (TextView) findViewById(R.id.multiprogressbar_summary);
		ivIcon = (ImageView) findViewById(R.id.multiprogressbar_icon);
		pbProgressBar1 = (ProgressBar) findViewById(R.id.multiprogressbar_progressBar1);
		pbProgressBar2 = (ProgressBar) findViewById(R.id.multiprogressbar_progressBar2);
		pbProgressBar_running = (ProgressBar) findViewById(R.id.multiprogressbar_running_progressBar);
	}

	public void update(String inTitre, String inSummary, int inIconResId,
			boolean inDisplayBar1, int inAvancBar1) {
		tvTitleText.setText(inTitre);

		ivIcon.setImageResource(inIconResId);

		int iconeZine = Integer.valueOf(Outils.getParamString(
				context.getApplicationContext(),
				R.string.pref_key_list_icon_size, "128"));
		ivIcon.setMaxHeight(iconeZine);

		tvSummaryText.setText(inSummary);

		if (inDisplayBar1) {
			pbProgressBar1.setVisibility(View.VISIBLE);
			pbProgressBar1.setProgress(inAvancBar1);
		} else {
			pbProgressBar1.setVisibility(View.GONE);
		}
		

		// Changement couleur de la barre en fonction de l'avancement
		int limite1 = Integer.parseInt(context
				.getString(R.string.avancement_progressbar_limite1));
		int limite2 = Integer.parseInt(context
				.getString(R.string.avancement_progressbar_limite2));
		int limite3 = Integer.parseInt(context
				.getString(R.string.avancement_progressbar_limite3));

		int couleurPhotoPrinc;
		if (inAvancBar1 <= limite1) {
			couleurPhotoPrinc = Color.parseColor(context
					.getString(R.string.avancement_progressbar_couleur1));
		} else if (inAvancBar1 <= limite2) {
			couleurPhotoPrinc = Color.parseColor(context
					.getString(R.string.avancement_progressbar_couleur2));
		} else if (inAvancBar1 <= limite3) {
			couleurPhotoPrinc = Color.parseColor(context
					.getString(R.string.avancement_progressbar_couleur3));
		} else {
			couleurPhotoPrinc = Color.parseColor(context
					.getString(R.string.avancement_progressbar_couleur4));
		}


		// API 10 : pbProgressBar.getProgressDrawable().setColorFilter(couleur,
		// Mode.MULTIPLY);
		// TODO : API 14 (Vérifier la version qui nécessite effectivement le
		// changement) :
		// pbProgressBar.getProgressDrawable().setColorFilter(couleur,
		// Mode.SRC_IN);
		pbProgressBar1.getProgressDrawable().setColorFilter(
				couleurPhotoPrinc,
				Mode.valueOf(context
						.getString(R.string.avancement_progressbar_mode)));
	}
	public void update(String inTitre, String inSummary, int inIconResId,
			boolean inDisplayBar1, int inAvancBar1, 
			boolean inDisplayBar2, int inAvancBar2, 
			boolean inDisplayRunningBar) {
		
		update(inTitre, inSummary, inIconResId, inDisplayBar1, inAvancBar1);
		
		
		if (inDisplayBar2) {
			pbProgressBar2.setVisibility(View.VISIBLE);
			pbProgressBar2.setProgress(inAvancBar2);
		} else {
			pbProgressBar2.setVisibility(View.GONE);
		}
		if (inDisplayRunningBar) {
			pbProgressBar_running.setVisibility(View.VISIBLE);
		} else {
			pbProgressBar_running.setVisibility(View.GONE);
		}

		// Changement couleur de la barre en fonction de l'avancement
		int limite1 = Integer.parseInt(context
				.getString(R.string.avancement_progressbar_limite1));
		int limite2 = Integer.parseInt(context
				.getString(R.string.avancement_progressbar_limite2));
		int limite3 = Integer.parseInt(context
				.getString(R.string.avancement_progressbar_limite3));

		

		int couleurPhoto;
		if (inAvancBar2 <= limite1) {
			couleurPhoto = Color.parseColor(context
					.getString(R.string.avancement_progressbar_couleur1));
		} else if (inAvancBar2 <= limite2) {
			couleurPhoto = Color.parseColor(context
					.getString(R.string.avancement_progressbar_couleur2));
		} else if (inAvancBar2 <= limite3) {
			couleurPhoto = Color.parseColor(context
					.getString(R.string.avancement_progressbar_couleur3));
		} else {
			couleurPhoto = Color.parseColor(context
					.getString(R.string.avancement_progressbar_couleur4));
		}

		// API 10 : pbProgressBar.getProgressDrawable().setColorFilter(couleur,
		// Mode.MULTIPLY);
		// TODO : API 14 (Vérifier la version qui nécessite effectivement le
		// changement) :
		// pbProgressBar.getProgressDrawable().setColorFilter(couleur,
		// Mode.SRC_IN);
		
		pbProgressBar2.getProgressDrawable().setColorFilter(
				couleurPhoto,
				Mode.valueOf(context
						.getString(R.string.avancement_progressbar_mode)));
		
	}
	
}