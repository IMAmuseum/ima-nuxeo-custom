package org.imamuseum.nuxeo.custom.eventService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.platform.ui.web.directory.DirectoryHelper;

/**
 * A custom event listener for newly created documents.
 */
public class IMACollectionCreationListener implements EventListener {
	
	private static final Log log = LogFactory
		.getLog(IMACollectionCreationListener.class);

	public void handleEvent(Event event) throws ClientException {

		EventContext ctx = event.getContext();

		if (ctx instanceof DocumentEventContext) {
			
			if (!event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)) return;

			DocumentEventContext docCtx = (DocumentEventContext) ctx;
			DocumentModel doc = docCtx.getSourceDocument();

			/**
			 * For new collection survey documents, create a custom title based on
			 * other metadata values.
			 */
			if (doc != null && "CollectionSurvey".equals(doc.getType()) && doc.getProperty("dublincore", "title") == null)
			{
				Session session = null;
				String[] artIds = (String[])doc.getProperty("ima_art_ids", "art_ids");
				String accNbr = "unknown";
				
				if (artIds != null && artIds.length > 0) {
					// Grab the first accession number for the title
					try {
						session = DirectoryHelper.getDirectoryService().open("ima_art_metadata_mercury_directory");
						DocumentModel entry = session.getEntry(artIds[0]);
						accNbr = (String)entry.getProperty("ima_art_metadata", "accession_number");
						
					} catch (DirectoryException e) {
						log.error("Could not connect to directory entries", e);
					} finally {
						try {
							if (session != null) {
								session.close();
							}
						} catch (DirectoryException ce) {
							log.error("Could not close directory session", ce);
						}
					}
				}
				
				String[] titleParts = new String[] {
					"survey",
					accNbr,
					ctx.getPrincipal().getName(),
				};
				doc.setPropertyValue("dublincore:title", StringUtils.join(titleParts, "_"));
			}
		}
	}
}
