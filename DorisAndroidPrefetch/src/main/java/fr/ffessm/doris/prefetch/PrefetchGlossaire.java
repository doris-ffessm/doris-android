package fr.ffessm.doris.prefetch;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.io.IOException;
import java.util.List;

import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.prefetch.ezpublish.ObjNameNodeId;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.glossaire.Glossaire;

public class PrefetchGlossaire extends AbstractNodePrefetch<DefinitionGlossaire, Glossaire, Dao<DefinitionGlossaire, Integer>> {

    public PrefetchGlossaire(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter) {
        super(dbContext, connectionSource, nbMaxFichesATraiter);
    }

    public PrefetchGlossaire(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter, int nbFichesParRequetes) {
        super(dbContext, connectionSource, nbMaxFichesATraiter, nbFichesParRequetes);
    }

    @Override
    DefinitionGlossaire getNewDBObjectInstance() {
        return new DefinitionGlossaire();
    }

    @Override
    List<ObjNameNodeId> getNodeIdsFromWeb(int nbLimitRequest, int offset) throws IOException {
        return dorisAPI_JSONTreeHelper.getTermesNodeIds(nbLimitRequest,offset);
    }


    @Override
    Glossaire getJsonObjectFromWeb(int id) throws IOException, WebSiteNotAvailableException {
        return dorisAPI_JSONDATABindingHelper.getTermeFieldsFromNodeId(id);
    }

    @Override
    DefinitionGlossaire getDBObjectFromJSONObject(ObjNameNodeId objNameNodeId, Glossaire glossaire) throws WebSiteNotAvailableException {
        return jsonToDB.getDefinitionGlossaireFromJSONTerme(glossaire);
    }

    @Override
    Dao<DefinitionGlossaire, Integer> getDao() {
        return dbContext.definitionGlossaireDao;
    }
}
