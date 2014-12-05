package com.redhat.training.dtopic.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.redhat.training.api.RouteComponent;
import com.redhat.training.api.RouteComponentFactoryManager;

@Component(name = CommandListenerComponent.COMPONENT_NAME, configurationPolicy = ConfigurationPolicy.require)
public class CommandListenerComponent implements RouteComponent {

	public static final String COMPONENT_NAME = "CommandListenerComponent";

	private static final Logger LOG = LoggerFactory
			.getLogger(CommandListenerComponent.class);
	
	private RouteComponentFactoryManager factoryManager;
	
	private Map<String, ?> properties;
	
	RouteBuilder routeBuilder;
	
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
		routeBuilder =  new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("amq:queue:" + properties.get("queue.command"))
						.process(new Processor() {
							
							@Override
							public void process(Exchange exchange) throws Exception {
								
								factoryManager.addTopicListener(exchange.getIn().getBody(String.class));
							}
						});
			}
		};
		return routeBuilder;
	}

	public RouteComponentFactoryManager getFactoryManager() {
		return factoryManager;
	}

	@Reference
	public void setFactoryManager(RouteComponentFactoryManager factoryManager) {
		this.factoryManager = factoryManager;
	}

    @Override
	public List<String> getRouteIds() {
		List<String> routeIds = new ArrayList<>();
		for(RouteDefinition routeDefinition : routeBuilder.getRouteCollection().getRoutes()) {
			routeIds.add( routeDefinition.getId() );
		}
		return routeIds;
	}

}
