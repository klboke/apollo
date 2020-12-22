package com.ctrip.framework.apollo.configservice;

import com.ctrip.framework.apollo.biz.eureka.ApolloEurekaClientConfig;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : kl
 * After startup, set FetchRegistry to true, refresh eureka client
 **/
@Configuration
@ConditionalOnProperty(value = "eureka.client.enabled", havingValue = "true", matchIfMissing = true)
public class ConfigServerEurekaClientConfigure {

    private static final String EUREKACLIENT_BEANNAME = "eurekaClient";
    private final ApolloEurekaClientConfig eurekaClientConfig;
    private final AtomicBoolean isRefresh = new AtomicBoolean(false);
    private final RefreshScope refreshScope;

    public ConfigServerEurekaClientConfigure(ApolloEurekaClientConfig eurekaClientConfig, RefreshScope refreshScope) {
        this.eurekaClientConfig = eurekaClientConfig;
        this.refreshScope = refreshScope;
    }

    @EventListener
    public void listenEurekaInstanceRegisteredEvent(EurekaInstanceRegisteredEvent event) {
        InstanceInfo.InstanceStatus status = event.getInstanceInfo().getStatus();
        if (InstanceInfo.InstanceStatus.UP.equals(status) && !eurekaClientConfig.isFetchRegistry()) {
            this.refreshEurekaClient();
        }
    }

    private void refreshEurekaClient() {
        if (isRefresh.compareAndSet(false, true)) {
            eurekaClientConfig.setFetchRegistry(true);
            refreshScope.refresh(EUREKACLIENT_BEANNAME);
        }
    }
}
