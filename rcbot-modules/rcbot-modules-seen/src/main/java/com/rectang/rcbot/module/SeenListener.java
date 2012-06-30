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
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.AbstractIRCListener;

import com.rectang.rcbot.Storage;
import com.rectang.rcbot.StorageImpl;

/**
 * A module for watching who is around
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCListener"
 *   role-hint="seen-listen"
 */
public class SeenListener extends AbstractIRCListener {

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.RCBot bot;

  public Storage getStore() {
    return StorageImpl.getInstance("seen", bot);
  }

  public void onMessage(String channel, IRCUser user, String message, IRCConnection conn) {
    getStore().setString(user.getNick(), System.currentTimeMillis() + ":" +
        channel + ":" + "saying " + message);
  }

  public void onPrivateMessage(IRCUser user, String message, IRCConnection conn) {
    /* ignore private messages */
  }

  public void onJoin(String channel, IRCUser user, IRCConnection conn) {
    getStore().setString(user.getNick(), System.currentTimeMillis() + ":" +
        channel + ":" + "joining");
  }
}
