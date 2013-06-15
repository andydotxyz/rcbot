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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

public class StorageImpl extends ConfigImpl implements Storage {

  private static Hashtable<String,StorageImpl> dbs = new Hashtable<String,StorageImpl>();

  private StorageImpl(String dbName, RCBot bot) {
    super(dbName, bot);
    dbs.put(dbName, this);

    createDir();
  }

  private void createDir() {
    File dir = getFileName().getParentFile();

    if (!dir.exists()) {
      if (!dir.mkdir()) {
        System.err.println(bot.getId() + ": Storage: could not create data dir");
      }
    } else if (!dir.isDirectory()) {
      System.err.println(bot.getId() + ": Storage: data dir is not a dir");
    }

    if (!dir.canWrite()) {
      System.err.println(bot.getId() + ": Storage: data dir is not writable");
    }
  }

  public static StorageImpl getInstance(String dbName, RCBot bot) {
    StorageImpl storage = dbs.get(dbName);
    if (storage == null)
      storage = new StorageImpl(dbName, bot);

    return storage;
  }

  public void save() throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(getFileName()));

    Enumeration keys = db.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();

      writer.write(key + "=" + db.get(key));
      writer.newLine();
    }
    writer.close();
  }

  protected String set(String key, String value) {
    String theKey = key.toLowerCase();
    String ret = get(theKey);
    db.put(theKey, value);

    try {
      save(); /* FIXME - we should not be saving all the time */
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  public String setString(String key, String value) {
    return set(key, value);
  }

  public boolean setBoolean(String key, boolean value) {
    boolean ret = getBoolean(key);

    set(key, String.valueOf(value));
    return ret;
  }

  public int setInt(String key, int value) {
    int ret = getInt(key);

    set(key, String.valueOf(value));
    return ret;
  }

  public float setFloat(String key, float value) {
    float ret = getFloat(key);

    set(key, String.valueOf(value));
    return ret;
  }

  public String[] setStringList(String key, String[] list) {
    String[] current = getStringList(key);
    String setting = "";
    for (String aList : list) {
      if (aList != null) {
        if (setting.length() > 0)
          setting = setting.concat("|");
        setting = setting.concat(aList.replaceAll("\\|", "\\\\|"));
      }
    }
    set(key, setting);

    return current;
  }

  public String[] appendStringList(String key, String value) {
    String[] old = getStringList(key);
    ArrayList<String> newList = new ArrayList<String>();
    if (old != null) {
      Collections.addAll(newList, old);
    }

    newList.add(value.replaceAll("\\|", "\\\\|"));
    setStringList(key, newList.toArray(new String[newList.size()]));
    return old;
  }

  public String[] removeStringList(String key, String value) {
    String[] old = getStringList(key);
    ArrayList<String> newList = new ArrayList<String>();
    if (old != null) {
      for (String anOld : old) {
        if (!anOld.equals(value))
          newList.add(anOld);
      }
    }
    setStringList(key, newList.toArray(new String[newList.size()]));
    return old;
  }

  public String remove(String key) {
    String ret = db.remove(key.toLowerCase());

    try {
      save(); /* FIXME - we should not be saving all the time */
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  protected File getFileName() {
    return new File(bot.getDataRoot(), name + ".storage");
  }
}
