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

import org.headsupdev.irc.AbstractIRCCommand;
import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;
import com.rectang.rcbot.StringUtils;

/**
 * A module with random commands like uptime and botsnack
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCCommand"
 *   role-hint="time"
 */
public class Time extends AbstractIRCCommand {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager"
   */
  private IRCServiceManager manager;

  public String getId() {
    return "time";
  }

  public void onCommand(String channel, IRCUser user, String message, IRCConnection conn) {
    String nick = StringUtils.getFirstArg(message);
    if (nick.length() == 0 || nick.equals(conn.getNick()))
      conn.sendMessage(channel, "The time is " + new java.util.Date());
    else {
      if (nick.length() < message.length()) {
        conn.sendMessage(channel,
            "The time command accepts only 1 optional argument");
      } else {
        (new TimeThread(nick, channel, conn)).start();
      }
    }
  }

  public String getHelp(String channel) {
    return "The time command is used to get the time on various computers.\n" +
        "  Used without arguments it tells you the time on the bot's server.\n" +
        "  Append a nick to the command to get the time on that persons computer";
  }

  class TimeThread extends Thread {
    private String channel, nick;
    private IRCConnection conn;

    public TimeThread(String nick, String channel, IRCConnection conn) {
      this.nick = nick;
      this.channel = channel;
      this.conn = conn;
    }

    public void run() {
      conn.sendMessage(channel, "The time at " + nick +
           "'s computer is " + conn.getTime(nick));
    }
  }
}
