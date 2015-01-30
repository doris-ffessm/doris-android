/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2015 - FFESSM
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
package fr.ffessm.doris.android.datamodel.associations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import fr.ffessm.doris.android.datamodel.*;

/** 
  *  
  */ 
@DatabaseTable(tableName = "fiches_ZonesObservations")
public class Fiches_ZonesObservations {

 
	
	public final static String FICHE_ID_FIELD_NAME = "Fiche_id";
	public final static String ZONEOBSERVATION_ID_FIELD_NAME = "ZoneObservation_id";

	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)
	protected int _id;
	
	// This is a foreign object which just stores the id from the User object in this table.
	@DatabaseField(foreign = true, columnName = FICHE_ID_FIELD_NAME)
	Fiche fiche;

	// This is a foreign object which just stores the id from the Post object in this table.
	@DatabaseField(foreign = true, columnName = ZONEOBSERVATION_ID_FIELD_NAME)
	ZoneObservation zoneObservation;

	Fiches_ZonesObservations() {
		// for ormlite
	}

	public Fiches_ZonesObservations(Fiche fiche, ZoneObservation zoneObservation) {
		this.fiche = fiche;
		this.zoneObservation = zoneObservation;
	}
}
