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

import org.apache.velocity.VelocityContext;

import java.io.*;
import java.util.Enumeration;

/**
 * HTML writer for IRC stats
 */
public class StatHtml extends Thread {

  private Stats stats;

  public StatHtml(Stats stats) {
    this.stats = stats;
  }

  public void run() {

    while (0 == 0) {
      try {
        Thread.sleep(1000 * 60 * 60 * 24);
      } catch (InterruptedException e) {
        /* meh */
      }

      Enumeration channels = stats.getChannelsLogged();
      while (channels.hasMoreElements()) {
        String channel = (String) channels.nextElement();
        StatBean bean = stats.getStats(channel);

        if (!bean.isDirty())
          continue;

        /* want to hook this in to another timer somewhere */
        StatMarshaller.write(bean, stats.getDumpFile(channel));

        VelocityContext context = new VelocityContext();
        context.put("stats", bean);
        try {
          Writer out = new FileWriter(stats.getOutputFile(channel));
          stats.getTemplate("stats.vm").merge(context, out);
          out.close();
        } catch (Exception e) {
          e.printStackTrace();
        }

        bean.setDirty(false);
      }

    }
  }
}
