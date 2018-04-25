/*
 * Copyright (C) 2000 - 2018 Silverpeas
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
 */

package org.silverpeas.mobile.shared.dto.almanach;

import java.io.Serializable;

/**
 * @author svu
 */
public class CalendarEventAttendeeDTO implements Serializable {
  private String id;
  private String fullName;
  private ParticipationStatusDTO participationStatus;
  private PresenceStatusDTO presenceStatus;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public ParticipationStatusDTO getParticipationStatus() {
    return participationStatus;
  }

  public void setParticipationStatus(final ParticipationStatusDTO participationStatus) {
    this.participationStatus = participationStatus;
  }

  public PresenceStatusDTO getPresenceStatus() {
    return presenceStatus;
  }

  public void setPresenceStatus(final PresenceStatusDTO presenceStatus) {
    this.presenceStatus = presenceStatus;
  }
}
