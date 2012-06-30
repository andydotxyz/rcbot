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

import org.headsupdev.irc.AbstractIRCListener;
import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.IRCServiceManager;
import com.rectang.rcbot.Storage;
import com.rectang.rcbot.StorageImpl;

/**
 * A module for watching who is active so we can send them tell messages
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCListener"
 *   role-hint="tell-listen"
 */
public class TellListener extends AbstractIRCListener {

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager"
   */
  private IRCServiceManager manager;

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.RCBot bot;

  public Storage getStore() {
    return StorageImpl.getInstance("tell", bot);
  }

  public void onMessage(String channel, IRCUser user, String message, IRCConnection conn) {
    getTell(channel, user.getNick(), conn);
  }

  public void onPrivateMessage(IRCUser user, String message, IRCConnection conn) {
    /* ignore private messages */
  }

  public void onJoin(String channel, IRCUser user, IRCConnection conn) {
    getTell(channel, user.getNick(), conn);
  }

  private void getTell(String channel, String sender, IRCConnection conn) {
    String[] tell = getStore().getStringList(sender + ":" + channel);
    if (tell != null && tell.length != 0) {
      for (int i = 0; i < tell.length; i++)
        conn.sendMessage(channel, sender + ": " + tell[i]);
      getStore().remove(sender + ":" + channel);
    }
  }
}
