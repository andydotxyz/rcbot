/*
 * Copyright 2006-2012 Andrew Williams.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rectang.rcbot.module;

import java.io.*;
import java.net.Socket;

/**
 * A dictionary for random wordy stuff
 *
 * @plexus.component
 *   role="com.rectang.rcbot.Dictionary"
 */
public class Dictionary implements com.rectang.rcbot.Dictionary {

  private static String DEFAULT_DICT = "wn";
  private static String DICT_SERVER = "dict.org";
  private static int DICT_PORT = 2628;

  /* Based on "IRC Hacks #52" http://hacks.oreilly.com/pub/h/1984 */
  public String lookupWord(String word) {
    return lookupWord(Dictionary.DEFAULT_DICT, word);
  }

  public String lookupWord(String dict, String word) {
    try {
      Dictionary.DictConnection con = new Dictionary.DictConnection();
      String ret = lookupWord(con, dict, word);
      con.close();

      return ret;
    } catch (IOException e) {
      return "Failed to lookup word on server " + Dictionary.DICT_SERVER;
    }
  }

  private String lookupWord(Dictionary.DictConnection con, String book,
                            String word) throws IOException {
    con.getWriter().write("DEFINE " + book + " \"" + word + "\"\r\n");
    con.getWriter().flush();

    String definition = "";
    String line;
    while ((line = con.getReader().readLine( )) != null) {

      // 552 No match.
      if (line.startsWith("552")) {
        return "No matches for \"" + word + "\", try " +
            matchWord(con, book, word);
      }

      // 151 word database name - text follows.
      if (line.startsWith("151")) {
        if (book.equals("foldoc")) {
          // Skip first 2 lines returned (header and blank line).
          con.getReader().readLine( );
          con.getReader().readLine( );
        }

        definition = "";
        while ((line = con.getReader().readLine( )) != null) {
          if (line.trim( ).equals("") || line.startsWith("2") ||
              line.startsWith(".")) {
            break;
          } else {
            definition += line + "\n";
          }
        }
        break;
      }

      if (line.startsWith("2")) {
        break;
      }
    }

    return definition;
  }

  public String matchWord(String word) {
    return matchWord(Dictionary.DEFAULT_DICT, word);
  }

  public String matchWord(String dict, String word) {
    try {
      Dictionary.DictConnection con = new Dictionary.DictConnection();
      String ret = matchWord(con, dict, word);
      con.close();

      return ret;
    } catch (IOException e) {
      return "Failed to lookup word on server " + Dictionary.DICT_SERVER;
    }
  }

  private String matchWord(Dictionary.DictConnection con, String book,
                           String word) throws IOException {
    // Levenshtein algorithm to try to get *some* result.
    con.getWriter().write("MATCH " + book + " lev \"" + word + "\"\r\n");
    con.getWriter().flush( );
    String line = con.getReader().readLine( );

    if (line.startsWith("552")) {
      // nothing found
      return "";
    } else if(line.startsWith("152")) {
      // 152 n matches found - text follows.
      String[] parts = line.split(" ");
      int numMatches = Integer.valueOf(parts[1]).intValue();

      // Some similar words were found ...
      String reply = "";
      int count = 0;
      while ((line = con.getReader().readLine( )) != null) {
        if (count > numMatches || line.startsWith(".")) {
          break;
        }

        reply += " " + line.substring(line.indexOf("\""),
            line.lastIndexOf("\"") + 1);

        count++;
      }
      return reply;
    } else {
      throw new IOException("Something unexpected happened: " + line);
    }
  }

  public String randomWord() {
    return randomWord(Dictionary.DEFAULT_DICT);
  }

  public String randomWord(String dict) {
    try {
      Dictionary.DictConnection con = new Dictionary.DictConnection();
      String ret = randomWord(con, dict);
      con.close();

      return ret;
    } catch (IOException e) {
      return "Failed to lookup word on server " + Dictionary.DICT_SERVER;
    }
  }

  private String randomWord(Dictionary.DictConnection con, String book)
      throws IOException {

    /* generate a random 3 char string to seed a substring search */
    char[] s = new char[3];
    s[0] = (char)('a' +  (int)(Math.random() * 26));
    s[1] = (char)('a' +  (int)(Math.random() * 26));
    s[2] = (char)('a' +  (int)(Math.random() * 26));
    String seed = new String(s);

    con.getWriter().write("m " + book + " substring \"" + seed + "\"\r\n");
    con.getWriter().flush( );
    String line = con.getReader().readLine( );

    if (line.startsWith("552")) {
      // nothing found - try again
      return randomWord(con, book);
    } else if(line.startsWith("152")) {
      // 152 n matches found - pick a random word from 0 to n
      String[] parts = line.split(" ");
      int numMatches = Integer.valueOf(parts[1]).intValue();
      int myPick = (int)(Math.random() * numMatches);

      String reply = null;
      int count = 0;
      while ((line = con.getReader().readLine( )) != null) {
        if (count > numMatches || line.startsWith(".")) {
          break;
        }

        if (count == myPick)
          reply = line.substring(line.indexOf("\"") + 1,
            line.lastIndexOf("\""));
        count++;
      }

      if (reply == null)
        return randomWord(con, book);
      return reply;
    } else {
      throw new IOException("Something unexpected happened: " + line);
    }
  }

  class DictConnection {

    private Socket sock;
    private BufferedReader reader;
    private BufferedWriter writer;

    public DictConnection() throws IOException {
      sock = new Socket(Dictionary.DICT_SERVER, Dictionary.DICT_PORT);
      reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
      reader.readLine(); // ignore welcome message
    }

    public BufferedReader getReader() {
      return reader;
    }

    public BufferedWriter getWriter() {
      return writer;
    }

    public void close() throws IOException {
      sock.close();
    }
  }
}
