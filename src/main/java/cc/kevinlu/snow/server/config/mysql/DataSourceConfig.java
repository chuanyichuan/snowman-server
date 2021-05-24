/**
 * MIT License
 *
 * Copyright (c) 2021 chuanyichuan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cc.kevinlu.snow.server.config.mysql;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *  多数据库DataSource 配置文件信息
 *  
 *  @author
 *  @date 2019/03/21
 */
//@Configuration
//@MapperScan(basePackages = { "cc.kevinlu.snow.server.data.mapper" }, sqlSessionFactoryRef = "sqlSessionFactory")
public class DataSourceConfig {

    @Resource
    private AcDataSourceProperties acDataSourceProperties;

    @Bean(name = "dataSourceCode")
    @Primary
    public DataSource dataSourceCode() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.url(acDataSourceProperties.getUrl());
        builder.username(acDataSourceProperties.getUsername());
        builder.password(acDataSourceProperties.getPassword());
        builder.driverClassName(acDataSourceProperties.getDriverClassName());
        return builder.build();
    }

    @Bean(name = "sqlSessionFactory")
    @Autowired
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceCode") DataSource dataSourceTest)
            throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSourceTest);
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("dataSourceCode") DataSource dataSourceHatsUser)
            throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory(dataSourceHatsUser));
    }

    @Bean(name = "code_transaction")
    public PlatformTransactionManager prodTransactionManager(@Qualifier("dataSourceCode") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        dataSourceTransactionManager.setNestedTransactionAllowed(true);
        return dataSourceTransactionManager;
    }

}
