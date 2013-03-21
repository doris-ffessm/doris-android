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


package fr.ffessm.doris.prefetch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/* Niveaux de trace de org.apache.commons.logging
# fatal = Level.SEVERE
# error = Level.SEVERE
# warn = Level.WARNING
# info = Level.INFO
# debug = Level.FINE
# trace = Level.FINEST
*/


/* *********************************************************************
 * Log
 ********************************************************************** */
public class Trace {
    private final static String LOGTAG = "Log";
    public Log log = LogFactory.getLog(Trace.class);
    
    
    public final int LOG_SILENCE = 9;
    public final int LOG_MESSAGE = 4;
    public final int LOG_WARNING = 3;
    public final int LOG_ERROR = 2;
    public final int LOG_VERBOSE = 1;
    public final int LOG_DEBUG = 0;
    
    public static int niveauTrace;
    
    Trace (){
    	niveauTrace = LOG_WARNING;
    	
    	log(LOG_DEBUG, LOGTAG, "Log() - Début");

    	log(LOG_DEBUG, LOGTAG, "Log() - Fin");
    }
    
    Trace (int niveauTraceIn){
    	niveauTrace = niveauTraceIn;
    }
    
    public void set_niveauTrace (int inNiveauTrace){
    	log(LOG_DEBUG, LOGTAG, "set_niveauTrace() - Début");
    	log(LOG_DEBUG, LOGTAG, "set_niveauTrace() - NiveauTrace : " + inNiveauTrace);
    	niveauTrace = inNiveauTrace;
    	
    	log(LOG_DEBUG, LOGTAG, "set_niveauTrace() - Fin");
    } 
	
    public void log(int inTypeTrace, String inTagLog, String inStrLog) {
    	System.out.println(inTagLog + " - " + inStrLog);
		if (inTypeTrace >= niveauTrace) {
			
			switch (inTypeTrace) {
			case LOG_SILENCE :
				break;
			case LOG_MESSAGE :
				log.info(inStrLog);
				//System.out.println(texte);
				break;
			case LOG_WARNING :
				log.warn(inStrLog);
				break;
			case LOG_ERROR :
				log.error(inStrLog);
				break;
			case LOG_VERBOSE :
				log.trace(inStrLog);
				break;
			case LOG_DEBUG :
				log.debug(inTagLog + " - " + inStrLog);
				break;
			}

		}
		
	}


}