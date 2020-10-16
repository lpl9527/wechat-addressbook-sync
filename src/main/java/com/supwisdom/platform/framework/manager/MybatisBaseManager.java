package com.supwisdom.platform.framework.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.supwisdom.platform.framework.domain.BaseDomain;
import com.supwisdom.platform.framework.domain.DataTablePage;
import com.supwisdom.platform.framework.exception.ManagerException;
import com.supwisdom.platform.framework.util.BeanUtils;
import com.supwisdom.platform.framework.util.SqlId;
import com.supwisdom.platform.framework.util.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public abstract class MybatisBaseManager<T extends BaseDomain> implements IBaseManager<T> {

    @Autowired(required = true)
    protected SqlSession sqlSessionTemplate;

    /**
     * 空间属性分割符
     */
    public static final String SQLNAME_SEPARATOR = ".";

    /**
     * SqlMapping命名空间
     */
    private String sqlNamespace = getDefaultSqlNamespace();

    /**
     * 获取泛型类型的实体对象类全名
     */
    protected String getDefaultSqlNamespace() {
        Class<?> genericClass = BeanUtils.getGenericClass(this.getClass());
        return genericClass == null ? null : genericClass.getName();
    }

    /**
     * 获取SqlMapping命名空间
     * 
     * @return SqlMapping命名空间
     */
    @Override
    public String getSqlNamespace() {
        return sqlNamespace;
    }

    /**
     * 设置SqlMapping命名空间。 以改变默认的SqlMapping命名空间， <br>
     * 不能滥用此方法随意改变SqlMapping命名空间。
     * 
     * @param sqlNamespace SqlMapping命名空间
     */
    @Override
    public void setSqlNamespace(String sqlNamespace) {
        this.sqlNamespace = sqlNamespace;
    }

    /**
     * 将SqlMapping命名空间与给定的SqlMapping名组合在一起。
     * 
     * @param sqlName SqlMapping名
     * @return 组合了SqlMapping命名空间后的完整SqlMapping名
     */
    protected String getSqlName(String sqlName) {
        return sqlNamespace + SQLNAME_SEPARATOR + sqlName;
    }

    /**
     * 生成主键值。 默认使用方法 如果需要生成主键，需要由子类重写此方法根据需要的方式生成主键值。
     * 
     */
    protected String generateId() {
        return UUIDUtils.create();
    }

    /**
     * 按主键查询一条记录
     * 
     * @param id
     * @return 当前实体对象
     */
    @Override
    public T selectById(String id) {
        Assert.notNull(id);
        try {
            return sqlSessionTemplate.selectOne(getSqlName(SqlId.SQL_SELECT_BY_ID), id);
        } catch (Exception e) {
            throw new ManagerException(String.format("根据ID查询对象出错！语句：%s", getSqlName(SqlId.SQL_SELECT_BY_ID)), e);
        }
    }

    @Override
    public List<T> selectList(T entity) {
        try {
            return sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT), entity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ManagerException(String.format("查询对象列表出错！语句：%s", getSqlName(SqlId.SQL_SELECT)), e);
        }
    }

    /**
     * 按条件查询全部记录
     * 
     * @return 当前实体对象集合
     */
    @Override
    public List<T> selectList(DataTablePage page) {
        try {
            return sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT), page);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ManagerException(String.format("查询对象列表出错！语句：%s", getSqlName(SqlId.SQL_SELECT)), e);
        }
    }

    /**
     * 查询全部记录
     * 
     * @return 当前实体表中的全部集合
     */
    @Override
    public List<T> selectAll() {
        try {
            return sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT));
        } catch (Exception e) {
            throw new ManagerException(String.format("查询所有对象列表出错！语句：%s", getSqlName(SqlId.SQL_SELECT)), e);
        }
    }

    /**
     * 分页按条查询查询记录
     * 
     * @param page 分页参数
     * @return 当前实体对象集合分页
     */
    @Override
    public DataTablePage selectPageList(DataTablePage page) {
        try {
            List<Object> contentList = sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT), page);
            page.setAaData(contentList);
            return page;
        } catch (Exception e) {
            throw new ManagerException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName(SqlId.SQL_SELECT)), e);
        }
    }

    /**
     * 分页无条件查询记录
     * 
     * @param page 分页参数
     * @return 当前实体对象全部集合中分页
     */
    @Override
    public DataTablePage selectPageAll(DataTablePage page) {
        try {
            page.setPageState(true);
            page.setItems(sqlSessionTemplate.selectList(getSqlName(SqlId.SQL_SELECT), page));
            return page;
        } catch (Exception e) {
            throw new ManagerException(String.format("根据分页对象查询列表出错！语句:%s", getSqlName(SqlId.SQL_SELECT)), e);
        }
    }

    /**
     * 查询总记录数
     * 
     * @return 当前实体总记录数
     */
    @Override
    public Integer selectCount() {
        try {
            return sqlSessionTemplate.selectOne(getSqlName(SqlId.SQL_SELECT_COUNT));
        } catch (Exception e) {
            throw new ManagerException(String.format("查询对象总数出错！语句：%s", getSqlName(SqlId.SQL_SELECT_COUNT)), e);
        }
    }

    /**
     * 插入一条记录
     * 
     * @param entity 插入的BEAN
     */
    @Override
    public void insert(T entity) {
        Assert.notNull(entity);
        try {
            if (StringUtils.isBlank(entity.getId()))
                entity.setId(generateId());
            sqlSessionTemplate.insert(getSqlName(SqlId.SQL_INSERT), entity);
        } catch (Exception e) {
            throw new ManagerException(String.format("添加对象出错！语句：%s", getSqlName(SqlId.SQL_INSERT)), e);
        }
    }

    /**
     * 批量添加
     * 
     * @param entityList 数据集合
     */
    @Override
    public void insertInBatch(List<T> entityList) {
        if (entityList == null || entityList.isEmpty())
            return;
        for (T entity : entityList) {
            this.insert(entity);
        }
    }

    /**
     * 对一条记录更新，利用ID 全部字段更新
     * 
     * @param entity 需要更新的记录实体
     * @return 更新记录条数
     */
    @Override
    public int updateById(T entity) {
        Assert.notNull(entity);
        try {
            return sqlSessionTemplate.update(getSqlName(SqlId.SQL_UPDATE_BY_ID), entity);
        } catch (Exception e) {
            throw new ManagerException(String.format("根据ID更新对象出错！语句：%s", getSqlName(SqlId.SQL_UPDATE_BY_ID)), e);
        }
    }


    /**
     * 批量更新（全部更新）
     * 
     * @param entityList 数据集合
     */
    @Override
    public void updateInBatch(List<T> entityList) {
        if (entityList == null || entityList.isEmpty())
            return;
        for (T entity : entityList) {
            this.updateById(entity);
        }
    }

    /**
     * 用ID来删除一条记录
     * 
     * @param id ID
     * @return 删除 记录条数
     */
    @Override
    public int deleteById(String id) {
        Assert.notNull(id);
        try {
            return sqlSessionTemplate.delete(getSqlName(SqlId.SQL_DELETE_BY_ID), id);
        } catch (Exception e) {
            throw new ManagerException(String.format("根据ID删除对象出错！语句：%s", getSqlName(SqlId.SQL_DELETE_BY_ID)), e);
        }
    }

    /**
     * 批量删除对ID
     * 
     * @param idList id集合
     */
    @Override
    public void deleteByIdInBatch(String[] idList) {
        if (idList == null || idList.length == 0)
            return;
        for (String id : idList) {
            this.deleteById(id);
        }
    }

    /**
     * 校验编号或者名称的重复性
     * 
     * @param type 校验的字段(name,num和tableName)
     * @param value 字段的值
     * @param id 校验的子集ID（更新时候的校验要去除自己本身）
     * @return true 可用 false 不可用
     */
    @Override
    public boolean checkFieldExists(String type, String value, String id) {
        Map<String, String> condtion = new HashMap<>();
        boolean result = false;
        try {
            condtion.put("type", type);
            condtion.put("value", value);
            condtion.put("id", id);
            int count = sqlSessionTemplate.selectOne(getSqlName(SqlId.SQL_EXISTS), condtion);
            if (count == 0) {
                result = true;
            }
        } catch (Exception e) {
            throw new ManagerException(String.format("校验字段重复性出错！语句:%s", getSqlName(SqlId.SQL_EXISTS)), e);
        }
        return result;
    }

}
