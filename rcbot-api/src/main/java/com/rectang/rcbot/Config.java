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
import java.util.Enumeration;

public interface Config {

  public void load() throws IOException;

  public Enumeration listKeys();

  public String getString(String key);

  public boolean getBoolean(String key);

  public int getInt(String key);

  public float getFloat(String key);

  public String[] getStringList(String key);

  public String getStringListFirst(String key);

  public String getStringListLast(String key);

  public String getStringListRandom(String key);
}
