package org.imamuseum.nuxeo.custom.backend;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.platform.wi.backend.Backend;
import org.nuxeo.ecm.platform.wi.backend.SearchVirtualBackend;
import org.nuxeo.ecm.platform.wi.backend.SimpleRealBackendFactory;

public class ImaSearchRootBackend extends SearchVirtualBackend {
	
    // For our installation we only want the AssetLibrary, Workspaces, and UserWorkspaces as the top-level WebDav folders
	private static final String QUERY = "select * from Document where ecm:primaryType IN ('ImportRoot','WorkspaceRoot','UserWorkspacesRoot') "
            + "AND ecm:currentLifeCycleState != 'deleted' AND ecm:isProxy = 0 order by ecm:path";

    public ImaSearchRootBackend() {
        super("", "", QUERY, new SimpleRealBackendFactory());
    }

    @Override
    public Backend getBackend(String uri) {
        if (StringUtils.isEmpty(uri) || "/".equals(uri)) {
            return this;
        } else {
            return super.getBackend(uri);
        }

    }

    @Override
    public boolean isRoot() {
        return true;
    }
}
