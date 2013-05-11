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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import fr.ffessm.doris.android.datamodel.associations.*;
import fr.ffessm.doris.android.datamodel.*;
// Start of user code additional import for DorisDBXMLParser
// End of user code

/**
 * Root Sax XML DefaultHandler for parsing the datamodel DorisDB
 */
public class DorisDBXMLParser extends DefaultHandler {
	// Start of user code additional handler code 1
	// End of user code
	private XMLReader reader;
   // private List<Team> teams;

    public DorisDBXMLParser(XMLReader reader) {
        this.reader = reader;
     //   this.teams = new LinkedList<Team>();
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

		if (name.equals("FICHES")) {
            // Switch handler to parse the Fiche element
            reader.setContentHandler(new FicheXMLParser(reader, this));
        }
		if (name.equals("AUTREDENOMINATIONS")) {
            // Switch handler to parse the AutreDenomination element
            reader.setContentHandler(new AutreDenominationXMLParser(reader, this));
        }
		if (name.equals("PHOTOFICHES")) {
            // Switch handler to parse the PhotoFiche element
            reader.setContentHandler(new PhotoFicheXMLParser(reader, this));
        }
		if (name.equals("SECTIONFICHES")) {
            // Switch handler to parse the SectionFiche element
            reader.setContentHandler(new SectionFicheXMLParser(reader, this));
        }
		if (name.equals("PARTICIPANTS")) {
            // Switch handler to parse the Participant element
            reader.setContentHandler(new ParticipantXMLParser(reader, this));
        }
		if (name.equals("PHOTOPARTICIPANTS")) {
            // Switch handler to parse the PhotoParticipant element
            reader.setContentHandler(new PhotoParticipantXMLParser(reader, this));
        }
		if (name.equals("ZONEGEOGRAPHIQUES")) {
            // Switch handler to parse the ZoneGeographique element
            reader.setContentHandler(new ZoneGeographiqueXMLParser(reader, this));
        }
		if (name.equals("ZONEOBSERVATIONS")) {
            // Switch handler to parse the ZoneObservation element
            reader.setContentHandler(new ZoneObservationXMLParser(reader, this));
        }
		if (name.equals("GROUPES")) {
            // Switch handler to parse the Groupe element
            reader.setContentHandler(new GroupeXMLParser(reader, this));
        }


        
    }
	// Start of user code additional handler code 2
	// End of user code
}
