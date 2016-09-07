package com.pansoft.app.smartmobile.view;

import java.io.Serializable;

/**
 * Created by eunji on 2016/8/19.
 */
public class ServiceItembean implements Serializable {
    public static final String SERVICT_TYPE_INTERNAL = "0";
    public static final String SERVICE_TYPE_EXTERNAL = "1";
    private String serviceImage;
    private String serviceId;
    private String serviceName;
    private String serviceMsg;
    private String serviceUrl;
    private String serviceType = "1";

    public ServiceItembean(String serviceImage, String serviceId, String serviceName, String serviceMsg, String serviceUrl) {
        this(serviceImage, serviceId, serviceName, serviceMsg, serviceUrl, "1");
    }

    public ServiceItembean(String serviceImage, String serviceId, String serviceName, String serviceMsg, String serviceUrl, String type) {
        this.serviceImage = serviceImage;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceMsg = serviceMsg;
        this.serviceUrl = serviceUrl;
        this.serviceType = type;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceMsg() {
        return serviceMsg;
    }

    public void setServiceMsg(String serviceMsg) {
        this.serviceMsg = serviceMsg;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String url) {
        this.serviceUrl = url;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String id) {
        this.serviceId = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String type) {
        this.serviceType = type;
    }
}
