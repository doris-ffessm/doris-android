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
//Start of user code additional import for ZoneGeographiqueXMLParser
//End of user code
public class ZoneGeographiqueXMLParser extends DefaultHandler{

	private XMLReader reader;
    private DefaultHandler parentHandler;
    private ZoneGeographique currentZoneGeographique;
    private StringBuilder content;

 	public ZoneGeographiqueXMLParser(XMLReader reader, DefaultHandler parentHandler) {
        this.reader = reader;
        this.parentHandler = parentHandler;
        this.content = new StringBuilder();
    }

	// characters can be called multiple times per element so aggregate the content in a StringBuilder
    public void characters(char[] ch, int start, int length) throws SAXException {
        content.append(ch, start, length);
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if(name.equals(ZoneGeographique.XML_ZONEGEOGRAPHIQUE)){

        	this.currentZoneGeographique = new ZoneGeographique();			
			// deal with simple DataAttribute
		}
		// reset content for current element (mixed content XML syntax not allowed)
        content.setLength(0);
	
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
		if(name.equals(ZoneGeographique.XML_ZONEGEOGRAPHIQUE)){
			// TODO store in the parent or database
		}
		// deal with not simple DataAttribute
		else if (name.equals(ZoneGeographique.XML_ATT_NOM)) {
			this.currentZoneGeographique.setNom(content.toString());
    	}
		//TODO deal with one 2 one reference
		// if(this.fiches!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_FICHES+">");
		//	sb.append(this.fiches);
	    //	sb.append("</"+XML_REF_FICHES+">");
		//}
		else if (name.equals("ZONEGEOGRAPHIQUES")) {            
            // Switch handler back to our parent
            reader.setContentHandler(parentHandler);
        }
    }

//Start of user code additional code for ZoneGeographiqueXMLParser
//End of user code
	
}
