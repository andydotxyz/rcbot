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
import org.headsupdev.irc.AbstractIRCListener;

import com.rectang.rcbot.StorageImpl;
import com.rectang.rcbot.Storage;

/**
 * A module for keeping track of people's/thing's karma
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCListener"
 *   role-hint="karma"
 */
public class KarmaListener extends AbstractIRCListener {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager"
   */
  private IRCServiceManager manager;

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.RCBot bot;

  public void onMessage(String channel, IRCUser user, String message, IRCConnection conn) {
    tryKarma(channel, user.getNick(), message, conn);
  }
/*
  public void onMessageMe(String channel, IRCUser user, String message) {
    tryKarma(channel, user.getNick(), message);
  }
*/
  private void tryKarma(String channel, String sender, String message, IRCConnection conn) {
    if (message.length() <= 2)
      return;

    String ident = message.substring(0, message.length() - 2).trim().toLowerCase();

    if (message.endsWith("++") && canKarma(sender, ident, channel, conn)) {
      getStore().setInt(ident, getStore().getInt(ident) + 1);
    } else if (message.endsWith("--") && canKarma(sender, ident, channel, conn)) {
      getStore().setInt(ident, getStore().getInt(ident)  - 1);
    }
  }

  private boolean canKarma(String sender, String topic, String channel, IRCConnection conn) {
    if (topic.equals(sender.toLowerCase())) {
      conn.sendMessage( channel, sender + ": you cannot karma yourself" );
      return false;
    }

    return true;
  }

  private Storage getStore() {
    return StorageImpl.getInstance("karma", bot);
  }
}
