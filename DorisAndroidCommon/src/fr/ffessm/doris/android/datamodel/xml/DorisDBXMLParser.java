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
 * Root Sax XML DefaultHandler for parsing the datamodel DorisDB
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
	Set<Fiche> fichesToUpdate = new HashSet<Fiche>();
	Set<AutreDenomination> autreDenominationsToUpdate = new HashSet<AutreDenomination>();
	Set<PhotoFiche> photoFichesToUpdate = new HashSet<PhotoFiche>();
	Set<SectionFiche> sectionFichesToUpdate = new HashSet<SectionFiche>();
	Set<Participant> participantsToUpdate = new HashSet<Participant>();
	Set<PhotoParticipant> photoParticipantsToUpdate = new HashSet<PhotoParticipant>();
	Set<ZoneGeographique> zoneGeographiquesToUpdate = new HashSet<ZoneGeographique>();
	Set<ZoneObservation> zoneObservationsToUpdate = new HashSet<ZoneObservation>();
	Set<Groupe> groupesToUpdate = new HashSet<Groupe>();
	Hashtable<String, Fiche> xmlId2Fiche = new Hashtable<String, Fiche>();
	Hashtable<String, AutreDenomination> xmlId2AutreDenomination = new Hashtable<String, AutreDenomination>();
	Hashtable<String, PhotoFiche> xmlId2PhotoFiche = new Hashtable<String, PhotoFiche>();
	Hashtable<String, SectionFiche> xmlId2SectionFiche = new Hashtable<String, SectionFiche>();
	Hashtable<String, Participant> xmlId2Participant = new Hashtable<String, Participant>();
	Hashtable<String, PhotoParticipant> xmlId2PhotoParticipant = new Hashtable<String, PhotoParticipant>();
	Hashtable<String, ZoneGeographique> xmlId2ZoneGeographique = new Hashtable<String, ZoneGeographique>();
	Hashtable<String, ZoneObservation> xmlId2ZoneObservation = new Hashtable<String, ZoneObservation>();
	Hashtable<String, Groupe> xmlId2Groupe = new Hashtable<String, Groupe>();

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
		 	if (name.equals("FICHES")) {
	            fiches.addAll(readFiches(parser,"FICHES"));
	        } else 
		 	if (name.equals("AUTREDENOMINATIONS")) {
	            autreDenominations.addAll(readAutreDenominations(parser,"AUTREDENOMINATIONS"));
	        } else 
		 	if (name.equals("PHOTOFICHES")) {
	            photoFiches.addAll(readPhotoFiches(parser,"PHOTOFICHES"));
	        } else 
		 	if (name.equals("SECTIONFICHES")) {
	            sectionFiches.addAll(readSectionFiches(parser,"SECTIONFICHES"));
	        } else 
		 	if (name.equals("PARTICIPANTS")) {
	            participants.addAll(readParticipants(parser,"PARTICIPANTS"));
	        } else 
		 	if (name.equals("PHOTOPARTICIPANTS")) {
	            photoParticipants.addAll(readPhotoParticipants(parser,"PHOTOPARTICIPANTS"));
	        } else 
		 	if (name.equals("ZONEGEOGRAPHIQUES")) {
	            zoneGeographiques.addAll(readZoneGeographiques(parser,"ZONEGEOGRAPHIQUES"));
	        } else 
		 	if (name.equals("ZONEOBSERVATIONS")) {
	            zoneObservations.addAll(readZoneObservations(parser,"ZONEOBSERVATIONS"));
	        } else 
		 	if (name.equals("GROUPES")) {
	            groupes.addAll(readGroupes(parser,"GROUPES"));
	        } else 
			{
	            skip(parser);
	        }
	    }
		
	}

	/**
     * parser for a group of Fiche
     */
	List<Fiche> readFiches(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<Fiche> entries = new ArrayList<Fiche>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("FICHE")) {
	            entries.add(readFiche(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}
	/**
     * parser for a group of AutreDenomination
     */
	List<AutreDenomination> readAutreDenominations(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<AutreDenomination> entries = new ArrayList<AutreDenomination>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("AUTREDENOMINATION")) {
	            entries.add(readAutreDenomination(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}
	/**
     * parser for a group of PhotoFiche
     */
	List<PhotoFiche> readPhotoFiches(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<PhotoFiche> entries = new ArrayList<PhotoFiche>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("PHOTOFICHE")) {
	            entries.add(readPhotoFiche(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}
	/**
     * parser for a group of SectionFiche
     */
	List<SectionFiche> readSectionFiches(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<SectionFiche> entries = new ArrayList<SectionFiche>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("SECTIONFICHE")) {
	            entries.add(readSectionFiche(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}
	/**
     * parser for a group of Participant
     */
	List<Participant> readParticipants(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<Participant> entries = new ArrayList<Participant>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("PARTICIPANT")) {
	            entries.add(readParticipant(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}
	/**
     * parser for a group of PhotoParticipant
     */
	List<PhotoParticipant> readPhotoParticipants(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<PhotoParticipant> entries = new ArrayList<PhotoParticipant>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("PHOTOPARTICIPANT")) {
	            entries.add(readPhotoParticipant(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}
	/**
     * parser for a group of ZoneGeographique
     */
	List<ZoneGeographique> readZoneGeographiques(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<ZoneGeographique> entries = new ArrayList<ZoneGeographique>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("ZONEGEOGRAPHIQUE")) {
	            entries.add(readZoneGeographique(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}
	/**
     * parser for a group of ZoneObservation
     */
	List<ZoneObservation> readZoneObservations(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<ZoneObservation> entries = new ArrayList<ZoneObservation>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("ZONEOBSERVATION")) {
	            entries.add(readZoneObservation(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}
	/**
     * parser for a group of Groupe
     */
	List<Groupe> readGroupes(XmlPullParser parser, String containingTag)  throws XmlPullParserException, IOException{
		List<Groupe> entries = new ArrayList<Groupe>();
		parser.require(XmlPullParser.START_TAG, ns, containingTag);
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("GROUPE")) {
	            entries.add(readGroupe(parser));
	        } else {
	            skip(parser);
	        }
	    }
		return entries;
	}

	Fiche readFiche(XmlPullParser parser)  throws XmlPullParserException, IOException{
		Fiche result = new Fiche();

		parser.require(XmlPullParser.START_TAG, ns, "FICHE");
    	String currentTagName = parser.getName();
    			
    	xmlId2Fiche.put(parser.getAttributeValue(null, "id"),result);		
		result.setNomScientifique(parser.getAttributeValue(null, "nomScientifique"));
		result.setNomCommun(parser.getAttributeValue(null, "nomCommun"));
		// TODO numeroFiche = parser.getAttributeValue(null, "NUMEROFICHE");
		// TODO etatFiche = parser.getAttributeValue(null, "ETATFICHE");
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals("dateCreation")) {
				parser.require(XmlPullParser.START_TAG, ns, "dateCreation");
	            result.setDateCreation(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "dateCreation");
	        } else
			if (currentTagName.equals("dateModification")) {
				parser.require(XmlPullParser.START_TAG, ns, "dateModification");
	            result.setDateModification(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "dateModification");
	        } else
			if (currentTagName.equals("redacteurs")) {	
				parser.require(XmlPullParser.START_TAG, ns, "redacteurs");
	            String id = readText(parser);
				refCommands.add(new Fiche_setRedacteurs_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "redacteurs");	    
	        } else
			if (currentTagName.equals("photosFiche")) {
				List<PhotoFiche> entries = readPhotoFiches(parser,"photosFiche");	
				photoFiches.addAll(entries); // add for inclusion in the DB
				//result.getPhotosFiche().addAll(entries);  //  doesn't work and need to be done in the other way round using the opposite
				refCommands.add(new Fiche_addContainedPhotosFiche_RefCommand(result,entries));	    
	        } else
			if (currentTagName.equals("zonesGeographiques")) {	
				parser.require(XmlPullParser.START_TAG, ns, "zonesGeographiques");
	            String id = readText(parser);
				refCommands.add(new Fiche_setZonesGeographiques_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "zonesGeographiques");	    
	        } else
			if (currentTagName.equals("zonesObservation")) {	
				parser.require(XmlPullParser.START_TAG, ns, "zonesObservation");
	            String id = readText(parser);
				refCommands.add(new Fiche_setZonesObservation_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "zonesObservation");	    
	        } else
			if (currentTagName.equals("verificateurs")) {	
				parser.require(XmlPullParser.START_TAG, ns, "verificateurs");
	            String id = readText(parser);
				refCommands.add(new Fiche_setVerificateurs_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "verificateurs");	    
	        } else
			if (currentTagName.equals("responsableRegional")) {	
				parser.require(XmlPullParser.START_TAG, ns, "responsableRegional");
	            String id = readText(parser);
				refCommands.add(new Fiche_setResponsableRegional_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "responsableRegional");	    
	        } else
			if (currentTagName.equals("contenu")) {
				List<SectionFiche> entries = readSectionFiches(parser,"contenu");	
				sectionFiches.addAll(entries); // add for inclusion in the DB
				//result.getContenu().addAll(entries);  //  doesn't work and need to be done in the other way round using the opposite
				refCommands.add(new Fiche_addContainedContenu_RefCommand(result,entries));	    
	        } else
			if (currentTagName.equals("photoPrincipale")) {	
				parser.require(XmlPullParser.START_TAG, ns, "photoPrincipale");
	            String id = readText(parser);
				refCommands.add(new Fiche_setPhotoPrincipale_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "photoPrincipale");	    
	        } else
					// TODO deal with owned ref autresDenominations
			if (currentTagName.equals("groupe")) {	
				parser.require(XmlPullParser.START_TAG, ns, "groupe");
	            String id = readText(parser);
				refCommands.add(new Fiche_setGroupe_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "groupe");	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	AutreDenomination readAutreDenomination(XmlPullParser parser)  throws XmlPullParserException, IOException{
		AutreDenomination result = new AutreDenomination();

		parser.require(XmlPullParser.START_TAG, ns, "AUTREDENOMINATION");
    	String currentTagName = parser.getName();
    			
    	xmlId2AutreDenomination.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals("denomination")) {
				parser.require(XmlPullParser.START_TAG, ns, "denomination");
	            result.setDenomination(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "denomination");
	        } else
			if (currentTagName.equals("langue")) {
				parser.require(XmlPullParser.START_TAG, ns, "langue");
	            result.setLangue(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "langue");
	        } else
			if (currentTagName.equals("fiche")) {	
				parser.require(XmlPullParser.START_TAG, ns, "fiche");
	            String id = readText(parser);
				refCommands.add(new AutreDenomination_setFiche_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "fiche");	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	PhotoFiche readPhotoFiche(XmlPullParser parser)  throws XmlPullParserException, IOException{
		PhotoFiche result = new PhotoFiche();

		parser.require(XmlPullParser.START_TAG, ns, "PHOTOFICHE");
    	String currentTagName = parser.getName();
    			
    	xmlId2PhotoFiche.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals("cleURL")) {
				parser.require(XmlPullParser.START_TAG, ns, "cleURL");
	            result.setCleURL(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "cleURL");
	        } else
			//TODO if (currentTagName.equals("IMAGEVIGNETTE")) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (currentTagName.equals("IMAGEMOYENNE")) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (currentTagName.equals("IMAGEGRANDE")) {
	        //    title = readTitle(parser);
	        //} else	
			if (currentTagName.equals("titre")) {
				parser.require(XmlPullParser.START_TAG, ns, "titre");
	            result.setTitre(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "titre");
	        } else
			if (currentTagName.equals("description")) {
				parser.require(XmlPullParser.START_TAG, ns, "description");
	            result.setDescription(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "description");
	        } else
			if (currentTagName.equals("fiche")) {	
				parser.require(XmlPullParser.START_TAG, ns, "fiche");
	            String id = readText(parser);
				refCommands.add(new PhotoFiche_setFiche_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "fiche");	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	SectionFiche readSectionFiche(XmlPullParser parser)  throws XmlPullParserException, IOException{
		SectionFiche result = new SectionFiche();

		parser.require(XmlPullParser.START_TAG, ns, "SECTIONFICHE");
    	String currentTagName = parser.getName();
    			
    	xmlId2SectionFiche.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals("titre")) {
				parser.require(XmlPullParser.START_TAG, ns, "titre");
	            result.setTitre(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "titre");
	        } else
			if (currentTagName.equals("texte")) {
				parser.require(XmlPullParser.START_TAG, ns, "texte");
	            result.setTexte(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "texte");
	        } else
			if (currentTagName.equals("fiche")) {	
				parser.require(XmlPullParser.START_TAG, ns, "fiche");
	            String id = readText(parser);
				refCommands.add(new SectionFiche_setFiche_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "fiche");	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	Participant readParticipant(XmlPullParser parser)  throws XmlPullParserException, IOException{
		Participant result = new Participant();

		parser.require(XmlPullParser.START_TAG, ns, "PARTICIPANT");
    	String currentTagName = parser.getName();
    			
    	xmlId2Participant.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals("nom")) {
				parser.require(XmlPullParser.START_TAG, ns, "nom");
	            result.setNom(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "nom");
	        } else
					// TODO deal with owned ref photo
			if (currentTagName.equals("fichesVerifiees")) {	
				parser.require(XmlPullParser.START_TAG, ns, "fichesVerifiees");
	            String id = readText(parser);
				refCommands.add(new Participant_setFichesVerifiees_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "fichesVerifiees");	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	PhotoParticipant readPhotoParticipant(XmlPullParser parser)  throws XmlPullParserException, IOException{
		PhotoParticipant result = new PhotoParticipant();

		parser.require(XmlPullParser.START_TAG, ns, "PHOTOPARTICIPANT");
    	String currentTagName = parser.getName();
    			
    	xmlId2PhotoParticipant.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals("cleURL")) {
				parser.require(XmlPullParser.START_TAG, ns, "cleURL");
	            result.setCleURL(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "cleURL");
	        } else
			//TODO if (currentTagName.equals("IMAGE")) {
	        //    title = readTitle(parser);
	        //} else	
			if (currentTagName.equals("participant")) {	
				parser.require(XmlPullParser.START_TAG, ns, "participant");
	            String id = readText(parser);
				refCommands.add(new PhotoParticipant_setParticipant_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "participant");	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	ZoneGeographique readZoneGeographique(XmlPullParser parser)  throws XmlPullParserException, IOException{
		ZoneGeographique result = new ZoneGeographique();

		parser.require(XmlPullParser.START_TAG, ns, "ZONEGEOGRAPHIQUE");
    	String currentTagName = parser.getName();
    			
    	xmlId2ZoneGeographique.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals("nom")) {
				parser.require(XmlPullParser.START_TAG, ns, "nom");
	            result.setNom(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "nom");
	        } else
			if (currentTagName.equals("fiches")) {	
				parser.require(XmlPullParser.START_TAG, ns, "fiches");
	            String id = readText(parser);
				refCommands.add(new ZoneGeographique_setFiches_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "fiches");	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	ZoneObservation readZoneObservation(XmlPullParser parser)  throws XmlPullParserException, IOException{
		ZoneObservation result = new ZoneObservation();

		parser.require(XmlPullParser.START_TAG, ns, "ZONEOBSERVATION");
    	String currentTagName = parser.getName();
    			
    	xmlId2ZoneObservation.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			if (currentTagName.equals("nom")) {
				parser.require(XmlPullParser.START_TAG, ns, "nom");
	            result.setNom(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "nom");
	        } else
			if (currentTagName.equals("fiches")) {	
				parser.require(XmlPullParser.START_TAG, ns, "fiches");
	            String id = readText(parser);
				refCommands.add(new ZoneObservation_setFiches_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "fiches");	    
	        } else
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	Groupe readGroupe(XmlPullParser parser)  throws XmlPullParserException, IOException{
		Groupe result = new Groupe();

		parser.require(XmlPullParser.START_TAG, ns, "GROUPE");
    	String currentTagName = parser.getName();
    			
    	xmlId2Groupe.put(parser.getAttributeValue(null, "id"),result);		
		result.setNomGroupe(parser.getAttributeValue(null, "nomGroupe"));
		result.setDescriptionGroupe(parser.getAttributeValue(null, "descriptionGroupe"));
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        currentTagName = parser.getName();
			//TODO if (currentTagName.equals("NUMEROGROUPE")) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (currentTagName.equals("NUMEROSOUSGROUPE")) {
	        //    title = readTitle(parser);
	        //} else	
			if (currentTagName.equals("groupesFils")) {
				List<Groupe> entries = readGroupes(parser,"groupesFils");	
				groupes.addAll(entries); // add for inclusion in the DB
				//result.getGroupesFils().addAll(entries);  //  doesn't work and need to be done in the other way round using the opposite
				refCommands.add(new Groupe_addContainedGroupesFils_RefCommand(result,entries));	    
	        } else
			if (currentTagName.equals("groupePere")) {	
				parser.require(XmlPullParser.START_TAG, ns, "groupePere");
	            String id = readText(parser);
				refCommands.add(new Groupe_setGroupePere_RefCommand(result,id, this));
				parser.require(XmlPullParser.END_TAG, ns, "groupePere");	    
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
	class Fiche_setZonesGeographiques_RefCommand extends RefCommand{
		Fiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Fiche_setZonesGeographiques_RefCommand(Fiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setZonesGeographiques(parser.xmlId2ZoneGeographique.get(referencedElementID));
			fichesToUpdate.add(self);
		}
	}
	class Fiche_setZonesObservation_RefCommand extends RefCommand{
		Fiche self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public Fiche_setZonesObservation_RefCommand(Fiche self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setZonesObservation(parser.xmlId2ZoneObservation.get(referencedElementID));
			fichesToUpdate.add(self);
		}
	}
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
	class Fiche_setContainedAutresDenominations_RefCommand extends RefCommand{
	Fiche container;
		AutreDenomination containedElement;
		
		public Fiche_setContainedAutresDenominations_RefCommand(Fiche container,
				AutreDenomination containedElement) {
			super();
			this.container = container;
			this.containedElement = containedElement;
		}

		@Override
		public void run() {
			containedElement.setFiche(container);
			autreDenominationsToUpdate.add(containedElement);			
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
	class ZoneGeographique_setFiches_RefCommand extends RefCommand{
		ZoneGeographique self;
		String referencedElementID;
		DorisDBXMLParser parser;
		
		public ZoneGeographique_setFiches_RefCommand(ZoneGeographique self,
				String referencedElementID, DorisDBXMLParser parser) {
			super();
			this.self = self;
			this.referencedElementID = referencedElementID;
			this.parser = parser;
		}

		@Override
		public void run() {
			self.setFiches(parser.xmlId2Fiche.get(referencedElementID));
			zoneGeographiquesToUpdate.add(self);
		}
	}
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
