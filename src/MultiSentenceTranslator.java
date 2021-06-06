import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;

import java.io.BufferedReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//setup java SDK https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-install.html
public class MultiSentenceTranslator {

    private static Scanner scan;

	public static void main(String[] args) throws IOException {
        // Define the text to be translated here
        String fileName = args[0];// "/Users/ncizmeli/Documents/iCollections/Folders/DeepNLP/test.txt"; //args[0]
    	String region = "ca-central-1";
 
        String sourceLang = "en";
        String targetLang = "tr";
        
        File txt = new File(fileName);
        scan = new Scanner(txt);
        ArrayList<String> data = new ArrayList<String>() ;
        while(scan.hasNextLine()){
            data.add(scan.nextLine());
        }
         String[] simpleArray = data.toArray(new String[]{});

    
        AWSCredentialsProviderChain DefaultAWSCredentialsProviderChain = new AWSCredentialsProviderChain(
                new SystemPropertiesCredentialsProvider(),
                new EnvironmentVariableCredentialsProvider(),
                new ProfileCredentialsProvider()
        );

        // Create an Amazon Translate client
        AmazonTranslate translate = AmazonTranslateClient.builder()
                .withCredentials(DefaultAWSCredentialsProviderChain)
                .withRegion(region)
                .build();
        List<String> result2 = new ArrayList<String>();
        // Translate sentences and print the results to stdout
        int counter = 0;
        for (String sentence : simpleArray) {
        	try
        	{
        	counter ++;
            TranslateTextRequest request = new TranslateTextRequest()
                    .withText(sentence)
                    .withSourceLanguageCode(sourceLang)
                    .withTargetLanguageCode(targetLang);
            TranslateTextResult result = translate.translateText(request);
            System.out.println("Original text: " + sentence);
            String r = result.getTranslatedText();
            System.out.println("Translated text: " + r);
            result2.add(Integer.toString(counter)+";"+ r);
        	}
        	catch(Exception ex)
        	{
                result2.add(Integer.toString(counter)+";<Error>");
        		ex.printStackTrace();
        	}
        }
        
        String outputFileName = args[1];// "output.txt";
        
        FileWriter writer = new FileWriter(outputFileName); 
        for (String temp : result2) {
         
          writer.write(temp + System.lineSeparator());
        }
        writer.close();
        System.out.println("finito...");
    }

}

class SentenceSegmenter {
    public List<String> segment(final String text, final String lang) throws Exception {
        List<String> res = new ArrayList<>();
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(new Locale(lang));
        sentenceIterator.setText(text);
        int prevBoundary = sentenceIterator.first();
        int curBoundary = sentenceIterator.next();
        while (curBoundary != BreakIterator.DONE) {
            String sentence = text.substring(prevBoundary, curBoundary);
            res.add(sentence);
            prevBoundary = curBoundary;
            curBoundary = sentenceIterator.next();
        }
        return res;
    }

}