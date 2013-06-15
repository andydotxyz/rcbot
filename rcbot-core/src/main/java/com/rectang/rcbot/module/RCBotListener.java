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
