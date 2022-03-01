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
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.util.List;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.prefetch.ezpublish.ObjNameNodeId;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.utilisateur.Utilisateur;

public class PrefetchIntervenants extends AbstractNodePrefetch<Participant, Utilisateur, Dao<Participant, Integer>> {

    public PrefetchIntervenants(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter) {
        super(dbContext, connectionSource, nbMaxFichesATraiter);
    }

    public PrefetchIntervenants(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter, int nbFichesParRequetes) {
        super(dbContext, connectionSource, nbMaxFichesATraiter, nbFichesParRequetes);
    }

    @Override
    Participant getNewDBObjectInstance() {
        return new Participant();
    }

    @Override
    List<ObjNameNodeId> getNodeIdsFromWeb(int nbLimitRequest, int offset) throws IOException, WebSiteNotAvailableException {
        return dorisAPI_JSONTreeHelper.getIntervenantsNodeIds(nbLimitRequest, offset);
    }
    @Override
    int getNbNodeIdsFromWeb() throws IOException, WebSiteNotAvailableException {
        return dorisAPI_JSONTreeHelper.getNbIntervenantsNodeIds();
    }

    @Override
    Utilisateur getJsonObjectFromWeb(int id) throws IOException, WebSiteNotAvailableException {
        return dorisAPI_JSONDATABindingHelper.getUtilisateurFieldsFromNodeId(id);
    }


    @Override
    Participant getDBObjectFromJSONObject(ObjNameNodeId objNameNodeId, Utilisateur utilisateur) {
        return jsonToDB.getParticipantFromJSONUtil(objNameNodeId.getObjectId(), utilisateur);
    }

    @Override
    Dao<Participant, Integer> getDao() {
        return dbContext.participantDao;
    }
}
