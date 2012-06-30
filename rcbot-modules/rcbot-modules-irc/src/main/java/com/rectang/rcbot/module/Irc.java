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

import com.rectang.rcbot.*;

import java.util.*;
import java.io.IOException;

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

/**
 * A module for issueing IRC related commands
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCCommand"
 *   role-hint="irc"
 */
public class Irc extends ModuleImpl {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager"
   */
  private IRCServiceManager manager;

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.RCBot bot;

  private Vector commands;
  private boolean connected = false;

  public Irc() {
    commands = new Vector();
    commands.add("join");
    commands.add("part");
    commands.add("op");
    commands.add("deop");
    commands.add("voice");
    commands.add("devoice");
    commands.add("kick");
    commands.add("ban");
    commands.add("unban");
    commands.add("topic");
    commands.add("disconnect");
  }

  public String getId() {
    return "irc";
  }

  public String getTitle() {
    return "Bot management of IRC based commands";
  }

  public Collection getCommands() {
    return commands;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    if (!bot.isOwner(user)) {
      conn.sendMessage(channel, "Sorry, " + user.getNick() +
          " only my owner can use IRC commands");
      return;
    }

    String arg1 = StringUtils.getFirstArg(message);
    String theMessage = message.substring(arg1.length()).trim();
    String arg2 = StringUtils.getFirstArg(theMessage);

    if (command.equals("join")) {
      if (StringUtils.isChannel(arg1))
        conn.join(arg1);
      else
        conn.sendMessage(channel, arg1 + " is not a channel");

    } else if (command.equals("part")) {
      if (StringUtils.isChannel(arg1)) {
        String partMessage = theMessage;
        if (partMessage.length() == 0)
          partMessage = conn.getNick() + " go bye bye";
        conn.part(arg1, partMessage);
      } else
        conn.sendMessage(channel, arg1 + " is not a channel");

    } else if (command.equals("op")) {
      if (arg1.equals(""))
        conn.sendMessage(channel, "Must specify nick to op");
      else
        conn.op(channel, arg1);

    } else if (command.equals("deop")) {
      if (arg1.equals(""))
        conn.sendMessage(channel, "Must specify nick to deop");
      else
        conn.deOp(channel, arg1);

    } else if (command.equals("voice")) {
      if (arg1.equals(""))
        conn.sendMessage(channel, "Must specify nick to voice");
      else
        conn.voice(channel, arg1);

    } else if (command.equals("devoice")) {
      if (arg1.equals(""))
        conn.sendMessage(channel, "Must specify nick to devoice");
      else
        conn.deVoice(channel, arg1);

    } else if (command.equals("kick")) {
      if (arg1.equals(""))
        conn.sendMessage(channel, "Must specify nick to kick");
      else {
        if (arg2.equals(""))
          conn.kick(channel, arg1);
        else
          conn.kick(channel, arg1, arg2);
      }

    } else if (command.equals("ban")) {
      if (arg1.equals(""))
        conn.sendMessage(channel, "Must specify hostmask to ban");
      else
        conn.ban(channel, arg1);

    } else if (command.equals("unban")) {
      if (arg1.equals(""))
        conn.sendMessage(channel, "Must specify hostmask to unban");
      else
        conn.unBan(channel, arg1);

    } else if (command.equals("topic")) {
      if (message.equals(""))
        conn.sendMessage(channel, "Must specify topic to set");
      else
        conn.setTopic(channel, message);

    } else if (command.equals("disconnect")) {
      try {
        conn.disconnect();
      } catch (IOException e) {
        conn.sendMessage(channel, "Could not disconnect");
      }
    }
  }

  public void onRegistered(IRCConnection conn) {
    if (connected) {
      String channels[] = bot.getConfig().getString("bot.channels").split(",");

      for (int i = 0; i < channels.length; i++) {
        String channel = channels[i].trim();
        conn.sendMessage(channel, "And the bot came back the very next day!");
      }
    } else {
      connected = true;
    }
  }

  public String getHelp(String parameters) {
    return "Try the following commands:\n" +
        "  join #chan          - make the bot join a channel\n" +
        "  part #chan [reason] - make the bot leave a channel [with reason]\n" +
        "  op nick             - op user \"nick\" on this channel\n" +
        "  deop nick           - deop user \"nick\" no this channel\n" +
        "  voice nick          - voice user \"nick\" on this channel\n" +
        "  devoice nick        - devoice user \"nick\" on this channel\n" +
        "  kick nick [reason]  - kick user \"nick\" from this channel [with reason]\n" +
        "  ban hostmask        - ban hosts \"hostmask\" on this channel\n" +
        "  unban hostmask      - unban hosts \"hostmask\" on this channel\n" +
        "  topic some-topic    - set topic on this channel\n" +
        "  disconnect          - disconnect bot from the server";
  }

  public void onMissingCommand( String command, String channel, IRCUser user, IRCConnection conn ) {
    conn.sendMessage(channel, "IRC module does not understand command \"" +
        command + "\"\nTry help");
  }
}
