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
import java.util.Hashtable;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import fr.ffessm.doris.android.datamodel.associations.*;
import fr.ffessm.doris.android.datamodel.*;
// Start of user code additional import for DorisDBXMLParser
// End of user code

/**
 * Root Sax XML DefaultHandler for parsing the datamodel DorisDB
 */
public class DorisDBXMLParser {
	// Start of user code additional handler code 1
	// End of user code
	List<Fiche> fiches = new ArrayList<Fiche>();
	List<AutreDenomination> autreDenominations = new ArrayList<AutreDenomination>();
	List<PhotoFiche> photoFiches = new ArrayList<PhotoFiche>();
	List<SectionFiche> sectionFiches = new ArrayList<SectionFiche>();
	List<Participant> participants = new ArrayList<Participant>();
	List<PhotoParticipant> photoParticipants = new ArrayList<PhotoParticipant>();
	List<ZoneGeographique> zoneGeographiques = new ArrayList<ZoneGeographique>();
	List<ZoneObservation> zoneObservations = new ArrayList<ZoneObservation>();
	List<Groupe> groupes = new ArrayList<Groupe>();
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
	            fiches = readFiches(parser);
	        } else 
		 	if (name.equals("AUTREDENOMINATIONS")) {
	            autreDenominations = readAutreDenominations(parser);
	        } else 
		 	if (name.equals("PHOTOFICHES")) {
	            photoFiches = readPhotoFiches(parser);
	        } else 
		 	if (name.equals("SECTIONFICHES")) {
	            sectionFiches = readSectionFiches(parser);
	        } else 
		 	if (name.equals("PARTICIPANTS")) {
	            participants = readParticipants(parser);
	        } else 
		 	if (name.equals("PHOTOPARTICIPANTS")) {
	            photoParticipants = readPhotoParticipants(parser);
	        } else 
		 	if (name.equals("ZONEGEOGRAPHIQUES")) {
	            zoneGeographiques = readZoneGeographiques(parser);
	        } else 
		 	if (name.equals("ZONEOBSERVATIONS")) {
	            zoneObservations = readZoneObservations(parser);
	        } else 
		 	if (name.equals("GROUPES")) {
	            groupes = readGroupes(parser);
	        } else 
			{
	            skip(parser);
	        }
	    }
		
	}

	List<Fiche> readFiches(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<Fiche> entries = new ArrayList<Fiche>();
		parser.require(XmlPullParser.START_TAG, ns, "FICHES");
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
	List<AutreDenomination> readAutreDenominations(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<AutreDenomination> entries = new ArrayList<AutreDenomination>();
		parser.require(XmlPullParser.START_TAG, ns, "AUTREDENOMINATIONS");
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
	List<PhotoFiche> readPhotoFiches(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<PhotoFiche> entries = new ArrayList<PhotoFiche>();
		parser.require(XmlPullParser.START_TAG, ns, "PHOTOFICHES");
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
	List<SectionFiche> readSectionFiches(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<SectionFiche> entries = new ArrayList<SectionFiche>();
		parser.require(XmlPullParser.START_TAG, ns, "SECTIONFICHES");
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
	List<Participant> readParticipants(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<Participant> entries = new ArrayList<Participant>();
		parser.require(XmlPullParser.START_TAG, ns, "PARTICIPANTS");
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
	List<PhotoParticipant> readPhotoParticipants(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<PhotoParticipant> entries = new ArrayList<PhotoParticipant>();
		parser.require(XmlPullParser.START_TAG, ns, "PHOTOPARTICIPANTS");
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
	List<ZoneGeographique> readZoneGeographiques(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<ZoneGeographique> entries = new ArrayList<ZoneGeographique>();
		parser.require(XmlPullParser.START_TAG, ns, "ZONEGEOGRAPHIQUES");
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
	List<ZoneObservation> readZoneObservations(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<ZoneObservation> entries = new ArrayList<ZoneObservation>();
		parser.require(XmlPullParser.START_TAG, ns, "ZONEOBSERVATIONS");
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
	List<Groupe> readGroupes(XmlPullParser parser)  throws XmlPullParserException, IOException{
		List<Groupe> entries = new ArrayList<Groupe>();
		parser.require(XmlPullParser.START_TAG, ns, "GROUPES");
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
    	String tag = parser.getName();
    			
    	xmlId2Fiche.put(parser.getAttributeValue(null, "id"),result);		
		result.setNomScientifique(parser.getAttributeValue(null, "nomScientifique"));
		result.setNomCommun(parser.getAttributeValue(null, "nomCommun"));
		// TODO numeroFiche = parser.getAttributeValue(null, "NUMEROFICHE");
		// TODO etatFiche = parser.getAttributeValue(null, "ETATFICHE");
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("DATECREATION")) {
				parser.require(XmlPullParser.START_TAG, ns, "DATECREATION");
	            result.setDateCreation(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "DATECREATION");
	        } else
			if (name.equals("DATEMODIFICATION")) {
				parser.require(XmlPullParser.START_TAG, ns, "DATEMODIFICATION");
	            result.setDateModification(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "DATEMODIFICATION");
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
    	String tag = parser.getName();
    			
    	xmlId2AutreDenomination.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("DENOMINATION")) {
				parser.require(XmlPullParser.START_TAG, ns, "DENOMINATION");
	            result.setDenomination(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "DENOMINATION");
	        } else
			if (name.equals("LANGUE")) {
				parser.require(XmlPullParser.START_TAG, ns, "LANGUE");
	            result.setLangue(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "LANGUE");
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
    	String tag = parser.getName();
    			
    	xmlId2PhotoFiche.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("CLEURL")) {
				parser.require(XmlPullParser.START_TAG, ns, "CLEURL");
	            result.setCleURL(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "CLEURL");
	        } else
			//TODO if (name.equals("IMAGEVIGNETTE")) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (name.equals("IMAGEMOYENNE")) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (name.equals("IMAGEGRANDE")) {
	        //    title = readTitle(parser);
	        //} else	
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	SectionFiche readSectionFiche(XmlPullParser parser)  throws XmlPullParserException, IOException{
		SectionFiche result = new SectionFiche();

		parser.require(XmlPullParser.START_TAG, ns, "SECTIONFICHE");
    	String tag = parser.getName();
    			
    	xmlId2SectionFiche.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("TITRE")) {
				parser.require(XmlPullParser.START_TAG, ns, "TITRE");
	            result.setTitre(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "TITRE");
	        } else
			if (name.equals("TEXTE")) {
				parser.require(XmlPullParser.START_TAG, ns, "TEXTE");
	            result.setTexte(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "TEXTE");
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
    	String tag = parser.getName();
    			
    	xmlId2Participant.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("NOM")) {
				parser.require(XmlPullParser.START_TAG, ns, "NOM");
	            result.setNom(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "NOM");
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
    	String tag = parser.getName();
    			
    	xmlId2PhotoParticipant.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("CLEURL")) {
				parser.require(XmlPullParser.START_TAG, ns, "CLEURL");
	            result.setCleURL(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "CLEURL");
	        } else
			//TODO if (name.equals("IMAGE")) {
	        //    title = readTitle(parser);
	        //} else	
	        {
	            skip(parser);
	        }
	    }

		return result;
	}
	ZoneGeographique readZoneGeographique(XmlPullParser parser)  throws XmlPullParserException, IOException{
		ZoneGeographique result = new ZoneGeographique();

		parser.require(XmlPullParser.START_TAG, ns, "ZONEGEOGRAPHIQUE");
    	String tag = parser.getName();
    			
    	xmlId2ZoneGeographique.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("NOM")) {
				parser.require(XmlPullParser.START_TAG, ns, "NOM");
	            result.setNom(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "NOM");
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
    	String tag = parser.getName();
    			
    	xmlId2ZoneObservation.put(parser.getAttributeValue(null, "id"),result);		
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			if (name.equals("NOM")) {
				parser.require(XmlPullParser.START_TAG, ns, "NOM");
	            result.setNom(readText(parser));
				parser.require(XmlPullParser.END_TAG, ns, "NOM");
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
    	String tag = parser.getName();
    			
    	xmlId2Groupe.put(parser.getAttributeValue(null, "id"),result);		
		result.setNomGroupe(parser.getAttributeValue(null, "nomGroupe"));
		result.setDescriptionGroupe(parser.getAttributeValue(null, "descriptionGroupe"));
		while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
			//TODO if (name.equals("NUMEROGROUPE")) {
	        //    title = readTitle(parser);
	        //} else	
			//TODO if (name.equals("NUMEROSOUSGROUPE")) {
	        //    title = readTitle(parser);
	        //} else	
	        {
	            skip(parser);
	        }
	    }

		return result;
	}

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
