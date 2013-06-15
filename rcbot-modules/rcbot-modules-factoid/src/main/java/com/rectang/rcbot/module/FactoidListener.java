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

import com.rectang.rcbot.RCBot;
import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.AbstractIRCListener;

import java.util.Date;

import com.rectang.rcbot.Storage;
import com.rectang.rcbot.StorageImpl;
import com.rectang.rcbot.ConfigImpl;

/**
 * A module for managing small facts etc
 *
 */
public class FactoidListener extends RCBotListener {

  private static String public_listen_key =
      ConfigImpl.getModuleConfigKey("factoid", "public_listen");
  private static String public_respond_key =
      ConfigImpl.getModuleConfigKey("factoid", "public_respond");

  public FactoidListener(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);
  }

  public void onMessage(String channel, IRCUser user, String message, IRCConnection conn) {
    if (bot.getConfig().getBoolean(public_listen_key))
      doMessage(channel, user, message,
          bot.getConfig().getBoolean(public_respond_key), false, conn);
  }

  private void doMessage(String channel, IRCUser user, String message,
                         boolean reply, boolean directed, IRCConnection conn) {
    if (message.endsWith("?")) {
      if (reply || directed) {
        String query = message.substring(0, message.length() - 1).trim();
        String match1 = getIsStore().getStringListRandom(query);
        String match2 = getAreStore().getStringListRandom(query);

        boolean found = false;
        if (match1 != null && !match1.equals("")) {
          sendFactoidMessage(channel, user.getNick(), query, match1, "is", conn);
          found = true;
        }
        if (match2 != null && !match2.equals("")) {
          sendFactoidMessage(channel, user.getNick(), query, match2, "are", conn);
          found = true;
        }
        if (directed && !found) {
          conn.sendMessage(channel, "How should I know?");
        }
      }
    } else {
      int pos = message.indexOf(" is ");

      if (pos != -1) {
        String key = message.substring(0, pos).trim();
        if (message.length() > pos + 9 &&
            message.substring(pos + 4, pos + 9).equals("also ")) {
          getIsStore().appendStringList(key, message.substring(pos + 9).trim());
          if (directed)
            conn.sendMessage(channel, "ok, thanks");
        } else {
          String value = getIsStore().getStringListFirst(key);

          if (value != null && !value.equals("")) {
            if (directed)
              conn.sendMessage(channel, "But " + key + " is " + value + "...");
          } else {
            getIsStore().setString(key, message.substring(pos + 4).trim());
            if (directed)
              conn.sendMessage(channel, "ok, thanks");
          }
        }
      } else {
        pos = message.indexOf(" are ");

        if (pos != -1) {
          String key = message.substring(0, pos).trim();
          if (message.length() > pos + 10 &&
              message.substring(pos + 5, pos + 10).equals("also ")) {
            getAreStore().appendStringList(key, message.substring(pos + 10).trim());
            if (directed)
              conn.sendMessage(channel, "got it now");
          } else {
            String value = getAreStore().getStringListFirst(key);

            if (value != null && !value.equals("")) {
              if (directed)
                conn.sendMessage(channel, "But " + key + " are " + value + "...");
            } else {
              getAreStore().setString(key, message.substring(pos + 5).trim());
              if (directed)
                conn.sendMessage(channel, "got it now");
            }
          }
        }
      }
    }
  }

  private void sendFactoidMessage(String channel, String sender, String key,
                                  String value, String type, IRCConnection conn) {
    String theValue = value.replaceAll("<who>", sender);
    theValue = theValue.replaceAll("<date>", (new Date()).toString());

    if (theValue.startsWith("<reply>") && theValue.length() > 7)
      conn.sendMessage(channel, theValue.substring(7).trim());
    else if (theValue.startsWith("<action>") && theValue.length() > 8)
      conn.sendAction(channel, theValue.substring(8).trim());
    else
      conn.sendMessage(channel, "I heard that " + key + " " + type + " " + theValue);
  }

  private Storage getIsStore() {
    return StorageImpl.getInstance("factoid-is", bot);
  }

  private Storage getAreStore() {
    return StorageImpl.getInstance("factoid-are", bot);
  }
}
