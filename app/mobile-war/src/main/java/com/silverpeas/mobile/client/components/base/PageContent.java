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

package com.silverpeas.mobile.client.components.base;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.silverpeas.mobile.client.SpMobil;
import com.silverpeas.mobile.client.common.EventBus;
import com.silverpeas.mobile.client.common.app.App;
import com.silverpeas.mobile.client.common.app.View;
import com.silverpeas.mobile.client.common.navigation.PageHistory;
import com.silverpeas.mobile.client.components.base.events.page.AbstractPageEvent;
import com.silverpeas.mobile.client.components.base.events.page.PageEvent;
import com.silverpeas.mobile.client.components.base.events.page.PageEventHandler;

public class PageContent extends Composite implements View, NativePreviewHandler, PageEventHandler {

  private App app;
  protected boolean clicked = false;
  protected String pageTitle = "Silverpeas";
  private HandlerRegistration registration;

  public PageContent() {
    super();
    registration = Event.addNativePreviewHandler(this);
    EventBus.getInstance().addHandler(AbstractPageEvent.TYPE, this);
  }

  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  public void show() {
    PageHistory.getInstance().goTo(this);
  }

  public boolean isVisible() {
    return PageHistory.getInstance().isVisible(this);
  }

  public void back() {
    PageHistory.getInstance().back();
  }

  public void clickGesture(Command call) {
    if (!clicked) {
      clicked = true;
      call.execute();
      Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
        @Override
        public boolean execute() {
          clicked = false;
          return false;
        }}, 400);
    }
  }

  @Override
  public void stop() {
    EventBus.getInstance().removeHandler(AbstractPageEvent.TYPE, this);
    registration.removeHandler();
    if (app != null) app.stop();
  }

  @Override
  public void onPreviewNativeEvent(NativePreviewEvent event) {
    if (event.getTypeInt() == Event.ONCLICK) {
      Element target = event.getNativeEvent().getEventTarget().cast();
      while(target.getParentElement() != null) {
        if (target.getId().equals("silverpeas-navmenu-panel") || target.getId().equals("menu")) {
          return;
        }
        target = target.getParentElement();
      }
      SpMobil.getMainPage().closeMenu();
    }
  }

  public App getApp() {
    return app;
  }

  public void setApp(App app) {
    this.app = app;
  }

  @Override
  public void receiveEvent(PageEvent event) {
    // for compatibility
  }
}
