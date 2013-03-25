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
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;
import fr.ffessm.doris.android.datamodel.associations.*;
import fr.ffessm.doris.android.datamodel.*;
// Start of user code additional import
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// End of user code

/**
 * Class used to simplify the access to the XML tools in the application
 */
public class XMLHelper {
	// Start of user code additional helper code 1
	private Log log = LogFactory.getLog(XMLHelper.class);

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
				sb.append("\n");
				sb.append(fiche.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</FICHES>\n");
		sb.append("\n\t<PARTICIPANTS>");
		try {	
			List<Participant> participants = dbContext.participantDao.queryForAll();
			for(Participant  participant : participants){
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
				sb.append("\n");
				sb.append(zoneObservation.toXML("\t\t", dbContext));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\n\t</ZONEOBSERVATIONS>\n");
		sb.append("\n</DORISDB>");
		return sb.toString();
	}
	
	// Start of user code additional helper code 2
	// End of user code
}
