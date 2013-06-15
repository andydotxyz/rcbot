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

package com.rectang.rcbot.stub;

import com.rectang.rcbot.RCBot;
import com.rectang.rcbot.module.RCBotCommand;

import java.util.Collection;

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

/**
 * Simple module stub for testing
 */
public class ModuleStub extends RCBotCommand {

  public ModuleStub(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);
  }

  public String getId() {
    return "stub";
  }

  public String getTitle() {
    return "stub";
  }

  public void onSubCommand( String command, String channel, IRCUser user, String message, IRCConnection conn ) {
  }

  public Collection getCommands() {
    return NO_COMMANDS;
  }

  public String getHelp(String channel) {
    return "I am a stub";
  }

  public void onMissingCommand( String command, String channel, IRCUser ircUser, IRCConnection conn ) {
  }

  public void onQuit(IRCUser user, String reason, IRCConnection conn) {
  }
}
