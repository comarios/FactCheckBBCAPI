package uk.ac.ucl.cs.mr.factcheck;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;

import java.util.*;

public class KeyPhraseExtractor {

	private StanfordCoreNLP processor;
	
	public KeyPhraseExtractor() {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		processor =  new StanfordCoreNLP(props);
	}

	
	
// TODO: separate the syntactic parsing pre-process from the key phrase extraction
// TODO: return the phrases extracted ordered by lenght, accompanied by token IDs
// TODO: separately return the named entities
		
	
	public Set<String> extract(String text, int maxLength){
		// create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	    
	    // run all Annotators on this text
	    processor.annotate(document);

	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);	  
	    
	    // Keep the string extracts here
	    // It is a set to avoid duplicates due to ne's overlapping with the keys
	    Set<String> extracts = new HashSet<String>();
	    
	    Set<String> entityTypesOfInterest = new HashSet<String>(Arrays.asList("ORGANIZATION","LOCATION","PERSON","MISC"));
	    for(CoreMap sentence: sentences) {
	    	
	    	ArrayList<String> tempPoS = new ArrayList<String>();
	    	ArrayList<Integer> tempTokenNos = new ArrayList<Integer>();
	    	ArrayList<String> tempStrings = new ArrayList<String>();
	    	
	    	String tempNE = "";
	    	String previousNEtype = "O";
	    	
	        // traversing the words in the current sentence
	        // a CoreLabel is a CoreMap with additional token-specific methods	  
	    	int tokenIndex = 0;
	        for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	          // this is the text of the token
	          String word = token.get(TextAnnotation.class);
	          // this is the POS tag of the token
	          String pos = token.get(PartOfSpeechAnnotation.class);
	          // this is the NER label of the token
	          String ne = token.get(NamedEntityTagAnnotation.class);
	          // this is the lemma
	          String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
	          System.out.println(word + "_" + pos + "_" + ne + "_" + lemma);
	          
	          // check for NE's
	          if (entityTypesOfInterest.contains(ne)){
	        	  if (entityTypesOfInterest.contains(previousNEtype)){
		        	  //if it is the same as the previous one
	        		  if(previousNEtype.equals(ne)){
	        			  tempNE += " " + word;
	        		  }
	        		  // a different ne starting
	        		  else{
	    	        	  extracts.add(tempNE);
	    	        	  tempNE = word;	        			  
	        		  }
	        	  }
	        	  // a new NE starting
	        	  else{
	        		  tempNE=word;
	        	  }
	          }
	          // so, no nes
	          else{
	        	  if (tempNE.length()>0) {
	        		  // make sure that this is getting a copy of the string
	        		  extracts.add(tempNE);
	        		  tempNE = "";
	        		  }
	          }
	          previousNEtype = ne;
	          
	          // we allow key phrases to start with nouns, adjectives or adverbs
	          if (pos.startsWith("N") || pos.startsWith("J") || pos.startsWith("R")){
	        	  //if the keyPhrase is empty
	        	  if (tempTokenNos.size() == 0){
	        		  // add it
	        		  tempPoS.add(pos);
	        		  tempStrings.add(word);
	        		  tempTokenNos.add(tokenIndex);
	        	  }
	        	  // we are in the middle of something
	        	  else{
	        		  // if we are already at the max, remove from the front
	        		  if (tempTokenNos.size() == maxLength){
	        			tempPoS.remove(0);
	        			tempStrings.remove(0);
	        			tempTokenNos.remove(0);
	        		  }
	        		  tempPoS.add(pos);
	        		  tempStrings.add(word);
	        		  tempTokenNos.add(tokenIndex);
	        	  }
	          }
	          // we allow prepositions in the middle
	          else if (pos.equals("IN")&&(tempPoS.size()>0)){
	        	  // if what we have already is more than one token and ends in N, keep it
	        	  if (tempPoS.get(tempPoS.size()-1).startsWith("N")){
		      	    	String tempKeyPhrase = "";
		      	    	
		      	    	for (String str:tempStrings){
		      	    		tempKeyPhrase+= str + " "; 
		      	    	}
		      	    	extracts.add(tempKeyPhrase.trim());
	        	  }
	        	  // Add it and see what happens
        		  // if we are already at the max, remove from the front
        		  if (tempTokenNos.size() == maxLength){
        			tempPoS.remove(0);
        			tempStrings.remove(0);
        			tempTokenNos.remove(0);
        		  }
        		  tempPoS.add(pos);
        		  tempStrings.add(word);
        		  tempTokenNos.add(tokenIndex);

	          }
	          // so we encountered a token of a different kind
	          else{
	        	  // if not empty and the last thing to be added is a noun then add it to the phrases to extract
	        	  if ((tempPoS.size()>0) && tempPoS.get(tempPoS.size()-1).startsWith("N")){
	      	    	String tempKeyPhrase = "";
	      	    	
	      	    	for (String str:tempStrings){
	      	    		tempKeyPhrase+= str + " "; 
	      	    	}
	      	    	extracts.add(tempKeyPhrase.trim());
	        		  
	        	  }
	        	  tempPoS.clear();
	        	  tempStrings.clear();
	        	  tempTokenNos.clear();
	          }

	          tokenIndex++;
	        }

	      }	    
		
		
		return extracts;
		
	}

//	public static void main(String[] args) {
//		KeyPhraseExtractor myExtractor = new KeyPhraseExtractor();
//		System.out.println("Done initializiing");
//		String text = "According to Barack Obama\'s newest unconstitutionally enacted law, health care professionals are now required to violate HIPAA privacy laws and submit medical data to the government which is then used as justification for gun confiscation.";
//	
//		Set<String> extracts = myExtractor.extract(text, 5);
//		for (String extract:extracts){
//			System.out.println(extract);
//		}
//		
//		
//	}

}
