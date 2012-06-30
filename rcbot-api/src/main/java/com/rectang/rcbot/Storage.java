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

public interface Storage extends Config {

  public void save() throws IOException;

  public String setString(String key, String value);

  public boolean setBoolean(String key, boolean value);

  public int setInt(String key, int value);

  public float setFloat(String key, float value);

  public String[] setStringList(String key, String[] list);

  public String[] appendStringList(String key, String value);

  public String[] removeStringList(String key, String value);

  public String remove(String key);
}
