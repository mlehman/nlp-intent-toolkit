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
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntentTrainer {

	public static String currentFile;

	public static String scanDir() {
        File actual = new File("/Users/bastienbotella/web_docs/NLP/nlp-intent-toolkit/fds/");
		Integer iteralol = 0;
        for( File f : actual.listFiles()) {
			if (f.getName().indexOf(".source") != -1) {
				iteralol++;
	            System.out.println(f.getName() + "----" + iteralol);
				IntentTrainer.currentFile = f.getName();
				return (f.getName());
			}
			try {
    //thread to sleep for the specified number of milliseconds
    Thread.sleep(100);
} catch ( java.lang.InterruptedException ie) {
System.out.println("tamere");
	System.out.println(ie);
}
        }
		return "";
    }

	public static String getNewLine() {
		String fileName;
		if ((fileName = IntentTrainer.scanDir()) != "") {
			File file = new File("/Users/bastienbotella/web_docs/NLP/nlp-intent-toolkit/fds/" + fileName);
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			DataInputStream dis = null;

			try {
				fis = new FileInputStream(file);
				// Here BufferedInputStream is added for fast reading.
				bis = new BufferedInputStream(fis);
				dis = new DataInputStream(bis);
				// dis.available() returns 0 if the file does not have more lines.
				while (dis.available() != 0) {
					// this statement reads the line from the file and print it to
					// the console.
					return (dis.readLine());
				}
				// dispose all the resources after using them.
				fis.close();
				bis.close();
				dis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static void removeFile(String fileName) {
        File fileToRemove = new File("/Users/bastienbotella/web_docs/NLP/nlp-intent-toolkit/fds/" + fileName);
		fileToRemove.delete();
    }

	public static void writeFile(String path, String content) {
		try {
			File file = new File(path);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
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
		Integer iteralol = 0;
		while (true) {
			while((s = IntentTrainer.getNewLine()) != ""){
				iteralol++;
				System.out.println(iteralol);
				double[] outcome = categorizer.categorize(s);
				String finalResult;
				finalResult = "{action:" + categorizer.getBestCategory(outcome) + " },args:{";

				String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(s);
				for (NameFinderME nameFinderME : nameFinderMEs) {
					Span[] spans = nameFinderME.find(tokens);
					String[] names = Span.spansToStrings(spans, tokens);
					for (int i = 0; i < spans.length; i++) {
						finalResult += spans[i].getType() + ":" + names[i] + " ";
					}
				}
				finalResult += "}";
				System.out.println(">");
				String[] fullFileName = IntentTrainer.currentFile.split("\\.");
				System.out.println(IntentTrainer.currentFile);
				System.out.println(fullFileName[0]);
				System.out.println(fullFileName[1]);
				String newFileName = fullFileName[0] + ".result";
				IntentTrainer.writeFile("/Users/bastienbotella/web_docs/NLP/nlp-intent-toolkit/fds/" + newFileName, finalResult);
				IntentTrainer.removeFile(IntentTrainer.currentFile);
			}
		}
	}
}
