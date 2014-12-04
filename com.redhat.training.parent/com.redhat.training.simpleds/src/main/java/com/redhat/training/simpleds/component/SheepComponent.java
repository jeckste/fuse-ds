package com.redhat.training.simpleds.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.training.api.Animal;

import aQute.bnd.annotation.component.Component;

@Component(name = SheepComponent.COMPONENT_NAME)
public class SheepComponent implements Animal {
	
	public static final String COMPONENT_NAME = "SheepComponent";
	
	private static final Logger LOG = LoggerFactory
			.getLogger(SheepComponent.class);
					
	public void makeNoise() {
		LOG.info("Baaahh Baaahh");
	}

}
