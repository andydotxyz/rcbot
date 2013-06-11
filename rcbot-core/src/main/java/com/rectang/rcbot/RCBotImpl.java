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

package com.rectang.rcbot;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.*;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;

import java.io.File;

/**
 * Default implementation of the RCBot role
 *
 * @plexus.component
 *   role="com.rectang.rcbot.RCBot"
 */
public class RCBotImpl implements RCBot, Startable, LogEnabled {

  private String ownerNick, ownerLogin, ownerHost;
  private long startTime;

  private IRCConnection connection;
  private StorageImpl config;

  // A hack around broken plexus - remove ASAP
  static boolean started = false;

  /**
   * @plexus.requirement
   *   role="org.headsupdev.irc.IRCServiceManager"
   */
  private IRCServiceManager manager;

  /**
   * @plexus.configuration default-value="${rcbot.id}"
   */
  private String id;

  private Logger logger;

  public RCBotImpl() {
    this.startTime = System.currentTimeMillis();
  }

  private void init() {
    config = StorageImpl.getInstance("config", this);

    ownerNick = getConfig().getString("owner.nick");
    ownerLogin = getConfig().getString("owner.login");
    ownerHost = getConfig().getString("owner.host");
  }

  public String getId() {
    return id;
  }

  public long getStartTime() {
    return startTime;
  }

  public void connect() throws Exception {
    connection = manager.connect(getConfig().getString("bot.server"),
        getConfig().getString("bot.nick"), getConfig().getString("bot.password"),
        getConfig().getString("bot.user"), getConfig().getString("bot.name"));
    String channels[] = getConfig().getString("bot.channels").split(",");

    for (int i = 0; i < channels.length; i++) {
      String channel = channels[i].trim();
      connection.join(channel);
    }

    String nickServPass = getConfig().getString("nickserv.pass");
    if (nickServPass != null && !nickServPass.equals(""))
      connection.sendMessage("nickserv", "identify " + nickServPass);
  }

  public Config getConfig() {
    return config;
  }

  public boolean isOwner(IRCUser user) {
    if (ownerNick == null || ownerNick.equals("") ||
        ownerNick.equals(user.getNick()))
      if (ownerLogin == null || ownerLogin.equals("") ||
          user.getLogin().endsWith(ownerLogin))
        if (ownerHost == null || ownerHost.equals("") ||
            ownerHost.equals(user.getHost()))
          return true;
    return false;
  }

  public File getDataRoot() {
    return new File(getId() + "_data");
  }

  public void start() throws StartingException {
    if (started)
      return;

    logger.info("Starting RCBot[" + id + "]");
    started = true;
    init();
  }

  public void stop() throws StoppingException {
    logger.info("Stopping RCBot");
  }

  public void enableLogging(Logger logger) {
    this.logger = logger;
  }

  public IRCConnection getConnection() {
    return connection;
  }
}
