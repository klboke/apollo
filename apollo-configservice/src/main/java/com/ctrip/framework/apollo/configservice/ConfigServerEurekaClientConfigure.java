package com.ctrip.framework.apollo.configservice;

import com.ctrip.framework.apollo.biz.eureka.ApolloEurekaClientConfig;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author : kl
 * After startup, set FetchRegistry to true, refresh eureka client
 **/
@ConditionalOnProperty(value = "eureka.client.enabled",havingValue = "true",  matchIfMissing = true)
@Configuration
public class ConfigServerEurekaClientConfigure implements ApplicationRunner {

    private static final String EUREKACLIENT_BEANNAME = "eurekaClient";
    private final ApolloEurekaClientConfig eurekaClientConfig;
    private final RefreshScope refreshScope;

    public ConfigServerEurekaClientConfigure(ApolloEurekaClientConfig eurekaClientConfig, RefreshScope refreshScope) {
        this.eurekaClientConfig = eurekaClientConfig;
        this.refreshScope = refreshScope;
    }

    @Override
    public void run(ApplicationArguments args) {
        this.refreshEurekaClient();
    }

    private void refreshEurekaClient(){
        eurekaClientConfig.setFetchRegistry(true);
        refreshScope.refresh(EUREKACLIENT_BEANNAME);
    }
}
