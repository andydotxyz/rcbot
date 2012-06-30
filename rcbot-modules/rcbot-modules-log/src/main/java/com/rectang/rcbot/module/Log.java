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

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.AbstractIRCListener;
import org.apache.velocity.Template;
import com.rectang.rcbot.Storage;
import com.rectang.rcbot.StorageImpl;
import com.rectang.rcbot.RCBot;

/**
 * A module used to log chat in a channel
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCListener"
 *   role-hint="log-listen"
 */
public class Log extends AbstractIRCListener {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager
   */
  private IRCServiceManager manager;

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.RCBot bot;

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.Template template;

  private DateFormat fileFormat = new SimpleDateFormat("yyyyMMdd");
  private DateFormat timeFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

  private Set ignoredPrefixes;

  private static HtmlRoller roller;

  public Log() {
    ignoredPrefixes = new HashSet();
    ignoredPrefixes.add("fatal");
    ignoredPrefixes.add("error");
    ignoredPrefixes.add("warn");
    ignoredPrefixes.add("info");
    ignoredPrefixes.add("debug");

    roller = new HtmlRoller(this);
    roller.start();
  }

  public static HtmlRoller getRoller() {
    return roller;
  }

  public void onMessage(String channel, IRCUser user, String message, IRCConnection conn) {
    String prefix = null;
    if (message.length() > 0 && message.charAt(0) == '[') {
      int prefEnd = message.indexOf(']', 1);
      if (prefEnd > 1)
        prefix = message.substring(1, prefEnd).toLowerCase();

      if (prefix!= null && prefix.equals("off"))
        return;
    }

    Date today = new Date();
    String logMessage = new StringBuffer()
        .append(timeFormat.format(today)).append(" ").append(user.getNick())
        .append(": ").toString();
    log(channel, fileFormat.format(today), logMessage + message);
    if (prefix != null && ! prefix.equals("")) {
      if (getTopicList(channel).contains(prefix)) {

        /* this created the dir */
        getTopicDir(channel);
        log(channel, "topics/" + prefix,
            logMessage + message.substring(prefix.length() + 2).trim());
      } else {
        if (!ignoredPrefixes.contains(prefix))
          conn.sendMessage(channel, "Topic \"" + prefix + "\" does not exist");
      }
    }
  }

  public void onJoin(String channel, IRCUser ircUser, IRCConnection conn) {
    if (!ircUser.getNick().equals(conn.getNick()))
      return;

    Storage store = getStore();
    String started = store.getString("channel." + channel + ".start");
    if (started == null || started.equals(""))
      store.setString("channel." + channel + ".start", fileFormat.format(new Date()));
  }

  public void onPrivateMessage(IRCUser user, String message, IRCConnection conn) {
    /* ignore private messages */
  }

  public void onAction(String target, IRCUser user, String action, IRCConnection conn) {
    if (target.charAt(0) != '#')
      return;

    Date today = new Date();
    String logMessage = new StringBuffer()
        .append(timeFormat.format(today)).append(" * ").append(user.getNick())
        .append(" ").toString();
    log(target, fileFormat.format(today), logMessage + action);
  }

  public String getHelp(String channel) {
    return "All messages on this channel will be logged unless\nprefixed with [off]";
  }

  public Storage getStore() {
    return StorageImpl.getInstance("log", bot);
  }

  public String getChannelTopic(String channel) {
    return bot.getConnection().getTopic(channel);
  }

  public DateFormat getFileFormat() {
    return fileFormat;
  }

  public DateFormat getTimeFormat() {
    return timeFormat;
  }

  public Collection getTopicList(String channel) {
    return getTopicList(channel, bot);
  }

  public static Collection getTopicList(String channel, RCBot bot) {
    String[] files = getTopicDir(channel, bot).list();
    if (files == null || files.length == 0)
      return new Vector();

    return Arrays.asList(files);
  }

  public File getLogRoot() {
    return getLogRoot(bot);
  }

  public static File getLogRoot(RCBot bot) {
    File ret = new File(bot.getDataRoot(), "logs");
    if (!ret.exists())
      ret.mkdirs();

    return ret;
  }

  public File getLogFile(String channel, String file) {
    return getLogFile(channel, file, bot);
  }

  public static File getLogFile(String channel, String file, RCBot bot) {
    File chanFile = new File (getLogRoot(bot), channel);
    if (!chanFile.exists())
      chanFile.mkdir();
    return new File(chanFile, file);
  }

  public File getTopicDir(String channel) {
    return getTopicDir(channel, bot);
  }

  public static File getTopicDir(String channel, RCBot bot) {
    File ret = getLogFile(channel, "topics", bot);
    if (!ret.exists())
      ret.mkdir();

    return ret;
  }

  private void log(String channel, String file, String message) {

    File to = getLogFile(channel, file, bot);
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(to, true));
      out.write(message);
      out.newLine();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Template getTemplate(String name) {
    try {
      return template.getTemplate(name);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
