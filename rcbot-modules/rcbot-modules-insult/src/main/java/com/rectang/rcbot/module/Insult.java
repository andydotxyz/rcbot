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

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

import java.util.*;

/**
 * A module for insulting folk in a channel (nicely ish)
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCCommand"
 *   role-hint="insult"
 */
public class Insult extends ModuleImpl {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager"
   */
  private IRCServiceManager manager;

  List arg1s, arg2s, arg3s, arg4s;

  public Insult() {
    arg1s = new Vector();
    arg1s.add("steaming");
    arg1s.add("expansive");
    arg1s.add("deadly");

    arg2s = new Vector();
    arg2s.add("pile");
    arg2s.add("truck load");
    arg2s.add("shed full");

    arg3s = new Vector();
    arg3s.add("monkey");
    arg3s.add("elephant");
    arg3s.add("horse");

    arg4s = new Vector();
    arg4s.add("balls");
    arg4s.add("droppings");
    arg4s.add("breath");
  }

  public String getId() {
    return "insult";
  }

  public String getTitle() {
    return "A module to insult stuff";
  }

  public Collection getCommands() {
    return NO_COMMANDS;
  }

  public void onSubCommand(String command, String channel, IRCUser user,
                           String message, IRCConnection conn) {
    conn.sendMessage(channel, generateInsult(message));
  }

  public String getHelp(String parameters) {
    return "Insult is a module for randomly insulting specified stuff";
  }

  public void onMissingCommand(String command, String channel, IRCUser user, IRCConnection conn) {
    /* NO_COMMANDS so cannot use this */
  }
  
  private String generateInsult(String name) {
    StringBuffer result = new StringBuffer();
    result.append(name);
    result.append(" is a ");
    appendRandom(arg1s, result);
    result.append(" ");
    appendRandom(arg2s, result);
    result.append(" of ");
    appendRandom(arg3s, result);
    result.append(" ");
    appendRandom(arg4s, result);
    result.append(".");

    return result.toString();
  }

  private void appendRandom(List inputs, StringBuffer out) {
    int rand = (int) (Math.random() * inputs.size());

    out.append(inputs.get(rand));
  }
}
