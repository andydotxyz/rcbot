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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.rectang.rcbot.Storage;

/**
 * Html log roller for the channel logs
 */
public class HtmlRoller extends Thread {

  private Log logger;
  private File htmlroot;

  private FileFilter channelListFilter = new FileFilter() {
    public boolean accept(File pathname) {
      return pathname.getName().startsWith("#");
    }
  };
  private DateFormat dayFormat;

  public HtmlRoller(Log logger) {
    this.logger = logger;
    dayFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
  }

  private Date getMidnightToday() {
    Calendar todayBegin = Calendar.getInstance();
    todayBegin.setTime(new Date());
    todayBegin.set(Calendar.HOUR_OF_DAY, 0);
    todayBegin.set(Calendar.MINUTE, 0);
    todayBegin.set(Calendar.SECOND, 0);
    return todayBegin.getTime();
  }

  public File getHtmlLogRoot() {
    if (htmlroot == null) {
      htmlroot = new File(logger.getLogRoot(), "../htmllogs");
      htmlroot.mkdirs();
    }
    return htmlroot;
  }

  public void run() {

    while (0 == 0) {
      try {
        Thread.sleep(1000 * 60 * 15);
      } catch (InterruptedException e) {
        /* meh */
      }

      writeLogs(false);
    }
  }

  public void writeAllLogs() {
    writeLogs(true);
  }

  public void writeLogs(boolean all) {
    List channelList = new ArrayList();
    File[] channels = logger.getLogRoot().listFiles(channelListFilter);
    for (int i = 0; i < channels.length; i++) {
      String channel = channels[i].getName();

      Storage store = logger.getStore();
      Date start;
      try {
        start = logger.getFileFormat().parse(store.getString("channel." +
            channel + ".start"));
      } catch (Exception e) {
        start = getMidnightToday();
      }

      // default is to generate today's log and yesterdays, updating links etc
      // we could write yesterdays once only, but that involves more state...
      Date yesterday = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
      if (!all && start.before(yesterday)) {
        generateFiles(channel, channels[i], yesterday, true);
      } else {
        generateFiles(channel, channels[i], start, false);
      }
      channelList.add(new LogChannel(channel, logger.getChannelTopic(channel)));
    }
    VelocityContext context = new VelocityContext();
    context.put("channels", channelList);
    try {
      Writer out = new FileWriter(new File(getHtmlLogRoot(), "index.html"));
      logger.getTemplate("channellist.vm").merge(context, out);
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void generateFiles(String chanName, File chanDir, Date start, boolean append)
  {
    File htmlChan =
        new File(getHtmlLogRoot(), chanDir.getName());
    if (!htmlChan.exists())
      htmlChan.mkdirs();

    Calendar today = Calendar.getInstance();
    today.setTime(new Date());

    Calendar thisDay = Calendar.getInstance();
    thisDay.setTime(start);
    thisDay.set(Calendar.HOUR, 0);
    thisDay.set(Calendar.MINUTE, 0);
    thisDay.set(Calendar.SECOND, 0);

    String fileName = null, prevFileName = null;
    Date logDate = null;
    if (append) { // if we are appending we need to link back to previous pages
      thisDay.add(Calendar.DATE, -1);
      prevFileName = logger.getFileFormat().format(thisDay.getTime());
      thisDay.add(Calendar.DATE, 1);
    }

    for (; today.after(thisDay); thisDay.add(Calendar.DATE, 1)) {
      String nextFileName = logger.getFileFormat().format(thisDay.getTime());
      if (fileName != null) {
        rollHtml(new File(chanDir, fileName),
                new File(htmlChan, fileName + ".html"), chanName, true,
                prevFileName, nextFileName, false, logDate);
        prevFileName = fileName;
      }
      fileName = nextFileName;
      logDate = thisDay.getTime();
    }
    rollHtml(new File(chanDir, fileName),
            new File(htmlChan, fileName + ".html"), chanName, true,
            prevFileName, null, true, logDate);

    File topicHtmlChan = new File(htmlChan, "topics");
    if (!topicHtmlChan.exists())
      topicHtmlChan.mkdir();
    List topicList = new ArrayList();
    File[] topics = (new File(chanDir, "topics")).listFiles();
    if (topics != null) {
      for (int j = 0; j < topics.length; j++) {
        topicList.add(new LogChannel(topics[j].getName(), "&nbsp;"));
        rollHtml(topics[j], new File (topicHtmlChan,
                topics[j].getName() + ".html"), topics[j].getName(), false,
                null, null, false, null);
      }
    }
    VelocityContext context = new VelocityContext();
    context.put("channel", chanName);
    context.put("topics", topicList);
    try {
      Writer out = new FileWriter(new File(topicHtmlChan, "index.html"));
      logger.getTemplate("topiclist.vm").merge(context, out);
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void rollHtml(File log, File html, String channel,
                        boolean isChannel, String prev, String next,
                        boolean isToday, Date day) {
    try {
      VelocityContext context = new VelocityContext();
      context.put("prev", prev);
      context.put("next", next);
      if (day != null) {
        context.put("day", dayFormat.format(day));
      }
      if (log.exists()) {
        BufferedReader in = new BufferedReader(new FileReader(log));
        List lines = new ArrayList();
        String line;
        Hashtable nickhash = new Hashtable();
        while ((line = in.readLine()) != null)
          lines.add(new LogLine(logger, line, nickhash));
        in.close();

        context.put("lines", lines);
      }

      context.put("channel", channel);
      Writer out = new FileWriter(html);
      if (isChannel)
        logger.getTemplate("channel.vm").merge(context, out);
      else
        logger.getTemplate("topic.vm").merge(context, out);
      out.close();

      if (isToday) {
        context.put("index", Boolean.TRUE);
        out = new FileWriter(new File(html.getParentFile(), "index.html"));
        logger.getTemplate("channel.vm").merge(context, out);
        out.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
