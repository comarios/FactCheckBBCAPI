package controllers;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import uk.ac.ucl.cs.mr.factcheck.KeyPhraseExtractor;


public class FactCheck extends Controller {

	public static Result getInformationExtraction() {

		String res = "";

		JsonNode stmt = request().body().asJson();
		String statement = stmt.findPath("statement").textValue();
		
		KeyPhraseExtractor myExtractor = new KeyPhraseExtractor();
		System.out.println("Done initializiing");
		//String text = "According to Barack Obama\'s newest unconstitutionally enacted law, health care professionals are now required to violate HIPAA privacy laws and submit medical data to the government which is then used as justification for gun confiscation.";
		ObjectNode response = Json.newObject();
		Set<String> extracts = myExtractor.extract(statement, 5);
		for (String extract:extracts){
			
			res += extract +" ";
			//System.out.println(extract);
		}
		
		response.put("tokens", res);
		return ok(response);
	}

}
