package modbus.rest.filter;

import javax.annotation.Priority;
import javax.ws.rs.container.PreMatching;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@PreMatching
@Priority(Integer.MIN_VALUE)
public final class Log4jFilter extends AbstractJerseyLoggingFilter {

	private final Logger logger;

	public Log4jFilter() {
		this(LogManager.getLogger(Log4jFilter.class));
	}

	public Log4jFilter(final Logger logger) {
		this(logger, false);
	}

	public Log4jFilter(final Logger logger, boolean printEntity) {
		super(printEntity);
		this.logger = logger;
	}

	public Log4jFilter(final Logger logger, int maxEntitySize) {
		super(maxEntitySize);
		this.logger = logger;
	}

	@Override
	protected void log(StringBuilder b) {
		if (logger != null) {
			logger.info(b.toString());
		}
	}
}