/*
 * VDCGroupPage.java
 *
 * Created on June 20, 2007, 2:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.web.networkAdmin;

import edu.harvard.hmdc.vdcnet.vdc.VDCGroup;
import edu.harvard.hmdc.vdcnet.vdc.VDCGroupServiceLocal;
import edu.harvard.hmdc.vdcnet.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

/**
 *
 * @author wbossons
 */
public class VDCGroupPage extends VDCBaseBean {
     @EJB VDCGroupServiceLocal vdcGroupService;
     
    /** Creates a new instance of VDCGroupPage */
    public VDCGroupPage() {
    }

    private VDCGroup VDCGroup;

    private DataModel model;

    @Resource
    private UserTransaction utx;

    @PersistenceUnit(unitName = "DVN-webPU")
    private EntityManagerFactory emf;

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    private int batchSize = 20;

    private int firstItem = 0;

    public void init() {
        super.init();
        
    }
    
    public VDCGroup getVDCGroup() {
        return VDCGroup;
    }

    public void setVDCGroup(VDCGroup VDCGroup) {
        this.VDCGroup = VDCGroup;
    }

    public DataModel getDetailVDCGroups() {
        return model;
    }

    public void setDetailVDCGroups(Collection<VDCGroup> m) {
        model = new ListDataModel(new ArrayList(m));
    }

    public String createSetup() {
        this.VDCGroup = new VDCGroup();
        return "VDCGroup_create";
    }

    public String create() {
        EntityManager em = getEntityManager();
        try {
            utx.begin();
            em.joinTransaction();
            em.persist(VDCGroup);
            utx.commit();
            addSuccessMessage("VDCGroup was successfully created.");
        } catch (Exception ex) {
            try {
                addErrorMessage(ex.getLocalizedMessage());
                utx.rollback();
            } catch (Exception e) {
                addErrorMessage(e.getLocalizedMessage());
            }
        } finally {
            em.close();
        }
        return "VDCGroup_list";
    }

    public String detailSetup() {
        setVDCGroupFromRequestParam();
        return "VDCGroup_detail";
    }

    public String editSetup() {
        setVDCGroupFromRequestParam();
        return "VDCGroup_edit";
    }

    public String deleteGroup() {
        EntityManager em = getEntityManager();
        try {
            utx.begin();
            em.joinTransaction();
            VDCGroup VDCGroup = getVDCGroupFromRequestParam();
            VDCGroup = em.merge(VDCGroup);
            em.remove(VDCGroup);
            utx.commit();
            addSuccessMessage("VDCGroup was successfully deleted.");
        } catch (Exception ex) {
            try {
                addErrorMessage(ex.getLocalizedMessage());
                utx.rollback();
            } catch (Exception e) {
                addErrorMessage(e.getLocalizedMessage());
            }
        } finally {
            em.close();
        }
        return "success";
    }
    
    /** saveOrder
     *
     * a method to save the ordering of 
     * the vdc groups
     *
     * @return success a string to be used for navigation.xml
     *
     * @author wbossons
     *
     */
    
    public String saveOrder() {
        //add some code to save the order
        return "success";
    }
    
    /** addGroup
     *
     * a method to add more vdc groups
     *
     * @return success a string to be used for navigation.xml
     *
     * @author wbossons
     *
     */
    
    public String addGroup() {
        //add some code to save the order
        return "success";
    }
    
    public VDCGroup getVDCGroupFromRequestParam() {
        EntityManager em = getEntityManager();
        try{
            VDCGroup o = (VDCGroup) model.getRowData();
            o = em.merge(o);
            return o;
        } finally {
            em.close();
        }
    }

    public void setVDCGroupFromRequestParam() {
        VDCGroup VDCGroup = getVDCGroupFromRequestParam();
        setVDCGroup(VDCGroup);
    }

    public DataModel getVDCGroups() {
        model = null;
        try {
            model = new ListDataModel((List)vdcGroupService.findAll());
        } catch (Exception e) {
            addErrorMessage(e.getCause().toString());
        } finally {
            return model;
        }
    }

    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, facesMsg);
    }

    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage("successInfo", facesMsg);
    }

    public VDCGroup findVDCGroup(Long id) {
        EntityManager em = getEntityManager();
        try{
            VDCGroup o = (VDCGroup) em.find(VDCGroup.class, id);
            return o;
        } finally {
            em.close();
        }
    }

    public int getItemCount() {
        EntityManager em = getEntityManager();
        try{
            int count = ((Long) em.createQuery("select count(o) from VDCGroup as o").getSingleResult()).intValue();
            return count;
        } finally {
            em.close();
        }
    }

    public int getFirstItem() {
        return firstItem;
    }

    public int getLastItem() {
        int size = getItemCount();
        return firstItem + batchSize > size ? size : firstItem + batchSize;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public String next() {
        if (firstItem + batchSize < getItemCount()) {
            firstItem += batchSize;
        }
        return "VDCGroup_list";
    }

    public String prev() {
        firstItem -= batchSize;
        if (firstItem < 0) {
            firstItem = 0;
        }
        return "VDCGroup_list";
    }
    
}
