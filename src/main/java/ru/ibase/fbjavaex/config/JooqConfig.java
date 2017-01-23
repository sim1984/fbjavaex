/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibase.fbjavaex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultExecuteListenerProvider;

import ru.ibase.fbjavaex.exception.ExceptionTranslator;

import ru.ibase.fbjavaex.managers.*;
import ru.ibase.fbjavaex.jqgrid.*;


@Configuration
public class JooqConfig {


    /**
     * Возвращает пул коннектов
     *
     * @return javax.sql.DataSource
     */
    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:firebirdsql://localhost:3050/examples");
        dataSource.setDriverClassName("org.firebirdsql.jdbc.FBDriver");
        dataSource.setUsername("SYSDBA");
        dataSource.setPassword("masterkey");
        return dataSource;
    }

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Bean(name = "transactionAwareDataSource")
    public TransactionAwareDataSourceProxy getTransactionAwareDataSource() {
        return new TransactionAwareDataSourceProxy(getDataSource());
    }

    @Bean(name = "connectionProvider")
    public DataSourceConnectionProvider getConnectionProvider() {
        return new DataSourceConnectionProvider(getTransactionAwareDataSource());
    }

    @Bean(name = "exceptionTranslator")
    public ExceptionTranslator getExceptionTranslator() {
        return new ExceptionTranslator();
    }

    /**
     * Возвращает конфгурацию DSL контекста
     *
     * @return org.jooq.Configuration
     */
    @Bean(name = "dslConfig")
    public org.jooq.Configuration getDslConfig() {
        DefaultConfiguration config = new DefaultConfiguration();
        config.setSQLDialect(SQLDialect.FIREBIRD);
        config.setConnectionProvider(getConnectionProvider());
        DefaultExecuteListenerProvider listenerProvider = new DefaultExecuteListenerProvider(getExceptionTranslator());
        config.setExecuteListenerProvider(listenerProvider);
        return config;
    }

    /**
     * Возвращает DSL котекст
     *
     * @return org.jooq.DSLContext
     */
    @Bean(name = "dsl")
    public DSLContext getDsl() {
        org.jooq.Configuration config = this.getDslConfig();
        return new DefaultDSLContext(config);
    }

    @Bean(name = "customerManager")
    public CustomerManager getCustomerManager() {
        return new CustomerManager();
    }

    @Bean(name = "customerGrid")
    public JqGridCustomer getCustomerGrid() {
        return new JqGridCustomer();
    }

    @Bean(name = "productManager")
    public ProductManager getProductManager() {
        return new ProductManager();
    }

    @Bean(name = "productGrid")
    public JqGridProduct getProductGrid() {
        return new JqGridProduct();
    }

    @Bean(name = "invoiceManager")
    public InvoiceManager getInvoiceManager() {
        return new InvoiceManager();
    }

    @Bean(name = "invoiceGrid")
    public JqGridInvoice getInvoiceGrid() {
        return new JqGridInvoice();
    }

    @Bean(name = "invoiceLineGrid")
    public JqGridInvoiceLine getInvoiceLineGrid() {
        return new JqGridInvoiceLine();
    }

}
