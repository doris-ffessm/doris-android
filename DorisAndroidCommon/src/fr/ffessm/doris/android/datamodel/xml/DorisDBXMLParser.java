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
package fr.ffessm.doris.android.datamodel.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

//import fr.ffessm.doris.android.datamodel.associations.*;
import fr.ffessm.doris.android.datamodel.*;
// Start of user code additional import for DorisDBXMLParser
// End of user code

/**
 * Root XmlPullParser for parsing the datamodel DorisDB
 */
public class DorisDBXMLParser {
	// Start of user code additional handler code 1
	// End of user code

	List<RefCommand> refCommands = new ArrayList<RefCommand>();

	List<Fiche> fiches = new ArrayList<Fiche>();
	List<AutreDenomination> autreDenominations = new ArrayList<AutreDenomination>();
	List<PhotoFiche> photoFiches = new ArrayList<PhotoFiche>();
	List<SectionFiche> sectionFiches = new ArrayList<SectionFiche>();
	List<Participant> participants = new ArrayList<Participant>();
	List<PhotoParticipant> photoParticipants = new ArrayList<PhotoParticipant>();
	List<ZoneGeographique> zoneGeographiques = new ArrayList<ZoneGeographique>();
	List<ZoneObservation> zoneObservations = new ArrayList<ZoneObservation>();
	List<Groupe> groupes = new ArrayList<Groupe>();
	List<DorisDB_metadata> dorisDB_metadatas = new ArrayList<DorisDB_metadata>();
	Set<Fiche> fichesToUpdate = new HashSet<Fiche>();
	Set<AutreDenomination> autreDenominationsToUpdate = new HashSet<AutreDenomination>();
	Set<PhotoFiche> photoFichesToUpdate = new HashSet<PhotoFiche>();
	Set<SectionFiche> sectionFichesToUpdate = new HashSet<SectionFiche>();
	Set<Participant> participantsToUpdate = new HashSet<Participant>();
	Set<PhotoParticipant> photoParticipantsToUpdate = new HashSet<PhotoParticipant>();
	Set<ZoneGeographique> zoneGeographiquesToUpdate = new HashSet<ZoneGeographique>();
	Set<ZoneObservation> zoneObservationsToUpdate = new HashSet<ZoneObservation>();
	Set<Groupe> groupesToUpdate = new HashSet<Groupe>();
	Set<DorisDB_metadata> dorisDB_metadatasToUpdate = new HashSet<DorisDB_metadata>();
	Hashtable<String, Fiche> xmlId2Fiche = new Hashtable<String, Fiche>();
	Hashtable<String, AutreDenomination> xmlId2AutreDenomination = new Hashtable<String, AutreDenomination>();
	Hashtable<String, PhotoFiche> xmlId2PhotoFiche = new Hashtable<String, PhotoFiche>();
	Hashtable<String, SectionFiche> xmlId2SectionFiche = new Hashtable<String, SectionFiche>();
	Hashtable<String, Participant> xmlId2Participant = new Hashtable<String, Participant>();
	Hashtable<String, PhotoParticipant> xmlId2PhotoParticipant = new Hashtable<String, PhotoParticipant>();
	Hashtable<String, ZoneGeographique> xmlId2ZoneGeographique = new Hashtable<String, ZoneGeographique>();
	Hashtable<String, ZoneObservation> xmlId2ZoneObservation = new Hashtable<String, ZoneObservation>();
	Hashtable<String, Groupe> xmlId2Groupe = new Hashtable<String, Groupe>();
	Hashtable<String, DorisDB_metadata> xmlId2DorisDB_metadata = new Hashtable<String, DorisDB_metadata>();

	// minimize memory footprint by using static Strings
    public static final String ID_STRING = "id";

	public static final String DATACLASSIFIER_FICHES = "FICHES";
	public static final String DATACLASSIFIER_FICHE  = "FICHE";
	public static final String DATACLASSIFIER_AUTREDENOMINATIONS = "AUTREDENOMINATIONS";
	public static final String DATACLASSIFIER_AUTREDENOMINATION  = "AUTREDENOMINATION";
	public static final String DATACLASSIFIER_PHOTOFICHES = "PHOTOFICHES";
	public static final String DATACLASSIFIER_PHOTOFICHE  = "PHOTOFICHE";
	public static final String DATACLASSIFIER_SECTIONFICHES = "SECTIONFICHES";
	public static final String DATACLASSIFIER_SECTIONFICHE  = "SECTIONFICHE";
	public static final String DATACLASSIFIER_PARTICIPANTS = "PARTICIPANTS";
	public static final String DATACLASSIFIER_PARTICIPANT  = "PARTICIPANT";
	public static final String DATACLASSIFIER_PHOTOPARTICIPANTS = "PHOTOPARTICIPANTS";
	public static final String DATACLASSIFIER_PHOTOPARTICIPANT  = "PHOTOPARTICIPANT";
	public static final String DATACLASSIFIER_ZONEGEOGRAPHIQUES = "ZONEGEOGRAPHIQUES";
	public static final String DATACLASSIFIER_ZONEGEOGRAPHIQUE  = "ZONEGEOGRAPHIQUE";
	public static final String DATACLASSIFIER_ZONEOBSERVATIONS = "ZONEOBSERVATIONS";
	public static final String DATACLASSIFIER_ZONEOBSERVATION  = "ZONEOBSERVATION";
	public static final String DATACLASSIFIER_GROUPES = "GROUPES";
	public static final String DATACLASSIFIER_GROUPE  = "GROUPE";
	public static final String DATACLASSIFIER_DORISDB_METADATAS = "DORISDB_METADATAS";
	public static final String DATACLASSIFIER_DORISDB_METADATA  = "DORISDB_METADATA";

	public static final String DATAATT_FICHE_nomScientifique = "nomScientifique";
	public static final String DATAATT_FICHE_NOMSCIENTIFIQUE = "NOMSCIENTIFIQUE";
	public static final String DATAATT_FICHE_nomCommun = "nomCommun";
	public static final String DATAATT_FICHE_NOMCOMMUN = "NOMCOMMUN";
	public static final String DATAATT_FICHE_numeroFiche = "numeroFiche";
	public static final String DATAATT_FICHE_NUMEROFICHE = "NUMEROFICHE";
	public static final String DATAATT_FICHE_etatFiche = "etatFiche";
	public static final String DATAATT_FICHE_ETATFICHE = "ETATFICHE";
	public static final String DATAATT_FICHE_dateCreation = "dateCreation";
	public static final String DATAATT_FICHE_DATECREATION = "DATECREATION";
	public static final String DATAATT_FICHE_dateModification = "dateModification";
	public static final String DATAATT_FICHE_DATEMODIFICATION = "DATEMODIFICATION";
	public static final String DATAATT_FICHE_numerofichesLiees = "numerofichesLiees";
	public static final String DATAATT_FICHE_NUMEROFICHESLIEES = "NUMEROFICHESLIEES";
	public static final String DATAREF_FICHE_redacteurs = "redacteurs";
	public static final String DATAREF_FICHE_photosFiche = "photosFiche";
	public static final String DATAREF_FICHE_zonesGeographiques = "zonesGeographiques";
	public static final String DATAREF_FICHE_zonesObservation = "zonesObservation";
	public static final String DATAREF_FICHE_verificateurs = "verificateurs";
	public static final String DATAREF_FICHE_responsableRegional = "responsableRegional";
	public static final String DATAREF_FICHE_contenu = "contenu";
	public static final String DATAREF_FICHE_photoPrincipale = "photoPrincipale";
	public static final String DATAREF_FICHE_autresDenominations = "autresDenominations";
	public static final String DATAREF_FICHE_groupe = "groupe";
	public static final String DATAATT_AUTREDENOMINATION_denomination = "denomination";
	public static final String DATAATT_AUTREDENOMINATION_DENOMINATION = "DENOMINATION";
	public static final String DATAATT_AUTREDENOMINATION_langue = "langue";
	public static final String DATAATT_AUTREDENOMINATION_LANGUE = "LANGUE";
	public static final String DATAREF_AUTREDENOMINATION_fiche = "fiche";
	public static final String DATAATT_PHOTOFICHE_cleURL = "cleURL";
	public static final String DATAATT_PHOTOFICHE_CLEURL = "CLEURL";
	public static final String DATAATT_PHOTOFICHE_imageVignette = "imageVignette";
	public static final String DATAATT_PHOTOFICHE_IMAGEVIGNETTE = "IMAGEVIGNETTE";
	public static final String DATAATT_PHOTOFICHE_imageMoyenne = "imageMoyenne";
	public static final String DATAATT_PHOTOFICHE_IMAGEMOYENNE = "IMAGEMOYENNE";
	public static final String DATAATT_PHOTOFICHE_imageGrande = "imageGrande";
	public static final String DATAATT_PHOTOFICHE_IMAGEGRANDE = "IMAGEGRANDE";
	public static final String DATAATT_PHOTOFICHE_titre = "titre";
	public static final String DATAATT_PHOTOFICHE_TITRE = "TITRE";
	public static final String DATAATT_PHOTOFICHE_description = "description";
	public static final String DATAATT_PHOTOFICHE_DESCRIPTION = "DESCRIPTION";
	public static final String DATAREF_PHOTOFICHE_fiche = "fiche";
	public static final String DATAATT_SECTIONFICHE_titre = "titre";
	public static final String DATAATT_SECTIONFICHE_TITRE = "TITRE";
	public static final String DATAATT_SECTIONFICHE_texte = "texte";
	public static final String DATAATT_SECTIONFICHE_TEXTE = "TEXTE";
	public static final String DATAREF_SECTIONFICHE_fiche = "fiche";
	public static final String DATAATT_PARTICIPANT_nom = "nom";
	public static final String DATAATT_PARTICIPANT_NOM = "NOM";
	public static final String DATAREF_PARTICIPANT_photo = "photo";
	public static final String DATAREF_PARTICIPANT_fichesVerifiees = "fichesVerifiees";
	public static final String DATAATT_PHOTOPARTICIPANT_cleURL = "cleURL";
	public static final String DATAATT_PHOTOPARTICIPANT_CLEURL = "CLEURL";
	public static final String DATAATT_PHOTOPARTICIPANT_image = "image";
	public static final String DATAATT_PHOTOPARTICIPANT_IMAGE = "IMAGE";
	public static final String DATAREF_PHOTOPARTICIPANT_participant = "participant";
	public static final String DATAATT_ZONEGEOGRAPHIQUE_nom = "nom";
	public static final String DATAATT_ZONEGEOGRAPHIQUE_NOM = "NOM";
	public static final String DATAATT_ZONEGEOGRAPHIQUE_description = "description";
	public static final String DATAATT_ZONEGEOGRAPHIQUE_DESCRIPTION = "DESCRIPTION";
	public static final String DATAREF_ZONEGEOGRAPHIQUE_fiches = "fiches";
	public static final String DATAREF_ZONEOBSERVATION_fiches = "fiches";
	public static final String DATAATT_GROUPE_nomGroupe = "nomGroupe";
	public static final String DATAATT_GROUPE_NOMGROUPE = "NOMGROUPE";
	public static final String DATAATT_GROUPE_descriptionGroupe = "descriptionGroupe";
	public static final String DATAATT_GROUPE_DESCRIPTIONGROUPE = "DESCRIPTIONGROUPE";
	public static final String DATAATT_GROUPE_numeroGroupe = "numeroGroupe";
	public static final String DATAATT_GROUPE_NUMEROGROUPE = "NUMEROGROUPE";
	public static final String DATAATT_GROUPE_numeroSousGroupe = "numeroSousGroupe";
	public static final String DATAATT_GROUPE_NUMEROSOUSGROUPE = "NUMEROSOUSGROUPE";
	public static final String DATAREF_GROUPE_groupesFils = "groupesFils";
	public static final String DATAREF_GROUPE_groupePere = "groupePere";
	public static final String DATAATT_DORISDB_METADATA_dateBase = "dateBase";
	public static final String DATAATT_DORISDB_METADATA_DATEBASE = "DATEBASE";



	// We don't use namespaces
    private static final String ns = null;

    public DorisDBXMLParser() {
        
    }

	public void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readDorisDB(parser);
        } finally {
            in.close();
        }
    }

	private void readDorisDB(XmlPullParser parser)  throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "DORISDB");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
		 	if (name.equals(DATACLASSIFIER_FICHES)) {
				fiches = readFiches(parser,DATACLASSIFIER_FICHES);
	            // fiches.addAll(readFiches(parser,DATACLASSIFIER_FICHES));
	        } else 
		 	if (name.equals(DATACLASSIFIER_AUTREDENOMINATIONS)) {
				autreDenominations = readAutreDenominations(parser,DATACLASSIFIER_AUTREDENOMINATIONS);
	            // autreDenominations.addAll(readAutreDenominations(parser,DATACLASSIFIER_AUTREDENOMINATIONS));
	        } else 
		 	if (name.equals(DATACLASSIFIER_PHOTOFICHES)) {
				photoFiches = readPhotoFiches(parser,DATACLASSIFIER_PHOTOFICHES);
	            // photoFiches.addAll(readPhotoFiches(parser,DATACLASSIFIER_PHOTOFICHES));
	        } else 
		 	if (name.equals(DATACLASSIFIER_SECTIONFICHES)) {
				sectionFiches = readSectionFiches(parser,DATACLASSIFIER_SECTIONFICHES);
	            // sectionFiches.addAll(readSectionFiches(parser,DATACLASSIFIER_SECTIONFICHES));
	        } else 
		 	if (name.equals(DATACLASSIFIER_PARTICIPANTS)) {
				participants = readParticipants(parser,DATACLASSIFIER_PARTICIPANTS);
	            // participants.addAll(readParticipants(parser,DATACLASSIFIER_PARTICIPANTS));
	        } else 
		 	if (name.equals(DATACLASSIFIER_PHOTOPARTICIPANTS)) {
				photoParticipants = readPhotoParticipants(parser,DATACLASSIFIER_PHOTOPARTICIPANTS);
	            // photoParticipants.addAll(readPhotoParticipants(parser,DATACLASSIFIER_PHOTOPARTICIPANTS));
	        } else 
		 	if (name.equals(DATACLASSIFIER_ZONEGEOGRAPHIQUES)) {
				zoneGeographiques = readZoneGeographiques(parser,DATACLASSIFIER_ZONEGEOGRAPHIQUES);
	            // zoneGeographiques.addAll(readZoneGeographiques(parser,DATACLASSIFIER_ZONEGEOGRAPHIQUES));
	        } else 
		 	if (name.equals(DATACLASSIFIER_ZONEOBSERVATIONS)) {
				zoneObservations = readZoneObservations(parser,DATACLASSIFIER_ZONEOBSERVATIONS);
	            // zoneObservations.addAll(readZoneObservations(parser,DATACLASSIFIER_ZONEOBSERVATIONS));
	        } else 
		 	if (name.equals(DATACLASSIFIER_GROUPES)) {
				groupes = readGroupes(parser,DATACLASSIFIER_GROUPES);
	            // groupes.addAll(readGroupes(parser,DATACLASSIFIER_GROUPES));
	        } else 
		 	if (name.equals(DATACLASSIFIER_DORISDB_METADATAS)) {
				dorisDB_metadatas = readDorisDB_metadatas(parser,DATACLASSIFIER_DORISDB_METADATAS);
	            // dorisDB_metadatas.addAll(readDorisDB_metadatas(parser,DATACLASSIFIER_DORISDB_METADATAS));
	        } else 
			{
	            skip(parser);
	        }
	    }
		
	}

	/**
     * parser for a group of Fiche
     */
	List<Fiche> readFiches(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<Fiche> entries = new ArrayList<Fiche>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_FICHE)) {
	            entries.add(readFiche(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of AutreDenomination
     */
	List<AutreDenomination> readAutreDenominations(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<AutreDenomination> entries = new ArrayList<AutreDenomination>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_AUTREDENOMINATION)) {
	            entries.add(readAutreDenomination(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of PhotoFiche
     */
	List<PhotoFiche> readPhotoFiches(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<PhotoFiche> entries = new ArrayList<PhotoFiche>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_PHOTOFICHE)) {
	            entries.add(readPhotoFiche(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of SectionFiche
     */
	List<SectionFiche> readSectionFiches(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<SectionFiche> entries = new ArrayList<SectionFiche>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_SECTIONFICHE)) {
	            entries.add(readSectionFiche(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of Participant
     */
	List<Participant> readParticipants(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<Participant> entries = new ArrayList<Participant>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_PARTICIPANT)) {
	            entries.add(readParticipant(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of PhotoParticipant
     */
	List<PhotoParticipant> readPhotoParticipants(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<PhotoParticipant> entries = new ArrayList<PhotoParticipant>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_PHOTOPARTICIPANT)) {
	            entries.add(readPhotoParticipant(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of ZoneGeographique
     */
	List<ZoneGeographique> readZoneGeographiques(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<ZoneGeographique> entries = new ArrayList<ZoneGeographique>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_ZONEGEOGRAPHIQUE)) {
	            entries.add(readZoneGeographique(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of ZoneObservation
     */
	List<ZoneObservation> readZoneObservations(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<ZoneObservation> entries = new ArrayList<ZoneObservation>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_ZONEOBSERVATION)) {
	            entries.add(readZoneObservation(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of Groupe
     */
	List<Groupe> readGroupes(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<Groupe> entries = new ArrayList<Groupe>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_GROUPE)) {
	            entries.add(readGroupe(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}
	/**
     * parser for a group of DorisDB_metadata
     */
	List<DorisDB_metadata> readDorisDB_metadatas(XmlPullParser parser, final String containingTag)  throws XmlPullParserException, IOException{
		ArrayList<DorisDB_metadata> entries = new ArrayList<DorisDB_metadata>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals(DATACLASSIFIER_DORISDB_METADATA)) {
	            entries.add(readDorisDB_metadata(parser));
	        } else {
	            skip(parser);
	        }
	    }
		entries.trimToSize();
		return entries;
	}

	Fiche readFiche(XmlPullParser parser)  throws XmlPullParserException, IOException{
		Fiche result = new Fiche();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_FICHE);
    	String currentTagName = parser.getName();
    			
    	xmlId2Fiche.put(parser.getAttributeValue(null, ID_STRING),result);		
		result.setNomScientifique(parser.getAttributeValue(null, DATAATT_FICHE_nomScientifique));
		result.setNomCommun(parser.getAttributeValue(null, DATAATT_FICHE_nomCommun));
		// TODO numeroFiche = parser.getAttributeValue(null, DATAATT_FICHE_NUMEROFICHE);
		// TODO etatFiche = parser.getAttributeValue(null, DATAATT_FICHE_ETATFICHE);
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAATT_FICHE_dateCreation)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_FICHE_dateCreation);
	            result.setDateCreation(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_FICHE_dateCreation);
	        } else
			if (currentTagName.equals(DATAATT_FICHE_dateModification)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_FICHE_dateModification);
	            result.setDateModification(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_FICHE_dateModification);
	        } else
			if (currentTagName.equals(DATAATT_FICHE_numerofichesLiees)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_FICHE_numerofichesLiees);
	            result.setNumerofichesLiees(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_FICHE_numerofichesLiees);
	        } else
			if (currentTagName.equals(DATAREF_FICHE_redacteurs)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_FICHE_redacteurs);
	            String id = readText(parser);
				refCommands.add(new Fiche_setRedacteurs_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_FICHE_redacteurs);	    
	        } else
			if (currentTagName.equals(DATAREF_FICHE_photosFiche)) {
				List<PhotoFiche> entries = readPhotoFiches(parser,DATAREF_FICHE_photosFiche);	
				photoFiches.addAll(entries); // add for inclusion in the DB
				//result.getPhotosFiche().addAll(entries);  //  doesn't work and need to be done in the other way round using the opposite
				refCommands.add(new Fiche_addContainedPhotosFiche_RefCommand(result,entries));	    
	        } else
					// TODO deal with ref zonesGeographiques
					// TODO deal with ref zonesObservation
			if (currentTagName.equals(DATAREF_FICHE_verificateurs)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_FICHE_verificateurs);
	            String id = readText(parser);
				refCommands.add(new Fiche_setVerificateurs_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_FICHE_verificateurs);	    
	        } else
			if (currentTagName.equals(DATAREF_FICHE_responsableRegional)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_FICHE_responsableRegional);
	            String id = readText(parser);
				refCommands.add(new Fiche_setResponsableRegional_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_FICHE_responsableRegional);	    
	        } else
			if (currentTagName.equals(DATAREF_FICHE_contenu)) {
				List<SectionFiche> entries = readSectionFiches(parser,DATAREF_FICHE_contenu);	
				sectionFiches.addAll(entries); // add for inclusion in the DB
				//result.getContenu().addAll(entries);  //  doesn't work and need to be done in the other way round using the opposite
				refCommands.add(new Fiche_addContainedContenu_RefCommand(result,entries));	    
	        } else
			if (currentTagName.equals(DATAREF_FICHE_photoPrincipale)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_FICHE_photoPrincipale);
	            String id = readText(parser);
				refCommands.add(new Fiche_setPhotoPrincipale_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_FICHE_photoPrincipale);	    
	        } else
			if (currentTagName.equals(DATAREF_FICHE_autresDenominations)) {
				List<AutreDenomination> entries = readAutreDenominations(parser,DATAREF_FICHE_autresDenominations);	
				autreDenominations.addAll(entries); // add for inclusion in the DB
				//result.getAutresDenominations().addAll(entries);  //  doesn't work and need to be done in the other way round using the opposite
				refCommands.add(new Fiche_addContainedAutresDenominations_RefCommand(result,entries));	    
	        } else
			if (currentTagName.equals(DATAREF_FICHE_groupe)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_FICHE_groupe);
	            String id = readText(parser);
				refCommands.add(new Fiche_setGroupe_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_FICHE_groupe);	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	AutreDenomination readAutreDenomination(XmlPullParser parser)  throws XmlPullParserException, IOException{
		AutreDenomination result = new AutreDenomination();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_AUTREDENOMINATION);
    	String currentTagName = parser.getName();
    			
    	xmlId2AutreDenomination.put(parser.getAttributeValue(null, ID_STRING),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAATT_AUTREDENOMINATION_denomination)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_AUTREDENOMINATION_denomination);
	            result.setDenomination(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_AUTREDENOMINATION_denomination);
	        } else
			if (currentTagName.equals(DATAATT_AUTREDENOMINATION_langue)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_AUTREDENOMINATION_langue);
	            result.setLangue(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_AUTREDENOMINATION_langue);
	        } else
			if (currentTagName.equals(DATAREF_AUTREDENOMINATION_fiche)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_AUTREDENOMINATION_fiche);
	            String id = readText(parser);
				refCommands.add(new AutreDenomination_setFiche_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_AUTREDENOMINATION_fiche);	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	PhotoFiche readPhotoFiche(XmlPullParser parser)  throws XmlPullParserException, IOException{
		PhotoFiche result = new PhotoFiche();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_PHOTOFICHE);
    	String currentTagName = parser.getName();
    			
    	xmlId2PhotoFiche.put(parser.getAttributeValue(null, ID_STRING),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAATT_PHOTOFICHE_cleURL)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_PHOTOFICHE_cleURL);
	            result.setCleURL(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_PHOTOFICHE_cleURL);
	        } else
			//TODO if (currentTagName.equals(DATAATT_PHOTOFICHE_IMAGEVIGNETTE)) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (currentTagName.equals(DATAATT_PHOTOFICHE_IMAGEMOYENNE)) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (currentTagName.equals(DATAATT_PHOTOFICHE_IMAGEGRANDE)) {
	        //    title = readTitle(parser);
	        //} else	
			if (currentTagName.equals(DATAATT_PHOTOFICHE_titre)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_PHOTOFICHE_titre);
	            result.setTitre(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_PHOTOFICHE_titre);
	        } else
			if (currentTagName.equals(DATAATT_PHOTOFICHE_description)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_PHOTOFICHE_description);
	            result.setDescription(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_PHOTOFICHE_description);
	        } else
			if (currentTagName.equals(DATAREF_PHOTOFICHE_fiche)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_PHOTOFICHE_fiche);
	            String id = readText(parser);
				refCommands.add(new PhotoFiche_setFiche_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_PHOTOFICHE_fiche);	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	SectionFiche readSectionFiche(XmlPullParser parser)  throws XmlPullParserException, IOException{
		SectionFiche result = new SectionFiche();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_SECTIONFICHE);
    	String currentTagName = parser.getName();
    			
    	xmlId2SectionFiche.put(parser.getAttributeValue(null, ID_STRING),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAATT_SECTIONFICHE_titre)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_SECTIONFICHE_titre);
	            result.setTitre(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_SECTIONFICHE_titre);
	        } else
			if (currentTagName.equals(DATAATT_SECTIONFICHE_texte)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_SECTIONFICHE_texte);
	            result.setTexte(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_SECTIONFICHE_texte);
	        } else
			if (currentTagName.equals(DATAREF_SECTIONFICHE_fiche)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_SECTIONFICHE_fiche);
	            String id = readText(parser);
				refCommands.add(new SectionFiche_setFiche_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_SECTIONFICHE_fiche);	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	Participant readParticipant(XmlPullParser parser)  throws XmlPullParserException, IOException{
		Participant result = new Participant();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_PARTICIPANT);
    	String currentTagName = parser.getName();
    			
    	xmlId2Participant.put(parser.getAttributeValue(null, ID_STRING),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAATT_PARTICIPANT_nom)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_PARTICIPANT_nom);
	            result.setNom(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_PARTICIPANT_nom);
	        } else
					// TODO deal with owned ref photo
			if (currentTagName.equals(DATAREF_PARTICIPANT_fichesVerifiees)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_PARTICIPANT_fichesVerifiees);
	            String id = readText(parser);
				refCommands.add(new Participant_setFichesVerifiees_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_PARTICIPANT_fichesVerifiees);	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	PhotoParticipant readPhotoParticipant(XmlPullParser parser)  throws XmlPullParserException, IOException{
		PhotoParticipant result = new PhotoParticipant();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_PHOTOPARTICIPANT);
    	String currentTagName = parser.getName();
    			
    	xmlId2PhotoParticipant.put(parser.getAttributeValue(null, ID_STRING),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAATT_PHOTOPARTICIPANT_cleURL)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_PHOTOPARTICIPANT_cleURL);
	            result.setCleURL(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_PHOTOPARTICIPANT_cleURL);
	        } else
			//TODO if (currentTagName.equals(DATAATT_PHOTOPARTICIPANT_IMAGE)) {
	        //    title = readTitle(parser);
	        //} else	
			if (currentTagName.equals(DATAREF_PHOTOPARTICIPANT_participant)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_PHOTOPARTICIPANT_participant);
	            String id = readText(parser);
				refCommands.add(new PhotoParticipant_setParticipant_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_PHOTOPARTICIPANT_participant);	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	ZoneGeographique readZoneGeographique(XmlPullParser parser)  throws XmlPullParserException, IOException{
		ZoneGeographique result = new ZoneGeographique();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_ZONEGEOGRAPHIQUE);
    	String currentTagName = parser.getName();
    			
    	xmlId2ZoneGeographique.put(parser.getAttributeValue(null, ID_STRING),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAATT_ZONEGEOGRAPHIQUE_nom)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_ZONEGEOGRAPHIQUE_nom);
	            result.setNom(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_ZONEGEOGRAPHIQUE_nom);
	        } else
			if (currentTagName.equals(DATAATT_ZONEGEOGRAPHIQUE_description)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_ZONEGEOGRAPHIQUE_description);
	            result.setDescription(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_ZONEGEOGRAPHIQUE_description);
	        } else
					// TODO deal with ref fiches
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	ZoneObservation readZoneObservation(XmlPullParser parser)  throws XmlPullParserException, IOException{
		ZoneObservation result = new ZoneObservation();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_ZONEOBSERVATION);
    	String currentTagName = parser.getName();
    			
    	xmlId2ZoneObservation.put(parser.getAttributeValue(null, ID_STRING),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAREF_ZONEOBSERVATION_fiches)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_ZONEOBSERVATION_fiches);
	            String id = readText(parser);
				refCommands.add(new ZoneObservation_setFiches_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_ZONEOBSERVATION_fiches);	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	Groupe readGroupe(XmlPullParser parser)  throws XmlPullParserException, IOException{
		Groupe result = new Groupe();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_GROUPE);
    	String currentTagName = parser.getName();
    			
    	xmlId2Groupe.put(parser.getAttributeValue(null, ID_STRING),result);		
		result.setNomGroupe(parser.getAttributeValue(null, DATAATT_GROUPE_nomGroupe));
		result.setDescriptionGroupe(parser.getAttributeValue(null, DATAATT_GROUPE_descriptionGroupe));
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			//TODO if (currentTagName.equals(DATAATT_GROUPE_NUMEROGROUPE)) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (currentTagName.equals(DATAATT_GROUPE_NUMEROSOUSGROUPE)) {
	        //    title = readTitle(parser);
	        //} else	
			if (currentTagName.equals(DATAREF_GROUPE_groupesFils)) {
				List<Groupe> entries = readGroupes(parser,DATAREF_GROUPE_groupesFils);	
				groupes.addAll(entries); // add for inclusion in the DB
				//result.getGroupesFils().addAll(entries);  //  doesn't work and need to be done in the other way round using the opposite
				refCommands.add(new Groupe_addContainedGroupesFils_RefCommand(result,entries));	    
	        } else
			if (currentTagName.equals(DATAREF_GROUPE_groupePere)) {	
				parser.require(XmlPullParser.START_TAG, ns, DATAREF_GROUPE_groupePere);
	            String id = readText(parser);
				refCommands.add(new Groupe_setGroupePere_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, DATAREF_GROUPE_groupePere);	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	DorisDB_metadata readDorisDB_metadata(XmlPullParser parser)  throws XmlPullParserException, IOException{
		DorisDB_metadata result = new DorisDB_metadata();

		parser.require(XmlPullParser.START_TAG, ns, DATACLASSIFIER_DORISDB_METADATA);
    	String currentTagName = parser.getName();
    			
    	xmlId2DorisDB_metadata.put(parser.getAttributeValue(null, ID_STRING),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals(DATAATT_DORISDB_METADATA_dateBase)) {
				parser.require(XmlPullParser.START_TAG, ns, DATAATT_DORISDB_METADATA_dateBase);
	            result.setDateBase(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, DATAATT_DORISDB_METADATA_dateBase);
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}

   /**
    * abstract command for dealing with all task that must wait that the element have been created
	*/
	public abstract class RefCommand{
		public abstract void run();
	}
	class Fiche_setRedacteurs_RefCommand extends RefCommand{
		Fiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Fiche_setRedacteurs_RefCommand(Fiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setRedacteurs(parser.xmlId2Participant.get(referencedElementID));
			fichesToUpdate.add(self);
		}
	}
	class Fiche_addContainedPhotosFiche_RefCommand extends RefCommand{
		Fiche container;
		List<PhotoFiche> containedElements;
		
		public Fiche_addContainedPhotosFiche_RefCommand(Fiche container,
				List<PhotoFiche> containedElements) {
			super();
			this.container = container;
			this.containedElements = containedElements;
		}

		@Override
		public void run() {
			for (PhotoFiche element : containedElements) {				
				element.setFiche(container);
				photoFichesToUpdate.add(element);
			}
		}
		
	}
	// class Fiche_addZonesGeographiques_RefCommand extends RefCommand{
	// class Fiche_addZonesObservation_RefCommand extends RefCommand{
	class Fiche_setVerificateurs_RefCommand extends RefCommand{
		Fiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Fiche_setVerificateurs_RefCommand(Fiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setVerificateurs(parser.xmlId2Participant.get(referencedElementID));
			fichesToUpdate.add(self);
		}
	}
	class Fiche_setResponsableRegional_RefCommand extends RefCommand{
		Fiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Fiche_setResponsableRegional_RefCommand(Fiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setResponsableRegional(parser.xmlId2Participant.get(referencedElementID));
			fichesToUpdate.add(self);
		}
	}
	class Fiche_addContainedContenu_RefCommand extends RefCommand{
		Fiche container;
		List<SectionFiche> containedElements;
		
		public Fiche_addContainedContenu_RefCommand(Fiche container,
				List<SectionFiche> containedElements) {
			super();
			this.container = container;
			this.containedElements = containedElements;
		}

		@Override
		public void run() {
			for (SectionFiche element : containedElements) {				
				element.setFiche(container);
				sectionFichesToUpdate.add(element);
			}
		}
		
	}
	class Fiche_setPhotoPrincipale_RefCommand extends RefCommand{
		Fiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Fiche_setPhotoPrincipale_RefCommand(Fiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setPhotoPrincipale(parser.xmlId2PhotoFiche.get(referencedElementID));
			fichesToUpdate.add(self);
		}
	}
	class Fiche_addContainedAutresDenominations_RefCommand extends RefCommand{
		Fiche container;
		List<AutreDenomination> containedElements;
		
		public Fiche_addContainedAutresDenominations_RefCommand(Fiche container,
				List<AutreDenomination> containedElements) {
			super();
			this.container = container;
			this.containedElements = containedElements;
		}

		@Override
		public void run() {
			for (AutreDenomination element : containedElements) {				
				element.setFiche(container);
				autreDenominationsToUpdate.add(element);
			}
		}
		
	}
	class Fiche_setGroupe_RefCommand extends RefCommand{
		Fiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Fiche_setGroupe_RefCommand(Fiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setGroupe(parser.xmlId2Groupe.get(referencedElementID));
			fichesToUpdate.add(self);
		}
	}
	class AutreDenomination_setFiche_RefCommand extends RefCommand{
		AutreDenomination self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public AutreDenomination_setFiche_RefCommand(AutreDenomination self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setFiche(parser.xmlId2Fiche.get(referencedElementID));
			autreDenominationsToUpdate.add(self);
		}
	}
	class PhotoFiche_setFiche_RefCommand extends RefCommand{
		PhotoFiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public PhotoFiche_setFiche_RefCommand(PhotoFiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setFiche(parser.xmlId2Fiche.get(referencedElementID));
			photoFichesToUpdate.add(self);
		}
	}
	class SectionFiche_setFiche_RefCommand extends RefCommand{
		SectionFiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public SectionFiche_setFiche_RefCommand(SectionFiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setFiche(parser.xmlId2Fiche.get(referencedElementID));
			sectionFichesToUpdate.add(self);
		}
	}
	class Participant_setContainedPhoto_RefCommand extends RefCommand{
	Participant container;
		PhotoParticipant containedElement;
		
		public Participant_setContainedPhoto_RefCommand(Participant container,
				PhotoParticipant containedElement) {
			super();
			this.container = container;
			this.containedElement = containedElement;
		}

		@Override
		public void run() {
			containedElement.setParticipant(container);
			photoParticipantsToUpdate.add(containedElement);			
		}
		
	}
	class Participant_setFichesVerifiees_RefCommand extends RefCommand{
		Participant self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Participant_setFichesVerifiees_RefCommand(Participant self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setFichesVerifiees(parser.xmlId2Fiche.get(referencedElementID));
			participantsToUpdate.add(self);
		}
	}
	class PhotoParticipant_setParticipant_RefCommand extends RefCommand{
		PhotoParticipant self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public PhotoParticipant_setParticipant_RefCommand(PhotoParticipant self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setParticipant(parser.xmlId2Participant.get(referencedElementID));
			photoParticipantsToUpdate.add(self);
		}
	}
	// class ZoneGeographique_addFiches_RefCommand extends RefCommand{
	class ZoneObservation_setFiches_RefCommand extends RefCommand{
		ZoneObservation self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public ZoneObservation_setFiches_RefCommand(ZoneObservation self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setFiches(parser.xmlId2Fiche.get(referencedElementID));
			zoneObservationsToUpdate.add(self);
		}
	}
	class Groupe_addContainedGroupesFils_RefCommand extends RefCommand{
		Groupe container;
		List<Groupe> containedElements;
		
		public Groupe_addContainedGroupesFils_RefCommand(Groupe container,
				List<Groupe> containedElements) {
			super();
			this.container = container;
			this.containedElements = containedElements;
		}

		@Override
		public void run() {
			for (Groupe element : containedElements) {				
				element.setGroupePere(container);
				groupesToUpdate.add(element);
			}
		}
		
	}
	class Groupe_setGroupePere_RefCommand extends RefCommand{
		Groupe self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Groupe_setGroupePere_RefCommand(Groupe self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setGroupePere(parser.xmlId2Groupe.get(referencedElementID));
			groupesToUpdate.add(self);
		}
	}

	// ---------- Additional helper methods
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	}

	// Start of user code additional handler code 2

	// End of user code
}
