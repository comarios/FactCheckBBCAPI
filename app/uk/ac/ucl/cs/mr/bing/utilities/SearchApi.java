package uk.ac.ucl.cs.mr.bing.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URI;

import org.apache.commons.codec.binary.Base64; // from the apache commons codec
import org.json.JSONException; // from the org.json (not the simple one though)
import org.json.JSONObject;


public class SearchApi {
	
	// Based on code http://www.cs.columbia.edu/~gravano/cs6111/Proj1/bing-java.txt
	// The number of results is set to 10, it is fast
	public static JSONObject bingIt(String queryTerm) throws IOException, JSONException, URISyntaxException {
		// This is the original string but it breaks very easily
		//String bingUrl = "https://api.datamarket.azure.com/Bing/SearchWeb/v1/Web?Query=%27%22" + queryTerm + "%22%27&$top=10&$format=json&WebSearchOptions=%27DisableQueryAlterations%27";
		
		URI uri = new URI("https", "api.datamarket.azure.com",
				"/Bing/SearchWeb/v1/Web", "Query=\'" + queryTerm + "\'&$top=10&$format=json&WebSearchOptions=\'DisableQueryAlterations\'", null);
			
		
		String bingUrl = uri.toASCIIString();
		
		//Provide your account key here. 
		String accountKey = "ZAk6G5VxGSD+K/mx3QH+PX24x85Cx9lEVnQzXA5H+P0";
		
		byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = new String(accountKeyBytes);

		URL url = new URL(bingUrl);
		System.out.println(url);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
				
		InputStream inputStream = (InputStream) urlConnection.getContent();		
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		String content = new String(contentRaw);
		//The content string is the xml/json output from Bing.
		//System.out.println(content);
		
		JSONObject jsonContent = new JSONObject(content);

		//System.out.println(jsonResult);
		return jsonContent;
	}
	

	public static void main(String[] args) {
		
		try {
			System.out.print(bingIt("\"Golden Warriors\"").toString(2));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
