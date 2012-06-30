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

package com.rectang.rcbot;

import org.headsupdev.irc.AbstractIRCListener;
import org.headsupdev.irc.IRCConnection;

/**
 * Main rcbot listener, for system events.
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCListener"
 *   role-hint="rcbot-listen"
 */
public class RCBotListener extends AbstractIRCListener {

  /**
   * @plexus.requirement
   */
  private com.rectang.rcbot.RCBot bot;

  public void onDisconnected(IRCConnection conn) {
    if (bot.getConfig().getBoolean("bot.reconnect")) {
      (new ReconnectionThread()).start();
    }
  }

  class ReconnectionThread extends Thread {
    public void run() {
      try {
        Thread.sleep(1000 * 2);
      } catch (InterruptedException e) {
        /* meh */
      }
      try {
        bot.connect();
      } catch (Exception e) {
        try {
          Thread.sleep(1000 * 20);
        } catch (InterruptedException e2) {
          /* meh */
        }
        try {
          bot.connect();
        } catch (Exception e2) {
          try {
            Thread.sleep(1000 * 60 * 2);
          } catch (InterruptedException e3) {
            /* meh */
          }
          try {
            bot.connect();
          } catch (Exception e3) {
            System.err.println("COULD NOT RECONNECT AFTER 2 minutes - giving up");
          }
        }
      }
    }
  }
}
