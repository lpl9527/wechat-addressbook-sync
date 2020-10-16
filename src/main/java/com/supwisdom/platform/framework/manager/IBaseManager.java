package com.supwisdom.platform.framework.manager;

import com.supwisdom.platform.framework.domain.DataTablePage;

import java.util.List;


public interface IBaseManager<T> {

    public String getSqlNamespace();

    public void setSqlNamespace(String sqlNamespace);

    public T selectById(String id);

    public List<T> selectList(T entity);

    public List<T> selectList(DataTablePage page);

    public List<T> selectAll();

    public DataTablePage selectPageList(DataTablePage page);

    public DataTablePage selectPageAll(DataTablePage page);

    public Integer selectCount();

    public void insert(T entity);

    public void insertInBatch(List<T> entityList);

    public int updateById(T entity);

    public void updateInBatch(List<T> entityList);

    public int deleteById(String id);

    public void deleteByIdInBatch(String[] idList);

    public boolean checkFieldExists(String type, String value, String id);

}
