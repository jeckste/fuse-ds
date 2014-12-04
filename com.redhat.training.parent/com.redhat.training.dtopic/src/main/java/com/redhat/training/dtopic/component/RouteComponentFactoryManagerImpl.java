package com.redhat.training.dtopic.component;

import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.redhat.training.api.RouteComponent;
import com.redhat.training.api.RouteComponentFactoryManager;

@Component(name = RouteComponentFactoryManagerImpl.COMPONENT_NAME)
public class RouteComponentFactoryManagerImpl implements RouteComponentFactoryManager {

	public static final String COMPONENT_NAME = "RouteComponentFactoryManager";
	
	private static final Logger LOG = LoggerFactory.getLogger(RouteComponentFactoryManagerImpl.class);

	private ComponentFactory factory;
	private ComponentInstance instance;
	private RouteComponent routeService;
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	
	@Activate
    public void activate() {
        LOG.info("Activating the " + COMPONENT_NAME);
//        
    }

    /**
     * Called when any of the SCR Components required dependencies become
     * unsatisfied.
     */
    @Deactivate
    public void deactivate() {
        LOG.info("Deactivating the " + COMPONENT_NAME);
        try {
            lock.readLock().lock();;
            instance.dispose();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Reference(target = "(component.factory=route.factory.provider)")
    public void setFactory(final ComponentFactory factory) {
        try {
            lock.writeLock().lock();
            this.factory = factory;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unsetFactory(final ComponentFactory factory) {
        try {
            lock.writeLock().lock();
            this.factory = null;
        } finally {
            lock.writeLock().unlock();
        }
    }

	@Override
	public void addTopicListener(String topic) {
		try {
          lock.readLock().lock();
          if (factory != null) {
              final Properties props = new Properties();
              props.setProperty("topicName", topic);
              instance = factory.newInstance(props);
              routeService = (RouteComponent)instance.getInstance();
          }
      } finally {
          lock.readLock().unlock();
      }
	}
}
