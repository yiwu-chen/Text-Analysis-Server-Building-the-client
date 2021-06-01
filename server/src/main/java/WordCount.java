/**
 * The word count function that able to count the number of words in a line of text.
 */
public class WordCount {
  /**
   * Method that counts the number of words in a string.
   * @param line a line of words to be processed
   * @return the word count
   */
  public static int count(String line) {
    int count = 0;
    if (!(" ".equals(line.substring(0, 1))) || !(" ".equals(line.substring(line.length() - 1)))) {
      for (int i = 0; i < line.length(); i++) {
        if (line.charAt(i) == ' ') {
          count++;
        }
      }
      count = count + 1;
    }
    return count;
  }
}
