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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;

/**
 * Marshal a StatBean to and from the xml dump file
 */
public class StatMarshaller {

  private static XStream xstream;

  static {
    xstream = new XStream(new DomDriver()); // does not require XPP3 library
    xstream.alias("StatBean", StatBean.class);
    xstream.alias("topic", StatTopicBean.class);
    xstream.alias("user", StatUserBean.class);
  }

  public static StatBean read(String channel, File file) {
    StatBean ret = new StatBean(channel);
    try {
      xstream.fromXML(new FileReader(file), ret);
    } catch (FileNotFoundException e) {
      System.err.println("Creating new StatBean for " + channel);
    }
    return ret;
  }

  public static void write(StatBean stat, File file) {
    try {
      xstream.toXML(stat, new FileWriter(file));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
