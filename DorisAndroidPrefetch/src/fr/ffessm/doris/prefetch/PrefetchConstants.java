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

package fr.ffessm.doris.prefetch;

public class PrefetchConstants {
	
	// we are using the in-memory H2 database
	//private final static String DATABASE_URL = "jdbc:h2:mem:fiche";
	// we are using the created SQLite database
	public final static String DATABASE_URL = "jdbc:sqlite:run/database/DorisAndroid.db";

	// Dossiers liés au fonctionnement de l'appli prefetch
	public final static String DOSSIER_RACINE = "./run";
	public final static String DOSSIER_DATABASE = "./run/database";
	public final static String DOSSIER_RES_HTML = "./res/html";
	
	// Ces dossiers seront renommés qd nécessaire
	public final static String DOSSIER_HTML = "html";
	public final static String DOSSIER_HTML_REF = "html_ref";
	public final static String DOSSIER_IMAGES = "images";
	public final static String DOSSIER_IMAGES_REF = "images_ref";
	public final static String SOUSDOSSIER_ICONES = "icones";
	public final static String SOUSDOSSIER_VIGNETTES = "vignettes_fiches";
	public final static String SOUSDOSSIER_MED_RES = "medium_res_images_fiches";
	public final static String SOUSDOSSIER_HI_RES = "hi_res_images_fiches";
	
	public final static String DOSSIER_CD = "cd";
	public final static String DOSSIER_DVD = "dvd";
	
	// Nombre maximum de fiches/groupes/etc traités (--max=K permet de changer cette valeur)
	// Si nbMaxFichesTraitees != nbMaxFichesTraiteesDef alors on limite aussi le nombre de groupes
	// de termes du glossaire, de participants, etc.
	public static int nbMaxFichesTraiteesDef = 9999;
	
	
}