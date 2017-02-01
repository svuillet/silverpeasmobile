/*
 * Copyright (C) 2000 - 2017 Silverpeas
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.silverpeas.mobile.shared.dto.configuration;

import com.silverpeas.mobile.shared.dto.BaseDTO;

import java.io.Serializable;

public class Config extends BaseDTO implements Serializable {

  private int newsNumber;
  private boolean newsDisplay;

  public Config() {
  }

  public static Config getDefaultConfig() {
    Config c = new Config();
    c.setNewsDisplay(false);
    c.setNewsNumber(5);
    return c;
  }

  public int getNewsNumber() {
    return newsNumber;
  }

  public void setNewsNumber(int newsNumber) {
    this.newsNumber = newsNumber;
  }

  public boolean isNewsDisplay() {
    return newsDisplay;
  }

  public void setNewsDisplay(boolean newsDisplay) {
    this.newsDisplay = newsDisplay;
  }
}
