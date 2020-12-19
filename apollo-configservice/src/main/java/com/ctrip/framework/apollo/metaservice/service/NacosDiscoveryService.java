package com.ctrip.framework.apollo.metaservice.service;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ctrip.framework.apollo.core.dto.ServiceDTO;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author : kl
 * Service discovery nacos implementation
 **/
@Service
@Profile({"nacosDiscovery"})
public class NacosDiscoveryService implements DiscoveryService {

    private static final String SERVICE_NAME_METADATA_KEY = "apollo.serviceName";

    @NacosInjected
    private NamingService namingService;

    @Override
    public List<ServiceDTO> getServiceInstances(String serviceId) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceId);
            List<ServiceDTO> serviceDTOList = Lists.newLinkedList();
            instances.forEach(instance -> {
                ServiceDTO serviceDTO = this.toServiceDTO(instance);
                serviceDTOList.add(serviceDTO);
            });
            return serviceDTOList;
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private ServiceDTO toServiceDTO(Instance instance) {
        ServiceDTO service = new ServiceDTO();
        String appName = instance.getMetadata().get(SERVICE_NAME_METADATA_KEY);
        service.setAppName(appName);
        service.setInstanceId(instance.getInstanceId());
        String homePageUrl = "http://" + instance.getIp() + ":" + instance.getPort() + "/";
        service.setHomepageUrl(homePageUrl);
        return service;
    }
}
