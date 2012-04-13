/**
 * Copyright (C) 2000 - 2011 Silverpeas
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
 * "http://www.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.silverpeas.mobile.server.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import com.silverpeas.mobile.shared.dto.SocialInformationDTO;
import com.silverpeas.mobile.shared.exceptions.AuthenticationException;
import com.silverpeas.mobile.shared.exceptions.DashboardException;
import com.silverpeas.mobile.shared.services.ServiceDashboard;
import com.silverpeas.socialNetwork.model.SocialInformation;
import com.silverpeas.socialNetwork.model.SocialInformationType;
import com.silverpeas.socialNetwork.relationShip.RelationShipService;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.beans.admin.UserDetail;
import com.stratelia.webactiv.util.DateUtil;

public class ServiceDashboardImpl extends AbstractAuthenticateService implements ServiceDashboard {

  private static final long serialVersionUID = 1L;
  private String myId;
  public Date currentDate = new Date();

  public List<SocialInformationDTO> getAll(int reinitialisationPage, String socialInformationType)
      throws DashboardException, AuthenticationException {
    Calendar begin = Calendar.getInstance(Locale.FRANCE);
    Calendar end = Calendar.getInstance(Locale.FRANCE);

    begin.set(Calendar.MONTH, -1);
    com.silverpeas.calendar.Date dBegin = new com.silverpeas.calendar.Date(begin.getTime());
    com.silverpeas.calendar.Date dEnd = new com.silverpeas.calendar.Date(end.getTime());

    List<String> myContactIds = getMyContactsIds();

    List<com.silverpeas.socialNetwork.model.SocialInformation> socialInformationsFull = null;
    socialInformationsFull =
        new ProviderService().getSocialInformationsListOfMyContact(SocialInformationType.ALL, myId,
            myContactIds, dBegin, dEnd);

    if (SocialInformationType.ALL.equals(SocialInformationType.ALL)) {
      Collections.sort(socialInformationsFull);
    }

    return processResults(socialInformationsFull, reinitialisationPage, socialInformationType);
  }

  public List<String> getMyContactsIds() throws AuthenticationException {
    try {
      checkUserInSession();
      UserDetail user = getUserInSession();
      myId = user.getId();
      return new RelationShipService().getMyContactsIds(Integer.parseInt(myId));
    } catch (SQLException ex) {
      SilverTrace.error("com.silverpeas.mobile.server.services",
          "ServiceDashboardImpl.getMyContactsIds", "", ex);
    }
    return new ArrayList<String>();
  }

  public List<SocialInformationDTO> processResults(
      List<com.silverpeas.socialNetwork.model.SocialInformation> socialInformationsFull,
      int reinitialisationPage, String socialInformationType) {
    String date = null;
    LinkedHashMap<Date, List<SocialInformationDTO>> hashtable =
        new LinkedHashMap<Date, List<SocialInformationDTO>>();
    List<SocialInformationDTO> lsi = new ArrayList<SocialInformationDTO>();

    for (SocialInformation information : socialInformationsFull) {
      if (DateUtil.formatDate(information.getDate()).equals(date)) {
        SocialInformationDTO socialInformationDTO = new SocialInformationDTO();
        socialInformationDTO.setDescription(information.getDescription());
        socialInformationDTO.setDate(information.getDate());
        socialInformationDTO.setType(information.getType());
        socialInformationDTO.setAuteur(information.getAuthor());
        lsi.add(socialInformationDTO);
      } else {
        date = DateUtil.formatDate(information.getDate());
        lsi = new ArrayList<SocialInformationDTO>();
        SocialInformationDTO socialInformationDTO = new SocialInformationDTO();
        socialInformationDTO.setDescription(information.getDescription());
        socialInformationDTO.setDate(information.getDate());
        socialInformationDTO.setType(information.getType());
        socialInformationDTO.setAuteur(information.getAuthor());
        lsi.add(socialInformationDTO);
        hashtable.put(information.getDate(), lsi);
      }
    }
    return processFiltreDate(hashtable, reinitialisationPage, socialInformationType);
  }

  public List<SocialInformationDTO> processFiltreDate(
      LinkedHashMap<Date, List<SocialInformationDTO>> hashtable, int reinitialisationPage,
      String socialInformationType) {
    if (reinitialisationPage == 0) {
      currentDate = new Date();
    }
    List<SocialInformationDTO> listSocialInformationDTO = new ArrayList<SocialInformationDTO>();
    for (Iterator<Date> i = hashtable.keySet().iterator(); i.hasNext();) {
      Date key = i.next();
      if (currentDate.after(key)) {
        Iterator<SocialInformationDTO> j = hashtable.get(key).iterator();
        while (j.hasNext()) {
          SocialInformationDTO siDTO = j.next();
          if (socialInformationType.equals(siDTO.getType()) || socialInformationType.equals("ALL")) {
            listSocialInformationDTO.add(siDTO);
            currentDate = key;
          }
        }
        return listSocialInformationDTO;
      }
    }
    return listSocialInformationDTO;
  }
}
