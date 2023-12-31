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

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;


public class PrefetchZonesGeographiques {


    // Initialisation de la Gestion des Log
    public static Log log = LogFactory.getLog(PrefetchZonesGeographiques.class);

    private DorisDBHelper dbContext;
    private ConnectionSource connectionSource;

    public PrefetchZonesGeographiques(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;
    }

    public int prefetchV4() throws Exception {
        log.debug("prefetchV4() - début");

        // - - - Zones Géographiques - - -

        // TOUT EN DUR "TEMPORAIREMENT" CAR JE N'ARRIVE PAS A RECUPERER CETTE LISTE avec JSON

        /*
        List<ZoneGeographique> listeZonesGeographiques = new ArrayList<ZoneGeographique>();
        listeZonesGeographiques.add(ZoneGeographique(int id, java.lang.String nom, java.lang.String description));


        final DefinitionGlossaire terme();
 */

        TransactionManager.callInTransaction(connectionSource,
                (Callable<Void>) () -> {
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(1, "Espèces marines d'Europe (côtes françaises)", "Méditerranée, Atlantique, Manche et mer du Nord", 71726)
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(2, "Espèces de la façade Atlantique française", "Atlantique Nord-Est, Manche et Mer du Nord françaises", 239991, dbContext.zoneGeographiqueDao.queryForId(1))
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(3, "Espèces de la façade Méditérranéenne française", "Méditerranée", 239910, dbContext.zoneGeographiqueDao.queryForId(1))
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(4, "Espèces d'eau douce d'Europe", "Fleuves, rivières, lacs, mares et étangs, ...", 71728)
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(5, "Espèces marines et d'eau douce de l'Atlantique Nord-Ouest", "Côte est du Canada, embouchure du St Laurent, archipel de St Pierre-et-Miquelon", 135595)
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(6, "Espèces marines et d'eau douce de l'Indo-Pacifique français", "La Réunion, Mayotte, Nouvelle-Calédonie, Polynésie et autres", 71730)
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(7, "Espèces des terres antarctiques françaises", "Circumpolaire, mers et côtes australes, Crozet, Kerguelen, Terre Adélie", 1086247, dbContext.zoneGeographiqueDao.queryForId(6))
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(8, "Espèces de la Mer Rouge", "Mer Rouge", 1790099, dbContext.zoneGeographiqueDao.queryForId(6))
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(9, "Espèces marines et d'eau douce des Antilles françaises", "Guadeloupe, Martinique et autres", 71731)
                    );
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(10, "Espèces marines et d'eau douce de Guyanne française", "Guyanne", 2225263, dbContext.zoneGeographiqueDao.queryForId(9))
                    );
                  /* currrently empty, the web site organisation is quite different and doesn't show species but other kind of items
                    dbContext.zoneGeographiqueDao.createOrUpdate(
                            new ZoneGeographique(11, "Habitats subaquatiques", "Herbiers, Fonds, Littoraux,...", 1090239)
                    ); */
                    return null;
                });


        log.debug("prefetchV4() - fin");
        return 1;
    }

}
