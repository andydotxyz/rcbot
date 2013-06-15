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

import com.rectang.rcbot.module.RCBotCommand;
import com.rectang.rcbot.module.RCBotListener;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.impl.DefaultIRCServiceManager;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 * Proxy for implementation of the IRCServiceManager role, load commands and services from the classpath.
 *
 */
public class ProxyIRCServiceManager extends DefaultIRCServiceManager {

  public ProxyIRCServiceManager(final RCBot bot) {
    loadCommands(bot);
    loadListeners(bot);
  }

  private void loadCommands(final RCBot bot) {
    Set<Class<? extends RCBotCommand>> commands = getClassesOfType(RCBotCommand.class);

    for (Class<? extends RCBotCommand> descriptor : commands) {
      try {
        Constructor<? extends RCBotCommand> constructor = descriptor.getConstructor(RCBot.class, IRCServiceManager.class);

        addCommand(constructor.newInstance(bot, this));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void loadListeners(final RCBot bot) {
    Set<Class<? extends RCBotListener>> listeners = getClassesOfType(RCBotListener.class);

    for (Class<? extends RCBotListener> descriptor : listeners) {
      try {
        Constructor<? extends RCBotListener> constructor = descriptor.getConstructor(RCBot.class, IRCServiceManager.class);

        addListener(constructor.newInstance(bot, this));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  protected <T> Set<Class<? extends T>> getClassesOfType(Class<T> clazz) {
      Reflections reflections = new Reflections(ClasspathHelper.forJavaClassPath(), new SubTypesScanner());

      return reflections.getSubTypesOf(clazz);
  }
}

