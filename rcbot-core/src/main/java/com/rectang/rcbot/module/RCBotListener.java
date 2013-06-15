package com.rectang.rcbot.module;

import com.rectang.rcbot.RCBot;
import org.headsupdev.irc.AbstractIRCListener;
import org.headsupdev.irc.IRCServiceManager;

/**
 * A base listener implementation for all listeners to inherit from
 *
 * @author Andrew Williams
 * @since 1.0
 */
public class RCBotListener extends AbstractIRCListener {

  protected IRCServiceManager manager;

  protected RCBot bot;

  public RCBotListener(RCBot bot, IRCServiceManager manager) {
    this.bot = bot;
    this.manager = manager;
  }

}
