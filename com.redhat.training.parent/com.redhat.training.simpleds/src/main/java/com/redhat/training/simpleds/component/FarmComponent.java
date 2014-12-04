package com.redhat.training.simpleds.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Modified;
import aQute.bnd.annotation.component.Reference;

import com.redhat.training.api.Animal;

@Component(name = FarmComponent.COMPONENT_NAME)
public class FarmComponent {
	public static final String COMPONENT_NAME = "FarmComponent";

	private static final Logger LOG = LoggerFactory
			.getLogger(FarmComponent.class);
	
	private Animal animal;
	
	private Thread run;

	@Activate
	public void activate() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Activating component");
		}
		run = new Thread( new Runner() );
		run.start();
	}

	@Deactivate
	public void deactivate() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deactivating component");
		}
		run.interrupt();
	}

	@Modified
	public void modified() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Modified component");
		}
		activate();
	}

	public Animal getAnimal() {
		return animal;
	}

	@Reference
	public void setAnimal(Animal animal) {
		this.animal = animal;
	}

	public class Runner implements Runnable {
		private boolean run = true;
		public void run() {
			while(run) {
				try {
					animal.makeNoise();
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage(), e);
					run = false;
				}
			}
		}
		
	}
}
