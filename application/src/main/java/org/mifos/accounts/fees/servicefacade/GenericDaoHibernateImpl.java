package org.mifos.accounts.fees.servicefacade;

import java.io.Serializable;

import org.hibernate.Query;
import org.mifos.accounts.fees.servicefacade.GenericDao;
import java.util.Collection;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GenericDaoHibernateImpl<T, ID extends Serializable>
    extends HibernateDaoSupport implements GenericDao<T, ID>, QueryExecutor<T> {

    private Class<T> type;

    public GenericDaoHibernateImpl(Class<T> type) {
        this.type = type;

    }

    @Override
    public void add(T newInstance) {
        getHibernateTemplate().save(newInstance);
    }

    @Override
    public ID create(T newInstance) {
        return (ID) getHibernateTemplate().save(newInstance);
    }

    @Override
    public void delete(T persistentObject) {
        getHibernateTemplate().delete(persistentObject);
    }

    @Override
    public T getDetails(ID id) {
        return getHibernateTemplate().get(type, id);
    }

    @Override
    public List<T> getDetailsAll() {
        return getHibernateTemplate().find("from " + type.getName());
    }

    @Override
    public void saveOrUpdateAll(Collection<T> c) {
        getHibernateTemplate().saveOrUpdateAll(c);
    }

    @Override
    public void update(T transientObject) {
        getHibernateTemplate().update(transientObject);
    }

    @Override
    public List<T> execFindQuery(String qryMethodName, Object[] queryArgs) {
        final Query namedQuery = prepareQuery(qryMethodName, queryArgs);
        //System.out.println("executing named query:" + namedQuery.getQueryString());
        return namedQuery.list();
    }

    protected Query prepareQuery(String qryMethodName, Object[] queryArgs) {
        final String queryName = type.getSimpleName() + "." + qryMethodName;
        final Query namedQuery = getSession().getNamedQuery(queryName);
        String[] namedParameters = namedQuery.getNamedParameters();
        if (namedParameters.length==0) {
            if(queryArgs != null) {
                for(int i = 0; i < queryArgs.length; i++) {
                    Object arg = queryArgs[i];
                    namedQuery.setParameter(i, arg);
                }
            }
        } else {
            if(queryArgs != null) {
                for(int i = 0; i < queryArgs.length; i++) {
                    Object arg = queryArgs[i];
                    if(arg instanceof Collection) {
                        namedQuery.setParameterList(namedParameters[i], (Collection) arg);
                    } else {
                        namedQuery.setParameter(namedParameters[i], arg);
                    }
                }
            }
        }
        return namedQuery;
    }

}
