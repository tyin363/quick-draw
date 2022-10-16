package nz.ac.auckland.se206.dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.exceptions.WordNotFoundException;
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
   * Calls the dictionary API and searches information of the word.
   *
   * @param query the word to be searched
   * @return Information of the word
   * @throws IOException if the API call fails
   * @throws WordNotFoundException There was no definition for that word
   */
  public static WordInfo searchWordInfo(final String query)
      throws IOException, WordNotFoundException {

    // Retrieve dictionary API
    final OkHttpClient client = new OkHttpClient();
    final Request request = new Request.Builder().url(API_URL + query).build();
    final Response response = client.newCall(request).execute();
    final ResponseBody responseBody = response.body();

    final String jsonString = responseBody.string();

    try {
      final JSONObject jsonObj = (JSONObject) new JSONTokener(jsonString).nextValue();
      final String title = jsonObj.getString("title");
      final String subMessage = jsonObj.getString("message");
      throw new WordNotFoundException(query, title, subMessage);
    } catch (final ClassCastException e) {
      System.out.println("Dictionary Lookup Class Cast Exception Catch Section Called");
    }
    final JSONArray jArray = (JSONArray) new JSONTokener(jsonString).nextValue();
    final List<WordEntry> entries = new ArrayList<WordEntry>();

    /*
     * Retrieve definitions of the word as well as its part of speech (e.g, noun, verb, etc.)
     *
     * Each entry will have a part of speech and a list of definitions attached to it
     */
    for (int e = 0; e < jArray.length(); e++) {
      final JSONObject jsonEntryObj = jArray.getJSONObject(e);
      final JSONArray jsonMeanings = jsonEntryObj.getJSONArray("meanings");

      // set default part of speech as not specified
      String partOfSpeech = "[not specified]";
      final List<String> definitions = new ArrayList<String>();

      // Get part of speech if it exists
      for (int m = 0; m < jsonMeanings.length(); m++) {
        final JSONObject jsonMeaningObj = jsonMeanings.getJSONObject(m);
        final String pos = jsonMeaningObj.getString("partOfSpeech");

        if (!pos.isEmpty()) {
          partOfSpeech = pos;
        }

        // Get definitions
        final JSONArray jsonDefinitions = jsonMeaningObj.getJSONArray("definitions");
        for (int d = 0; d < jsonDefinitions.length(); d++) {
          final JSONObject jsonDefinitionObj = jsonDefinitions.getJSONObject(d);

          final String definition = jsonDefinitionObj.getString("definition");
          // Add definition
          if (!definition.isEmpty()) {
            definitions.add(definition);
          }
        }
      }

      final WordEntry wordEntry = new WordEntry(partOfSpeech, definitions);
      entries.add(wordEntry);
    }

    return new WordInfo(query, entries);
  }
}
