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

import com.rectang.rcbot.*;

import java.util.*;

import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCUser;

/**
 * A module for managing the bot's configuration
 *
 */
public class ConfigModule extends RCBotCommand {

  private Vector commands;

  public ConfigModule(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);

    commands = new Vector();
    commands.add("list");
    commands.add("get");
    commands.add("set");
  }

  public String getId() {
    return "config";
  }

  public String getTitle() {
    return "Config management commands";
  }

  public Collection getCommands() {
    return commands;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    if (!bot.isOwner(user)) {
        conn.sendMessage(channel, "Sorry, " + user.getNick() +
          " only my owner can use config commands");
      return;
    }

    String arg1 = StringUtils.getFirstArg(message);

    Config config = bot.getConfig();
    if (command.equals("list")) {
      Enumeration keys = config.listKeys();
      while (keys.hasMoreElements()) {
        conn.sendMessage(channel, (String) keys.nextElement());
      }
    } else if (command.equals("get")) {
      if (arg1 == null || arg1.equals(""))
        conn.sendMessage(channel, "You need to specify a key");
      else {
        String value = config.getString(arg1);
        if (value == null)
          conn.sendMessage(channel, "Key \"" + arg1 + "\" not found");
        else
          conn.sendMessage(channel, value);
      }
    } /* else if (command.equals("set")) {
      if (arg1 == null || arg1.equals(""))
        sendMessage(channel, "You need to specify a key");
      else {
        String arg2 = StringUtils.getFirstArg(message.substring(arg1.length()));
        if (arg2 == null || arg2.equals("")) 
          sendMessage(channel, "You need to specify a value");
        else
          config.set(arg1, arg2);
      }
    } */
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
      conn.sendMessage(channel, "Config does not understand command \"" +
        command + "\"\nTry list, get or set");
  }

  public String getHelp(String parameters) {
    return "Here you can list, get and set config values.\n" +
        "  try \"list\" or \"get <key>\" or \"set <key> <value>\"";
  }

}
