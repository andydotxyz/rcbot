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

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

/**
 * A module for accessing dictionary features
 *
 */
public class DictionaryModule extends RCBotCommand {

  private Vector commands;

  private com.rectang.rcbot.Dictionary dict = new Dictionary();

  public DictionaryModule(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);

    commands = new Vector();
    commands.add("define");
    commands.add("spell");
    commands.add("random");
  }

  public String getId() {
    return "dict";
  }

  public String getTitle() {
    return "A dictionary module based on the dictd protocol";
  }

  public Collection getCommands() {
    return commands;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    String arg1 = StringUtils.getFirstArg(message);
    String args = message.substring(arg1.length()).trim();

    if (command.equals("define")) {
      if (args.length() == 0)
        conn.sendMessage(channel, dict.lookupWord(arg1));
      else
        conn.sendMessage(channel, dict.lookupWord(arg1, args));
    } else if (command.equals("spell")) {
      if (args.length() == 0)
        conn.sendMessage(channel, dict.matchWord(arg1));
      else
        conn.sendMessage(channel, dict.matchWord(arg1, args));
    } else if (command.equals("random")) {
      if (args.length() == 0)
        conn.sendMessage(channel, dict.randomWord());
      else
        conn.sendMessage(channel, dict.randomWord(arg1));
    }
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    conn.sendMessage(channel, "Dictionary does not understand command \"" +
        command + "\"\nTry define, spell or random");
  }

  public String getHelp(String parameters) {
    return "Dictionary is a module for getting information about words\n" +
        "It connects to a dictd server using the standard dictionary protocol";
  }
}
