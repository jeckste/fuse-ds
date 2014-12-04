package com.redhat.training.dtopic.component;

import java.util.Map;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;

import com.redhat.training.api.RouteComponent;

@Component(name = RouteComponentFactoryImpl.COMPONENT_NAME, factory = "route.factory.provider")
public class RouteComponentFactoryImpl implements RouteComponent {

	public static final String COMPONENT_NAME = "RouteComponentFactory";
	
	private static final Logger LOG = LoggerFactory.getLogger(RouteComponentFactoryImpl.class);
	
	private Map<String, ?> properties;
	
	@Activate
    public void activate(final Map<String, ?> properties) {
        LOG.info("Activating the " + COMPONENT_NAME);
        this.properties = properties;
    }

    @Deactivate
    public void deactivate() {
        LOG.info("Deactivating the " + COMPONENT_NAME);
    }

	@Override
	public RouteBuilder getRouteBuilder() {
		return new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				from("amq:topic:"+properties.get("topicName")).log(LoggingLevel.INFO,"${body}");
			}
		};
	}

}
