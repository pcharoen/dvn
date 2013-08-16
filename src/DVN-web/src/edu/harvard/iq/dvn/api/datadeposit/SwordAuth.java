/*
 Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Dataverse Network - A web application to share, preserve and analyze research data.
 Developed at the Institute for Quantitative Social Science, Harvard University.
 Version 3.0.
 */
package edu.harvard.iq.dvn.api.datadeposit;

import edu.harvard.iq.dvn.core.admin.UserServiceLocal;
import edu.harvard.iq.dvn.core.admin.VDCUser;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.vdc.VDCServiceLocal;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

public class SwordAuth {

    private static final Logger logger = Logger.getLogger(SwordAuth.class.getCanonicalName());
    @EJB
    UserServiceLocal userService;
    @EJB
    VDCServiceLocal vdcService;

    public VDCUser auth(AuthCredentials authCredentials) throws SwordAuthException, SwordServerException {
        if (authCredentials != null) {
            String username = authCredentials.getUsername();
            String password = authCredentials.getPassword();
            logger.fine("Checking username " + username + " ...");

            VDCUser vdcUser = userService.findByUserName(username);
            if (vdcUser != null) {
                if (userService.validatePassword(vdcUser.getId(), password)) {
                    return vdcUser;
                } else {
                    logger.info("wrong password");
                    throw new SwordAuthException();
                }
            } else {
                logger.info("could not find username: " + username);
                throw new SwordAuthException();
            }
        } else {
            // it seems this is never reached... eaten somewhere by way of ServiceDocumentServletDefault -> ServiceDocumentAPI -> SwordAPIEndpoint
            logger.info("no credentials provided");
            throw new SwordAuthException();
        }
    }

    boolean hasAccessToModifyDataverse(VDCUser vdcUser, VDC dv) throws SwordError {
        boolean authorized = false;
        String role = vdcUser.getVDCRole(dv).getRole().getName();
        if ("admin".equals(role)) {
            authorized = true;
        } else {
            authorized = false;
        }

        if (!authorized) {
            throw new SwordError("User " + vdcUser.getUserName() + " with role of " + role + " is not authorized to modify dataverse " + dv.getAlias());
        } else {
            return authorized;
        }
    }
}
