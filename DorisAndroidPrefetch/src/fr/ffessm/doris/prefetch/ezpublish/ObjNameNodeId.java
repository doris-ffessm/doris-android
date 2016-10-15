package fr.ffessm.doris.prefetch.ezpublish;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObjNameNodeId {

	// Initialisation de la Gestion des Log
	public static Log log = LogFactory.getLog(ObjNameNodeId.class);

	public boolean debug = true;
	public boolean debug_SaveJSON = true;
	public static String DEBUG_SAVE_JSON_BASE_PATH = "target/json";
	public static String JSON_EXT = ".json";


    private Integer nodeId;
    private String objectName;

	public ObjNameNodeId(Integer nodeId){
        this.nodeId = nodeId;
	}

    public ObjNameNodeId(Integer nodeId, String objectName){
        this.nodeId = nodeId;
        this.objectName = objectName;
    }

    public Integer getNodeId(){
        return nodeId;
    }

    public String getObjectName(){
        return objectName;
    }

}
