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

  /** Port in the "Callback URL". */
  public static final int PORT = 8087;

  /** Domain name in the "Callback URL". */
  //public static final String DOMAIN = "127.0.0.1";
  //public static final String DOMAIN = "http://localhost/test";
  public static final String DOMAIN = "localhost";
  
  public static final String DORIS_WEB_SERVER_HOST = "dorisprod.donatello.io";
  
  public static final String TOKEN_SERVER_URL = "http://"+DORIS_WEB_SERVER_HOST+"/api/auth/oauth/token"; 
  public static final String AUTHORIZATION_SERVER_URL = "http://"+DORIS_WEB_SERVER_HOST+"/oauth/authorize"; 
  
  
  public static final String USER_ID = "dvojtise@gmail.com";  // � remplacer par l'utilisateur du server ?
  
  public static String getUserId(){
	  return USER_ID;
  }
  

  public static final String SERVER_NODE_FIELD_BASE_URL = "http://"+DORIS_WEB_SERVER_HOST+"/api/ezp/content/node/";
  
  public static final String SERVER_NODE_URL = "http://"+DORIS_WEB_SERVER_HOST+"/api/ezp/v1/content/node/";
  public static final String SERVER_OBJECT_URL = "http://"+DORIS_WEB_SERVER_HOST+"/api/ezx/v1/object/";
  public static final String SPECIES_NODE_URL = SERVER_NODE_URL+"66";
  public static final String IMAGES_NODE_URL = SERVER_NODE_URL+"19055";

}
