/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package fr.ffessm.doris.prefetch.ezpublish;

/**
 *
 * @author Didier Vojtisek
 */
public class DorisOAuth2ClientCredentials {

    /** Value of the "API Key". client_id*/
    public static final String API_KEY = "624c0b53e8c90dc2cf7aa5d6b4572c03";

    /** Value of the "API Secret". client_secret */
    public static final String API_SECRET = "d12e0548228dfe75de65848ff1e45411";

    /** Probablement pas propre mais permet de ne pas avoir à se connecter */
    //public static final String API_SUFFIXE = "e97c7911452f87ba23440ab4af541e39a64ecaef";
    // public static final String API_SUFFIXE = "adcde8792660f8a32ebe399966225d6965a50d89";
    //public static final String API_SUFFIXE = "9d00951e9946da1bf60ecbe6883712a28d5ae1ed";
    //public static final String API_SUFFIXE = "ca62da8df4a420b34b9e5acceb0c719ef6977093";
    //public static final String API_SUFFIXE = "d5c5af08aab6f777900a42e581aa0ab5bd053ee2";
      public static final String API_SUFFIXE = "c977aaf12f519c53081d70edd3010b3961bbfcc4";
    /** Port in the "Callback URL". */
    public static final int PORT = 8087;

    /** Domain name in the "Callback URL". */
    //public static final String DOMAIN = "127.0.0.1";
    //public static final String DOMAIN = "http://localhost/test";
    public static final String DOMAIN = "localhost";

    public static final String DORIS_WEB_SERVER_HOST = "doris.ffessm.fr";

    public static final String TOKEN_SERVER_URL = "https://"+DORIS_WEB_SERVER_HOST+"/api/auth/oauth/token";
    public static final String AUTHORIZATION_SERVER_URL = "https://"+DORIS_WEB_SERVER_HOST+"/oauth/authorize";


    public static final String USER_ID = "dvojtise@gmail.com";  // à remplacer par l'utilisateur du server ?

    public static String getUserId(){
      return USER_ID;
    }

    public static final String SERVER_NODE_URL = "https://"+DORIS_WEB_SERVER_HOST+"/api/ezp/v1/content/node/";
    public static final String SERVER_NODE_URL_SUFFIXE = "/fields";
    public static final String SERVER_OBJECT_URL = "https://"+DORIS_WEB_SERVER_HOST+"/api/ezx/v1/object/";

    public static final String SERVER_NODE_FIELD_BASE_URL = "https://"+DORIS_WEB_SERVER_HOST+"/api/ezp/content/node/";

    public static String getServerNodeUrlTousLesChamps(String nodeId){
      return SERVER_NODE_URL+nodeId+SERVER_NODE_URL_SUFFIXE;
    }

    public static String getServerObjectUrlTousLesChamps(String objectId){
        return SERVER_OBJECT_URL+objectId;
    }
    // Les Espèces
    public static final String SPECIES_NODE_URL = SERVER_NODE_URL+"66";

    // Les Photos des Espèces
    public static final String IMAGES_NODE_URL = SERVER_NODE_URL+"19055";

    // Bibliographie
    public static final String BIBLIO_NODE_URL = SERVER_NODE_URL+"68";

    // Glossaire
    public static final String GLOSSAIRE_NODE_URL = SERVER_NODE_URL+"69";

    // Zones Géographique
    //TODO : toujours aucun ????
    public static final String ZONES_NODE_URL = SERVER_NODE_URL+"70";

    // Taxons - Valeurs : Embranchement / Sous-embranchement / Super-classe / Classe / Sous-classe / Super-ordre
    //                    Ordre / Sous-ordre / Famille / Sous-famille
    public static final String TAXONS_NODE_URL = SERVER_NODE_URL+"71";

    // Groupes 87
    // TODO : toujours aucun ????
    public static final String GROUPES_NODE_URL = SERVER_NODE_URL+"87";
    public static final String SURGROUPES_NODE_URL = SERVER_NODE_URL+"119";

    // Intervenants
    public static final String PARTICIPANTS_NODE_URL = SERVER_NODE_URL+"77";





    // Videos 64 - toujours aucune ????
    // Aides 65 - Email envoyés par le site
    // Liens 67
    // Commentaires 72 - toujours aucun ????
    // Forum 75
    // Actualités 86
    // Types de lien 89

}
