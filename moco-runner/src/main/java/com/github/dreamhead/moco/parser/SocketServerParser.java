package com.github.dreamhead.moco.parser;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.SocketServer;
import com.github.dreamhead.moco.internal.ActualSocketServer;
import com.github.dreamhead.moco.parser.model.SessionSetting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketServerParser extends BaseParser<SocketServer> {
    protected static Logger logger = LoggerFactory.getLogger(SocketServerParser.class);

    @Override
    protected SocketServer createServer(final ImmutableList<SessionSetting> sessionSettings, final Optional<Integer> port, final MocoConfig... configs) {
        SocketServer server = ActualSocketServer.createLogServer(port);
        for (SessionSetting session : sessionSettings) {
            logger.debug("Parse session: {}", session);

            session.bindTo(server);
        }

        return server;
    }
}
