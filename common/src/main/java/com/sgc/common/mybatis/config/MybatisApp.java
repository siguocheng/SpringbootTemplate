package com.sgc.common.mybatis.config;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;

/**
 * Hello world!
 *
 */
public class MybatisApp 
{
    public static void main( String[] args ) throws IOException, XMLParserException, SQLException, InterruptedException, InvalidConfigurationException
    {
    	List<String> warnings = new ArrayList<String>();
    	boolean overwrite = true;
    	//自己的配置文件位置
    	File configFile = new File("./src/main/resources/generatorConfig.xml");
    	ConfigurationParser cp = new ConfigurationParser(warnings);
    	Configuration config = cp.parseConfiguration(configFile);
    	
//    	if (config != null) {
//    		
//    		List<Context> contextList = config.getContexts();
//    		
//    		for (Context context : contextList) {
//    			
//    			// 实体类
//    			JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = context.getJavaModelGeneratorConfiguration();
//    			String modelTargetPackage = javaModelGeneratorConfiguration.getTargetPackage();
//    			System.out.println("modelTargetPackage:" + modelTargetPackage);
//    			// dao类
//    			SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = context.getSqlMapGeneratorConfiguration();
//    			String daoTargetPackage = sqlMapGeneratorConfiguration.getTargetPackage();
//    			System.out.println("daoTargetPackage:" + daoTargetPackage);
//    			// sql xml文件
//    			JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = context.getJavaClientGeneratorConfiguration();
//    			String xmlTargetPackage = javaClientGeneratorConfiguration.getTargetPackage();
//    			System.out.println("xmlTargetPackage:" + xmlTargetPackage);
//    			
//    			List<TableConfiguration> tableConfigurationList = context.getTableConfigurations();
//				for (TableConfiguration tableConfiguration : tableConfigurationList) {
//					String tableName = tableConfiguration.getTableName();
//					String domainObjectName = tableConfiguration.getDomainObjectName();
//					
//					System.out.println("domainObjectName:" + domainObjectName);
//    			}
//    		}
//    	}
    	
    	DefaultShellCallback callback = new DefaultShellCallback(overwrite);
    	//mybatis-generator-core依赖 1.3.6 后才有MyBatisGenerator类,需要jre1.8才能运行
    	MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);

    	myBatisGenerator.generate(null);
    }
}
