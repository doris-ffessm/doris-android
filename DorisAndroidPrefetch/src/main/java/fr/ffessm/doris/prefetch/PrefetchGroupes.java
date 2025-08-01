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

import static java.lang.String.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPIHTTPHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisOAuth2ClientCredentials;
import fr.ffessm.doris.prefetch.ezpublish.JsonToDB;


public class PrefetchGroupes {


    // Initialisation de la Gestion des Log
    public static Log log = LogFactory.getLog(PrefetchGroupes.class);
    protected DorisAPIHTTPHelper httpHelper;
    DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper = new DorisAPI_JSONTreeHelper();
    DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper();
    JsonToDB jsonToDB = new JsonToDB();
    private final DorisDBHelper dbContext;
    private final ConnectionSource connectionSource;

    public PrefetchGroupes(DorisDBHelper dbContext, ConnectionSource connectionSource) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;
        httpHelper = new DorisAPIHTTPHelper(null);
    }

    public PrefetchGroupes(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter, int nbFichesParRequetes) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;
        httpHelper = new DorisAPIHTTPHelper(null);
    }

    /**
     * recompute the groups by loading the modal dialog
     * @return & if ok, -1 in case of error
     */
    @SuppressWarnings("DefaultLocale")
    public int prefetchFromModalDialog() throws IOException {
        log.debug("PrefetchGroupes.prefetchFromModalDialog() - start");

        // parse the html
        String htmlString = getModalHtml();
        Source source = new Source(htmlString);
        source.fullSequentialParse();
        List<Element> elements = source.getAllElements("a");
        List<Element> filteredElements = new ArrayList<>();
        for (Element element : elements) {
            if (element.getAttributeValue("data-groupid") != null) {
                filteredElements.add(element);
            }
        }
        /*
        for (Element element : filteredElements ) {
            log.info(element.getContent() + " : " + element.getAttributeValue("data-groupid") + " : " + element.getDepth());
        }
         */

        // create groupe from html
        List<Groupe> listeGroupes = new ArrayList<>(); // all created Groupes
        Stack<GroupeDepthTuple> currentGroupeStack = new Stack<>(); // allows to recreate the hierarchy
        //int currentDepth = 0;

        Groupe racine = new Groupe(0, 0, "racine", 0x00000000, null);
        racine.setContextDB(dbContext);
        racine.setId(1);
        listeGroupes.add(racine);
        currentGroupeStack.push(new GroupeDepthTuple(0, racine));

        for (Element element : filteredElements ) {
            log.debug(element.getContent() + " : " + element.getAttributeValue("data-groupid") + " : " + element.getDepth());
            Groupe parentGroup;
            if(currentGroupeStack.peek().depth < element.getDepth()) {
                // this is a children Groupe
                parentGroup = currentGroupeStack.peek().groupe;

            } else if(currentGroupeStack.peek().depth == element.getDepth()) {
                // this is a sibling Groupe
                currentGroupeStack.pop();
                parentGroup = currentGroupeStack.peek().groupe;

            } else /* if (currentGroupeStack.peek().depth > element.getDepth()) */{
                // this is a new parent Groupe
                // pop until reach lower depth
                while(currentGroupeStack.peek().depth >= element.getDepth()) {
                    currentGroupeStack.pop();
                }
                parentGroup = currentGroupeStack.peek().groupe;
            }
            String shortName = element.getContent().toString();
            shortName = shortName.replaceAll("\\(.*?\\)", "");
            shortName = shortName.replaceAll(":.*", "").trim();
            Groupe newGroup = new Groupe(Integer.parseInt(element.getAttributeValue("data-groupid")),
                    0, // useless now ?
                    shortName,  // shorter name
                    parentGroup);
            newGroup.setDescriptionGroupe(element.getContent().toString()); // longer name

            currentGroupeStack.push(new GroupeDepthTuple(element.getDepth(), newGroup));
            try {
                final Groupe existingGroupe = dbContext.groupeDao.queryForFirst(
                        dbContext.groupeDao.queryBuilder().where().eq("numeroGroupe", newGroup.getNumeroGroupe()).prepare()
                );
                if(existingGroupe != null) {
                    log.info("cached Groupe " + currentGroupeStack.size() + " :" + newGroup.getNomGroupe() + " " + newGroup.getNumeroGroupe());
                } else {
                    listeGroupes.add(newGroup);
                    log.info("new    Groupe " + currentGroupeStack.size() + " :" + newGroup.getNomGroupe() + " " + newGroup.getNumeroGroupe());
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }

        log.info(format("Adding/updating %d groups", listeGroupes.size()));
        nbDownloadedGroupImages = 0;
        nbGroupImagesFromCache = 0;
        // update groups in order to maintain _id and grap updated text
        for (Groupe g : listeGroupes) {
            try {
                this.updateGroupe(g, listeGroupes);
            } catch (SQLException | IOException | WebSiteNotAvailableException e) {
                log.error("Une erreur est survenue dans PrefetchGroupes", e);
                return -1;
            }
        }
        log.info(format("Downloaded group images %d, group images already in cache %d", nbDownloadedGroupImages, nbGroupImagesFromCache));

        if (!saveListGroupInDB(listeGroupes)) return -1;

        // update contextDb for all groups in order to enable correct navigation
        for (Groupe groupe : listeGroupes) {
            groupe.setContextDB(dbContext);
        }
        // patch missing images
        for (Groupe groupe : listeGroupes) {
            patchMissingGroupeImage(groupe, listeGroupes);
        }
        if (!saveListGroupInDB(listeGroupes)) return -1;

        log.debug("PrefetchGroupes.prefetchFromModalDialog() - end");
        return 1;
    }

    /**
     * save the list of groupe in the DB
     * @param listGroupes
     * @return true if it's ok, false otherwise
     */
    public boolean saveListGroupInDB(List<Groupe> listGroupes) {
        try {
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        for (Groupe groupe : listGroupes) {
                            dbContext.groupeDao.createOrUpdate(groupe);
                        }
                        return null;
                    });
        } catch (Exception e) {
            // une erreur est survenue
            log.error("Une erreur est survenue dans PrefetchGroupes");
            log.error(e);
            return false;
        }
        return true;
    };

    class GroupeDepthTuple {
        public Integer depth;
        public Groupe groupe;

        public GroupeDepthTuple(Integer depth, Groupe groupe) {
            this.depth = depth;
            this.groupe = groupe;
        }
    }
    protected String getModalHtml() throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://doris.ffessm.fr/ezjscore/call/");
        httppost.setEntity(new StringEntity("ezjscServer_function_arguments=public%3A%3AgetGroupsModal&ezxform_token=",  "UTF8"));

        httppost.setHeader("User-Agent","Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/118.0");
        httppost.setHeader("Accept","application/json, text/javascript, /; q=0.01");
        httppost.setHeader("Accept-Language","en-US,en;q=0.5");
        httppost.setHeader("Accept-Encoding","gzip, deflate, br");
        httppost.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        httppost.setHeader("Accept-Encoding","gzip, deflate, br");
        httppost.setHeader("X-Requested-With","XMLHttpRequest");
        httppost.setHeader("Accept-Encoding","gzip, deflate, br");
        httppost.setHeader("Origin","https://doris.ffessm.fr");
        httppost.setHeader("Connection","keep-alive");
        httppost.setHeader("Referer","https://doris.ffessm.fr/");
        httppost.setHeader("Sec-Fetch-Dest","empty");
        httppost.setHeader("Sec-Fetch-Mode","cors");
        httppost.setHeader("Sec-Fetch-Site","same-origin");
        httppost.setHeader("Pragma","no-cache");
        httppost.setHeader("Cache-Control","no-cache");
        /* requete curl equivalente   (obtenue par analyse du site web et inspection des appels reseaux depuis le browser
        curl 'https://doris.ffessm.fr/ezjscore/call/' -X POST -H 'User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/118.0' -H 'Accept: application/json, text/javascript, /; q=0.01' -H 'Accept-Language: en-US,en;q=0.5' -H 'Accept-Encoding: gzip, deflate, br' -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8' -H 'X-Requested-With: XMLHttpRequest' -H 'Origin: https://doris.ffessm.fr' -H 'Connection: keep-alive' -H 'Referer: https://doris.ffessm.fr/' -H 'Cookie: tarteaucitron=!gajs=wait!gtag=wait!vimeo=wait' -H 'Sec-Fetch-Dest: empty' -H 'Sec-Fetch-Mode: cors' -H 'Sec-Fetch-Site: same-origin' -H 'Pragma: no-cache' -H 'Cache-Control: no-cache' --data-raw 'ezjscServer_function_arguments=public%3A%3AgetGroupsModal&ezxform_token='
        -H 'User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/118.0'
        -H 'Accept: application/json, text/javascript, /; q=0.01'
        -H 'Accept-Language: en-US,en;q=0.5'
        -H 'Accept-Encoding: gzip, deflate, br'
        -H 'Content-Type: application/x-www-form-urlencoded; charset=UTF-8'
        -H 'X-Requested-With: XMLHttpRequest'
        -H 'Origin: https://doris.ffessm.fr'
        -H 'Connection: keep-alive'
        -H 'Referer: https://doris.ffessm.fr/'
        -H 'Cookie: tarteaucitron=!gajs=wait!gtag=wait!vimeo=wait'
        -H 'Sec-Fetch-Dest: empty'
        -H 'Sec-Fetch-Mode: cors'
        -H 'Sec-Fetch-Site: same-origin'
        -H 'Pragma: no-cache'
         */

        //Execute and get the response.
        HttpResponse response = client.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                // this returns a json structure
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(instream);
                log.debug("content : "+rootNode.get("content").asText());
                return rootNode.get("content").asText();
            }
        } else {
            throw new RuntimeException("Not able to get valid  response on https://doris.ffessm.fr/ezjscore/call/ post");
        }
    }

    /* TODO : Ici en Durs mais devraient être récupérés avec la Classification */

    // note pour tenter de recalculer en auto
    // groupes 87 et sur groupes 119
    // https://doris.ffessm.fr/api/ezp/v1/content/node/87?oauth_token=c977aaf12f519c53081d70edd3010b3961bbfcc4
    // https://doris.ffessm.fr/api/ezp/v1/content/node/119?oauth_token=c977aaf12f519c53081d70edd3010b3961bbfcc4


    @SuppressWarnings("DefaultLocale")
    public int prefetchV4() {
        //clearExistingGroups();

        List<Groupe> listeGroupes = new ArrayList<>();
        // check manuel du n° https://doris.ffessm.fr/api/ezx/v1/object/<numeroGroupe>?oauth_token=c977aaf12f519c53081d70edd3010b3961bbfcc4  sur le site
        Groupe racine = new Groupe(0, 0, "racine", 0x00000000, null);
        racine.setId(1);
        listeGroupes.add(racine);
        listeGroupes.add(new Groupe(171365, 0, "PROCARYOTES", getGroupeWithName(listeGroupes, "racine")));
        listeGroupes.add(new Groupe(48868, 1000, "Procaryotes", getGroupeWithName(listeGroupes, "PROCARYOTES")));
        listeGroupes.add(new Groupe(136033, 0, "VEGETAUX", getGroupeWithName(listeGroupes, "racine")));
        listeGroupes.add(new Groupe(136029, 0, "Algues", getGroupeWithName(listeGroupes, "VEGETAUX")));
        listeGroupes.add(new Groupe(48869, 2000, "Rhodophycées", getGroupeWithName(listeGroupes, "Algues")));
        listeGroupes.add(new Groupe(48972, 2072, "Thalles érigés", getGroupeWithName(listeGroupes, "Rhodophycées")));
        listeGroupes.add(new Groupe(48973, 2073, "Thalles encroûtants", getGroupeWithName(listeGroupes, "Rhodophycées")));
        listeGroupes.add(new Groupe(48870, 3000, "Chlorophycées", getGroupeWithName(listeGroupes, "Algues")));
        listeGroupes.add(new Groupe(48871, 4000, "Phéophycées", getGroupeWithName(listeGroupes, "Algues")));
        listeGroupes.add(new Groupe(811394, 4000, "Bryophytes", getGroupeWithName(listeGroupes, "VEGETAUX")));
        listeGroupes.add(new Groupe(897451, 4000, "Filicophytes", getGroupeWithName(listeGroupes, "VEGETAUX")));

        listeGroupes.add(new Groupe(136031, 0, "Plantes à fleurs", getGroupeWithName(listeGroupes, "VEGETAUX")));
        listeGroupes.add(new Groupe(48872, 5000, "Plantes subaquatiques", getGroupeWithName(listeGroupes, "Plantes à fleurs")));
        listeGroupes.add(new Groupe(48873, 6000, "Plantes terrestres", getGroupeWithName(listeGroupes, "Plantes à fleurs")));
        listeGroupes.add(new Groupe(171370, 0, "LICHENS", getGroupeWithName(listeGroupes, "racine")));
        listeGroupes.add(new Groupe(48874, 7000, "Champignons et Lichens", getGroupeWithName(listeGroupes, "LICHENS")));
        listeGroupes.add(new Groupe(171372, 0, "ANIMAUX", getGroupeWithName(listeGroupes, "racine")));
        listeGroupes.add(new Groupe(48875, 8000, "Animaux unicellulaires", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(136051, 0, "EPONGES ou SPONGIAIRES", getGroupeWithName(listeGroupes, "Animaux unicellulaires")));
        listeGroupes.add(new Groupe(48876, 9000, "Calcisponges", getGroupeWithName(listeGroupes, "EPONGES ou SPONGIAIRES")));
        listeGroupes.add(new Groupe(48877, 10000, "Démosponges", getGroupeWithName(listeGroupes, "EPONGES ou SPONGIAIRES")));
        listeGroupes.add(new Groupe(48878, 11000, "Hexactinellides", getGroupeWithName(listeGroupes, "EPONGES ou SPONGIAIRES")));
        listeGroupes.add(new Groupe(201199, 0, "Homoscléromorphes", getGroupeWithName(listeGroupes, "EPONGES ou SPONGIAIRES")));

        listeGroupes.add(new Groupe(171374, 0, "CNIDAIRES", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48879, 12000, "Hydrozoaires", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48937, 12025, "Hydrozoaires benthiques", getGroupeWithName(listeGroupes, "Hydrozoaires")));
        listeGroupes.add(new Groupe(48938, 12026, "Hydrozoaires pélagiques", getGroupeWithName(listeGroupes, "Hydrozoaires")));
        listeGroupes.add(new Groupe(48880, 13000, "Cubozoaires", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48881, 14000, "Scyphozoaires ou lucernaires", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48950, 14048, "Scyphozoaires", getGroupeWithName(listeGroupes, "Scyphozoaires ou lucernaires")));
        listeGroupes.add(new Groupe(48949, 14047, "Staurozoaires", getGroupeWithName(listeGroupes, "Scyphozoaires ou lucernaires")));

        listeGroupes.add(new Groupe(48886, 14047, "Anémones au sens large", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48925, 14047, "Anémones", getGroupeWithName(listeGroupes, "Anémones au sens large")));
        listeGroupes.add(new Groupe(48924, 14047, "Anémones encroutantes", getGroupeWithName(listeGroupes, "Anémones au sens large")));
        listeGroupes.add(new Groupe(48926, 14047, "Cérianthes", getGroupeWithName(listeGroupes, "Anémones au sens large")));
        listeGroupes.add(new Groupe(48927, 14047, "Corallimorphaires", getGroupeWithName(listeGroupes, "Anémones au sens large")));
        listeGroupes.add(new Groupe(48887, 15000, "Coraux dur", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48888, 15000, "Antipathaires", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48882, 15000, "Alcyonides", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48883, 16000, "Gorgonaires", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48884, 17000, "Pennatulaires", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48885, 18000, "Autres Octocoralliaires", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        // TODO n'est plus dans doris ?
        listeGroupes.add(new Groupe(48886, 19000, "Anémones de mer au sens large, cérianthes", getGroupeWithName(listeGroupes, "CNIDAIRES")));
        listeGroupes.add(new Groupe(48925, 19006, "Anémones", getGroupeWithName(listeGroupes, "Anémones de mer au sens large, cérianthes")));
        listeGroupes.add(new Groupe(48926, 19007, "Cérianthes", getGroupeWithName(listeGroupes, "Anémones de mer au sens large, cérianthes")));
        listeGroupes.add(new Groupe(48924, 19005, "Anémones encroûtantes", getGroupeWithName(listeGroupes, "Anémones de mer au sens large, cérianthes")));
        listeGroupes.add(new Groupe(48927, 19008, "Corallimorphaires", getGroupeWithName(listeGroupes, "Anémones de mer au sens large, cérianthes")));


        listeGroupes.add(new Groupe(171376, 0, "CTENAIRES", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48889, 22000, "Cténophores", getGroupeWithName(listeGroupes, "CTENAIRES")));

        listeGroupes.add(new Groupe(171385, 0, "VERS", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48890, 23000, "Plathelminthes", getGroupeWithName(listeGroupes, "VERS")));
        listeGroupes.add(new Groupe(48891, 24000, "Polychètes errantes", getGroupeWithName(listeGroupes, "VERS")));
        listeGroupes.add(new Groupe(48892, 25000, "Polychètes sédentaires", getGroupeWithName(listeGroupes, "VERS")));
        listeGroupes.add(new Groupe(48893, 26000, "Oligochètes et Hirudinées", getGroupeWithName(listeGroupes, "VERS")));
        // TODO il en manque ici ...
        listeGroupes.add(new Groupe(48894, 27000, "Autres « vers » subaquatiques", getGroupeWithName(listeGroupes, "VERS")));

        listeGroupes.add(new Groupe(171380, 0, "MOLLUSQUES", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48895, 28000, "Bivalves", getGroupeWithName(listeGroupes, "MOLLUSQUES")));
        listeGroupes.add(new Groupe(48896, 29000, "Céphalopodes", getGroupeWithName(listeGroupes, "MOLLUSQUES")));
        //listeGroupes.add(new Groupe(48897,30000,"Gastéropodes Pulmonés", getGroupeWithName(listeGroupes,"MOLLUSQUES")));
        // TODO des changements ici
        listeGroupes.add(new Groupe(869357, 0, "Gastéropodes pélagiques", getGroupeWithName(listeGroupes, "MOLLUSQUES")));

        listeGroupes.add(new Groupe(171439, 0, "Gastéropodes Prosobranches pélagiques", getGroupeWithName(listeGroupes, "Gastéropodes pélagiques")));
        listeGroupes.add(new Groupe(498097, 0, "Gastéropodes Opisthobranches pélagiques", getGroupeWithName(listeGroupes, "Gastéropodes pélagiques")));

        listeGroupes.add(new Groupe(48898, 31000, "Gastéropodes à coquille unique", getGroupeWithName(listeGroupes, "MOLLUSQUES")));
        listeGroupes.add(new Groupe(811641, 31050, "Coquille spiralée bien visible non recouverte par le manteau", getGroupeWithName(listeGroupes, "Gastéropodes à coquille unique")));
        listeGroupes.add(new Groupe(811620, 31050, "Coquille arrondie en forme de chapeau chinois ou aplatie", getGroupeWithName(listeGroupes, "Gastéropodes à coquille unique")));
        listeGroupes.add(new Groupe(48952, 31050, "Vermets", getGroupeWithName(listeGroupes, "Gastéropodes à coquille unique")));
        listeGroupes.add(new Groupe(814159, 31050, "Coquille lisse recouverte par le manteau", getGroupeWithName(listeGroupes, "Gastéropodes à coquille unique")));




        listeGroupes.add(new Groupe(48899, 32000, "Gastéropodes Opisthobranches", getGroupeWithName(listeGroupes, "MOLLUSQUES")));
        listeGroupes.add(new Groupe(48939, 32034, "Nudibranches Doridiens", getGroupeWithName(listeGroupes, "Gastéropodes Opisthobranches")));
        listeGroupes.add(new Groupe(48940, 32035, "Nudibranches Eolidiens", getGroupeWithName(listeGroupes, "Gastéropodes Opisthobranches")));
        listeGroupes.add(new Groupe(48941, 32036, "Autres Nudibranches", getGroupeWithName(listeGroupes, "Gastéropodes Opisthobranches")));
        listeGroupes.add(new Groupe(48942, 32037, "Autres Opisthobranches", getGroupeWithName(listeGroupes, "Gastéropodes Opisthobranches")));
        listeGroupes.add(new Groupe(48900, 33000, "Autres Mollusques", getGroupeWithName(listeGroupes, "MOLLUSQUES")));

        listeGroupes.add(new Groupe(171393, 0, "LOPHOPHORATES", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48901, 34000, "Bryozoaires, Brachiopodes et Phoronidiens", getGroupeWithName(listeGroupes, "LOPHOPHORATES")));
        listeGroupes.add(new Groupe(48943, 34038, "Bryozoaires arbustifs", getGroupeWithName(listeGroupes, "Bryozoaires, Brachiopodes et Phoronidiens")));
        listeGroupes.add(new Groupe(48944, 34039, "Bryozoaires encroûtants", getGroupeWithName(listeGroupes, "Bryozoaires, Brachiopodes et Phoronidiens")));
        listeGroupes.add(new Groupe(48946, 34041, "Brachiopodes", getGroupeWithName(listeGroupes, "Bryozoaires, Brachiopodes et Phoronidiens")));
        listeGroupes.add(new Groupe(48945, 34040, "Phoronidiens", getGroupeWithName(listeGroupes, "Bryozoaires, Brachiopodes et Phoronidiens")));

        listeGroupes.add(new Groupe(171390, 0, "ARTHROPODES", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48902, 35000, "Crustacés Malacostracés", getGroupeWithName(listeGroupes, "ARTHROPODES")));
        listeGroupes.add(new Groupe(48979, 35079, "Isopodes, Amphipodes, Mysidacés...", getGroupeWithName(listeGroupes, "Crustacés Malacostracés")));
        listeGroupes.add(new Groupe(48981, 35081, "Squilles", getGroupeWithName(listeGroupes, "Crustacés Malacostracés")));
        listeGroupes.add(new Groupe(48975, 35075, "Crevettes et apparentés : Caridés, Sténopodidés...", getGroupeWithName(listeGroupes, "Crustacés Malacostracés")));
        listeGroupes.add(new Groupe(48976, 35076, "Homards, langoustes...", getGroupeWithName(listeGroupes, "Crustacés Malacostracés")));
        listeGroupes.add(new Groupe(48977, 35077, "Bernard l'ermite, galathées...", getGroupeWithName(listeGroupes, "Crustacés Malacostracés")));
        listeGroupes.add(new Groupe(48978, 35078, "Crabes, araignées de mer...", getGroupeWithName(listeGroupes, "Crustacés Malacostracés")));
        listeGroupes.add(new Groupe(48903, 36000, "Crustacés Cirripèdes", getGroupeWithName(listeGroupes, "ARTHROPODES")));
        listeGroupes.add(new Groupe(48904, 37000, "Autres Crustacés", getGroupeWithName(listeGroupes, "ARTHROPODES")));
        listeGroupes.add(new Groupe(48905, 38000, "Chélicérates aquatiques", getGroupeWithName(listeGroupes, "ARTHROPODES")));
        listeGroupes.add(new Groupe(48906, 39000, "Hexapodes aquatiques", getGroupeWithName(listeGroupes, "ARTHROPODES")));

        listeGroupes.add(new Groupe(171388, 0, "ECHINODERMES", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48907, 40000, "Astérides", getGroupeWithName(listeGroupes, "ECHINODERMES")));
        listeGroupes.add(new Groupe(48908, 41000, "Échinides", getGroupeWithName(listeGroupes, "ECHINODERMES")));
        listeGroupes.add(new Groupe(48947, 41045, "Oursins réguliers", getGroupeWithName(listeGroupes, "Échinides")));
        listeGroupes.add(new Groupe(48948, 41046, "Oursins irréguliers", getGroupeWithName(listeGroupes, "Échinides")));
        listeGroupes.add(new Groupe(48909, 42000, "Crinoïdes", getGroupeWithName(listeGroupes, "ECHINODERMES")));
        listeGroupes.add(new Groupe(48910, 43000, "Ophiurides", getGroupeWithName(listeGroupes, "ECHINODERMES")));
        listeGroupes.add(new Groupe(48911, 44000, "Holothurides", getGroupeWithName(listeGroupes, "ECHINODERMES")));

        listeGroupes.add(new Groupe(171383, 0, "PROCORDÉS", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48912, 45000, "Tuniciers et Céphalocordés", getGroupeWithName(listeGroupes, "PROCORDÉS")));
        listeGroupes.add(new Groupe(48923, 45004, "Ascidies simples et sociales", getGroupeWithName(listeGroupes, "Tuniciers et Céphalocordés")));
        listeGroupes.add(new Groupe(48928, 45009, "Ascidies composées", getGroupeWithName(listeGroupes, "Tuniciers et Céphalocordés")));
        listeGroupes.add(new Groupe(48929, 45012, "Thaliacés et Appendiculaires", getGroupeWithName(listeGroupes, "Tuniciers et Céphalocordés")));
        listeGroupes.add(new Groupe(48930, 45013, "Céphalocordés", getGroupeWithName(listeGroupes, "Tuniciers et Céphalocordés")));

        listeGroupes.add(new Groupe(171378, 0, "VERTÉBRÉS", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48913, 46000, "Poissons cartilagineux", getGroupeWithName(listeGroupes, "VERTÉBRÉS")));
        listeGroupes.add(new Groupe(48962, 46060, "Requins", getGroupeWithName(listeGroupes, "Poissons cartilagineux")));
        listeGroupes.add(new Groupe(48961, 46059, "Raies", getGroupeWithName(listeGroupes, "Poissons cartilagineux")));
        listeGroupes.add(new Groupe(48914, 47000, "Poissons osseux nageant en pleine eau", getGroupeWithName(listeGroupes, "VERTÉBRÉS")));
        listeGroupes.add(new Groupe(49009, 47112, "Carangues", getGroupeWithName(listeGroupes, "Poissons osseux nageant en pleine eau")));
        listeGroupes.add(new Groupe(49005, 47107, "Mulets, bars, loups", getGroupeWithName(listeGroupes, "Poissons osseux nageant en pleine eau")));
        listeGroupes.add(new Groupe(49010, 47113, "Thons, thazards, maquereaux, barracudas", getGroupeWithName(listeGroupes, "Poissons osseux nageant en pleine eau")));
        listeGroupes.add(new Groupe(1843282, 47116, "Sardines", getGroupeWithName(listeGroupes, "Poissons osseux nageant en pleine eau")));
        listeGroupes.add(new Groupe(49012, 47115, "Fusiliers", getGroupeWithName(listeGroupes, "Poissons osseux nageant en pleine eau")));
        listeGroupes.add(new Groupe(49011, 47114, "Autres poissons de pleine eau", getGroupeWithName(listeGroupes, "Poissons osseux nageant en pleine eau")));
        //
        listeGroupes.add(new Groupe(48915, 48000, "Poissons osseux nageant près du fond", getGroupeWithName(listeGroupes, "VERTÉBRÉS")));
        listeGroupes.add(new Groupe(49002, 48104, "Truites, saumons, ombles", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48983, 48084, "Morues, mostelles, merlans, lieus, tacauds,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48986, 48087, "Poissons-soleils, gros-yeux, soldats et écureuils", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48955, 48053, "Mérous, serrans, cerniers, barbiers ,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48970, 48105, "Rascasses, poissons-scorpions, poissons-pierre", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48992, 48094, "Apogons ou cardinaux", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48990, 48092, "Lutjans et gaterins", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48958, 48056, "Sars, dorades, oblades, dentés, marbrés", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48959, 48057, "Corbs, ombrines, maigres, poissons-chevaliers,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(49000, 48102, "Becs de cane, empereurs ...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48953, 48051, "Poissons-papillons", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(49007, 48109, "Poissons-cochers et Zancles", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48960, 48058, "Poissons-anges", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(49001, 48103, "Platax", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48963, 48061, "Poissons-clowns", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48989, 48091, "Demoiselles, castagnoles, sergents-majors...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48957, 48055, "Labres, girelles, napoléons,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(49013, 48117, "Blennies et gobies nageurs", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48991, 48093, "Perroquets", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48982, 48083, "Poissons-chirurgiens, nasons", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48971, 48069, "Balistes", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48954, 48052, "Bourses ou poissons-limes", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48996, 48098, "Poissons-coffres,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48997, 48099, "Poissons-lapins,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48956, 48054, "Poissons-ballons, poissons-globes, fugu,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(49004, 48106, "Poissons-trompettes, poissons-flûtes,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(49006, 48108, "Poissons-fantômes, solénostomes,...", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));
        listeGroupes.add(new Groupe(48994, 48096, "Autres poissons près du fond", getGroupeWithName(listeGroupes, "Poissons osseux nageant près du fond")));

        listeGroupes.add(new Groupe(48916, 49000, "« Agnathes » et Poissons osseux posés sur le fond", getGroupeWithName(listeGroupes, "VERTÉBRÉS")));
        listeGroupes.add(new Groupe(48974, 49074, "Agnathes", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48987, 49088, "Chondrostéens", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48965, 49063, "Poissons au corps serpentiforme", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48969, 49067, "Baudroies, antennaires, poissons chauve-souris,...", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48964, 49062, "Hippocampes, syngnathes...", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(49003, 49068, "Rascasses, poissons-scorpions, poissons-pierre", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48968, 49066, "Rougets, barbets, barbarins,...", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48984, 49085, "Grondins", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48988, 49089, "Poissons-faucons", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48993, 49095, "Poissons-lézards", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48966, 49064, "Blennies", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48967, 49065, "Gobies", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48985, 49086, "Triptérygions", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48951, 49049, "Poissons plats", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));
        listeGroupes.add(new Groupe(48995, 49097, "Autres poissons sur le fond", getGroupeWithName(listeGroupes, "« Agnathes » et Poissons osseux posés sur le fond")));

        listeGroupes.add(new Groupe(48917, 50000, "Amphibiens", getGroupeWithName(listeGroupes, "VERTÉBRÉS")));
        listeGroupes.add(new Groupe(48998, 50100, "Anoures", getGroupeWithName(listeGroupes, "Amphibiens")));
        listeGroupes.add(new Groupe(48999, 50101, "Urodèles", getGroupeWithName(listeGroupes, "Amphibiens")));
        // TODO  changements ici !
        listeGroupes.add(new Groupe(48918, 51000, "Mammifères aquatiques", getGroupeWithName(listeGroupes, "VERTÉBRÉS")));
        listeGroupes.add(new Groupe(48934, 51019, "Pinnipèdes", getGroupeWithName(listeGroupes, "Mammifères aquatiques")));
        listeGroupes.add(new Groupe(48935, 51020, "Siréniens", getGroupeWithName(listeGroupes, "Mammifères aquatiques")));
        listeGroupes.add(new Groupe(48936, 51021, "Cétacés", getGroupeWithName(listeGroupes, "Mammifères aquatiques")));
        listeGroupes.add(new Groupe(871058, 51023, "Cétacés à dents", getGroupeWithName(listeGroupes, "Cétacés")));
        listeGroupes.add(new Groupe(871064, 51024, "Cétacés à fanons", getGroupeWithName(listeGroupes, "Cétacés")));
        listeGroupes.add(new Groupe(48980, 51080, "Autres mammifères", getGroupeWithName(listeGroupes, "Mammifères aquatiques")));
        listeGroupes.add(new Groupe(49008, 51111, "Mammifères d'eau douce", getGroupeWithName(listeGroupes, "Mammifères aquatiques")));

        listeGroupes.add(new Groupe(862238, 51000, "Serpents et tortues aquatiques", getGroupeWithName(listeGroupes, "VERTÉBRÉS")));

        listeGroupes.add(new Groupe(48932, 51017, "Tortues aquatiques", getGroupeWithName(listeGroupes, "Serpents et tortues aquatiques")));
        listeGroupes.add(new Groupe(48933, 51018, "Serpents aquatiques", getGroupeWithName(listeGroupes, "Serpents et tortues aquatiques")));



        listeGroupes.add(new Groupe(48919, 52000, "Oiseaux", getGroupeWithName(listeGroupes, "VERTÉBRÉS")));
        listeGroupes.add(new Groupe(171395, 0, "AUTRES", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(48920, 53000, "Autres groupes mineurs", getGroupeWithName(listeGroupes, "ANIMAUX")));
        listeGroupes.add(new Groupe(49014, 53118, "Kamptozoaires ou Entoproctes", getGroupeWithName(listeGroupes, "Autres groupes mineurs")));


        log.debug(format("Adding %d groups", listeGroupes.size()));
        nbDownloadedGroupImages = 0;
        nbGroupImagesFromCache = 0;
        // update groups in order to maintain _id and grap updated text
        for (Groupe g : listeGroupes) {
            try {
                this.updateGroupe(g, listeGroupes);
            } catch (SQLException | IOException | WebSiteNotAvailableException e) {
                log.error("Une erreur est survenue dans PrefetchGroupes", e);
                return -1;
            }
        }
        log.debug(format("Downloaded group images %d, group images already in cache %d", nbDownloadedGroupImages, nbGroupImagesFromCache));
        // call in reverse order to make sure that it find a leak with an image
        for (int i = listeGroupes.size() - 1; i >= 0; i--) {
            patchMissingGroupeImage(listeGroupes.get(i), listeGroupes);
        }

        try {
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        for (Groupe groupe : listeGroupes) {
                            dbContext.groupeDao.createOrUpdate(groupe);
                        }
                        return null;
                    });
        } catch (Exception e) {
            // une erreur est survenue
            log.error("Une erreur est survenue dans PrefetchGroupes");
            log.error(e);
            return -1;
        }


        log.debug("PrefetchGroupes.prefetchV4() - fin");
        return 1;
    }

    protected void clearExistingGroups() {
        try {
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        List<Groupe> listGroupes = dbContext.groupeDao.queryForAll();
                        if (!listGroupes.isEmpty()) {
                            log.debug("Clearing existing groups");
                            dbContext.groupeDao.delete(listGroupes);
                        }
                        return null;
                    });
        } catch (Exception e) {
            // une erreur est survenue
            log.error("Une erreur est survenue dans PrefetchGroupes");
            log.error(e);
        }
    }

    /**
     * retrieve the groupID in the data base if possible
     * retrieve the group definition on internet in order to collect the description and image link
     *
     * @param group group to be updated
     */
    @SuppressWarnings("DefaultLocale")
    protected void updateGroupe(Groupe group, List<Groupe> groupList) throws SQLException, IOException, WebSiteNotAvailableException {

        Groupe queryPattern = new Groupe();
        queryPattern.setNumeroGroupe(group.getNumeroGroupe());
        queryPattern.setNumeroSousGroupe(group.getNumeroSousGroupe());

        List<Groupe> groupFound = dbContext.groupeDao.queryForMatching(queryPattern);
        if (!groupFound.isEmpty()) {
            group.setId(groupFound.get(0).getId());
        }
        if (group.getNumeroGroupe() != 0) {
            log.info(format("updateGroupe %d %d (modified %s)", group.getNumeroGroupe(), group.getNumeroSousGroupe(), group.getModificationDate()));

            fr.ffessm.doris.prefetch.ezpublish.jsondata.groupe.Groupe jsonGroup = dorisAPI_JSONDATABindingHelper.getGroupeFieldsFromObjectId(group.getNumeroGroupe());
            //group.setNomGroupe(jsonGroup.getDataMap().getTitle());
            group.setDescriptionGroupe(jsonGroup.getDataMap().getTitle());
            group.setDescriptionDetailleeGroupe(jsonGroup.getDataMap().getDescription());
            group.setCleURLImage(jsonGroup.getDataMap().getImage());

            // grab images in order to save them in the app
            downloadGroupeImage(group);
        }

        // hue = 40, start color is in the yellow/green
        List<Color> colors = ColorTools.getNbWheelColors(groupList.size(), 40, 50, 75);
        Color color = colors.get(groupList.indexOf(group)); // not very robust but work if the table is clean
        group.setCouleurGroupe(ColorTools.getColorInt(color));

    }


    /**
     * Use images from other sources in order to have relevant images for groups
     * By default try using 1rst sub group image
     * Must be called in reverse order to make sure that it find a leak with an image
     * @param group
     * @param allGroups
     */
    protected void patchMissingGroupeImage(Groupe group, List<Groupe> allGroups) {
        if (group.getNumeroGroupe() != 0 && group.getCleURLImage().equals("images/pucecarre.gif")) {
            if(group.getNumeroGroupe() == 136033) { // VEGETAUX
                Optional<Groupe> alqgueGroupe = allGroups.stream().filter(g -> g.getNumeroGroupe() == 136029).findFirst(); // algues
                alqgueGroupe.ifPresent(groupe -> group.setCleURLImage(groupe.getCleURLImage()));
            } else  if(group.getNumeroGroupe() == 171378) { // Vertébrés
                Optional<Groupe> alqgueGroupe = allGroups.stream().filter(g -> g.getNumeroGroupe() == 48914).findFirst(); // poissons osseux
                alqgueGroupe.ifPresent(groupe -> group.setCleURLImage(groupe.getCleURLImage()));
            } else  if(group.getNumeroGroupe() == 171372) { // ANIMAUX
                Optional<Groupe> alqgueGroupe = allGroups.stream().filter(g -> g.getNumeroGroupe() == 48914).findFirst(); // poissons osseux
                alqgueGroupe.ifPresent(groupe -> group.setCleURLImage(groupe.getCleURLImage()));
            } else {
                // find 1rst groupeFils
                //group.getGroupePere()
                Optional<Groupe> firstChild = allGroups.stream().filter(g -> g.getGroupePere() != null && g.getGroupePere().getId() == group.getId()).findFirst();
                if (firstChild.isPresent()) {
                    log.info("child group of " + group.getNomGroupe() + " : " + group.getGroupesFils().stream().map( g -> g.getNomGroupe()).collect(Collectors.joining(", ")));
                    log.info("uses 1rst sub group image for missing group image of " + group.getNomGroupe() + " with " + firstChild.get().getNomGroupe());
                    group.setCleURLImage(firstChild.get().getCleURLImage());
                }
            }
        }
    }


    /**
     * nb images downloaded
     */
    protected int nbDownloadedGroupImages = 0;
    /**
     * nb images from cache (ie. not downloaded)
     */
    protected int nbGroupImagesFromCache = 0;
    protected void downloadGroupeImage(Groupe group) throws IOException, WebSiteNotAvailableException {
        if (!group.getCleURLImage().isEmpty()) {
            Path path = Paths.get(PrefetchConstants.DOSSIER_RACINE + "/images/images_groupe_" + group.getNumeroGroupe() + ".png");
            if (Files.exists(path)) {
                nbGroupImagesFromCache++;
                return;
            }
            String uri = "https://" + DorisOAuth2ClientCredentials.DORIS_WEB_SERVER_HOST + "/" + group.getCleURLImage();
            HttpResponse response = httpHelper.getHttpResponse(uri);
            if (response.getStatusLine().getStatusCode() == 200) {
                Files.createDirectories(path.getParent());
                try (FileOutputStream out = new FileOutputStream(path.toString())) {
                    response.getEntity().writeTo(out);
                    nbDownloadedGroupImages++;
                }
                group.setCleURLImage("gestionenligne/images_groupe_" + group.getNumeroGroupe() + ".png");
            } else {
                log.error("Error "+response.getStatusLine() +"cannot get group image " + uri);

                group.setCleURLImage("images/pucecarre.gif");
            }
        } else {
            group.setCleURLImage("images/pucecarre.gif");
        }
    }

    public Groupe getGroupeWithName(List<Groupe> groups, String name) {
        for (Groupe g : groups) {
            if (g.getNomGroupe().equals(name)) {
                return g;
            }
        }
        return null;
    }

}
