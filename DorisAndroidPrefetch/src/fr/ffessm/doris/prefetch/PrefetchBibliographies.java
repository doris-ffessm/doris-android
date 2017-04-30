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

package fr.ffessm.doris.prefetch;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;

import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
import fr.ffessm.doris.prefetch.ezpublish.JsonToDB;
import fr.ffessm.doris.prefetch.ezpublish.ObjNameNodeId;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.bibliographie.Bibliographie;

public class PrefetchBibliographies {

	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchBibliographies.class);
	
	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;

	private int nbMaxFichesATraiter;
	private int nbFichesParRequetes;

	public PrefetchBibliographies(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter, int nbFichesParRequetes) {
		this.dbContext = dbContext;
		this.connectionSource = connectionSource;

		this.nbMaxFichesATraiter = nbMaxFichesATraiter;
		this.nbFichesParRequetes = nbFichesParRequetes;
	}

	public int prefetchV4() throws Exception {
        log.debug("prefetchV4() - début");

        // - - - Bibliographie  - - -
		JsonToDB jsonToDB = new JsonToDB();
		DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper = new DorisAPI_JSONTreeHelper();
		DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper();

		// TODO : Il faudrait mettre un While ici
		int nbFichesDORIS = nbMaxFichesATraiter;

		int count = 0;

		for (int i = 0; i < (nbFichesDORIS / nbFichesParRequetes); i++) {

			List<ObjNameNodeId> nodeIds = dorisAPI_JSONTreeHelper.getBibliographieNodeIds(nbFichesParRequetes, nbFichesParRequetes * i);

			for (ObjNameNodeId oeuvreNodeId : nodeIds) {
				count++;
				if (count > nbMaxFichesATraiter) {
					log.debug("doMain() - nbMaxFichesATraiter atteint");
					i = 9999;
					break;
				}

				// Référence de l'Espèce dans le message JSON
				Bibliographie biblioJSON = dorisAPI_JSONDATABindingHelper.getOeuvreFieldsFromNodeId(oeuvreNodeId.getNodeId().intValue());
				final EntreeBibliographie oeuvre = jsonToDB.getEntreeBibliographieFromJSONTerme(biblioJSON);

				TransactionManager.callInTransaction(connectionSource,
						new Callable<Void>() {
							public Void call() throws Exception {

								dbContext.entreeBibliographieDao.create(oeuvre);

								return null;
							}
						});

			}

		}
        log.debug("prefetchV4() - fin");
		return 1;
	}

}
