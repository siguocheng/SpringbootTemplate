/**
 * 名称: DatabaseConfig.java
 * 描述:
 *
 * @author shengling.guan
 * date: 2018年9月27日 下午2:01:43
 */
package com.sgc.common.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * @ClassName: DatabaseConfig
 * @Description:
 * @author: shengling.guan date: 2018年9月27日 下午2:01:43
 */
@Configuration
@PropertySource({ "classpath:database.properties" })
public class DatabaseConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);

	@Value("${jdbc.driverClass}")
	private String driverClass;

	@Value("${jdbc.url}")
	private String url;

	@Value("${jdbc.username}")
	private String username;

	@Value("${jdbc.password}")
	private String password;

	@Value("${jdbc.maxActive}")
	private Integer maxActive;
	
	@Value("${jdbc.initialSize}")
	private Integer initialSize;
	
	@Value("${jdbc.minIdle}")
	private Integer minIdle;
	
	@Bean
	public DruidDataSource getDataSource() {
		DruidDataSource datasource = new DruidDataSource();
		
		// 读取环境变量里的配置
		String localConfigDBUrl = System.getenv("jdbc.url");
		String localConfigDBUsername = System.getenv("jdbc.username");
		String localConfigDBUrlPassword = System.getenv("jdbc.password");
		if (localConfigDBUrl != null) {
			url = localConfigDBUrl;
		}
		if (localConfigDBUsername != null) {
			username = localConfigDBUsername;
		}
		if (localConfigDBUrlPassword != null) {
			password = localConfigDBUrlPassword;
		}
		LOGGER.info("url:" + url);
		LOGGER.info("username:" + username);
		datasource.setUrl(url);
		datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClass);
        datasource.setMaxActive(maxActive);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
		return datasource;
	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		
		// 多个路径 classpath*:mapper/*.xml,classpath*:com/urthink/upfs/**/*Mapper.xml
//		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
//				.getResources("classpath*:com/sgc/servicehi/dao/mapper/**.xml"));
		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
				.getResources("classpath*:com/sgc/**/**.xml"));
		
		return sessionFactory.getObject();
	}

	@Bean
	public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
