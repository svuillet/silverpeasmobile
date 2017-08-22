package com.silverpeas.mobile.server.services;

import com.silverpeas.mobile.server.common.SpMobileLogModule;
import com.silverpeas.mobile.shared.dto.BaseDTO;
import com.silverpeas.mobile.shared.dto.documents.AttachmentDTO;
import com.silverpeas.mobile.shared.dto.documents.PublicationDTO;
import com.silverpeas.mobile.shared.dto.documents.TopicDTO;
import com.silverpeas.mobile.shared.exceptions.AuthenticationException;
import com.silverpeas.mobile.shared.exceptions.DocumentsException;
import com.silverpeas.mobile.shared.services.ServiceDocuments;
import org.silverpeas.components.kmelia.model.KmeliaPublication;
import org.silverpeas.components.kmelia.service.KmeliaService;
import org.silverpeas.core.admin.ObjectType;
import org.silverpeas.core.admin.component.model.ComponentInstLight;
import org.silverpeas.core.admin.service.Administration;
import org.silverpeas.core.admin.service.OrganizationController;
import org.silverpeas.core.comment.service.CommentServiceProvider;
import org.silverpeas.core.contribution.attachment.AttachmentServiceProvider;
import org.silverpeas.core.contribution.attachment.model.DocumentType;
import org.silverpeas.core.contribution.attachment.model.SimpleDocument;
import org.silverpeas.core.contribution.attachment.model.SimpleDocumentPK;
import org.silverpeas.core.contribution.publication.model.PublicationDetail;
import org.silverpeas.core.contribution.publication.model.PublicationPK;
import org.silverpeas.core.contribution.publication.service.PublicationService;
import org.silverpeas.core.node.model.NodeDetail;
import org.silverpeas.core.node.model.NodePK;
import org.silverpeas.core.node.service.NodeService;
import org.silverpeas.core.util.LocalizationBundle;
import org.silverpeas.core.util.ResourceLocator;
import org.silverpeas.core.util.StringUtil;
import org.silverpeas.core.util.logging.SilverLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service de gestion des GED.
 * @author svuillet
 */
public class ServiceDocumentsImpl extends AbstractAuthenticateService implements ServiceDocuments {

  private static final long serialVersionUID = 1L;
  private OrganizationController organizationController = OrganizationController.get();

  /**
   * Retourne tous les topics de premier niveau d'un topic.
   */
  @Override
  public List<TopicDTO> getTopics(String instanceId, String rootTopicId) throws DocumentsException, AuthenticationException {
    checkUserInSession();
    List<TopicDTO> topicsList = new ArrayList<TopicDTO>();
    boolean coWriting = false;
    try {
      coWriting = isCoWritingEnabled(instanceId);
    } catch (Exception e) { }
    try {
      if (rootTopicId == null || rootTopicId.isEmpty()) {
        rootTopicId = "0";
      }
      NodePK pk = new NodePK(rootTopicId, instanceId);
      NodeDetail rootNode = getNodeBm().getDetail(pk);
      TopicDTO rootTopic = new TopicDTO();
      rootTopic.setRoot(true);
      if (rootNode.hasFather()) {
        rootTopic.setName(rootNode.getName());
      } else {
        ComponentInstLight app = Administration.get().getComponentInstLight(instanceId);
        rootTopic.setName(app.getLabel());
      }
      topicsList.add(rootTopic);
      ArrayList<NodeDetail> nodes = getNodeBm().getSubTreeByLevel(pk, rootNode.getLevel() + 1);
      TopicDTO trash = null;
      for (NodeDetail nodeDetail : nodes) {
        if (rootTopicId.equals(nodeDetail.getFatherPK().getId())) {
          TopicDTO topic = new TopicDTO();
          if (nodeDetail.getId() != 2) {

            if (isCurrentTopicAvailable(nodeDetail)) {
              topic.setId(String.valueOf(nodeDetail.getId()));
              topic.setName(nodeDetail.getName());
              int childrenNumber = getNodeBm()
                  .getChildrenNumber(new NodePK(String.valueOf(nodeDetail.getId()), instanceId));

              // count publications
              Collection<NodePK> pks = getAllSubNodePKs(nodeDetail.getNodePK());
              List<String> ids = new ArrayList<String>();
              ids.add(String.valueOf(nodeDetail.getId()));
              if (isRightsOnTopicsEnabled(instanceId)) {
                for (NodePK onePk : pks) {
                  NodeDetail oneNode = getNodeBm().getDetail(onePk);
                  if (isCurrentTopicAvailable(oneNode)) {
                    ids.add(onePk.getId());
                  }
                }
              } else {
                for (NodePK onePk : pks) {
                  NodeDetail oneNode = getNodeBm().getDetail(onePk);
                  ids.add(onePk.getId());
                }
              }
              PublicationPK pubPK = new PublicationPK("useless", instanceId);
              List<PublicationDetail> publications = (List<PublicationDetail>) getPubBm().getDetailsByFatherIds(ids, pubPK, "pubname");
              int nbPubNotVisible = 0;
              for (PublicationDetail publication : publications) {
                if (coWriting) {
                  if (isRightsOnTopicsEnabled(instanceId)) {
                    NodePK f = getKmeliaBm().getPublicationFatherPK(publication.getPK(), true,
                        getUserInSession().getId(), isRightsOnTopicsEnabled(instanceId));
                    String[] profiles = organizationController
                        .getUserProfiles(getUserInSession().getId(), instanceId, Integer.valueOf
                            (f.getId()), ObjectType.NODE);
                    if (isSingleReader(profiles) && publication.isDraft()) {
                      nbPubNotVisible++;
                    }
                  } else {
                    String [] profiles = organizationController.getUserProfiles(getUserInSession().getId(), instanceId);
                    if (isSingleReader(profiles) && publication.isDraft()) {
                      nbPubNotVisible++;
                    }
                  }
                } else {
                  if (publication.isDraft() && !publication.getUpdaterId().equals(getUserInSession().getId())) {
                    nbPubNotVisible++;
                  }
                }
              }
              topic.setPubCount(publications.size() - nbPubNotVisible);


              topic.setTerminal(childrenNumber == 0);
              if (nodeDetail.getId() ==1) {
                trash = topic;
              } else {
                topicsList.add(topic);
              }
            }
          }
        }
      }
      if (trash != null) topicsList.add(0, trash);
    } catch (Exception e) {
      SilverLogger.getLogger(SpMobileLogModule.getName()).error("ServiceDocumentsImpl.getTopics", "root.EX_NO_MESSAGE", e);
      throw new DocumentsException(e.getMessage());
    }
    return topicsList;
  }

  private boolean isSingleReader(final String[] profiles) {
    for (String profile : profiles) {
      if (profile.equals("admin")) return false;
      if (profile.equals("publisher")) return false;
      if (profile.equals("writer")) return false;
    }
    return true;
  }

  private Collection<NodePK> getAllSubNodePKs(final NodePK pk) throws Exception {
    CopyOnWriteArrayList<NodePK> subNodes = new CopyOnWriteArrayList<NodePK>();
    if (pk != null) {
      subNodes.addAll(getNodeBm().getChildrenPKs(pk));
      for (NodePK subNode : subNodes) {
        subNodes.addAll(getAllSubNodePKs(subNode));
      }
    }
    return subNodes;
  }

  /**
   * Retourne les publications d'un topic (au niveau 1).
   */
  @Override
  public List<PublicationDTO> getPublications(String instanceId, String topicId) throws DocumentsException, AuthenticationException {
    checkUserInSession();
    LocalizationBundle resource = ResourceLocator
        .getLocalizationBundle("org.silverpeas.mobile.multilang.mobileBundle", getUserInSession().getUserPreferences().getLanguage());
    ArrayList<PublicationDTO> pubs = new ArrayList<PublicationDTO>();

    try {
      if (topicId == null || topicId.isEmpty()) {
        topicId = "0";
      }
      NodePK nodePK = new NodePK(topicId, instanceId);
      PublicationPK pubPK = new PublicationPK("useless", instanceId);
      String status = "Valid";
      ArrayList<String> nodeIds = new ArrayList<String>();
      nodeIds.add(nodePK.getId());

      List<PublicationDetail> publications = (List<PublicationDetail>) getPubBm().getDetailsByFatherIds(nodeIds, pubPK, "pubname");
      for (PublicationDetail publicationDetail : publications) {

        PublicationDTO dto = new PublicationDTO();
        dto.setId(publicationDetail.getId());
        if (publicationDetail.isDraft()) {
          if (publicationDetail.getUpdaterId().equals(getUserInSession().getId())) {
            dto.setName(publicationDetail.getName() + " (" + resource.getString("publication.draft") + ")");
          }
        } else if (publicationDetail.getStatus().equals("Valid")) {
          dto.setName(publicationDetail.getName());
        }
        pubs.add(dto);
      }
    } catch (Exception e) {
      SilverLogger.getLogger(SpMobileLogModule.getName()).error("ServiceDocumentsImpl.getPublications", "root.EX_NO_MESSAGE", e);
      throw new DocumentsException(e.getMessage());
    }

    return pubs;
  }

  private boolean isCurrentTopicAvailable(NodeDetail node) throws Exception {
    if (isRightsOnTopicsEnabled(node.getNodePK().getInstanceId())) {
      if (node.haveRights()) {
        int rightsDependsOn = node.getRightsDependsOn();
        return organizationController.isObjectAvailable(rightsDependsOn, ObjectType.NODE, node.getNodePK().getInstanceId(), getUserInSession().getId());
      }
    }
    return true;
  }

  private boolean isRightsOnTopicsEnabled(String instanceId) throws Exception {
    String value = getMainSessionController().getComponentParameterValue(instanceId, "rightsOnTopics");
    return StringUtil.getBooleanValue(value);
  }

  private boolean isCoWritingEnabled(String instanceId) throws Exception {
    String value = getMainSessionController().getComponentParameterValue(instanceId, "coWriting");
    return StringUtil.getBooleanValue(value);
  }

  private PublicationService getPubBm() {
   return PublicationService.get();
  }

  private KmeliaService getKmeliaBm() {
    return KmeliaService.get();
  }

  private NodeService getNodeBm() {
    return NodeService.get();
  }

  @Override
  public PublicationDTO getPublication(String pubId) throws DocumentsException, AuthenticationException {
    SilverLogger.getLogger(SpMobileLogModule.getName()).debug("ServiceDocumentsImpl.getPublication", "getPublication for id " + pubId);
    checkUserInSession();
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

      PublicationDetail pub = getPubBm().getDetail(new PublicationPK(pubId));

      PublicationDTO dto = new PublicationDTO();
      dto.setId(pub.getId());
      dto.setName(pub.getName());
      dto.setCreator(pub.getCreator().getDisplayedName() + " " + sdf.format(pub.getCreationDate()));
      dto.setUpdater(organizationController.getUserDetail(pub.getUpdaterId()).getDisplayedName());
      dto.setVersion(pub.getVersion());
      dto.setDescription(pub.getDescription());
      dto.setUpdateDate(sdf.format(pub.getUpdateDate()));
      dto.setCommentsNumber(CommentServiceProvider.getCommentService().getCommentsCountOnPublication("Publication", new PublicationPK(pubId)));
      dto.setInstanceId(pub.getInstanceId());
      if (pub.getWysiwyg() == null|| !pub.getWysiwyg().trim().isEmpty() || !pub.getInfoId().equals("0")) {
        dto.setContent(true);
      }

      ArrayList<AttachmentDTO> attachments = new ArrayList<AttachmentDTO>();
      SilverLogger.getLogger(SpMobileLogModule.getName()).debug("ServiceDocumentsImpl.getPublication", "Get attachments");

      List<SimpleDocument> pubAttachments = AttachmentServiceProvider.getAttachmentService().listDocumentsByForeignKeyAndType(pub.getPK(), DocumentType.attachment, getUserInSession().getUserPreferences().getLanguage());

      SilverLogger.getLogger(SpMobileLogModule.getName()).debug("ServiceDocumentsImpl.getPublication", "Attachments number=" + pubAttachments.size());

      for (SimpleDocument attachment : pubAttachments) {
        attachments.add(populate(attachment));
      }
      dto.setAttachments(attachments);

      return dto;
    } catch (Throwable e) {
      SilverLogger.getLogger(SpMobileLogModule.getName()).error("ServiceDocumentsImpl.getPublication", "root.EX_NO_MESSAGE", e);
      throw new DocumentsException(e.getMessage());
    }
  }

  @Override
  public AttachmentDTO getAttachment(String attachmentId, String appId) throws DocumentsException, AuthenticationException {
    SimpleDocumentPK pk = new SimpleDocumentPK(attachmentId, appId);
    SimpleDocument doc = AttachmentServiceProvider.getAttachmentService().searchDocumentById(pk, getUserInSession().getUserPreferences().getLanguage());
    return populate(doc);
  }

  @Override
  public List<BaseDTO> getTopicsAndPublications(String instanceId, String rootTopicId) throws DocumentsException, AuthenticationException {
    checkUserInSession();
    ArrayList<BaseDTO> list = new ArrayList<BaseDTO>();
    list.addAll(getTopics(instanceId, rootTopicId));
    list.addAll(getPublications(instanceId, rootTopicId));
    return list;
  }

  private AttachmentDTO populate(SimpleDocument attachment) {
    AttachmentDTO attach = new AttachmentDTO();
    attach.setTitle(attachment.getTitle());
    if (attachment.getTitle() == null || attachment.getTitle().isEmpty()) {
      attach.setTitle(attachment.getFilename());
    }
    attach.setInstanceId(attachment.getInstanceId());
    attach.setId(attachment.getId());
    attach.setLang(attachment.getLanguage());
    attach.setUserId(getUserInSession().getId());
    attach.setType(attachment.getContentType());
    attach.setAuthor(attachment.getCreatedBy());
    attach.setOrderNum(attachment.getOrder());
    attach.setSize(attachment.getSize());
    attach.setCreationDate(attachment.getCreated());
    return attach;
  }
}
