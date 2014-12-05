package com.redhat.training.dtopic.camel.context;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.camel.CamelContext;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.util.jndi.JndiContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.redhat.training.api.RouteComponent;

@Component(immediate = true, name = CamelContextComponent.COMPONENT_NAME)
public class CamelContextComponent {

	public static final String COMPONENT_NAME = "CamelContextComponent";

	private static final Logger LOG = LoggerFactory
			.getLogger(CamelContextComponent.class);

	CamelContext context;
	private ReadWriteLock contextLock = new ReentrantReadWriteLock();

	JndiContext jndiContext;
	private ReadWriteLock jndiContextLock = new ReentrantReadWriteLock();

	public CamelContextComponent() {
		try {
			jndiContext = new JndiContext();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		JndiRegistry jndiRegistry = new JndiRegistry(jndiContext);
		BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
				.getBundleContext();
		context = new OsgiDefaultCamelContext(bundleContext, jndiRegistry);
	}

	@Activate
	public void activate() throws Exception {

		// try {
		// contextLock.writeLock().lock();
		// }
		// finally {
		// contextLock.writeLock().unlock();
		// }

		try {
			contextLock.writeLock().lock();
			context.start();
		} finally {
			contextLock.writeLock().unlock();
		}
	}

	@Deactivate
	public void deactivate() throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deactivating component");
		}
		try {
			contextLock.writeLock().lock();
			context.stop();
		} finally {
			contextLock.writeLock().unlock();
		}
	}

	@Reference(multiple = true, unbind = "unbindRouteComponent", dynamic = true, optional = true)
	public void setCommandRouteBuilderComponent(
			RouteComponent routeComponentFactory) {
		LOG.info("adding route");
		try {
			contextLock.writeLock().lock();
			context.addRoutes(routeComponentFactory.getRouteBuilder());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			contextLock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unused")
	private void unbindRouteComponent(RouteComponent routeComponent) {
		LOG.info("Attempting unbind of: "
				+ routeComponent.getClass().getSimpleName());
		List<String> routeIds = routeComponent.getRouteIds();
		if (routeIds != null) {
			for (String routeId : routeIds) {
				LOG.info("Unbinding routebuidler component route: " + routeId);
				try {
					contextLock.writeLock().lock();
					context.removeRoute(routeId);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				} finally {
					contextLock.writeLock().unlock();
				}

			}
		}
	}
}
