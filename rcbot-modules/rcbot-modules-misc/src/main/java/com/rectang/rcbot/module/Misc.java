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

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

/**
 * A module with random commands like uptime and botsnack
 *
 */
public class Misc extends RCBotCommand {

  public Misc(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);
  }

  public String getId() {
    return "misc";
  }

  public String getTitle() {
    return "Get misc bot stats";
  }

  public void onSubCommand(String command, String channel, IRCUser user, String message, IRCConnection conn) {
    conn.sendMessage(channel, "This RCBot up " +
        StringUtils.formatTimeOffset((System.currentTimeMillis() -
            bot.getStartTime()) / 1000));
  }

}
