package nz.ac.auckland.se206.dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

  /**
   * Calls the dictionary API and searches information of the word
   *
   * @param query the word to be searched
   * @return Information of the word
   * @throws IOException
   * @throws WordNotFoundException
   */
  public static WordInfo searchWordInfo(String query) throws IOException, WordNotFoundException {

    // Retrieve dictionary API
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

    /*
     * Retrieve definitions of the word as well as its part of speech (e.g, noun, verb, etc.)
     *
     * Each entry will have a part of speech and a list of definitions attached to it
     */
    for (int e = 0; e < jArray.length(); e++) {
      JSONObject jsonEntryObj = jArray.getJSONObject(e);
      JSONArray jsonMeanings = jsonEntryObj.getJSONArray("meanings");

      // set default part of speech as not specified
      String partOfSpeech = "[not specified]";
      List<String> definitions = new ArrayList<String>();

      // Get part of speech if it exists
      for (int m = 0; m < jsonMeanings.length(); m++) {
        JSONObject jsonMeaningObj = jsonMeanings.getJSONObject(m);
        String pos = jsonMeaningObj.getString("partOfSpeech");

        if (!pos.isEmpty()) {
          partOfSpeech = pos;
        }

        // Get definitions
        JSONArray jsonDefinitions = jsonMeaningObj.getJSONArray("definitions");
        for (int d = 0; d < jsonDefinitions.length(); d++) {
          JSONObject jsonDefinitionObj = jsonDefinitions.getJSONObject(d);

          String definition = jsonDefinitionObj.getString("definition");
          // Add definition
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

  /**
   * This method searches the word info of the given query word
   *
   * @param queryWord The word to search up on
   */
  public static void searchWord(String queryWord) {

    Task<Void> backgroundTask =
        new Task<Void>() {

          @Override
          protected Void call() throws Exception {

            try {
              WordInfo wordResult = DictionaryLookup.searchWordInfo(queryWord);
              System.out.println(
                  "\""
                      + wordResult.getWord()
                      + "\" has "
                      + wordResult.getNumberOfEntries()
                      + " dictionary entries.");

              // get word info
              List<WordEntry> entries = wordResult.getWordEntries();

              // Print word
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

                // Print definitions
                for (String definition : entry.getDefinitions()) {
                  System.out.println(definition);
                }
              }
            } catch (IOException e) {
              e.printStackTrace();
            } catch (WordNotFoundException e) {
              System.out.println("\"" + e.getWord() + "\" has problems: " + e.getMessage());
            }

            return null;
          }
        };

    Thread backgroundThread = new Thread(backgroundTask);
    backgroundThread.start();
  }
}
