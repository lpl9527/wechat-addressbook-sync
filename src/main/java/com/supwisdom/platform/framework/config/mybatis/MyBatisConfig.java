package com.supwisdom.platform.framework.config.mybatis;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * MyBatis配置类
 * @author StevenChang
 *
 */
@Configuration
public class MyBatisConfig implements TransactionManagementConfigurer {

    @Resource(name="defaultDruidDataSource")
    private DataSource dataSource;

    @Value("${spring.mybatis.typeAliasesPackage}")
    private String typeAliasesPackage;
    
    @Value("${spring.mybatis.mapperLocations}")
    private String mapperLocations;
    
    @Value("${spring.mybatis.dialect}")
    private String dialect;
    
    
    @Bean(name = "sqlSessionFactory") // 3
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        //设置datasource
        bean.setDataSource(dataSource);
        //设置typeAlias 包扫描路径   
        bean.setTypeAliasesPackage(typeAliasesPackage);

        Properties properties = new Properties();
        properties.setProperty("databaseType", dialect);
//        PageInterceptor pageInterceptor = new PageInterceptor();
//        pageInterceptor.setProperties(properties);
//
//        Interceptor[] plugins = new Interceptor[] { pageInterceptor };
//        bean.setPlugins(plugins);

        // 添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            //添加mapper 扫描路径 
            bean.setMapperLocations(resolver.getResources(mapperLocations));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean(name="sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate() {
        return new SqlSessionTemplate(sqlSessionFactoryBean());
    }

    @Override
    @Bean(name="annotationDrivenTransactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

}
