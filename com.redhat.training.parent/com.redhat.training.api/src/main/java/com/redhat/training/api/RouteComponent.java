package com.redhat.training.api;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;

public interface RouteComponent {
	public RouteBuilder getRouteBuilder();
	public List<String> getRouteIds();
}
