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
package fr.ffessm.doris.android.datamodel;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

import fr.ffessm.doris.android.datamodel.associations.*;

/**
 * Context class used to simplify the access to the different DAOs of the application
 */
public class DorisDBHelper {


	public Dao<Fiche, Integer> ficheDao;
	//public RuntimeExceptionDao<Fiche, Integer> ficheDao;
	public Dao<AutreDenomination, Integer> autreDenominationDao;
	//public RuntimeExceptionDao<AutreDenomination, Integer> autreDenominationDao;
	public Dao<PhotoFiche, Integer> photoFicheDao;
	//public RuntimeExceptionDao<PhotoFiche, Integer> photoFicheDao;
	public Dao<SectionFiche, Integer> sectionFicheDao;
	//public RuntimeExceptionDao<SectionFiche, Integer> sectionFicheDao;
	public Dao<IntervenantFiche, Integer> intervenantFicheDao;
	//public RuntimeExceptionDao<IntervenantFiche, Integer> intervenantFicheDao;
	public Dao<Participant, Integer> participantDao;
	//public RuntimeExceptionDao<Participant, Integer> participantDao;
	public Dao<ZoneGeographique, Integer> zoneGeographiqueDao;
	//public RuntimeExceptionDao<ZoneGeographique, Integer> zoneGeographiqueDao;
	public Dao<ZoneObservation, Integer> zoneObservationDao;
	//public RuntimeExceptionDao<ZoneObservation, Integer> zoneObservationDao;
	public Dao<Groupe, Integer> groupeDao;
	//public RuntimeExceptionDao<Groupe, Integer> groupeDao;
	public Dao<DorisDB_metadata, Integer> dorisDB_metadataDao;
	//public RuntimeExceptionDao<DorisDB_metadata, Integer> dorisDB_metadataDao;
	public Dao<EntreeBibliographie, Integer> entreeBibliographieDao;
	//public RuntimeExceptionDao<EntreeBibliographie, Integer> entreeBibliographieDao;
	public Dao<Classification, Integer> classificationDao;
	//public RuntimeExceptionDao<Classification, Integer> classificationDao;
	public Dao<DefinitionGlossaire, Integer> definitionGlossaireDao;
	//public RuntimeExceptionDao<DefinitionGlossaire, Integer> definitionGlossaireDao;
	public Dao<ClassificationFiche, Integer> classificationFicheDao;
	//public RuntimeExceptionDao<ClassificationFiche, Integer> classificationFicheDao;
	public Dao<Fiches_ZonesGeographiques, Integer> fiches_ZonesGeographiquesDao;
	//public RuntimeExceptionDao<Fiches_ZonesGeographiques, Integer> fiches_ZonesGeographiquesDao;

	
	public DorisDBHelper(){
	}

	public DorisDBHelper(
		Dao<Fiche, Integer> ficheDao,
		Dao<AutreDenomination, Integer> autreDenominationDao,
		Dao<PhotoFiche, Integer> photoFicheDao,
		Dao<SectionFiche, Integer> sectionFicheDao,
		Dao<IntervenantFiche, Integer> intervenantFicheDao,
		Dao<Participant, Integer> participantDao,
		Dao<ZoneGeographique, Integer> zoneGeographiqueDao,
		Dao<ZoneObservation, Integer> zoneObservationDao,
		Dao<Groupe, Integer> groupeDao,
		Dao<DorisDB_metadata, Integer> dorisDB_metadataDao,
		Dao<EntreeBibliographie, Integer> entreeBibliographieDao,
		Dao<Classification, Integer> classificationDao,
		Dao<DefinitionGlossaire, Integer> definitionGlossaireDao,
		Dao<ClassificationFiche, Integer> classificationFicheDao,
        Dao<Fiches_ZonesGeographiques, Integer> fiches_ZonesGeographiquesDao
	){
		this.ficheDao = ficheDao;
		this.autreDenominationDao = autreDenominationDao;
		this.photoFicheDao = photoFicheDao;
		this.sectionFicheDao = sectionFicheDao;
		this.intervenantFicheDao = intervenantFicheDao;
		this.participantDao = participantDao;
		this.zoneGeographiqueDao = zoneGeographiqueDao;
		this.zoneObservationDao = zoneObservationDao;
		this.groupeDao = groupeDao;
		this.dorisDB_metadataDao = dorisDB_metadataDao;
		this.entreeBibliographieDao = entreeBibliographieDao;
		this.classificationDao = classificationDao;
		this.definitionGlossaireDao = definitionGlossaireDao;
		this.classificationFicheDao = classificationFicheDao;
		this.fiches_ZonesGeographiquesDao = fiches_ZonesGeographiquesDao;
	}

	

}
