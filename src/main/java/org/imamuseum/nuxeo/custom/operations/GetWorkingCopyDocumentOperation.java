package org.imamuseum.nuxeo.custom.operations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.DocumentModel;

@Operation(id = GetWorkingCopyDocumentOperation.ID, category = Constants.CAT_DOCUMENT, label = "Get Working Copy", description = "Get the source if the document is a proxy")
public class GetWorkingCopyDocumentOperation {
	
	public final static String ID = "Document.GetWorkingCopy";
	
	@OperationMethod(collector=DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) throws Exception {
    	// Fetch the working copy (live document) for a proxy or version
    	return doc.getCoreSession().getWorkingCopy(doc.getRef());
    }

}
