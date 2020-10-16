package com.supwisdom.platform.framework.util;

/**
 * 生成Mybaits脚本名称 <br>
 * 对于自动生成工具的XML与实体BEAN修正方法 <br>
 * 1、
 * <mapper namespace="com.supwisdom.platform.generator.ModulecommonCustomcols" >
 * 此处的namespace需与bean一致 <br>
 * 2、去掉以下几个节点：updateByExample,updateByExampleSelective,insertSelective <br>
 * 4、按要求修正 Example_Where_Clause <br>
 * 5、Update_By_Example_Where_Clause删除 <br>
 * 6、每一个select,update,insertr操作中的参数需修改：把对应实体+example改成bean <br>
 * 9、添加了Example_Where_Clause查询条的参数类型用
 * com.supwisdom.platform.core.common.framework.Page
 * 
 * @author hush
 */
public interface SqlId {
    /**
     * 查询表记录总条数
     */
    public String SQL_SELECT_COUNT = "countByExample";
    /**
     * 查询表记录
     */
    public String SQL_SELECT = "selectByExample";
    /**
     * 按ID来查询一条记录
     */
    public String SQL_SELECT_BY_ID = "selectByPrimaryKey";
    /**
     * 按ID来修改一条记录
     */
    public String SQL_UPDATE_BY_ID = "updateByPrimaryKey";
    /**
     * 按查询条件来修改记录
     */
    public String SQL_UPDATE_BY_ID_SELECTIVE = "updateByPrimaryKeySelective";
    /**
     * 按条件删除记录
     */
    public String SQL_DELETE = "deleteByExample";
    /**
     * 按ID来删除一条记录
     */
    public String SQL_DELETE_BY_ID = "deleteByPrimaryKey";
    /**
     * 插入一条记录
     */
    public String SQL_INSERT = "insert";

    /**
     * 查询重复字段
     */
    public String SQL_EXISTS = "selectExistsCountByField";

    /**
     * 表备份
     */
    public String SQL_BACKUP = "backup";

    /**
     * 删除备份表
     */
    public String SQL_DROP_BACKUP = "dropBackup";
}
