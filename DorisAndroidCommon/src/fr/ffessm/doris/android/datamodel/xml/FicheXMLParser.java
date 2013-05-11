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
//Start of user code additional import for FicheXMLParser
//End of user code
public class FicheXMLParser extends DefaultHandler{

	private XMLReader reader;
    private DefaultHandler parentHandler;
    private Fiche currentFiche;
    private StringBuilder content;

 	public FicheXMLParser(XMLReader reader, DefaultHandler parentHandler) {
        this.reader = reader;
        this.parentHandler = parentHandler;
        this.content = new StringBuilder();
    }

	// characters can be called multiple times per element so aggregate the content in a StringBuilder
    public void characters(char[] ch, int start, int length) throws SAXException {
        content.append(ch, start, length);
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		if(name.equals(Fiche.XML_FICHE)){

        	this.currentFiche = new Fiche();			
			// deal with simple DataAttribute
			this.currentFiche.setNomScientifique(attributes.getValue(Fiche.XML_ATT_NOMSCIENTIFIQUE));
			this.currentFiche.setNomCommun(attributes.getValue(Fiche.XML_ATT_NOMCOMMUN));
			// TODO this.currentFiche.setNumeroFiche(attributes.getValue(Fiche.XML_ATT_NUMEROFICHE));			
			// TODO this.currentFiche.setEtatFiche(attributes.getValue(Fiche.XML_ATT_ETATFICHE));			
		}
		// reset content for current element (mixed content XML syntax not allowed)
        content.setLength(0);
	
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
		if(name.equals(Fiche.XML_FICHE)){
			// TODO store in the parent or database
		}
		// deal with not simple DataAttribute
		else if (name.equals(Fiche.XML_ATT_DATECREATION)) {
			this.currentFiche.setDateCreation(content.toString());
    	}
		else if (name.equals(Fiche.XML_ATT_DATEMODIFICATION)) {
			this.currentFiche.setDateModification(content.toString());
    	}
		//TODO deal with one 2 one reference
		// if(this.redacteurs!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_REDACTEURS+">");
		//	sb.append(this.redacteurs);
	    //	sb.append("</"+XML_REF_REDACTEURS+">");
		//}
		//TODO deal with one 2 one reference
		// if(this.photosFiche!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_PHOTOSFICHE+">");
		//	sb.append(this.photosFiche);
	    //	sb.append("</"+XML_REF_PHOTOSFICHE+">");
		//}
		//TODO deal with one 2 one reference
		// if(this.zonesGeographiques!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_ZONESGEOGRAPHIQUES+">");
		//	sb.append(this.zonesGeographiques);
	    //	sb.append("</"+XML_REF_ZONESGEOGRAPHIQUES+">");
		//}
		//TODO deal with one 2 one reference
		// if(this.zonesObservation!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_ZONESOBSERVATION+">");
		//	sb.append(this.zonesObservation);
	    //	sb.append("</"+XML_REF_ZONESOBSERVATION+">");
		//}
		//TODO deal with one 2 one reference
		// if(this.verificateurs!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_VERIFICATEURS+">");
		//	sb.append(this.verificateurs);
	    //	sb.append("</"+XML_REF_VERIFICATEURS+">");
		//}
		//TODO deal with one 2 one reference
		// if(this.responsableRegional!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_RESPONSABLEREGIONAL+">");
		//	sb.append(this.responsableRegional);
	    //	sb.append("</"+XML_REF_RESPONSABLEREGIONAL+">");
		//}
		// deal with many 2 one contained reference
		//sb.append("\n"+indent+"\t<"+XML_REF_CONTENU+">");
		//if(this.contenu != null){
		//	for(SectionFiche ref : this.contenu){
		//		sb.append("\n"+ref.toXML(indent+"\t\t", contextDB));
	    //	}
		//}
		//sb.append("</"+XML_REF_CONTENU+">");		
		//TODO deal with one 2 one reference
		// if(this.photoPrincipale!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_PHOTOPRINCIPALE+">");
		//	sb.append(this.photoPrincipale);
	    //	sb.append("</"+XML_REF_PHOTOPRINCIPALE+">");
		//}
		//TODO deal with one 2 one reference
		// if(this.autresDenominations!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_AUTRESDENOMINATIONS+">");
		//	sb.append(this.autresDenominations);
	    //	sb.append("</"+XML_REF_AUTRESDENOMINATIONS+">");
		//}
		//TODO deal with one 2 one reference
		// if(this.groupe!= null){
		//	sb.append("\n"+indent+"\t<"+XML_REF_GROUPE+">");
		//	sb.append(this.groupe);
	    //	sb.append("</"+XML_REF_GROUPE+">");
		//}
		else if (name.equals("FICHES")) {            
            // Switch handler back to our parent
            reader.setContentHandler(parentHandler);
        }
    }

//Start of user code additional code for FicheXMLParser
//End of user code
	
}
