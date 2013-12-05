/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *     <a href="mailto:cmoad@imamuseum.org">Charles Moad</a>
 *
 * $Id:  $
 */

package org.imamuseum.nuxeo.custom.widgets;

import static org.jboss.seam.ScopeType.PAGE;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.platform.ui.web.component.list.UIEditableList;
import org.nuxeo.ecm.platform.ui.web.directory.DirectoryHelper;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

/**
 * Methods to get accession number suggestions from searches.
 */
@Name("artworksSuggestionActions")
@Scope(PAGE)
public class ArtworksSuggestionActionsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory
			.getLog(ArtworksSuggestionActionsBean.class);
	
	@In(create = true, required = false)
	protected transient FacesMessages facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor resourcesAccessor;

	/**
	 * Id of the editable list component where selection ids are put.
	 * 
	 * Component must be an instance of {@link UIEditableList}
	 */
	@RequestParameter
	protected String suggestionSelectionListId;

	public List<Map<String, Object>> getSuggestions(Object input) throws ClientException {

		String searchPattern = (String) input;
		List<Map<String, Object>> accNbrs = new LinkedList<Map<String, Object>>();
		if (searchPattern == null || searchPattern.length() == 0) {
			return accNbrs;
		}
		
		log.debug("artworksSuggestionActions.getSuggestions(" + searchPattern + ")");

		Map<String, Serializable> filter = new HashMap<String, Serializable>();
		Map<String, String> orderBy = new HashMap<String, String>();
		Set<String> fullText = new HashSet<String>();
		filter.put("accession_number", searchPattern);
		orderBy.put("accession_number", "ASC");
		Session session = null;
		List<DocumentModel> list = Collections.emptyList();
		
		try {
			session = DirectoryHelper.getDirectoryService().open("ima_art_metadata_mercury_directory");
			// NOTE: query limit set in SQLDirectoryFactory:directories extension point
			list = session.query(filter, fullText, orderBy);
			log.debug("artworksSuggestionActions.getSuggestions: Found " + list.size() + " matches.");
			
		} catch (DirectoryException e) {
			log.error("Could not connect to directory entries", e);
		} catch (ClientException ce) {
			log.error("Could not query directory mercury", ce);
		} finally {
			try {
				if (session != null) {
					session.close();
				}
			} catch (DirectoryException ce) {
				log.error("Could not close directory session", ce);
			}
		}
		
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i).getDataModel("ima_art_metadata").getMap();

			// The widget expects "id" as the id field
			map.put("id", ((Long)map.get("art_id")).toString());
			accNbrs.add(map);
		}
		
		return accNbrs;
	}

	public Map<String, Object> getArtworkInfo(String id) throws ClientException {
		
		Map<String, Object> res = new HashMap<String, Object>();
		Session session = null;
		
		if (id.isEmpty()) {
			log.error("Empty id passed to artworksSuggestionActions.getArtworkInfo");
			return res;
		}

		log.debug("artworksSuggestionActions.getArtworkInfo(" + id + ")");
		
		try {
			session = DirectoryHelper.getDirectoryService().open("ima_art_metadata_mercury_directory");
			DocumentModel entry = session.getEntry(id);
			res = entry.getDataModel("ima_art_metadata").getMap();
			
			// The widget expects "id" as the id field
			res.put("id", id);
			
			// Add the full url of the thumbnail image and medium image
			String imageId = (String)res.get("image_id");
			if (imageId != null && imageId.length() == 36) {
				String[] thumbnail_url_parts = {
					"https://project.imamuseum.org/ima-collections-proxy/ima-collections/images",
					imageId.substring(0, 2),
					imageId.substring(2, 4),
					imageId,
					imageId + "_o.jpg",
				};
				res.put("thumbnail_url", StringUtils.join(thumbnail_url_parts, "/"));
				
				String[] medium_url_parts = {
					"https://project.imamuseum.org/ima-collections-proxy/ima-collections/images",
					imageId.substring(0, 2),
					imageId.substring(2, 4),
					imageId,
					imageId + "_o.jpg",
				};
				res.put("medium_url", StringUtils.join(medium_url_parts, "/"));
			}
			
			// Add the link to the Athena page for this work
			res.put("link_url", "https://project.imamuseum.org/mercury/art/" + id);
		
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
		
		return res;
	}
	
	public String nl2br(String input) {
		return input.replaceAll("\\n", "<br/>");
	}
}
