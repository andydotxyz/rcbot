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
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.util.*;
import java.net.URL;

import org.headsupdev.irc.IRCServiceManager;

/**
 * A simple feed watcher.
 *
 * @author Andrew Williams
 * @since <pre>30-Oct-2006</pre>
 */
public class FeedWatcher {

  private RCBot bot;

  private Map feeds = new HashMap();
  private WatcherThread watcher = new WatcherThread();

  public FeedWatcher(RCBot bot) {
    this.bot = bot;
    watcher.start();
  }

  public SyndFeed updateFeed(String channel, String url) {
    try {
      URL feedUrl = new URL(url);

      SyndFeedInput input = new SyndFeedInput();
      SyndFeed feed = input.build(new XmlReader(feedUrl));

      WatchedFeed old = (WatchedFeed) feeds.get(channel + "-" + url);
      if (old == null)
        feeds.put(channel + "-" + url, new WatchedFeed(feed, url, channel));
      else
        old.setFeed(feed);
      return feed;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public void announceChanges() {
    watcher.interrupt();
  }

  public String addFeed(String channel, String url) {
    SyndFeed feed = updateFeed(channel, url);

    if (feed == null)
      return null;

    WatchedFeed watched = (WatchedFeed) feeds.get(channel + "-" + url);
    Iterator articles = watched.getFeed().getEntries().iterator();
    while (articles.hasNext()) {
      SyndEntry syndEntry = (SyndEntry) articles.next();
      watched.setAnnounced(syndEntry.getTitle());
    }

    return feed.getTitle();
  }

  public String removeFeed(String channel, String url) {
    WatchedFeed removing = (WatchedFeed) feeds.remove(channel + "-" + url);

    if (removing == null)
      return null;
    return removing.getFeed().getTitle();
  }

  private class WatcherThread extends Thread {

    public void run() {

      while (0 == 0) {
        try {
          Thread.sleep(1000 * 60 * 60); // 1 hour wait (slashdot etc ...)
        } catch (InterruptedException e) {
          /* meh */
        }

        Enumeration feedEnum = new Vector(feeds.values()).elements(); //not fail-fast
        while (feedEnum.hasMoreElements()) {
          WatchedFeed watched = (WatchedFeed) feedEnum.nextElement();

          String channel = watched.getChannel();
          updateFeed(channel, watched.getUrl());
          Iterator articles = watched.getFeed().getEntries().iterator();
          while (articles.hasNext()) {
            SyndEntry syndEntry = (SyndEntry) articles.next();
            String topic = syndEntry.getTitle();
            if (!watched.getAnnounced(topic)) {
              bot.getConnection().sendMessage( channel, "[" + watched.getFeed().getTitle()
                      + "] " + topic + " (" + syndEntry.getLink() + ")" );
              String body = syndEntry.getDescription().getValue();
              if (body.length() < 70)
                bot.getConnection().sendMessage(channel, "  " + body);
              else
                bot.getConnection().sendMessage(channel, "  " + body.substring(0, 70) + "...");
              watched.setAnnounced(topic);
            }
          }
        }
      }
    }
  }
}

class WatchedFeed {
  private SyndFeed feed;
  private String url;
  private String channel;

  private Set announced;

  public WatchedFeed(SyndFeed feed, String url, String channel) {
    this.feed = feed;
    this.url = url;
    this.channel = channel;
    this.announced = new HashSet();
  }

  public SyndFeed getFeed() {
    return feed;
  }

  public void setFeed(SyndFeed feed) {
    this.feed = feed;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public boolean getAnnounced(String topic) {
    return announced.contains(topic);
  }

  public void setAnnounced(String topic) {
    announced.add(topic);
  }

  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (!(o instanceof WatchedFeed))
      return false;

    WatchedFeed eq = (WatchedFeed) o;
    return feed.equals(eq.getFeed()) && channel.equals(eq.getChannel());
  }
}
