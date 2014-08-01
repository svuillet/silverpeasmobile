package com.silverpeas.mobile.client.apps.media.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtmobile.ui.client.widgets.ScrollPanel;
import com.silverpeas.mobile.client.apps.media.events.controller.LoadRemotePreviewPictureEvent;
import com.silverpeas.mobile.client.apps.media.events.pages.remote.viewer.AbstractPictureViewerPageEvent;
import com.silverpeas.mobile.client.apps.media.events.pages.remote.viewer.PictureViewLoadedEvent;
import com.silverpeas.mobile.client.apps.media.events.pages.remote.viewer.PicturesViewerPageEventHandler;
import com.silverpeas.mobile.client.apps.media.resources.GalleryMessages;
import com.silverpeas.mobile.client.apps.media.resources.GalleryResources;
import com.silverpeas.mobile.client.common.EventBus;
import com.silverpeas.mobile.client.common.app.View;
import com.silverpeas.mobile.client.components.base.PageContent;

public class PictureViewerPage extends PageContent implements View, PicturesViewerPageEventHandler {

	@UiField(provided = true) protected GalleryMessages msg = null;
	@UiField(provided = true) protected GalleryResources ressources = null;
	@UiField ScrollPanel container;
	@UiField protected Image content;
	@UiField protected Label title;
	
	private String photoId;
	private String galleryId;
	
	private static PictureViewerPageUiBinder uiBinder = GWT.create(PictureViewerPageUiBinder.class);

	interface PictureViewerPageUiBinder extends	UiBinder<Widget, PictureViewerPage> {
	}

	public PictureViewerPage() {
		ressources = GWT.create(GalleryResources.class);		
		ressources.css().ensureInjected();
		msg = GWT.create(GalleryMessages.class);
		initWidget(uiBinder.createAndBindUi(this));
		EventBus.getInstance().addHandler(AbstractPictureViewerPageEvent.TYPE, this);
	}
	
	public void init(String galleryId, String photoId) {
		this.galleryId = galleryId;
		this.photoId = photoId;
		EventBus.getInstance().fireEvent(new LoadRemotePreviewPictureEvent(galleryId, photoId));
	}
	


	@Override
	public void stop() {
		EventBus.getInstance().removeHandler(AbstractPictureViewerPageEvent.TYPE, this);		
	}

	@Override
	public void onPictureLoaded(PictureViewLoadedEvent event) {		
		content.setUrl(event.getPhoto().getDataPhoto());
		title.setText(event.getPhoto().getTitle());
		container.addStyleName(ressources.css().localPicture());
	}
	
	@UiHandler("fullScreen")
	void openImage(ClickEvent e) {
		Window.open(content.getUrl(), "_blank", "");
	}
}