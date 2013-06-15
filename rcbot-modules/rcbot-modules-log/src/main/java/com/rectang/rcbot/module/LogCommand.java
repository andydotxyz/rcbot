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
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

import java.util.List;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;

import com.rectang.rcbot.StorageImpl;
import com.rectang.rcbot.StringUtils;

/**
 * A module for insulting folk in a channel (nicely ish)
 *
 */
public class LogCommand extends RCBotCommand {

  List commands;

  public LogCommand(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);

    commands = new Vector();
    commands.add("url");
    commands.add("list-topics");
    commands.add("start-topic");
    commands.add("generate");
//    commands.add("stop-topic");
  }

  public String getId() {
    return "log";
  }

  public String getTitle() {
    return "A module to manage log stuff";
  }

  public Collection getCommands() {
    return commands;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    if (command.equals("url")) {
      String root = StorageImpl.getInstance("log", bot).getString("root-url");
      if (root == null || root.equals(""))
        root = "http://someserver.editconfig.com/";
      conn.sendMessage(channel,
          root + channel.replaceFirst("#", "%23") + "/");
    } else if (command.equals("list-topics")) {
      StringBuffer list = new StringBuffer();
      Collection topics = Log.getTopicList(channel, bot);

      /* no topics */
      if (topics.size() == 0) {
        conn.sendMessage(channel, "No active topics in " + channel);
      } else {

        /* list the topics */
        Iterator topicIter = topics.iterator();
        while (topicIter.hasNext()) {
          String topic = (String) topicIter.next();
          list.append(topic);
          if (topicIter.hasNext())
            list.append(" ");
        }
        conn.sendMessage(channel, list.toString());
      }
    } else if (command.equals("start-topic")) {
      String topic = StringUtils.getFirstArg(message);
      if (topic == null || topic.equals("") ||
          message.length() > topic.length()) {
        conn.sendMessage(channel, "You must specify 1 parameter - the topic name without spaces");
      } else {

        File topicFile = new File(Log.getTopicDir(channel, bot), topic);
        try {
          topicFile.createNewFile();
        } catch (IOException e) {
          conn.sendMessage(channel, "Could not create new topic output: "
              + e.getMessage());
        }
      }
    } else if (command.equals("generate")) {
      if (!bot.isOwner(user)) {
        conn.sendMessage(channel, "Sorry, " + user.getNick() +
            " only my owner can force log generation");
        return;
      }
      Log.getRoller().writeAllLogs();
      conn.sendMessage(channel, "Logs generated :)");
    }
  }

  public void onPrivateSubCommand(String command, IRCUser user,
                                  String message, IRCConnection conn) {
    conn.sendMessage(user.getNick(),
        "Logging and topics disabled in private chats");
  }

  public String getHelp(String channel) {
    return "Manage the channel logs - the following commands are available\n" +
        "  \"url\"         Get the URL of the logs for this channel\n" +
        "  \"list-topics\" List this channel's conversation threads\n" +
        "  \"start-topic\" Start a new conversation thread\n" +
        "  \"generate\"    Force log generation (admin)\n" + 
        "To have your chat logged in a topic use the [topicname] prefix";
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    conn.sendMessage(channel, "Log module does not understand command \"" +
        command + "\"\nTry help");
  }
}
