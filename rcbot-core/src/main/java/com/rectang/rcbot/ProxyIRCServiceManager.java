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

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.*;
import org.headsupdev.irc.IRCCommand;
import org.headsupdev.irc.IRCListener;
import org.headsupdev.irc.IRCServiceManager;
import org.headsupdev.irc.impl.DefaultIRCServiceManager;

import java.util.Iterator;
import java.util.List;

/**
 * Proxy for implementation of the IRCServiceManager role, load commands and services from plexus config.
 *
 * @plexus.component
 *   role="org.headsupdev.irc.IRCServiceManager"
 */
public class ProxyIRCServiceManager extends DefaultIRCServiceManager implements Serviceable {
  private ServiceLocator locator;

  // A hack around broken plexus - remove ASAP
  static LoadThread lookups;

  public void service(final ServiceLocator serviceLocator) {
    this.locator = serviceLocator;

    if (lookups != null) {
      lookups.cancel();
    }

    (lookups = new LoadThread() {
      public void run() {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          // just execute anyway - we will cancel before it happens
        }

        if (cancelled)
          return;

        loadCommands();
        loadListeners();
      }
    }).start();
  }

  private void loadCommands() {
    List commands;
    try {
      commands = locator.lookupList(IRCCommand.class.getName());
    } catch (ComponentLookupException e) {
      System.err.println("Error looking up commands: " + e.getMessage());
      return;
    }

    Iterator cmdIter = commands.iterator();
    while (cmdIter.hasNext()) {
      IRCCommand command = (IRCCommand) cmdIter.next();
      ((DefaultIRCServiceManager) IRCServiceManager.getInstance()).addCommand(command);
    }
  }

  private void loadListeners() {
    List listeners;
    try {
      listeners = locator.lookupList(IRCListener.class.getName());
    } catch (ComponentLookupException e) {
      System.err.println("Error looking up listener: " + e.getMessage());
      return;
    }

    Iterator listIter = listeners.iterator();
    while (listIter.hasNext()) {
      IRCListener listener = (IRCListener) listIter.next();
      ((DefaultIRCServiceManager) IRCServiceManager.getInstance()).addListener(listener);
    }
  }
}

class LoadThread extends Thread {
  protected boolean cancelled = false;

  public void cancel() {
    cancelled = true;
  }
}
