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

package fr.ffessm.doris.android.activities.view;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.DetailsFiche_ElementViewActivity;

public class FoldableClickListener implements OnClickListener {

	private static final String LOG_TAG = DetailsFiche_ElementViewActivity.class.getCanonicalName();
	
	private final Context context;
	
	protected View associatedFoldableView;
	protected ImageButton foldButton;
	
	public enum ImageButtonKind {
		DETAILS_FICHE,
		DESCRIPTION_ARBRE_PHYLO
	}
	private int image_maximize;
	private int image_minimize;
	
	public FoldableClickListener(Context context, View foldableView){
		this.context = context;
		this.associatedFoldableView = foldableView;
	}
	
	public FoldableClickListener(Context context, View foldableView, ImageButton foldButton, ImageButtonKind imageButtonKind ){
		this.context = context;
		this.associatedFoldableView = foldableView;
		this.foldButton = foldButton;
		
		switch(imageButtonKind){
		case DETAILS_FICHE :
			image_maximize = R.drawable.ic_menu_down_outline;
			image_minimize = R.drawable.ic_menu_up_outline;
			break;
		case DESCRIPTION_ARBRE_PHYLO :
			/*image_maximize = ThemeUtil.attrToResId(((DetailsFiche_ElementViewActivity) context), R.attr.ic_app_more_informations);
			image_minimize = ThemeUtil.attrToResId(((DetailsFiche_ElementViewActivity) context), R.attr.ic_app_more_informations);

			*/

			image_maximize = R.drawable.ic_menu_down_outline;
			image_minimize = R.drawable.ic_menu_up_outline;
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		if(associatedFoldableView.getVisibility() == View.GONE){
			unfold();
		}
		else{
			fold();
		}
	}
	
	public void onClick() {
		if(associatedFoldableView.getVisibility() == View.GONE){
			unfold();
		}
		else{
			fold();
		}
	}
	
	public void fold(){
		associatedFoldableView.setVisibility(View.GONE);
		foldButton.setImageResource(image_maximize);
	}
	
	public void unfold(){
		associatedFoldableView.setVisibility(View.VISIBLE);
		foldButton.setImageResource(image_minimize);
	}

}
