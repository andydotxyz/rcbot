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

import org.apache.velocity.app.Velocity;

import java.io.File;

/**
 */
public class TemplateImpl implements Template {

  private boolean inited = false;

  public void init() {
    if (inited)
      return;
    inited = true;
    /* read the .vm's from the bot's data dir if they exist and from our
       * resources otherwise */
    Velocity.setProperty("resource.loader", "file, class");
    Velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    Velocity.setProperty("file.resource.loader.path", getTemplateRoot().getAbsolutePath());
    Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

    try {
      Velocity.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public org.apache.velocity.Template getTemplate(String name)
      throws Exception {
    init();
    return Velocity.getTemplate(name);
  }

  public File getTemplateRoot() {
    return new File(RCBot.getInstance().getDataRoot(), "templates");
  }
}
