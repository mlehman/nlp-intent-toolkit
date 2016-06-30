package nlp.intent.toolkit;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.*;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntentTrainer {

	private static String readLine() throws Exception {
        // System.out.println("start");
		if (System.console() != null) {
        	System.out.println("read.console is not null !!!");
			return System.console().readLine();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader( System.in));
        System.out.println("start2");
		System.out.println(reader.ready());
		if (reader.ready() != false) {
			if (reader.readLine() == null) {
	        	System.out.println("read is null !!!");
			} else {
	        	System.out.println("else");
	        	//System.out.println(reader.readLine());
			}
			return reader.readLine();
		}
        System.out.println("start3");
		return "";
	}

    public static void main(String[] args) throws Exception {

        File trainingDirectory = new File(args[0]);
        String[] slots = new String[0];
        if(args.length > 1){
            slots = args[1].split(",");
        }


        if(!trainingDirectory.isDirectory()) {
            throw new IllegalArgumentException("TrainingDirectory is not a directory: " + trainingDirectory.getAbsolutePath());
        }

        List<ObjectStream<DocumentSample>> categoryStreams = new ArrayList<ObjectStream<DocumentSample>>();
        for (File trainingFile : trainingDirectory.listFiles()) {
            String intent = trainingFile.getName().replaceFirst("[.][^.]+$", "");
            ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingFile), "UTF-8");
            ObjectStream<DocumentSample> documentSampleStream = new IntentDocumentSampleStream(intent, lineStream);
            categoryStreams.add(documentSampleStream);
        }
        ObjectStream<DocumentSample> combinedDocumentSampleStream = ObjectStreamUtils.createObjectStream(categoryStreams.toArray(new ObjectStream[0]));

        DoccatModel doccatModel = DocumentCategorizerME.train("fr", combinedDocumentSampleStream, 0, 100);
        combinedDocumentSampleStream.close();

        List<TokenNameFinderModel> tokenNameFinderModels = new ArrayList<TokenNameFinderModel>();

        for(String slot : slots) {
            List<ObjectStream<NameSample>> nameStreams = new ArrayList<ObjectStream<NameSample>>();
            for (File trainingFile : trainingDirectory.listFiles()) {
                ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingFile), "UTF-8");
                ObjectStream<NameSample> nameSampleStream = new NameSampleDataStream(lineStream);
                nameStreams.add(nameSampleStream);
            }
            ObjectStream<NameSample> combinedNameSampleStream = ObjectStreamUtils.createObjectStream(nameStreams.toArray(new ObjectStream[0]));

            TokenNameFinderModel tokenNameFinderModel = NameFinderME.train("fr", "societe", combinedNameSampleStream, TrainingParameters.defaultParams(),
                    (AdaptiveFeatureGenerator)null, Collections.<String, Object>emptyMap());
            combinedNameSampleStream.close();
            tokenNameFinderModels.add(tokenNameFinderModel);
        }


        DocumentCategorizerME categorizer = new DocumentCategorizerME(doccatModel);
        NameFinderME[] nameFinderMEs = new NameFinderME[tokenNameFinderModels.size()];
        for(int i = 0; i < tokenNameFinderModels.size(); i++) {
            nameFinderMEs[i] = new NameFinderME(tokenNameFinderModels.get(i));
        }

        System.out.println("Training complete. Ready.");
        System.out.print(">");
        String s;
        while((s = IntentTrainer.readLine()) != null){
        	System.out.println(s);
            double[] outcome = categorizer.categorize(s);
            System.out.print("action=" + categorizer.getBestCategory(outcome) + " args={ ");

            String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(s);
            for (NameFinderME nameFinderME : nameFinderMEs) {
                Span[] spans = nameFinderME.find(tokens);
                String[] names = Span.spansToStrings(spans, tokens);
                for (int i = 0; i < spans.length; i++) {
                    System.out.print(spans[i].getType() + "=" + names[i] + " ");
                }
            }
            System.out.println("}");
            System.out.print(">");

        }
    }

}
