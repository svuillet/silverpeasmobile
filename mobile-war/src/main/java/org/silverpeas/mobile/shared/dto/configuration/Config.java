/*
 * Copyright (C) 2000 - 2022 Silverpeas
 *
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
 * "https://www.silverpeas.org/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.silverpeas.mobile.shared.dto.configuration;

import com.google.web.bindery.autobean.shared.AutoBean;
import org.silverpeas.mobile.client.common.storage.LocalStorageHelper;

public class Config {

  private boolean newsDisplay;
  private boolean lastPublicationsDisplay;
  private boolean lastEventsDisplay;
  private boolean favoritesDisplay;
  private boolean shortCutsDisplay;
  private boolean shortCutsToolsDisplay;
  private boolean freeZoneDisplay;
  private boolean freeZoneThinDisplay;

  public Config() {
  }

  public static Config getDefaultConfig() {
    Config c = new Config();
    c.setNewsDisplay(true);
    c.setLastPublicationsDisplay(true);
    c.setLastEventsDisplay(true);
    c.setFavoritesDisplay(true);
    c.setShortCutsDisplay(true);
    c.setShortCutsToolsDisplay(true);
    c.setFreeZoneDisplay(true);
    c.setFreeZoneThinDisplay(true);
    return c;
  }

  public boolean isFreeZoneThinDisplay() {
    return freeZoneThinDisplay;
  }

  public void setFreeZoneThinDisplay(final boolean freeZoneThinDisplay) {
    this.freeZoneThinDisplay = freeZoneThinDisplay;
  }

  public boolean isNewsDisplay() {
    return newsDisplay;
  }

  public void setNewsDisplay(boolean newsDisplay) {
    this.newsDisplay = newsDisplay;
  }

  public boolean isLastPublicationsDisplay() {
    return lastPublicationsDisplay;
  }

  public void setLastPublicationsDisplay(final boolean lastPublicationsDisplay) {
    this.lastPublicationsDisplay = lastPublicationsDisplay;
  }

  public void setLastEventsDisplay(final boolean lastEventsDisplay) {
    this.lastEventsDisplay = lastEventsDisplay;
  }

  public boolean isFavoritesDisplay() {
    return favoritesDisplay;
  }

  public void setFavoritesDisplay(final boolean favoritesDisplay) {
    this.favoritesDisplay = favoritesDisplay;
  }

  public boolean isLastEventsDisplay() {
    return lastEventsDisplay;
  }

  public boolean isShortCutsDisplay() {
    return shortCutsDisplay;
  }

  public void setShortCutsDisplay(final boolean shortCutsDisplay) {
    this.shortCutsDisplay = shortCutsDisplay;
  }

  public boolean isFreeZoneDisplay() {
    return freeZoneDisplay;
  }

  public void setFreeZoneDisplay(final boolean freeZoneDisplay) {
    this.freeZoneDisplay = freeZoneDisplay;
  }

  public boolean isShortCutsToolsDisplay() {
    return shortCutsToolsDisplay;
  }

  public void setShortCutsToolsDisplay(final boolean shortCutsToolsDisplay) {
    this.shortCutsToolsDisplay = shortCutsToolsDisplay;
  }

  public AutoBean<IConfig> getAutoBean () {
    AutoBean<IConfig> b = LocalStorageHelper.factory.iconfig();
    b.as().setFreeZoneDisplay(isFreeZoneDisplay());
    b.as().setFavoritesDisplay(isFavoritesDisplay());
    b.as().setFreeZoneThinDisplay(isFreeZoneThinDisplay());
    b.as().setLastEventsDisplay(isLastEventsDisplay());
    b.as().setNewsDisplay(isNewsDisplay());
    b.as().setLastPublicationsDisplay(isLastPublicationsDisplay());
    b.as().setShortCutsDisplay(isShortCutsDisplay());
    b.as().setShortCutsToolsDisplay(isShortCutsToolsDisplay());
    return b;
  }

  public static Config getBean (AutoBean<IConfig> b) {
    Config conf = new Config();
    conf.setFreeZoneDisplay(b.as().isFreeZoneDisplay());
    conf.setFavoritesDisplay(b.as().isFavoritesDisplay());
    conf.setFreeZoneThinDisplay(b.as().isFreeZoneThinDisplay());
    conf.setLastEventsDisplay(b.as().isLastEventsDisplay());
    conf.setNewsDisplay(b.as().isNewsDisplay());
    conf.setLastPublicationsDisplay(b.as().isLastPublicationsDisplay());
    conf.setShortCutsDisplay(b.as().isShortCutsDisplay());
    conf.setShortCutsToolsDisplay(b.as().isShortCutsToolsDisplay());
    return conf;
  }
}

