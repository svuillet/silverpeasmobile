package org.silverpeas.mobile.shared.dto.configuration;

/**
 * @author svu
 */
public interface IConfig {


  public boolean isFreeZoneThinDisplay();


  public void setFreeZoneThinDisplay(final boolean freeZoneThinDisplay);


  public boolean isNewsDisplay();


  public void setNewsDisplay(boolean newsDisplay);


  public boolean isLastPublicationsDisplay();

  public void setLastPublicationsDisplay(final boolean lastPublicationsDisplay);

  public void setLastEventsDisplay(final boolean lastEventsDisplay);

  public boolean isFavoritesDisplay();

  public void setFavoritesDisplay(final boolean favoritesDisplay);

  public boolean isLastEventsDisplay();

  public boolean isShortCutsDisplay();

  public void setShortCutsDisplay(final boolean shortCutsDisplay);

  public boolean isFreeZoneDisplay();

  public void setFreeZoneDisplay(final boolean freeZoneDisplay);

  public boolean isShortCutsToolsDisplay();

  public void setShortCutsToolsDisplay(final boolean shortCutsToolsDisplay);

}
