package com.redhat.training.api;

import org.apache.camel.builder.RouteBuilder;

public interface RouteComponent {
	public RouteBuilder getRouteBuilder();
}
