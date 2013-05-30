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
/*
 * EditNetworkNamePage.java
 *
 * Created on October 19, 2006, 4:04 PM
 * 
 */
package edu.harvard.iq.dvn.core.web.networkAdmin;

import edu.harvard.iq.dvn.core.vdc.VDCNetwork;
import edu.harvard.iq.dvn.core.vdc.VDCNetworkServiceLocal;
import edu.harvard.iq.dvn.core.web.common.StatusMessage;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import javax.ejb.EJB;
import com.icesoft.faces.component.ext.HtmlInputText;
import edu.harvard.iq.dvn.core.web.common.VDCRequestBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;

/**
 * <p>Page bean that corresponds to a similarly named JSP page.  This
 * class contains component definitions (and initialization code) for
 * all components that you have defined on this page, as well as
 * lifecycle methods and event handlers where you may add behavior
 * to respond to incoming events.</p>
 */
@ViewScoped
@Named("EditNetworkNamePage")
public class EditNetworkNamePage extends VDCBaseBean  implements java.io.Serializable {
    @EJB VDCNetworkServiceLocal vdcNetworkService;
    StatusMessage msg;
    private Boolean addMode = false;
    private String alias = "";
    
    public StatusMessage getMsg(){
        return msg;
    }
    
    public void setMsg(StatusMessage msg){
        this.msg = msg;
    }
    
    String networkName;
    public void setNetworkName(String name){
        networkName=name;
    }
    public String getNetworkName(){
        return networkName;
    }
    
    public void preRenderView(){
        String edit = getVDCRequestBean().getRequestParam("edit");
        if (edit !=null && edit.equals("false")){
            addMode = true;
            networkName = "";
        } 
            else {
            networkName = getVDCRequestBean().getCurrentVdcNetwork().getName();
        }
    }
    
    public void init() {
        super.init();
    }

    private HtmlInputText textFieldNetworkName = new HtmlInputText();

    public HtmlInputText getTextFieldNetworkName() {
        return textFieldNetworkName;
    }

    public void setTextFieldNetworkName(HtmlInputText hit) {
        this.textFieldNetworkName = hit;
    }
    


    /** 
     * <p>Construct a new Page bean instance.</p>
     */
    public EditNetworkNamePage() {
    }


    public String saveNetworkName() {
        /* Save done in options page or edit subnetwork page
         * 
         */
        return "";
    }
    
    public String cancel(){
        return "/networkAdmin/NetworkOptionsPage.xhtml?faces-redirect=true";
    }

}

