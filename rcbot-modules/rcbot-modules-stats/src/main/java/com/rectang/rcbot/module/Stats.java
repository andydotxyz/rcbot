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

import java.util.*;
import java.io.File;

import com.rectang.rcbot.RCBot;
import com.rectang.rcbot.TemplateImpl;
import org.headsupdev.irc.IRCConnection;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.IRCUser;
import org.headsupdev.irc.AbstractIRCListener;
import org.apache.velocity.Template;

/**
 * A module used to keep channel statistics
 *
 */
public class Stats extends RCBotListener {

  private com.rectang.rcbot.Template template = new TemplateImpl();

  private File root = null, htmlroot = null;
  private Hashtable stats;

  public Stats(RCBot bot, IRCServiceManager manager) {
    super(bot, manager);

    stats = new Hashtable();
    (new StatHtml(this)).start();
  }

  public String getHelp(String channel) {
    return "Gather stats of what we see...";
  }

  protected Enumeration getChannelsLogged() {
    return stats.keys();
  }

  protected StatBean getStats(String channel) {
    StatBean ret = (StatBean) stats.get(channel);
    if (ret == null) {
      ret = StatMarshaller.read(channel, getDumpFile(channel));
      ret.setNick( bot.getConnection().getNick() );
      ret.setHost( bot.getConnection().getHost() );
      stats.put(channel, ret);
    }

    return ret;
  }

  public void onTopic(String channel, IRCUser user, String topic, IRCConnection conn) {
    getStats(channel).addTopic(new StatTopicBean(topic, user.getNick(),
        new Date()));
  }

  public void onMessage(String channel, IRCUser user, String message, IRCConnection conn) {
    StatBean stats = getStats(channel);
    List users = stats.getUsers();
    StatUserBean userBean = null;

    Enumeration userEnum = (new Vector(users)).elements();
    while (userEnum.hasMoreElements()) {
      StatUserBean bean = (StatUserBean) userEnum.nextElement();
      if (bean.getNick().equals(user.getNick())) {
        userBean = bean;
        break;
      }
    }
    if (userBean == null) {
      userBean = new StatUserBean(user.getNick());
      users.add(userBean);
    }
    userBean.incLines();
    userBean.setQuote(message);
    userBean.setLastSeen(new Date());
    stats.setDirty(true);
  }

  public void onPrivateMessage(IRCUser user, String message, IRCConnection conn) {
    /* Ignore private messages */
  }

  public File getStatRoot() {
    if (root == null) {
      root = new File(bot.getDataRoot(), "stats");
      root.mkdirs();
    }
    return root;
  }

  public File getHtmlStatRoot() {
    if (htmlroot == null) {
      htmlroot = new File(bot.getDataRoot(), "htmlstats");
      htmlroot.mkdirs();
    }
    return htmlroot;
  }

  public File getDumpFile(String channel) {
    return new File (getStatRoot(), channel + ".xml");
  }

  public File getOutputFile(String channel) {
    File chanFile = new File (getHtmlStatRoot(), channel);
    if (!chanFile.exists())
      chanFile.mkdir();
    return new File(chanFile, "index.html");
  }

  public Template getTemplate(String name) {
    try {
      return template.getTemplate(name);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
