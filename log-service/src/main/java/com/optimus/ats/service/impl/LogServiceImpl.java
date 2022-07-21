package com.optimus.ats.service.impl;

import com.optimus.ats.model.LogManager;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class LogServiceImpl {

	static final Logger log = LoggerFactory.getLogger(LogServiceImpl.class);

	@Inject
	Vertx vertx;

	@Inject
	EventBus bus;

	public void init(@Observes StartupEvent ev) {
		log.info("init with startup event");
		vertx.eventBus().consumer("log")
				.handler(h -> {
					LogManager logManager = JsonObject.mapFrom(h.body()).mapTo(LogManager.class);
					log.info("sr: {}", logManager.getServiceName());
					LogManager log = new LogManager();
					log.setServiceName(logManager.getServiceName());
					log.setDetails(logManager.getDetails());
					bus.publish("centrallog",log);
				});
	}

	@ConsumeEvent(value = "centrallog", blocking = true)
	@Transactional
	public LogManager setCentralLog(LogManager dto) {
		log.info("Central log Service Name-->" + dto.getServiceName());
		LogManager.persist(dto);
		return dto;
	}
}
