/*
 * VariableServiceBean.java
 *
 * Created on November 8, 2006, 3:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author gdurand
 */
@Stateless
public class VariableServiceBean implements edu.harvard.hmdc.vdcnet.study.VariableServiceLocal {
    
    @PersistenceContext(unitName="VDCNet-ejbPU")
    private EntityManager em;
    
    /** Creates a new instance of VariableServiceBean */
    public VariableServiceBean() {
    }
    
    public DataTable getDataTable(Long dtId) {
        DataTable dt = em.find(DataTable.class,dtId);
        if (dt==null) {
            throw new IllegalArgumentException("Unknown dtId: "+dtId);
        }
        
        
        return dt;
    }
    
    public DataVariable getDataVariable(Long dvId) {
        DataVariable dv = em.find(DataVariable.class,dvId);
        if (dv==null) {
            throw new IllegalArgumentException("Unknown dvId: "+dvId);
        }
        
        
        return dv;
    }
    
    public List getDataVariablesByFileOrder(Long dtId) {
        String queryStr = "SELECT dv FROM DataTable dt JOIN dt.dataVariables dv where dt.id = " + dtId +" ORDER BY dv.fileOrder";
        Query query =em.createQuery(queryStr);
        List <DataVariable> dvs = query.getResultList();
        
        return dvs;        
    }
    
    public SummaryStatisticType findSummaryStatisticTypeByName(String name) {
        String query="SELECT t from SummaryStatisticType t where t.name = '"+name+"'";
        SummaryStatisticType type = null;
        try {
            type=(SummaryStatisticType)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return type;
    }
    
    
  public List<SummaryStatisticType> findAllSummaryStatisticType() {
        String query="SELECT t from SummaryStatisticType t ";
        return em.createQuery(query).getResultList();
          
    }
    
    /**
     * Find type from prefetched list
     * TODO: find a place to put the global list
     */
    public SummaryStatisticType findSummaryStatisticTypeByName(List<SummaryStatisticType> typeList,String name) {
       SummaryStatisticType type = null;
       for (Iterator<SummaryStatisticType> it = typeList.iterator(); it.hasNext();) {
           SummaryStatisticType elem = it.next();
           if (elem.getName().equals(name)) {
               type=elem;
               break;
           }
       }
       return type;
    }      
        
    
 public List<VariableRangeType> findAllVariableRangeType() {
        String query="SELECT t from VariableRangeType t ";
        return em.createQuery(query).getResultList();
          
    }
    
    /**
     * Find type from prefetched list
     * TODO: find a place to put the global list
     */
    public VariableRangeType findVariableRangeTypeByName(List<VariableRangeType> typeList,String name) {
       VariableRangeType type = null;
       for (Iterator<VariableRangeType> it = typeList.iterator(); it.hasNext();) {
           VariableRangeType elem = it.next();
           if (elem.getName().equals(name)) {
               type=elem;
               break;
           }
       }
       return type;
    }          
    
    public VariableFormatType findVariableFormatTypeByName(String name) {
        String query="SELECT t from VariableFormatType t where t.name = '"+name+"'";
        VariableFormatType type = null;
        try {
            type=(VariableFormatType)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return type;
    }
    
    public List<VariableFormatType> findAllVariableFormatType() {
        String query="SELECT t from VariableFormatType t ";
        return em.createQuery(query).getResultList();
          
    }
    
    /**
     * Find type from prefetched list
     * TODO: find a place to put the global list
     */
    public VariableFormatType findVariableFormatTypeByName(List<VariableFormatType> typeList,String name) {
       VariableFormatType type = null;
       for (Iterator<VariableFormatType> it = typeList.iterator(); it.hasNext();) {
           VariableFormatType elem = it.next();
           if (elem.getName().equals(name)) {
               type=elem;
               break;
           }
       }
       return type;
    }   
    
  public List<VariableIntervalType> findAllVariableIntervalType() {
        String query="SELECT t from VariableIntervalType t ";
        return em.createQuery(query).getResultList();
          
    }
    
    /**
     * Find type from prefetched list
     * TODO: find a place to put the global list
     */
    public VariableIntervalType findVariableIntervalTypeByName(List<VariableIntervalType> typeList,String name) {
       VariableIntervalType type = null;
       for (Iterator<VariableIntervalType> it = typeList.iterator(); it.hasNext();) {
           VariableIntervalType elem = it.next();
           if (elem.getName().equals(name)) {
               type=elem;
               break;
           }
       }
       return type;
    }      
    
    
    public VariableIntervalType findVariableIntervalTypeByName(String name) {
        String query="SELECT t from VariableIntervalType t where t.name = '"+name+"'";
        VariableIntervalType type = null;
        try {
            type=(VariableIntervalType)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return type;
    }
    
    public VariableRangeType findVariableRangeTypeByName(String name) {
        String query="SELECT t from VariableRangeType t where t.name = '"+name+"'";
        VariableRangeType type = null;
        try {
            type=(VariableRangeType)em.createQuery(query).getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            // DO nothing, just return null.
        }
        return type;
    }
    
        public void determineStudiesFromVariables(List variables, List studies, Map variableMap) {
        Iterator iter = variables.iterator();
        while (iter.hasNext()) {
            Long dvId = (Long) iter.next();
            DataVariable dv = null;
            
            try {                    
                dv = getDataVariable(dvId);
            } catch (IllegalArgumentException ex) {
                System.out.println("Data variable (ID=" + dvId + ") was found in index, but is not in DB.");
            }
            
            if (dv != null) {
                Long studyId = dv.getDataTable().getStudyFile().getFileCategory().getStudy().getId();
                if ( studies.contains(studyId) ) {
                    List dvList = (List) variableMap.get(studyId);
                    dvList.add(dv);
                    variableMap.put(studyId, dvList);

                } else {
                    studies.add( studyId );
                    List dvList = new ArrayList();
                    dvList.add(dv);
                    variableMap.put(studyId, dvList);
                }
            }
            
        }
    }
}
