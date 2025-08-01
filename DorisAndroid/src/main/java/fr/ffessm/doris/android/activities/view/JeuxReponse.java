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

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;

public class JeuxReponse {

	private Context context;
	private Activity activity;

	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getCanonicalName();

	private final Param_Outils paramOutils;
	private final Disque_Outils disqueOutils;
	private final Photos_Outils photosOutils;

    private int id;
    private String valeur;
    private String libelle;
    private String icone;

	public JeuxReponse(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;

		paramOutils = new Param_Outils(context);
		disqueOutils = new Disque_Outils(context);
		photosOutils = new Photos_Outils(context);
	}

	public JeuxReponse(Context context) {
        this.context = context;

        paramOutils = new Param_Outils(context);
        disqueOutils = new Disque_Outils(context);
        photosOutils = new Photos_Outils(context);
	}

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
       return id;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
    public String getValeur() {
        return valeur;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
