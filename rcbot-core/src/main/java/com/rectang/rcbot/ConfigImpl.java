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

package com.rectang.rcbot;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class ConfigImpl implements Config {

  private static ConfigImpl instance = null;

  protected RCBot bot;
  protected Hashtable<String,String> db;
  protected String name;

  /* TODO, make hashtable be key => value list */
  protected ConfigImpl(String dbName, RCBot bot) {
    this.db = new Hashtable<String,String>();
    this.bot = bot;
    this.name = dbName;

    try {
      load();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static ConfigImpl getInstance(RCBot bot) {
    if (instance == null)
      instance = new ConfigImpl("config", bot);

    return instance;
  }

  public void load() throws IOException {
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(getFileName()));
    } catch (FileNotFoundException e) {
      return;
    }

    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0 || line.charAt(0) == '!' || line.charAt(0) == '#')
        continue;

      int pos = line.indexOf('=');
      if (pos != -1)
        db.put(line.substring(0, pos).toLowerCase(), line.substring(pos + 1));
      else
        db.put(line.toLowerCase(), "");
    }
    reader.close();
  }

  public Enumeration listKeys() {
    return db.keys();
  }

  protected String get(String key) {
    return db.get(key.toLowerCase());
  }

  public String getString(String key) {
    return get(key);
  }

  public boolean getBoolean(String key) {
    return Boolean.valueOf(get(key));
  }

  public int getInt(String key) {
    try {
      return Integer.parseInt(get(key));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public float getFloat(String key) {
    try {
      return Float.parseFloat(get(key));
    } catch (NumberFormatException e) {
      return 0.0f;
    }
  }

  public String[] getStringList(String key) {
    String line = get(key);
    if (line == null || line.trim().length() == 0)
      return null;
    ArrayList<String> list = new ArrayList<String>();

    int pos = 0;
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) == '|') {
        if (line.charAt(i - 1) != '\\') {
          list.add(line.substring(pos, i)
              .replaceAll("\\|", "|"));
          pos = i + 1;
        }
      }
    }
    if (pos < line.length())
      list.add(line.substring(pos, line.length())
          .replaceAll("\\|", "|"));

    return list.toArray(new String[list.size()]);
  }

  public String getStringListFirst(String key) {
    String[] list = getStringList(key);
    if (list == null || list.length == 0)
      return null;
    return list[0];
  }

  public String getStringListLast(String key) {
    String[] list = getStringList(key);
    if (list == null || list.length == 0)
      return null;
    return list[list.length - 1];
  }

  public String getStringListRandom(String key) {
    String[] list = getStringList(key);
    if (list == null || list.length == 0)
      return null;
    int random = (int) Math.floor(Math.random() * list.length);
    /* not likely at all, but could happen 
     * going to list.length - 1 hardly ever yields the last
     * element of the list */
    if (random == list.length)
      random = list.length;
    return list[random];
  }

  protected File getFileName() {
    return new File(bot.getDataRoot(), "config.storage");
  }

  public static String getModuleConfigKey(String module, String key) {
    return "module." + module + "." + key;
  }
}
