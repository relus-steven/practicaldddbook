/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.practicalddd.cargotracker.bookingms;

import java.io.IOException;
import java.util.logging.LogManager;

import com.practicalddd.cargotracker.bookingms.infrastructure.brokers.rabbitmq.BookingEventBinder;
import com.practicalddd.cargotracker.bookingms.infrastructure.brokers.rabbitmq.RoutingEventBinder;
import io.helidon.microprofile.server.Server;
import io.helidon.webserver.Routing;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Main method simulating trigger of main method of the server.
 */
public final class Main {

    /**
     * Cannot be instantiated.
     */
    private Main() { }

    /**
     * Application main entry point.
     * @param args command line arguments
     * @throws IOException if there are problems reading logging properties
     */
    public static void main(final String[] args) throws IOException {
        setupLogging();

        Server server = startServer();

        BeanManager beanManager = server.cdiContainer().getBeanManager();
        Bean<BookingEventBinder> bean =
                (Bean<BookingEventBinder>) beanManager.resolve
                        (beanManager.getBeans(BookingEventBinder.class));
        BookingEventBinder bookingEventBinder =
                beanManager.getContext(bean.getScope()).
                        get(bean, beanManager.createCreationalContext(bean));

        Bean<RoutingEventBinder> routingBean =
                (Bean<RoutingEventBinder>) beanManager.resolve
                        (beanManager.getBeans(RoutingEventBinder.class));
        RoutingEventBinder routingEventBinder =
                beanManager.getContext(routingBean.getScope()).
                        get(routingBean, beanManager.createCreationalContext(routingBean));

    }

    /**
     * Start the server.
     * @return the created {@link Server} instance
     */
    static Server startServer() {
        // Server will automatically pick up configuration from
        // microprofile-config.properties
        // and Application classes annotated as @ApplicationScoped
        return Server.create().start();
    }

    /**
     * Configure logging from logging.properties file.
     */
    private static void setupLogging() throws IOException {
        // load logging configuration
        LogManager.getLogManager().readConfiguration(
                Main.class.getResourceAsStream("/logging.properties"));
    }
}
