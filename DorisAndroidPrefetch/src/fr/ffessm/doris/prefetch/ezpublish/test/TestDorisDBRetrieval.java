package fr.ffessm.doris.prefetch.ezpublish.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;



import com.google.api.client.auth.oauth2.Credential;

import fr.ffessm.doris.prefetch.ezpublish.DorisAPIConnexionHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisOAuth2ClientCredentials;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.specie_fields.SpecieFields;

public class TestDorisDBRetrieval {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Credential credent = DorisAPIConnexionHelper
					.authorizeViaWebPage(DorisOAuth2ClientCredentials.getUserId());
			
			// get species List
			new DorisAPI_JSONTreeHelper(credent).getSpeciesNodeIds( 500);
			// DorisAPI_JSONTreeHelper.getSpecieDorisReferenceIdFromNodeId(credent,19835); // ne fonctionne pas  
//			DorisAPI_JSONTreeHelper.getSpeciesList(credent);
//			DorisAPI_JSONDATABindingHelper.getSpeciesList_full_data_binding_version(credent);
//			DorisAPI_JSONDATABindingHelper.getSpecieFromNodeId_full_data_binding_version(credent, 72613);
//			DorisAPI_JSONDATABindingHelper.getSpecieFieldsFromNodeId_full_data_binding_version(credent, 72613);
//			DorisAPI_JSONDATABindingHelper.getImageList(credent);
//			DorisAPI_JSONDATABindingHelper.getImageFromImageId(credent,19835);
			
			
//			testSpeciesRetrieval(credent);
			
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}

	public static void testSpeciesRetrieval(Credential credent) throws ClientProtocolException, IOException{
		//List<Integer> speciesNodeIds =DorisAPI_JSONTreeHelper.getSpeciesNodeIds(credent, 500);
		List<Integer> speciesNodeIds = new ArrayList<Integer>();
		new DorisAPI_JSONTreeHelper(credent).getSpeciesNodeIds( 0,speciesNodeIds, 500 );
		int i = 0;
		for (Integer specieNodeId : speciesNodeIds) {
			if(i++ > 5){
				break;
			}
			SpecieFields specieFields = new DorisAPI_JSONDATABindingHelper(credent).getSpecieFieldsFromNodeId( specieNodeId);
			String specieDorisReferenceId = specieFields.getFields().getReference().getValue();
			System.out.println(" nodeId="+specieNodeId+", dorisId="+specieDorisReferenceId);
		}
	}
	
	
}
