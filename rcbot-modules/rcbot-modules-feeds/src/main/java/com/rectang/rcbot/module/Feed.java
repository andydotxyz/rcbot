/*
 * Copyright 2006-2013 Andrew Williams.
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

import com.rectang.rcbot.RCBot;
import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.IRCServiceManager;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.Enumeration;

import com.rectang.rcbot.StorageImpl;
import com.rectang.rcbot.Storage;

/**
 * A module for announcing articles on remote feeds.
 * 
 * @author Andrew Williams
 * @since <pre>30-Oct-2006</pre>
 */
public class Feed extends RCBotCommand {

  /**
   */
  private FeedWatcher watcher;

  List commands;

  public Feed(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);

    commands = new Vector();
    commands.add("watch");
    commands.add("unwatch");
    commands.add("update");

    initialize();
  }

  public String getId() {
    return "feeds";
  }

  public String getTitle() {
    return "A module for monitoring feeds such as RSS, Atom etc";
  }

  public Collection getCommands() {
    return commands;
  }

  public void onSubCommand(String command, String channel, IRCUser user, String message, IRCConnection conn) {
    channel = channel.toLowerCase();
    Storage conf = StorageImpl.getInstance("feeds", bot);
    if (command.equals("watch")) {
      conf.appendStringList(channel + ".feeds", message);
      String title = watcher.addFeed(channel, message);
      conn.sendMessage(channel, "Added the feed \"" + title + "\"");
    } else if (command.equals("unwatch")) {
      conf.removeStringList(channel + ".feeds", message);
      String title = watcher.removeFeed(channel, message);
      conn.sendMessage(channel, "Removed the feed \"" + title + "\"");
    } else if (command.equals("update")) {
      watcher.announceChanges();
    }
  }

  public void onMissingCommand(String command, String channel, IRCUser ircUser, IRCConnection conn) {
    conn.sendMessage(channel, "Feeds module does not understand command \"" +
        command + "\"\nTry help");
  }

  public void initialize() {
    watcher = new FeedWatcher(bot);
    Storage conf = StorageImpl.getInstance("feeds", bot);

    Enumeration keys = conf.listKeys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String channel = key.substring(0, key.indexOf('.'));
      String[] feedList = conf.getStringList(key);

      if (feedList != null) {
        for (int i = 0; i < feedList.length; i++) {
          watcher.addFeed(channel, feedList[i]);
        }
      }
    }
  }
}
