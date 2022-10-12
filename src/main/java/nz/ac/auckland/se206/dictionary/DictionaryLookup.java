package nz.ac.auckland.se206.dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.concurrent.Task;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class DictionaryLookup {

  private static final String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

  public static WordInfo searchWordInfo(String query) throws IOException, WordNotFoundException {

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(API_URL + query).build();
    Response response = client.newCall(request).execute();
    ResponseBody responseBody = response.body();

    String jsonString = responseBody.string();

    try {
      JSONObject jsonObj = (JSONObject) new JSONTokener(jsonString).nextValue();
      String title = jsonObj.getString("title");
      String subMessage = jsonObj.getString("message");
      throw new WordNotFoundException(query, title, subMessage);
    } catch (ClassCastException e) {
    }

    JSONArray jArray = (JSONArray) new JSONTokener(jsonString).nextValue();
    List<WordEntry> entries = new ArrayList<WordEntry>();

    for (int e = 0; e < jArray.length(); e++) {
      JSONObject jsonEntryObj = jArray.getJSONObject(e);
      JSONArray jsonMeanings = jsonEntryObj.getJSONArray("meanings");

      String partOfSpeech = "[not specified]";
      List<String> definitions = new ArrayList<String>();

      for (int m = 0; m < jsonMeanings.length(); m++) {
        JSONObject jsonMeaningObj = jsonMeanings.getJSONObject(m);
        String pos = jsonMeaningObj.getString("partOfSpeech");

        if (!pos.isEmpty()) {
          partOfSpeech = pos;
        }

        JSONArray jsonDefinitions = jsonMeaningObj.getJSONArray("definitions");
        for (int d = 0; d < jsonDefinitions.length(); d++) {
          JSONObject jsonDefinitionObj = jsonDefinitions.getJSONObject(d);

          String definition = jsonDefinitionObj.getString("definition");
          if (!definition.isEmpty()) {
            definitions.add(definition);
          }
        }
      }

      WordEntry wordEntry = new WordEntry(partOfSpeech, definitions);
      entries.add(wordEntry);
    }

    return new WordInfo(query, entries);
  }

  public static void searchWord(String queryWord) {
    String queryWords = queryWord;

    long startTime = System.currentTimeMillis();

    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            String query = queryWords;
            try {
              WordInfo wordResult = DictionaryLookup.searchWordInfo(query);
              System.out.println(
                  "\""
                      + wordResult.getWord()
                      + "\" has "
                      + wordResult.getNumberOfEntries()
                      + " dictionary entries.");

              // get word info
              List<WordEntry> entries = wordResult.getWordEntries();

              for (int e = 0; e < entries.size(); e++) {
                WordEntry entry = entries.get(e);

                System.out.println(
                    "Entry "
                        + (e + 1)
                        + " of "
                        + entries.size()
                        + " ["
                        + entry.getPartOfSpeech()
                        + "]:");

                for (String definition : entry.getDefinitions()) {
                  System.out.println(definition);
                }
              }
              Platform.runLater(
                  () -> {
                    // resultsAccordion.getPanes().add(pane);
                  });

            } catch (IOException e) {
              e.printStackTrace();
            } catch (WordNotFoundException e) {
              System.out.println("\"" + e.getWord() + "\" has problems: " + e.getMessage());
              // TitledPane pane = WordPane.generateErrorPane(e);
              Platform.runLater(
                  () -> {
                    // resultsAccordion.getPanes().add(pane);
                  });
            }
            // progressBar.setProgress((s + 1.0) / numberOfWords);

            long time = System.currentTimeMillis() - startTime;
            System.out.println();
            System.out.println("Search took " + time + "ms");

            return null;
          }
        };

    // backgroundTask.setOnSucceeded(event -> {
    // searchForDefinitionsButton.setDisable(false);
    // });
    //
    // backgroundTask.setOnFailed(event -> {
    // searchForDefinitionsButton.setDisable(false);
    // });

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }
}
