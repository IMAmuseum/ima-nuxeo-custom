package org.imamuseum.nuxeo.custom.backend;

import org.nuxeo.ecm.platform.wi.backend.Backend;
import org.nuxeo.ecm.platform.wi.backend.AbstractBackendFactory;
import org.nuxeo.ecm.platform.wi.backend.BackendFactory;

public class ImaBackendFactory extends AbstractBackendFactory implements BackendFactory {

	@Override
	protected Backend createRootBackend() {
		return new ImaSearchRootBackend();
	}
}
