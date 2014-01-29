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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

import fr.ffessm.doris.android.datamodel.associations.*;
import fr.ffessm.doris.android.datamodel.*;
import fr.ffessm.doris.android.datamodel.xml.DorisDBXMLParser.RefCommand;
// Start of user code additional import
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// End of user code

/**
 * Class used to simplify the access to the XML tools in the application
 */
public class XMLHelper {
	// Start of user code additional helper code 1
	private static Log log = LogFactory.getLog(XMLHelper.class);

	// End of user code

	public static void saveDBToFile(File file,DorisDBHelper dbContext){
		try {
			// Create file
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(generateXML4DB(dbContext));
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static String generateXML4DB(DorisDBHelper dbContext){
		StringBuilder sb = new StringBuilder();
		sb.append("<DORISDB>");
		sb.append("\n\t<FICHES>");
		try {	
			List<Fiche> fiches = dbContext.ficheDao.queryForAll();
			for(Fiche  fiche : fiches){
				// TODO find if contained by another element, if not put it there
					sb.append("\n");
					sb.append(fiche.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</FICHES>\n");
		sb.append("\n\t<AUTREDENOMINATIONS>");
		try {	
			List<AutreDenomination> autreDenominations = dbContext.autreDenominationDao.queryForAll();
			for(AutreDenomination  autreDenomination : autreDenominations){
				// TODO find if contained by another element, if not put it there
				boolean isContained = false;
				if(autreDenomination.getFiche() != null){
					isContained = true;
				}
				if(!isContained){
					sb.append("\n");
					sb.append(autreDenomination.toXML("\t\t", dbContext));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</AUTREDENOMINATIONS>\n");
		sb.append("\n\t<PHOTOFICHES>");
		try {	
			List<PhotoFiche> photoFiches = dbContext.photoFicheDao.queryForAll();
			for(PhotoFiche  photoFiche : photoFiches){
				// TODO find if contained by another element, if not put it there
				boolean isContained = false;
				if(photoFiche.getFiche() != null){
					isContained = true;
				}
				if(!isContained){
					sb.append("\n");
					sb.append(photoFiche.toXML("\t\t", dbContext));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</PHOTOFICHES>\n");
		sb.append("\n\t<SECTIONFICHES>");
		try {	
			List<SectionFiche> sectionFiches = dbContext.sectionFicheDao.queryForAll();
			for(SectionFiche  sectionFiche : sectionFiches){
				// TODO find if contained by another element, if not put it there
				boolean isContained = false;
				if(sectionFiche.getFiche() != null){
					isContained = true;
				}
				if(!isContained){
					sb.append("\n");
					sb.append(sectionFiche.toXML("\t\t", dbContext));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</SECTIONFICHES>\n");
		sb.append("\n\t<INTERVENANTFICHES>");
		try {	
			List<IntervenantFiche> intervenantFiches = dbContext.intervenantFicheDao.queryForAll();
			for(IntervenantFiche  intervenantFiche : intervenantFiches){
				// TODO find if contained by another element, if not put it there
					sb.append("\n");
					sb.append(intervenantFiche.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</INTERVENANTFICHES>\n");
		sb.append("\n\t<PARTICIPANTS>");
		try {	
			List<Participant> participants = dbContext.participantDao.queryForAll();
			for(Participant  participant : participants){
				// TODO find if contained by another element, if not put it there
					sb.append("\n");
					sb.append(participant.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</PARTICIPANTS>\n");
		sb.append("\n\t<ZONEGEOGRAPHIQUES>");
		try {	
			List<ZoneGeographique> zoneGeographiques = dbContext.zoneGeographiqueDao.queryForAll();
			for(ZoneGeographique  zoneGeographique : zoneGeographiques){
				// TODO find if contained by another element, if not put it there
					sb.append("\n");
					sb.append(zoneGeographique.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</ZONEGEOGRAPHIQUES>\n");
		sb.append("\n\t<ZONEOBSERVATIONS>");
		try {	
			List<ZoneObservation> zoneObservations = dbContext.zoneObservationDao.queryForAll();
			for(ZoneObservation  zoneObservation : zoneObservations){
				// TODO find if contained by another element, if not put it there
					sb.append("\n");
					sb.append(zoneObservation.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</ZONEOBSERVATIONS>\n");
		sb.append("\n\t<GROUPES>");
		try {	
			List<Groupe> groupes = dbContext.groupeDao.queryForAll();
			for(Groupe  groupe : groupes){
				// TODO find if contained by another element, if not put it there
				boolean isContained = false;
				if(!isContained){
					sb.append("\n");
					sb.append(groupe.toXML("\t\t", dbContext));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</GROUPES>\n");
		sb.append("\n\t<DEFINITIONGLOSSAIRES>");
		try {	
			List<DefinitionGlossaire> definitionGlossaires = dbContext.definitionGlossaireDao.queryForAll();
			for(DefinitionGlossaire  definitionGlossaire : definitionGlossaires){
				// TODO find if contained by another element, if not put it there
					sb.append("\n");
					sb.append(definitionGlossaire.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</DEFINITIONGLOSSAIRES>\n");
		sb.append("\n\t<DORISDB_METADATAS>");
		try {	
			List<DorisDB_metadata> dorisDB_metadatas = dbContext.dorisDB_metadataDao.queryForAll();
			for(DorisDB_metadata  dorisDB_metadata : dorisDB_metadatas){
				// TODO find if contained by another element, if not put it there
					sb.append("\n");
					sb.append(dorisDB_metadata.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</DORISDB_METADATAS>\n");
		sb.append("\n\t<ENTREEBIBLIOGRAPHIES>");
		try {	
			List<EntreeBibliographie> entreeBibliographies = dbContext.entreeBibliographieDao.queryForAll();
			for(EntreeBibliographie  entreeBibliographie : entreeBibliographies){
				// TODO find if contained by another element, if not put it there
					sb.append("\n");
					sb.append(entreeBibliographie.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</ENTREEBIBLIOGRAPHIES>\n");
		sb.append("\n</DORISDB>");
		return sb.toString();
	}
	
	public static void loadDBFromXMLFile(DorisDBHelper dbContext, File file){
		try{
			loadDBFromXMLFile(dbContext, new FileInputStream(file));
		} catch (FileNotFoundException e) {
			log.error("File not found "+e.getMessage(),e);
		}
	}

	public static void loadDBFromXMLFile(DorisDBHelper dbContext, InputStream inputStream){
		DorisDBXMLParser parser = new DorisDBXMLParser();
		try {
			log.info("starting parsing...");
			parser.parse(inputStream);
			// create the elements in the DB
			log.info("starting creation of Fiche...");
			for(Fiche fiche : parser.fiches){
				try {
					dbContext.ficheDao.create(fiche);
				} catch (SQLException e) {
					log.error("cannot create Fiche "+e.getMessage(),e);
				}
			}
			log.info("starting creation of AutreDenomination...");
			for(AutreDenomination autreDenomination : parser.autreDenominations){
				try {
					dbContext.autreDenominationDao.create(autreDenomination);
				} catch (SQLException e) {
					log.error("cannot create AutreDenomination "+e.getMessage(),e);
				}
			}
			log.info("starting creation of PhotoFiche...");
			for(PhotoFiche photoFiche : parser.photoFiches){
				try {
					dbContext.photoFicheDao.create(photoFiche);
				} catch (SQLException e) {
					log.error("cannot create PhotoFiche "+e.getMessage(),e);
				}
			}
			log.info("starting creation of SectionFiche...");
			for(SectionFiche sectionFiche : parser.sectionFiches){
				try {
					dbContext.sectionFicheDao.create(sectionFiche);
				} catch (SQLException e) {
					log.error("cannot create SectionFiche "+e.getMessage(),e);
				}
			}
			log.info("starting creation of IntervenantFiche...");
			for(IntervenantFiche intervenantFiche : parser.intervenantFiches){
				try {
					dbContext.intervenantFicheDao.create(intervenantFiche);
				} catch (SQLException e) {
					log.error("cannot create IntervenantFiche "+e.getMessage(),e);
				}
			}
			log.info("starting creation of Participant...");
			for(Participant participant : parser.participants){
				try {
					dbContext.participantDao.create(participant);
				} catch (SQLException e) {
					log.error("cannot create Participant "+e.getMessage(),e);
				}
			}
			log.info("starting creation of ZoneGeographique...");
			for(ZoneGeographique zoneGeographique : parser.zoneGeographiques){
				try {
					dbContext.zoneGeographiqueDao.create(zoneGeographique);
				} catch (SQLException e) {
					log.error("cannot create ZoneGeographique "+e.getMessage(),e);
				}
			}
			log.info("starting creation of ZoneObservation...");
			for(ZoneObservation zoneObservation : parser.zoneObservations){
				try {
					dbContext.zoneObservationDao.create(zoneObservation);
				} catch (SQLException e) {
					log.error("cannot create ZoneObservation "+e.getMessage(),e);
				}
			}
			log.info("starting creation of Groupe...");
			for(Groupe groupe : parser.groupes){
				try {
					dbContext.groupeDao.create(groupe);
				} catch (SQLException e) {
					log.error("cannot create Groupe "+e.getMessage(),e);
				}
			}
			log.info("starting creation of DefinitionGlossaire...");
			for(DefinitionGlossaire definitionGlossaire : parser.definitionGlossaires){
				try {
					dbContext.definitionGlossaireDao.create(definitionGlossaire);
				} catch (SQLException e) {
					log.error("cannot create DefinitionGlossaire "+e.getMessage(),e);
				}
			}
			log.info("starting creation of DorisDB_metadata...");
			for(DorisDB_metadata dorisDB_metadata : parser.dorisDB_metadatas){
				try {
					dbContext.dorisDB_metadataDao.create(dorisDB_metadata);
				} catch (SQLException e) {
					log.error("cannot create DorisDB_metadata "+e.getMessage(),e);
				}
			}
			log.info("starting creation of EntreeBibliographie...");
			for(EntreeBibliographie entreeBibliographie : parser.entreeBibliographies){
				try {
					dbContext.entreeBibliographieDao.create(entreeBibliographie);
				} catch (SQLException e) {
					log.error("cannot create EntreeBibliographie "+e.getMessage(),e);
				}
			}
			log.info("starting crossref...");
			// proceed with cross ref
			for (RefCommand command : parser.refCommands) {
				command.run();
			}
			
			// update the DB
			log.info("starting update DB of Fiche...");
			for(Fiche elem : parser.fichesToUpdate){
				try {
					dbContext.ficheDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update Fiche "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of AutreDenomination...");
			for(AutreDenomination elem : parser.autreDenominationsToUpdate){
				try {
					dbContext.autreDenominationDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update AutreDenomination "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of PhotoFiche...");
			for(PhotoFiche elem : parser.photoFichesToUpdate){
				try {
					dbContext.photoFicheDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update PhotoFiche "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of SectionFiche...");
			for(SectionFiche elem : parser.sectionFichesToUpdate){
				try {
					dbContext.sectionFicheDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update SectionFiche "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of IntervenantFiche...");
			for(IntervenantFiche elem : parser.intervenantFichesToUpdate){
				try {
					dbContext.intervenantFicheDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update IntervenantFiche "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of Participant...");
			for(Participant elem : parser.participantsToUpdate){
				try {
					dbContext.participantDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update Participant "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of ZoneGeographique...");
			for(ZoneGeographique elem : parser.zoneGeographiquesToUpdate){
				try {
					dbContext.zoneGeographiqueDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update ZoneGeographique "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of ZoneObservation...");
			for(ZoneObservation elem : parser.zoneObservationsToUpdate){
				try {
					dbContext.zoneObservationDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update ZoneObservation "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of Groupe...");
			for(Groupe elem : parser.groupesToUpdate){
				try {
					dbContext.groupeDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update Groupe "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of DefinitionGlossaire...");
			for(DefinitionGlossaire elem : parser.definitionGlossairesToUpdate){
				try {
					dbContext.definitionGlossaireDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update DefinitionGlossaire "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of DorisDB_metadata...");
			for(DorisDB_metadata elem : parser.dorisDB_metadatasToUpdate){
				try {
					dbContext.dorisDB_metadataDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update DorisDB_metadata "+e.getMessage(),e);
				}
			}
			log.info("starting update DB of EntreeBibliographie...");
			for(EntreeBibliographie elem : parser.entreeBibliographiesToUpdate){
				try {
					dbContext.entreeBibliographieDao.update(elem);
				} catch (SQLException e) {
					log.error("cannot update EntreeBibliographie "+e.getMessage(),e);
				}
			}
			log.info("DB filled from XML");
		} catch (XmlPullParserException e) {
			log.error("XML parse error "+e.getMessage(),e);
		} catch (IOException e) {
			log.error("Read error "+e.getMessage(),e);
		}
		// Start of user code loadDBFromXMLFile 2
		// End of user code
	}
	
	// Start of user code additional helper code 2
	// End of user code
}
