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

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import fr.ffessm.doris.android.datamodel.AbstractWebNodeObject;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
import fr.ffessm.doris.prefetch.ezpublish.JsonToDB;
import fr.ffessm.doris.prefetch.ezpublish.ObjNameNodeId;

public abstract class AbstractNodePrefetch<DBObject extends AbstractWebNodeObject, JSONObject, DAO extends Dao<DBObject, Integer>> {


    // Initialisation de la Gestion des Log
    public static Log log = LogFactory.getLog(AbstractNodePrefetch.class);

    protected DorisDBHelper dbContext = null;
    protected ConnectionSource connectionSource = null;

    private int nbMaxFichesATraiter;
    private int nbFichesParRequetes;

    public static int pauseEntreRequetes = 1000;

    public String dbTypeName;

    public AbstractNodePrefetch(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;

        this.nbMaxFichesATraiter = nbMaxFichesATraiter;

        dbTypeName = getNewDBObjectInstance().getClass().getSimpleName();
    }

    public AbstractNodePrefetch(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter, int nbFichesParRequetes) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;

        this.nbMaxFichesATraiter = nbMaxFichesATraiter;
        this.nbFichesParRequetes = nbFichesParRequetes;
        dbTypeName = getNewDBObjectInstance().getClass().getSimpleName();
    }

    public int prefetch() throws Exception {
        log.debug(String.format("AbstractNodePrefetch<%s>.prefetch() - begin", dbTypeName));

        int nbFichesDORIS =  getNbNodeIdsFromWeb();

        int count = 0;
        int newFicheDownloadCount = 0;


        for (int i = 0; i <= (nbFichesDORIS / nbFichesParRequetes); i++) {
            log.info(String.format("Requesting %d %s out of %d", nbFichesParRequetes, dbTypeName, nbFichesDORIS));
            List<ObjNameNodeId> nodeIds = getNodeIdsFromWeb(nbFichesParRequetes, nbFichesParRequetes * i);
            if (nodeIds.isEmpty()) {
                // il ne reste plus de fiche à traiter
                break;
            }

            for (ObjNameNodeId objectNameNodeId : nodeIds) {
                String processStatus = "Ignored";
                count++;

                log.debug(String.format("Eval processing %s %d/%d",  dbTypeName, count, nbFichesDORIS));

                // Référence de l'intervenant dans le message JSON

                // vérif si utilisateur existe dans la base actuelle
                DBObject dbQuery = getNewDBObjectInstance();
                dbQuery.setWebNodeId(objectNameNodeId.getObjectId());
                List<DBObject> exisitingParticipantsForNodeID = getDao().queryForMatching(dbQuery);
                boolean mustRetrieveNode = false;
                int previousInternalDBId = -1;
                if (!exisitingParticipantsForNodeID.isEmpty()) {
                    DBObject existingDBEntry = exisitingParticipantsForNodeID.get(0);
                    if (existingDBEntry.getModificationDate() == objectNameNodeId.getModificationDate()) {
                        log.debug(String.format("Ignoring %s %s/%d already in the db with same modificationDate=%d",
                                dbTypeName,
                                objectNameNodeId.getObjectName(),
                                objectNameNodeId.getObjectId(),
                                existingDBEntry.getModificationDate()));
                        processStatus = "UP-TO-DATE";
                    } else {
                        log.debug(String.format("Updating old entry - %s %s/%d exists in db with different modification dates(%d!=%d).",
                                dbTypeName,
                                objectNameNodeId.getObjectName(),
                                objectNameNodeId.getObjectId(),
                                existingDBEntry.getModificationDate(),
                                objectNameNodeId.getModificationDate()
                                ));
                        /*TransactionManager.callInTransaction(connectionSource,
                                new Callable<Void>() {
                                    public Void call() throws Exception {
                                        getDao().delete(existingDBEntry);
                                        return null;
                                    }
                                });*/
                        previousInternalDBId = existingDBEntry.getId();
                        mustRetrieveNode = true;
                        processStatus = "UPDATED";
                    }
                } else {
                    log.debug(String.format("%s %s/%d NOT in the db, modificationDate=%d",
                            dbTypeName, objectNameNodeId.getObjectName(), objectNameNodeId.getObjectId(), objectNameNodeId.getModificationDate()));
                    mustRetrieveNode = true;
                    processStatus = "NEW";
                }

                log.info(String.format("Processing %s %d/%d - %s",  dbTypeName, count, nbFichesDORIS, processStatus));
                // seulement si n'existe pas ou plus récente alors récupération du noeud
                if (mustRetrieveNode) {
                    newFicheDownloadCount++;

                    if (newFicheDownloadCount > nbMaxFichesATraiter) {
                        log.info("nbMaxNewFichesATraiter " +newFicheDownloadCount+" reached");
                        if(newFicheDownloadCount < nbFichesDORIS) {
                            log.warn(String.format("only %d %s out of %d have been fetched; Please run again in order to get more.", newFicheDownloadCount, dbTypeName, nbFichesDORIS));
                        }
                        i = nbFichesDORIS;
                        break;
                    }
                    JSONObject jsonObject = getJsonObjectFromWeb(objectNameNodeId.getNodeId().intValue());

                    if (jsonObject != null) {
                        final DBObject dbObject = getDBObjectFromJSONObject(objectNameNodeId, jsonObject);
                        if(previousInternalDBId != -1) {
                            dbObject.setId(previousInternalDBId);
                        }
                        dbObject.setModificationDate(objectNameNodeId.getModificationDate());
                        dbObject.setWebNodeId(objectNameNodeId.getObjectId());
                        TransactionManager.callInTransaction(connectionSource,
                                new Callable<Void>() {
                                    public Void call() throws Exception {
                                        getDao().createOrUpdate(dbObject);
                                        return null;
                                    }
                                });
                        postNodeCreation(objectNameNodeId, dbObject, jsonObject);
                    }
                }
            }
        }

        log.debug(String.format("AbstractNodePrefetch<%s>.prefetch() - end", dbTypeName));
        return count;
    }


    DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper = new DorisAPI_JSONTreeHelper();
    DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper();
    JsonToDB jsonToDB = new JsonToDB();


    /**
     * create an instance of the DBObject
     * @return
     */
    abstract DBObject getNewDBObjectInstance();

    /**
     * exemple implementation: <code>return dorisAPI_JSONTreeHelper.getIntervenantsNodeIds(nbLimitRequest, offset);</code>
     * @param nbLimitRequest
     * @param offset
     * @return
     * @throws IOException
     */
    abstract List<ObjNameNodeId> getNodeIdsFromWeb(int nbLimitRequest, int offset) throws IOException, WebSiteNotAvailableException;

    abstract int getNbNodeIdsFromWeb() throws IOException, WebSiteNotAvailableException;

    abstract JSONObject getJsonObjectFromWeb(int id) throws IOException, WebSiteNotAvailableException;

    abstract DBObject getDBObjectFromJSONObject(ObjNameNodeId objNameNodeId, JSONObject jsonObject) throws WebSiteNotAvailableException;

    abstract DAO getDao();

    protected void postNodeCreation(ObjNameNodeId objNameNodeId, DBObject dbObject, JSONObject jsonObject) throws SQLException, WebSiteNotAvailableException {}

}
